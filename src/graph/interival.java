package graph;

import textpreprocess.function;
import textpreprocess.media;
import textpreprocess.news;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class interival {
    public static String Path = "";
    public static Integer interival = 2;

    public static Map<String, ArrayList<String>> il = new HashMap<String, ArrayList<String>>(){};

    public static void main(String[] args){
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(Path + "pre.txt"), "UTF-8")) {//打开data
            BufferedReader br = new BufferedReader(isr);
            try {
                String line = "";
                String now = "";
                while((line = br.readLine()) != null) {
                    String[] str = line.split("\t");//切分
                    if(!now.equals(str[1])) {il.put(str[1], new ArrayList<String>(){});now = str[1];}
                    il.get(str[1]).add(line);
                    il.get(str[1]).add(br.readLine());
                    il.get(str[1]).add(br.readLine());
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

        String start = "2014-09-01";
        String end = "2018-05-01";
        System.out.println(function.interival(start, end));
        while(function.interival(start, end) > interival){
            String temp = start;
            try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(Path + "date/" + temp + ".txt"), "UTF-8")) {//输出词频预处理文件
                for(int i=0;i<interival;i++){
                    ArrayList<String> sl = new ArrayList<String>(){};
                    if(il.containsKey(temp)) sl = il.get(temp);
                    for(String s : sl) fw.write(s+"\n");
                    temp = function.torrow(temp);
                }
                fw.close();
                System.out.println("upload is done -> "+temp+".txt");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i=0;i<(interival/2);i++) start = function.torrow(start);
        }
    }
}
