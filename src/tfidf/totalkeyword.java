package tfidf;

import textpreprocess.function;
import textpreprocess.word;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class totalkeyword {
    public static String Path = "";
    public static Long znum = (long) 20207;
    public static Long zwnum = (long) 0;
    public static int times = 15;
    public static void main(String[] args){
        ArrayList<word> wl = new ArrayList<word>(){};
        ArrayList<String> sl = new ArrayList<String>(){};
        ArrayList<String> dl = new ArrayList<String>(){};

        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(Path + args[0]), "UTF-8")) {//读取总字频
            BufferedReader br = new BufferedReader(isr);
            try {
                String line = "";
                while((line = br.readLine()) != null) {
                    String[] str = line.split("\t");
                    wl.add(new word(str[1],Long.valueOf(str[2]),Long.valueOf(str[3]), Long.valueOf(str[4])));
                    wl.get(wl.size() - 1).newsl = function.compositestr(str[5]);
                    wl.get(wl.size() - 1).dayl = function.compositestr(str[6]);
                    for(int i = 0;i < wl.get(wl.size() - 1).dayl.size();i++){
                        for(int j = 0;;j++){
                            if(j ==dl.size()){
                                dl.add(wl.get(wl.size() - 1).dayl.get(i));
                                break;
                            }
                            if(dl.get(j).equals(wl.get(wl.size() - 1).dayl.get(i))){
                                break;
                            }
                        }
                    }
                    zwnum += (Long.valueOf(str[2]) * times + Long.valueOf(str[3]));
                    //znum += Long.valueOf(str[4]);
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
        System.out.println(dl.size());

        for(word w : wl) {//计算词频、逆词频
            w.tf = (w.ttimes.doubleValue() * times + w.ctimes.doubleValue()) / zwnum.doubleValue();
            w.idf = Math.log(znum.doubleValue() / (w.totaldocu.doubleValue()));
            //System.out.println(w.ttimes+"\t"+w.ctimes+"\t"+zwnum+"\t"+w.totaldocu+"\t"+w.tf+"\t"+w.idf);
            //System.out.println(w.dayl.size()+"\t"+dl.size()+"\t"+((double) w.dayl.size() / dl.size()));
        }

        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(Path + "dictionary.txt"), "UTF-8")) {//读取过滤词
            BufferedReader br = new BufferedReader(isr);
            try {
                String line = "";
                while((line = br.readLine()) != null) {
                    sl.add(line);
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

        Collections.sort(wl, new Comparator<word>() {//根据tf-idf排序
            @Override
            public int compare(word o1, word o2) {
                Double d1 = o1.tf * o1.idf;
                Double d2 = o2.tf * o2.idf;
                //Double d1 = o1.tf * o1.idf / ((double) o1.dayl.size() / dl.size());
                //Double d2 = o2.tf * o2.idf / ((double) o2.dayl.size() / dl.size());
                return -1 * d1.compareTo(d2);
            }
        });

//        for(int i = 1;;i++) {//去除无用词
//            //System.out.println(wl.get(i).word+" "+wl.get(i).dayl.get(0)+" "+wl.get(i).dayl.get(wl.get(i).dayl.size() - 1)+" "+function.interival(wl.get(i).dayl));
//            if(i >= wl.size()) break;
//            for(String s : sl) {
//                if(s.contains(wl.get(i).word) || wl.get(i).word.contains(s) || wl.get(i).word.equals(s)) {
//                    wl.remove(i);
//                    i--;
//                    break;
//                }
//            }
//        }

        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(Path + "keywords.txt"), "UTF-8")) {//输出词频预处理文件
            for(int i = 0;i < wl.size();i++) fw.write(String.valueOf(i) + "\t" + wl.get(i).word + "\t" + wl.get(i).ttimes + "\t" + wl.get(i).ctimes + "\t" + wl.get(i).totaldocu + "\t" + wl.get(i).dayl.size() + "\t" + function.interival(wl.get(i).dayl) + "\t" + wl.get(i).idf + "\t" + wl.get(i).tf + "\t" + wl.get(i).idf * wl.get(i).tf + "\n");
            fw.close();
            System.out.println("keyword select is done -> keywords.txt");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(wl.size());
        ArrayList<word> rwl = new ArrayList<word>(){};
        for(int i = 0;i < 135411;i++){
            rwl.add(wl.get(i));
        }

        Collections.sort(rwl, new Comparator<word>() {//根据tf-idf排序
            @Override
            public int compare(word o1, word o2) {
                //Double d1 = o1.tf * o1.idf;
                //Double d2 = o2.tf * o2.idf;
                Double d1 = o1.tf * o1.idf / function.active((double) o1.dayl.size() / dl.size());
                Double d2 = o2.tf * o2.idf / function.active((double) o2.dayl.size() / dl.size());
                return -1 * d1.compareTo(d2);
            }
        });

        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(Path + "tfidf.txt"), "UTF-8")) {//输出词频预处理文件
            //for(int i = 0;i < 5000;i++) fw.write(rwl.get(i).word + "\t" + rwl.get(i).tf * rwl.get(i).idf +"\n");
            for(int i = 0;i < 1500;i++) fw.write(rwl.get(i).word + "\n");
            fw.close();
            System.out.println("upload is done -> up.txt");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        for(int i = 0;;i++) {
//            if(i >= wl.size()) break;
//            if(wl.get(i).ttimes + wl.get(i).ctimes < 3) {
//                wl.remove(i);
//                i--;
//            }
//        }
//
//        ArrayList<node> nl = new ArrayList<node>(){};
//
//        for(word w : wl){
//            nl.add(new node(w.word, w.tf, w.idf));
//        }
    }
}
