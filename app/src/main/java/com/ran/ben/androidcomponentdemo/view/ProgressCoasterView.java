package com.ran.ben.androidcomponentdemo.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;

import com.ran.ben.androidcomponentdemo.utils.DensityUtil;

import java.util.ArrayList;
import java.util.Random;


public class ProgressCoasterView extends RelativeLayout {


    final static String ANDROID_XML = "http://schemas.android.com/apk/res/android";
    int backgroundColor = Color.parseColor("#FFFD6859");

    //大圆半径
    private float mCircle2Radius;
    //里面雷达半径
    private float mInnerRadius = 130;
    final static float INNER_RADIUS_PERCENT = 0.3f;

    //四周小头像半径
    private float miniCircleRadius = 30;
    final static float MINI_RADIUS_PERCENT = 0.08f;
    //旋转速度
    final static float ROTATE_SPEED = 1.0f;

    private boolean initFinish = false;

    private Paint mPaint;

    private float input = 0f;
    private float input2 = -50f;

    //在圆周排放的imageView的轨迹坐标
    private PathMeasure pathMeasure;
    private Path orbit;
    //在圆周排放的imageView
    private ArrayList<View> cViewList = new ArrayList<View>();

    public ProgressCoasterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
    }

    protected void setAttributes(AttributeSet attrs) {

        setMinimumHeight(dpToPx(32));
        setMinimumWidth(dpToPx(32));

        int backgroundColor = attrs.getAttributeResourceValue(ANDROID_XML, "background", -1);
        if (backgroundColor != -1) {
            setBackgroundColor(getResources().getColor(backgroundColor));
        } else {
            int background = attrs.getAttributeIntValue(ANDROID_XML, "background", -1);
            if (background != -1)
                setBackgroundColor(background);
            else
                setBackgroundColor(Color.parseColor("#FFFD6859"));
        }
    }

    private void init() {
        mCircle2Radius = getWidth()  <  getHeight() ? getWidth() / 2 : getHeight() / 2;
        mInnerRadius = mCircle2Radius * INNER_RADIUS_PERCENT;
        miniCircleRadius = mCircle2Radius * MINI_RADIUS_PERCENT;
        if (mInnerRadius < dpToPx(5)) {
            mInnerRadius = dpToPx(5);
        }
        if (getChildCount() >= 1) {
            View view = getChildAt(0);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            //Log.d("TAG", "init: params.width=" + params.width + " params.height=" + params.height);
            params.width = (int) (mInnerRadius * 2);
            params.height = (int) (mInnerRadius * 2);
            //Log.d("TAG", "init: params.width=" + params.width + " params.height=" + params.height);
            requestLayout();

            RotateAnimation mRotateAnimation =
                    new RotateAnimation(0, 360, Animation.ABSOLUTE, mInnerRadius,
                            Animation.ABSOLUTE, mInnerRadius);
            mRotateAnimation.setInterpolator(new LinearInterpolator());
            mRotateAnimation.setDuration(1000);
            mRotateAnimation.setRepeatCount(Animation.INFINITE);
            view.setAnimation(mRotateAnimation);
        }

        cViewList.clear();
        for (int i = 1; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.setVisibility(INVISIBLE);
            cViewList.add(view);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            drawFrame(canvas);
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Draw animation of view
     *
     * @param canvas
     */
    private void drawFrame(Canvas canvas) {

        if (!initFinish) {
            init();
            initFinish = true;
        }
        if (pathMeasure == null) {
            orbit = new Path();
            orbit.addCircle(mCircle2Radius,
                    mCircle2Radius,
                    mCircle2Radius * 0.7f,
                    Path.Direction.CW);

            pathMeasure = new PathMeasure(orbit, false);

            Random random = new Random();
            int start = Math.abs(random.nextInt() % (int) pathMeasure.getLength());
            int position = 0, i = 0;
            for (View view : cViewList) {
                if (position < pathMeasure.getLength() - (miniCircleRadius * 3.6f)) {
                    float[] coords = new float[]{0f, 0f};
                    pathMeasure.getPosTan((start + position) % (int) pathMeasure.getLength(),
                            coords, null);

                    view.setX((int) coords[0] - (miniCircleRadius * 3.6f) / 2 + i * dpToPx(5));
                    view.setY((int) coords[1] - (miniCircleRadius * 3.6f) / 2 + i * dpToPx(5));
                    //ViewGroup.LayoutParams params = view.getLayoutParams();
                    //params.width = (int) (miniCircleRadius * 3.6f);
                    //params.height = (int) (miniCircleRadius * 3.6f);
                    //Log.d("TAG", "init: params.width=" + params.width + " params.height=" + params.height);
                } else {
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.width = 0;
                    params.height = 0;
                }

                position += (miniCircleRadius * 3.6f) * 3;
                i++;
                i = i > 3 ? 0 : i;
            }
            requestLayout();
        }
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAntiAlias(true);
        }

        input += ROTATE_SPEED;
        input = input > 100 ? (input - 100) :  input;
        float rotateAngle = (input / 100);

        input2 += ROTATE_SPEED;
        input2 = input2 > 100 ? (input2 - 100) : input2;
        float rotateAngle2 = input2 > 0 ? (input2 / 100) :  0;

        drawCircle(canvas, rotateAngle, mPaint);
        drawCircle(canvas, rotateAngle2, mPaint);
    }

    private void drawCircle(Canvas canvas, float time , Paint paint) {
        float radius = (time) * ((mCircle2Radius - mInnerRadius))  + mInnerRadius;
        //Log.d("ben", "draw: time =  " + time + "  radius = " + radius);
        paint.setColor(Color.argb((int) ((1 - time) * 155), 225, 112, 79));
        canvas.drawCircle(mCircle2Radius, mCircle2Radius, radius, paint);
    }

    public void setBackgroundColor(int color) {
        super.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        this.backgroundColor = color;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != VISIBLE) {
            try {
                if (pathMeasure != null) {
                    pathMeasure = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (View view : cViewList) {
                view.setVisibility(INVISIBLE);
            }
        }
    }

    public void showCircleView(final IOnShowViewEndListener listener) {
        if (pathMeasure != null) {
            int i = 1;
            int duration = 300;
            for (final View view : cViewList) {
                view.setScaleX(0);
                view.setScaleY(0);
                view.setVisibility(VISIBLE);

                PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0, 1);
                PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0, 1);
                ObjectAnimator listObjectAnimator
                        = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY);
                listObjectAnimator.setDuration(duration * i);
                listObjectAnimator.setInterpolator(new OvershootInterpolator(3.0f));
                listObjectAnimator.setStartDelay(duration * (i - 1));
                if (i == cViewList.size()) {
                    listObjectAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (listener != null) {
                                listener.onEnd();
                            }
                        }
                    });
                }
                listObjectAnimator.start();
                i++;
            }
        } else {
            if (listener != null) {
                listener.onEnd();
            }
        }
    }

    public interface IOnShowViewEndListener {
        void onEnd();
    }

    private int dpToPx(float dp) {
        return DensityUtil.dip2px(getContext(), dp);
    }
}
