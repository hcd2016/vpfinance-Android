package cn.vpfinance.vpjr.model;

import android.graphics.drawable.Drawable;

/**
 */
public class MedalInfo {
    public Drawable mDrawable;

    public String mName;

    public String mTitle;

    public boolean isTitleItem = false;

    public MedalInfo(Drawable drawable, String name, String title, boolean isTitleItem) {
        mDrawable = drawable;
        mName = name;
        mTitle = title;
        this.isTitleItem = isTitleItem;
    }

}
