package integrate;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class remove {
    public static String Path = "";
    public static ArrayList<String> sl = new ArrayList<String>(){};
    public static void main(String[] args){
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(Path + "final.txt"), "UTF-8")) {//读取总字频
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
        Collections.sort(sl, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        for(int i = 0;;i++){
            if(i >= sl.size() - 1) break;
            if(sl.get(i).equals(sl.get(i+1))) {System.out.println(sl.get(i)+"\t"+sl.get(i+1));sl.remove(i+1);i--;}
        }
        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(Path + "final1.txt"), "UTF-8")) {//输出词频预处理文件
            int nu = 0;
            for(int j = 0;j < sl.size();j++) {
                fw.write(sl.get(j) + "\n");
            }
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
