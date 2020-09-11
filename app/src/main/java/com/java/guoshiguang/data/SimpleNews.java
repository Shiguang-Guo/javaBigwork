package com.java.guoshiguang.data;

import java.util.List;

public class SimpleNews {
    public String plainJson;
    public long tflag;
    public String id;
    public String type;
    public String title;
    public String category;
    public String lang;
    public String time;
    public String source;
    public List<GeoInfo> geoInfo;
    public List<Author> authors;
    public double influence;
    public boolean hasRead = false;
    public boolean local;

    static class GeoInfo {
        public String geoName;
        public String latitude;
        public String longitude;
        public String originText;

    }

    static class Author {
        public String name;
    }


}