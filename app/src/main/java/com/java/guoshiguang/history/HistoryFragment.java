package com.java.guoshiguang.history;

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

public class HistoryFragment extends Fragment implements HistoryContract.View {
    private HistoryContract.Presenter mPresenter;

    private int mLastClickPosition = -1;
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private RecyclerView mRecyclerView;
    private HistoryAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private View mTextEmpty;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsListFragment.
     */
    public static HistoryFragment newInstance() {
        Bundle args = new Bundle();
        HistoryFragment fragment = new HistoryFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLastClickPosition = -1;
        mPresenter = new HistoryPresenter(this);

        mAdapter = new HistoryAdapter(getContext());
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        mTextEmpty = view.findViewById(R.id.his_text_empty);
        mTextEmpty.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

        mSwipeRefreshWidget = view.findViewById(R.id.his_swipe_widget);
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
        mRecyclerView = view.findViewById(R.id.his_recycle_view);
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
    public void setPresenter(HistoryContract.Presenter presenter) {
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
