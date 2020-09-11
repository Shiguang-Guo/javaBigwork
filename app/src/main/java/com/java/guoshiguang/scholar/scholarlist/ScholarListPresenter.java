package com.java.guoshiguang.scholar.scholarlist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.java.guoshiguang.data.Manager;
import com.java.guoshiguang.data.Scholar;
import com.java.guoshiguang.scholar.scholardetail.ScholarDetailActivity;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

public class ScholarListPresenter implements ScholarListContract.Presenter {
    private final int PAGE_SIZE = 20;
    public int mPageNo = 1;
    private ScholarListContract.View mView;
    private int mCategory;
    private String mKeyword;
    private boolean mLoading = false;
    private long mLastFetchStart;

    public ScholarListPresenter(ScholarListContract.View view, int category, String keyword) {
        this.mView = view;
        this.mCategory = category;
        this.mKeyword = keyword;

        view.setPresenter((ScholarListContract.Presenter) this);
    }

    @Override
    public boolean isLoading() {
        return mLoading;
    }


    @Override
    public void doSomeThing() throws Exception {
        refreshScholar();
    }

    @Override
    public void requireMoreScholars() throws Exception {
        mPageNo++;
        fetchScholar();
    }

    @Override
    public void refreshScholar() throws Exception {
        mPageNo = 1;
        fetchScholar();
    }

    @Override
    public void openScholarDetailUI(Scholar scholar, Bundle options) {
        Intent intent = new Intent(mView.context(), ScholarDetailActivity.class);
        intent.putExtra(ScholarDetailActivity.NAME, scholar.name);
        intent.putExtra(ScholarDetailActivity.NAMEZH, scholar.name_zh);
        intent.putExtra(ScholarDetailActivity.HOMEPAGE, scholar.profile.homepage);
        intent.putExtra(ScholarDetailActivity.WORK, scholar.profile.work);
        intent.putExtra(ScholarDetailActivity.BIO, scholar.profile.bio);
        intent.putExtra(ScholarDetailActivity.EDU, scholar.profile.edu);
        intent.putExtra(ScholarDetailActivity.AFF, scholar.profile.affiliation);
        intent.putExtra(ScholarDetailActivity.AFFZH, scholar.profile.affiliation_zh);
        intent.putExtra(ScholarDetailActivity.POS, scholar.profile.position);
        intent.putExtra(ScholarDetailActivity.ACTIVITY, String.valueOf(scholar.indices.activity));
        intent.putExtra(ScholarDetailActivity.HINDEX, String.valueOf(scholar.indices.hindex));
        intent.putExtra(ScholarDetailActivity.SOCIABILITY, String.valueOf(scholar.indices.sociability));
        intent.putExtra(ScholarDetailActivity.PUBS, String.valueOf(scholar.indices.pubs));
        intent.putExtra(ScholarDetailActivity.CITATONS, String.valueOf(scholar.indices.citations));
        intent.putExtra(ScholarDetailActivity.PIC, String.valueOf(scholar.avatar));
        mView.start(intent, options);
    }


    private void fetchScholar() throws Exception {
        final long start = System.currentTimeMillis();
        mLoading = true;
        mLastFetchStart = start;
        Single<List<Scholar>> single = null;
        String thiscate = mCategory == 0 ? "Highly Concerned" : "Remembrance";

        single = Manager.I.fetchScholarData(mCategory != 0);

        single.subscribe(new Consumer<List<Scholar>>() {
            @SuppressLint("CheckResult")
            @Override
            public void accept(List<Scholar> simplescholar) throws Exception {

                if (mPageNo == 1) mView.setScholarList(simplescholar);
                else mView.appendScholarList(simplescholar);

//                    if (mPageNo == 1 && simplescholar.size() > 0 && simplescholar.size() < 10)
//                        requireMoreScholars();

            }
        });
    }
}
