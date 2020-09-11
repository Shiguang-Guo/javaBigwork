package com.java.guoshiguang.cluster;

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
import com.java.guoshiguang.cluster.clusternewslist.ClusterListFragment;
import com.java.guoshiguang.data.Config;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ClusterFragment extends Fragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String mKeyword = "";
    private ClusterFragment.MyPagerAdapter mPagerAdapter;
    private List<Config.Category> mCategories = new ArrayList<>();

    public ClusterFragment() {
        // Required empty public constructor
    }


    public static ClusterFragment newInstance() {
        return new ClusterFragment();
    }

    public void setKeyword(String keyword) {
        mKeyword = keyword;
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategories.clear();
        mCategories.add(new Config.Category("【\u68c0\u6d4b、\u6297\u4f53、\u7ed3\u679c】", 0));
        mCategories.add(new Config.Category("【\u75c5\u6bd2、\u65b0\u51a0、\u611f\u67d3】", 1));
        mCategories.add(new Config.Category("【cov、sar、\u86cb\u767d】", 2));
        mCategories.add(new Config.Category("【\u80ba\u708e、" + "\u9662\u58eb、\u60a3\u8005】", 3));
        mCategories.add(new Config.Category("【\u51a0\u72b6\u75c5\u6bd2、\u51a0\u72b6、\u65b0\u578b】", 4));


        mPagerAdapter = new ClusterFragment.MyPagerAdapter(getChildFragmentManager(), mCategories);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cluster_page, container, false);
        mViewPager = view.findViewById(R.id.cluster_page_view_pager);
        mViewPager.setOffscreenPageLimit(8);

        mTabLayout = view.findViewById(R.id.cluster_page_tab_layout);
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
            return ClusterListFragment.newInstance(mCategories.get(position).idx, mKeyword);
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ClusterListFragment f = (ClusterListFragment) super.instantiateItem(container, position);
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
