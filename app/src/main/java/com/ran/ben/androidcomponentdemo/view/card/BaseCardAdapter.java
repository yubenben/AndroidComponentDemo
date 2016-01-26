package com.ran.ben.androidcomponentdemo.view.card;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by yubenben
 * 2015/9/18.
 *
 */
public abstract class BaseCardAdapter  extends BaseCardStackAdapter {

    protected final Context mContext;

    public  BaseCardAdapter(Context context) {
        this.mContext  =  context;
    }

    public Context getContext() {
        return this.mContext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FrameLayout wrapper = (FrameLayout) convertView;
        FrameLayout innerWrapper;
        View cardView;
        View convertedCardView;
        if (wrapper == null) {
            wrapper = new FrameLayout(mContext);
            innerWrapper = wrapper;
            cardView = getCardView(position, null, parent);
            innerWrapper.addView(cardView);
        } else {
            innerWrapper = wrapper;
            cardView = innerWrapper.getChildAt(0);
            convertedCardView = getCardView(position, cardView, parent);
            if (convertedCardView != cardView) {
                wrapper.removeView(cardView);
                wrapper.addView(convertedCardView);
            }
        }

        return wrapper;
    }

    protected abstract View getCardView(int position, View convertView, ViewGroup parent);

}
