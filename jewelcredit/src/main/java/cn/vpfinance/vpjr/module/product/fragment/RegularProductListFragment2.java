package cn.vpfinance.vpjr.module.product.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.product.shenyang.PresellProductActivity;
import cn.vpfinance.vpjr.module.product.invest.RegularProductActivity;
import cn.vpfinance.vpjr.module.product.transfer.TransferProductActivity;
import cn.vpfinance.vpjr.adapter.RegularAdapter;
import cn.vpfinance.vpjr.adapter.RegularAdapter2;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import cn.vpfinance.vpjr.model.RefreshTab;
import cn.vpfinance.vpjr.model.RegularProductList;
import cn.vpfinance.vpjr.network.OkHttpUtil;
import de.greenrobot.event.EventBus;

/**
 * 定期理财列表
 * @author cheungquentin
 *
 */
public class RegularProductListFragment2 extends BaseFragment {

	private ListView mListView;
	private TextView mTextView;
	public static final int REGULAR_PRODUCT_LIST  = 1; //定期理财
	public static final int TRANSFER_PRODUCT_LIST = 2; //债权转让
	public static       int typeList              = REGULAR_PRODUCT_LIST;

	private RegularAdapter2           mListAdapter = null;
	private HttpService               mHttpService = null;
	private ArrayList<FinanceProduct> productList  = new ArrayList<FinanceProduct>();

	private int page;
	private              boolean isRefreshing = false;
	private              boolean isLastPage   = false;
	private final static int     PAGE_SIZE    = 10;

	private SwipeRefreshLayout mRefresh  = null;
	private String             queryText = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_regular_list2, null);
		mListView = (ListView) view.findViewById(R.id.listView);
		mTextView = (TextView) view.findViewById(R.id.textview);
		mTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//mHttpService.getRegularProductList(page, PAGE_SIZE);
				getRegularProductList(page, PAGE_SIZE);
			}
		});

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object obj = mListAdapter.getItem(position);
				if (obj instanceof FinanceProduct) {
					FinanceProduct product = (FinanceProduct) obj;
					if (product != null) {
						if (isAdded()) {
							if (typeList == REGULAR_PRODUCT_LIST) {
								String productType = product.getProductType();
								if ("3".equals(productType)) {
									PresellProductActivity.goPresellProductActivity(
											getActivity(), "" + product.getPid());
								} else {
									RegularProductActivity.goRegularProductActivity(
											getActivity(), "" + product.getPid(),
											product.getType(), product.getProduct(), -1, "" + 0);
								}
							} else if (typeList == TRANSFER_PRODUCT_LIST) {
								TransferProductActivity.goTransferProductActivity(getContext(), "" + product.getPid());
							}
						}
					}
				} else if (obj instanceof RegularAdapter.TopProductData) {
					RegularAdapter.TopProductData topProductData = (RegularAdapter.TopProductData) obj;
					gotoWeb(topProductData.topProductUrl, "");
				}
			}
		});


		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (view.getLastVisiblePosition() == view.getCount() - 1 && !isRefreshing && !isLastPage) {
					isRefreshing = true;
					page++;
					mRefresh.setRefreshing(true);
					getRegularProductList(page, PAGE_SIZE);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});

		mRefresh = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
		mRefresh.setColorSchemeResources(R.color.main_color);
		mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				isRefreshing = true;
				page = 0;
				getRegularProductList(page, PAGE_SIZE);
				//mHttpService.getRegularProductList(page, PAGE_SIZE);
			}
		});
		mListAdapter = new RegularAdapter2(getActivity());
		mListView.setAdapter(mListAdapter);

		getRegularProductList(page, PAGE_SIZE);
		//mHttpService.getRegularProductList(page, PAGE_SIZE);

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Activity activity = getActivity();
		EventBus.getDefault().register(this);
		mHttpService = new HttpService(activity, this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	public void onEventMainThread(RefreshTab event) {
		if (event != null && isAdded()){
			if (event.tabType == RefreshTab.TAB_LIST){
				mRefresh.setRefreshing(true);
				page = 0 ;
				productList.clear();
				getRegularProductList(page, PAGE_SIZE);
				mListAdapter.notifyDataSetChanged();
			}
		}
	}

//	public void onEventMainThread(RefreshListInfo event) {
//		if (event != null && event.isNeedRefresh == true && isAdded()){
//			new Handler().postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					mRefresh.setRefreshing(true);
//					page = 0 ;
//					productList.clear();
//					getRegularProductList(page, PAGE_SIZE);
//					mListAdapter.notifyDataSetChanged();
//					mRefresh.setRefreshing(false);
//				}
//			},INTERVAL_REFRESH_TIME);
//
//		}
//	}

	public void onEventMainThread(final RegularProductList event) {
		if(event.success && isAdded())
		{
			int p = event.page;
			ArrayList<FinanceProduct> pList = event.list;
			if(!event.hasTopProduct && (null == pList || pList.isEmpty())){
				mTextView.setText("暂无数据");
				mTextView.setVisibility(View.VISIBLE);
			}else{
				mTextView.setVisibility(View.GONE);
			}

			if(p == 0)
			{
				productList.clear();
			}

			if(pList!=null)
			{
				productList.addAll(pList);
			}
			mListAdapter.setList(productList,typeList);

			if(event.hasTopProduct || !productList.isEmpty())
			{
				mTextView.setVisibility(View.GONE);
			}
		}


		if(mRefresh.isRefreshing()) {
			mRefresh.setRefreshing(false);
		}
		isRefreshing = false;
	}

	@Override
	public void onHttpSuccess(int reqId, JSONObject json) {
		super.onHttpSuccess(reqId,json);
	}

	@Override
	public void onHttpError(int reqId, String errmsg) {

	}

	public void getRegularProductList(final int page,final int pageSize)
	{
		ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_getBidList;
		String method = ServiceCmd.getMethodName(cmdId);
		String url = HttpService.getServiceUrl(method);

		ArrayMap<String,String> param = new ArrayMap<String,String>();
		param.put("type", ""+typeList);// 1.定期理财  2.债权转让  默认为1
		param.put("start", "" + (page * pageSize));//从start条记录开始（默认0）
		param.put("limit", "" + pageSize);//取limit条记录（默认5）
		param.put("title", queryText);//模糊查询
		Request req = OkHttpUtil.newPostRequest(url, param);

		OkHttpUtil.enqueue(req,new Callback() {

			@Override
			public void onFailure(Request request, IOException e) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mRefresh.setRefreshing(false);
					}
				});
				RegularProductList product = new RegularProductList();
				product.success = false;
				EventBus.getDefault().post(product);
			}

			@Override
			public void onResponse(Response response) throws IOException {
				if (response.isSuccessful()) {

					JSONObject json = null;
					try {
						json = new JSONObject(response.body().string());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if(json!=null)
					{
						ArrayList<FinanceProduct> pList = mHttpService.onGetRegularProductList(getActivity(),json);
						RegularProductList product = new RegularProductList();
						product.success = true;
						product.page = page;
						product.isLastPage = pList==null || pList.isEmpty() || pList.size()<pageSize;
						if(pList!=null && pList.size()>0)
						{
							FinanceProduct prod = pList.get(0);
							product.isLastPage = prod.getTotalPage()<= (page+1) * pageSize;
						}
						product.list = pList;
						EventBus.getDefault().post(product);
					}
				}
			}
		});

		getMofang_remoteCtrl();
	}

	private void getMofang_remoteCtrl()
	{
		ServiceCmd.CmdId cmdId = ServiceCmd.CmdId.CMD_Mofang_remoteCtrl;
		String method = ServiceCmd.getMethodName(cmdId);
		String url = HttpService.getServiceUrl(method);

		ArrayMap<String, String> params = new ArrayMap<String, String>();

		Request req = OkHttpUtil.newPostRequest(url, params);

		OkHttpUtil.enqueue(req, new Callback() {

			@Override
			public void onFailure(Request request, IOException e) {

			}

			@Override
			public void onResponse(Response response) throws IOException {
				if (response.isSuccessful()) {
					JSONObject json = null;
					try {
						json = new JSONObject(response.body().string());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (json != null) {
						RegularAdapter.TopProductData top = mHttpService.onMofang_remoteCtrl(json);
						if (top != null) {
							EventBus.getDefault().post(top);
						}
					}
				}
			}
		});
	}
}
