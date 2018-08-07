package cn.vpfinance.vpjr.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.EditText;

import cn.vpfinance.android.R;

public class CodeVerifyView extends EditText {

    private int codeNum = 6;
    private int padding = 30;

    private int background = Color.WHITE;
    private int textLength = 0;
    private int dividerWidth;
    private int measuredHeight;
    private int measuredWidth;
    private boolean isError = false;

    private OnFullCodeListener listener;

    public void setError(boolean error) {
        isError = error;
    }

    public CodeVerifyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CodeVerifyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setMaxEms(codeNum);
    }

    public void clear() {
        textLength = 0;
        isError = false;
        setText("");
    }

    public void error() {
        textLength = 0;
        isError = true;
        setText("");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(null);
        }
        dividerWidth = (getMeasuredWidth() - ((codeNum - 1) * padding)) / codeNum;
        measuredHeight = getMeasuredHeight();
        measuredWidth = getMeasuredWidth();
        // 覆盖内容区
        drawBackground(canvas);

        drawLine(canvas);

        drawText(canvas);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        this.textLength = text.toString().length();
        if (isError && textLength == 1){
            isError = false;
            if (listener != null){
                listener.restoreListener();
            }
        }
        if (textLength == codeNum && listener != null) {
            listener.fullCodeListener();
        }
        invalidate();
    }

    private void drawText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(8);
        paint.setTextSize(80);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(getResources().getColor(R.color.text_4985ff));

        for (int i = 0; i < textLength; i++) {
            String text = String.valueOf(getText().charAt(i));
            canvas.drawText(text, (dividerWidth / 2) + i * (padding + dividerWidth), measuredHeight - 20, paint);
        }

    }

    private void drawBackground(Canvas canvas) {
        RectF rectIn = new RectF(0, 0, measuredWidth, measuredHeight);
        Paint bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(background);
        canvas.drawRoundRect(rectIn, 0, 0, bgPaint);
    }

    private void drawLine(Canvas canvas) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(8);
        if (isError) {
            paint.setColor(getResources().getColor(R.color.text_ff5050));
        } else if (textLength == 0) {
            paint.setColor(getResources().getColor(R.color.text_666666));
        } else {
            paint.setColor(getResources().getColor(R.color.text_4985ff));
        }
        float startX = 0;
        float endX = 0;

        for (int i = 0; i < codeNum; i++) {
            endX = startX + dividerWidth;
            canvas.drawLine(startX, measuredHeight, endX, measuredHeight, paint);
            startX = endX + padding;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        MeasureSpec.makeMeasureSpec(widthMeasureSpec, heightMeasureSpec);
    }

    public void setOnFullCodeListener(OnFullCodeListener listener) {
        this.listener = listener;
    }

    public interface OnFullCodeListener {
        void fullCodeListener();
        void restoreListener();
    }
}
