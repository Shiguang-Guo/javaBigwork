package com.java.guoshiguang.scholar.scholardetail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.java.guoshiguang.R;
import com.java.guoshiguang.data.ImageLoader;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

public class ScholarDetailActivity extends AppCompatActivity implements ScholarDetailContract.View {

    public static final String NAME = "NAME";
    public static final String NAMEZH = "NAMEZH";
    public static final String PIC = "PIC";
    public static final String HINDEX = "HINDEX";
    public static final String ACTIVITY = "ACTIVITY";
    public static final String SOCIABILITY = "SOCIABILITY";
    public static final String CITATONS = "CITATONS";
    public static final String PUBS = "PUBS";
    public static final String AFF = "AFF";
    public static final String AFFZH = "AFFZH";
    public static final String POS = "POS";
    public static final String HOMEPAGE = "HOMEPAGE";
    public static final String WORK = "WORK";
    public static final String BIO = "BIO";
    public static final String EDU = "EDU";

    private ScholarDetailContract.Presenter mPresenter;
    private boolean mError;

    private TextView mName, mInfo, mIndic, mhomeContent,mworkContent,mbioContent,meduContent;
    private ImageView mImage;
    private ScrollView mScrollView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;


    private void onShare() {

    }

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String mnamezh = getIntent().getStringExtra(NAMEZH);
        String mname = getIntent().getStringExtra(NAME);
        String mhome = getIntent().getStringExtra(HOMEPAGE);
        String mwork = getIntent().getStringExtra(WORK);
        String mbio = getIntent().getStringExtra(BIO);
        String medu = getIntent().getStringExtra(EDU);
        String maff = getIntent().getStringExtra(AFF);
        String maffzh = getIntent().getStringExtra(AFFZH);
        String mpos = getIntent().getStringExtra(POS);
        String mactivity = getIntent().getStringExtra(ACTIVITY);
        String mhindex = getIntent().getStringExtra(HINDEX);
        String msoc = getIntent().getStringExtra(SOCIABILITY);
        String mpubs = getIntent().getStringExtra(PUBS);
        String mcita = getIntent().getStringExtra(CITATONS);
        String mpic = getIntent().getStringExtra(PIC);

        mPresenter = new ScholarDetailPresenter(this);
        mError = false;

        setContentView(R.layout.activity_scholar);
        Toolbar toolbar = findViewById(R.id.scholar_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mScrollView = findViewById(R.id.scholar_scroll_view);
        mScrollView.setOnScrollChangeListener(new ScrollView.OnScrollChangeListener() {
            private boolean isBottomShow = true;

            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY - oldScrollY > 0 && isBottomShow) { // 下移隐藏
                    isBottomShow = false;
                } else if (scrollY - oldScrollY < 0 && !isBottomShow) { // 上移出现
                    isBottomShow = true;
                }
            }
        });

        mCollapsingToolbarLayout = findViewById(R.id.scholar_collapsing_toolbar);

        mhomeContent = findViewById(R.id.scholar_home_content);
        mworkContent=findViewById(R.id.scholar_work_content);
        mbioContent=findViewById(R.id.scholar_bio_content);
        meduContent=findViewById(R.id.scholar_edu_content);
        mImage = findViewById(R.id.scholar_detail_image);

        ImageLoader.displayImage(mpic, mImage);

        mName = findViewById(R.id.scholar_detail_name);
        mIndic = findViewById(R.id.scholar_detail_indic);
        mInfo = findViewById(R.id.scholar_detail_info);

        mName.setText(mnamezh + " " + mname);
        StringBuilder sb = new StringBuilder();
        sb.append("HINDEX:");
        sb.append(mhindex).append(" ");
        sb.append("ACTIVITY:");
        sb.append(mactivity).append(" ");
        sb.append("SOCIABILITY:");
        sb.append(msoc).append(" ");
        sb.append("CITATONS:");
        sb.append(mcita).append(" ");
        sb.append("PUBS:");
        sb.append(mpubs).append(" ");
        mIndic.setText(sb.toString());
        sb.setLength(0);

        sb.append(maff).append(maffzh).append("\n").append(mpos);
        mInfo.setText(sb.toString());
        sb.setLength(0);

        mhomeContent.setText(mhome);
        mworkContent.setText(mwork);
        mbioContent.setText(mbio);
        meduContent.setText(medu);

        try {
            mPresenter.doSomeThing();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
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
    public void setPresenter(ScholarDetailContract.Presenter presenter) {
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

    @Override
    public void onShowToast(String title) {
        //Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
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
        findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
        findViewById(R.id.layout_content).setVisibility(View.INVISIBLE);
        findViewById(R.id.layout_error).setVisibility(View.VISIBLE);
    }

    @Override
    public void setImageVisible(boolean visible) {
        findViewById(R.id.image_layout).setVisibility(visible && !mError ? View.VISIBLE : View.GONE);
    }

}
