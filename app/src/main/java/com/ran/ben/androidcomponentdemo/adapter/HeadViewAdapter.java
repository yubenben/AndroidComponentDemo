package com.ran.ben.androidcomponentdemo.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.utils.DensityUtil;

import java.util.ArrayList;

public class HeadViewAdapter extends PagerAdapter {
    /**
     * 数据源
     */
    private ArrayList<String> mLists;
    private LayoutInflater mInflater;
    private int mScreenWidth = 0;
    private IOnViewPagerItemClickListener mListener;

    public HeadViewAdapter(Context context, IOnViewPagerItemClickListener listener) {
        mInflater = LayoutInflater.from(context);
        mScreenWidth = DensityUtil.gettDisplayWidth(context);
        mListener = listener;
    }

    public HeadViewAdapter(Context context, IOnViewPagerItemClickListener listener, ArrayList<String> lists) {
        mLists = lists;
        mInflater = LayoutInflater.from(context);
        mScreenWidth = DensityUtil.gettDisplayWidth(context);
        mListener = listener;
    }

    public void setmLists(ArrayList<String> mLists) {
        this.mLists = mLists;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View topicLayout = mInflater.inflate(R.layout.head_item, view, false);
        SimpleDraweeView image = (SimpleDraweeView) topicLayout.findViewById(R.id.item_iv);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onViewPagerItemClick(position, mLists.get(position));
                }
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mScreenWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        image.setLayoutParams(params);
        String url = mLists.get(position);
        if (url != null) {
            Uri uri = Uri.parse(url);
            //image.setImageURI(uri);
            image.setImageURI(Uri.parse("res://com.ran.ben.androidcomponentdemo/" +
                    R.drawable.business_banner));
        }
        view.addView(topicLayout, 0);
        return topicLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    //列表项点击事件
    public interface IOnViewPagerItemClickListener {
        void onViewPagerItemClick(int position, Object data);
    }
}
