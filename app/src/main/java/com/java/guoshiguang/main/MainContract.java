package com.java.guoshiguang.main;

import com.java.guoshiguang.BasePresenter;
import com.java.guoshiguang.BaseView;

public interface MainContract {

    interface View extends BaseView<Presenter> {

        /**
         * 切换到新闻页面
         */
        void switchToNews();

        /**
         * 切换到设置页面
         */
        void switchToSettings();

        /**
         * 切换到历史页面
         */
        void switchToHistory();

        /**
         * 切换到图表页面
         */
        void switchToChart();

        /**
         * 切换到图谱页面
         */
        void switchTokg();

        /**
         * 切换到学者页面
         */
        void switchToscholar();

        /**
         * 切换到聚类页面
         */
        void switchTocluster();

    }

    interface Presenter extends BasePresenter {


        /**
         * 切换页面
         *
         * @param id 页面 ID
         */
        void switchNavigation(int id);

        /**
         * 获得当前页面
         *
         * @return 页面 ID
         */
        int getCurrentNavigation();
    }
}
