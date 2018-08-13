package com.jewelcredit.util;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

/**
 * <p>
 * Description：同一textview内容不同颜色或需目标文字点击事件
 * </p>
 *
 */
public class DifColorTextStringBuilder {

    private SpannableStringBuilder mSpannableStringBuilder;

    private String content;
    private TextView textView;

    /**
     * 设置文本内容
     *
     * @param content 文本内容
     */
    public DifColorTextStringBuilder setContent(String content) {
        this.content = content;
            mSpannableStringBuilder = new SpannableStringBuilder(content);
        return this;
    }

    /**
     * 设置TextView控件
     *
     * @param textView 目标TextView
     */
    public DifColorTextStringBuilder setTextView(TextView textView) {
        this.textView = textView;
        this.textView.setMovementMethod(LinkMovementMethod.getInstance());
        return this;
    }

    /**
     * 设置高亮内容与点击事件
     *
     * @param highlightContent 需要高亮的文本
     * @param clickableSpan    高亮文本对应的点击事件，可为null
     */
    public DifColorTextStringBuilder setHighlightContent(String highlightContent, MyClickableSpan clickableSpan) {
        if (TextUtils.isEmpty(highlightContent) || !content.contains(highlightContent)) {
            return this;
        }
        int startIndex = content.indexOf(highlightContent);
        int endIndex = startIndex + highlightContent.length();
        mSpannableStringBuilder.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * 设置高亮内容(不需点击事件)
     *
     * @param highlightContent 需要高亮的文本
     * @param color            高亮颜色
     */
    public DifColorTextStringBuilder setHighlightContent(String highlightContent, int color) {
        if (TextUtils.isEmpty(highlightContent) || !content.contains(highlightContent)) {
            return this;
        }
        int startIndex = content.indexOf(highlightContent);
        int endIndex = startIndex + highlightContent.length();
        mSpannableStringBuilder.setSpan(new ForegroundColorSpan(Utils.getColor(color)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * 一定要在最后一步调用，为TextView设置内容
     */
    public void create() {
        textView.setText(mSpannableStringBuilder);
    }

}
