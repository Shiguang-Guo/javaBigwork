package com.java.guoshiguang.cluster.clusternewslist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.java.guoshiguang.cluster.clusterdetail.ClusterDetailActivity;
import com.java.guoshiguang.data.DetailNews;
import com.java.guoshiguang.data.Manager;
import com.java.guoshiguang.data.SimpleNews;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

import static com.java.guoshiguang.data.Config.config;

public class ClusterListPresenter implements ClusterListContract.Presenter {

    private final int PAGE_SIZE = 20;
    public int mPageNo = 1;
    private ClusterListContract.View mView;
    private int mCategory;
    private String mKeyword;
    private boolean mLoading = false;
    private long mLastFetchStart;

    public ClusterListPresenter(ClusterListContract.View view, int category, String keyword) {
        this.mView = view;
        this.mCategory = category;
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
        Intent intent = new Intent(mView.context(), ClusterDetailActivity.class);
        intent.putExtra(ClusterDetailActivity.NEWS_ID, news.plainJson);
        intent.putExtra(ClusterDetailActivity.NEWS_TITLE, news.title);
        //intent.putExtra(NewsDetailActivity.NEWS_PICTURE_URL, news.picture_url);
        intent.putExtra(ClusterDetailActivity.NEWS_TIME, news.time);
        intent.putExtra(ClusterDetailActivity.NEWS_SOURCE, news.source);
        mView.start(intent, options);
    }


    private void fetchNews() throws Exception {
        final long start = System.currentTimeMillis();
        mLoading = true;
        mLastFetchStart = start;
        Single<List<SimpleNews>> single = null;
        String thiscate = config.availableCategories().get(mCategory).title;
        single = Manager.I.fetchClusteredEvents(mCategory, mView.context());


        single.subscribe(new Consumer<List<SimpleNews>>() {
            @SuppressLint("CheckResult")
            @Override
            public void accept(List<SimpleNews> simpleNewses) throws Exception {
                System.out.println(System.currentTimeMillis() - start + " | " + mCategory + " | " + simpleNewses.size() + " | " + mPageNo);
                if (start != mLastFetchStart) return;

                mLoading = false;
                mView.onSuccess(simpleNewses.size() == 0);
                if (simpleNewses.size() == 1 && simpleNewses.get(0) == DetailNews.NULL) {
                    simpleNewses.clear();
                    requireMoreNews();
                }
                if (mPageNo == 1) mView.setNewsList(simpleNewses);
                else mView.appendNewsList(simpleNewses);

                //if (mPageNo == 1 && simpleNewses.size() > 0 && simpleNewses.size() < 10)
                //requireMoreNews();
            }
        });
    }

}
