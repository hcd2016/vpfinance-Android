package cn.vpfinance.vpjr.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.tdk.utils.HttpDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.App;
import cn.vpfinance.vpjr.util.Common;

/**
 * Created by Administrator on 2016/11/7.
 */
public class VerificationCodeDialog extends DialogFragment implements HttpDownloader.HttpDownloaderListener{

    private static int isRegisterType = 0;//是否是注册类型,如果是,值为1,3为修改经办人(企业)手机号
    @Bind(R.id.ver_code_tv)
    EditText  mVerCodeTv;
    @Bind(R.id.ver_code_img)
    ImageView mVerCodeImg;
    @Bind(R.id.refresh)
    ImageView mRefresh;
    @Bind(R.id.tv_error)
    TextView  mTvError;
    @Bind(R.id.tvCancel)
    TextView  mTvCancel;
    @Bind(R.id.tvAffirm)
    TextView  mTvAffirm;
    private String mImgCaptcha;

    public static final String PERSON_PHONE = "person_phone";
    public static final String CODE_TYPE    = "code_type";
    private String              mPhone;
    private int                 mType;
    private HttpService         mHttpService;
    private DisplayImageOptions mOptions;
    private SmsListener mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mPhone = bundle.getString(PERSON_PHONE);
            mType = bundle.getInt(CODE_TYPE);
        }
        mHttpService = new HttpService(getContext(), this);
        mHttpService.getImageCode();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_verification_code, container, false);
        ButterKnife.bind(this, view);
        initListener();
        return view;
    }

    private void initListener() {
        mVerCodeTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    mImgCaptcha = s.toString();
                }
            }
        });
    }

    public static VerificationCodeDialog newInstance(String phone, int type) {
        VerificationCodeDialog codeDialog = new VerificationCodeDialog();
        Bundle bundle = new Bundle();
        bundle.putString(PERSON_PHONE,phone);
        bundle.putInt(CODE_TYPE,type);
        codeDialog.setArguments(bundle);
        return codeDialog;
    }

    public static VerificationCodeDialog newInstance(String phone, int type,int registerType) {
        VerificationCodeDialog codeDialog = new VerificationCodeDialog();
        Bundle bundle = new Bundle();
        bundle.putString(PERSON_PHONE,phone);
        bundle.putInt(CODE_TYPE,type);
        codeDialog.setArguments(bundle);
        isRegisterType = registerType;
        return codeDialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.refresh, R.id.tvCancel, R.id.tvAffirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refresh:
                mHttpService.getImageCode();
                mTvError.setText("");
                break;
            case R.id.tvCancel:
                dismiss();
                break;
            case R.id.tvAffirm:
                if(isRegisterType == 1) {//是注册
                    mHttpService.getVerifyImageCode(mImgCaptcha+"",mPhone+"",mType+"","");
                }else if(isRegisterType == 0){
                    mHttpService.getVerifyImageCode(mImgCaptcha+"",mPhone+"",mType+"","1");
                }else if(isRegisterType == 3) {//是修改经办人(企业)手机号
                    mHttpService.getVerifyImageCode(mImgCaptcha+"",mPhone+"",mType+"","3");
                }
                mTvAffirm.setEnabled(false);
                break;
        }
    }

    public interface SmsListener{
        void smsStart(int type);
    }

    public void setSmsListener(SmsListener listener) {
        mListener = listener;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        mTvAffirm.setEnabled(true);
        if (json == null || (!isAdded()) || Common.isForceLogout(getContext(),json))    return;
        if (reqId == ServiceCmd.CmdId.CMD_IMAGE_CODE.ordinal()) {
            String imageUrl = json.optString("imageUrl");
            /*String jsd = json.optString("jsd");
            if (!TextUtils.isEmpty(jsd)) {

                if (mOptions == null) {
                    mOptions = new DisplayImageOptions.Builder()
                            .showImageOnLoading(R.drawable.bg_qrempty)
                            .showImageForEmptyUri(R.drawable.bg_qrempty)
                            .showImageOnFail(R.drawable.bg_qrempty)
                            .cacheInMemory(false)
                            .cacheOnDisk(false)
                            .extraForDownloader(jsd)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .build();
                }
//                Log.d("aa", "onHttpSuccess: " + jsd);
                ImageLoader.getInstance().displayImage(imageUrl, mVerCodeImg, mOptions);
            }*/
            Glide.with(this)
                    .load(imageUrl)
                    .asBitmap()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE )//禁用磁盘缓存
                    .skipMemoryCache( true )//跳过内存缓存
//                    .placeholder(R.mipmap.ic_head_default)
                    .into(mVerCodeImg);
        }

        if (reqId == ServiceCmd.CmdId.CMD_VERIFY_IMAGE_CODE.ordinal()) {
            String msg = json.optString("msg");
            String str = "";
            switch (msg) {
                case "0":
                    str = "发送失败";
                    mTvError.setText(str);
                    break;
                case "1":
                    str = "参数有误";
                    break;
                case "2":
                    str = "输入验证码错误";
                    mHttpService.getImageCode();
                    mVerCodeTv.requestFocus();
                    mVerCodeTv.setText("");
                    mTvError.setText(str);
                    break;
                case "3":
                    str = "手机号码格式不正确";
                    mTvError.setText(str);
                    break;
                case "4":
                    str = "操作太频繁，稍后再试";
                    mTvError.setText(str);
                    break;
                case "5":
                    str = "手机号己经存在";
                    mTvError.setText(str);
                    break;
                case "6"://6. 正确
                    str = "验证码校验成功";
                    mListener.smsStart(mType);//验证码校验成功  开启倒计时
                    /*if (mListener != null) {//校验成功后不需要重新回调发送短信的方法，服务器自动发送
                        mListener.smsStart(mHttpService,mPhone,mType);
                    }*/
                    dismiss();
                    break;
                case "7":
                    str = "手机号不存在";
                    break;
                default:
                    str = "其他错误";
                    break;
            }
            Utils.Toast(App.getAppContext(),str);
        }

    }

    @Override
    public void onHttpSuccess(int reqId, JSONArray json) {

    }

    @Override
    public void onHttpCache(int reqId) {

    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        mTvAffirm.setEnabled(true);
        Utils.Toast(App.getAppContext(),"加载数据失败，请检查您的网络...");
    }
}
