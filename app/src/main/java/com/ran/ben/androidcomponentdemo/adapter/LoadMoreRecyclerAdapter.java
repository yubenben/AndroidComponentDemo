package com.ran.ben.androidcomponentdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.refresh.BaseLoadMoreRecyclerAdapter;


public class LoadMoreRecyclerAdapter extends BaseLoadMoreRecyclerAdapter<String, LoadMoreRecyclerAdapter.ViewHolder> {

    public LoadMoreRecyclerAdapter() {
     }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, int position) {
        try{
            StringBuffer str = new StringBuffer();
            for  (int i = 0; i < position; i++) {
                str.append("str");
            }
            holder.mTextView.setText(getItem(position) + str);
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

}
