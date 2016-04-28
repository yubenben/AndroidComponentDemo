package com.ran.ben.androidcomponentdemo.view.card;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

/**
 * Created by yubenben
 * 2015/9/18.
 *
 */
public abstract class SlideBaseCardAdapter extends BaseAdapter {

    protected final Context mContext;

    public SlideBaseCardAdapter(Context context) {
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
            CardAdapterView.LayoutParams params  =
                    (CardAdapterView.LayoutParams) innerWrapper.getLayoutParams();
            if  (CardSlidePanel.DEBUG) {
                Log.i("SlideBaseCardAdapter", "getView: params.viewType=" + params.viewType);
                Log.i("SlideBaseCardAdapter", "getView: position + " + position + " itemViewType=" +
                        getItemViewType(position));
            }
            if (params.viewType == getItemViewType(position)) {
                try {
                    convertedCardView = getCardView(position, cardView, parent);
                    if (convertedCardView != cardView) {
                        wrapper.removeView(cardView);
                        wrapper.addView(convertedCardView);
                    }
                } catch (ClassCastException e) {
                    convertedCardView = getCardView(position, null, parent);
                    wrapper.removeView(cardView);
                    wrapper.addView(convertedCardView);
                    wrapper.setLayoutParams(new CardAdapterView.LayoutParams(CardAdapterView.LayoutParams.MATCH_PARENT,
                            CardAdapterView.LayoutParams.MATCH_PARENT,
                            getItemViewType(position)));
                    e.printStackTrace();
                }
            } else {
                convertedCardView = getCardView(position, null, parent);
                wrapper.removeView(cardView);
                wrapper.addView(convertedCardView);
                wrapper.setLayoutParams(new CardAdapterView.LayoutParams(CardAdapterView.LayoutParams.MATCH_PARENT,
                        CardAdapterView.LayoutParams.MATCH_PARENT,
                        getItemViewType(position)));
            }
        }

        return wrapper;
    }

    protected abstract View getCardView(int position, View convertView, ViewGroup parent);

}
