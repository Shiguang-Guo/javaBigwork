package com.java.guoshiguang.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;


public class Config {
    final public static String[] CATEGORYS = {
            "all",
            "event",
            "points",
            "news",
            "paper",
    };

    final public static String[] SCHOLARS = {
            "Highly Concerned",
            "Remembrance"
    };

    public static Config config = null;
    private static List<Integer> available_categories;
    private Scheduler save_thread = Schedulers.newThread();

    public Config() {
        available_categories = new ArrayList<>();
        for (int i = 0; i < CATEGORYS.length; i++)
            available_categories.add(i);

    }

    Config(Context context) {
        //path = context.getFilesDir().getPath() + "/config.json";
        //loadConfig();
    }

    public static void Createcfg() {
        config = new Config();
    }

    /**
     * 所有分类
     *
     * @return
     */
    public List<Category> allCategories() {
        List<Category> list = new ArrayList<>();
        for (int x = 1; x < CATEGORYS.length; x++) {
            list.add(new Category(CATEGORYS[x], x));
        }
        return list;
    }

    public List<Category> availableCategories() {
        List<Category> list = new ArrayList<>();
        for (int x = 0; x < available_categories.size(); x++) {
            list.add(new Category(CATEGORYS[available_categories.get(x)], available_categories.get(x)));
        }
        return list;
    }

    /**
     * 未选的分类
     *
     * @return
     */
    public List<Category> unavailableCategories() {
        List<Category> list = new ArrayList<>();
        for (Integer x = 1; x < CATEGORYS.length; x++)
            if (!available_categories.contains(x))
                list.add(new Category(CATEGORYS[x], x));
        return list;
    }

    /**
     * 添加分类
     *
     * @param category 分类
     * @return 是否成功
     */
    public boolean addCategory(Category category) {
        if (!available_categories.contains(category.idx)) {
            available_categories.add(category.idx);
            // saveConfig();
            return true;
        }
        return false;
    }

    /**
     * 删除分类
     *
     * @param category 分类
     * @return 是否成功
     */
    public boolean removeCategory(Category category) {
        if (available_categories.contains(category.idx)) {
            available_categories.remove((Integer) category.idx);
            //saveConfig();
            return true;
        }
        return false;
    }

    /**
     * 切换分类的状态，已选变成未选，未选变成已选
     *
     * @param idx
     */
    public void switchAvailable(Integer idx) {
        if (available_categories.contains(idx))
            available_categories.remove(idx);
        else available_categories.add(idx);
        //saveConfig();
    }

    public static class Category {
        public String title;
        public int idx;

        public Category(String title, int idx) {
            this.title = title;
            this.idx = idx;
        }
    }

}
