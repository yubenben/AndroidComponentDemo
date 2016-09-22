package com.ran.ben.androidcomponentdemo.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.ran.ben.androidcomponentdemo.R;
import com.ran.ben.androidcomponentdemo.utils.DensityUtil;

import java.util.ArrayList;
import java.util.Random;


public class ProgressCoasterView extends RelativeLayout {


    final static String ANDROID_XML = "http://schemas.android.com/apk/res/android";
    int backgroundColor = Color.parseColor("#FFFD6859");
    int backgroundColorPrimy = Color.parseColor("#FFFDADA3");
    int backgroundColorGray = Color.parseColor("#FFF3F3F3");

    //从外到内的三个圆弧的bitmap
    private Bitmap bitmapArc1;
    private Bitmap bitmapArc2;
    private Bitmap bitmapArc3;
    private Bitmap bitmapCircle;
    //从外到内的三个圆弧的半径（和View宽度的百分比）
    private float arcR1 = 0.75f;
    private float arcR2 = 0.7f;
    private float arcR3 = 0.34f;
    //圆弧的长度起点
    private int arcStart = -135;
    //圆弧的长度终点
    private int arcEnd = 90;
    //圆弧的宽度
    private float arcZ = 30;
    private float arcZa = 0.04f;
    //旋转速度
    private static float ROTATE_SPEED = 1.0f;

    private boolean initFinish = false;

    //在圆周排放的imageView的轨迹坐标
    private PathMeasure pathMeasure;
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
        arcZ = getWidth() * arcZa;
        if (arcZ < dpToPx(5)) {
            arcZ = dpToPx(5);
        }
        if (getChildCount() >= 1) {
            View view = getChildAt(0);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            //Log.d("TAG", "init: params.width=" + params.width + " params.height=" + params.height);
            params.width = (int) (getWidth() * arcR3 - (arcZ) * 3);
            params.height = (int) (getHeight() * arcR3 - (arcZ) * 3);
            //Log.d("TAG", "init: params.width=" + params.width + " params.height=" + params.height);
            requestLayout();

            RotateAnimation mRotateAnimation =
                    new RotateAnimation(0, 360, Animation.ABSOLUTE, (getWidth() * arcR3 - (arcZ) * 3) /2,
                            Animation.ABSOLUTE, (getWidth() * arcR3 - (arcZ) * 3) / 2);
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

    Path orbit;

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
            orbit.addCircle(canvas.getWidth() / 2,
                    canvas.getHeight() / 2,
                    canvas.getWidth() / 2 * arcR2,
                    Path.Direction.CW);

            pathMeasure = new PathMeasure(orbit, false);

            Random random = new Random();
            int start = Math.abs(random.nextInt() % (int) pathMeasure.getLength());
            int position = 0, i = 0;
            for (View view : cViewList) {
                if (position < pathMeasure.getLength() - (arcZ * 3.6f)) {
                    float[] coords = new float[]{0f, 0f};
                    pathMeasure.getPosTan((start + position) % (int) pathMeasure.getLength(),
                            coords, null);

                    view.setX((int) coords[0] - (arcZ * 3.6f) / 2 + i * dpToPx(5));
                    view.setY((int) coords[1] - (arcZ * 3.6f) / 2 + i * dpToPx(5));
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.width = (int) (arcZ * 3.6f);
                    params.height = (int) (arcZ * 3.6f);
                    //Log.d("TAG", "init: params.width=" + params.width + " params.height=" + params.height);
                } else {
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.width = 0;
                    params.height = 0;
                }

                position += (arcZ * 3.6f) * 3;
                i++;
                i = i > 3 ? 0 : i;
            }
            requestLayout();
        }

//        if (bitmapArc1 == null) {
//            float width = canvas.getWidth() * arcR1;
//            float height = canvas.getHeight() * arcR1;
//            float stokeW = dpToPx(2);
//            bitmapArc1 = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
//            Canvas temp = new Canvas(bitmapArc1);
//            Paint paint = new Paint();
//            paint.setStyle(Paint.Style.STROKE); //设置空心
//            paint.setStrokeWidth(dpToPx(2)); //设置圆环的宽度
//            paint.setAntiAlias(true);  //消除锯齿
//            paint.setColor(backgroundColorGray);
//            temp.drawArc(new RectF(stokeW, stokeW, width - stokeW, height - stokeW),
//                    0, 360, false, paint);
//            paint.setColor(backgroundColorPrimy);
//            temp.drawArc(new RectF(stokeW, stokeW, width - stokeW, height - stokeW),
//                    arcStart, arcEnd * 2, false, paint);
//        }
//
//        if (bitmapArc2 == null) {
//            float width = canvas.getWidth() * arcR2;
//            float height = canvas.getHeight() * arcR2;
//            float stokeW = arcZ;
//            bitmapArc2 = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
//            Canvas temp = new Canvas(bitmapArc2);
//            Paint paint = new Paint();
//            paint.setStyle(Paint.Style.STROKE); //设置空心
//            paint.setStrokeWidth(arcZ); //设置圆环的宽度
//            paint.setAntiAlias(true);  //消除锯齿
//            paint.setColor(backgroundColorGray);
//            temp.drawArc(new RectF(stokeW, stokeW, width - stokeW, height - stokeW),
//                    0, 360, false, paint);
//            paint.setColor(backgroundColor);
//            temp.drawArc(new RectF(stokeW, stokeW, width - stokeW, height - stokeW),
//                    arcStart, arcEnd, false, paint);
//        }
//
//        if (bitmapArc3 == null) {
//            float width = canvas.getWidth() * arcR3;
//            float height = canvas.getHeight() * arcR3;
//            float stokeW = arcZ;
//            bitmapArc3 = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
//            Canvas temp = new Canvas(bitmapArc3);
//            Paint paint = new Paint();
//            paint.setStyle(Paint.Style.STROKE); //设置空心
//            paint.setStrokeWidth(arcZ); //设置圆环的宽度
//            paint.setAntiAlias(true);  //消除锯齿
//            paint.setColor(backgroundColorGray);
//            temp.drawArc(new RectF(stokeW, stokeW, width - stokeW, height - stokeW),
//                    0, 360, false, paint);
//            paint.setColor(backgroundColor);
//            temp.drawArc(new RectF(stokeW, stokeW, width - stokeW, height - stokeW),
//                    arcStart, arcEnd, false, paint);
//        }
//
//
//        input += ROTATE_SPEED;
//        input = input % 360;
//        rotateAngle = getInterpolation(input / 360) * 360;
        //Log.d("progress", "drawFrame: input1= "+ input / 360 + " rotation="+ rotateAngle / 360 + "    input= "+ input + " rotation="+ rotateAngle);

//        Paint bpPaint = new Paint();
//        bpPaint.setAntiAlias(true);
//        if (bitmapArc1 != null && !bitmapArc1.isRecycled()) {
//            canvas.save();
//            canvas.rotate(-rotateAngle, getWidth() / 2, getHeight() / 2);
//            canvas.drawBitmap(bitmapArc1, getWidth() * (1 - arcR1) / 2,
//                    getHeight() * (1 - arcR1) / 2, bpPaint);
//            canvas.restore();
//        }
//
//        if (bitmapArc2 != null && !bitmapArc2.isRecycled()) {
//            canvas.save();
//            canvas.rotate(rotateAngle, getWidth() / 2, getHeight() / 2);
//            canvas.drawBitmap(bitmapArc2, getWidth() * (1 - arcR2) / 2,
//                    getHeight() * (1 - arcR2) / 2, bpPaint);
//            canvas.restore();
//        }
//
//        if (bitmapArc3 != null && !bitmapArc3.isRecycled()) {
//            canvas.save();
//            canvas.rotate(-rotateAngle, getWidth() / 2, getHeight() / 2);
//            canvas.drawBitmap(bitmapArc3, getWidth() * (1 - arcR3) / 2,
//                    getWidth() * (1 - arcR3) / 2, bpPaint);
//            canvas.restore();
//        }

        input += ROTATE_SPEED;
        input = input % 360;
        float rotateAngle = getInterpolation(input / 360);

        float input2 = (input + 180) % 360;
        float rotateAngle2 = getInterpolation(input2 / 360);

        drawCircle(canvas, rotateAngle);
        drawCircle(canvas, rotateAngle2);
    }

    static Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成bitmap
    {
        int width = drawable.getIntrinsicWidth();// 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE
                ? Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;// 取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);// 把drawable内容画到画布中
        return bitmap;
    }

    private void drawCircle(Canvas canvas, float time) {
        float width = canvas.getWidth();
        float radius = (time) * (width / 2);
        Log.d("ben", "draw: time =  " + time + "  radius = " + radius);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        int color = Color.argb((int) ((1 - time) * 255), 225, 112, 79);
        paint.setColor(color);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, radius, paint);
    }

    private float input = 0f;

    private float getInterpolation(float input) {
        if (input < 0.24f) {
            return input * 0.55f;
        } else if (input > 0.76f) {
            return (input - 0.76f) * 0.55f + (float) (Math.cos((0.76f + 1) * Math.PI) / 2.0f) + 0.5f;
        } else {
            return (float) (Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
        }
        //return (float) (Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
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
                if (bitmapArc1 != null && !bitmapArc1.isRecycled()) {
                    bitmapArc1.recycle();
                    bitmapArc1 = null;
                }

                if (bitmapArc2 != null && !bitmapArc2.isRecycled()) {
                    bitmapArc2.recycle();
                    bitmapArc2 = null;
                }

                if (bitmapArc3 != null && !bitmapArc3.isRecycled()) {
                    bitmapArc3.recycle();
                    bitmapArc3 = null;
                }

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
