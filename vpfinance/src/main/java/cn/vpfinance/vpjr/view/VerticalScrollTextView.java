package cn.vpfinance.vpjr.view;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.jewelcredit.util.Utils;

import java.util.ArrayList;


/**
 */
public class VerticalScrollTextView extends TextView{

    private ArrayList<Pair<String,String>> textList;
    private static int currShowText = 0;
    private Context mContext ;
    private Pair<String, String> pair;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try{
                if (textList != null && textList.size() != 0){
                    pair = textList.get(currShowText);
                    setText(TextUtils.isEmpty(pair.first) ? "" : pair.first);
                    if (currShowText < textList.size() - 1){
                        currShowText++;
                    }else{
                        currShowText = 0;
                    }
                }
                invalidate();
                removeCallbacks(runnable);
                postDelayed(this,3000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    public VerticalScrollTextView(Context context) {
        super(context);
        mContext = context;
    }

    public VerticalScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public VerticalScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setScrollList(final ArrayList<Pair<String,String>> textList){
        this.textList = textList;
        removeCallbacks(runnable);
        post(runnable);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pair != null && (!TextUtils.isEmpty(pair.second))){
                    Utils.goToWeb(mContext, pair.second, "");
                }
            }
        });
    }

    public void clearScroll(){
        removeCallbacks(runnable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
