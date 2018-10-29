package cn.vpfinance.vpjr.module.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;

/**
 * 常用提示dialog
 */
public class CommonTipsDialogFragment extends DialogFragment {
    public OnLeftClickListner onLeftClickListner;
    public OnRightClickListner onRightClickListner;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.tv_btnLeft)
    TextView tvBtnLeft;
    @Bind(R.id.view_line)
    View viewLine;
    @Bind(R.id.tv_btnRight)
    TextView tvBtnRight;

    public void setOnLeftClickListner(OnLeftClickListner onLeftClickListner) {
        this.onLeftClickListner = onLeftClickListner;
    }

    public void setOnRightClickListner(OnRightClickListner onRightClickListner) {
        this.onRightClickListner = onRightClickListner;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_common_tips_dialog, null);
        ButterKnife.bind(this, view);
        setData();
        return view;
    }

    public static CommonTipsDialogFragment newInstance(String title, String content, String btnLeft, String btnRight, boolean isCancel, float width, float height, int gravity) {
        CommonTipsDialogFragment commonTipsDialogFragment = new CommonTipsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putString("btnLeft", btnLeft);
        bundle.putString("btnRight", btnRight);
        bundle.putBoolean("isCancel", isCancel);
        bundle.putFloat("width", width);
        bundle.putFloat("height", height);
        bundle.putInt("gravity", gravity);
        commonTipsDialogFragment.setArguments(bundle);
        return commonTipsDialogFragment;
    }

    private void setData() {
        String title = getArguments().getString("title");
        String content = getArguments().getString("content");
        String btnLeft = getArguments().getString("btnLeft");
        String btnRight = getArguments().getString("btnRight");
        tvTitle.setText(title == null ? tvTitle.getText().toString() : title);
        tvContent.setText(content == null ? tvContent.getText().toString() : content);
        if (btnLeft == null) {//传空为只有一个按钮
            tvBtnLeft.setVisibility(View.GONE);
            viewLine.setVisibility(View.GONE);
        } else {
            tvBtnLeft.setText(btnLeft);
        }
        if (btnRight == null) {
            tvBtnRight.setVisibility(View.GONE);
            viewLine.setVisibility(View.GONE);
        } else {
            tvBtnRight.setText(btnRight);
        }
        initDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean isCancel = getArguments().getBoolean("isCancel");
        getDialog().setCancelable(isCancel);//必须在这调用才生效
    }

    private void initDialog() {
        float width = getArguments().getFloat("width");
        float height = getArguments().getFloat("height");
        int gravity = getArguments().getInt("gravity");

        getDialog().getWindow().setGravity(gravity);
//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        int lastWidth;
        if (width > 0 && width < 1) {
            lastWidth = (int) (dm.widthPixels * width);
        } else {
            lastWidth = (int) width;
        }
        int lastHeight;
        if (height > 0 && height < 1) {
            lastHeight = (int) (dm.widthPixels * height);
        } else {
            lastHeight = (int) width;
        }
        getDialog().getWindow().setLayout(lastWidth, lastHeight);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //点击按钮后是否需要关闭dialog控制,默认都关闭
    public static boolean isLeftClickDismiss = true;
    public static boolean isRightClickDiamiss = true;

    @OnClick({R.id.tv_btnLeft, R.id.tv_btnRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_btnLeft:
                if (onLeftClickListner != null) {
                    onLeftClickListner.leftClick();
                }
                if (isLeftClickDismiss) dismiss();
                break;
            case R.id.tv_btnRight:
                if (onRightClickListner != null) {
                    onRightClickListner.rightClick();
                }
                if (isRightClickDiamiss) dismiss();
                break;
        }
    }

    public void show(FragmentActivity activity) {
        show(activity.getSupportFragmentManager(), "base_dialog_tag");
    }

    public static class Buidler {
        public String title;
        public String content;
        public String btnLeft;
        public String btnRight;
        public boolean isCancel = true;//点击外部是否关闭
        public float width = 0.8f;//dialog相对屏幕宽度的百分比,默认为屏幕的百分之80
        public int gravity = Gravity.CENTER; //默认为居中
        public float height = ViewGroup.LayoutParams.WRAP_CONTENT; //默认为包裹内容,可传0~1百分比
        public OnLeftClickListner onLeftClickListenr;
        public OnRightClickListner onRightClickListner;

        public Buidler setWidth(float width) {
            this.width = width;
            return this;
        }

        public Buidler setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Buidler setHeight(float height) {
            this.height = height;
            return this;
        }

        public Buidler setCancelable(boolean cancel) {
            isCancel = cancel;
            return this;
        }

        public Buidler setTitle(String title) {
            this.title = title;
            return this;
        }

        public Buidler setContent(String content) {
            this.content = content;
            return this;
        }

        public Buidler setBtnLeft(String btnLeft) {
            this.btnLeft = btnLeft;
            return this;
        }

        public Buidler setBtnRight(String btnRight) {
            this.btnRight = btnRight;
            return this;
        }

        //点击左按钮是否关闭弹窗
        public Buidler setIsLeftDismiss(boolean isLeftDismiss) {
            isLeftClickDismiss = isLeftDismiss;
            return this;
        }

        public Buidler setIsRightDismiss(boolean isRightDismiss) {
            isRightClickDiamiss = isRightDismiss;
            return this;
        }

        public Buidler setOnLeftClickListenr(OnLeftClickListner leftClickListenr) {
            this.onLeftClickListenr = leftClickListenr;
            return this;
        }

        public Buidler setOnRightClickListener(OnRightClickListner rightClickListener) {
            this.onRightClickListner = rightClickListener;
            return this;
        }

        public CommonTipsDialogFragment create() {
            CommonTipsDialogFragment dialog = newInstance(title, content, btnLeft, btnRight, isCancel, width, height, gravity);
            //将回调传出去给dialog
            dialog.setOnLeftClickListner(onLeftClickListenr);
            dialog.setOnRightClickListner(onRightClickListner);
            return dialog;
        }
    }

    public interface OnLeftClickListner {
        void leftClick();
    }

    public interface OnRightClickListner {
        void rightClick();
    }

//    调用示例
//    CommonTipsDialogFragment.Buidler buidler = new CommonTipsDialogFragment.Buidler();
//                buidler
//                        .setTitle("我是标题")
//                        .setContent("我是内容")
//                        .setBtnLeft("左按键")
//                        .setBtnRight("右按键")
//                        .setOnLeftClickListenr(new CommonTipsDialogFragment.OnLeftClickListner() {
//        @Override
//        public void leftClick() {
//            Utils.Toast("点击了左按键");
//        }
//    })
//            .setOnRightClickListener(new CommonTipsDialogFragment.OnRightClickListner() {
//        @Override
//        public void rightClick() {
//            Utils.Toast("点击了右按键");
//        }
//    })
//            .setIsLeftDismiss(false)
//                        .setWidth(0.5f)
//                        .setGravity(Gravity.BOTTOM)
//                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
//                        .setCancelable(false)
//                        .create()
//                        .show(getActivity());
}
