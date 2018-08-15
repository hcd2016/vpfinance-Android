package cn.vpfinance.vpjr.module.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import com.jewelcredit.util.Utils;

import butterknife.ButterKnife;
import cn.vpfinance.android.R;


/**
 * <p>
 * Description：Dialog基类，继承之后在构造中的super方法之后调用setWindowParam可设置dialog宽高，对齐，动画
 * </p>
 */
public abstract class TBaseDialog extends Dialog {

    public Context mContext;

    /**
     * dialog对应的布局view
     */
    public View view;

    public TBaseDialog(Context context, int layoutId) {
        super(context, R.style.style_dialog_without_anim);
        this.mContext = context;
        view = getLayoutInflater().inflate(layoutId, null);
        setContentView(view);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    public abstract void initView();

    /**
     * 初始化数据
     */
    public abstract void initData();


    /**
     * 设置宽高和对齐方式
     *
     * @param width       宽度
     * @param height      高度
     * @param gravity     对齐方式
     * @param animStyleId 动画style,传入0代表没有动画
     */
    public void setWindowParam(float width, float height, int gravity, int animStyleId) {
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值

        if (width == WindowManager.LayoutParams.WRAP_CONTENT) {//宽度小于0，默认为WRAP_CONTENT
            p.width = WindowManager.LayoutParams.WRAP_CONTENT;
        } else if (width > 0 && width < 1) {//宽度介于0与1之间（0.x），设置为屏幕高的百分比
            p.width = (int) (Utils.getScreenWidth(mContext) * width);
        } else if (width >= Utils.getScreenWidth(mContext) ||
                width == WindowManager.LayoutParams.MATCH_PARENT) {//宽度大于屏幕宽度，设置为屏幕宽度
            p.width = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            p.width = (int) width;
        }

        if (height == WindowManager.LayoutParams.WRAP_CONTENT) {//高度小于0，默认为WRAP_CONTENT
            p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        } else if (height > 0 && height < 1) {//高度介于0与1之间（0.x），设置为屏幕高的百分比
            p.height = (int) (Utils.getScreenHeight(mContext) * height);
        } else if (height >= Utils.getScreenHeight(mContext)
                || height == WindowManager.LayoutParams.MATCH_PARENT) {//高度大于屏幕宽度，设置为屏幕宽度
            p.height = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            p.height = (int) height;
        }
        //设置对齐方式
        p.gravity = gravity;
        getWindow().setAttributes(p);
        if (animStyleId != 0)
            getWindow().setWindowAnimations(animStyleId);
    }

}
