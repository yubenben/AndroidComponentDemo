package com.ran.ben.androidcomponentdemo.view.loading;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

public class SwapCircleLoadingRenderer extends LoadingRenderer {
    private static final Interpolator MATERIAL_INTERPOLATOR = new AccelerateDecelerateInterpolator();

    private static final long ANIMATION_DURATION = 1500;

    private static final int CIRCLE_COUNT = 2;

    //(CIRCLE_COUNT - 1) / 2 is the Circle interval width; the 2 * 2 is the both side inset
    private static final float DEFAULT_WIDTH = 15.0f * (CIRCLE_COUNT + (CIRCLE_COUNT - 1) / 2 + 2 * 2);
    //the 2 * 2 is the both side inset
    private static final float DEFAULT_HEIGHT = 15.0f * (1 + 2 * 2);
    private static final float DEFAULT_STROKE_WIDTH = 1.5f;

    private static final int DEFAULT_COLOR = Color.WHITE;
    private static final String TAG = "Renderer";

    private final Paint mPaint = new Paint();
    private final RectF mTempBounds = new RectF();

    private int mColor;

    private float lastProgress;
    private float mSwapXOffsetProgress;
    boolean isReverse = false;

    private float mSwapXOffsetProgress2;

    public SwapCircleLoadingRenderer(Context context) {
        super(context);

        setDuration(ANIMATION_DURATION);
        init(context);
        setupPaint();
    }

    private void init(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_WIDTH, displayMetrics);
        mHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_HEIGHT, displayMetrics);
        mStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_STROKE_WIDTH, displayMetrics);
    }

    private void setupPaint() {
        mColor = DEFAULT_COLOR;

        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(getStrokeWidth());
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas, Rect bounds) {
        mPaint.setColor(mColor);

        int saveCount = canvas.save();

        RectF arcBounds = mTempBounds;
        arcBounds.set(bounds);

        float cy = mHeight / 2 ;
        float circleRadius = computeCircleRadius(arcBounds);

        float circleDiameter = bounds.width() - 2 * circleRadius;

        Log.d(TAG, "draw: mSwapXOffsetProgress = " + mSwapXOffsetProgress);

        if  (lastProgress > mSwapXOffsetProgress) {
            isReverse = !isReverse;
        }

        lastProgress = mSwapXOffsetProgress;

        float xMoveOffset = mSwapXOffsetProgress * (circleDiameter);
        float xCoordinate = isReverse ? xMoveOffset : circleDiameter - xMoveOffset;
        float yMoveOffset = (float) (Math.sqrt(Math.pow(circleDiameter / 2, 2.0f) - Math.pow(circleDiameter / 2 - xCoordinate, 2.0f)));

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        canvas.drawCircle(circleRadius + xCoordinate
                , isReverse ? cy - yMoveOffset : cy + yMoveOffset, circleRadius - getStrokeWidth() / 2, mPaint);


        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.YELLOW);
        canvas.drawCircle(circleDiameter -  xCoordinate + circleRadius
                , isReverse ? cy + yMoveOffset : cy - yMoveOffset, circleRadius - getStrokeWidth() / 2, mPaint);

//        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setColor(Color.YELLOW);
//        canvas.drawCircle(circleRadius + xCoordinate * 2
//                , isReverse ? cy - yMoveOffset : cy + yMoveOffset, (circleRadius - getStrokeWidth() / 2) / 2, mPaint);

        float xMoveOffsetz = mSwapXOffsetProgress2 * (circleDiameter);
        float xCoordinatez = isReverse ? xMoveOffsetz : circleDiameter - xMoveOffsetz;
        float yMoveOffsetz = (float) (Math.sqrt(Math.pow(circleDiameter / 2, 2.0f) - Math.pow(circleDiameter / 2 - xCoordinate, 2.0f)));

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(circleRadius + xCoordinatez
                , isReverse ? cy - yMoveOffsetz : cy + yMoveOffsetz, circleRadius - getStrokeWidth() / 2, mPaint);


        canvas.restoreToCount(saveCount);
    }

    private float computeCircleRadius(RectF rectBounds) {
        float width = rectBounds.width();
        float height = rectBounds.height();

        float radius = Math.min(width / 5, height / 5);
        return radius;
    }

    @Override
    public void computeRender(float renderProgress) {
        mSwapXOffsetProgress = MATERIAL_INTERPOLATOR.getInterpolation(
                (renderProgress));

        mSwapXOffsetProgress2 = MATERIAL_INTERPOLATOR.getInterpolation(
                (renderProgress * 2 > 1.0f ? renderProgress * 2 - 1 : renderProgress));

        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public void reset() {
    }

    public void setColor(int color) {
        mColor = color;
    }

    @Override
    public void setStrokeWidth(float strokeWidth) {
        super.setStrokeWidth(strokeWidth);
        mPaint.setStrokeWidth(strokeWidth);
        invalidateSelf();
    }
}
