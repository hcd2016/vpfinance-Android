package com.jewelcredit.util;

import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import cn.vpfinance.vpjr.FinanceApplication;

/**
 * <p>
 * Description：
 * </p>
 *
 * @author tangzhijie
 */
public abstract class MyClickableSpan extends ClickableSpan {

    private int color;

    public MyClickableSpan() {
    }

    public MyClickableSpan(int color) {
        this.color = color;
    }

    public abstract void onClick(View widget);

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(false);
        if (color != 0) {
            ds.setColor(ContextCompat.getColor(FinanceApplication.getContext(), color));
        }
    }
}
