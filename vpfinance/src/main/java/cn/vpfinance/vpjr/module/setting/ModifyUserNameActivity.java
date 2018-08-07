package cn.vpfinance.vpjr.module.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 * 修改用户名
 * Created by Administrator on 2015/12/18.
 */
public class ModifyUserNameActivity extends BaseActivity {

    private ActionBarLayout titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_name);

        titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
        titleBar.reset().setHeadBackVisible(View.VISIBLE).setTitle("用户名").setActionRight("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ModifyUserNameActivity.this,"保存",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
