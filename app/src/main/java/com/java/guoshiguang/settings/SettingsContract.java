package com.java.guoshiguang.settings;

import com.java.guoshiguang.BasePresenter;
import com.java.guoshiguang.BaseView;
import com.java.guoshiguang.data.Config;

import java.util.List;

public interface SettingsContract {

    interface View extends BaseView<Presenter> {

        void showNightMode(boolean is_night_mode);

        void showTextMode(boolean is_text_mode);

        /**
         * 添加首页标签
         *
         * @param tag 分类标签
         */
        void onAddTag(Config.Category tag);

        /**
         * 删除首页标签
         *
         * @param tag      分类标签
         * @param position 位置
         */
        void onRemoveTag(Config.Category tag, int position);


        /**
         * 弹窗
         *
         * @param title 标题
         */
        void onShowToast(String title);

        /**
         * 弹窗
         *
         * @param title   标题
         * @param message 消息
         */
        void onShowAlertDialog(String title, String message);
    }

    interface Presenter extends BasePresenter {

        /**
         * 获取首页标签列表
         *
         * @return 分类列表
         */
        List<Config.Category> getTags();

        /**
         * 添加首页标签
         *
         * @param tag 分类标签
         */
        void addTag(Config.Category tag);

        /**
         * 删除首页标签
         *
         * @param tag      分类标签
         * @param position 位置
         */
        void removeTag(Config.Category tag, int position);
    }
}
