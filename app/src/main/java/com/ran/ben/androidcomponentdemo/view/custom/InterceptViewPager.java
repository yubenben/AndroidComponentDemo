package com.ran.ben.androidcomponentdemo.view.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by yubenben
 * on 2015/11/17.
 */
public class InterceptViewPager extends ViewPager {

    private Context mContext;

    public InterceptViewPager(Context context) {
        super(context);
        mContext = context;
    }

    public InterceptViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return false;
        //return (getCurrentItem() != 0) && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        return false;
        //return (getCurrentItem() != 0) && super.onTouchEvent(ev);
    }
}
