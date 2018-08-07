package cn.vpfinance.vpjr.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.Utils;

/**
 * 验证码
 */
public class CaptchaHelper {

    private Context mContext;
    private Button mButton;
    private TextView mVoice;
    private static final int SMS_TIME = 120;
    private static final int VOICE_TIME = 90;
    private int mDelaySeconds = VOICE_TIME;
    private int captchaState = 0; //0初始化 1短信ing 2短信ed 3语音ing 4语音ed

    private Handler mSmsHandler = new Handler();
    private Handler mVoiceHandler = new Handler();

    public CaptchaHelper(Context context,Button button, TextView voice) {
        mContext = context;
        mButton = button;
        mVoice = voice;
    }

    /**
     *
     * @param mHttpService
     * @param phone
     * @param type 注册一定要加type=1
     */
    public void smsStart(HttpService mHttpService,String phone,String type){
        if (mButton == null || captchaState == 1)    return;
        captchaState = 1;
        mDelaySeconds =  SMS_TIME;
        if (mHttpService != null) {
            mHttpService.getVerifyCode(null, null, null, phone, null,type);
        }
//        Utils.Toast(mContext,"短信已经发出，请填写短信中的验证码");
        mSmsHandler.postDelayed(mSmsCallback, 1000);
        mButton.setEnabled(false);
        mButton.setText("重新获取(" + mDelaySeconds + ")");
    }

    public void smsFinish(){
        if (mButton == null)    return;
        captchaState = 2;
        mDelaySeconds = 0;
        mButton.setText("获取验证码");
        mButton.setEnabled(true);
        mSmsHandler.removeCallbacks(mSmsCallback);
    }

    /**
     *
     * @param mHttpService
     * @param phone
     * @param type 注册一定要加type=1
     */
    public void voiceStart(HttpService mHttpService,String phone,String type){
        if (mVoice == null || captchaState == 3)    return;

        if (captchaState == 1){
            Utils.Toast(mContext,"短信获取中，请填写短信中的验证码");
            return;
        }

        mButton.setText("获取验证码");
        mButton.setEnabled(false);
        if (mHttpService != null) {
            mHttpService.getVoiceCaptcha(phone,type);
        }
        captchaState = 3;
        mDelaySeconds =  VOICE_TIME;
        mVoiceHandler.postDelayed(mVoiceCallback, 1000);
        mVoice.setEnabled(false);
        mVoice.setText("语音验证码(" + mDelaySeconds + ")");
    }

    public void voiceFinish(){
        if (mVoice == null)    return;

        mButton.setText("获取验证码");
        mButton.setEnabled(true);
        mSmsHandler.removeCallbacks(mSmsCallback);

        captchaState = 4;
        mDelaySeconds = 0;
        mVoice.setText("语音验证码");
        mVoice.setEnabled(true);
        mVoiceHandler.removeCallbacks(mVoiceCallback);
    }

    public void onRecycle(){
        try{
            mDelaySeconds =  0;
            mButton = null;
            mSmsHandler.removeCallbacks(mSmsCallback);
            mVoice = null;
            mVoiceHandler.removeCallbacks(mVoiceCallback);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Runnable mSmsCallback = new Runnable()
    {
        @Override
        public void run() {
            if (mButton == null)    return;
            mDelaySeconds --;
            if(mDelaySeconds <= 0)
            {
                smsFinish();
                return;
            }

            mButton.setText("验证码(" + mDelaySeconds + ")");
            mSmsHandler.postDelayed(this, 1000);
        }
    };

    private Runnable mVoiceCallback = new Runnable()
    {
        @Override
        public void run() {
            if (mButton == null)    return;
            mDelaySeconds --;
            if(mDelaySeconds <= 0)
            {
                voiceFinish();
                return;
            }

            mVoice.setText("语音验证码(" + mDelaySeconds + ")");
            mVoiceHandler.postDelayed(this, 1000);
        }
    };
}
