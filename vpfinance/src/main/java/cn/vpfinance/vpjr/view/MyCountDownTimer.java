package cn.vpfinance.vpjr.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.Interval;
import org.joda.time.Period;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.App;


/**
 *
 */
public class MyCountDownTimer extends LinearLayout {

    private TextView countdown_day;
    private TextView countdown_hour;
    private TextView countdown_minute;
    private TextView countdown_second;
    private MyCounter counter;

    public MyCountDownTimer(Context context) {
        this(context, null);
    }

    public MyCountDownTimer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCountDownTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        Context mContext = context;
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,R.styleable.MyCountDownTimer,defStyleAttr,0);
        int resourceId = typedArray.getResourceId(R.styleable.MyCountDownTimer_newLayout, R.layout.layout_time_countdown);
        View view = View.inflate(mContext, resourceId, null);
        addView(view);
        countdown_day = ((TextView) view.findViewById(R.id.countdown_day));
        countdown_hour = ((TextView) view.findViewById(R.id.countdown_hour));
        countdown_minute = ((TextView) view.findViewById(R.id.countdown_minute));
        countdown_second = ((TextView) view.findViewById(R.id.countdown_second));
    }

    /**
     * 设置发布时间
     * @param countDownTime
     */
    public void setCountDownTime(Context context,long countDownTime) {
        countdown_day.setText("0");
        countdown_hour.setText("0");
        countdown_minute.setText("0");
        countdown_second.setText("0");

        if (countDownTime < 0 || countDownTime < getCurrentTime(context)){
            return;
        }

        cancel();
        counter = new MyCounter(context,countDownTime - getCurrentTime(context), 1000);
        counter.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (counter != null){
            counter.cancel();
            counter.context = null;
            counter = null;
        }
    }

    /**
     * 获取服务器时间
     * @param context
     * @return
     */
    private long getCurrentTime(Context context){
        App application = (App) context.getApplicationContext();
        long serviceTime = application.differTime + System.currentTimeMillis();
//        Logger.e("getCurrentTime: differTime:"+application.differTime+",serviceTime: "+serviceTime);
        return serviceTime;
    }

    public void cancel(){
        if (counter != null){
            counter.cancel();
        }
    }
    private onFinish mListener;

    public interface onFinish{
        void finish();
    }

    public void setOnFinishListener(onFinish listener){
        mListener = listener;
    }

    class MyCounter extends CountDownTimer {

        public Context context;
        public MyCounter(Context context,long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            this.context = context;
        }

        @Override
        public void onFinish() {
            countdown_day.setText("0");
            countdown_hour.setText("0");
            countdown_minute.setText("0");
            countdown_second.setText("0");

            if (mListener != null){
                mListener.finish();
            }
        }
        @Override
        public void onTick(long millisUntilFinished) {
            long time = millisUntilFinished;
            time = time / 1000;
            if (time > 0) {
                Interval interval = new Interval(getCurrentTime(context), millisUntilFinished + getCurrentTime(context));
                Period p = interval.toPeriod();
                countdown_day.setText("" + (p.getWeeks() * 7 + p.getDays()));
                countdown_hour.setText("" + p.getHours());
                countdown_minute.setText("" + p.getMinutes());
                countdown_second.setText("" + p.getSeconds());
            }
        }
    }
}
