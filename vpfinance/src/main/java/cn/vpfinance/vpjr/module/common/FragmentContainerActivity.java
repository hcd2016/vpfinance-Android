package cn.vpfinance.vpjr.module.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.jewelcredit.ui.widget.ActionBarLayout;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 * Created by zhangqingtian on 16/2/2.
 */
public class FragmentContainerActivity extends BaseActivity implements View.OnClickListener {

    private ActionBarLayout titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_for_fragment);

        titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
        titleBar.setHeadBackVisible(View.VISIBLE);

    }

    public void setFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragmentLayout, fragment);
        ft.commit();
    }

    public void setTitle(String title)
    {
        titleBar.setTitle(title);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.headBack:
                finish();
                break;
        }
    }
}
