package com.java.guoshiguang.news.newsdetail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.java.guoshiguang.R;
import com.java.guoshiguang.data.DetailNews;
import com.java.guoshiguang.data.ImageLoader;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

public class NewsDetailActivity extends AppCompatActivity implements NewsDetailContract.View {

    public static final String NEWS_ID = "NEWS_ID";
    public static final String NEWS_TITLE = "NEWS_TITLE";
    public static final String NEWS_PICTURE_URL = "NEWS_PICTURE_URL";
    public static final String NEWS_SOURCE = "NEWS_SOURCE";
    public static final String NEWS_TIME = "NEWS_TIME";

    private NewsDetailContract.Presenter mPresenter;
    private DetailNews mNews;
    private boolean mError;

    private TextView mDetail, mContent;
    private ImageView mImage;
    private NestedScrollView mScrollView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private View mBottomView, mShareBtn;


    private void onShare() {
        if (mNews != null) {
            mPresenter.shareNews(this, mNews);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String news_ID = getIntent().getStringExtra(NEWS_ID);
        String news_Title = getIntent().getStringExtra(NEWS_TITLE);
        String news_picture_url = getIntent().getStringExtra(NEWS_PICTURE_URL);
        String news_source = getIntent().getStringExtra(NEWS_SOURCE);
        String news_time = getIntent().getStringExtra(NEWS_TIME);


        mPresenter = new NewsDetailPresenter(this, news_ID);
        mError = false;

        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mBottomView = findViewById(R.id.bottom_view);
        mScrollView = findViewById(R.id.scroll_view);
        mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            private boolean isBottomShow = true;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY - oldScrollY > 0 && isBottomShow) { // 下移隐藏
                    isBottomShow = false;
                    mBottomView.animate().translationY(mBottomView.getHeight());
                } else if (scrollY - oldScrollY < 0 && !isBottomShow) { // 上移出现
                    isBottomShow = true;
                    mBottomView.animate().translationY(0);
                }
            }
        });

        mShareBtn = findViewById(R.id.bottom_share);

        mShareBtn.setOnClickListener((View view) -> onShare());

        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle(news_Title);

        mDetail = findViewById(R.id.text_detail);
        mContent = findViewById(R.id.text_content);
        mImage = findViewById(R.id.image_view);

        if (news_picture_url != null) {
            ImageLoader.displayImage(news_picture_url, mImage);
        }

        findViewById(R.id.button_reload).setOnClickListener((View view) -> {
            try {
                mPresenter.doSomeThing();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        try {
            mPresenter.doSomeThing();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        findViewById(R.id.progress_bar).setVisibility(mNews != null || mError ? View.INVISIBLE : View.VISIBLE);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_news, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_share:
                onShare();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void setPresenter(NewsDetailContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void start(Intent intent, Bundle options) {
        startActivity(intent, options);
    }

    @Override
    public Context context() {
        return this;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setNewsDetail(DetailNews news) {
        mNews = news;
        mError = false;
        mDetail.setText(news.source + "　" + news.time);

        if (!news.content.trim().isEmpty()) {
            String content = TextUtils.join("\n\n　　", news.content.trim().split("　　"));
            mContent.setText(content);
        } else {
            mContent.setText(mNews.title);
        }
        long links_start = System.currentTimeMillis();

        mShareBtn.setClickable(true);
        findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
        findViewById(R.id.layout_error).setVisibility(View.INVISIBLE);
        findViewById(R.id.layout_content).setVisibility(View.VISIBLE);
    }

    @Override
    public void onShowToast(String title) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStartLoading() {
        findViewById(R.id.layout_error).setVisibility(View.INVISIBLE);
        findViewById(R.id.layout_content).setVisibility(View.INVISIBLE);
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
    }

    @Override
    public void onError() {
        mError = true;
        mShareBtn.setClickable(false);
        findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
        findViewById(R.id.layout_content).setVisibility(View.INVISIBLE);
        findViewById(R.id.layout_error).setVisibility(View.VISIBLE);
    }

    @Override
    public void setImageVisible(boolean visible) {
        findViewById(R.id.image_layout).setVisibility(visible && !mError ? View.VISIBLE : View.GONE);
    }
}
