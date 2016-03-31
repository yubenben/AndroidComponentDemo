package com.ran.ben.androidcomponentdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.ran.ben.androidcomponentdemo.utils.DensityUtil;


public class ProgressBarCircular extends RelativeLayout {


    final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";

    int backgroundColor = Color.parseColor("#1E88E5");


    private View child1;
    private View child2;

    public ProgressBarCircular(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
    }

    // Set atributtes of XML to View
    protected void setAttributes(AttributeSet attrs) {

        setMinimumHeight(dpToPx(32));

        setMinimumWidth(dpToPx(32));

        //Set background Color
        // Color by resource
        int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML, "background", -1);
        if (bacgroundColor != -1) {
            setBackgroundColor(getResources().getColor(bacgroundColor));
        } else {
            // Color by hexadecimal
            int background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
            if (background != -1)
                setBackgroundColor(background);
            else
                setBackgroundColor(Color.parseColor("#1E88E5"));
        }

        setMinimumHeight(dpToPx(3));
    }

    private void init() {
        if (getChildCount() >= 2) {
            child1 = getChildAt(0);
            child2 = getChildAt(1);
        }
    }

    private int dpToPx(float dp) {
        return DensityUtil.dip2px(getContext(), dp);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSecondAnimation(canvas);
        invalidate();

    }


    private int arcO = -135;
    private int arcD = 90;
    private float rotateAngle = 0;

    private float r1 = 1;
    private float r2 = 0.98f;
    private float r3 = 0.5f;
    private float arcZ = 10;

    int x1;
    int x2;

    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmap3;

    Path orbit;
    PathMeasure measure;

    /**
     * Draw second animation of view
     *
     * @param canvas
     */
    private void drawSecondAnimation(Canvas canvas) {

        if (orbit == null) {
            init();
            orbit = new Path();
            orbit.addCircle(canvas.getWidth() / 2,
                    canvas.getHeight() / 2,
                    canvas.getWidth() / 2 - dpToPx(30),
                    Path.Direction.CW);

            measure = new PathMeasure(orbit, false);

        }

        x1+=10;
        x1 = x1 % (int)measure.getLength();
        x2 = (x1 + 150) % (int)measure.getLength();
        float[] coords1 = new float[] {0f, 0f};
        measure.getPosTan(x1, coords1, null);
        child1.setX(coords1[0] - dpToPx(50) / 2);
        child1.setY(coords1[1] - dpToPx(50) / 2);

        float[] coords2 = new float[] {0f, 0f};
        measure.getPosTan(x2, coords2, null);
        child2.setX(coords2[0] - dpToPx(50) / 2);
        child2.setY(coords2[1] - dpToPx(50) / 2);


        if (bitmap1 == null) {
            float width = canvas.getWidth() * r1;
            float height = canvas.getHeight() * r1;
            bitmap1 = Bitmap.createBitmap((int) width + 1, (int) height + 1, Bitmap.Config.ARGB_8888);
            Canvas temp = new Canvas(bitmap1);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(0xffe6e6e6);
            temp.drawArc(new RectF(0, 0, width, height), 0, 360, true, paint);
            paint.setColor(backgroundColor);
            temp.drawArc(new RectF(0, 0, width, height), arcO, arcD * 2, true, paint);
            Paint transparentPaint = new Paint();
            transparentPaint.setAntiAlias(true);
            transparentPaint.setColor(getResources().getColor(android.R.color.holo_blue_bright));
            transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            temp.drawCircle(width / 2, height / 2, (width / 2) - dpToPx(1), transparentPaint);
        }

        if (bitmap2 == null) {
            float width = canvas.getWidth() * r2;
            float height = canvas.getHeight() * r2;
            bitmap2 = Bitmap.createBitmap((int) width + 1, (int) height + 1, Bitmap.Config.ARGB_8888);
            Canvas temp = new Canvas(bitmap2);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(0xffe6e6e6);
            temp.drawArc(new RectF(0, 0, width, height), 0, 360, true, paint);
            paint.setColor(backgroundColor);
            temp.drawArc(new RectF(0, 0, width, height), arcO, arcD, true, paint);
            Paint transparentPaint = new Paint();
            transparentPaint.setAntiAlias(true);
            transparentPaint.setColor(getResources().getColor(android.R.color.holo_blue_bright));
            transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            temp.drawCircle(width / 2, height / 2, (width / 2) - dpToPx(arcZ), transparentPaint);
        }

        if (bitmap3 == null) {
            float width = canvas.getWidth() * r3;
            float height = canvas.getHeight() * r3;
            bitmap3 = Bitmap.createBitmap((int) width + 1, (int) height + 1, Bitmap.Config.ARGB_8888);
            Canvas temp = new Canvas(bitmap3);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(0xffe6e6e6);
            temp.drawArc(new RectF(0, 0, width, height), 0, 360, true, paint);
            paint.setColor(backgroundColor);
            temp.drawArc(new RectF(0, 0, width, height), arcO, arcD, true, paint);
            Paint transparentPaint = new Paint();
            transparentPaint.setAntiAlias(true);
            transparentPaint.setColor(getResources().getColor(android.R.color.holo_blue_bright));
            transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            temp.drawCircle(width / 2, height / 2, (width / 2) - dpToPx(arcZ), transparentPaint);
        }

        if (rotateAngle > 45 && rotateAngle < 225) {
            rotateAngle += 10;
        } else {
            rotateAngle += 5;
        }
        rotateAngle = rotateAngle % 360;

        canvas.save();
        canvas.rotate(-rotateAngle, getWidth() / 2, getHeight() / 2);
        canvas.drawBitmap(bitmap1, getWidth() * (1 - r1) / 2, getHeight() * (1 - r1) / 2, new Paint());
        canvas.restore();

        canvas.save();
        canvas.rotate(rotateAngle, getWidth() / 2, getHeight() / 2);
        canvas.drawBitmap(bitmap2, getWidth() * (1 - r2) / 2, getHeight() * (1 - r2) / 2, new Paint());
        canvas.restore();

        canvas.save();
        canvas.rotate(-rotateAngle, getWidth() / 2, getHeight() / 2);
        canvas.drawBitmap(bitmap3, getWidth() * (1 - r3) / 2, getWidth() * (1 - r3) / 2, new Paint());
        canvas.restore();

//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setColor(getResources().getColor(android.R.color.holo_blue_bright));
        //canvas.drawPath(orbit, paint);
    }


    public void setBackgroundColor(int color) {
        super.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        this.backgroundColor = color;
    }

}
