package com.java.guoshiguang.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.java.guoshiguang.R;
import com.pchmn.materialchips.ChipView;

import java.util.ArrayList;
import java.util.List;


public abstract class ChipsAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<T> mChips;
    private OnRemoveChipListener mOnRemoveChipListener;
    private OnChipsCountChangeListener mOnChipsCountChangeListener;
    private int mPinnedChipIndex = -1;

    public ChipsAdapter(List<T> list) {
        mChips = new ArrayList<T>(list);
    }

    public ChipsAdapter(List<T> list, int pinnedChipIndex) {
        mChips = new ArrayList<T>(list);
        mPinnedChipIndex = pinnedChipIndex;
    }

    public T getChip(int position) {
        return mChips.get(position);
    }

    public void addChip(T chip) {
        mChips.add(chip);
        notifyItemInserted(mChips.size() - 1);
        if (mOnChipsCountChangeListener != null)
            mOnChipsCountChangeListener.onChipsCountChange(getItemCount());
    }

    public void removeChip(int position) {
        mChips.remove(position);
        notifyItemRemoved(position);
        if (mOnChipsCountChangeListener != null)
            mOnChipsCountChangeListener.onChipsCountChange(getItemCount());
    }

    public void setOnRemoveChipListener(OnRemoveChipListener listener) {
        mOnRemoveChipListener = listener;
    }

    public void setOnTagsCountChangeListener(OnChipsCountChangeListener listener) {
        mOnChipsCountChangeListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        return new ChipsAdapter.TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final TagViewHolder tag = (TagViewHolder) holder;
        tag.mTag.setLabel(getChipsTitle(mChips.get(position)));
        tag.mTag.setDeletable(position != mPinnedChipIndex);
    }

    @Override
    public int getItemCount() {
        return mChips.size();
    }

    abstract String getChipsTitle(final T chip);

    interface OnRemoveChipListener {
        void onRemoveChip(View view, int position);
    }

    interface OnChipsCountChangeListener {
        void onChipsCountChange(int count);
    }

    class TagViewHolder extends RecyclerView.ViewHolder {
        ChipView mTag;

        public TagViewHolder(View view) {
            super(view);
            mTag = view.findViewById(R.id.tag);
            mTag.setOnDeleteClicked((View v) -> {
                if (mOnRemoveChipListener != null)
                    mOnRemoveChipListener.onRemoveChip(view, this.getLayoutPosition());
            });
        }
    }

}
