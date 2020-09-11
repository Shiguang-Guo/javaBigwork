package com.java.guoshiguang.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.java.guoshiguang.R;
import com.java.guoshiguang.data.Config;
import com.java.guoshiguang.data.Manager;

import java.util.List;

import static com.java.guoshiguang.data.Config.CATEGORYS;
import static com.java.guoshiguang.data.Config.config;

/**
 * 设置页面
 */
public class SettingsFragment extends Fragment implements SettingsContract.View {

    private SettingsContract.Presenter mPresenter;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch mNightSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch mTextSwitch;
    private Button mAddTagButton,mClearbutton;

    private RecyclerView mTagsView;
    private ChipsAdapter mTagsAdapter;

//    public SettingsFragment() {
//    }

    /**
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new SettingsPresenter(this);
        mTagsAdapter = new ChipsAdapter<Config.Category>(mPresenter.getTags(), 0) {
            @Override
            String getChipsTitle(final Config.Category chip) {
                return chip.title;
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mPresenter.doSomeThing();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mAddTagButton = view.findViewById(R.id.button_add_tag);
        mAddTagButton.setOnClickListener((View v) -> {
            List<Config.Category> list = config.unavailableCategories();
            if (list.isEmpty()) return;
            String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); i++)
                array[i] = list.get(i).title;

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("请选择要添加的首页标签").setNegativeButton("取消", null);
            builder.setItems(array, (DialogInterface dialog, int which) -> mPresenter.addTag(list.get(which)));
            builder.create().show();
        });

        mAddTagButton.setEnabled(mTagsAdapter.getItemCount() < CATEGORYS.length);
        mTagsAdapter.setOnTagsCountChangeListener((int count) -> mAddTagButton.setEnabled(count < CATEGORYS.length));
        mTagsAdapter.setOnRemoveChipListener((View v, int position) -> {
            Config.Category tag = (Config.Category) mTagsAdapter.getChip(position);
            mPresenter.removeTag(tag, position);
        });

        ChipsLayoutManager tagsLayoutManager = ChipsLayoutManager.newBuilder(getContext())
                .setRowStrategy(ChipsLayoutManager.STRATEGY_CENTER)
                .withLastRow(true)
                .build();
        mTagsView = view.findViewById(R.id.tags_view);
        mTagsView.setLayoutManager(tagsLayoutManager);
        mTagsView.setItemAnimator(new DefaultItemAnimator());
        mTagsView.setAdapter(mTagsAdapter);

        ChipsLayoutManager blacklistLayoutManager = ChipsLayoutManager.newBuilder(getContext())
                .setRowStrategy(ChipsLayoutManager.STRATEGY_CENTER)
                .withLastRow(true)
                .build();


        mClearbutton=view.findViewById(R.id.button_clear);
        mClearbutton.setOnClickListener((View v)->{
            Manager.I.clean();
            Toast.makeText(context(),"成功清楚所有缓存",Toast.LENGTH_SHORT).show();
        });
        return view;
    }

    @Override
    public void setPresenter(SettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public Context context() {
        return getContext();
    }

    @Override
    public void start(Intent intent, Bundle options) {
        startActivity(intent, options);
    }

    @Override
    public void onAddTag(Config.Category tag) {
        mTagsAdapter.addChip(tag);
    }

    @Override
    public void onRemoveTag(Config.Category tag, int position) {
        mTagsAdapter.removeChip(position);
    }

    @Override
    public void onShowToast(String title) {
        Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShowAlertDialog(String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", null).create();
        dialog.show();
    }

    @Override
    public void showNightMode(boolean is_night_mode) {
        mNightSwitch.setChecked(is_night_mode);
    }

    @Override
    public void showTextMode(boolean is_text_mode) {
        mTextSwitch.setChecked(is_text_mode);
    }
}
