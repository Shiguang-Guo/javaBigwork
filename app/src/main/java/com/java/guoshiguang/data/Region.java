package com.java.guoshiguang.data;

public class Region {
    public String country = "";
    public String province = "";
    public String county = "";

    public Region() {

    }

    public Region(String[] args) {
        country = args[0];
        if (args.length > 1) {
            province = args[1];
        }
        if (args.length > 2) {
            county = args[2];
        }
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Region && (country.equals(((Region) obj).country))
                && (province.equals(((Region) obj).province)) && (county.equals(((Region) obj).county));
    }

    @Override
    public String toString() {
        String result = country;
        if (province.length() > 0) {
            result += "|" + province;
        }
        if (county.length() > 0) {
            result += "|" + county;
        }
        return result;
    }
}
