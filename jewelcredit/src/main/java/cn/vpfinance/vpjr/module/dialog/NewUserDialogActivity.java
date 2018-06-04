package cn.vpfinance.vpjr.module.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.QueryPopUpBean;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.module.user.BindBankHintActivity;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;

/**
 * type 1: 体验金 + 优惠券 + 绑卡激活
 * type 2: 体验金 + 绑卡激活
 * type 3: 绑卡激活
 * type 4: 绑卡激活奖励 + 首次投资奖励
 * type 5: 绑卡激活奖励
 * type 6: 首笔投资奖励
 */
public class NewUserDialogActivity extends Activity {


    private QueryPopUpBean bean;

    public static void goThis(Activity activity, String json) {
        Intent intent = new Intent(activity, NewUserDialogActivity.class);
        intent.putExtra("json", json);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_exit);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        Intent intent = getIntent();
        if (intent == null) return;
        String json = intent.getStringExtra("json");
        bean = new Gson().fromJson(json.toString(), QueryPopUpBean.class);
        if (bean == null || bean.returnType == 0) return;

        switch (bean.returnType) {
            case 1:
                initType1();
                break;
            case 2:
                initType2();
                break;
            case 3:
                initType3();
                break;
            case 4:
                initType4();
                break;
            case 5:
                initType5();
                break;
            case 6:
                initType6();
                break;
        }

        //保存到sp中, 只显示一次
        User user = DBUtils.getUser(this);
        if (user != null) {
            String userId = user.getUserId().toString();
            Set<String> values = SharedPreferencesHelper.getInstance(this).getSetValue("NewUserPop_"+userId, new HashSet<String>());
            values.add(bean.bigType);
            SharedPreferencesHelper.getInstance(this).putSetValue(userId, values);
        }


        findViewById(R.id.ibCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initType6() {
        setContentView(R.layout.activity_dialog_new_user_6);
        findViewById(R.id.vButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        try {
            for (QueryPopUpBean.CouponDetailBean detailBean : bean.couponDetail) {
                if ("优惠券".equals(detailBean.couponName)) {
                    ((TextView) findViewById(R.id.tvVoucher50)).setText("有效期至" + detailBean.time);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initType5() {
        setContentView(R.layout.activity_dialog_new_user_5);
        findViewById(R.id.vButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        try {
            for (QueryPopUpBean.CouponDetailBean detailBean : bean.couponDetail) {
                if ("优惠券".equals(detailBean.couponName)) {
                    ((TextView) findViewById(R.id.tvVoucher30)).setText("有效期至" + detailBean.time);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initType4() {
        setContentView(R.layout.activity_dialog_new_user_4);
        findViewById(R.id.vButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(NewUserDialogActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.SWITCH_TAB_NUM, 1);
                startActivity(intent);
            }
        });
        try {
            for (QueryPopUpBean.CouponDetailBean detailBean : bean.couponDetail) {
                if ("优惠券".equals(detailBean.couponName)) {
                    ((TextView) findViewById(R.id.tvVoucher30)).setText("有效期至" + detailBean.time);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initType3() {
        setContentView(R.layout.activity_dialog_new_user_3);
        findViewById(R.id.vButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                User user = DBUtils.getUser(NewUserDialogActivity.this);
                if (user != null) {
                    BindBankHintActivity.goThis(NewUserDialogActivity.this, user.getUserId().toString());
                }
            }
        });
        try {
            for (QueryPopUpBean.CouponDetailBean detailBean : bean.couponDetail) {
                if ("体验金".equals(detailBean.couponName)) {
                    ((TextView) findViewById(R.id.tvExperience)).setText("有效期至" + detailBean.time);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initType2() {
        setContentView(R.layout.activity_dialog_new_user_2);

        findViewById(R.id.vButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                User user = DBUtils.getUser(NewUserDialogActivity.this);
                if (user != null) {
                    BindBankHintActivity.goThis(NewUserDialogActivity.this, user.getUserId().toString());
                }
            }
        });
        try {
            for (QueryPopUpBean.CouponDetailBean detailBean : bean.couponDetail) {
                if ("体验金".equals(detailBean.couponName)) {
                    ((TextView) findViewById(R.id.tvExperience)).setText("有效期至" + detailBean.time);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initType1() {
        setContentView(R.layout.activity_dialog_new_user_1);

        findViewById(R.id.vButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                User user = DBUtils.getUser(NewUserDialogActivity.this);
                if (user != null) {
                    BindBankHintActivity.goThis(NewUserDialogActivity.this, user.getUserId().toString());
                }
            }
        });
        try {
            for (QueryPopUpBean.CouponDetailBean detailBean : bean.couponDetail) {
                if ("体验金".equals(detailBean.couponName)) {
                    ((TextView) findViewById(R.id.tvExperience)).setText("有效期至" + detailBean.time);
                } else if ("优惠券".equals(detailBean.couponName)) {
                    ((TextView) findViewById(R.id.tvVoucher20)).setText("有效期至" + detailBean.time);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
