package com.java.guoshiguang.data;

import android.content.Context;

import androidx.annotation.NonNull;

import com.java.guoshiguang.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.reactivestreams.Publisher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class Manager {
    public static Manager I = null;
    public FS fs;
    //很多私有变量
    //补齐在收藏、已经阅读等信息
    private FlowableTransformer<SimpleNews, SimpleNews> liftAllSimple;
    private FlowableTransformer<DetailNews, DetailNews> liftAllDetail;

    private Manager(final Context context) throws IOException {
        //TODO

        this.liftAllSimple = new FlowableTransformer<SimpleNews, SimpleNews>() {
            @Override
            public Publisher<SimpleNews> apply(@NonNull Flowable<SimpleNews> upstream) {
                return upstream.map(new Function<SimpleNews, SimpleNews>() {
                    @Override
                    public SimpleNews apply(SimpleNews simpleNews) throws Exception {
                        if (simpleNews == DetailNews.NULL)
                            return simpleNews;
                        simpleNews.hasRead = fs.hasRead(simpleNews.id);
                        return simpleNews;
                    }
                });
            }
        };

        this.liftAllDetail = new FlowableTransformer<DetailNews, DetailNews>() {
            @Override
            public Publisher<DetailNews> apply(Flowable<DetailNews> upstream) {
                return upstream.map(new Function<DetailNews, DetailNews>() {
                    @Override
                    public DetailNews apply(DetailNews detailNews) throws Exception {
                        if (detailNews == DetailNews.NULL)
                            return detailNews;
                        detailNews.hasRead = fs.hasRead(detailNews.id);
                        return detailNews;
                    }
                });
            }
        };
        fs = new FS(context);
    }

    public static synchronized void CreateI(Context context) {
        try {
            I = new Manager(context);
        } catch (IOException e) {
        }
    }

//    /**
//     * simpleNews
//     *
//     */
//    public Single<List<SimpleNews>> fetchSimpleNews(long timeFlag) {
//        //TODO
//    }

    /**
     * simpleNews
     * 失败返回空列表
     */
    public Single<List<SimpleNews>> fetchSimpleNews(final String type, final int page, final int size) throws Exception {
        return Flowable.fromCallable(new Callable<List<SimpleNews>>() {
            @Override
            public List<SimpleNews> call() {
                try {
                    return API.getSimpleNews(type, page, size);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            }

        }).flatMap(new Function<List<SimpleNews>, Publisher<SimpleNews>>() {
            @Override
            public Publisher<SimpleNews> apply(List<SimpleNews> simpleNewses) throws Exception {
                if (simpleNewses.size() > 0) {
                    return Flowable.fromIterable(simpleNewses);
                }
                return Flowable.fromIterable(fs.fetchSimple(type, page, size));

            }
        }).map(new Function<SimpleNews, SimpleNews>() {
            @Override
            public SimpleNews apply(SimpleNews simpleNews) throws Exception {
                fs.insertSimple(simpleNews, type);
                return simpleNews;
            }
        }).compose(liftAllSimple).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取已读列表
     * 如果失败返回空列表
     */
    public Single<List<SimpleNews>> fetchReadNews() {
        return Flowable.fromCallable(new Callable<List<SimpleNews>>() {
            @Override
            public List<SimpleNews> call() {
                try {
                    return fs.fetchRead();
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            }
        }).flatMap(new Function<List<SimpleNews>, Publisher<SimpleNews>>() {
            @Override
            public Publisher<SimpleNews> apply(List<SimpleNews> simpleNews) throws Exception {
                if (simpleNews.size() > 0) {
                    return Flowable.fromIterable(simpleNews);
                } else {
                    return Flowable.fromIterable(simpleNews);
                }
            }
        }).map(new Function<SimpleNews, SimpleNews>() {
            @Override
            public SimpleNews apply(SimpleNews simpleNews) {

                simpleNews.hasRead = true;
                return simpleNews;
            }
        }).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * detailNews
     * 如果失败返回detailNews.NULL
     */

    public Single<DetailNews> fetchDetailNews(final String newsId) {
        return Flowable.fromCallable(new Callable<DetailNews>() {
            @Override
            public DetailNews call() {
                try {
                    return API.getDetailNews(newsId);
                } catch (Exception e) {
                    e.printStackTrace();
                    return DetailNews.NULL;
                }
            }
        }).flatMap(new Function<DetailNews, Publisher<DetailNews>>() {
            @Override
            public Publisher<DetailNews> apply(DetailNews detailNews) throws Exception {
                if (detailNews == DetailNews.NULL) {
                    return Flowable.just(fs.fetchDetail(newsId));
                }
                fs.insertDetail(detailNews);
                return Flowable.just(detailNews);
            }
        }).compose(liftAllDetail).firstOrError().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<SimpleNews>> searchNewsData(final String type, int mPageNo, final String keyword) {
        return Flowable.fromCallable(new Callable<List<SimpleNews>>() {
            @Override
            public List<SimpleNews> call() throws Exception {
                try {
                    return API.getSimpleNews(type, mPageNo, 700);

                } catch (Exception e) {
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            }
        }).flatMap(new Function<List<SimpleNews>, Publisher<SimpleNews>>() {
            @Override
            public Publisher<SimpleNews> apply(List<SimpleNews> simpleNews) throws Exception {

                return Flowable.fromIterable(simpleNews);

            }
        }).filter(new Predicate<SimpleNews>() {
            @Override
            public boolean test(SimpleNews simpleNews) throws Exception {
                Pattern pattern = Pattern.compile(keyword);
                Matcher matcher = pattern.matcher(simpleNews.title);

                return matcher.find();
            }
        }).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 如果失败返回size为0的表
     */
    public Single<HashMap<Region, EpidemicData>> fetchEpidemicData() {
        return Flowable.fromCallable(new Callable<HashMap<Region, EpidemicData>>() {
            @Override
            public HashMap<Region, EpidemicData> call() {
                try {
                    return API.getEpidemicData();
                } catch (Exception e) {
                    e.printStackTrace();
                    return new HashMap<>();
                }
            }
        }).firstOrError().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    }

    /**
     *
     */
    public Single<List<Entity>> searchEntityData(final String entityName) {
        return Flowable.fromCallable(new Callable<List<Entity>>() {
            @Override
            public List<Entity> call() {
                try {
                    return API.searchEntity(entityName);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            }
        }).firstOrError().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     *
     */
    public Single<List<Scholar>> fetchScholarData(Boolean iPA) {
        //"Highly Concerned": "Remembrance";
        return Flowable.fromCallable(new Callable<List<Scholar>>() {
            @Override
            public List<Scholar> call() throws Exception {
                try {
                    return API.getScholar();
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            }
        }).flatMap(new Function<List<Scholar>, Publisher<Scholar>>() {
            @Override
            public Publisher<Scholar> apply(List<Scholar> scholars) throws Exception {
                return Flowable.fromIterable(scholars);
            }
        }).filter(new Predicate<Scholar>() {
            @Override
            public boolean test(Scholar scholar) throws Exception {
                return iPA == scholar.is_passedaway;
            }
        }).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 添加已读
     */
    public void touchRead(final String newsId) {
        Single.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                DetailNews news = fs.fetchDetail(newsId);
                try {
                    if (news == null) {
                        news = API.getDetailNews(newsId);
                    }
                    fs.insertRead(newsId, news);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new Object();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }


    public Single<String[][]> getIndices(HashMap<Region, EpidemicData> hashMap) {
        return Flowable.fromCallable(new Callable<HashMap<Region, EpidemicData>>() {
            @Override
            public HashMap<Region, EpidemicData> call() throws Exception {
                return hashMap;
            }
        }).map(new Function<HashMap<Region, EpidemicData>, String[][]>() {
            @Override
            public String[][] apply(HashMap<Region, EpidemicData> hashMap) throws Exception {
                HashMap<String, ArrayList<String>> tempMap = new HashMap<>();
                for (Region r : hashMap.keySet()) {
                    if (r.county.length() > 0)
                        continue;
                    if (!tempMap.containsKey(r.country)) {
                        tempMap.put(r.country, new ArrayList<>());
                    }
                    if (r.province.length() > 0) {
                        ArrayList<String> emm = tempMap.get(r.country);
                        emm.add(r.province);
                        tempMap.replace(r.country, emm);
                    }
                }
                String[][] result = new String[tempMap.keySet().size()][];
                int i = 0;
                for (String c : tempMap.keySet()) {
                    result[i] = new String[tempMap.get(c).size() + 1];
                    ArrayList<String> tempList = tempMap.get(c);
                    result[i][0] = c;
                    for (int j = 1; j < tempList.size(); j++) {
                        result[i][j] = tempList.get(j);
                    }
                    i++;
                }
                return result;
            }
        }).firstOrError().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<String[]>> fetchClusteredKeyword(final Context context) {
        return Flowable.fromCallable(new Callable<List<String[]>>() {
            @Override
            public List<String[]> call() throws Exception {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.emm)));
                    JSONArray array = new JSONArray(in.readLine());
                    List<String[]> result = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONArray newsArray = array.getJSONObject(i).getJSONArray("keyword&quot");

                        result.add(new String[]{newsArray.getString(0), newsArray.getString(1), newsArray.getString(2)});
                    }
                    return result;
                } catch (Exception e) {
                    return new ArrayList<>();
                }
            }
        }).firstOrError().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<SimpleNews>> fetchClusteredEvents(final int cluster, final Context context) {
        return Flowable.fromCallable(new Callable<List<SimpleNews>>() {

            @Override
            public List<SimpleNews> call() throws Exception {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.emm)));
                    JSONArray array = new JSONArray(in.readLine());
                    JSONArray newsArray = array.getJSONObject(cluster).getJSONArray("plainJson");
                    List<SimpleNews> temp = new ArrayList<>();
                    for (int i = 0; i < newsArray.length(); i++) {
                        temp.add(API.getSimpleNewsFromJson(newsArray.getJSONObject(i), true));
                    }
                    return temp;
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            }
        }).firstOrError().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<DetailNews> fetchDetailEvents(String plainjson) {
        try {
            System.out.println(plainjson);
            return Flowable.just(API.getDetailNewsFromJson(new JSONObject(plainjson), true))
                    .firstOrError()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io());
        } catch (Exception e) {
            e.printStackTrace();
            return Flowable.just(DetailNews.NULL)
                    .firstOrError()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    }
    public void clean() {
        fs.dropTables();
        fs.createTables();
    }




}

