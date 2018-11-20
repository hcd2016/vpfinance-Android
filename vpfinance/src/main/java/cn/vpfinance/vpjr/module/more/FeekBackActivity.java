package cn.vpfinance.vpjr.module.more;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;

/**
 * 意见反馈
 */
public class FeekBackActivity extends BaseActivity {
    @Bind(R.id.title_bar)
    ActionBarLayout titleBar;
    @Bind(R.id.ll_choise_type_container)
    LinearLayout llChoiseTypeContainer;
    @Bind(R.id.et_feedback)
    EditText etFeedback;
    @Bind(R.id.iv_pic)
    ImageView ivPic;
    @Bind(R.id.tv_question_desc)
    TextView tvQuestionDesc;
    @Bind(R.id.iv_arrow)
    ImageView ivArrow;
    @Bind(R.id.tv_btn_comit)
    TextView tvBtnComit;
    private PopupWindow popupWindow;
    public final int IMAGE_REQUEST_CODE = 0;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        titleBar.setTitle("我要反馈").setHeadBackVisible(View.VISIBLE);
        etFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 200) {
                    Utils.Toast("不能大于200字");
                    etFeedback.setText(editable.toString().substring(0, 200));
                    etFeedback.setSelection(etFeedback.getText().length());
                }
            }
        });
    }

    @OnClick({R.id.ll_choise_type_container, R.id.iv_pic,R.id.tv_btn_comit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_choise_type_container:
                showTypePop();
                ivArrow.setImageResource(R.drawable.list_arrow_down);
                tvQuestionDesc.setTextColor(Utils.getColor(R.color.text_999999));
                break;
            case R.id.iv_pic://选择照片
                //在这里跳转到手机系统相册里面
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
                break;
            case R.id.tv_btn_comit://提交反馈
                if(etFeedback.getText().toString().length() < 10) {
                    Utils.Toast("文本不能少于10个字");
                    return;
                }
                if(tvQuestionDesc.equals("选择反馈问题类型")) {
                    Utils.Toast("请选择反馈问题类型");
                    return;
                }
                //todo
                break;
        }
    }

    //类型pop
    private void showTypePop() {
        TypeClickListner typeClickListner = new TypeClickListner();
        View view = View.inflate(this, R.layout.popwindow_feedback_types, null);
        view.findViewById(R.id.tv_type1).setOnClickListener(typeClickListner);
        view.findViewById(R.id.tv_type2).setOnClickListener(typeClickListner);
        view.findViewById(R.id.tv_type3).setOnClickListener(typeClickListner);
        view.findViewById(R.id.tv_type4).setOnClickListener(typeClickListner);
        view.findViewById(R.id.tv_type5).setOnClickListener(typeClickListner);
        view.findViewById(R.id.tv_type6).setOnClickListener(typeClickListner);
        view.findViewById(R.id.tv_type7).setOnClickListener(typeClickListner);
        view.findViewById(R.id.alphaView).setOnClickListener(typeClickListner);
        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ivArrow.setImageResource(R.drawable.arrow_down);
            }
        });
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        if (Build.VERSION.SDK_INT >= 24) {
            Rect visibleFrame = new Rect();
            llChoiseTypeContainer.getGlobalVisibleRect(visibleFrame);
            int height = llChoiseTypeContainer.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            popupWindow.setHeight(height);
            popupWindow.showAsDropDown(llChoiseTypeContainer, 0, 0);
        } else {
            popupWindow.showAsDropDown(llChoiseTypeContainer, 0, 0);
        }
//        popupWindow.showAtLocation(llChoiseTypeContainer, Gravity.TOP, 0, llChoiseTypeContainer.getMeasuredHeight()+titleBar.getMeasuredHeight());
    }

    private class TypeClickListner implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            tvQuestionDesc.setTextColor(Utils.getColor(R.color.text_333333));
            popupWindow.dismiss();
            switch (view.getId()) {
                case R.id.tv_type1:
                    tvQuestionDesc.setText("出借");
                    break;
                case R.id.tv_type2:
                    tvQuestionDesc.setText("债权转让");
                    break;
                case R.id.tv_type3:
                    tvQuestionDesc.setText("优惠券及体验金");
                    break;
                case R.id.tv_type4:
                    tvQuestionDesc.setText("借还款");
                    break;
                case R.id.tv_type5:
                    tvQuestionDesc.setText("自动投标");
                    break;
                case R.id.tv_type6:
                    tvQuestionDesc.setText("银行存管");
                    break;
                case R.id.tv_type7:
                    tvQuestionDesc.setText("其他");
                    break;
                case R.id.alphaView:
                    popupWindow.dismiss();
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_REQUEST_CODE://这里的requestCode是我自己设置的，就是确定返回到那个Activity的标志
                if (resultCode == RESULT_OK) {//resultcode是setResult里面设置的code值
                    try {
                        Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        path = cursor.getString(columnIndex);  //获取照片路径
                        cursor.close();
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        ivPic.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        // TODO Auto-generatedcatch block
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
