package com.java.guoshiguang.news;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.java.guoshiguang.R;
import com.java.guoshiguang.data.Config;
import com.java.guoshiguang.news.newslist.NewsListFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static com.java.guoshiguang.data.Config.config;

/**
 * 新闻主页面
 * Created by equation on 9/8/17.
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class NewsFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String mKeyword = "";
    private MyPagerAdapter mPagerAdapter;
    private List<Config.Category> mCategories = new ArrayList<Config.Category>();

    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsListFragment.
     */
    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    public void setKeyword(String keyword) {
        mKeyword = keyword;
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCategories = config.availableCategories();
        mPagerAdapter = new MyPagerAdapter(getChildFragmentManager(), mCategories);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_page, container, false);
        mViewPager = view.findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(3);

        mTabLayout = view.findViewById(R.id.tab_layout);
        for (int i = 0; i < mCategories.size(); i++)
            mTabLayout.addTab(mTabLayout.newTab());

        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        private List<Config.Category> mCategories;

        public MyPagerAdapter(FragmentManager fm, List<Config.Category> list) {
            super(fm);
            mCategories = list;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mCategories.get(position).title;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return NewsListFragment.newInstance(mCategories.get(position).idx, mKeyword);
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            NewsListFragment f = (NewsListFragment) super.instantiateItem(container, position);
            f.setKeyword(mKeyword);
            return f;
        }

        @Override
        public int getCount() {
            return mCategories.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
