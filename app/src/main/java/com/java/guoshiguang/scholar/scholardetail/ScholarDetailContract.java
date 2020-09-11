package com.java.guoshiguang.scholar.scholardetail;

import android.app.Activity;

import com.java.guoshiguang.BasePresenter;
import com.java.guoshiguang.BaseView;


public interface ScholarDetailContract {
    interface View extends BaseView<ScholarDetailContract.Presenter> {


        /**
         * 弹窗
         *
         * @param title 标题
         */
        void onShowToast(String title);

        /**
         * 开始加载
         */
        void onStartLoading();

        /**
         * 获取新闻详情失败
         */
        void onError();

        /**
         * 设置图片可见性
         *
         * @param visible 图片是否可见
         */
        void setImageVisible(boolean visible);
    }

    interface Presenter extends BasePresenter {

        /**
         * 分享
         *
         * @param activity 调用者
         */
        void shareNews(Activity activity);
    }
}
