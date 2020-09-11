package com.java.guoshiguang;

import android.app.Application;

import com.java.guoshiguang.data.Config;
import com.java.guoshiguang.data.ImageLoader;
import com.java.guoshiguang.data.Manager;


public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoader.init(this);

        // 创建数据管理
        Manager.CreateI(this);
        Config.Createcfg();
    }
}
