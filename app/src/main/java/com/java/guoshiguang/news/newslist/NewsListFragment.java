package com.java.guoshiguang.news.newslist;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.java.guoshiguang.R;
import com.java.guoshiguang.data.SimpleNews;

import java.util.List;

/**
 * 新闻列表，带分类
 * Created by equation on 9/7/17.
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class NewsListFragment extends Fragment implements NewsListContract.View {

    private NewsListContract.Presenter mPresenter;
    private int mCategory;
    private String mKeyword;

    private int mLastClickPosition = -1;
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private View mTextEmpty;

    public NewsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param category 新闻分类 code
     * @return A new instance of fragment NewsListFragment.
     */
    public static NewsListFragment newInstance(int category, String keyword) {
        Bundle args = new Bundle();
        NewsListFragment fragment = new NewsListFragment();
        args.putInt("category", category);
        args.putString("keyword", keyword);
        fragment.setArguments(args);
        return fragment;
    }

    public String getKeyword() {
        return mKeyword;
    }

    public void setKeyword(String keyword) {
        mKeyword = keyword;
        Bundle args = this.getArguments();
        args.putString("keyword", keyword);
        this.setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLastClickPosition = -1;
        mCategory = getArguments().getInt("category");
        mKeyword = getArguments().getString("keyword");
        mPresenter = new NewsListPresenter(this, mCategory, mKeyword);

        mAdapter = new NewsAdapter(getContext());
        mAdapter.setOnItemClickListener((View itemView, int position) -> {
            SimpleNews news = mAdapter.getNews(position);
            if (!news.hasRead) {
                this.mLastClickPosition = position;
            } else
                this.mLastClickPosition = -1;
            View transitionView = itemView.findViewById(R.id.image_view);

            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                            transitionView, getString(R.string.transition_news_img));

            this.mPresenter.openNewsDetailUI(news, options.toBundle());
        });

        try {
            mPresenter.doSomeThing();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // FIXME notify other view page
//        if (mLastClickPosition >= 0) // 在离线情况下，点击不代表已读
//            mAdapter.notifyItemChanged(mLastClickPosition);
        if (mLastClickPosition >= 0 && mPresenter != null)
            mPresenter.fetchNewsRead(mLastClickPosition, mAdapter.getNews(mLastClickPosition));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TypedValue colorPrimary = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, colorPrimary, true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        mTextEmpty = view.findViewById(R.id.scholar_text_empty);
        mTextEmpty.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

        mSwipeRefreshWidget = view.findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setColorSchemeColors(getResources().getColor(colorPrimary.resourceId));
        mSwipeRefreshWidget.setOnRefreshListener(() -> {
            mSwipeRefreshWidget.setRefreshing(true);
            try {
                mPresenter.refreshNews();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = view.findViewById(R.id.recycle_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisibleItem;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem == mAdapter.getItemCount() - 1
                        && mAdapter.isShowFooter() && !mPresenter.isLoading()) {
                    try {
                        mPresenter.requireMoreNews();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void setPresenter(NewsListContract.Presenter presenter) {
        this.mPresenter = presenter;
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
    public void setNewsList(List<SimpleNews> list) {
        mAdapter.setData(list);
        mTextEmpty.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void appendNewsList(List<SimpleNews> list) {
        mAdapter.appendData(list);
    }

    @Override
    public void resetItemRead(int pos, boolean has_read) {
        if (mAdapter.getNews(pos).hasRead != has_read) {
            mAdapter.setRead(pos, has_read);
            mAdapter.notifyItemChanged(pos);
        }
    }

    @Override
    public void onSuccess(boolean loadCompleted) {
        mAdapter.setFooterVisible(!loadCompleted);
        mSwipeRefreshWidget.setRefreshing(false);
    }

    @Override
    public void onError() {
        mSwipeRefreshWidget.setRefreshing(false);
        Toast.makeText(getContext(), "获取新闻失败，请稍后再试", Toast.LENGTH_SHORT).show();
    }
}
