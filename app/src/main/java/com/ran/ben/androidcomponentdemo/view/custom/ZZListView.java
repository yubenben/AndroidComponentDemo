package com.ran.ben.androidcomponentdemo.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.Utils.ScrollUtil;

public class ZZListView extends ListView {

    private float lastMotionY;
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
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (mode == MeasureSpec.UNSPECIFIED) {
            int height = getLayoutParams().height;
            if (height > 0)
                setMeasuredDimension(getMeasuredWidth(), height);
        }
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//        float x = event.getX();
//        float y = event.getY();
//        float dy = y - lastMotionY;
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                lastMotionY = y;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (ScrollUtil.canScroll(this, false, (int) dy, (int) x, (int) y)) {
//                    lastMotionY = y;
//                    return false;
//                }
//                break;
//        }
//        return super.onInterceptTouchEvent(event);
//    }
}
