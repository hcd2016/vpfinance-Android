package cn.vpfinance.vpjr.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.vpfinance.android.R;

/**
 */
public class MedalXImageView extends RelativeLayout{

    private Context mContext;
//    private int mWidth;
//    private int mHeight;
    private ImageView mImageView;
    public static final int STYLE_BIG = 1;
    public static final int STYLE_SMALL = 2;
    private LinearLayout.LayoutParams mPartParams;

    public MedalXImageView(Context context) {
        this(context,null);
    }

    public MedalXImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MedalXImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        mImageView = new ImageView(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(params);
        addView(mImageView);

//        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        mImageView.measure(w, h);
//        mHeight = mImageView.getMeasuredHeight();
//        mWidth = mImageView.getMeasuredWidth();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setBackground(String urlBg){
        ImageLoader.getInstance().displayImage(urlBg,mImageView);
    }

    public void setStyle(int styleSize){
        if (styleSize == STYLE_SMALL){
            mPartParams = new LinearLayout.LayoutParams(28,32);
        }else if(styleSize == STYLE_BIG){
            mPartParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        mPartParams.gravity = Gravity.CENTER;
    }
    public void setXNum(int num){
        LinearLayout wrapperView = new LinearLayout(mContext);
        wrapperView.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,10,10);
        params.addRule(ALIGN_PARENT_BOTTOM);
        params.addRule(ALIGN_PARENT_RIGHT);
        wrapperView.setLayoutParams(params);

        if (num <= 1)    return;
        
        if (num <100){
            int tens = num / 10;
            int units = num % 10;
            addPartView(wrapperView,"x");
            addPartView(wrapperView,tens,false);
            addPartView(wrapperView,units,true);
        }else{
            //100以上
            addPartView(wrapperView,"x");
            addPartView(wrapperView,9,false);
            addPartView(wrapperView,9,true);
        }
        addView(wrapperView);
    }

    private void addPartView(LinearLayout view,int num,boolean isShowZero) {
        if (!isShowZero){
            if (num == 0)   return;
        }

        int res = 0;
        switch (num){
            case 0:
                res = R.drawable.ic_medal_0;
                break;
            case 1:
                res = R.drawable.ic_medal_1;
                break;
            case 2:
                res = R.drawable.ic_medal_2;
                break;
            case 3:
                res = R.drawable.ic_medal_3;
                break;
            case 4:
                res = R.drawable.ic_medal_4;
                break;
            case 5:
                res = R.drawable.ic_medal_5;
                break;
            case 6:
                res = R.drawable.ic_medal_6;
                break;
            case 7:
                res = R.drawable.ic_medal_7;
                break;
            case 8:
                res = R.drawable.ic_medal_8;
                break;
            case 9:
                res = R.drawable.ic_medal_9;
                break;
        }
        ImageView x = new ImageView(mContext);
        if (mPartParams == null){
            mPartParams = new LinearLayout.LayoutParams(28,32);
//            throw new RuntimeException("you should setup mPartParams!");
        }
        x.setLayoutParams(mPartParams);
        x.setImageDrawable(getResources().getDrawable(res));
        view.addView(x);
    }

    private void addPartView(LinearLayout view,String str) {
        if (TextUtils.isEmpty(str))   return;

        if (str.equals("x")){
            ImageView x = new ImageView(mContext);
            if (mPartParams == null){
                mPartParams = new LinearLayout.LayoutParams(28,32);
//                throw new RuntimeException("you should setup mPartParams!");
            }
            x.setLayoutParams(mPartParams);
            x.setImageDrawable(getResources().getDrawable(R.drawable.ic_medal_x));
            view.addView(x);
        }
    }
}
