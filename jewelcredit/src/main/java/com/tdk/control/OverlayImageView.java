package com.tdk.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import cn.vpfinance.android.R;

/**
 * Created by Administrator on 2015/8/19.
 */
public class OverlayImageView extends View {

    private Drawable maskImage;

    private Drawable bgLayerImage;

    private int offsetMode;

    private int offset;

    private static final int MODE_LEFT = 0;
    private static final int MODE_TOP = 1;
    private static final int MODE_RIGHT = 2;
    private static final int MODE_BOTTOM = 3;

    private static final int STEP_PROGRESS = 2;
    private static final long TIME_ATOM = 50;

    private Runnable animator = new Runnable() {
        @Override
        public void run() {
            if (offset < 100 && offset + STEP_PROGRESS > 100) {
                setOffset(100);
            } else if (offset == 100) {
                setOffset(0);
            } else {
                setOffset(offset + STEP_PROGRESS);
            }
            postDelayed(animator, TIME_ATOM);
        }
    };

    public OverlayImageView(Context context) {
        super(context);
    }

    public OverlayImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        construct(context, attrs);
    }

    public OverlayImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        construct(context, attrs);
    }

    private void construct(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OverlayImageView);
        offsetMode = a.getInteger(R.styleable.OverlayImageView_offsetMode, MODE_TOP);
        bgLayerImage = a.getDrawable(R.styleable.OverlayImageView_bgLayerImage);
        maskImage = a.getDrawable(R.styleable.OverlayImageView_maskImage);
        offset = a.getInteger(R.styleable.OverlayImageView_offset, 0);
        a.recycle();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(animator);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        bgLayerImage.setBounds(0, 0, bgLayerImage.getIntrinsicWidth(), bgLayerImage.getIntrinsicHeight());
        bgLayerImage.draw(canvas);

        drawMaskImage(canvas);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        startAnimation();
        restart();
    }

    public void startAnimation() {
        post(animator);
    }

    private void drawMaskImage(Canvas canvas) {
        switch (offsetMode) {
            case MODE_LEFT:
            case MODE_RIGHT:
                int w = (int) (maskImage.getIntrinsicWidth() * offset / 100f);
                if (w == 0) {
                    return;
                }
                break;

            case MODE_TOP:
            case MODE_BOTTOM:
                int h = (int) (maskImage.getIntrinsicHeight() * offset / 100f);
                if (h == 0) {
                    return;
                }
                break;
        }

        Bitmap tmp = drawableToBitmap(maskImage);
        switch (offsetMode) {
            case MODE_LEFT:
                tmp = Bitmap.createBitmap(tmp, 0, 0, (int)(maskImage.getIntrinsicWidth() * offset / 100f), maskImage.getIntrinsicHeight());
                canvas.drawBitmap(tmp, 0, 0, null);
                break;

            case MODE_RIGHT:
                tmp = Bitmap.createBitmap(tmp, (int)(maskImage.getIntrinsicWidth() * (1 - offset / 100f)), 0,
                        (int)(maskImage.getIntrinsicWidth() * offset / 100f), maskImage.getIntrinsicHeight());
                canvas.drawBitmap(tmp, (int)(maskImage.getIntrinsicWidth() * (1 - offset / 100f)), 0, null);
                break;

            case MODE_TOP:
                tmp = Bitmap.createBitmap(tmp, 0, 0, maskImage.getIntrinsicWidth(), (int)(maskImage.getIntrinsicHeight()  * offset / 100f));
                canvas.drawBitmap(tmp, 0, 0, null);
                break;

            case MODE_BOTTOM:
                tmp = Bitmap.createBitmap(tmp, 0, (int)(maskImage.getIntrinsicHeight()  * (1 - offset / 100f)),
                        (int)(maskImage.getIntrinsicWidth()), (int)(maskImage.getIntrinsicHeight()  * offset / 100f));
                canvas.drawBitmap(tmp, 0, (int) (maskImage.getIntrinsicHeight() * (1 - offset / 100f)), null);
                break;

            default:
                throw new RuntimeException();
        }
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        if (offset < 0) {
            offset = 0;
        } else if (offset > 100) {
            offset = 100;
        }

        this.offset = offset;
        invalidate();
    }

    public void restart() {
        this.offset = 0;
        post(animator);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
            setMeasuredDimension(bgLayerImage.getIntrinsicWidth(), bgLayerImage.getIntrinsicHeight());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
