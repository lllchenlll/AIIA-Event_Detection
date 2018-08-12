package integrate;

import textpreprocess.function;
import textpreprocess.word;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class overall {
    public static String Path = "";
    public static ArrayList<words> wsl;
    public static Double avgtfidf = (double) 0;
    public static Double avgtextrank = (double) 0;

    public static void main(String[] args){
        wsl = new ArrayList<words>(){};

        int num = 0;

        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(Path + "tfidf-top5000withscore.txt"), "UTF-8")) {//读取tfidf
            BufferedReader br = new BufferedReader(isr);
            try {
                String line = "";
                while((line = br.readLine()) != null) {
                    if(num++ == 1500) break;
                    String[] str = line.split("\t");
                    wsl.add(new words(str[0]));
                    wsl.get(wsl.size() - 1).tdidf = Double.valueOf(str[1]);
                    avgtfidf += Double.valueOf(str[1]);
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
        avgtfidf /= 1500;

        num = 0;
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(Path + "textrank-top5000withscore.txt"), "UTF-8")) {//读取textrank
            BufferedReader br = new BufferedReader(isr);
            try {
                String line = "";
                while((line = br.readLine()) != null) {
                    if(num++ == 1500) break;
                    String[] str = line.split("\t");
                    for(int i = 0;;i++){
                        if(i >= wsl.size()){
                            wsl.add(new words(str[0]));
                            wsl.get(wsl.size() - 1).textrank = Double.valueOf(str[1]);
                            avgtextrank += Double.valueOf(str[1]);
                            break;
                        }
                        if(wsl.get(i).s.equals(str[0])){
                            wsl.get(i).textrank = Double.valueOf(str[1]);
                            avgtextrank += Double.valueOf(str[1]);
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
        avgtextrank /= 1500;

        int n = 0;
        for(words ws : wsl){
            if(ws.tdidf!=0 && ws.textrank!= 0) {n++; }
            else{
                //System.out.println(ws.s+"\t"+ws.tdidf+"\t"+ws.textrank);
            }

        }
        System.out.println(n);
        System.out.println(wsl.size());
        System.out.println(avgtfidf);
        System.out.println(avgtextrank);

//        for(int i=0;;i++){
//            if(i >=wsl.size()) break;
//            if(wsl.get(i).tdidf != 0 && wsl.get(i).textrank != 0) continue;
//            else{
//                wsl.remove(i);
//                i--;
//            }
//        }

        Collections.sort(wsl, new Comparator<words>() {
            @Override
            public int compare(words o1, words o2) {
                return o2.textrank.compareTo(o1.textrank);
            }
        });
        System.out.println(wsl.size());

//        Collections.sort(wsl, new Comparator<words>() {
//            @Override
//            public int compare(words o1, words o2) {
//                Double dti1 = o1.tdidf;
//                Double dte1 = o1.textrank;
//                Double dti2 = o2.tdidf;
//                Double dte2 = o2.textrank;
//
//                if(dti1 == (double) 0) dti1 = avgtfidf;
//                if(dte1 == (double) 0) dte1 = avgtextrank;
//                if(dti2 == (double) 0) dti2 = avgtfidf;
//                if(dte2 == (double) 0) dte2 = avgtextrank;
//
//                dti1 *= dte1;
//                dti2 *= dte2;
//
//                return dti2.compareTo(dti1);
//            }
//        });

        ArrayList<String> tl = new ArrayList<String>(){};
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(Path + "useless.txt"), "UTF-8")) {//读取过滤词
            BufferedReader br = new BufferedReader(isr);
            try {
                String line = "";
                while((line = br.readLine()) != null) {
                    tl.add(line);
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

        ArrayList<String> al = new ArrayList<String>(){};
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(Path + "filter.txt"), "UTF-8")) {//读取过滤词
            BufferedReader br = new BufferedReader(isr);
            try {
                String line = "";
                while((line = br.readLine()) != null) {
                    al.add(line);
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

        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(Path + "final.txt"), "UTF-8")) {//输出词频预处理文件
            int nu = 0;
            for(int j = 0;j < wsl.size();j++) {
                //if(wsl.get(j).textrank != 0) fw.write(wsl.get(j).s+"\t"+wsl.get(j).textrank + "\n");
                if(wsl.get(j).textrank!=0){
                    for(int i = 0;;i++) {
                        if(i >= tl.size()) {al.add(wsl.get(j).s);break;}
                        if(tl.get(i).equals(wsl.get(j).s)) break;
                    }
                }
            }
            //System.out.println(al.size());
            Collections.sort(al, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            for(int i = 0;;i++){
                if(i >= al.size() - 1) break;
                if(al.get(i).equals(al.get(i+1))) {al.remove(i+1);i--;}
            }
            for(String s : al) fw.write(s + "\n");
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
}
