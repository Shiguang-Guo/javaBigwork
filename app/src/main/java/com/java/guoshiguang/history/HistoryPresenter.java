package com.java.guoshiguang.history;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.java.guoshiguang.data.DetailNews;
import com.java.guoshiguang.data.Manager;
import com.java.guoshiguang.data.SimpleNews;
import com.java.guoshiguang.news.newsdetail.NewsDetailActivity;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;


public class HistoryPresenter implements HistoryContract.Presenter {

    private final int PAGE_SIZE = 20;
    public int mPageNo = 1;
    private HistoryContract.View mView;
    private boolean mLoading = false;
    private long mLastFetchStart;

    public HistoryPresenter(HistoryContract.View view) {
        this.mView = view;
        view.setPresenter(this);
    }

    @Override
    public boolean isLoading() {
        return mLoading;
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
        single = Manager.I.fetchReadNews();

        single.subscribe(new Consumer<List<SimpleNews>>() {
            @SuppressLint("CheckResult")
            @Override
            public void accept(List<SimpleNews> simpleNewses) throws Exception {
                if (start != mLastFetchStart) return;

                mLoading = false;
                mView.onSuccess(simpleNewses.size() == 0);

                mView.setNewsList(simpleNewses);
            }
        });
    }
}

