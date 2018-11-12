package cn.vpfinance.vpjr.module.setting;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.App;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.greendao.BankCard;
import cn.vpfinance.vpjr.greendao.BankCardDao;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.gson.UserInfoBean;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.model.UserHeadEvent;
import cn.vpfinance.vpjr.module.dialog.CommonDialogFragment;
import cn.vpfinance.vpjr.module.dialog.CommonTipsDialog;
import cn.vpfinance.vpjr.module.dialog.MyDialogFragment;
import cn.vpfinance.vpjr.module.home.MainActivity;
import cn.vpfinance.vpjr.module.user.BindBankHintActivity;
import cn.vpfinance.vpjr.module.user.personal.BankManageActivity;
import cn.vpfinance.vpjr.util.AlertDialogUtils;
import cn.vpfinance.vpjr.util.CameraGalleryUtils;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.util.EventStringModel;
import cn.vpfinance.vpjr.util.FileUtil;
import cn.vpfinance.vpjr.util.FormatUtils;
import cn.vpfinance.vpjr.util.ScreenUtil;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.view.CircleImg;
import cn.vpfinance.vpjr.view.popwindow.SelectPicPopupWindow;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2015/10/26.
 * 设置
 */
public class PersonalInfoActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_bind_phone_desc)
    TextView tvBindPhoneDesc;
    @Bind(R.id.tv_weixin_bind_desc)
    TextView tvWeixinBindDesc;
    @Bind(R.id.ll_weixin_bind_container)
    LinearLayout llWeixinBindContainer;
    private LinearLayout modifypasword;
    private TextView realname;
    private TextView bindbankcard;
    private TextView logout;
    private HttpService mHttpService;
    private LinearLayout paypassword;
    private LinearLayout forgetPayPassword;
    private User user;
    private TextView tvUserName;
    private TextView userPhone;
    private TextView bindemail;
    private TextView isReName;
    private TextView isBandCark;
    private LinearLayout ll_bindbankcard;


    private LinearLayout ll_my_describe;
    private LinearLayout ll_bindemail;
    private LinearLayout ll_clickUserInfo;
    private LinearLayout ll_realname;
    private SwitchCompat lockSwitch;
    private boolean close = false;
    private String emailPass;
    private String email;
    private TextView isBandMail;
    private String cellPhone;
    private String newUserName;
    private String userName1;
    private MyDialogFragment myDialogFragment;
    private ActionBarLayout titlebar;
    public static final String NONENAME = "未设置用户名";
    private CircleImg mUserHead;
    private ImageView user_background;
    private boolean isUserHead = true;

    private SelectPicPopupWindow menuWindow;
    private static final int REQUESTCODE_PICK = 0;        // 相册选图标记
    private static final int REQUESTCODE_TAKE = 1;        // 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 2;    // 图片裁切标记
    private static final String IMAGE_FILE_NAME = "avatarImage.jpg";// 头像文件名称
    private String urlpath;            // 图片本地路径
    private static ProgressDialog pd;// 等待进度圈
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;//拍照权限请求
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 101;//读写权限请求
    private View mViewLl;
    private String mHeadImgUrl;
    private LinearLayout mPrivate_setting;
    private TextView my_describe;
    private UserInfoBean mUserInfoBean;
    private int mBottomStatusHeight = 0;
    private LinearLayout ll_bind_phone;
    private TextView isHxBandCarkStatus;
    private boolean isPersonType;

    private void initFind() {
        titlebar = (ActionBarLayout) findViewById(R.id.titleBar);
        isBandMail = (TextView) findViewById(R.id.isBandMail);
        ll_bindbankcard = (LinearLayout) findViewById(R.id.ll_bindbankcard);
        ll_my_describe = (LinearLayout) findViewById(R.id.ll_my_describe);
        ll_bindemail = (LinearLayout) findViewById(R.id.ll_bindemail);
        ll_realname = (LinearLayout) findViewById(R.id.ll_realname);
        tvUserName = (TextView) findViewById(R.id.userName);
        userPhone = (TextView) findViewById(R.id.userPhone);
        ll_clickUserInfo = (LinearLayout) findViewById(R.id.clickUserInfo);
        bindemail = (TextView) findViewById(R.id.bindemail);
        modifypasword = (LinearLayout) findViewById(R.id.modifypasword);
        paypassword = (LinearLayout) findViewById(R.id.paypassword);
        realname = (TextView) findViewById(R.id.realname);
        bindbankcard = (TextView) findViewById(R.id.bindbankcard);
        logout = (TextView) findViewById(R.id.logout);
        forgetPayPassword = (LinearLayout) findViewById(R.id.forgetPayPassword);
        isReName = (TextView) findViewById(R.id.isReName);
        isBandCark = (TextView) findViewById(R.id.isBandCark);
//        lockSwitch = (SwitchCompat) findViewById(R.id.switch1);
        mUserHead = (CircleImg) findViewById(R.id.userHead);
        user_background = (ImageView) findViewById(R.id.user_background);
        mViewLl = findViewById(R.id.view_ll);
        mPrivate_setting = (LinearLayout) findViewById(R.id.private_setting);
        my_describe = (TextView) findViewById(R.id.my_describe);
        ll_bind_phone = (LinearLayout) findViewById(R.id.ll_bind_phone);
        isHxBandCarkStatus = ((TextView) findViewById(R.id.isHxBandCarkStatus));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mBottomStatusHeight = ScreenUtil.getBottomStatusHeight(this);
        SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(this);
        isPersonType = sp.getBooleanValue(SharedPreferencesHelper.KEY_ISPERSONTYPE);
        initFind();
        mHttpService = new HttpService(this, this);
        user = DBUtils.getUser(this);
        initView();
        mHttpService.getBankCard(AppState.instance().getSessionCode());
        mHttpService.getUserInfo();
        setHeadImage();
        setBgImage();
//        BindBankHintActivity.goThis(this,DBUtils.getUser(this).getUserId()+"");
    }

    private void setHeadImage() {
        if (user == null) {
            return;
        }
        String headUrl = SharedPreferencesHelper.getInstance(this).getStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId());
        if (headUrl == null) {
            mUserHead.setImageResource(R.drawable.user_head);
        } else {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(headUrl, mUserHead);
        }
    }

    private void setBgImage() {
        if (user == null) {
            return;
        }
        String bgUrl = SharedPreferencesHelper.getInstance(this).getStringValue(SharedPreferencesHelper.USER_BACKGROUND_URL + user.getUserId());
        if (bgUrl == null || bgUrl.equals(HttpService.mBaseUrl)) {
            return;
        }
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(bgUrl, user_background);
    }

    protected void initView() {
        titlebar.setTitle("设置").setHeadBackVisible(View.VISIBLE);
        if (user != null) {
            String cellPhone = user.getCellPhone();
            userName1 = user.getUserName().trim();
            if (TextUtils.isEmpty(userName1)) {
                userName1 = NONENAME;
                //mHttpService.getRandomUserName();
            }
            tvUserName.setText(userName1);
            if (!TextUtils.isEmpty(user.getCellPhone())) {
                userPhone.setText(FormatUtils.hidePhone(user.getCellPhone()));
            }

            if (!TextUtils.isEmpty(user.getRealName())) {
                isReName.setText("已认证");
                isReName.setTextColor(getResources().getColor(R.color.text_999999));
            } else {
                isReName.setText("未认证");
                isReName.setTextColor(getResources().getColor(R.color.red_text));
            }
        }

        ll_my_describe.setOnClickListener(this);
        mPrivate_setting.setOnClickListener(this);
        mUserHead.setOnClickListener(this);
        user_background.setOnClickListener(this);
//        modifypasword.setOnClickListener(this);
        paypassword.setOnClickListener(this);
        ll_realname.setOnClickListener(this);
        ll_bindbankcard.setOnClickListener(this);
        logout.setOnClickListener(this);
        ll_bindemail.setOnClickListener(this);
        forgetPayPassword.setOnClickListener(this);
        ll_bind_phone.setOnClickListener(this);
        llWeixinBindContainer.setOnClickListener(this);
        ll_clickUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chargeUserName();
            }
        });

        findViewById(R.id.deposit_account_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserInfoBean != null || user != null) {
                    Long userId = user.getUserId();
                    if (!"1".equals(mUserInfoBean.isOpen)) {
                        boolean isRealName = !TextUtils.isEmpty(mUserInfoBean.realName);
                        AlertDialogUtils.openBankAccount(PersonalInfoActivity.this, isRealName, userId.toString());
                        return;
                    }

                    String link = HttpService.mBaseUrl + "/hx/account/manage?userId=" + userId.toString();
                    gotoWeb(link, "存管账户设置");
////                    启动存管账户设置
//                    DepositAccountSetActivity.startDepositAccountSetActivity(PersonalInfoActivity.this, mUserInfoBean);
                }
            }
        });
//        findViewById(R.id.changeHXPayPassword).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mUserInfoBean != null || user != null){
//                    Long userId = user.getUserId();
//                    if (!"1".equals(mUserInfoBean.isOpen)){
//                        boolean isRealName = !TextUtils.isEmpty(mUserInfoBean.realName);
//                        AlertDialogUtils.openBankAccount(PersonalInfoActivity.this,isRealName, userId.toString());
//                        return;
//                    }
//
//                    String link = HttpService.mBaseUrl+"/hx/account/manage?userId="+userId.toString();
//                    gotoWeb(link,"修改交易密码");
//                }
//            }
//        });
//        findViewById(R.id.changeHXBindPhone).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mUserInfoBean != null || user != null){
//                    Long userId = user.getUserId();
//                    if (!"1".equals(mUserInfoBean.isOpen)){
//                        boolean isRealName = !TextUtils.isEmpty(mUserInfoBean.realName);
//                        AlertDialogUtils.openBankAccount(PersonalInfoActivity.this,isRealName, userId.toString());
//                        return;
//                    }
//
//                    String link = HttpService.mBaseUrl+"/hx/account/manage?userId="+userId;
//                    gotoWeb(link,"修改绑定手机号");
//                }
//            }
//        });
        findViewById(R.id.ll_bind_hx_bankcard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserInfoBean != null || user != null || "1".equals(mUserInfoBean.isBindHxBank)) {
                    Long userId = user.getUserId();
                    if (!"1".equals(mUserInfoBean.isOpen)) {
                        boolean isRealName = !TextUtils.isEmpty(mUserInfoBean.realName);
                        AlertDialogUtils.openBankAccount(PersonalInfoActivity.this, isRealName, userId.toString());
                        return;
                    }
                    //跳转到存管绑卡第三方界面
                    BindBankHintActivity.goThis(PersonalInfoActivity.this, user.getUserId().toString());
                }
            }
        });

        findViewById(R.id.safety_centre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SafetyCentreActivity.startActivity(PersonalInfoActivity.this);
            }
        });
//        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(PersonalInfoActivity.this);
//        String lockStr = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_STRING);
//        lockSwitch.setChecked(!TextUtils.isEmpty(lockStr));
//        lockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    if (!close) {
//                        if (!AppState.instance().logined()) {
//                            startActivity(new Intent(PersonalInfoActivity.this, LoginActivity.class));
//                            finish();
//                            return;
//                        }
//                        startActivity(new Intent(PersonalInfoActivity.this, LockSetupActivity.class));
//
//                        ArrayMap<String, String> map = new ArrayMap<String, String>();
//                        map.put("switch", "on");
//                        MobclickAgent.onEvent(PersonalInfoActivity.this, "GestureLock", map);
//                    }
//                    close = false;
//
//                } else {
//                    final SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(PersonalInfoActivity.this);
//                    String patternString = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_STRING, null);
//                    if (patternString == null) {
//                        return;
//                    }
//
//                    TextInputDialogFragment tidf = TextInputDialogFragment.newInstance("登录验证", "请输入登录密码", false);
//                    tidf.setOnTextConfrimListener(new TextInputDialogFragment.onTextConfrimListener() {
//                        @Override
//                        public boolean onTextConfrim(String value) {
//                            if (value != null) {
//                                Md5Algorithm md5 = Md5Algorithm.getInstance();
//                                value = md5.md5Digest((value + HttpService.LOG_KEY).getBytes());
//                                String pwd = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_USER_PWD, null);
//                                if (value.equals(pwd)) {
//                                    preferencesHelper.removeKey(SharedPreferencesHelper.KEY_LOCK_STRING);
//                                    lockSwitch.setChecked(false);
//                                    close = false;
//
//                                    ArrayMap<String, String> map = new ArrayMap<String, String>();
//                                    map.put("switch", "off");
//                                    MobclickAgent.onEvent(PersonalInfoActivity.this, "GestureLock", map);
//
//                                    return true;
//                                } else {
//                                    Utils.Toast(PersonalInfoActivity.this, "密码错误!");
//                                    close = true;
//                                    lockSwitch.setChecked(true);
//                                }
//                            }
//                            return false;
//                        }
//                    });
//                    tidf.setOnTextCancleListener(new TextInputDialogFragment.onTextConfrimListener() {
//                        @Override
//                        public boolean onTextConfrim(String value) {
//                            close = true;
//                            lockSwitch.setChecked(true);
//                            return true;
//                        }
//                    });
//                    tidf.show(getSupportFragmentManager(), "inputPwd");
//                }
//            }
//        });

        boolean isNewUser = SharedPreferencesHelper.getInstance(this).getBooleanValue(SharedPreferencesHelper.KEY_IS_NEW_USER, false);
//        findViewById(R.id.container_lianlian).setVisibility(isNewUser ? View.GONE : View.VISIBLE);
        findViewById(R.id.container_lianlian).setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        newUserName = TextUtils.isEmpty(newUserName) ? userName1 : newUserName;
//        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(PersonalInfoActivity.this);
//        String lockStr = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_STRING);
//        lockSwitch.setChecked(!TextUtils.isEmpty(lockStr));
        mHttpService.getBankCard(AppState.instance().getSessionCode());
        mHttpService.getUserInfo();
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_getBankCard.ordinal()) {
            BankCard card = mHttpService.onGetBankCard(this, json);
            if (card != null) {
                isBandCark.setText("已绑定");
                isBandCark.setTextColor(getResources().getColor(R.color.text_999999));
            } else {
                isBandCark.setText("未绑定");
                isBandCark.setTextColor(getResources().getColor(R.color.red_text));
            }
        } else if (reqId == ServiceCmd.CmdId.CMD_updateUserBasicInfo.ordinal()) {
            String ret = mHttpService.onChangeName(json);
            if ("1".equals(ret)) {
                Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                final SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
                String lockPattenString = preferencesHelper.getStringValue(SharedPreferencesHelper.KEY_LOCK_STRING, null);
                if (lockPattenString != null && !NONENAME.equals(newUserName)) {
                    //                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_STRING  , newUserName);
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_SAVE_USER_NAME, newUserName);
                    preferencesHelper.putStringValue(SharedPreferencesHelper.KEY_LOCK_USER_NAME, newUserName);
                }
                user.setUserName(newUserName);
                DBUtils.getUserDao(PersonalInfoActivity.this).insertOrReplace(user);
                tvUserName.setText(newUserName);
            } else if ("2".equals(ret)) {
                Toast.makeText(this, "用户名已经被占用", Toast.LENGTH_SHORT).show();
            } else if("3".equals(ret)){
                Toast.makeText(this, "用户名不能为手机或邮箱格式", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "修改用户名失败", Toast.LENGTH_SHORT).show();
            }
        }

        if (reqId == ServiceCmd.CmdId.CMD_member_center.ordinal()) {
            mHttpService.onGetUserInfo(json, user);
            mUserInfoBean = mHttpService.onGetUserInfo(json);
            if (mUserInfoBean == null) return;
            mHeadImgUrl = mUserInfoBean.headImg;
            mHeadImgUrl = HttpService.mBaseUrl + mHeadImgUrl;

//            ImageLoader.getInstance().displayImage(BaseUrl.mBaseUrl + userInfoBean.background , user_background);
            if (TextUtils.isEmpty(mUserInfoBean.signature)) {
                my_describe.setText("未设置签名");
                my_describe.setTextColor(getResources().getColor(R.color.red_text));
            } else {
                my_describe.setText(mUserInfoBean.signature);
                my_describe.setTextColor(getResources().getColor(R.color.text_999999));
            }


            if ("1".equals(mUserInfoBean.isBindHxBank)) {
                isHxBandCarkStatus.setText("已激活");
            } else {
                isHxBandCarkStatus.setText("未激活");
            }

//            if (mUserInfoBean.customerType.equals("1")) {
//                tvBindPhoneDesc.setText("手机号绑定");
//            } else {
//                tvBindPhoneDesc.setText("经办人手机号");
//            }

            if (mUserInfoBean.isBindWx.equals("0")) {//未绑定微信
                tvWeixinBindDesc.setText("未绑定");
            } else {
                tvWeixinBindDesc.setText("已绑定");
            }
            if (user != null) {
                SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(this);
                String value = sp.getStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId());
                if (TextUtils.isEmpty(value) /*|| !mHeadImgUrl.equals(value)*/) {
                    sp.putStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId(), mHeadImgUrl);
                    setHeadImage();
                }
                cellPhone = user.getCellPhone();
                if (!TextUtils.isEmpty(user.getRealName())) {
                    isReName.setText("已认证");
                    isReName.setTextColor(getResources().getColor(R.color.text_999999));
                } else {
                    isReName.setText("未认证");
                    isReName.setTextColor(getResources().getColor(R.color.red_text));
                }
            }
            if (json != null) {
                emailPass = (String) json.opt("emailPass");
            }
            if(isPersonType) {
                boolean b = "1".equals(emailPass);
                isBandMail.setText(b ? "已绑定" : "未绑定");
                isBandMail.setTextColor(b ? getResources().getColor(R.color.text_999999) : getResources().getColor(R.color.red_text));
            }else {
                isBandMail.setText("已绑定");
            }


            SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(this);
            String phone = "";
            if (sp.getBooleanValue(SharedPreferencesHelper.KEY_ISPERSONTYPE)) {
                phone = FormatUtils.hidePhone(mUserInfoBean.phone);
            } else {
                phone = FormatUtils.hidePhone(mUserInfoBean.qqNum);
            }
            ((TextView) findViewById(R.id.phoneNum)).setText(phone);
        }
        if (reqId == ServiceCmd.CmdId.CMD_WEIXIN_UNBIND.ordinal()) {
            String msg = json.optString("msg");
            switch (msg) {
                case "0":
                    Utils.Toast("服务器异常");
                    break;
                case "1":
                    Utils.Toast("授权被取消");
                    tvWeixinBindDesc.setText("未绑定");
                    mUserInfoBean.isBindWx = "0";
                    break;
                case "2":
                    Utils.Toast("用户不存在");
                    break;
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.modifypasword:
//                gotoActivity(PasswordChangeActivity.class);
//                break;
            case R.id.paypassword:
                Intent intent = new Intent();
                intent.putExtra("index", 1);

                intent.setClass(this, PasswordChangeActivity.class);
                startActivity(intent);
                this.overridePendingTransition(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left);
                break;
            case R.id.forgetPayPassword:
                gotoActivity(ResetPayPasswordActivity.class);
                break;
            case R.id.ll_realname:
                gotoActivity(RealnameAuthActivity.class);
                break;
            case R.id.ll_bindbankcard:
                gotoActivity(BankManageActivity.class);
                break;
            case R.id.logout:
//                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage("是否退出登录");
//                builder.setPositiveButton("退出登录", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        App application = (App)getApplication();
//                        application.guideConfig.put(App.SHOW_SETUP_GUIDE, false);
//                        logout();
//                    }
//                });
//                builder.setNegativeButton("取消",null);
//                builder.create().show();
                final CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance("是否退出登录?", "", "退出登录", "取消");
                dialogFragment.setOnAllLinstener(new CommonDialogFragment.onAllListener() {
                    @Override
                    public void clickOk() {
//                        App application = (App)getApplication();
//                        application.guideConfig.put(App.SHOW_SETUP_GUIDE, false);
                        logout();
                    }

                    @Override
                    public void clickCancel() {
                    }
                });
                dialogFragment.show(getFragmentManager(), "CommonDialogFragment");
                break;
            case R.id.ll_bindemail:
//                Intent intent1 = new Intent(PersonalInfoActivity.this, BindMailActivity2.class);
//                startActivity(intent1);
                BindOrChangeEmailActivity.startBindOrChangeEmailActivity(this);
//                EmailSMSVerificationActivity.startEmailSMSVerificationActivity(this);
                break;
//            case R.id.modifyUserName:
//                gotoActivity(ModifyUserNameActivity.class);
//                break;
            case R.id.user_background:
                isUserHead = false;
//                Window window = getWindow();
//                WindowManager.LayoutParams lp = window.getAttributes();
//                lp.dimAmount=0.5f;
//                window.setAttributes(lp);
//                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                menuWindow = new SelectPicPopupWindow(this, itemsOnClick);
                menuWindow.showAtLocation(mViewLl,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, mBottomStatusHeight);
                ScreenUtil.lightoff(this);
                menuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ScreenUtil.lighton(PersonalInfoActivity.this);
                    }
                });
                break;
            case R.id.userHead:
                isUserHead = true;
                menuWindow = new SelectPicPopupWindow(this, itemsOnClick);
                menuWindow.showAtLocation(mViewLl,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, mBottomStatusHeight);
                ScreenUtil.lightoff(this);
                menuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ScreenUtil.lighton(PersonalInfoActivity.this);
                    }
                });
                break;
            case R.id.private_setting:
                Intent intent2 = new Intent(this, PrivateSettingAcitvity.class);
                gotoActivity(intent2);
                break;
            case R.id.ll_my_describe:
                Intent intent3 = new Intent(this, MyDescribeAcitvity.class);
                intent3.putExtra(MyDescribeAcitvity.DES, mUserInfoBean.signature);
                gotoActivity(intent3);
                break;
            case R.id.ll_bind_phone:
                SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(this);
                if (sp.getBooleanValue(SharedPreferencesHelper.KEY_ISPERSONTYPE)) {
                    BindPhoneActivity.goThis(this,mUserInfoBean.phone);
                } else {
                    BindPhoneActivity.goThis(this,mUserInfoBean.qqNum);
                }
                break;
            case R.id.ll_weixin_bind_container://微信绑定
                final CommonTipsDialog commonTipsDialog = new CommonTipsDialog(this);
                if (mUserInfoBean.isBindWx.equals("0")) {
                    commonTipsDialog.setTitle("微信绑定");
                    commonTipsDialog.setTips("绑定微信可以通过微信快速登录,还可以收到短信消息提醒哦!");
                    commonTipsDialog.setOnRightClickListener(new CommonTipsDialog.OnRightClickListener() {
                        @Override
                        public void onRightClick() {//确定
                            IWXAPI mWxApi = ((App) getApplication()).mWxApi;
                            if (!mWxApi.isWXAppInstalled()) {
                                Utils.Toast("您还未安装微信客户端");
                                return;
                            }
                            SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(PersonalInfoActivity.this);
                            preferencesHelper.putBooleanValue(SharedPreferencesHelper.KEY_WX_BIND_IS_FROM_SETTING, true);
                            final SendAuth.Req req = new SendAuth.Req();
                            req.scope = "snsapi_userinfo";
                            req.state = "diandi_wx_login";
                            mWxApi.sendReq(req);
                            commonTipsDialog.dismiss();
                        }
                    });
                } else {
                    commonTipsDialog.setTitle("微信解绑");
                    commonTipsDialog.setTips("是否需要解除微信绑定,解除后将无法通过微信快速登录,也将不再收到微信消息提醒!");
                    commonTipsDialog.setOnRightClickListener(new CommonTipsDialog.OnRightClickListener() {
                        @Override
                        public void onRightClick() {//确定
                            mHttpService.unbindWeixin(DBUtils.getUser(PersonalInfoActivity.this).getUserId() + "");
                            commonTipsDialog.dismiss();
                        }
                    });
                }
                commonTipsDialog.setRightBtnTextColor(R.color.red_text);
                commonTipsDialog.show();
                break;
        }
    }

    public void onEventMainThread(EventStringModel event) {
        if (event != null & event.getCurrentEvent().equals(EventStringModel.EVENT_BIND_WEIXIN_SUCCESS_FROM_SETTING)) {//微信绑定成功
            mUserInfoBean.isBindWx = "1";
            tvWeixinBindDesc.setText("已绑定");
            mHttpService.getUserInfo();
        }
    }

    private void chargeUserName() {
        myDialogFragment = MyDialogFragment.newInstance("编辑用户名", "用户名可包含字母数字下划线", "确定", newUserName);
        myDialogFragment.setOnTextConfrimListener(new MyDialogFragment.onTextConfrimListener() {
            @Override
            public boolean onTextConfrim(String value) {
                if (value != null) {
//                    if (VerifyUtils.checkUserName(PersonalInfoActivity.this, value)) {
                        newUserName = value;
                        mHttpService.updateUserBasicInfo(value, "" + user.getUserId());
//                    } else {
//                        Utils.Toast(PersonalInfoActivity.this, "用户名不能为手机或邮箱格式");
//                    }
                }
                return false;
            }
        });
        myDialogFragment.show(getSupportFragmentManager(), "editName");
    }

    private void logout() {

        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;
        BankCardDao dao;
        UserDao userDao;

        dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME, null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        dao = daoSession.getBankCardDao();
        userDao = daoSession.getUserDao();
        dao.deleteAll();
        userDao.deleteAll();

        SharedPreferencesHelper preferencesHelper = SharedPreferencesHelper.getInstance(this);
        preferencesHelper.removeKey(SharedPreferencesHelper.KEY_LOCK_STRING);
        preferencesHelper.removeKey(SharedPreferencesHelper.KEY_LOCK_USER_ID);

        ((App) getApplication()).isLogin = false;
        mHttpService.logout();
        AppState.instance().logout();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                // 拍照
                case R.id.takePhotoBtn:
//                    getPhotoFromCamera();
                    CameraGalleryUtils.cameraAndClipByPermission(PersonalInfoActivity.this, CameraGalleryUtils.getSaveFile(HEADER_PHOTO));
                    break;
                // 相册选择图片
                case R.id.pickPhotoBtn:
//                    getPhotoFromPictureLibrary();
                    CameraGalleryUtils.galleryAndClipByPermission(PersonalInfoActivity.this, CameraGalleryUtils.getSaveFile(HEADER_PHOTO));
                    break;
                default:
                    break;
            }
        }
    };


    private static final String HEADER_PHOTO = "header_photo.png";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CameraGalleryUtils.CAMERA_REQUEST_CODE:
                CameraGalleryUtils.onCameraActivityResult(this, HEADER_PHOTO);
                break;
            case CameraGalleryUtils.CLIP_REQUEST_CODE:
                File file = CameraGalleryUtils.getSaveFile(HEADER_PHOTO);
                if (file != null && file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    Drawable drawable = new BitmapDrawable(null, bitmap);
                    urlpath = file.getAbsolutePath();

                    ServiceCmd.CmdId cmdId = null;
                    if (drawable != null) {
                        mUserHead.setImageDrawable(drawable);
                    } else {
                        Utils.Toast(this, "选择图片错误");
                    }
                    EventBus.getDefault().post(new UserHeadEvent(drawable));
                    cmdId = ServiceCmd.CmdId.CMD_User_HeadImage;

                    String method = ServiceCmd.getMethodName(cmdId);
                    String url = HttpService.mBaseUrl + method;
//                    File file = new File(this.getFilesDir(), imgName);
                    uploadImage(url, file);
                }
                break;
            case CameraGalleryUtils.GALLERY_KITKAT_REQUEST_CODE:
                CameraGalleryUtils.onGalleryKitkatActivityResult(this, data, HEADER_PHOTO);
                break;
            case CameraGalleryUtils.GALLERY_REQUEST_CODE:
                CameraGalleryUtils.onGalleryActivityResult(this, data, HEADER_PHOTO);
                break;
        }
        /*switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                if (isUserHead) {
                    if (data != null) {
                        startPhotoZoom(data.getData());
                    }
                }else {
                    doPhoto(requestCode, data);
                    }
                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
                if (isUserHead) {
                    File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                    startPhotoZoom(Uri.fromFile(temp));
                }else {
                    doPhoto(requestCode, data);
                }

                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
//                    setPicToView(data,isUserHead ? user.getUserId() + "vpjr.jpg" : user.getUserId() + "vpjrbg.jpg");
                    setPicToView(data, user.getUserId() + "vpjr.jpg");
                }
                break;
        }*/
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata, String imgName) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            // 取得SDCard图片路径做显示
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(null, photo);
            urlpath = FileUtil.saveFile(this, imgName, photo);

            ServiceCmd.CmdId cmdId = null;
//            if (isUserHead) {
            if (drawable != null) {
                mUserHead.setImageDrawable(drawable);
            } else {
                Utils.Toast(this, "选择图片错误");
            }
            EventBus.getDefault().post(new UserHeadEvent(drawable));
            cmdId = ServiceCmd.CmdId.CMD_User_HeadImage;
//            } else {
//                user_background.setImageDrawable(drawable);
//                cmdId = ServiceCmd.CmdId.CMD_Upload_BackGround;
//            }
            String method = ServiceCmd.getMethodName(cmdId);
            String url = HttpService.mBaseUrl + method;
            File file = new File(this.getFilesDir(), imgName);
            uploadImage(url, file);
        }
    }


    /**
     * 拍照取得图片
     */
    public void getPhotoFromCamera() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {

            if (isUserHead) {

                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //指定调用相机拍照后的照片存储的路径
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                startActivityForResult(takeIntent, REQUESTCODE_TAKE);
            } else {
                takePhoto();
            }
        }

    }

    /**
     * 从相册库里取得图片
     */
    public void getPhotoFromPictureLibrary() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
        } else {
            if (isUserHead) {

                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                // 如果要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(pickIntent, REQUESTCODE_PICK);
            } else {
                pickPhoto();
            }
        }

    }

    /**
     * 申请权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == CameraGalleryUtils.PERMISSIONS_REQUEST_GALLERY) {
                CameraGalleryUtils.galleryAndClip(this, CameraGalleryUtils.getSaveFile(HEADER_PHOTO));
            } else if (requestCode == CameraGalleryUtils.PERMISSIONS_REQUEST_CAMERA) {
                CameraGalleryUtils.cameraAndClip(this, CameraGalleryUtils.getSaveFile(HEADER_PHOTO));
            }
        }

        /*if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //指定调用相机拍照后的照片存储的路径
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                startActivityForResult(takeIntent, REQUESTCODE_TAKE);
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                // 如果要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image*//*");
                startActivityForResult(pickIntent, REQUESTCODE_PICK);
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    private void uploadImage(String url, File file) {


        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"userid\""),
                        RequestBody.create(null, user.getUserId() + ""))
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"fileurl\"; " +
                                "filename=\"temphead.jpg\""), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("APP-VERSION",Utils.getVersion(App.getAppContext()))
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject json = null;
                    try {
                        String data = response.body().string();
                        json = new JSONObject(data);
                        String imgUrl = json.optString("url");
                        String state = json.optString("state");
//                        SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(PersonalInfoActivity.this);
//                        sp.putStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId(), BaseUrl.mBaseUrl + imgUrl);
                        String str = "";
                        switch (state) {
                            case "0":
                                str = "更换成功";
                                break;
                            case "1":
                                str = "上传失败";
                                break;
                            case "2":
                                str = "图片格式不对";
                                break;
                        }
                        final String finalStr = str;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalInfoActivity.this, finalStr, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    Handler mHandler = new Handler();

    /**
     * 选择图片后，获取图片的路径
     *
     * @param requestCode
     * @param data
     */
    private void doPhoto(int requestCode, Intent data) {

        if (requestCode == REQUESTCODE_PICK) {
            if (data == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
            photoUri = data.getData();
//            Logger.e("photoUri:"+photoUri);
            if (photoUri == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
        }

        String[] pojo = {MediaStore.MediaColumns.DATA};
        // The method managedQuery() from the type Activity is deprecated
//        Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
        Cursor cursor = getContentResolver().query(photoUri, pojo, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);

            cursor.moveToFirst();
            picPath = cursor.getString(columnIndex);

            if (Integer.parseInt(Build.VERSION.SDK) < 14) {
                cursor.close();
            }
        }

        BitmapFactory.Options option = new BitmapFactory.Options();
        // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图
        option.inSampleSize = 1;

        // 根据图片的SDCard路径读出Bitmap
//        Logger.e("picPath:"+picPath);
        Bitmap bm = BitmapFactory.decodeFile(picPath, option);
        //部分手机获取的uri直接是图片路径file:///storage/emulated/0/DCIM/Camera/IMG_20160225_141452.jpg
        if (bm == null && photoUri != null) {
            bm = BitmapFactory.decodeFile(photoUri.getPath(), option);
            picPath = photoUri.getPath();
        }
        // 显示在图片控件上
        if (bm != null) {
            user_background.setImageBitmap(bm);
        } else {
            Utils.Toast(this, "选择图片错误");
        }

        String method = ServiceCmd.getMethodName(ServiceCmd.CmdId.CMD_Upload_BackGround);
        String url = HttpService.mBaseUrl + method;
        if (bm != null) {
            uploadImage(url, new File(picPath));
        }

    }

    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ContentValues values = new ContentValues();
            photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, REQUESTCODE_TAKE);
        } else {
            Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }

    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);//这个意图 部分手机会有问题
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUESTCODE_PICK);

    }

    private Uri photoUri;
    /**
     * 获取到的图片路径
     */
    private String picPath = "";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
