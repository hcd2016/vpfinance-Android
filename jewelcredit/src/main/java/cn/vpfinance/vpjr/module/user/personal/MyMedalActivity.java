package cn.vpfinance.vpjr.module.user.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.AllMedalsBean;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.view.MedalXImageView;

/**
 * 我的勋章
 */
public class MyMedalActivity extends BaseActivity {

    @Bind(R.id.titleBar)
    ActionBarLayout mTitleBar;
    @Bind(R.id.hadMedals)
    GridLayout mHadMedals;
    @Bind(R.id.noMedals)
    GridLayout mNoMedals;


    private Context mContext;
    private HttpService mHttpService;
    private int accountType = Constant.AccountBank;

    public static void goThis(Context context,int accountType){
        Intent intent = new Intent(context,MyMedalActivity.class);
        intent.putExtra("accountType",accountType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarCompat1.translucentStatusBar(this);
        setContentView(R.layout.activity_my_medal);

        if (getIntent() != null){
            accountType = getIntent().getIntExtra(Constant.AccountType,Constant.AccountBank);
        }

        mContext = this;
        ButterKnife.bind(this);

        mHttpService = new HttpService(this, this);
        mTitleBar.setTitle("我的勋章").setHeadBackVisible(View.VISIBLE).setTransparent().setFakeStatusBar(false);

        if (AppState.instance().logined()){
            User user = DBUtils.getUser(this);
            if (user != null){
                mHttpService.getAllMedals(accountType,user.getUserId()+"");
            }
        }else{
            Toast.makeText(this, "请登录.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_LookAllMedals.ordinal()) {
            AllMedalsBean bean = mHttpService.onGetAllMedals(json);
//            Logger.e("bean:"+bean.toString());
            initView(bean);
        }
    }

    private void initView(AllMedalsBean bean) {
        if (bean == null)   return;
        List<AllMedalsBean.HadMedalsBean> hadMedals = bean.getHadMedals();
        if (hadMedals != null){
            if (hadMedals.size() == 0){
                findViewById(R.id.hadMedalsTitle).setVisibility(View.GONE);
                mHadMedals.setVisibility(View.GONE);
            }else{
                findViewById(R.id.hadMedalsTitle).setVisibility(View.VISIBLE);
                mHadMedals.setVisibility(View.VISIBLE);
                for (final AllMedalsBean.HadMedalsBean hadMedal : hadMedals) {
                    View view = View.inflate(mContext, R.layout.item_medal, null);
                    ((TextView) view.findViewById(R.id.tvName)).setText(hadMedal.getName());

                    MedalXImageView imageView = (MedalXImageView) view.findViewById(R.id.ivMedals);
                    imageView.setStyle(MedalXImageView.STYLE_BIG);
                    imageView.setBackground(hadMedal.getLogo()+"3.png");
                    imageView.setXNum(hadMedal.getNumber());

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext,MedalDetailActivity.class);
                            int id = hadMedal.getId();
                            intent.putExtra(MedalDetailActivity.MEDAL_ID, ""+id);
                            intent.putExtra(MedalDetailActivity.MEDAL_STATUS, true);
                            intent.putExtra(MedalDetailActivity.MEDAL_NAME, ""+hadMedal.getName());
                            intent.putExtra(MedalDetailActivity.MEDAL_DESCRIPTION, ""+hadMedal.getDescription());
                            intent.putExtra(MedalDetailActivity.MEDAL_LOGO, ""+hadMedal.getLogo());
                            intent.putExtra(MedalDetailActivity.MEDAL_CONDITION, ""+hadMedal.getCondition());
                            gotoActivity(intent);
                        }
                    });
                    mHadMedals.addView(view);
                }
            }
        }
        List<AllMedalsBean.NoMedalsBean> noMedals = bean.getNoMedals();
        if (noMedals != null){
            for (final AllMedalsBean.NoMedalsBean noMedal : noMedals) {
                View view = View.inflate(mContext, R.layout.item_medal, null);
                ((TextView) view.findViewById(R.id.tvName)).setText(noMedal.getName());

                MedalXImageView imageView = (MedalXImageView) view.findViewById(R.id.ivMedals);
                imageView.setBackground(noMedal.getLogo()+"3.png");
                imageView.setXNum(0);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext,MedalDetailActivity.class);
                        int id = noMedal.getId();
                        intent.putExtra(MedalDetailActivity.MEDAL_ID, ""+id);
                        intent.putExtra(MedalDetailActivity.MEDAL_STATUS, false);
                        intent.putExtra(MedalDetailActivity.MEDAL_NAME, ""+noMedal.getName());
                        intent.putExtra(MedalDetailActivity.MEDAL_DESCRIPTION, ""+noMedal.getDescription());
                        intent.putExtra(MedalDetailActivity.MEDAL_LOGO, ""+noMedal.getLogo());
                        intent.putExtra(MedalDetailActivity.MEDAL_CONDITION, ""+noMedal.getCondition());
                        gotoActivity(intent);
                    }
                });
                mNoMedals.addView(view);
            }
        }
    }
}
