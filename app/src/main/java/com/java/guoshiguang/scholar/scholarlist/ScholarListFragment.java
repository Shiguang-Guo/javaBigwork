package com.java.guoshiguang.scholar.scholarlist;

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
import com.java.guoshiguang.data.Scholar;

import java.util.List;

public class ScholarListFragment extends Fragment implements ScholarListContract.View {
    private ScholarListContract.Presenter mPresenter;
    private int mCategory;
    private String mKeyword;

    private int mLastClickPosition = -1;
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private RecyclerView mRecyclerView;
    private ScholarAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private View mTextEmpty;

    public ScholarListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param category 新闻分类 code
     * @return A new instance of fragment ScholarListFragment.
     */
    public static ScholarListFragment newInstance(int category, String keyword) {
        Bundle args = new Bundle();
        ScholarListFragment fragment = new ScholarListFragment();
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
        mPresenter = new ScholarListPresenter(this, mCategory, mKeyword);

        mAdapter = new ScholarAdapter(getContext());
        mAdapter.setOnItemClickListener((View itemView, int position) -> {
            Scholar news = mAdapter.getScholar(position);
            this.mLastClickPosition = position;
            View transitionView = itemView.findViewById(R.id.scholar_list_image_view);

            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                            transitionView, getString(R.string.trans));

            this.mPresenter.openScholarDetailUI(news, options.toBundle());
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TypedValue colorPrimary = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, colorPrimary, true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scholar_list, container, false);

        mTextEmpty = view.findViewById(R.id.scholar_text_empty);
        mTextEmpty.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

        mSwipeRefreshWidget = view.findViewById(R.id.scholar_swipe_refresh_widget);
        mSwipeRefreshWidget.setColorSchemeColors(getResources().getColor(colorPrimary.resourceId));
        mSwipeRefreshWidget.setOnRefreshListener(() -> {
            mSwipeRefreshWidget.setRefreshing(true);
            try {
                mPresenter.refreshScholar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = view.findViewById(R.id.scholar_recycle_view);
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
                        mPresenter.requireMoreScholars();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void setPresenter(ScholarListContract.Presenter presenter) {
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
    public void setScholarList(List<Scholar> list) {
        mAdapter.setData(list);
        mTextEmpty.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void appendScholarList(List<Scholar> list) {
        mAdapter.appendData(list);
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
