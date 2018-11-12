package cn.vpfinance.vpjr.module.product.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import de.greenrobot.event.EventBus;

public class ProductBorrowerFragment extends BaseFragment {

	private long pid;
	private int type;
	
	private static final String ARGS_PRODUCT_TYPE = "type";
	private static final String ARGS_PRODUCT_ID = "id";
	
	public static ProductBorrowerFragment newInstance(long pid, int type) {
		ProductBorrowerFragment frag = new ProductBorrowerFragment();
		Bundle args = new Bundle();
		args.putInt(ARGS_PRODUCT_TYPE, type);
		args.putLong(ARGS_PRODUCT_ID, pid);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);

		Bundle args = getArguments();
		if (args != null) {
			pid = args.getLong(ARGS_PRODUCT_ID);
			type = args.getInt(ARGS_PRODUCT_TYPE);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_invest_record, null);
		
		return view;
	}
	
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	public void onEventMainThread(FinanceProduct event) {
		if (event != null && isAdded()) {
			
		}
	}
}
