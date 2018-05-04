package com.jewelcredit.ui.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.ViewConfiguration;

/**
 * Created by Administrator on 2015/7/29.
 */

public class PictureZoom extends SurfaceView {

    private static final int DOUBLE_CLICK_DURATION = 200;
    private static final float DOUBLE_CLICK_DISTANCE = 300;
    private static final float DOUBLE_CLICK_SCALE_VALUE = 0.25f;
    private static final float SCALE_MAX_VALUE = 3.1f;
    private static final float SCALE_DIVISOR_VALUE = 4f;

    private int SCALE_WIDTH_MAX = 0;
    private int SCALE_WIDTH_MIN = 0;
    private int SCALE_HEIGHT_MAX = 0;
    private int SCALE_HEIGHT_MIN = 0;

    private float EFFECTIVE_DISTANCE = 0f;

    private enum MODE {NONE, DRAG, ZOOM};

    private enum QUADRANT {ONE, TWO, THREE, FOUR, CENTER};

    private int mScreenWidth = 0;
    private int mScreenHeight = 0;

    private int mBitmapWidth = 0;
    private int mBitmapHeight = 0;

    private Bitmap mBitmap = null;
    private Paint mClearPaint = null;
    private Rect mBitmapRect = null;
    private Rect mBitmapCurrentRect = null;

    // scale
    private MODE mCurrentMode = MODE.NONE;
    private float mPointerCurrentDistance = 0f;

    // drag
    private float mDownX = 0f;
    private float mDownY = 0f;
    private float mCurrentX = 0f;
    private float mCurrentY = 0f;

    private Timer mDoubleClickTimer = null;
    private boolean mIsWaitSecondClick = false;

    public PictureZoom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public PictureZoom(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PictureZoom(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        EFFECTIVE_DISTANCE = ViewConfiguration.get(context).getScaledTouchSlop();
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        mBitmapRect = new Rect();
        mBitmapCurrentRect = new Rect();
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mScreenWidth = MeasureSpec.getSize(widthMeasureSpec);
        mScreenHeight = MeasureSpec.getSize(heightMeasureSpec);
        initRect();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        showScaleBitmap();
    }

    public void setBitmapSize(int bitmapWidth, int bitmapHeight) {
        mBitmapWidth = bitmapWidth;
        mBitmapHeight = bitmapHeight;
        initRect();
    }

    private void initRect(){
        mBitmapRect.left = (mScreenWidth - mBitmapWidth)/2;
        mBitmapRect.top = (mScreenHeight - mBitmapHeight)/2;
        mBitmapRect.right = (mScreenWidth + mBitmapWidth)/2;
        mBitmapRect.bottom = (mScreenHeight + mBitmapHeight)/2;

        mBitmapCurrentRect.left = mBitmapRect.left;
        mBitmapCurrentRect.top = mBitmapRect.top;
        mBitmapCurrentRect.right = mBitmapRect.right;
        mBitmapCurrentRect.bottom = mBitmapRect.bottom;

        SCALE_WIDTH_MAX = (int) (mBitmapRect.width() * SCALE_MAX_VALUE);
        SCALE_WIDTH_MIN = mBitmapRect.width();
        SCALE_HEIGHT_MAX = (int) (mBitmapRect.height() * SCALE_MAX_VALUE);
        SCALE_HEIGHT_MIN = mBitmapRect.height();

        showScaleBitmap();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                onPointerDown(event);
                break;

            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;

            case MotionEvent.ACTION_POINTER_UP:

                break;

            case MotionEvent.ACTION_UP:

                break;
        }

        return true;
    }

    private void onTouchDown(MotionEvent event) {
        mCurrentMode = MODE.DRAG;
        if (false == mIsWaitSecondClick) {
            mDownX = event.getRawX();
            mDownY = event.getRawY();
            mCurrentX = event.getRawX();
            mCurrentY = event.getRawY();

            mIsWaitSecondClick = true;
            if (null == mDoubleClickTimer) {
                mDoubleClickTimer = new Timer();
            }
            mDoubleClickTimer.schedule(new DoubleClickTimerTask(), DOUBLE_CLICK_DURATION);
        } else {
            mIsWaitSecondClick = false;
            mDoubleClickTimer.cancel();
            mDoubleClickTimer = null;
            dealDoubleClickEvent(event);
        }
    }

    private void onPointerDown(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            mCurrentMode = MODE.ZOOM;
            mPointerCurrentDistance = getPointerDistance(event);
        }
    }

    private void onTouchMove(MotionEvent event) {
        if (MODE.DRAG == mCurrentMode) {
            setDrag(event);
        } else if (MODE.ZOOM == mCurrentMode) {
            setScale(event);
        }
    }

    private void setDrag(MotionEvent event) {
        float dx = event.getRawX() - mCurrentX;
        float dy = event.getRawY() - mCurrentY;
        int left = mBitmapCurrentRect.left;
        int right = mBitmapCurrentRect.right;
        int top = mBitmapCurrentRect.top;
        int bottom = mBitmapCurrentRect.bottom;
        mBitmapCurrentRect.left = (int) (mBitmapCurrentRect.left + dx);
        mBitmapCurrentRect.right = (int) (mBitmapCurrentRect.right + dx);
        mBitmapCurrentRect.top = (int) (mBitmapCurrentRect.top + dy);
        mBitmapCurrentRect.bottom = (int) (mBitmapCurrentRect.bottom + dy);
        if (mBitmapCurrentRect.left <= 0 && mBitmapCurrentRect.right >= mScreenWidth) {
            mCurrentX = event.getRawX();
        } else {
            mBitmapCurrentRect.left = left;
            mBitmapCurrentRect.right = right;
        }
        if (mBitmapCurrentRect.top <= 0 && mBitmapCurrentRect.bottom >= mScreenHeight) {
            mCurrentY = event.getRawY();
        } else {
            mBitmapCurrentRect.top = top;
            mBitmapCurrentRect.bottom = bottom;
        }
        showDragBitmap();
    }

    private void setScale(MotionEvent event) {
        float tempDistance = getPointerDistance(event);
        float moveDistance = (tempDistance - mPointerCurrentDistance) / SCALE_DIVISOR_VALUE;
        if (Math.abs(moveDistance) < EFFECTIVE_DISTANCE) {
            return;
        }
        float scaleValue = moveDistance / mPointerCurrentDistance;
        setBitmapCurrentRect(scaleValue, event);
        mPointerCurrentDistance = tempDistance;
        showScaleBitmap();
    }

    private void showScaleBitmap() {
        synchronized (PictureZoom.class) {
            Canvas canvas = getHolder().lockCanvas();
            if (null != canvas && null != mBitmap) {
                canvas.drawPaint(mClearPaint);
                canvas.drawBitmap(mBitmap, null, mBitmapCurrentRect, null);
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    private void showDragBitmap() {
        synchronized (PictureZoom.class) {
            Canvas canvas = getHolder().lockCanvas();
            if (null != canvas && null != mBitmap) {
                canvas.drawPaint(mClearPaint);
                canvas.drawBitmap(mBitmap, null, mBitmapCurrentRect, null);
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    private float getPointerDistance(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float)Math.sqrt(x * x + y * y);
        } else {
            return mPointerCurrentDistance;
        }
    }

    private void setBitmapCurrentRect(float scaleValue, MotionEvent event) {
        float distanceX = mBitmapCurrentRect.width() * scaleValue;
        float distanceY = mBitmapCurrentRect.height() * scaleValue;

        float distanceLeft = 0f;
        float distanceRight = 0f;
        float distanceTop = 0f;
        float distanceBottom = 0f;

        if (event.getX(0) < event.getX(1)) {
            distanceLeft = distanceX * event.getX(0) / (Math.abs(event.getX(0)) + Math.abs(mScreenWidth - event.getX(1)));
            distanceRight = distanceX * event.getX(1) / (Math.abs(event.getX(0)) + Math.abs(mScreenWidth - event.getX(1)));
        } else {
            distanceLeft = distanceX * event.getX(1) / (Math.abs(event.getX(1)) + Math.abs(mScreenWidth - event.getX(0)));
            distanceRight = distanceX * event.getX(0) / (Math.abs(event.getX(1)) + Math.abs(mScreenWidth - event.getX(0)));
        }

        if (event.getY(0) < event.getY(1)) {
            distanceTop = distanceY * event.getY(0) / (Math.abs(event.getY(0)) + Math.abs(mScreenHeight - event.getY(1)));
            distanceBottom = distanceY * event.getY(1) / (Math.abs(event.getY(0)) + Math.abs(mScreenHeight - event.getY(1)));
        } else {
            distanceTop = distanceY * event.getY(1) / (Math.abs(event.getY(1)) + Math.abs(mScreenHeight - event.getY(0)));
            distanceBottom = distanceY * event.getY(0) / (Math.abs(event.getY(1)) + Math.abs(mScreenHeight - event.getY(0)));
        }

        mBitmapCurrentRect.left = (int) (mBitmapCurrentRect.left - distanceLeft);
        mBitmapCurrentRect.right = (int) (mBitmapCurrentRect.right + distanceRight);
        mBitmapCurrentRect.top = (int) (mBitmapCurrentRect.top - distanceTop);
        mBitmapCurrentRect.bottom = (int) (mBitmapCurrentRect.bottom + distanceBottom);
        if (SCALE_WIDTH_MAX > mBitmapCurrentRect.width() && mBitmapCurrentRect.width() > SCALE_WIDTH_MIN &&
                SCALE_HEIGHT_MAX > mBitmapCurrentRect.height() && mBitmapCurrentRect.height() > SCALE_HEIGHT_MIN) {
        } else {
            mBitmapCurrentRect.left = (int) (mBitmapCurrentRect.left + distanceLeft);
            mBitmapCurrentRect.right = (int) (mBitmapCurrentRect.right - distanceRight);
            mBitmapCurrentRect.top = (int) (mBitmapCurrentRect.top + distanceTop);
            mBitmapCurrentRect.bottom = (int) (mBitmapCurrentRect.bottom - distanceBottom);
        }
    }

    // double click
    private class DoubleClickTimerTask extends TimerTask {
        public void run() {
            mIsWaitSecondClick = false;
        }
    }

    private void dealDoubleClickEvent(MotionEvent event) {
        float distanceX = Math.abs(mDownX - event.getX());
        float distanceY = Math.abs(mDownY - event.getY());
        if (distanceX < DOUBLE_CLICK_DISTANCE && distanceY < DOUBLE_CLICK_DISTANCE) {
            if (true == hasScale()) {
                mBitmapCurrentRect.left = mBitmapRect.left;
                mBitmapCurrentRect.top = mBitmapRect.top;
                mBitmapCurrentRect.right = mBitmapRect.right;
                mBitmapCurrentRect.bottom = mBitmapRect.bottom;
            } else {
                float disX = mBitmapCurrentRect.width() * DOUBLE_CLICK_SCALE_VALUE;
                float disY = mBitmapCurrentRect.height() * DOUBLE_CLICK_SCALE_VALUE;
                QUADRANT quadrant = judgeQuadrant();
                if (QUADRANT.CENTER == quadrant) {
                    mBitmapCurrentRect.left = (int) (mBitmapCurrentRect.left - disX);
                    mBitmapCurrentRect.top = (int) (mBitmapCurrentRect.top - disY);
                    mBitmapCurrentRect.right = (int) (mBitmapCurrentRect.right + disX);
                    mBitmapCurrentRect.bottom = (int) (mBitmapCurrentRect.bottom + disY);
                } else {
                    if (QUADRANT.ONE == quadrant || QUADRANT.TWO == quadrant) {
                        mBitmapCurrentRect.bottom = (int) (mBitmapCurrentRect.bottom + 2 * disY);
                        if (mBitmapCurrentRect.height() > mScreenHeight) {
                            mBitmapCurrentRect.top = 0;
                        } else {
                            mBitmapCurrentRect.top = mBitmapCurrentRect.top;
                        }
                    } else {
                        mBitmapCurrentRect.top = (int) (mBitmapCurrentRect.top - 2 * disY);
                        if (mBitmapCurrentRect.height() > mScreenHeight) {
                            mBitmapCurrentRect.bottom = mScreenHeight;
                        } else {
                            mBitmapCurrentRect.bottom = mBitmapCurrentRect.bottom;
                        }
                    }

                    if (QUADRANT.TWO == quadrant || QUADRANT.THREE == quadrant) {
                        mBitmapCurrentRect.right = (int) (mBitmapCurrentRect.right + 2 * disX);
                        if (mBitmapCurrentRect.width() > mScreenWidth) {
                            mBitmapCurrentRect.left = 0;
                        } else {
                            mBitmapCurrentRect.left = mBitmapCurrentRect.left;
                        }
                    } else {
                        mBitmapCurrentRect.left = (int) (mBitmapCurrentRect.left - 2 * disX);
                        if (mBitmapCurrentRect.width() > mScreenWidth) {
                            mBitmapCurrentRect.right = mScreenWidth;
                        } else {
                            mBitmapCurrentRect.right = mBitmapCurrentRect.right;
                        }
                    }
                }
            }
            showScaleBitmap();
        }
    }

    private QUADRANT judgeQuadrant() {
        int halfScreenWidht = mScreenWidth / 2;
        int halfScreenHeight = mScreenHeight / 2;
        int centerLeft = mScreenWidth * 4 / 10;
        int centerRight = mScreenWidth * 7 / 10;
        int centerTop = mScreenHeight * 4 / 10;
        int centerBottom = mScreenHeight * 7 / 10;

        int center2Top = mScreenHeight * 4 / 10;
        int center2Bottom = mScreenHeight * 7 / 10;

        if (mDownX < centerRight && mDownX > centerLeft &&
                mDownY < centerBottom && mDownY > centerTop) {
            return QUADRANT.CENTER;
        }

        if (mScreenWidth > mBitmapRect.width() && mScreenHeight <= mBitmapRect.height()) {
            if (mDownY < center2Bottom && mDownY > center2Top) {
                return QUADRANT.CENTER;
            }
        }

        if (mDownX < halfScreenWidht) {
            if (mDownY < halfScreenHeight) {
                return QUADRANT.TWO;
            } else {
                return QUADRANT.THREE;
            }
        } else {
            if (mDownY < halfScreenHeight) {
                return QUADRANT.ONE;
            } else {
                return QUADRANT.FOUR;
            }
        }
    }

    private boolean hasScale() {
        boolean result = false;
        if (mBitmapCurrentRect.width() > SCALE_WIDTH_MIN || mBitmapCurrentRect.height() > SCALE_HEIGHT_MIN) {
            result = true;
        }
        return result;
    }
}
