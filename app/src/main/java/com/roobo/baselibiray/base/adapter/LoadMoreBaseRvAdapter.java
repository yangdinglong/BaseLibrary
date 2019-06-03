package com.roobo.baselibiray.base.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by HP on 2019/3/8.
 */

public abstract class LoadMoreBaseRvAdapter extends BaseRvAdapter {

    // 普通布局
    protected final int TYPE_ITEM = 1;
    // 脚布局
    protected final int TYPE_FOOTER = 2;
    // 当前加载状态，默认为加载完成
    public int loadState = 1;
    // 正在加载
    public static final int LOADING = 1;
    // 加载完成
    public static final int LOADING_COMPLETE = 2;

    // 加载失败
    public static final int LOADING_FAIL = 3;
    // 加载到底
    public static final int LOADING_END = 4;
    // 隐藏
    public static final int LOADING_GONE = 5;

    public LoadMoreBaseRvAdapter(Context context) {
        super(context);
    }

    public LoadMoreBaseRvAdapter(Context context, OnItemClickListener onItemClickListener) {
        super(context, onItemClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为FooterView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

    public class FooterViewHolder<T extends View> extends RecyclerView.ViewHolder {
        SparseArray<T> sparseArray = new SparseArray<>();
        View itemView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public T getView(int viewId) {
            T view = (T) sparseArray.get(viewId);
            if (sparseArray.get(viewId) == null) {
                view = this.itemView.findViewById(viewId);
                sparseArray.put(viewId, view);
            }
            return view;
        }
    }


}
