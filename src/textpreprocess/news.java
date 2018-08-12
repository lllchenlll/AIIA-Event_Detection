package textpreprocess;

import java.util.List;

public class news {
    public String id;
    public String content;
    public String title;
    public String time;
    public String source;
    public List titleseg;
    public List contentseg;
    public news(){}
    public news(String id, String time, String source, String title, String content){this.id = id; this.content = content; this.title = title; this.time = time; this.source=source;}
    public news(String time, String source, String title, String content){this.content = content; this.title = title; this.time = time; this.source=source;}
    public news(String id, String time, List titleseg, List contentseg){this.id = id; this.time = time; this.titleseg = titleseg; this.contentseg = contentseg;}
    String display(){return id+"\t"+time+"\t"+source+"\t"+title+"\t"+content;};
    String tseg(){
        String r = "";
        for(Object s : titleseg) r += String.valueOf(s) + " ";
        return r;
    }
    String cseg(){
        String r = "";
        for(Object s : contentseg) r += String.valueOf(s) + " ";
        return r;
    }
}
