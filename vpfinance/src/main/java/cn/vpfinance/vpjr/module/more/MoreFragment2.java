package cn.vpfinance.vpjr.module.more;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jewelcredit.model.AppUpdateInfo;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.NewAppUpdateInfo;
import cn.vpfinance.vpjr.gson.UserInfoBean;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.module.setting.PersonalInfoActivity;
import cn.vpfinance.vpjr.module.user.personal.InvestTopActivity;
import cn.vpfinance.vpjr.module.user.personal.InviteGiftActivity;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.view.CircleImg;

/**
 * Created by Administrator on 2015/10/27.
 */
public class MoreFragment2 extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.iv_avatar)
    CircleImg ivAvatar;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.tv_vip_level)
    TextView tvVipLevel;
    @Bind(R.id.tv_signature)
    TextView tvSignature;
    @Bind(R.id.btn_risk)
    TextView btnRisk;
    @Bind(R.id.tv_unlogin_desc)
    TextView tvUnloginDesc;
    @Bind(R.id.ll_logined_header_container)
    LinearLayout llLoginedHeaderContainer;
    @Bind(R.id.ll_header_contianer)
    LinearLayout llHeaderContianer;
    private HttpService mHttpService;
    private TextView etRealname;
    private String userRealname;
    private View view;
    private AppUpdateInfo info;
    private boolean isUpdate = false;
    private TextView tvVersion;
    private NewAppUpdateInfo mAppUpdateInfo;
    private String mVersion;
    private UserInfoBean mUserInfoBean;

    public static MoreFragment2 newInstance() {
        return new MoreFragment2();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_more2, null);
        ButterKnife.bind(this, view);

        ((ActionBarLayout) view.findViewById(R.id.titleBar)).reset().setTitle("更多")
                .setImageButtonRight(R.mipmap.icon_set, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {//设置
                        if (!AppState.instance().logined()) {
                            gotoActivity(LoginActivity.class);
                        } else {
                            gotoActivity(PersonalInfoActivity.class);
                        }
                    }
                });
        tvVersion = ((TextView) view.findViewById(R.id.tvVersion));

        mHttpService = new HttpService(getActivity(), this);
        mVersion = Utils.getVersion(getActivity());
        tvVersion.setText(mVersion);
        tvVersion.setTextColor(Color.parseColor("#999999"));
        mHttpService.getVersionCheck(mVersion);
        //        mHttpService.checkAppVersion();//旧的
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MoreFragment");
        if (AppState.instance().logined()) {
            mHttpService.getUserInfo();
        }
        initView(view);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MoreFragment");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView(View view) {
        view.findViewById(R.id.clickNewAct).setOnClickListener(this);
        view.findViewById(R.id.clickPraise).setOnClickListener(this);
        view.findViewById(R.id.clickAboutMe).setOnClickListener(this);
        view.findViewById(R.id.clickNotice).setOnClickListener(this);
        view.findViewById(R.id.clickHelp).setOnClickListener(this);
        view.findViewById(R.id.clickSafe).setOnClickListener(this);
        view.findViewById(R.id.clickRepay).setOnClickListener(this);
//        ((TextView)  view.findViewById(R.id.currVer)).setText("当前版本为" + Utils.getVersion(getActivity()));
//        view.findViewById(R.id.clickShare).setOnClickListener(this);
//        view.findViewById(R.id.clickCall).setOnClickListener(this);
        view.findViewById(R.id.clickVersionUpdate).setOnClickListener(this);
        view.findViewById(R.id.clickRisk).setOnClickListener(this);
        view.findViewById(R.id.clickInfoShow).setOnClickListener(this);
        if (AppState.instance().logined()) {//登录与未登录头部
            llLoginedHeaderContainer.setVisibility(View.VISIBLE);
            tvUnloginDesc.setVisibility(View.GONE);
            ivAvatar.setImageResource(R.mipmap.profile);
        } else {
            ivAvatar.setImageResource(R.mipmap.profile_gray);
            tvUnloginDesc.setVisibility(View.VISIBLE);
            llLoginedHeaderContainer.setVisibility(View.GONE);
        }
    }


    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_VERSION_CHECK.ordinal()) {
            mAppUpdateInfo = HttpService.onGetVersion(json);
            if (mAppUpdateInfo != null && !TextUtils.isEmpty(mAppUpdateInfo.appVersion)) {
                if (Utils.compareVersion(mAppUpdateInfo.appVersion, mVersion) > 0) {
                    isUpdate = true;
                    tvVersion.setText("最新版本" + mAppUpdateInfo.appVersion);
                    tvVersion.setTextColor(Color.RED);
                } else {
                    isUpdate = false;
                }
            }
        }
        if (reqId == ServiceCmd.CmdId.CMD_APPUPDATE.ordinal()) {
            info = new AppUpdateInfo();
            mHttpService.onGetVersion2(json, info);
            if (info != null & (!TextUtils.isEmpty(info.appVersion))) {
                if (Utils.compareVersion(info.appVersion, Utils.getVersion(getActivity())) > 0) {
                    isUpdate = true;
                    tvVersion.setText("最新版本" + info.appVersion);
                    tvVersion.setTextColor(Color.RED);
                } else {
                    isUpdate = false;
                    tvVersion.setText(Utils.getVersion(getActivity()));
                    tvVersion.setTextColor(Color.parseColor("#999999"));
                }
            }
        }
        if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()) {
            mUserInfoBean = mHttpService.onGetUserInfo(json);
            setData(mUserInfoBean);
        }
    }

    private void setData(UserInfoBean mUserInfoBean) {
        if (null != mUserInfoBean) {
            Glide.with(getActivity()).load(HttpService.mBaseUrl + mUserInfoBean.headImg).
                    dontAnimate().error(R.mipmap.profile).into(ivAvatar);
            tvUserName.setText(mUserInfoBean.userName);
            tvSignature.setText(TextUtils.isEmpty(mUserInfoBean.signature) ? "未设置签名" : mUserInfoBean.signature);//个人签名
            //todo vip等级待加

        }
    }

    @Override
    public void onClick(View v) {
        ServiceCmd.CmdId cmdId = null;
        String method = null;
        String url;
        switch (v.getId()) {
            case R.id.clickNewAct:
                User user = DBUtils.getUser(mContext);
                String uid = "";
                if (user != null) {
                    uid = "?uid=" + user.getUserId();
                }
                String activiceUrl = "/AppContent/toPlatformBox" + uid;
                gotoWeb(activiceUrl, "活动专区");
                break;

            case R.id.clickPraise:
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setData(uri);
                if (isIntentAvailable(getActivity(), intent)) {
                    startActivity(intent);
                }
                break;
//            case R.id.clickRepay:
//                cmdId = ServiceCmd.CmdId.CMD_GONGGAO;
//                method = ServiceCmd.getMethodName(cmdId);
//                url = mHttpService.getServiceUrl(method);
//                gotoWeb(url, "还款公告");
//                break;
            case R.id.clickRepay://风云榜
//                InvestTopActivity.goThis(getActivity(), 1, "");
                gotoActivity(InvestTopNewActivity.class);
                break;
            case R.id.clickAboutMe:
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
                break;
//            case R.id.clickNotice:
//                cmdId = ServiceCmd.CmdId.CMD_NewAct;
//                method = ServiceCmd.getMethodName(cmdId);
//                url = mHttpService.getServiceUrl(method);
//                gotoWeb(url, "平台公告");
//                break;
            case R.id.clickNotice://邀请有礼
                InviteGiftActivity.goThis(getActivity());
                break;
            case R.id.clickHelp://更多_帮助中心
                gotoActivity(MoreHelpActivity.class);
                break;
            case R.id.clickSafe:
//                startActivity(new Intent(getActivity(), SafetyGuardActivity.class));
                gotoWeb("/security/assurance", "平台优势");
                break;
//            case R.id.clickShare:
//                gotoActivity(InviteGiftActivity.class);
//                break;
            case R.id.clickCall:
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:0755-86627551")));
                break;
            case R.id.clickVersionUpdate:
                if (isUpdate) {
                    Intent i = new Intent(getActivity(), VersionUpdateActivity.class);
//                    i.putExtra(VersionUpdateActivity.IS_NEED_UPDATE,isUpdate);
                    i.putExtra(VersionUpdateActivity.UPDATE_LOG, mAppUpdateInfo.updateLog);
                    i.putExtra(VersionUpdateActivity.UPDATE_URL, mAppUpdateInfo.downloadUrl);
                    startActivity(i);
                } else {
                    Utils.Toast(getActivity(), "已经是最新版本");
                }
                break;
            case R.id.clickRisk:
                gotoWeb("/registration/riskAgreement", "风险提示");
                break;
            case R.id.clickInfoShow:
                gotoWeb("/to/infoDisclosure", "信息披露");
                break;

        }
    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        if (context != null && intent != null) {
            final PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return list != null && list.size() > 0;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.ll_header_contianer, R.id.tv_unlogin_desc, R.id.btn_risk})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_header_contianer://点击头像
                if (!AppState.instance().logined()) {
                    gotoActivity(LoginActivity.class);
                } else {
                    gotoActivity(MyHomePageActivity.class);
                }
                break;
            case R.id.tv_unlogin_desc://未登录描述
                gotoActivity(LoginActivity.class);
                break;
            case R.id.btn_risk://风险评估
                if (!AppState.instance().logined()) {
                    gotoActivity(LoginActivity.class);
                } else {
                    gotoWeb("/h5/help/riskInvestigation?userId=" + DBUtils.getUser(getContext()).getUserId(), "风险评测");
                }
                break;
        }
    }
}
