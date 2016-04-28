package com.ran.ben.androidcomponentdemo.view.card;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.ran.ben.androidcomponentdemo.R;

/**
 * Created by yubenben
 * Date: 16-1-26.
 */
public class CardAdapterView extends AdapterView<ListAdapter> {

    private static final String TAG = "CardAdapterView";
    private ListAdapter mListAdapter;

    private final DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if  (mCardSlidePanel != null) {
                mCardSlidePanel.refreshViewStack();
            }
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            removeAllViewsInLayout();
            if  (mCardSlidePanel != null) {
                mCardSlidePanel.clearViewStack();
            }
        }
    };

    public static class LayoutParams extends ViewGroup.LayoutParams {

        public int viewType;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(int w, int h, int viewType) {
            super(w, h);
            this.viewType = viewType;
        }
    }

    public CardAdapterView(Context context) {
        super(context);
    }

    public CardAdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardAdapterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int requestedWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int requestedHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        int childWidthMeasureSpec, childHeightMeasureSpec;
        childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(requestedWidth, MeasureSpec.AT_MOST);
        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(requestedHeight, MeasureSpec.AT_MOST);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            assert child != null;
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }


    private final Rect boundsRect = new Rect();
    private final Rect childRect = new Rect();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        int topMargin  = 3;
        for (int i = 0; i < getChildCount(); i++) {
            boundsRect.set(0, 0, getWidth(), getHeight());

            View view = getChildAt(i);
            int w, h;
            w = view.getMeasuredWidth();
            h = view.getMeasuredHeight();

            Gravity.apply(Gravity.CENTER, w, h, boundsRect, 0, topMargin, childRect);
            view.layout(childRect.left, childRect.top,
                    childRect.right, childRect.bottom);
//            Log.d(TAG, "onLayout: boundsRect: left=" + boundsRect.left
//                    + ", top=" + boundsRect.top + ", right=" + boundsRect.right
//                    + ", bottom=" + boundsRect.bottom);
//            Log.d(TAG, "onLayout: childRect: left=" + childRect.left
//                    + ", top=" + childRect.top + ", right=" + childRect.right
//                    + ", bottom=" + childRect.bottom);

        }
    }

    @Override
    public ListAdapter getAdapter() {
        return mListAdapter;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mListAdapter != null) {
            mListAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        mListAdapter = adapter;
        adapter.registerDataSetObserver(mDataSetObserver);

    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setSelection(int position) {

    }

    private CardSlidePanel mCardSlidePanel;
    public void setParentView(CardSlidePanel panelView) {
        mCardSlidePanel = panelView;
    }

    public void addViewInLayout(View view, int position) {
        addViewInLayout(view, 0, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                mListAdapter.getItemViewType(position)), false);
    }
}
