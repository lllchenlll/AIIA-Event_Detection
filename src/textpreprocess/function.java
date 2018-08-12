package textpreprocess;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static jdk.nashorn.internal.objects.NativeMath.min;

public class function {
    public static Double active(Double d){
        double ey = Math.pow(Math.E, -1 * d);
        double result = 1 / (1 + ey);
        return result;
    }
    public static String dateformat(String date){//日期格式
        Date d = new Date();
        String[] dstr = date.split("/");
        d.setYear(Integer.parseInt(dstr[0]) - 1900);
        d.setMonth(Integer.parseInt(dstr[1]) - 1);
        d.setDate(Integer.parseInt(dstr[2]));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(d);
    }
    public static String compositestr(List L){//拼字符串
        String r = "";
        for(Object s : L) r += String.valueOf(s) + " ";
        return r;
    }
    public static String compositestr(ArrayList<String> sl){//拼字符串
        String r = "";
        for(String s : sl) r += s + ",";
        return r;
    }
    public static ArrayList<String> compositestr(String s){//拼字符串
        ArrayList<String> r = new ArrayList<String>(){};
        String[] str = s.split(",");
        for(String ss : str) r.add(ss);
        return r;
    }
    public static ArrayList<String> compositestr(String s, String inter){//拼字符串
        ArrayList<String> r = new ArrayList<String>(){};
        String[] str = s.split(inter);
        for(String ss : str) r.add(ss);
        return r;
    }
    public static int hznum(Object o){
        char[] c = null;
        c = o.toString().toCharArray();
        int count = 0;
        for(int i = 0;i < c.length;i++) if(Character.toString(c[i]).matches("[\\u4E00-\\u9FA5]+")) count++;
        return count;
    }
    public static ArrayList<word> wordstat(ArrayList<word> wl, List L, String id, String time, String type){
        for(Object o : L){
            if(hznum(o) < 2) continue;
            for(int i = 0;;i++) {
                if(i == wl.size()){
                    if(type.equals("t")) wl.add(new word(String.valueOf(o), (long) 1, (long) 0, (long) 1));
                    else wl.add(new word(String.valueOf(o), (long) 0, (long) 1, (long) 1));
                    wl.get(wl.size() - 1).newsl.add(id);
                    wl.get(wl.size() - 1).dayl.add(time);
                    break;
                }
                if(wl.get(i).word.equals(String.valueOf(o))){
                    if(type.equals("t")) wl.get(i).ttimes++;
                    else wl.get(i).ctimes++;
                    for(int j = 0;;j++){
                        if(j >= wl.get(i).newsl.size()) {
                            wl.get(i).newsl.add(id);
                            wl.get(i).dayl.add(time);
                            wl.get(i).totaldocu++;
                            break;
                        }
                        if(wl.get(i).newsl.get(j).equals(id)) break;
                    }
                    break;
                }
            }
        }
        return wl;
    }
    public static ArrayList getsingle(ArrayList list){
        ArrayList newList = new ArrayList();     //创建新集合
        Iterator it = list.iterator();        //根据传入的集合(旧集合)获取迭代器
        while(it.hasNext()){          //遍历老集合
            Object obj = it.next();       //记录每一个元素
            if(!newList.contains(obj)){      //如果新集合中不包含旧集合中的元素
                newList.add(obj);       //将元素添加
            }
        }
        Collections.sort(newList, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((String) o1).compareTo((String) o2);
            }
        });
        return newList;
    }
    public static int textdis(String str1, String str2)//计算文本距离
    {
        int d[][];
        int n = str1.length();
        int m = str2.length();
        int i;
        int j;
        char ch1;
        char ch2;
        int temp;
        if(n == 0) {
            return m;
        }
        if(m == 0) {
            return n;
        }
        d = new int[n+1][m+1];
        for(i=0; i<=n; i++) {
            d[i][0] = i;
        }
        for(j=0; j<=m; j++) {
            d[0][j] = j;
        }
        for(i=1; i<=n; i++) {
            ch1 = str1.charAt(i-1);
            for(j=1; j<=m; j++) {
                ch2 = str2.charAt(j-1);
                if(ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                d[i][j] = min(java.util.Optional.of(d[i - 1][j] + 1), d[i][j-1]+1, d[i-1][j-1]+temp);
            }
        }
        return d[n][m];
    }
    public static String torrow(String date)//根据输入 返回明天的时间
    {
        String[] datestr = date.split("-");
        Date querytime = new Date();
        querytime.setYear(Integer.parseInt(datestr[0]) - 1900);
        querytime.setMonth(Integer.parseInt(datestr[1]) - 1);
        querytime.setDate(Integer.parseInt(datestr[2]));
        Date yesterday = new Date(querytime.getTime() + 24 * 60 * 60 * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String r = sdf.format(yesterday);
        return r;
    }
    public static boolean fileisexists(String strFile)
    {
        try {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }

        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    public static List lcomposite(String str){
        List res = null;
        String[] s = str.split(" ");
        for(String ss : s) res.add(ss);
        return res;
    }
    public static float ticompute(word w, ArrayList<word> zwl){
        int res = 0;
        return res;
    }
    public static Integer interival(ArrayList<String> dayl){
        String[] sstr = dayl.get(0).split("-");
        String[] estr = dayl.get(dayl.size() - 1).split("-");
        Date stime = new Date();
        Date etime = new Date();
        stime.setYear(Integer.parseInt(sstr[0]) - 1900);
        stime.setMonth(Integer.parseInt(sstr[1]) - 1);
        stime.setDate(Integer.parseInt(sstr[2]));
        etime.setYear(Integer.parseInt(estr[0]) - 1900);
        etime.setMonth(Integer.parseInt(estr[1]) - 1);
        etime.setDate(Integer.parseInt(estr[2]));
        Long res = (etime.getTime() - stime.getTime()) / (24*3600*1000);
        return res.intValue();
    }
    public static Integer interival(String s1, String s2){
        String[] sstr = s1.split("-");
        String[] estr = s2.split("-");
        Date stime = new Date();
        Date etime = new Date();
        stime.setYear(Integer.parseInt(sstr[0]) - 1900);
        stime.setMonth(Integer.parseInt(sstr[1]) - 1);
        stime.setDate(Integer.parseInt(sstr[2]));
        etime.setYear(Integer.parseInt(estr[0]) - 1900);
        etime.setMonth(Integer.parseInt(estr[1]) - 1);
        etime.setDate(Integer.parseInt(estr[2]));
        Long res = (etime.getTime() - stime.getTime()) / (24*3600*1000);
        return res.intValue();
    }

    public static Integer repectivestr(ArrayList<String> l1, ArrayList<String> l2){
        Integer r = 0;
        ArrayList<String> s = (ArrayList<String>) l1.clone();
        s.retainAll(l2);
        r = s.size();
        return r;
    }

    public static Integer repectiveint(ArrayList<Integer> l1, ArrayList<Integer> l2){
        Integer r = 0;
        ArrayList<Integer> s = (ArrayList<Integer>) l1.clone();
        s.retainAll(l2);
        r = s.size();
        return r;
    }

    public static ArrayList<Integer> togetint(ArrayList<Integer> l1, ArrayList<Integer> l2){
        ArrayList<Integer> s = (ArrayList<Integer>) l1.clone();
        ArrayList<Integer> t = (ArrayList<Integer>) l2.clone();
        s.removeAll(t);
        t.addAll(s);
        return t;
    }

    public static ArrayList<String> togetstr(ArrayList<String> l1, ArrayList<String> l2) {
        ArrayList<String> s = (ArrayList<String>) l1.clone();
        ArrayList<String> t = (ArrayList<String>) l2.clone();
        s.removeAll(t);
        t.addAll(s);
        return t;
    }
}
