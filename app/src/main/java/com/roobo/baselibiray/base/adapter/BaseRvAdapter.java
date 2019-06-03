package com.roobo.baselibiray.base.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by HP on 2019/3/8.
 */

public abstract class BaseRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected static String TAG;

    protected Context mContext;

    protected OnItemClickListener mOnItemClickListener;

    public BaseRvAdapter(Context context) {
        TAG = this.getClass().getSimpleName();
        this.mContext = context;
    }

    public BaseRvAdapter(Context context, OnItemClickListener onItemClickListener) {
        TAG = this.getClass().getSimpleName();
        this.mContext = context;
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public class MyViewHolder<T extends View> extends RecyclerView.ViewHolder {
        SparseArray<T> sparseArray = new SparseArray<>();
        View itemView;

        public MyViewHolder(View itemView) {
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

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
