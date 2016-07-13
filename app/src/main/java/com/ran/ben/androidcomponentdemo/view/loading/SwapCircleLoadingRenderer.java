package com.ran.ben.androidcomponentdemo.view.loading;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class SwapCircleLoadingRenderer extends LoadingRenderer {

    private static boolean DEBUG = true;
    private static final Interpolator MATERIAL_INTERPOLATOR = new LinearInterpolator();

    private static final long ANIMATION_DURATION = 2000;

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

    private static final int RED_COLOR = Color.parseColor("#ff6459");
    private static final int YELLOW_COLOR = Color.parseColor("#fc8a8a");

    private float mSwapXOffsetProgress;

    public SwapCircleLoadingRenderer(Context context) {
        super(context);

        if (DEBUG) {
            setDuration(20000);
        } else {
            setDuration(ANIMATION_DURATION);
        }
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

        float circleRadius = computeCircleRadius(arcBounds);
        float circleRadius1 = circleRadius;
        float circleRadius2 = circleRadius;
        float circleRadius3 = circleRadius / 2;

        Path path = null;
        int mix = 0;

        double distance1 = distance(mFirstBallPosition[0], mFirstBallPosition[1],
                mThirdBallPosition[0],  mThirdBallPosition[1]);
        double distance2 = distance(mSecondBallPosition[0], mSecondBallPosition[1],
                mThirdBallPosition[0],  mThirdBallPosition[1]);
        if (distance1 < circleRadius - circleRadius3) {
            mix = 1;
            circleRadius3 = circleRadius;
            circleRadius1 = (circleRadius + circleRadius3 / 4);
            float offset =  90;
            path = drawAdhesionBody(mFirstBallPosition[0], mFirstBallPosition[1], circleRadius1, offset,
                    mThirdBallPosition[0], mThirdBallPosition[1], circleRadius3, offset);
        } else if (distance1 <  circleRadius + circleRadius3) {
            mix = 2;
            float offset = 45;
            circleRadius3 += (circleRadius - circleRadius3) *
                    ((circleRadius + circleRadius3 - distance1) / (2 * circleRadius3));
            circleRadius1 = (circleRadius + circleRadius3 / 4 * (1.0f - (float) distance1 / (2*  circleRadius3)));
            offset += 45 * ((circleRadius + circleRadius3 - distance1) / (2 * circleRadius3));
            path = drawAdhesionBody(mFirstBallPosition[0], mFirstBallPosition[1], circleRadius1, offset,
                    mThirdBallPosition[0], mThirdBallPosition[1], circleRadius3, offset);
        } else  if (distance1 < circleRadius * 2) {
            path = drawAdhesionBody(mFirstBallPosition[0], mFirstBallPosition[1], circleRadius1, 45,
                    mThirdBallPosition[0], mThirdBallPosition[1], circleRadius3, 45);
        }  else if (distance2 < circleRadius - circleRadius3) {
            mix = 3;
            circleRadius3 = circleRadius;
            circleRadius2 = (circleRadius + circleRadius3 / 4);
            float offset =  90;
            path = drawAdhesionBody(mSecondBallPosition[0], mSecondBallPosition[1], circleRadius2, offset,
                    mThirdBallPosition[0], mThirdBallPosition[1], circleRadius3, offset);
        }else if (distance2 < circleRadius + circleRadius3) {
            mix = 4;
            float offset = 45;
            circleRadius3 += (circleRadius - circleRadius3) *
                    ((circleRadius + circleRadius3 - distance2) / (2 * circleRadius3));
            circleRadius2 = (circleRadius + circleRadius3 / 4 * (1.0f - (float) distance2 / (2 * circleRadius3)));
            offset += 45 * ((circleRadius + circleRadius3 - distance2) / (2 * circleRadius3));
            path = drawAdhesionBody(mSecondBallPosition[0], mSecondBallPosition[1], circleRadius2, offset,
                    mThirdBallPosition[0], mThirdBallPosition[1], circleRadius3, offset);
        }else if (distance2 < circleRadius * 2){
            path = drawAdhesionBody(mSecondBallPosition[0], mSecondBallPosition[1], circleRadius2, 45,
                    mThirdBallPosition[0], mThirdBallPosition[1], circleRadius3, 45);
        }

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mix  == 1 ? YELLOW_COLOR :  RED_COLOR);
        canvas.drawCircle(mFirstBallPosition[0]
                , mFirstBallPosition[1],
                circleRadius1, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mix  == 3 ? YELLOW_COLOR :  RED_COLOR);
        canvas.drawCircle(mSecondBallPosition[0]
                , mSecondBallPosition[1],
                circleRadius2, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(YELLOW_COLOR);
        canvas.drawCircle(mThirdBallPosition[0]
                , mThirdBallPosition[1], circleRadius3, mPaint);

        if  (path != null) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(YELLOW_COLOR);
            canvas.drawPath(path, mPaint);
        }

        canvas.restoreToCount(saveCount);

    }

    private double distance(float x1, float y1,  float x2,  float y2) {
        return Math.sqrt(Math.abs(x1-x2)*Math.abs(x1-x2)+Math.abs(y1-y2)*Math.abs(y1-y2));
    }


    private float computeCircleRadius(RectF rectBounds) {
        float width = rectBounds.width();
        float height = rectBounds.height();

        float radius = Math.min(width / 8, height / 8);
        return radius;
    }

    private PathMeasure mPathMeasure;
    private final float[] mFirstBallPosition = new float[2];
    private final float[] mSecondBallPosition = new float[2];
    private final float[] mThirdBallPosition = new float[2];

    @Override
    public void computeRender(float renderProgress) {
        mSwapXOffsetProgress = MATERIAL_INTERPOLATOR.getInterpolation(
                (renderProgress));

        if (mPathMeasure == null) {
            mPathMeasure = new PathMeasure(createSkipBallPath(), false);
        }

        mPathMeasure.getPosTan(renderProgress * mPathMeasure.getLength(), mFirstBallPosition, null);
        float progress = 0.0f;
        if (DEBUG) {
            progress = renderProgress + 0.2f > 1f ? renderProgress - 0.2f : renderProgress + 0.2f;
        } else {
            progress = renderProgress + 0.5f > 1f ? renderProgress - 0.5f : renderProgress + 0.5f;
        }
        mPathMeasure.getPosTan(progress * mPathMeasure.getLength(), mSecondBallPosition, null);
        progress = renderProgress * 2 >  1f  ?  renderProgress * 2 - 1.0f :  renderProgress * 2;
        mPathMeasure.getPosTan(progress * mPathMeasure.getLength(), mThirdBallPosition, null);

        invalidateSelf();
    }

    private Path createSkipBallPath() {
        Path path = new Path();
        path.addCircle(mWidth / 2 , mHeight / 2, mWidth / 2 - Math.min(mWidth / 8, mHeight / 8) * 2, Path.Direction.CW);

        return path;
    }


    /**
     * 画粘连体
     * @param cx1     圆心x1
     * @param cy1     圆心y1
     * @param r1      圆半径r1
     * @param offset1 贝塞尔曲线偏移角度offset1
     * @param cx2     圆心x2
     * @param cy2     圆心y2
     * @param r2      圆半径r2
     * @param offset2 贝塞尔曲线偏移角度offset2
     * @return
     */
    public static Path drawAdhesionBody(float cx1, float cy1, float r1, float offset1, float
            cx2, float cy2, float r2, float offset2) {

    /* 求三角函数 */
        float degrees =(float) Math.toDegrees(Math.atan(Math.abs(cy2 - cy1) / Math.abs(cx2 - cx1)));

    /* 根据圆1与圆2的相对位置求四个点 */
        float differenceX = cx1 - cx2;
        float differenceY = cy1 - cy2;

    /* 两条贝塞尔曲线的四个端点 */
        float x1,y1,x2,y2,x3,y3,x4,y4;

    /* 圆1在圆2的下边 */
        if (differenceX == 0 && differenceY > 0) {
            x2 = cx2 - r2 * (float) Math.sin(Math.toRadians(offset2));
            y2 = cy2 + r2 * (float) Math.cos(Math.toRadians(offset2));
            x4 = cx2 + r2 * (float) Math.sin(Math.toRadians(offset2));
            y4 = cy2 + r2 * (float) Math.cos(Math.toRadians(offset2));
            x1 = cx1 - r1 * (float) Math.sin(Math.toRadians(offset1));
            y1 = cy1 - r1 * (float) Math.cos(Math.toRadians(offset1));
            x3 = cx1 + r1 * (float) Math.sin(Math.toRadians(offset1));
            y3 = cy1 - r1 * (float) Math.cos(Math.toRadians(offset1));
        }
    /* 圆1在圆2的上边 */
        else if (differenceX == 0 && differenceY < 0) {
            x2 = cx2 - r2 * (float) Math.sin(Math.toRadians(offset2));
            y2 = cy2 - r2 * (float) Math.cos(Math.toRadians(offset2));
            x4 = cx2 + r2 * (float) Math.sin(Math.toRadians(offset2));
            y4 = cy2 - r2 * (float) Math.cos(Math.toRadians(offset2));
            x1 = cx1 - r1 * (float) Math.sin(Math.toRadians(offset1));
            y1 = cy1 + r1 * (float) Math.cos(Math.toRadians(offset1));
            x3 = cx1 + r1 * (float) Math.sin(Math.toRadians(offset1));
            y3 = cy1 + r1 * (float) Math.cos(Math.toRadians(offset1));
        }
    /* 圆1在圆2的右边 */
        else if (differenceX > 0 && differenceY == 0) {
            x2 = cx2 + r2 * (float) Math.cos(Math.toRadians(offset2));
            y2 = cy2 + r2 * (float) Math.sin(Math.toRadians(offset2));
            x4 = cx2 + r2 * (float) Math.cos(Math.toRadians(offset2));
            y4 = cy2 - r2 * (float) Math.sin(Math.toRadians(offset2));
            x1 = cx1 - r1 * (float) Math.cos(Math.toRadians(offset1));
            y1 = cy1 + r1 * (float) Math.sin(Math.toRadians(offset1));
            x3 = cx1 - r1 * (float) Math.cos(Math.toRadians(offset1));
            y3 = cy1 - r1 * (float) Math.sin(Math.toRadians(offset1));
        }
    /* 圆1在圆2的左边 */
        else if (differenceX < 0 && differenceY == 0 ) {
            x2 = cx2 - r2 * (float) Math.cos(Math.toRadians(offset2));
            y2 = cy2 + r2 * (float) Math.sin(Math.toRadians(offset2));
            x4 = cx2 - r2 * (float) Math.cos(Math.toRadians(offset2));
            y4 = cy2 - r2 * (float) Math.sin(Math.toRadians(offset2));
            x1 = cx1 + r1 * (float) Math.cos(Math.toRadians(offset1));
            y1 = cy1 + r1 * (float) Math.sin(Math.toRadians(offset1));
            x3 = cx1 + r1 * (float) Math.cos(Math.toRadians(offset1));
            y3 = cy1 - r1 * (float) Math.sin(Math.toRadians(offset1));
        }
    /* 圆1在圆2的右下角 */
        else if (differenceX > 0 && differenceY > 0) {
            x2 = cx2 - r2 * (float) Math.cos(Math.toRadians(180 - offset2 - degrees));
            y2 = cy2 + r2 * (float) Math.sin(Math.toRadians(180 - offset2 - degrees));
            x4 = cx2 + r2 * (float) Math.cos(Math.toRadians(degrees - offset2));
            y4 = cy2 + r2 * (float) Math.sin(Math.toRadians(degrees - offset2));
            x1 = cx1 - r1 * (float) Math.cos(Math.toRadians(degrees - offset1));
            y1 = cy1 - r1 * (float) Math.sin(Math.toRadians(degrees - offset1));
            x3 = cx1 + r1 * (float) Math.cos(Math.toRadians(180 - offset1 - degrees));
            y3 = cy1 - r1 * (float) Math.sin(Math.toRadians(180 - offset1 - degrees));
        }
    /* 圆1在圆2的左上角 */
        else if (differenceX < 0 && differenceY < 0) {
            x2 = cx2 - r2 * (float) Math.cos(Math.toRadians(degrees - offset2));
            y2 = cy2 - r2 * (float) Math.sin(Math.toRadians(degrees - offset2));
            x4 = cx2 + r2 * (float) Math.cos(Math.toRadians(180 - offset2 - degrees));
            y4 = cy2 - r2 * (float) Math.sin(Math.toRadians(180 - offset2 - degrees));
            x1 = cx1 - r1 * (float) Math.cos(Math.toRadians(180 - offset1 - degrees));
            y1 = cy1 + r1 * (float) Math.sin(Math.toRadians(180 - offset1 - degrees));
            x3 = cx1 + r1 * (float) Math.cos(Math.toRadians(degrees - offset1));
            y3 = cy1 + r1 * (float) Math.sin(Math.toRadians(degrees - offset1));
        }
    /* 圆1在圆2的左下角 */
        else if (differenceX < 0 && differenceY > 0) {
            x2 = cx2 - r2 * (float) Math.cos(Math.toRadians(degrees - offset2));
            y2 = cy2 + r2 * (float) Math.sin(Math.toRadians(degrees - offset2));
            x4 = cx2 + r2 * (float) Math.cos(Math.toRadians(180 - offset2 - degrees));
            y4 = cy2 + r2 * (float) Math.sin(Math.toRadians(180 - offset2 - degrees));
            x1 = cx1 - r1 * (float) Math.cos(Math.toRadians(180 - offset1 - degrees));
            y1 = cy1 - r1 * (float) Math.sin(Math.toRadians(180 - offset1 - degrees));
            x3 = cx1 + r1 * (float) Math.cos(Math.toRadians(degrees - offset1));
            y3 = cy1 - r1 * (float) Math.sin(Math.toRadians(degrees - offset1));
        }
    /* 圆1在圆2的右上角 */
        else {
            x2 = cx2 - r2 * (float) Math.cos(Math.toRadians(180 - offset2 - degrees));
            y2 = cy2 - r2 * (float) Math.sin(Math.toRadians(180 - offset2 - degrees));
            x4 = cx2 + r2 * (float) Math.cos(Math.toRadians(degrees - offset2));
            y4 = cy2 - r2 * (float) Math.sin(Math.toRadians(degrees - offset2));
            x1 = cx1 - r1 * (float) Math.cos(Math.toRadians(degrees - offset1));
            y1 = cy1 + r1* (float) Math.sin(Math.toRadians(degrees - offset1));
            x3 = cx1 + r1 * (float) Math.cos(Math.toRadians(180 - offset1 - degrees));
            y3 = cy1 + r1 * (float) Math.sin(Math.toRadians(180 - offset1 - degrees));
        }

    /* 贝塞尔曲线的控制点 */
        float anchorX1,anchorY1,anchorX2,anchorY2;

    /* 圆1大于圆2 */
        if (r1 > r2) {
            anchorX1 = (x2 + x3) / 2;
            anchorY1 = (y2 + y3) / 2;
            anchorX2 = (x1 + x4) / 2;
            anchorY2 = (y1 + y4) / 2;
        }
    /* 圆1小于或等于圆2 */
        else {
            anchorX1 = (x1 + x4) / 2;
            anchorY1 = (y1 + y4) / 2;
            anchorX2 = (x2 + x3) / 2;
            anchorY2 = (y2 + y3) / 2;
        }

    /* 画粘连体 */
        Path path = new Path();
        path.reset();
        path.moveTo(x1, y1);
        path.quadTo(anchorX1, anchorY1, x2, y2);
        path.lineTo(x4, y4);
        path.quadTo(anchorX2, anchorY2, x3, y3);
        path.lineTo(x1, y1);
        return path;
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
