package graph;

import textpreprocess.function;
import textpreprocess.news;
import textpreprocess.node;
import textpreprocess.word;
import textrank.textrank;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class eventdetection {

    public static String Path = "";
    public static ArrayList<news> nl = new ArrayList<news>(){};
    public static ArrayList<word> wl = new ArrayList<word>(){};
    public static ArrayList<node> ndl = new ArrayList<node>(){};
    public static ArrayList<String> sl = new ArrayList<String>(){};
    public static Integer dis = 100;
    public static Integer totalword = 0;
    public static Integer titletimes = 15;


    public static void main(String[] args){

        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(Path + "backupqingxi/pre.txt"), "UTF-8")) {//打开data
            BufferedReader br = new BufferedReader(isr);
            try {
                String line = "";
                int k = 0;
                while((line = br.readLine()) != null) {
                    k++;
                    if(k > 30 && k < dis + 30){
                        String[] detail = line.split("\t");
                        news n = new news(detail[0],detail[1],detail[2],detail[3],detail[4]);
                        n.titleseg = function.compositestr(br.readLine()," ");
                        totalword += (n.titleseg.size() * 15);
                        n.contentseg = function.compositestr(br.readLine()," ");
                        totalword += n.contentseg.size();
                        nl.add(n);
                    }
                    if(k<=30) {br.readLine();br.readLine();}
                    if(k>=dis + 30) break;
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            System.out.println("news: "+nl.size());
            isr.close();//关闭文件
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(news n : nl){//词频排序
            wl = function.wordstat(wl, n.titleseg, n.id, n.time, "t");
            wl = function.wordstat(wl, n.contentseg, n.id, n.time,"c");
        }
        System.out.println("sort word frequency, word: "+wl.size());

        for(int i = 0;i < wl.size();i++) wl.get(i).newsl = function.getsingle(wl.get(i).newsl);//去重新闻id
        System.out.println("remove extra news");

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
        System.out.println("remove extra symbol");

        for(int i = 0;;i++){//合并相同词汇
            if(i >= wl.size() - 1) break;
            for (int j = i + 1;;j++){
                if(j >= wl.size() - 1) break;
                if(wl.get(i).word.contains(wl.get(j).word) || wl.get(j).word.contains(wl.get(i).word)){
                    wl.get(i).ttimes += wl.get(j).ttimes;
                    wl.get(i).ctimes += wl.get(j).ctimes;
                    wl.get(i).newsl.addAll(wl.get(j).newsl);
                    wl.get(i).newsl = function.getsingle(wl.get(i).newsl);
                    wl.get(i).totaldocu = (long) wl.get(i).newsl.size();
                    wl.remove(j);
                }
            }
        }
        System.out.println("merge repeated word, word: "+wl.size());

        for(word w : wl) {//计算词频、逆词频
            w.tf = (w.ttimes.doubleValue() * titletimes + w.ctimes.doubleValue()) / totalword.doubleValue();
            w.idf = Math.log(dis.doubleValue() / (w.totaldocu.doubleValue()));
        }
        Collections.sort(wl, new Comparator<word>() {
            @Override
            public int compare(word o1, word o2) {
                Double d1 = o1.idf * o1.tf;
                Double d2 = o2.idf * o2.tf;
                return d2.compareTo(d1);
            }
        });

        int fn = wl.size() / 2;
        for(int i = 0;i < wl.size();i++){//过滤低频词
            if(wl.get(i).ttimes < dis / 20 && wl.get(i).ctimes < dis / 10) {
                wl.remove(i);
                i--;
                continue;
            }
            if(i > fn) {
                wl.remove(i);
                i--;
                continue;
            }
        }
        System.out.println("filter low frequent word, word: "+wl.size());

//        for(word w:wl){
//            System.out.println(w.word+"\t"+w.idf+"\t"+w.tf);
//        }

        for(int i = 0;i < wl.size();i++) wl.get(i).id = i;//给每个词添加唯一id

        ArrayList<wordgroup> wgl = new ArrayList<wordgroup>(){};//存储共现词对
        for(int i = 0;i < wl.size();i++){
            for(int j = i + 1;j < wl.size();j++){
                ArrayList<String> ml = (ArrayList<String>) wl.get(i).newsl.clone();
                ml.retainAll(wl.get(j).newsl);
                if(ml.size() > 0){
                    wordgroup wg = new wordgroup();
                    wg.wl.add(i);
                    wg.wl.add(j);
                    wg.nl.addAll(ml);
                    if(wg.mintimes == 0 || wg.mintimes > ml.size()) wg.mintimes = ml.size();
                    if(wg.maxtimes == 0 || wg.maxtimes < ml.size()) wg.maxtimes = ml.size();
                    wg.avgtimes = (double) wg.maxtimes;
                    wgl.add(wg);
                }
            }
        }

        Collections.sort(wgl, new Comparator<wordgroup>() {//根据共现文档数排序
            @Override
            public int compare(wordgroup o1, wordgroup o2) {
                return o2.mintimes.compareTo(o1.mintimes);
            }
        });
        System.out.println("word pair: "+wgl.size());

        Integer times = (int) Math.log10((double) wgl.size());//移出共现次数较低的词对
        times = Math.max(times, wgl.get(wgl.size() / 5).nl.size());
        ArrayList<wordgroup> nwgl = new ArrayList<wordgroup>(){};
        for(wordgroup wg : wgl) if(wg.nl.size() >= times) nwgl.add(wg);
        System.out.println("word pair(>"+times+"): "+nwgl.size());

        for(int i = 0;;i++){//合并共现文档完全相同的词对
            if(i >= nwgl.size()) break;
            for(int j = i + 1;;j++){
                if(j >= nwgl.size()) break;
                if(nwgl.get(i).nl.equals(nwgl.get(j).nl)){
                    nwgl.get(j).wl.removeAll(nwgl.get(i).wl);
                    nwgl.get(i).wl.addAll(nwgl.get(j).wl);
                    nwgl.remove(j);
                    i--;
                }
            }
        }
        System.out.println("word pair (initial merge): "+nwgl.size());

        ArrayList<wordgroup> temp = new ArrayList<wordgroup>(){};
        int sum = 1;
        double precent = 10 / 9;
        while(sum>0) {
            sum--;
            //precent *= 0.9;
            for (int i = nwgl.size() - 1; i > 0; i--) {
                for (int j = i - 1; j > 0; j--) {
                    double mn = (double) function.repectivestr(nwgl.get(j).nl, nwgl.get(i).nl) * precent;
                    double mw = (double) function.repectiveint(nwgl.get(j).wl, nwgl.get(i).wl) * precent;
                    if ((mn >= nwgl.get(j).nl.size() || mn >= nwgl.get(i).nl.size()) || (mw >= nwgl.get(j).wl.size() || mw >= nwgl.get(i).wl.size())) {
                        wordgroup wg = new wordgroup();
                        if (mn < (double) nwgl.get(i).nl.size() / 2 && nwgl.get(i).nl.size() >= 4) {
                            wg.nl = nwgl.get(j).nl;
                            wg.maxtimes = nwgl.get(i).nl.size();
                        } else if (mn < (double) nwgl.get(j).nl.size() / 2 && nwgl.get(j).nl.size() >= 4) {
                            wg.nl = nwgl.get(i).nl;
                            wg.maxtimes = nwgl.get(j).nl.size();
                        } else {
                            wg.nl = function.togetstr(nwgl.get(j).nl, nwgl.get(i).nl);
                        }
                        if (mw < (double) nwgl.get(i).wl.size() / 2 && nwgl.get(i).wl.size() >= 4) {
                            wg.wl = nwgl.get(j).wl;
                        } else if (mw < (double) nwgl.get(j).wl.size() / 2 && nwgl.get(j).wl.size() >= 4) {
                            wg.wl = nwgl.get(i).wl;
                        } else {
                            //System.out.println(nwgl.get(j).wl+"\t"+nwgl.get(i).wl);
                            //                    ArrayList<Integer> in = (ArrayList<Integer>) nwgl.get(j).wl.clone();
                            //                    in.retainAll(nwgl.get(i).wl);
                            wg.wl = function.togetint(nwgl.get(j).wl, nwgl.get(i).wl);
                        }
                        wg.mintimes = wg.nl.size();
                        //System.out.println(wg.wl+"\t"+wg.nl);
                        temp.add(wg);
                    }
                }
            }
            Collections.sort(temp, (o1, o2) -> ((Integer) o2.wl.size()).compareTo((Integer) o1.wl.size()));
            System.out.println("total: " + temp.size());
            for(int i=0;;i++){
                if(i >= temp.size()) break;
                if(temp.get(i).wl.size()<dis / 33 || temp.get(i).nl.size()<dis / 10){
                    temp.remove(i);
                    i--;
                }
            }
            System.out.println("total (filter low frequency): " + temp.size());
            for (int i = 0; ; i++) {
                if (i >= temp.size()) break;
//                ArrayList<Integer> ss = (ArrayList<Integer>) temp.get(i).wl.clone();
//                ArrayList<Integer> ss1 = (ArrayList<Integer>) temp.get(i).wl.clone();
                for (int j = i + 1; ; j++) {
                    if (j >= temp.size()) break;
                    if(temp.get(i).wl.equals(temp.get(j).wl)){
                        temp.remove(j);
                        j--;
                        continue;
                    }
                    int m = function.repectiveint(temp.get(i).wl, temp.get(j).wl);
                    if( (m >= (double) temp.get(i).wl.size() * 0.8 && m >= (double) temp.get(j).wl.size() * 0.8) || (temp.get(i).wl.size() - m <= 2 && temp.get(j).wl.size() - m <= 2)){
                        temp.get(i).wl = function.togetint(temp.get(i).wl, temp.get(j).wl);
                        temp.get(i).nl = function.togetstr(temp.get(i).nl, temp.get(j).nl);
                        temp.get(i).mintimes = temp.get(i).nl.size();
                        temp.remove(j);
                        j--;
                    }
                    else{
                        int n = function.repectivestr(temp.get(i).nl, temp.get(j).nl);
                        if((n >= (double) temp.get(i).nl.size() * 0.8 && n >= (double) temp.get(j).nl.size() * 0.8) || (temp.get(i).nl.size() - n <= 3 && temp.get(j).nl.size() - n <= 3)){
                            temp.get(i).wl = function.togetint(temp.get(i).wl, temp.get(j).wl);
                            temp.get(i).nl = function.togetstr(temp.get(i).nl, temp.get(j).nl);
                            temp.get(i).mintimes = temp.get(i).nl.size();
                            temp.remove(j);
                            j--;
                        }
                    }
//                    if (j >= temp.size()) break;
//                    ss.retainAll(temp.get(j).wl);
////                    if(ss.size()!=0){
////                    System.out.println("ss:"+ss1);
////                    System.out.println("j:"+temp.get(j).wl);
////                    System.out.println("h:"+ss);}
//                    if (temp.get(i).wl.size() == ss.size()) {
////                        System.out.println("ss1:"+ss1);
////                        System.out.println("j1:"+temp.get(j).wl);
////                        System.out.println("h1:"+ss);
//                        temp.get(j).nl = function.togetstr(temp.get(j).nl, temp.get(i).nl);
//                        temp.remove(i);
//                        i--;
//                        break;
//                    }
//                    if (temp.get(j).wl.size() == ss.size()) {
////                        System.out.println("ss2:"+ss1);
////                        System.out.println("j2:"+temp.get(j).wl);
////                        System.out.println("h2:"+ss);
//                        temp.get(i).nl = function.togetstr(temp.get(j).nl, temp.get(i).nl);
//                        temp.remove(j);
//                        j--;
//                    }
                }
            }
            System.out.println("total(merge): " + temp.size());
        }

//        ArrayList<String> ttl = new ArrayList<String>(){};
//        for(int i=0;i<nwgl.size();i++){
//            ArrayList<String> dl = new ArrayList<String>(){};
//            for(int a : nwgl.get(i).wl) dl.add(wl.get(a).word);
//            System.out.println(function.compositestr(dl)+"\t"+nwgl.get(i).nl+"\t"+nwgl.get(i).mintimes);
////            nwgl.get(i).nl.removeAll(ttl);
////            ttl.addAll(nwgl.get(i).nl);
//        }

//        System.out.println("///////////////////////////////////////");
        Collections.sort(temp, new Comparator<wordgroup>() {
            @Override
            public int compare(wordgroup o1, wordgroup o2) {
                Integer n1 = o1.nl.size();
                Integer n2 = o2.nl.size();
                return n2.compareTo(n1);
            }
        });
        for(int i=0;i<temp.size();i++){
            ArrayList<String> dl = new ArrayList<String>(){};
            for(int a : temp.get(i).wl) dl.add(wl.get(a).word);
            Collections.sort(dl, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            Collections.sort(temp.get(i).nl);
            System.out.println(function.compositestr(dl)+"\t"+temp.get(i).nl+"\t"+temp.get(i).mintimes);
//            nwgl.get(i).nl.removeAll(ttl);
//            ttl.addAll(nwgl.get(i).nl);
        }
//        Collections.sort(ttl, new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                return o1.compareTo(o2);
//            }
//        });
//        System.out.println("retain news (initial merge): "+ttl.size());
//        for(news ns : nl)
//        {
//            boolean b = true;
//            for(int i=0;i<ttl.size();i++){
//                if(ns.id.equals(ttl.get(i))){
//                    b = !b;
//                    break;
//                }
//            }
//            if(b) System.out.println(ns.title);
//        }

    }
}
