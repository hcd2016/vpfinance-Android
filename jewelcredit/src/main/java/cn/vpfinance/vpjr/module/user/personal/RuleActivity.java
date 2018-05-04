package cn.vpfinance.vpjr.module.user.personal;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.jewelcredit.ui.widget.ActionBarLayout;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 * Created by Administrator on 2015/11/3.
 * 邀请规则说明
 */
public class RuleActivity extends BaseActivity {
    private ActionBarLayout titleBar;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                RuleActivity.this.finish();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule);

//        titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
//        titleBar.setTitle("邀请有礼").setHeadBackVisible(View.VISIBLE);

        findViewById(R.id.clickX).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RuleActivity.this.finish();
            }
        });
        findViewById(R.id.parentView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RuleActivity.this.finish();
            }
        });


    }
}
