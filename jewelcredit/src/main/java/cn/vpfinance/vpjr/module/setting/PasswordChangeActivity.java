package cn.vpfinance.vpjr.module.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

import com.jewelcredit.ui.widget.ActionBarLayout;

public class PasswordChangeActivity extends BaseActivity {

    public static final String EXTRA_KEY_INDEX = "index";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        Intent intent = getIntent();

        if (intent != null) {
            int index = intent.getIntExtra(EXTRA_KEY_INDEX, 0);
            if (index == 0) {
                ((ActionBarLayout)findViewById(R.id.titleBar)).setTitle("修改登录密码").setHeadBackVisible(View.VISIBLE);
            }
            else if (index == 1) {
                ((ActionBarLayout)findViewById(R.id.titleBar)).setTitle("设置交易密码").setHeadBackVisible(View.VISIBLE);
            }
        }


//        mViewPager = (ViewPager) findViewById(R.id.viewpager);
//        mViewPager.setAdapter(new ChangePwdAdapter(getSupportFragmentManager()));
//        mViewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
        ((RadioButton)findViewById(R.id.tab1)).setChecked(true);
        ((RadioGroup)findViewById(R.id.btnTabSel)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.tab1:
//                        mViewPager.setCurrentItem(0, false);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content, ChangeLoginPwdFragment.newInstance())
                                .commit();
                        break;

                    case R.id.tab2:
//                        mViewPager.setCurrentItem(1, false);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content, ChangeTradePwdFragment.newInstance())
                                .commit();
                        break;
                }
            }
        });
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, ChangeLoginPwdFragment.newInstance())
                .commit();


        if (intent != null) {
            int index = intent.getIntExtra(EXTRA_KEY_INDEX, 0);
            if (index == 0) {
                ((RadioButton)findViewById(R.id.tab1)).performClick();
            }
            else if (index == 1) {
                ((RadioButton)findViewById(R.id.tab2)).performClick();
            }
        }
    }

//    static class ChangePwdAdapter extends FragmentPagerAdapter {
//
//        public ChangePwdAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int i) {
//            if (i == 0) {
//                return ChangeLoginPwdFragment.newInstance();
//            } else if (i == 1) {
//                return ChangeTradePwdFragment.newInstance();
//            }
//            return null;
//        }
//
//        @Override
//        public int getCount() {
//            return 2;
//        }
//    }
}
