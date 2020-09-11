package com.java.guoshiguang.news.newslist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.java.guoshiguang.data.DetailNews;
import com.java.guoshiguang.data.Manager;
import com.java.guoshiguang.data.SimpleNews;
import com.java.guoshiguang.news.newsdetail.NewsDetailActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

import static com.java.guoshiguang.data.Config.config;


public class NewsListPresenter implements NewsListContract.Presenter {

    private final int PAGE_SIZE = 20;
    public int mPageNo = 1;
    private NewsListContract.View mView;
    private int mCategory;
    private String mKeyword;
    private boolean mLoading = false;
    private long mLastFetchStart;

    public NewsListPresenter(NewsListContract.View view, int category, String keyword) {
        this.mView = view;
        this.mCategory = category;
        this.mKeyword = keyword;

        view.setPresenter(this);
    }

    @Override
    public boolean isLoading() {
        return mLoading;
    }

    @Override
    public void setKeyword(String keyword) throws Exception {
        mKeyword = keyword;
        refreshNews();
    }

    @Override
    public void doSomeThing() throws Exception {
        refreshNews();
    }

    @Override
    public void requireMoreNews() throws Exception {
        mPageNo++;
        fetchNews();
    }

    @Override
    public void refreshNews() throws Exception {
        mPageNo = 1;
        fetchNews();
    }

    @Override
    public void openNewsDetailUI(SimpleNews news, Bundle options) {
        Intent intent = new Intent(mView.context(), NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.NEWS_ID, news.id);
        intent.putExtra(NewsDetailActivity.NEWS_TITLE, news.title);
        //intent.putExtra(NewsDetailActivity.NEWS_PICTURE_URL, news.picture_url);
        intent.putExtra(NewsDetailActivity.NEWS_TIME, news.time);
        intent.putExtra(NewsDetailActivity.NEWS_SOURCE, news.source);
        mView.start(intent, options);
    }

    @Override
    public void fetchNewsRead(final int pos, SimpleNews news) {
        Manager.I.fetchDetailNews(news.id)
                .subscribe(new Consumer<DetailNews>() {
                    @Override
                    public void accept(DetailNews detailNews) throws Exception {
                        mView.resetItemRead(pos, detailNews.hasRead);
                    }
                });
    }

    private void fetchNews() throws Exception {
        final long start = System.currentTimeMillis();
        mLoading = true;
        mLastFetchStart = start;
        Single<List<SimpleNews>> single = null;
        String thiscate = config.availableCategories().get(mCategory).title;
        if (mKeyword.trim().length() > 0) {
            single = Manager.I.searchNewsData(thiscate, mPageNo, mKeyword);
        } else if (mCategory >= 0) {

            single = Manager.I.fetchSimpleNews(thiscate, mPageNo, PAGE_SIZE);
        }

        single.subscribe(new Consumer<List<SimpleNews>>() {
            @SuppressLint("CheckResult")
            @Override
            public void accept(List<SimpleNews> simpleNewses) throws Exception {
                System.out.println(System.currentTimeMillis() - start + " | " + mCategory + " | " + simpleNewses.size() + " | " + mPageNo);
                if (start != mLastFetchStart) return;

                mLoading = false;
                if (mKeyword.trim().length() != 0 || mCategory >= 0) {
                    mView.onSuccess(simpleNewses.size() == 0);
                    if (simpleNewses.size() == 1 && simpleNewses.get(0) == DetailNews.NULL) {
                        simpleNewses.clear();
                        requireMoreNews();
                    }
                    if (mPageNo == 1) mView.setNewsList(simpleNewses);
                    else mView.appendNewsList(simpleNewses);

                    //if (mPageNo == 1 && simpleNewses.size() > 0 && simpleNewses.size() < 10)
                    //requireMoreNews();
                } else {
                    if (mPageNo > 1 || simpleNewses.size() == 0) {
                        mView.onSuccess(true);
                        mView.appendNewsList(new ArrayList<>());
                    } else {
                        mView.onSuccess(false);
                        mView.setNewsList(simpleNewses);
                    }
                }
            }
        });
    }
}

