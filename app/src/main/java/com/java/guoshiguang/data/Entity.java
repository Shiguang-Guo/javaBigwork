package com.java.guoshiguang.data;

import java.util.HashMap;
import java.util.List;

public class Entity {
    public double hot = -1;
    public String label;
    public String url;
    public String img;
    public String enwiki;
    public String baidu;
    public String zhwiki;
    public HashMap<String, String> properties;
    public List<Relation> relations;

    public static class Relation {
        public boolean forward;
        public String relationType;
        public String url;
        public String label;
    }
}
