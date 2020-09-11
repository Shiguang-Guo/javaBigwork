package com.java.guoshiguang.data;

public class Scholar {
    public boolean is_passedaway;
    public String avatar;
    public String id;
    public String name;
    public String name_zh;
    public Indices indices;
    public Profile profile;

    public static class Indices {
        public double activity;
        public int citations;
        public double diversity;
        public int gindex;
        public int hindex;
        public double newStar;
        public int pubs;
        public double risingStar;
        public double sociability;
    }

    public static class Profile {
        public String affiliation;
        public String affiliation_zh;
        public String position;

        public String homepage;
        public String work;
        public String bio;
        public String edu;

    }

}
