package cn.vpfinance.vpjr.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jewelcredit.util.Utils;
import com.tdk.utils.HttpDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.util.Common;

public class BaseFragment extends Fragment implements HttpDownloader.HttpDownloaderListener{
	
	protected Context mContext;
	private boolean isVisibleToUser;
	private boolean isHidden = true;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//		mTitleBar = new TitleBar(null);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		this.isVisibleToUser = isVisibleToUser;
		if (isVisibleToUser) {
			onUserVisible();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (getView() != null) {
			getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
		}
	}

	protected void onUserVisible() {

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		isHidden = hidden;
	}

	@Override
	public void onHttpSuccess(int reqId, JSONObject json) {
		if (isVisibleToUser == true){
//			Common.isForceLogout(getContext(),json);
		}
    }

	protected boolean isHttpHandle(JSONObject json){
		if (json == null || (!isAdded()))	return false;
//		if (!isHidden){
//			if (Common.isForceLogout(getContext(),json)){
//				return false;
//			}
//		}
		return true;
	}
    
    @Override
	public void onHttpSuccess(int reqId, JSONArray json) {
	}

	protected void loadDate(){}
    
    
    public void onHttpCache(int reqId) {
    	
    }

	public void onHttpError(int reqId, String errmsg) {
    }
    
	public void gotoActivity(Class<? extends Activity> activityClass) {
		if (isAdded()) {
			startActivity(new Intent(this.getActivity(), activityClass));
			getActivity().overridePendingTransition(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left);
		}
	}

	public void gotoActivity(Intent intent) {
		if (isAdded()) {
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left);
		}
	}
    
    
    public void gotoWeb(String url, String title)
    {
    	if (isAdded()) {
    		Utils.goToWeb(getActivity(), url, title);
    	}
    }

}
