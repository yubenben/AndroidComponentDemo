package com.ran.ben.androidcomponentdemo.view.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
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
    public static final boolean DEBUG = false;
    private static final boolean ROTATION_ENABLE = true;

    private List<View> viewList = new ArrayList<View>(); // 存放的是每一层的view，从顶到底
    private List<View> releasedViewList = new ArrayList<View>(); // 手指松开后存放的view列表

    private CardAdapterView mCardAdapterView;  //卡片列表,使用adapterView实现复用
    private View mTopLike; //喜欢标记
    private View mTopIgnore; //无感标记

    private ViewDragHelper mDragHelper; // 这个跟原生的ViewDragHelper差不多，我仅仅只是修改了Interpolator
    private GestureDetectorCompat moveDetector;

    private int initCenterViewX = 0, initCenterViewY = 0; // 最初时，中间View的x位置,y位置 onLayout会赋值
    private int allWidth = 0; // 面板的宽度
    private int allHeight = 0; // 面板的高度
    private int childWith = 0; // 每一个子View对应的宽度

    //触摸到卡片的半部分，上半部分时旋转角度大
    private boolean touchOnBottom;
    private boolean touchOnTop;

    public final static int MAX_VIEW_SIZE = 4; //同时存在的卡片数量
    private static final float SCALE_STEP = 0.08f; // view叠加缩放的步长
    private static final int MAX_SLIDE_DISTANCE_LINKAGE = 400; // 水平距离+垂直距离，超过这个值则下一层view完成向上一层view的过渡
    private int yOffsetStep = 40; // view叠加垂直偏移量的步长
    private View bottomLayout; // 卡片下边的三个按钮布局

    private static final int X_VEL_THRESHOLD = 1000; //手指滑动加速度小于这个的时候卡片回到中心，否则滑走。
    private int mFlingSlop = 100;  //快速滑动时滑动距离小于这个值则回到中心
    private int mLikeSlop;   //卡片超过这个值时，送卡卡片则滑走

    public static final int VANISH_TYPE_LEFT = 0;
    public static final int VANISH_TYPE_RIGHT = 1;

    private final Object objLock = new Object();

    private CardSwitchListener cardSwitchListener; // 回调接口
    private int showingPosition = 0; // 当前顶部卡片的下标值

    private View leftBtn, rightBtn;
    private boolean btnLock = false;
    private boolean needRefresh = false; //卡片正在处理中不更新数据

    public CardSlidePanel(Context context) {
        this(context, null);
    }

    public CardSlidePanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardSlidePanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideCard);

        yOffsetStep = (int) a.getDimension(R.styleable.SlideCard_yOffsetStep, yOffsetStep);
        a.recycle();

        moveDetector = new GestureDetectorCompat(context,
                new MoveDetector());

        //根据手机屏幕初始化滑动边界
        mLikeSlop = getDisplayWidth(context) / 5;
        mFlingSlop = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity() / 2;
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    public static int getDisplayWidth(Context ctx){
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager winManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        winManager.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels; // 屏幕宽度（像素）
        return width;
    }

    public ListAdapter getAdapter() {
        return mCardAdapterView.getAdapter();
    }

    public void setAdapter(ListAdapter adapter) {
        mCardAdapterView.setAdapter(adapter);

        ensureFull();

        if (null != cardSwitchListener) {
            cardSwitchListener.onShow(mCardAdapterView, 0);
        }

        requestLayout();
    }

    public boolean isEmpty() {
        return (mCardAdapterView == null || mCardAdapterView.getAdapter() == null || showingPosition >= mCardAdapterView.getAdapter().getCount());
    }

    public int getCurrentViewId() {
        return showingPosition;
    }

    /*
    * 重新加载卡片到viewList
    * */
    private void ensureFull() {
        if (DEBUG) {
            Log.d(TAG, "ensureFull");
        }
        viewList.clear();
        for (int i = 0; i < MAX_VIEW_SIZE && i + showingPosition < mCardAdapterView.getAdapter().getCount(); i++) {
            View viewItem = mCardAdapterView.getAdapter().getView(i + showingPosition, null, mCardAdapterView);
            viewItem.setLayerType(LAYER_TYPE_HARDWARE, null);
            mCardAdapterView.addViewInLayout(viewItem, i + showingPosition);
            viewList.add(viewItem);
        }
        mCardAdapterView.requestLayout();
    }

    /*
    * 在adapter 的onInvalidated中调用清除掉当前存在的卡片
    * */
    void clearViewStack() {
        viewList.clear();
        showingPosition = 0;
    }

    /*
    * 有新数据时会更新卡片
    * */
    public void refreshViewStack() {
        if (DEBUG) {
            Log.d(TAG, "refreshViewStack " + showingPosition);
        }

        if (releasedViewList.size() > 0) {
            if (DEBUG) {
                Log.d(TAG, "releasedViewList.size() = " + releasedViewList.size());
            }
            needRefresh = true;
            return;
        }

        if (viewList.size() > 0) {

            needRefresh = false;
            int i = 0;
            for (View view : viewList) {
                if (showingPosition + i < mCardAdapterView.getAdapter().getCount()) {
                    if (View.VISIBLE != view.getVisibility()) {
                        view.setVisibility(View.VISIBLE);
                        try {
                            mCardAdapterView.getAdapter().getView(showingPosition + i,
                                    view, mCardAdapterView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                i++;
            }

            if (viewList.size() >= MAX_VIEW_SIZE) {
                return;
            }

            while (i < MAX_VIEW_SIZE && showingPosition + i  <  mCardAdapterView.getAdapter().getCount()) {
                View viewItem = mCardAdapterView.getAdapter().getView(showingPosition + i, null, mCardAdapterView);
                viewItem.setLayerType(LAYER_TYPE_HARDWARE, null);
                mCardAdapterView.addViewInLayout(viewItem, showingPosition + i);
                viewList.add(viewItem);
                i++;
            }

            requestLayout();
        } else {
            ensureFull();
            if (null != cardSwitchListener) {
                cardSwitchListener.onShow(mCardAdapterView, 0);
            }

            requestLayout();
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

    //初始化底部三个按钮，我们现在没有使用
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
                    || child.getVisibility() != View.VISIBLE || child.getScaleX() <= 1.0f - SCALE_STEP
                    || showingPosition >= mCardAdapterView.getAdapter().getCount()
                    || mCardAdapterView.getAdapter().getItemViewType(showingPosition) < 0) {
                // 一般来讲，如果拖动的是第三层、或者第四层的View，则直接禁止
                // 此处用getScale的用法来巧妙回避
                if (DEBUG) {
                    Log.e(TAG, "tryCaptureView: return false");
                }
                return false;
            }

            if (btnLock) {
                if (DEBUG) {
                    Log.e(TAG, "tryCaptureView: return false btnLock");
                }
                return false;
            }

            // 只捕获顶部view(rotation=0)
            int childIndex = viewList.indexOf(child);
            if (childIndex > 0) {
                if (DEBUG) {
                    Log.e(TAG, "tryCaptureView: return false childIndex = " + childIndex);
                }

                try {
                    if (mDragHelper.smoothSlideViewTo(viewList.get(0), initCenterViewX, initCenterViewY)) {
                        ViewCompat.postInvalidateOnAnimation(CardSlidePanel.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
        public int getViewVerticalDragRange(View child) {
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
                Log.d(TAG, "orderViewStack position=" + showingPosition
                        + "releasedViewList size=" + releasedViewList.size());
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
            if (mTopLike != null) {
                mTopLike.setAlpha(0);
            }
            if (mTopIgnore != null) {
                mTopIgnore.setAlpha(0);
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

                CardAdapterView.LayoutParams params  =
                        (CardAdapterView.LayoutParams) changedView.getLayoutParams();
                if  (DEBUG) {
                    Log.i(TAG, "orderViewStack: params.viewType=" + params.viewType);
                    Log.i(TAG, "orderViewStack: newIndex + " + newIndex + " itemViewType=" +
                            mCardAdapterView.getAdapter().getItemViewType(newIndex));
                }
                if (params.viewType == mCardAdapterView.getAdapter().getItemViewType(newIndex)) {
                    try {
                        mCardAdapterView.getAdapter().getView(newIndex, changedView, mCardAdapterView);
                        // 4. viewList中的卡片view的位次调整
                        viewList.remove(changedView);
                        viewList.add(changedView);
                        releasedViewList.remove(0);
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        changedView.setVisibility(GONE);
                        mCardAdapterView.removeViewInLayout(changedView);
                        viewList.remove(changedView);
                        View viewItem = mCardAdapterView.getAdapter().getView(newIndex, null, mCardAdapterView);
                        viewItem.setLayerType(LAYER_TYPE_HARDWARE, null);
                        mCardAdapterView.addViewInLayout(viewItem, newIndex);
                        viewList.add(viewItem);
                        releasedViewList.remove(0);
                        requestLayout();
                    }
                }else {
                    changedView.setVisibility(GONE);
                    mCardAdapterView.removeViewInLayout(changedView);
                    viewList.remove(changedView);
                    View viewItem = mCardAdapterView.getAdapter().getView(newIndex, null, mCardAdapterView);
                    viewItem.setLayerType(LAYER_TYPE_HARDWARE, null);
                    mCardAdapterView.addViewInLayout(viewItem, newIndex);
                    viewList.add(viewItem);
                    releasedViewList.remove(0);
                    requestLayout();
                }
            } else {
                changedView.setVisibility(View.GONE);
                // 4. viewList中的卡片view的位次调整
//                viewList.remove(changedView);
//                viewList.add(changedView);
//                releasedViewList.remove(0);
                viewList.remove(changedView);
                releasedViewList.remove(changedView);
                mCardAdapterView.removeViewInLayout(changedView);
            }

            if (newIndex >= mCardAdapterView.getAdapter().getCount() - 3) {
                if (mOnLastItemVisible != null) {
                    mOnLastItemVisible.onVisible();
                }
            }

            // 5. 更新showIndex、接口回调
            showingPosition++;
            if (showingPosition < mCardAdapterView.getAdapter().getCount()) {
                if (null != cardSwitchListener) {
                    cardSwitchListener.onShow(mCardAdapterView, showingPosition);
                }
            } else {
                setVisibility(GONE);
            }

            if (viewList.size() > 0) {
                View topView = viewList.get(0);
                mTopLike = topView.findViewById(R.id.like_btn);
                if (mTopLike != null) {
                    mTopLike.setAlpha(0);
                }
                mTopIgnore = topView.findViewById(R.id.ignore_btn);
                if (mTopIgnore != null) {
                    mTopIgnore.setAlpha(0);
                }
            }

            if (needRefresh) {
                refreshViewStack();
            }
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
                float rotation = Math.copySign(Math.abs((float) changeViewLeft - initCenterViewX)
                        * 15  / getWidth(), initCenterViewX - changeViewLeft);
                if (DEBUG) {
                    Log.d(TAG, "bottom processLinkageView: rotation = " + rotation);
                }
                changedView.setRotation(Math.abs(rotation) < 45 ? rotation : Math.copySign(45, rotation));

            } else if(touchOnTop) {
                float rotation = Math.copySign(Math.abs((float) changeViewLeft - initCenterViewX)
                        * 30  / getWidth(), changeViewLeft - initCenterViewX);
                if (DEBUG) {
                    Log.d(TAG, "processLinkageView: rotation = " + rotation);
                }
                changedView.setRotation(Math.abs(rotation) < 45 ? rotation : Math.copySign(45, rotation));
            }
        }

        float alpha = ((float) changeViewLeft - initCenterViewX) / mLikeSlop;
        if (DEBUG) {
            Log.d(TAG, "processLinkageView: alpha = "  + alpha);
        }
        if (mTopLike != null) {
            mTopLike.setAlpha(alpha > 0 ? alpha : 0);
        }
        if (mTopIgnore != null) {
            mTopIgnore.setAlpha(alpha < 0 ? -alpha : 0);
        }
    }

    // 由index对应view变成index-1对应的view
    private void ajustLinkageViewItem(View changedView, float rate, int index) {
        if (DEBUG) {
            Log.d(TAG, "ajustLinkageViewItem: rate=" + rate + "  index=" + index);
        }
        int changeIndex = viewList.indexOf(changedView);
        int initPosY = yOffsetStep * index;
        float initScale = 1 - SCALE_STEP * index;

        int nextPosY = yOffsetStep * (index - 1);
        float nextScale = 1 - SCALE_STEP * (index - 1);

        int offset = (int) (initPosY + (nextPosY - initPosY) * rate);
        float scale = initScale + (nextScale - initScale) * rate;

        if (changeIndex  + index >= viewList.size()) {
            return;
        }
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
            int  index  = viewList.indexOf(changedView);
            Log.w(TAG, "animToSide: changedView visible=" + changedView.getVisibility() +
                    ", in viewList  index=" + index);
        }

        if (changedView.getVisibility() != View.VISIBLE) {
            return;
        }

        int finalX = initCenterViewX;
        int finalY = initCenterViewY;
        int flyType = -1;

        // 1. 下面这一坨计算finalX和finalY，要读懂代码需要建立一个比较清晰的数学模型才能理解，不信拉倒
        int dx = changedView.getLeft() - initCenterViewX;
        int dy = changedView.getTop() - initCenterViewY;
        if (dx == 0) {
            // 由于dx作为分母，此处保护处理
            dx = (int) Math.copySign(1, xvel);
        }
        if  (dy == 0) {
            dy = (int) Math.copySign(100, yvel);
        }
        if (dx > mLikeSlop || (xvel > X_VEL_THRESHOLD && (dx > mFlingSlop || dx == 1))) {
            if (DEBUG) {
                Log.w(TAG, "animToSide: xvel=" + xvel + " dx=" + dx + " mFlingSlop=" + mFlingSlop);
            }
            finalX = allWidth * 3 / 2;
            finalY = dy * (childWith + initCenterViewX) / dx + initCenterViewY;
            flyType = VANISH_TYPE_RIGHT;
        } else if (dx < -mLikeSlop || (xvel < -X_VEL_THRESHOLD && (dx < -mFlingSlop || dx == -1))) {
            if (DEBUG) {
                Log.w(TAG, "animToSide: xvel=" + xvel + " dx=" + dx  + " mFlingSlop=" + mFlingSlop);
            }
            finalX = -childWith * 3 / 2;
            finalY = dy * (childWith + initCenterViewX) / (-dx) + dy
                    + initCenterViewY;
            flyType = VANISH_TYPE_LEFT;
        } else {
            if (DEBUG) {
                Log.e(TAG, "animToSide: xvel=" + xvel + " dx=" + dx  + " mFlingSlop=" + mFlingSlop);
            }
        }

        // 如果斜率太高，就折中处理
        if (finalY > allHeight) {
            finalY = allHeight;
        } else if (finalY < -allHeight / 2) {
            finalY = -allHeight / 2;
        }

        // 如果没有飞向两侧，而是回到了中间，需要谨慎处理
        if (finalX != initCenterViewX) {
            if (DEBUG) {
                Log.e(TAG, "releasedViewList add");
            }
            releasedViewList.add(changedView);
        }

        // 2. 启动动画
        if (finalX != initCenterViewX) {

            if (mDragHelper.smoothSlideViewTo(changedView, finalX, finalY)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            if (mDragHelper.smoothSlideViewToOvershoot(changedView, finalX, finalY)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }


        // 3. 消失动画即将进行，listener回调
        if (flyType >= 0 && cardSwitchListener != null) {
            cardSwitchListener.onCardVanish(mCardAdapterView, changedView, showingPosition,
                    flyType, 0);
        }
    }

    /**
     * 点击按钮消失动画
     */
    public void vanishOnBtnClick(int flyType, int type) {
        synchronized (objLock) {
            if (DEBUG) {
                Log.d(TAG, "vanishOnBtnClick: type=" + flyType);
            }

            if (viewList.size() > 0) {
                View animateView = viewList.get(0);
                if (animateView.getVisibility() != View.VISIBLE || releasedViewList.contains(animateView)) {
                    return;
                }

                int finalX = 0;
                if (flyType == VANISH_TYPE_LEFT) {
                    finalX = -childWith * 3 / 2;
                } else if (flyType == VANISH_TYPE_RIGHT) {
                    finalX = allWidth * 3 / 2;
                }

                if (finalX != 0) {
                    touchOnBottom = false;
                    touchOnTop = true;
                    releasedViewList.add(animateView);
                    if (mDragHelper.smoothSlideViewToAnticipateOvershoot(animateView, finalX, initCenterViewY)) {
                        ViewCompat.postInvalidateOnAnimation(this);
                    }
                }

                if (flyType >= 0 && cardSwitchListener != null) {
                    cardSwitchListener.onCardVanish(mCardAdapterView, animateView,
                            showingPosition, flyType, type);
                }
            }
        }
    }

    /**
     * 点击按钮消失动画
     */
    public void vanishOnBtnClick(int flyType) {
        vanishOnBtnClick(flyType, 0);
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

            //确定手指触摸卡片的位置
            float downY = ev.getY();

            touchOnTop = downY < initCenterViewY +  (float)childWith / 4;
            touchOnBottom = downY > initCenterViewY + (float)childWith * 3 / 4;

            if (DEBUG) {
                Log.d(TAG, "onInterceptTouchEvent: downY=" + downY
                        + " initCenterViewY=" + initCenterViewY + " childWidth=" + childWith
                        + " centerY = " + (initCenterViewY + (float) childWith / 3));
            }

            // ACTION_DOWN的时候就对view重新排序
            orderViewStack();

            // 保存初次按下时arrowFlagView的Y坐标
            // action_down时就让mDragHelper开始工作，否则有时候导致异常
            mDragHelper.processTouchEvent(ev);
        }

        if (DEBUG) {
            Log.d(TAG, "onInterceptTouchEvent:" + action +
                    ", shouldIntercept = " + shouldIntercept
                    + ", moveFlag = " + moveFlag);
        }
        return shouldIntercept && moveFlag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (DEBUG) {
            Log.d(TAG, "onTouchEvent:" + e.getAction());
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

        allWidth = getMeasuredWidth();
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

            View topView = viewList.get(0);
            mTopLike = topView.findViewById(R.id.like_btn);
            mTopIgnore = topView.findViewById(R.id.ignore_btn);
        }
    }

    public void leftAndRightAnimation() {

        if (viewList == null || viewList.size() == 0) {
            return;
        }

        View animateView = viewList.get(0);
        if (animateView.getVisibility() != View.VISIBLE || releasedViewList.contains(animateView)) {
            return;
        }

        int duration = 500;
        RotateAnimation rotateAnimation = new RotateAnimation(0, -10,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
        rotateAnimation.setDuration(duration);
        rotateAnimation.setRepeatCount(1);
        rotateAnimation.setRepeatMode(Animation.REVERSE);

        AnimationSet mLeftAnimationSet = new AnimationSet(true);
        mLeftAnimationSet.setInterpolator(new LinearInterpolator());
        //mLeftAnimationSet.addAnimation(translateAnimation);
        mLeftAnimationSet.addAnimation(rotateAnimation);
        mLeftAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (mTopIgnore != null) {
                    mTopIgnore.animate()
                            .setDuration(500)
                            .setInterpolator(new LinearInterpolator())
                            .alpha(1.0f);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mTopIgnore != null) {
                    mTopIgnore.animate()
                            .setDuration(250)
                            .setInterpolator(new LinearInterpolator())
                            .alpha(0);
                }
                rightAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animateView.startAnimation(mLeftAnimationSet);

    }

    private void rightAnimation() {

        if (viewList == null || viewList.size() == 0) {
            return;
        }

        View animateView = viewList.get(0);
        if (animateView.getVisibility() != View.VISIBLE || releasedViewList.contains(animateView)) {
            return;
        }

        int duration = 500;
        RotateAnimation rotateAnimation = new RotateAnimation(0, 10,
                Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 1);
        rotateAnimation.setDuration(duration);
        rotateAnimation.setRepeatCount(1);
        rotateAnimation.setRepeatMode(Animation.REVERSE);

        AnimationSet mRightAnimationSet = new AnimationSet(true);
        mRightAnimationSet.setInterpolator(new LinearInterpolator());
        //mRightAnimationSet.addAnimation(translateAnimation);
        mRightAnimationSet.addAnimation(rotateAnimation);
        mRightAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (mTopLike != null) {
                    mTopLike.animate()
                            .setDuration(500)
                            .setInterpolator(new LinearInterpolator())
                            .alpha(1.0f);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mTopLike != null) {
                    mTopLike.animate()
                            .setDuration(250)
                            .setInterpolator(new LinearInterpolator())
                            .alpha(0);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animateView.startAnimation(mRightAnimationSet);

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
        void onShow(AdapterView<?> parent, int index);

        /**
         * 卡片飞向两侧回调
         *
         * @param index 飞向两侧的卡片数据index
         * @param type  飞向哪一侧{@link #VANISH_TYPE_LEFT}或{@link #VANISH_TYPE_RIGHT}
         */
        void onCardVanish(AdapterView<?> parent, View view, int index, int flyType, int type);

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
