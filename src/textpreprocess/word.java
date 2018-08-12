package textpreprocess;

import java.util.ArrayList;

public class word {
    public Integer id;
    public String word;
    public Long ttimes;
    public Long ctimes;
    public Long totaldocu;
    public Long localdocu;
    public ArrayList<String> newsl;
    public ArrayList<String> dayl;
    public Integer hot;
    public Double tf;
    public Double idf;
    public Double rank;
    public word(){}
    public word(String word){
        this.word = word;
        ttimes = (long) 0;
        ctimes = (long) 0;
        hot = 0;
        tf = (double) 0;
        idf = (double) 0;
        rank = (double) 0;
        totaldocu = (long) 0;
        localdocu = (long) 0;}
    public word(String word, Long ttimes, Long ctimes, Long totaldocu){
        this.word = word;
        this.ttimes = ttimes;
        this.ctimes = ctimes;
        hot = 0;
        tf = (double) 0;
        idf = (double) 0;
        rank = (double) 0;
        localdocu = (long) 0;
        this.totaldocu = totaldocu;
        newsl = new ArrayList<String>(){};
        dayl = new ArrayList<String>(){};}
    public String display(){return word+"\t"+ttimes+"\t"+ctimes;}
}
