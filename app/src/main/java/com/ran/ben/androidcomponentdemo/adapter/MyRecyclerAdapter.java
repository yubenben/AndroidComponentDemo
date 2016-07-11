package com.ran.ben.androidcomponentdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.refresh.BaseLoadMoreRecyclerAdapter;

import java.util.LinkedList;
import java.util.List;


public class MyRecyclerAdapter<T> extends RecyclerView.Adapter {
    public static final int TYPE_ITEM = 0;
    private final List<T> mList = new LinkedList<T>();

    public MyRecyclerAdapter() {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        try{
            StringBuffer str = new StringBuffer();
            for  (int i = 0; i < position; i++) {
                str.append("str");
            }
            ((ViewHolder)holder).mTextView.setText((String)getItem(position) + str);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    public List<T> getList() {
        return mList;
    }

    public void appendToList(List<T> list) {
        if (list == null) {
            return;
        }
        mList.addAll(list);
    }

    public void append(T t) {
        if (t == null) {
            return;
        }
        mList.add(t);
    }

    public void appendToTop(T item) {
        if (item == null) {
            return;
        }
        mList.add(0, item);
    }

    public void appendToTopList(List<T> list) {
        if (list == null) {
            return;
        }
        mList.addAll(0, list);
    }


    public void remove(int position) {
        if (position < mList.size() - 1 && position >= 0) {
            mList.remove(position);
        }
    }

    public void clear() {
        mList.clear();
    }

    private int getBasicItemCount() {
        return mList.size();
    }

    @Override
    public int getItemCount() {
        return getBasicItemCount();
    }

    public T getItem(int position) {
        if (position > mList.size() - 1) {
            return null;
        }
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
