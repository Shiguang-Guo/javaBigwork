package com.java.guoshiguang.news.newsdetail;

import android.app.Activity;
import android.content.Intent;

import com.java.guoshiguang.data.DetailNews;
import com.java.guoshiguang.data.Manager;

import io.reactivex.functions.Consumer;


public class NewsDetailPresenter implements NewsDetailContract.Presenter {

    private String mNews_ID;
    private NewsDetailContract.View mView;

    public NewsDetailPresenter(NewsDetailContract.View view, String news_ID) {
        this.mNews_ID = news_ID;
        this.mView = view;
        view.setPresenter(this);
    }

    @Override
    public void doSomeThing() {
        Manager.I.fetchDetailNews(mNews_ID)
                .subscribe(new Consumer<DetailNews>() {
                    @Override
                    public void accept(DetailNews detailNews) throws Exception {
                        if (detailNews == DetailNews.NULL) {
                            mView.setImageVisible(false);
                            mView.onError();
                        } else {
                            Manager.I.touchRead(mNews_ID);
                            mView.setNewsDetail(detailNews);
                        }
                    }
                });
        mView.onStartLoading();
    }


    @Override
    public void shareNews(Activity activity, DetailNews news) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, news.title + "\n" + news.date + "\n" + news.content);
        activity.startActivity(sendIntent);

    }

}
