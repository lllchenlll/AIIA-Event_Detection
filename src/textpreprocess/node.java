package textpreprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class node {
    public Integer id;
    public String w;
    public Map<Integer, Integer> nl;
    public Long weight;
    public Double tf;
    public Double idf;
    public node(){}
    public node(Integer i){this.id = i; this.nl = new HashMap<Integer, Integer>(){}; this.weight = (long) 0;}
    public node(String w, Double tf, Double idf){this.w = w; this.tf = tf; this.idf = idf; this.weight = (long) 0; nl = new HashMap<Integer, Integer>(){};}
}
