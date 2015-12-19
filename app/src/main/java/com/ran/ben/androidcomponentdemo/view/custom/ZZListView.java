package com.ran.ben.androidcomponentdemo.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ZZListView extends ListView {

    public ZZListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ZZListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZZListView(Context context) {
        super(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
