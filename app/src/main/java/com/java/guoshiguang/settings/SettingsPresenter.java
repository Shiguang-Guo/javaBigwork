package com.java.guoshiguang.settings;

import com.java.guoshiguang.data.Config;

import java.util.List;

import static com.java.guoshiguang.data.Config.config;

public class SettingsPresenter implements SettingsContract.Presenter {

    private SettingsContract.View mView;

    SettingsPresenter(SettingsContract.View view) {
        mView = view;
        view.setPresenter(this);
    }

    @Override
    public void doSomeThing() {
    }

    @Override
    public List<Config.Category> getTags() {
        return config.availableCategories();
    }

    @Override
    public void addTag(Config.Category tag) {
        if (config.addCategory(tag))
            mView.onAddTag(tag);
    }

    @Override
    public void removeTag(Config.Category tag, int position) {
        if (config.removeCategory(tag))
            mView.onRemoveTag(tag, position);
    }

}
