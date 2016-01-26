package com.ran.ben.androidcomponentdemo.view.card;

import android.widget.BaseAdapter;


public abstract class BaseCardStackAdapter extends BaseAdapter {
    protected CardSlidePanel.CardSwitchListener mOnCardSwitchListener;

    final public void  registerCardSwitchListener(CardSlidePanel.CardSwitchListener listener) {
        this.mOnCardSwitchListener = listener;
    }
}
