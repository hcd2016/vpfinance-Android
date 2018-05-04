package cn.vpfinance.vpjr.module.gusturelock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 */
public class MiniLockPatternView extends View{
    private static float RADIUS = 3.0f;
    private static final int ROW = 3;
    private static final int COLUMN = 3;
    private static float paddingCircle = 1;
    private Paint paintForStroke;
    private Paint paintForFill;
    private int width;
    private int height;
    private float[] centerXs;
    private float[] centerYs;
    private static List<PointCell> pattern = new ArrayList<PointCell>();
    private float w;
    private float h;

    public MiniLockPatternView(Context context) {
        super(context);
    }

    public MiniLockPatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MiniLockPatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPaint();
        initDate();
        initBg(canvas);
        initPattern(canvas);
    }

    private void initPattern(Canvas canvas) {
//        pattern.add(new PointCell(1, 0));
//        pattern.add(new PointCell(0, 1));

        if (pattern != null && pattern.size() != 0){
            for (PointCell pointCell : pattern) {
                canvas.drawCircle(centerXs[pointCell.row],centerYs[pointCell.colum], RADIUS,paintForFill);
            }
        }
    }

    public void setPoint(PointCell addPoint){
        pattern.add(addPoint);
        invalidate();
    }

    public void clearPoint(){
        pattern.clear();
        invalidate();
    }

    private void initBg(Canvas canvas) {
        for (int i = 0; i < COLUMN; i++ ){
            for (int j = 0; j < ROW; j++){
                canvas.drawCircle(centerXs[i],centerYs[j],RADIUS,paintForStroke);
            }
        }
    }

    private void initDate() {
        w = width / ROW;
        h = height / COLUMN;
        RADIUS = w / ( ROW + paddingCircle);
        centerXs = new float[]{w *1-RADIUS, w *2-RADIUS, w *3-RADIUS};
        centerYs = new float[]{h *1-RADIUS, h *2-RADIUS, h *3-RADIUS};
    }

    private void initPaint() {
        if (paintForStroke == null){
            paintForStroke = new Paint();
            paintForStroke.setAntiAlias(true);
            paintForStroke.setFilterBitmap(true);
            paintForStroke.setStyle(Paint.Style.STROKE);
            paintForStroke.setStrokeWidth(3);
            paintForStroke.setColor(Color.WHITE);
        }
        if (paintForFill == null){
            paintForFill = new Paint();
            paintForFill.setAntiAlias(true);
            paintForFill.setFilterBitmap(true);
            paintForFill.setStyle(Paint.Style.FILL_AND_STROKE);
            paintForFill.setColor(Color.WHITE);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }

    public class PointCell{
        public int row;
        public int colum;

        public PointCell(int row, int colum) {
            this.row = row;
            this.colum = colum;
        }
    }
}
