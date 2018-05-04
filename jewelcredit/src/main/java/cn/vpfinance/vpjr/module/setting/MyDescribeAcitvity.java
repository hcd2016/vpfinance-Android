package cn.vpfinance.vpjr.module.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.util.Common;

/**
 * Created by Administrator on 2016/6/13.
 */
public class MyDescribeAcitvity extends BaseActivity {
    @Bind(R.id.edit_des)
    EditText        mEditDes;
    @Bind(R.id.max_tv)
    TextView        mMaxTv;
    @Bind(R.id.titleBar)
    ActionBarLayout mTitleBar;
    private HttpService mHttpService;
    public static final String DES = "des";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_describe);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        mHttpService = new HttpService(this, this);
        mTitleBar.setTitle("设置签名").setHeadBackVisible(View.VISIBLE);

        Intent intent = getIntent();
        String des = intent.getStringExtra(DES);
        if (des == null) {
            des = "";
        }
        mEditDes.setText(des);
        mEditDes.selectAll();
        mMaxTv.setText(des.toCharArray().length + "/32");
        mEditDes.addTextChangedListener(mTextWatcher);

    }

    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            temp = s;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
            //          mTextView.setText(s);//将输入的内容实时显示
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            editStart = mEditDes.getSelectionStart();
            editEnd = mEditDes.getSelectionEnd();
            mMaxTv.setText(temp.length() + "/32");
            if (temp.length() > 32) {
                Toast.makeText(MyDescribeAcitvity.this,
                        "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT)
                        .show();
                s.delete(editStart - 1, editEnd);
                //                int tempSelection = editStart;
                //                mEditDes.removeTextChangedListener(this);
                //                mEditDes.setText(s);
                //                mEditDes.addTextChangedListener(this);
                //                mEditDes.setSelection(tempSelection);
            }

            mTitleBar.setActionRight("完成", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHttpService.getMyDescribe(String.valueOf(temp));
                }
            });
        }
    };

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_My_Describe.ordinal()) {
            String info = json.optString("info");
            if ("1".equals(info)) {
                Utils.Toast(this, "修改成功");
            } else {
                Utils.Toast(this, "修改失败");
            }
            finish();
        }
    }
}
