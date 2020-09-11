package com.java.guoshiguang.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.java.guoshiguang.R;
import com.java.guoshiguang.chart.ChartFragment;
import com.java.guoshiguang.cluster.ClusterFragment;
import com.java.guoshiguang.history.HistoryFragment;
import com.java.guoshiguang.kg.KgFragment;
import com.java.guoshiguang.news.NewsFragment;
import com.java.guoshiguang.scholar.ScholarFragment;
import com.java.guoshiguang.settings.SettingsFragment;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainContract.View {

    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private MainContract.Presenter mPresenter;
    private Fragment mNews, mSettings, mHistory, mChart, mkg, mscholar, mcluster;
    private MenuItem mSearchItem;
    private SearchView mSearchView;
    private String mKeyword = "";

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MainPresenter(this);


        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mPresenter.doSomeThing();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        mPresenter.switchNavigation(item.getItemId());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public Context context() {
        return this;
    }

    @Override
    public void start(Intent intent, Bundle options) {
        startActivity(intent, options);
    }

    public void switchTo(int id, String title) {
        mToolbar.setTitle(title);
        if (mSearchItem != null) {
            mSearchItem.setVisible(R.id.nav_news == id);
        }
        if (mSearchView != null) {
            mSearchView.clearFocus();
            mSearchView.setQuery(mKeyword, false);
        }
        mNavigationView.setCheckedItem(id);
    }

    @Override
    public void switchToNews() {
        switchTo(R.id.nav_news, "新闻");
        if (mNews == null)
            mNews = NewsFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mNews).commit();
    }


    @Override
    public void switchToSettings() {
        switchTo(R.id.nav_settings, "设置");
        if (mSettings == null)
            mSettings = SettingsFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mSettings).commit();
    }

    @Override
    public void switchToHistory() {
        switchTo(R.id.nav_history, "历史");
        if (mHistory == null)
            mHistory = HistoryFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mHistory).commit();
    }

    @Override
    public void switchToChart() {
        switchTo(R.id.nav_chart, "折线图");
        if (mChart == null)
            mChart = ChartFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mChart).commit();
    }

    @Override
    public void switchTokg() {
        switchTo(R.id.nav_kg, "图谱");
        if (mkg == null)
            mkg = KgFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mkg).commit();
    }

    @Override
    public void switchToscholar() {
        switchTo(R.id.nav_scholar, "学者");
        if (mscholar == null)
            mscholar = ScholarFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mscholar).commit();
    }

    @Override
    public void switchTocluster() {
        switchTo(R.id.nav_cluster, "聚类");
        if (mcluster == null)
            mcluster = ClusterFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mcluster).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        mSearchItem = menu.findItem(R.id.action_search);
        mSearchItem.setVisible(R.id.nav_news == mPresenter.getCurrentNavigation());
        mSearchView = (SearchView) mSearchItem.getActionView();
        mSearchView.setOnCloseListener(() -> {
            if (!mKeyword.isEmpty()) {
                mKeyword = "";
                ((NewsFragment) mNews).setKeyword("");
            }
            return false;
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                mKeyword = query;
                System.out.println(mPresenter.getCurrentNavigation() + "  " + R.id.nav_news);
                if (mPresenter.getCurrentNavigation() == R.id.nav_news && mNews != null)
                    ((NewsFragment) mNews).setKeyword(query);
                mSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

}
