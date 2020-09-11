package com.java.guoshiguang.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.java.guoshiguang.R;
import com.java.guoshiguang.data.ImageLoader;
import com.java.guoshiguang.data.SimpleNews;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻列表适配器
 * Created by equation on 9/8/17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;

    private Context mContext;
    private List<SimpleNews> mData = new ArrayList<SimpleNews>();
    private boolean mIsShowFooter = true;
    private OnItemClickListener mOnItemClickListener;

    public HistoryAdapter(Context context) {
        mContext = context;
    }

    public SimpleNews getNews(int position) {
        return mData.get(position);
    }

    public void setData(List<SimpleNews> data) {
        mData = new ArrayList<SimpleNews>(data);
        this.notifyDataSetChanged();
    }

    public void appendData(List<SimpleNews> data) {
        int pos = mData.size();
        mData.addAll(data);
        this.notifyItemRangeChanged(pos, getItemCount());
    }

    public void removeItem(int position) {
        mData.remove(position);
        this.notifyItemRemoved(position);
    }

    public void setRead(int position, boolean has_read) {
        SimpleNews news = getNews(position);
        news.hasRead = has_read;
        mData.set(position, news);
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false);
            return new FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            SimpleNews news = mData.get(position);
            final ItemViewHolder item = (ItemViewHolder) holder;
            item.mTitle.setText(news.title);
            item.mAuthor.setText(news.source);
            item.mDate.setText(news.time);
            ImageLoader.displayImage("", item.mImage);
            item.setBackgroundColor(mContext.getResources().getColor(news.hasRead ? R.color.colorCardRead : R.color.colorCard));
            item.mCurrentPosition = position;
            final long start = System.currentTimeMillis();
//            news.single_picture_url
////                    .observeOn(AndroidSchedulers.mainThread())
////                    .doSomeThing(new Consumer<String>() {
////                        @Override
////                        public void accept(String s) throws Exception {
////                            System.out.println("single_picture_url : " + (System.currentTimeMillis() - start));
////                            if (item.mCurrentPosition == position)
////                                ImageLoader.displayImage(s, item.mImage);
////                            else
////                                ImageLoader.cancelDisplayTask(item.mImage);
////                        }
////                    });
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
        TextView mTitle, mAuthor, mDate;
        int mCurrentPosition = -1;
        ImageView mImage;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = view.findViewById(R.id.text_title);
            mAuthor = view.findViewById(R.id.text_author);
            mDate = view.findViewById(R.id.text_date);
            mImage = view.findViewById(R.id.image_view);
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
