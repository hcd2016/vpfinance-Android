package cn.vpfinance.vpjr.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by vFISHv on 15/9/23.
 */
public class SmallCircularProgressView extends View {

    private int width;
    private int height;
    private Paint paint;

    int color_background = 0xFFCCCCCC;
    int color_progress = 0xFFCCCCCC;
    int strokeWidth = 6;

    boolean isAnimator = false;
    // from 0 to 100
    float progress = 1;
    float tmpProgress = 0;
    ValueAnimator animation;


    public SmallCircularProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SmallCircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmallCircularProgressView(Context context) {
        super(context);
    }

    public void setStrokeWidth(int width) {
        this.strokeWidth = width;
        postInvalidate();
    }

    public void setProgressColor(int color){
        this.color_progress = color;
    }

    public void isAnimator(boolean b) {
        this.isAnimator = b;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        this.tmpProgress = 0;
        if (animation != null) {
            animation.cancel();
            animation = null;
            initAnimator();
        }
        postInvalidate();
    }

    public void setProgress(String progress) {
        try{
            float v = Float.parseFloat(progress);
            this.progress = v;
            this.tmpProgress = 0;
            if (animation != null) {
                animation.cancel();
                animation = null;
                initAnimator();
            }
            postInvalidate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initAnimator() {
        if (animation == null) {
            animation = ValueAnimator.ofFloat(0f, progress);
            animation.setDuration(isAnimator ? 1000 : 0);
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    tmpProgress = (Float) animation.getAnimatedValue();
                }
            });
            animation.setInterpolator(new LinearInterpolator());
            animation.start();
        }
    }

    private void initPaint() {
        if (paint == null) {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            paint.setStrokeWidth(strokeWidth);
            paint.setStyle(Paint.Style.STROKE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        initPaint();
        initAnimator();

        int radius = Math.min(width, height) / 2;
        radius -= 20;

        paint.setColor(color_background);
        int centerX = width / 2;
        int centerY = height / 2;
        canvas.drawCircle(centerX, centerY, radius, paint);


        paint.setColor(color_progress);
        float left = width * 0.5f - radius;
        float top = height * 0.5f - radius;
        float right = width * 0.5f + radius;
        float bottom = height * 0.5f + radius;
        float startAngle = -90;
        float sweepAngle = tmpProgress * 0.01f * 360;
        boolean useCenter = false;
        //canvas.drawArc(left,top,right,bottom,startAngle,sweepAngle,useCenter,paint);
        RectF oval = new RectF(left, top, right, bottom);
        canvas.drawArc(oval, startAngle, sweepAngle, useCenter, paint);

        if (tmpProgress != progress) {
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }
}
