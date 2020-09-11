package com.java.guoshiguang.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


class API {
    private API() {
    }

    /**
     * @param jsonNews
     * @return SimpleNews
     */
    static SimpleNews getSimpleNewsFromJson(JSONObject jsonNews, boolean fromDisk) throws JSONException {
        String temp;
        SimpleNews news = new SimpleNews();
        news.local = fromDisk;
        news.plainJson = jsonNews.toString();

        news.category = jsonNews.optString("category");
        news.id = jsonNews.optString("_id");
        news.lang = jsonNews.optString("lang");

        if ((temp = jsonNews.optString("tflag")).length() != 0) {
            news.tflag = Long.parseLong(temp);
        }
        news.type = jsonNews.optString("type");
        news.title = jsonNews.optString("title");
        news.time = jsonNews.optString("time");
        news.source = jsonNews.optString("source");
        JSONArray geoList = jsonNews.optJSONArray("geoInfo");
        if (geoList != null) {
            news.geoInfo = new ArrayList<>();
            for (int i = 0; i < geoList.length(); i++) {
                JSONObject ob = geoList.getJSONObject(i);
                SimpleNews.GeoInfo emm = new SimpleNews.GeoInfo();
                emm.geoName = ob.optString("geoName");
                emm.latitude = ob.optString("latitude");
                emm.longitude = ob.optString("longitude");
                emm.originText = jsonNews.optString("originText");
                news.geoInfo.add(emm);
            }
        }

        news.influence = -1;
        if ((temp = jsonNews.optString("influence")).length() != 0) {
            news.influence = Double.parseDouble(temp);
        }

        JSONArray authorList = jsonNews.optJSONArray("authors");
        if (authorList != null) {
            news.authors = new ArrayList<>();
            for (int i = 0; i < authorList.length(); i++) {
                JSONObject ob = authorList.getJSONObject(i);
                SimpleNews.Author au = new SimpleNews.Author();
                au.name = ob.optString("name");
                news.authors.add(au);
            }
        }

        return news;
    }


    /**
     * @param jsonNews
     * @return DetailNews
     */
    static DetailNews getDetailNewsFromJson(JSONObject jsonNews, boolean fromDisk) throws JSONException {
        String temp;
        DetailNews news = new DetailNews();
        news.local = fromDisk;
        news.plainJson = jsonNews.toString();
        news.category = jsonNews.optString("category");
        news.id = jsonNews.optString("_id");
        news.lang = jsonNews.optString("lang");

        if ((temp = jsonNews.optString("tflag")).length() != 0) {
            news.tflag = Long.parseLong(temp);
        }
        news.type = jsonNews.optString("type");
        news.title = jsonNews.optString("title");
        news.time = jsonNews.optString("time");
        news.source = jsonNews.optString("source");
        JSONArray geoList = jsonNews.optJSONArray("geoInfo");
        if (geoList != null) {
            news.geoInfo = new ArrayList<>();
            for (int i = 0; i < geoList.length(); i++) {
                JSONObject ob = geoList.getJSONObject(i);
                SimpleNews.GeoInfo emm = new SimpleNews.GeoInfo();
                emm.geoName = ob.optString("geoName");
                emm.latitude = ob.optString("latitude");
                emm.longitude = ob.optString("longitude");
                emm.originText = jsonNews.optString("originText");
                news.geoInfo.add(emm);
            }
        }

        news.influence = -1;
        if ((temp = jsonNews.optString("influence")).length() != 0) {
            news.influence = Double.parseDouble(temp);
        }

        JSONArray authorList = jsonNews.optJSONArray("authors");
        if (authorList != null) {
            news.authors = new ArrayList<>();
            for (int i = 0; i < authorList.length(); i++) {
                JSONObject ob = authorList.getJSONObject(i);
                SimpleNews.Author au = new SimpleNews.Author();
                au.name = ob.optString("name");
                news.authors.add(au);
            }
        }


        news.content = jsonNews.optString("content");
        news.date = jsonNews.optString("date");
        return news;
    }

    /**
     * 从json获得疫情数据
     */
    static HashMap<Region, EpidemicData> getEpidemicDataFromJson(JSONObject jsonData) throws JSONException {
        Iterator<String> iter = jsonData.keys();
        HashMap<Region, EpidemicData> result = new HashMap<>();
        while (iter.hasNext()) {
            String input = iter.next();
            String[] args = input.split("\\|");
            Region region = new Region(args);


            EpidemicData data = new EpidemicData();
            JSONObject rawData = jsonData.getJSONObject(input);
            data.begin = rawData.optString("begin");
            data.data = new ArrayList<>();
            JSONArray daysArray = rawData.optJSONArray("data");
            for (int i = 0; i < daysArray.length(); i++) {
                JSONArray emm = daysArray.getJSONArray(i);
                Day day = new Day();
                day.confirmed = emm.optInt(0);
                day.suspected = emm.optInt(1);
                day.cured = emm.optInt(2);
                day.dead = emm.optInt(3);
                data.data.add(day);
            }
            result.put(region, data);
        }
        return result;

    }

    /**
     * 从json获得实体列表
     */
    static Entity getEntityFromJson(JSONObject jsonData) throws JSONException {
        Entity entity = new Entity();
        entity.hot = jsonData.optDouble("hot");
        entity.label = jsonData.optString("label");
        entity.url = jsonData.optString("url");
        entity.img = jsonData.optString("img");
        JSONObject info = jsonData.getJSONObject("abstractInfo");
        entity.enwiki = info.optString("enwiki");
        entity.baidu = info.optString("baidu");
        entity.zhwiki = info.optString("zhwiki");

        JSONObject COVID = info.getJSONObject("COVID");
        JSONArray relationList = COVID.getJSONArray("relations");
        entity.relations = new ArrayList<>();
        for (int i = 0; i < relationList.length(); i++) {
            JSONObject rJson = relationList.getJSONObject(i);
            Entity.Relation relation = new Entity.Relation();
            relation.relationType = rJson.optString("relation");
            relation.label = rJson.optString("label");
            relation.forward = rJson.optBoolean("forward");
            relation.url = rJson.optString("url");
            entity.relations.add(relation);
        }

        JSONObject properties = COVID.getJSONObject("properties");
        Iterator<String> iter = properties.keys();
        entity.properties = new HashMap<>();
        while (iter.hasNext()) {
            String key = iter.next();
            entity.properties.put(key, properties.getString(key));
        }

        return entity;
    }

    /**
     * 从json获得学者列表
     */
    static Scholar getScholarFromJson(JSONObject jsonData) throws JSONException {
        Scholar scholar = new Scholar();
        scholar.avatar = jsonData.optString("avatar");
        scholar.id = jsonData.optString("id");
        scholar.is_passedaway = jsonData.optBoolean("is_passedaway");
        scholar.name = jsonData.optString("name");
        scholar.name_zh = jsonData.optString("name_zh");
        scholar.indices = new Scholar.Indices();
        JSONObject jsonIndices = jsonData.getJSONObject("indices");
        scholar.indices.activity = jsonIndices.optDouble("activity");
        scholar.indices.citations = jsonIndices.optInt("citations");
        scholar.indices.diversity = jsonIndices.optDouble("diversity");
        scholar.indices.gindex = jsonIndices.optInt("gindex");
        scholar.indices.hindex = jsonIndices.optInt("hindex");
        scholar.indices.newStar = jsonIndices.optDouble("newStar");
        scholar.indices.pubs = jsonIndices.optInt("pubs");
        scholar.indices.risingStar = jsonIndices.optDouble("risingStar");
        scholar.indices.sociability = jsonIndices.optDouble("sociability");

        JSONObject jsonProfile = jsonData.getJSONObject("profile");
        scholar.profile = new Scholar.Profile();
        scholar.profile.affiliation = jsonProfile.optString("affiliation");
        scholar.profile.affiliation_zh = jsonProfile.optString("affiliation_zh");
        scholar.profile.bio = jsonProfile.optString("bio");
        scholar.profile.edu = jsonProfile.optString("edu");
        scholar.profile.homepage = jsonProfile.optString("homepage");
        scholar.profile.position = jsonProfile.optString("position");
        scholar.profile.work = jsonProfile.optString("work");
        return scholar;
    }

    /**
     * 从update接口获取很多SimpleNews
     */
    static List<SimpleNews> getSimpleNews(final long timeFlag) throws IOException, JSONException {
        String url = String.format("https://covid-dashboard.aminer.cn/api/events/update?tflag=%l", timeFlag);
        String body = getBodyFromURL(url);
        JSONObject allData = new JSONObject(body);
        JSONArray newsArray = allData.getJSONObject("data").getJSONArray("datas");
        List<SimpleNews> result = new ArrayList<>();
        for (int i = 0; i < newsArray.length(); i++) {
            JSONObject temp = newsArray.getJSONObject(i);
            result.add(getSimpleNewsFromJson(temp, false));
        }
        return result;
    }


    /**
     * 从List接口获取很多SimpleNews
     */
    static List<SimpleNews> getSimpleNews(final String type, final int page, final int size) throws IOException, JSONException {
        String url = String.format("https://covid-dashboard.aminer.cn/api/events/list?type=%s&page=%d&size=%d", type, page, size);
        String body = getBodyFromURL(url);
        JSONObject allData = new JSONObject(body);
        JSONArray newsArray = allData.getJSONArray("data");
        ArrayList<SimpleNews> result = new ArrayList<>();
        for (int i = 0; i < newsArray.length(); i++) {
            JSONObject temp = newsArray.getJSONObject(i);
            result.add(getSimpleNewsFromJson(temp, false));
        }
        return result;
    }

    /**
     * 获取新闻详情
     *
     * @param newsId
     * @return 新闻详情
     */
    static DetailNews getDetailNews(final String newsId) throws IOException, JSONException {
        String url = String.format("https://covid-dashboard.aminer.cn/api/event/%s", newsId);
        String body = getBodyFromURL(url);
        JSONObject allData = new JSONObject(body).getJSONObject("data");

        return getDetailNewsFromJson(allData, false);
    }


    /**
     * 获取所有国家的疫情数据
     */
    static HashMap<Region, EpidemicData> getEpidemicData() throws IOException, JSONException {
        String url = "https://covid-dashboard.aminer.cn/api/dist/epidemic.json";
        String body = getBodyFromURL(url);

        JSONObject jsonData = new JSONObject(body);
        return getEpidemicDataFromJson(jsonData);
    }


    /**
     * 获取新冠图谱实体
     *
     * @param entityName
     * @return 返回实体列表
     */
    static List<Entity> searchEntity(String entityName) throws IOException, JSONException {
        String url = String.format("https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity=%s", entityName);
        String body = getBodyFromURL(url);

        JSONArray jsonData = new JSONObject(body).getJSONArray("data");
        List<Entity> list = new ArrayList<>();
        for (int i = 0; i < jsonData.length(); i++) {
            list.add(getEntityFromJson(jsonData.getJSONObject(i)));
        }
        return list;

    }

    /**
     * 获取学者列表
     */
    static List<Scholar> getScholar() throws IOException, JSONException {
        String url = "https://innovaapi.aminer.cn/predictor/api/v1/valhalla/highlight/get_ncov_expers_list?v=2";
        String body = getBodyFromURL(url);
        JSONArray jsonArray = new JSONObject(body).getJSONArray("data");
        List<Scholar> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(getScholarFromJson(jsonArray.getJSONObject(i)));
        }
        return list;
    }

    /**
     * @param url 网页地址
     * @return 网页内容
     */
    static String getBodyFromURL(String url) throws IOException {
        URL cs = new URL(url);
        URLConnection uc = cs.openConnection();
        uc.setConnectTimeout(20 * 1000);
        uc.setReadTimeout(10 * 1000);
        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        String body = "", line = "";
        while ((line = in.readLine()) != null) {
            body += line;
        }
        return body;
    }
}

