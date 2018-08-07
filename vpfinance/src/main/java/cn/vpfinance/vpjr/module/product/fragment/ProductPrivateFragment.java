package cn.vpfinance.vpjr.module.product.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.home.MainActivity;

/**
 */
public class ProductPrivateFragment extends BaseFragment {

    @Bind(R.id.info)
    TextView info;
    private Context mContext;
    public static final String HINT_TEXT = "hint_text";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(mContext, R.layout.fragment_product_private, null);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        if (bundle != null && !TextUtils.isEmpty(bundle.getString(HINT_TEXT))){
            info.setText(bundle.getString(HINT_TEXT));
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.lookOtherProduct)
    public void onClick() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(MainActivity.SWITCH_TAB_NUM, 1);
        startActivity(intent);
    }
}
