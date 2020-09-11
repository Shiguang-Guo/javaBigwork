package com.java.guoshiguang.scholar.scholarlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.java.guoshiguang.R;
import com.java.guoshiguang.data.ImageLoader;
import com.java.guoshiguang.data.Scholar;

import java.util.ArrayList;
import java.util.List;

public class ScholarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;

    private Context mContext;
    private List<Scholar> mData = new ArrayList<Scholar>();
    private boolean mIsShowFooter = true;
    private ScholarAdapter.OnItemClickListener mOnItemClickListener;

    public ScholarAdapter(Context context) {
        mContext = context;
    }

    public Scholar getScholar(int position) {
        return mData.get(position);
    }

    public void setData(List<Scholar> data) {
        mData = new ArrayList<Scholar>(data);
        this.notifyDataSetChanged();
    }

    public void appendData(List<Scholar> data) {
        int pos = mData.size();
        mData.addAll(data);
        this.notifyItemRangeChanged(pos, getItemCount());
    }

    public void removeItem(int position) {
        mData.remove(position);
        this.notifyItemRemoved(position);
    }


    public boolean isShowFooter() {
        return mIsShowFooter;
    }

    public void setFooterVisible(boolean visible) {
        if (mIsShowFooter != visible) {
            mIsShowFooter = visible;
            if (mIsShowFooter)
                this.notifyItemInserted(mData.size());
            else
                this.notifyItemRemoved(mData.size());
        }
    }

    public void setOnItemClickListener(ScholarAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sch, parent, false);
            return new ScholarAdapter.ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false);
            return new ScholarAdapter.FooterViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ScholarAdapter.ItemViewHolder) {
            Scholar s = mData.get(position);
            final ScholarAdapter.ItemViewHolder item = (ScholarAdapter.ItemViewHolder) holder;
            item.mName.setText(s.name_zh + s.name);
            item.mInfo.setText(s.profile.affiliation_zh+"\n"+s.profile.affiliation);
            ImageLoader.displayImage(s.avatar, item.mImage);
            item.mCurrentPosition = position;

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mData.size() && mIsShowFooter)
            return TYPE_FOOTER;
        else
            return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mData.size() + (mIsShowFooter ? 1 : 0);
    }

    /**
     * 新闻点击 Listener
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 新闻单元格
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;
        TextView mName, mInfo;
        int mCurrentPosition = -1;
        ImageView mImage;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mName = view.findViewById(R.id.scholar_list_name);
            mInfo = view.findViewById(R.id.scholar_list_info);
            mImage = view.findViewById(R.id.scholar_list_image_view);
            view.setOnClickListener(this);
        }

        public void setBackgroundColor(int color) {
            mView.setBackgroundColor(color);
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, this.getLayoutPosition());
            }
        }
    }

    /**
     * 列表底部
     */
    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }
    }
}
