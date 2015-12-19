package com.ran.ben.androidcomponentdemo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ran.ben.androidcomponentdemo.R;

import java.util.ArrayList;

/**
 * @date: 2015年12月17日 上午13:50:36
 */
public class StringAdapter extends BaseAdapter {

    private Context mContext;

    public ArrayList<String> getArrayList() {
        return mArrayList;
    }

    public void setArrayList(ArrayList<String> mArrayList) {
        this.mArrayList = mArrayList;
    }

    private ArrayList<String> mArrayList;

    public StringAdapter(Context context, ArrayList<String> arrayList) {
        super();
        mContext = context;
        mArrayList = arrayList;
    }

    public void addAll(ArrayList<String> arrayList) {
        mArrayList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public void clear() {
        mArrayList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return mArrayList.size();
    }

    @Override
    public Object getItem(int pos) {

        return mArrayList.get(pos);
    }

    @Override
    public long getItemId(int pos) {

        return pos;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        final String item = mArrayList.get(pos);
        if (item == null) {
            throw new NullPointerException("no item entity");
        }

        final Holder holder;

        if (convertView == null) {
            Log.d("StringAdapter", "new convertView  " + pos);
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.strings_item_layout, null);

            holder = new Holder();
            holder.mName = (TextView) convertView.findViewById(R.id.name);

            convertView.setTag(holder);
        } else {
            Log.d("StringAdapter", "reuse convertView  " + pos);
            holder = (Holder) convertView.getTag();
        }

        holder.mName.setText(item + " " + String.valueOf(pos));
        return convertView;
    }

    private class Holder {
        TextView mName;
    }
}




