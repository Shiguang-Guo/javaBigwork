package com.java.guoshiguang.main;

import com.java.guoshiguang.R;



public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mMainView;
    private int mCurrentNavigation = R.id.nav_news;

    public MainPresenter(MainContract.View view) {
        this.mMainView = view;
        view.setPresenter(this);
    }

    public int getCurrentNavigation() {
        return mCurrentNavigation;
    }

    @Override
    public void doSomeThing() {
        switchNavigation(mCurrentNavigation);
    }

    @Override
    public void switchNavigation(int id) {
        mCurrentNavigation = id;
        switch (id) {
            case R.id.nav_news:
                mMainView.switchToNews();
                break;
            case R.id.nav_settings:
                mMainView.switchToSettings();
                break;
            case R.id.nav_history:
                mMainView.switchToHistory();
                break;
            case R.id.nav_chart:
                mMainView.switchToChart();
                break;
            case R.id.nav_kg:
                mMainView.switchTokg();
                break;
            case R.id.nav_cluster:
                mMainView.switchTocluster();
                break;
            case R.id.nav_scholar:
                mMainView.switchToscholar();
                break;
            default:
                break;
        }
    }
}
