package com.java.guoshiguang.cluster.clusterdetail;

import android.app.Activity;

import com.java.guoshiguang.data.DetailNews;
import com.java.guoshiguang.data.Manager;

import io.reactivex.functions.Consumer;

public class ClusterDetailPresenter implements ClusterDetailContract.Presenter {

    private String mPlainjson;
    private ClusterDetailContract.View mView;

    public ClusterDetailPresenter(ClusterDetailContract.View view, String news_ID) {
        mPlainjson = news_ID;
        System.out.println(news_ID);
        System.out.println("!@#$$%^&*(&^#$%^&*^%$%^&");
        this.mView = view;
        view.setPresenter(this);
    }

    @Override
    public void doSomeThing() {
        Manager.I.fetchDetailEvents(mPlainjson)
                .subscribe(new Consumer<DetailNews>() {
                    @Override
                    public void accept(DetailNews detailNews) throws Exception {
                        if (detailNews == DetailNews.NULL) {
                            System.out.println("%%%%%%%%%%%%%%%%%%%%");
                            mView.setImageVisible(false);
                            mView.onError();
                        } else {
                            System.out.println("********************************");
                            Manager.I.touchRead(mPlainjson);
                            mView.setNewsDetail(detailNews);
                        }
                    }
                });
        mView.onStartLoading();
    }


    @Override
    public void shareNews(Activity activity, DetailNews news) {
//        Manager_cyz.I.shareNews(activity, news.news_Title,
//                news.news_Intro.isEmpty() ? news.news_Title : news.news_Intro, news.news_URL, news.picture_url);
    }

}
