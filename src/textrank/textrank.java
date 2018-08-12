package textrank;

import textpreprocess.function;
import textpreprocess.media;
import textpreprocess.news;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class textrank {
    static final float d = 0.84f;           // 阻尼系数
    static final int max_iter = 200;        // 迭代次数
    static final float min_diff = 0.0001f;  // 判断是否继续递归
    private static  int nKeyword = 5;         // 关键词个数
    private static  int nTotalKeyword = 5000;         // 总关键词个数
    private static  int coOccuranceWindow = 5; // 共现窗口
    public static boolean finalnum = false;
    public static String Path = "";
    public static List<Float> valKeywordList=new ArrayList<Float>();

    public static void setKeywordNumber(int sysKeywordNum) {
        nKeyword = sysKeywordNum;
    }
    public static void setWindowSize(int window) {
        coOccuranceWindow = window;
    }
    public static void main(String[] args){
        String res = "";
        int i = 0;
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(Path + "pre.txt"), "UTF-8")) {//打开data
            BufferedReader br = new BufferedReader(isr);
            try {
                String line = "";
                while((line = br.readLine()) != null) {
                    String st = br.readLine();
                    String sc = br.readLine();
                    List<String> l = textrank.getKeyword(st, sc);//独立计算句子中的关键词
                    for(String s : l)res += (s + " ");
                    if(i++ % 1000 == 0) System.out.println(i);
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
        nKeyword = nTotalKeyword;
        finalnum = true;
        List<String> l = textrank.getKeyword("", res);
//        Collections.sort(l, new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                return o1.compareTo(o2);
//            }
//        });
        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(Path + "textrank.txt"), "UTF-8")) {//输出词频预处理文件
            //for(int j = 0;j < nTotalKeyword;j++) fw.write(l.get(j).toString() + "\t" + valKeywordList.get(j).toString() + "\n");
            for(int j = 0;j < 1500;j++) fw.write(l.get(j).toString() + "\n");
            fw.close();
            System.out.println("upload is done -> up.txt");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<String> getKeyword(String title, String content) {//计算每个句子中的关键词

        Map<String, Float> score = textrank.getWordScore(title, content);//获取每个词的分数
        List<Map.Entry<String, Float>> entryList = new ArrayList<Map.Entry<String, Float>>(score.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<String, Float>>() {//根据分数排序
                    @Override
                    public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                        return (o2.getValue().compareTo(o1.getValue()));
                    }
                }
        );
        //if(!finalnum) nKeyword = Math.min(10, nKeyword + entryList.size());
        if(!finalnum) nKeyword = Math.min(10, nKeyword + entryList.size() / 50);//修改关键词窗口
        List<String> sysKeywordList=new ArrayList<String>();
        for (int i = 0; i < nKeyword; ++i){//去除符号
            try{
                Pattern p = Pattern.compile(".*\\d+.*");
                Matcher m = p.matcher(entryList.get(i).getKey());
                if(m.matches()) continue;
                Pattern pat = Pattern.compile("/+[a-zA-Z]+");
                Matcher mat = pat.matcher(entryList.get(i).getKey());
                Pattern p1 = Pattern.compile("[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t");
                Matcher m1 = p.matcher(mat.replaceAll(""));
                if (function.hznum(mat.replaceAll("")) > 1){
                    sysKeywordList.add(mat.replaceAll(""));
                    if(finalnum) valKeywordList.add(entryList.get(i).getValue());
                }
            }catch(IndexOutOfBoundsException e){
                continue;
            }
        }
        return sysKeywordList;
    }
    public static Map<String,Float> getWordScore(String title, String content) {//计算每个词的分数
        ArrayList<String> sl = new ArrayList<String>(){};
        String[] st = title.split(" ");
        String[] sc = content.split(" ");
        for(int i = 0;i < 3;i++) for(String s : st) sl.add(s);//增加权重
        for(String s : sc) sl.add(s);

        int count=1;
        Map<String,Integer> wordPosition = new HashMap<String,Integer>();

        List<String> wordList=new ArrayList<String>();

        for (String s : sl) {//计数
            wordList.add(s);
            if(!wordPosition.containsKey(s)) {
                wordPosition.put(s,count);
                count++;
            }
        }
        Map<String, Set<String>> words = new HashMap<String, Set<String>>();
        Queue<String> que = new LinkedList<String>();
        for (String w : wordList) {//计算窗口
            if (!words.containsKey(w)) {
                words.put(w, new HashSet<String>());
            }
            que.offer(w);
            if (que.size() > coOccuranceWindow) {
                que.poll();
            }
            for (String w1 : que){
                for (String w2 : que) {
                    if (w1.equals(w2)) {
                        continue;
                    }
                    words.get(w1).add(w2);
                    words.get(w2).add(w1);
                }
            }
        }
        Map<String, Float> score = new HashMap<String, Float>();
        for (int i = 0; i < max_iter; ++i) {//计算分数
            Map<String, Float> m = new HashMap<String, Float>();
            float max_diff = 0;
            for (Map.Entry<String, Set<String>> entry : words.entrySet()) {
                String key = entry.getKey();
                Set<String> value = entry.getValue();
                m.put(key, 1 - d);
                for (String other : value) {
                    int size = words.get(other).size();
                    if (key.equals(other) || size == 0) continue;
                    m.put(key, m.get(key) + d / size * (score.get(other) == null ? 0 : score.get(other)));
                }
                max_diff = Math.max(max_diff, Math.abs(m.get(key) - (score.get(key) == null ? 1 : score.get(key))));
            }
            score = m;
            if (max_diff <= min_diff) break;
        }
        return score;
    }
}
