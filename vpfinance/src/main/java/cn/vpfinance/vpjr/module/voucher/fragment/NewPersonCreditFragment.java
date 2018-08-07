package cn.vpfinance.vpjr.module.voucher.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;

/**
 * Created by Administrator on 2016/10/24.
 * datatype为6的数据格式（个人标） 保持原有信用评级的数据格式不变
 */
public class NewPersonCreditFragment extends BaseFragment {

    private static final String PRODUCT_ID = "loanId";
    private static final String SHOW_TYPE  = "showType";
    private static final String NET_URL    = "netUrl";
    private long mLoanId;
    private String mShowType;
    private String mNetUrl;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mShowType = args.getString(SHOW_TYPE);
            mNetUrl = args.getString(NET_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_personcredit_fragment, container, false);
        return view;
    }

    public static NewPersonCreditFragment newInstance(String showType,String url){
        NewPersonCreditFragment fragment = new NewPersonCreditFragment();
        Bundle args = new Bundle();
        args.putString(SHOW_TYPE, showType);
        args.putString(NET_URL, url);
        fragment.setArguments(args);
        return fragment;
    }
}
