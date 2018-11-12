package cn.vpfinance.vpjr.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.jewelcredit.util.Utils;

import java.util.ArrayList;

import cn.vpfinance.android.R;

/**
 * Created by Administrator on 2015/11/2.
 */
public class CirclePercentView extends View{

    private int width;
    private int height;
    private Paint paint;
    int strokeWidth = 35;
    int color_background = 0xFFE6E6E6;
    private ArrayList<Integer> datas = new ArrayList<>();
    private ArrayList<Integer> colorsNo1 = new ArrayList<>();
    private ArrayList<Integer> colorsNo2 = new ArrayList<>();
    /*三个百分百*/
    private ArrayList<Float> percents;
    private Float tmpAngle;


    public CirclePercentView(Context context) {
        super(context);
    }

    public CirclePercentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CirclePercentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private void initPaint()
    {
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

        /*内圈白色*/
        int radius = Math.min(width, height) / 2 - 10;
        radius -= 60;
        paint.setColor(color_background);
        int centerX = width/2;
        int centerY = height/2;
//        canvas.drawCircle(centerX, centerY, radius, paint);

//        /*第一圈颜色*/
//        colorsNo1.add(0xFFFFE0AD);//橙色
//        colorsNo1.add(0xFFFF7A7A);//红色
//        colorsNo1.add(0xFF6386BD);//蓝色
        float left = width*0.5f - radius;
        float top = height*0.5f - radius;
        float right = width*0.5f + radius;
        float bottom = height*0.5f + radius;
        float startAngle = 90;
//        for (int i=0 ; i < 3; i++){
//            if (percents !=null && percents.size() != 0){
//                tmpAngle = percents.get(i) * (float)360.0;
//            }else {
//                tmpAngle =(120.0f);
//            }
//            paint.setColor(colorsNo1.get(i));
//            boolean useCenter = false;
//            RectF oval = new RectF(left,top,right,bottom);
//            if (i == 2){
//                tmpAngle += 2;
//            }
//            canvas.drawArc(oval, startAngle,tmpAngle , useCenter, paint);
//            startAngle += (tmpAngle - 1);
//        }
        /*第二圈*/
//        colorsNo2.add(0xFFFAD597);//橙色
//        colorsNo2.add(0xFFFF4D4D);//红色
//        colorsNo2.add(0xFF5777A8);//蓝色
        colorsNo2.add(Utils.getColor(R.color.percent_view_red));//红色
        colorsNo2.add(Utils.getColor(R.color.percent_view_blue));//蓝色
        colorsNo2.add(Utils.getColor(R.color.percent_view_yellow));//橙色
//        colorsNo2.add(0xff666b);//红色
//        colorsNo2.add(0x66adff);//蓝色
        radius += strokeWidth;
        left = width*0.5f - radius;
        top = height*0.5f - radius;
        right = width*0.5f + radius;
        bottom = height*0.5f + radius;
        startAngle = 270;
        for (int i=0 ; i < 3; i++){
            if (percents !=null && percents.size() != 0){
                tmpAngle = percents.get(i) * 360;
            }else {
                tmpAngle =(120.0f);
            }
//            Utils.log("startAngle:"+startAngle+"----tmpAngle:"+tmpAngle);
            paint.setColor(colorsNo2.get(i));
            boolean useCenter = false;
            RectF oval = new RectF(left,top,right,bottom);
            if (i == 2){
                tmpAngle += 1;
            }
            canvas.drawArc(oval, startAngle,tmpAngle , useCenter, paint);
            startAngle += (tmpAngle - 0.5);
        }

        if(360 + 90 - (int)startAngle < 0)
        {
            invalidate();
        }
    }
    public void setProgress(ArrayList<Float> percents) {
        if (percents == null || percents.size() == 0){
            this.percents = new ArrayList<Float>();
            percents.add(0.33f);
            percents.add(0.33f);
            percents.add(0.33f);
        }else{
            this.percents = percents;
        }
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = MeasureSpec.getSize(widthMeasureSpec) ;
        height = MeasureSpec.getSize(heightMeasureSpec) ;
    }
}
