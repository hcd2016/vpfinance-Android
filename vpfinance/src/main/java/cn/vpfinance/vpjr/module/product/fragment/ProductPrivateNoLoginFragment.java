package cn.vpfinance.vpjr.module.product.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.common.LoginActivity;

/**
 */
public class ProductPrivateNoLoginFragment extends BaseFragment {

    @Bind(R.id.lookOtherProduct)
    TextView lookOtherProduct;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(mContext, R.layout.fragment_product_private_no_login, null);
        ButterKnife.bind(this, view);
        lookOtherProduct.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.lookOtherProduct)
    public void onClick() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
//        getActivity().finish();
    }
}
