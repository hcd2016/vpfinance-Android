package cn.vpfinance.vpjr.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/5/10.
 */
public class UserHeadEvent {
    private Drawable userHead;

    public Drawable getUserHead() {
        return userHead;
    }

    public void setUserHead(Drawable userHead) {
        this.userHead = userHead;
    }

    public UserHeadEvent(Drawable userHead) {

        this.userHead = userHead;
    }
}
