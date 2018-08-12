import textpreprocess.preprocess;
import tfidf.totalkeyword;

public class main {
    public static void main(String[] args){
        if(args[0].equals("preprocess")) textpreprocess.preprocess.main(new String[] {"dataset.csv"});
        if(args[0].equals("tfidf")) tfidf.totalkeyword.main(new String[] {"totalwordstat.txt"});
        if(args[0].equals("textrank")) textrank.textrank.main(new String[] {"pre.txt"});
        if(args[0].equals("fusion")) integrate.overall.main(new String[] {""});
    }
}
