package com.ran.ben.androidcomponentdemo.view.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 卡片滑动面板，主要逻辑实现类
 */
public class CardSlidePanel extends RelativeLayout {
    private static final String TAG = "CardSlidePanel";
    private static final boolean DEBUG = true;
    private static final boolean ROTATION_ENABLE = true;

    private List<View> viewList = new ArrayList<>(); // 存放的是每一层的view，从顶到底
    private List<View> releasedViewList = new ArrayList<>(); // 手指松开后存放的view列表

    private CardAdapterView mCardAdapterView;  //卡片列表

    /* 拖拽工具类 */
    private ViewDragHelper mDragHelper; // 这个跟原生的ViewDragHelper差不多，我仅仅只是修改了Interpolator
    private int initCenterViewX = 0, initCenterViewY = 0; // 最初时，中间View的x位置,y位置
    private int allWidth = 0; // 面板的宽度
    private int allHeight = 0; // 面板的高度
    private int childWith = 0; // 每一个子View对应的宽度
    //触摸到卡片的下半部分
    private boolean touchOnBottom;

    private static final float SCALE_STEP = 0.08f; // view叠加缩放的步长
    private static final int MAX_SLIDE_DISTANCE_LINKAGE = 400; // 水平距离+垂直距离
    // 超过这个值
    // 则下一层view完成向上一层view的过渡
    private View bottomLayout; // 卡片下边的三个按钮布局

    private int bottomMarginTop = 40;
    private int yOffsetStep = 40; // view叠加垂直偏移量的步长

    private static final int X_VEL_THRESHOLD = 900;
    private static final int X_DISTANCE_THRESHOLD = 300;

    public static final int VANISH_TYPE_LEFT = 0;
    public static final int VANISH_TYPE_RIGHT = 1;

    private final Object objLock = new Object();

    private CardSwitchListener cardSwitchListener; // 回调接口
    private int showingPosition = 0; // 当前正在显示的小项
    private View leftBtn, rightBtn;
    private boolean btnLock = false;
    private GestureDetectorCompat moveDetector;

    public CardSlidePanel(Context context) {
        this(context, null);
    }

    public CardSlidePanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardSlidePanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.card);

        bottomMarginTop = (int) a.getDimension(R.styleable.card_bottomMarginTop, bottomMarginTop);
        yOffsetStep = (int) a.getDimension(R.styleable.card_yOffsetStep, yOffsetStep);
        a.recycle();

        moveDetector = new GestureDetectorCompat(context,
                new MoveDetector());
    }

    public ListAdapter getAdapter() {
        return mCardAdapterView.getAdapter();
    }

    public void setAdapter(ListAdapter adapter) {
        mCardAdapterView.setAdapter(adapter);

        ensureFull();

        if (null != cardSwitchListener) {
            cardSwitchListener.onShow(0);
        }

        requestLayout();
    }

    private final static int MAX_VIEW_SIZE = 4;

    private void ensureFull() {
        if (DEBUG) {
            Log.d(TAG, "ensureFull");
        }
        viewList.clear();
        for (int i = 0; i < MAX_VIEW_SIZE; i++) {
            View viewItem = mCardAdapterView.getAdapter().getView(i, null, mCardAdapterView);
            mCardAdapterView.addViewInLayout(viewItem, i);
            viewList.add(viewItem);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (DEBUG) {
            Log.d(TAG, "onFinishInflate");
        }

        // 渲染完成，初始化卡片view列表
        viewList.clear();
        int num = getChildCount();
        for (int i = num - 1; i >= 0; i--) {
            View childView = getChildAt(i);
            if (childView.getId() == R.id.card_bottom_layout) {
                bottomLayout = childView;
                initBottomLayout();
            } else if (childView instanceof CardAdapterView) {
                mCardAdapterView = (CardAdapterView) childView;
                mCardAdapterView.setParentView(this);
                // 滑动相关类
                mDragHelper = ViewDragHelper
                        .create(mCardAdapterView, 10f, new DragHelperCallback());
                mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
            }
        }
    }

    private void initBottomLayout() {
        if (DEBUG) {
            Log.d(TAG, "initBottomLayout");
        }
        leftBtn = bottomLayout.findViewById(R.id.card_left_btn);
        rightBtn = bottomLayout.findViewById(R.id.card_right_btn);

        OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 点击的是bottomLayout里面的一些按钮
                btnLock = true;
                int type = -1;
                if (view == leftBtn) {
                    type = VANISH_TYPE_LEFT;
                } else if (view == rightBtn) {
                    type = VANISH_TYPE_RIGHT;
                }
                vanishOnBtnClick(type);
            }
        };

        leftBtn.setOnClickListener(btnListener);
        rightBtn.setOnClickListener(btnListener);
    }

    class MoveDetector extends SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx,
                                float dy) {
            // 拖动了，touch不往下传递
            return Math.abs(dy) + Math.abs(dx) > 5;
        }
    }


    /**
     * 这是viewdraghelper拖拽效果的主要逻辑
     */
    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            if (DEBUG) {
                Log.d(TAG, "onViewPositionChanged() called with: " + "changedView = [" + changedView + "], left = [" + left + "], top = [" + top + "], dx = [" + dx + "], dy = [" + dy + "]");
            }
            // 调用offsetLeftAndRight导致viewPosition改变，会调到此处，所以此处对index做保护处理
            int index = viewList.indexOf(changedView);
            if (index + 2 > viewList.size()) {
                return;
            }

            processLinkageView(changedView);
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            // 如果数据List为空，或者子View不可见，则不予处理
            if (child == bottomLayout || mCardAdapterView == null ||
                    mCardAdapterView.getAdapter() == null || mCardAdapterView.getAdapter().getCount() == 0
                    || child.getVisibility() != View.VISIBLE || child.getScaleX() <= 1.0f - SCALE_STEP) {
                // 一般来讲，如果拖动的是第三层、或者第四层的View，则直接禁止
                // 此处用getScale的用法来巧妙回避
                if (DEBUG) {
                    Log.d(TAG, "tryCaptureView: return false");
                }
                return false;
            }

            if (btnLock) {
                if (DEBUG) {
                    Log.d(TAG, "tryCaptureView: return false");
                }
                return false;
            }

            // 只捕获顶部view(rotation=0)
            int childIndex = viewList.indexOf(child);
            if (childIndex > 0) {
                if (DEBUG) {
                    Log.d(TAG, "tryCaptureView: return false");
                }
                return false;
            }

            if (DEBUG) {
                Log.d(TAG, "tryCaptureView: return true");
            }
            return true;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            // 这个用来控制拖拽过程中松手后，自动滑行的速度
            return 256;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            animToSide(releasedChild, xvel, yvel);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }
    }

    /**
     * 对View重新排序
     */
    private void orderViewStack() {
        synchronized (objLock) {
            if (releasedViewList.size() == 0) {
                return;
            }

            View changedView = releasedViewList.get(0);
            if (changedView.getLeft() == initCenterViewX) {
                releasedViewList.remove(0);
                return;
            }

            if (DEBUG) {
                Log.d(TAG, "orderViewStack");
            }

            // 1. 消失的卡片View位置重置，由于大多手机会重新调用onLayout函数，所以此处大可以不做处理，不信你注释掉看看
            changedView.offsetLeftAndRight(initCenterViewX
                    - changedView.getLeft());
            changedView.offsetTopAndBottom(initCenterViewY
                    - changedView.getTop() + yOffsetStep * 2);
            float scale = 1.0f - SCALE_STEP * 2;
            changedView.setScaleX(scale);
            changedView.setScaleY(scale);
            //add by yubenben for rotation back
            if (ROTATION_ENABLE) {
                changedView.setRotation(0);
            }

            // 2. 卡片View在ViewGroup中的顺次调整
            int num = viewList.size();
            for (int i = num - 1; i > 0; i--) {
                View tempView = viewList.get(i);
                tempView.bringToFront();
            }

            // 3. changedView填充新数据
            int newIndex = showingPosition + 4;
            if (newIndex < mCardAdapterView.getAdapter().getCount()) {
                if (changedView.getVisibility() != View.VISIBLE) {
                    changedView.setVisibility(View.VISIBLE);
                }
                mCardAdapterView.getAdapter().getView(newIndex, changedView, mCardAdapterView);
            } else {
                changedView.setVisibility(View.INVISIBLE);
                if (newIndex >= mCardAdapterView.getAdapter().getCount() - 3) {
                    if (mOnLastItemVisible != null) {
                        mOnLastItemVisible.onVisible();
                    }
                }
            }

            // 4. viewList中的卡片view的位次调整
            viewList.remove(changedView);
            viewList.add(changedView);
            releasedViewList.remove(0);

            // 5. 更新showIndex、接口回调
            showingPosition++;
            if (showingPosition < mCardAdapterView.getAdapter().getCount()) {
                if (null != cardSwitchListener) {
                    cardSwitchListener.onShow(showingPosition);
                }
            }
        }
    }

    public void refreshViewStack() {
        if (DEBUG) {
            Log.d(TAG, "refreshViewStack");
        }
        int i = 0;
        for (View view : viewList) {
            if (View.VISIBLE != view.getVisibility()) {
                view.setVisibility(View.VISIBLE);
                mCardAdapterView.getAdapter().getView(showingPosition + i,
                        view, mCardAdapterView);
            }
            i++;
        }
    }

    /**
     * 顶层卡片View位置改变，底层的位置需要调整
     *
     * @param changedView 顶层的卡片view
     */
    private void processLinkageView(View changedView) {
        if (DEBUG) {
            Log.d(TAG, "processLinkageView");
        }
        int changeViewLeft = changedView.getLeft();
        int changeViewTop = changedView.getTop();
        int distance = Math.abs(changeViewTop - initCenterViewY)
                + Math.abs(changeViewLeft - initCenterViewX);
        float rate = distance / (float) MAX_SLIDE_DISTANCE_LINKAGE;

        float rate1 = rate;
        float rate2 = rate - 0.2f;

        if (rate > 1) {
            rate1 = 1;
        }

        if (rate2 < 0) {
            rate2 = 0;
        } else if (rate2 > 1) {
            rate2 = 1;
        }

        ajustLinkageViewItem(changedView, rate1, 1);
        ajustLinkageViewItem(changedView, rate2, 2);

        //add by yubenben for rotation
        if (ROTATION_ENABLE) {
            if (touchOnBottom) {
                float rotation = Math.copySign(Math.abs((float)changeViewLeft - initCenterViewX) / 50, initCenterViewX - changeViewLeft);
                Log.d(TAG, "processLinkageView: rotation = " + rotation);
                changedView.setRotation(Math.abs(rotation) < 45 ? rotation : Math.copySign(45, rotation));

            } else {
                float rotation = Math.copySign(Math.abs((float)changeViewLeft - initCenterViewX) / 25, changeViewLeft - initCenterViewX);
                Log.d(TAG, "processLinkageView: rotation = " + rotation);
                changedView.setRotation(Math.abs(rotation) < 45 ? rotation : Math.copySign(45, rotation));
            }
        }
    }

    // 由index对应view变成index-1对应的view
    private void ajustLinkageViewItem(View changedView, float rate, int index) {
        if (DEBUG) {
            Log.d(TAG, "ajustLinkageViewItem: rate="+ rate +  "  index=" + index);
        }
        int changeIndex = viewList.indexOf(changedView);
        int initPosY = yOffsetStep * index;
        float initScale = 1 - SCALE_STEP * index;

        int nextPosY = yOffsetStep * (index - 1);
        float nextScale = 1 - SCALE_STEP * (index - 1);

        int offset = (int) (initPosY + (nextPosY - initPosY) * rate);
        float scale = initScale + (nextScale - initScale) * rate;

        View ajustView = viewList.get(changeIndex + index);
        ajustView.offsetTopAndBottom(offset - ajustView.getTop()
                + initCenterViewY);
        ajustView.setScaleX(scale);
        ajustView.setScaleY(scale);
    }

    /**
     * 松手时处理滑动到边缘的动画
     *
     * @param xvel X方向上的滑动速度
     */
    private void animToSide(View changedView, float xvel, float yvel) {
        if (DEBUG) {
            Log.d(TAG, "animToSide: xvel=" + xvel + " yvel=" + yvel);
        }
        int finalX = initCenterViewX;
        int finalY = initCenterViewY;
        int flyType = -1;

        // 1. 下面这一坨计算finalX和finalY，要读懂代码需要建立一个比较清晰的数学模型才能理解，不信拉倒
        int dx = changedView.getLeft() - initCenterViewX;
        int dy = changedView.getTop() - initCenterViewY;
        if (dx == 0) {
            // 由于dx作为分母，此处保护处理
            dx = 1;
        }
        if (xvel > X_VEL_THRESHOLD || dx > X_DISTANCE_THRESHOLD) {
            finalX = allWidth * 3 / 2;
            finalY = dy * (childWith + initCenterViewX) / dx + initCenterViewY;
            flyType = VANISH_TYPE_RIGHT;
        } else if (xvel < -X_VEL_THRESHOLD || dx < -X_DISTANCE_THRESHOLD) {
            finalX = -childWith * 3 / 2;
            finalY = dy * (childWith + initCenterViewX) / (-dx) + dy
                    + initCenterViewY;
            flyType = VANISH_TYPE_LEFT;
        }

        // 如果斜率太高，就折中处理
        if (finalY > allHeight) {
            finalY = allHeight;
        } else if (finalY < -allHeight / 2) {
            finalY = -allHeight / 2;
        }

        // 如果没有飞向两侧，而是回到了中间，需要谨慎处理
        if (finalX != initCenterViewX) {
            releasedViewList.add(changedView);
        }

        // 2. 启动动画
        if (mDragHelper.smoothSlideViewTo(changedView, finalX, finalY)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }


        // 3. 消失动画即将进行，listener回调
        if (flyType >= 0 && cardSwitchListener != null) {
            cardSwitchListener.onCardVanish(showingPosition, flyType);
        }
    }

    /**
     * 点击按钮消失动画
     */
    private void vanishOnBtnClick(int type) {
        synchronized (objLock) {
            if (DEBUG) {
                Log.d(TAG, "vanishOnBtnClick: type=" + type);
            }
            View animateView = viewList.get(0);
            if (animateView.getVisibility() != View.VISIBLE || releasedViewList.contains(animateView)) {
                return;
            }

            int finalX = 0;
            if (type == VANISH_TYPE_LEFT) {
                finalX = -childWith;
            } else if (type == VANISH_TYPE_RIGHT) {
                finalX = allWidth;
            }

            if (finalX != 0) {
                releasedViewList.add(animateView);
                if (mDragHelper.smoothSlideViewTo(animateView, finalX, initCenterViewY - allHeight / 3)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
            }

            if (type >= 0 && cardSwitchListener != null) {
                cardSwitchListener.onCardVanish(showingPosition, type);
            }
        }
    }

    @Override
    public void computeScroll() {
        if (DEBUG) {
            Log.d(TAG, "computeScroll");
        }
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            // 动画结束
            synchronized (this) {
                if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
                    orderViewStack();
                    btnLock = false;
                }
            }
        }
    }

    /* touch事件的拦截与处理都交给mDraghelper来处理 */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean shouldIntercept = mDragHelper.shouldInterceptTouchEvent(ev);
        boolean moveFlag = moveDetector.onTouchEvent(ev);
        int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {

            float downX = ev.getX();
            float downY = ev.getY();
            Log.d(TAG, "onInterceptTouchEvent: downY=" + downY
                    + " initCenterViewY=" + initCenterViewY + " childWidth=" + childWith
                    + " centerY = " + (initCenterViewY + (float)childWith / 2));
            touchOnBottom = downY >  initCenterViewY + (float)childWith / 2;

            // ACTION_DOWN的时候就对view重新排序
            orderViewStack();

            // 保存初次按下时arrowFlagView的Y坐标
            // action_down时就让mDragHelper开始工作，否则有时候导致异常
            mDragHelper.processTouchEvent(ev);
        } else if (action == MotionEvent.ACTION_UP) {
            if (shouldIntercept && !moveFlag) {
                // 点击的是卡片
                if (null != cardSwitchListener) {
                    cardSwitchListener.onItemClick(showingPosition);
                }
                if (DEBUG) {
                    Log.d(TAG, "onInterceptTouchEvent: mDragHelper.abort()");
                }
                mDragHelper.abort();
            }
        }

        if (DEBUG) {
            Log.d(TAG, "onInterceptTouchEvent:" + StringUtils.motionEventActionToString(action) +
                    ", shouldIntercept = " + shouldIntercept
                    + ", moveFlag = " + moveFlag);
        }
        return shouldIntercept && moveFlag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (DEBUG) {
            Log.d(TAG, "onTouchEvent:" + StringUtils.motionEventActionToString(e.getAction()));
        }
        try {
            // 统一交给mDragHelper处理，由DragHelperCallback实现拖动效果
            // 该行代码可能会抛异常，正式发布时请将这行代码加上try catch
            mDragHelper.processTouchEvent(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        allWidth = getMeasuredWidth() + 100;
        allHeight = getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (DEBUG) {
            Log.d(TAG, "onLayout");
        }
        int size = viewList.size();
        if (size > 0) {

            // 布局卡片view
            for (int i = 0; i < size; i++) {
                View viewItem = viewList.get(i);
                int offset = yOffsetStep * i;
                float scale = 1 - SCALE_STEP * i;
                if (i > 2) {
                    // 备用的view
                    offset = yOffsetStep * 2;
                    scale = 1 - SCALE_STEP * 2;
                }

                viewItem.offsetTopAndBottom(offset);
                viewItem.setScaleX(scale);
                viewItem.setScaleY(scale);
                viewItem.setRotation(0);
            }

            // 初始化一些中间参数
            initCenterViewX = viewList.get(0).getLeft();
            initCenterViewY = viewList.get(0).getTop();
            childWith = viewList.get(0).getMeasuredWidth();
        }
    }

    /**
     * 设置卡片操作回调
     *
     * @param cardSwitchListener 回调接口
     */
    public void setCardSwitchListener(CardSwitchListener cardSwitchListener) {
        this.cardSwitchListener = cardSwitchListener;
    }

    /**
     * 卡片回调接口
     */
    public interface CardSwitchListener {
        /**
         * 新卡片显示回调
         *
         * @param index 最顶层显示的卡片的index
         */
        void onShow(int index);

        /**
         * 卡片飞向两侧回调
         *
         * @param index 飞向两侧的卡片数据index
         * @param type  飞向哪一侧{@link #VANISH_TYPE_LEFT}或{@link #VANISH_TYPE_RIGHT}
         */
        void onCardVanish(int index, int type);

        /**
         * 卡片点击事件
         *
         * @param index 点击到的index
         */
        void onItemClick(int index);
    }

    private OnLastItemVisible mOnLastItemVisible = null;

    public interface OnLastItemVisible {
        void onVisible();
    }

    public void setOnLastItemVisible(OnLastItemVisible listener) {
        this.mOnLastItemVisible = listener;
    }
}
