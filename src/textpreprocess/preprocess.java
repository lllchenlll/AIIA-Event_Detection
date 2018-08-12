package textpreprocess;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class preprocess {
    public static String Path = "";
    public static Integer interval = 1;
    public static Integer processnum = 8;
    public static void main(String[] args){

        ArrayList<news> nl = new ArrayList<news>(){};//新闻列表
        ArrayList<media> ml = new ArrayList<media>(){};//媒体列表
        ArrayList<word> wl = new ArrayList<word>();//总词列表

        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(Path + args[0]), "UTF-8")) {//打开data
            BufferedReader br = new BufferedReader(isr);
            try {
                String line = "";
                line = br.readLine();//略掉首行
                while((line = br.readLine()) != null) {
                    String[] str = line.split(",",4);//切分
                    if(str[0].contains("/"))  nl.add(new news(function.dateformat(str[0]),str[1],str[2],str[3]));//整理日期格式
                    else nl.add(new news(str[0],str[1],str[2],str[3]));
                    for(int i = 0;;i++) {//添加媒体
                        if(i == ml.size()) {
                            ml.add(new media(str[1]));
                            break;
                        }
                        if(ml.get(i).name.equals(str[1]))
                        {
                            ml.get(i).times++;
                            break;
                        }
                    }
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            isr.close();//关闭文件
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(nl, Comparator.comparing(o -> o.time));//按日期升序排列
        ml.sort((o1, o2) -> o2.times.compareTo(o1.times));//按次数降序排列

//        for(int i = 0;;i++) {//去重
//            //f(nl.get(i).time.equals("2014-09-10")) System.out.println(nl.get(i).display());
//            if(i >= nl.size() - 1) break;
//            if(nl.get(i).time.equals(nl.get(i + 1).time) && nl.get(i).source.equals(nl.get(i + 1).source) && function.textdis(nl.get(i).title, nl.get(i + 1).title) > 0.8){
//                System.out.println(nl.get(i).time);
//                nl.remove(i + 1);
//                i--;
//            }
//        }

        int id = 0;
        for(news n : nl) {//分词（去停用词）
            n.id = String.valueOf(id);
            n.titleseg = WordSegmenter.seg(n.title, SegmentationAlgorithm.MaxNgramScore);
            n.contentseg = WordSegmenter.seg(n.content, SegmentationAlgorithm.MaxNgramScore);
            id++;
        }
        System.out.println("news(retain): " + nl.size() + "\tmedia: " + ml.size());//输出读取结果

        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(Path + "pre.txt"), "UTF-8")) {//输出新闻预处理文件
            for(int i = 0;i < nl.size();i++){
                fw.write(nl.get(i).id + "\t" + nl.get(i).time + "\t" + nl.get(i).source + "\t" + nl.get(i).title.replace("\t","") + "\t" + nl.get(i).content.replace("\t","") + "\n");
                fw.write(function.compositestr(nl.get(i).titleseg)+"\n");
                fw.write(function.compositestr(nl.get(i).contentseg)+"\n");
            }
            fw.close();
            System.out.println("news pre-processing is done -> pre.txt");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(Path + "media.txt"), "UTF-8")) {//输出媒体预处理文件
            for(int i = 0;i < ml.size();i++) fw.write(String.valueOf(i) + "\t" + ml.get(i).name + "\t" + ml.get(i).times + "\n");
            fw.close();
            System.out.println("media pre-processing is done -> media.txt");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(news n : nl) {//统计词频
            wl = function.wordstat(wl, n.titleseg, n.id, n.time,"t");//统计标题内词汇
            wl = function.wordstat(wl, n.contentseg, n.id, n.time,"c");//统计内容内词汇
        }
        for(int i = 0;i < wl.size();i++) wl.get(i).dayl = function.getsingle(wl.get(i).dayl);
        for(word w : wl) Collections.sort(w.newsl, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        System.out.println("total word (without filter): " + wl.size());
        for(int i= 0;i < wl.size();i++) {//去掉纯数字及网页编码
            if(wl.get(i).word.contains("[0-9]+")) wl.remove(i);
            if(wl.get(i).word.contains("/")) {
                Pattern pat = Pattern.compile("/+[a-zA-Z]+");
                Matcher mat = pat.matcher(wl.get(i).word);
                wl.get(i).word = mat.replaceAll("");
            }
            Pattern p = Pattern.compile("[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t");
            Matcher m = p.matcher(wl.get(i).word);
            wl.get(i).word = m.replaceAll("");
        }
        System.out.println("total word (filter): " + wl.size());
        Collections.sort(wl, (o1, o2) -> o1.word.compareTo(o2.word));
        for(int i = 0;;i++)//合并相同词汇
        {
            if(i >= wl.size() - 1) break;
            if(wl.get(i).word.equals(wl.get(i + 1).word)){
                wl.get(i).ttimes += wl.get(i + 1).ttimes;
                wl.get(i).ctimes += wl.get(i + 1).ctimes;
                wl.get(i).totaldocu +=  wl.get(i + 1).ctimes;
                wl.get(i).newsl.addAll(wl.get(i + 1).newsl);
                wl.remove(i + 1);
                i--;
            }
        }
        System.out.println("total word: " + wl.size());

        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(Path + "totalwordstat.txt"), "UTF-8")) {//输出词频预处理文件
            for(int i = 0;i < wl.size();i++) fw.write(String.valueOf(i) + "\t" + wl.get(i).word + "\t" + wl.get(i).ttimes + "\t" + wl.get(i).ctimes + "\t" + wl.get(i).totaldocu + "\t" + function.compositestr(wl.get(i).newsl) + "\t" + function.compositestr(wl.get(i).dayl) + "\n");
            fw.close();
            System.out.println("word frequency statistic is done -> totalwordstat.txt");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        String filename = "";
//        for(int i = 0;i < nl.size();i++){//输出每天的数据
//            if(!nl.get(i).time.equals(filename)){
//                filename = nl.get(i).time;
//                try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(Path+ filename + ".txt"), "UTF-8")) {//输出切分文件
//                    for(;i < nl.size();i++) {
//                        if(!nl.get(i).time.equals(filename)){
//                            i--;
//                            break;
//                        }
//                        fw.write(nl.get(i).id + "\t" + nl.get(i).time + "\t" + nl.get(i).source + "\t" + nl.get(i).title.replace("\t","") + "\t" + nl.get(i).content.replace("\t","") + "\n");
//                        fw.write(function.compositestr(nl.get(i).titleseg)+"\n");
//                        fw.write(function.compositestr(nl.get(i).contentseg)+"\n");
//                    }
//                    fw.close();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        System.out.println("each day file is done -> XXXX-XX-XX.txt");

    }
}
