package cn.vpfinance.vpjr.module.product.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.model.ProductCarDescriptionInfo;
import de.greenrobot.event.EventBus;

/**
 */
public class RegularProductCarDescriptionFragment extends BaseFragment {

    @Bind(R.id.deal_des)
    TextView mDealDes;

    public static RegularProductCarDescriptionFragment newInstance() {
        RegularProductCarDescriptionFragment frag = new RegularProductCarDescriptionFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle args = getArguments();
        if (args != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(getActivity(), R.layout.fragment_car_description, null);
        ButterKnife.bind(this, view);
        return view;
    }
    public void onEventMainThread(ProductCarDescriptionInfo event) {
        if (event != null && isAdded()){
            mDealDes.setText("\u3000\u3000"+event.description);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}
