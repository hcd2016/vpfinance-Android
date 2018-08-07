package cn.vpfinance.vpjr.module.setting;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.PrivateSettingBean;
import cn.vpfinance.vpjr.util.Common;

/**
 * Created by Administrator on 2016/6/14.
 */
public class PrivateSettingAcitvity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    @Bind(R.id.titleBar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.allow_pub)
    SwitchCompat    mAllowPub;
    @Bind(R.id.allow_invest)
    SwitchCompat    mAllowInvest;
    @Bind(R.id.allow_medal)
    SwitchCompat    mAllowMedal;
    @Bind(R.id.allow_friend)
    SwitchCompat    mAllowFriend;
    private HttpService mHttpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_privacy);
        ButterKnife.bind(this);
        mHttpService = new HttpService(this,this);
        mTitleBar.setTitle("个人隐私").setHeadBackVisible(View.VISIBLE);
        mHttpService.getPrivateSetting();

        mAllowInvest.setOnCheckedChangeListener(this);
        mAllowFriend.setOnCheckedChangeListener(this);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;

        if (reqId == ServiceCmd.CmdId.CMD_LookPrivateSetting.ordinal()){
            PrivateSettingBean bean = mHttpService.onGetPrivateSetting(json);
            if (bean == null || bean.getData() == null || bean.getData().size() == 0)   return;

            List<PrivateSettingBean.DataBean> data = bean.getData();
            for (PrivateSettingBean.DataBean dataBean : data) {
                String key = dataBean.getSettingKey();
                if ("showTender".equals(key)){
                    String value = dataBean.getSettingValue();
                    if (TextUtils.isEmpty(value))   return;
                    mAllowInvest.setChecked("1".equals(value) ? true : false);//1显示
                }
                if ("showFriends".equals(key)){
                    String value = dataBean.getSettingValue();
                    if (TextUtils.isEmpty(value))   return;
                    mAllowFriend.setChecked("1".equals(value) ? true : false);//1显示
                }
            }
        }else if(reqId == ServiceCmd.CmdId.CMD_ChangePrivateSetting.ordinal()){
            String info = json.optString("info");//1成功2失败
//            Logger.e("修改："+info);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        String value = isChecked ? "1" : "2";
        String key = "";
        switch (id){
            case R.id.allow_invest:
                key = "showTender";
                break;
            case R.id.allow_friend:
                key = "showFriends";
                break;
        }
        mHttpService.getUpdatePrivateSetting(key,value);
    }
}
