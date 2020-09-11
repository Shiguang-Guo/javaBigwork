package com.java.guoshiguang.scholar.scholardetail;

import android.app.Activity;

public class ScholarDetailPresenter implements ScholarDetailContract.Presenter {

    private String mScholar_ID;
    private ScholarDetailContract.View mView;

    public ScholarDetailPresenter(ScholarDetailContract.View view) {
        this.mView = view;
        view.setPresenter(this);
    }

    @Override
    public void doSomeThing() {

    }


    @Override
    public void shareNews(Activity activity) {
//        Manager_cyz.I.shareNews(activity, news.news_Title,
//                news.news_Intro.isEmpty() ? news.news_Title : news.news_Intro, news.news_URL, news.picture_url);
    }

}
