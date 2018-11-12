package cn.vpfinance.vpjr.module.voucher.fragment;

import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.App;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.dialog.CommonDialogFragment;
import cn.vpfinance.vpjr.module.voucher.NewSelectVoucherActivity;
import cn.vpfinance.vpjr.module.product.invest.ProductInvestActivity;
import cn.vpfinance.vpjr.adapter.GroupAdapter;
import cn.vpfinance.vpjr.gson.SelectVoucherBean;
import cn.vpfinance.vpjr.model.CalcMoneyBean;
import cn.vpfinance.vpjr.model.VoucherEvent;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/7/28.
 */
public class NewSelectVoucherFragment extends BaseFragment implements AdapterView.OnItemClickListener {


    @Bind(R.id.voucherLV)
    ListView           mVoucherLV;
    @Bind(R.id.refresh)
    SwipeRefreshLayout mRefresh;
    @Bind(R.id.textview)
    TextView           mTextview;

    private String mPid;
    private int page = 1;
    private HttpService                               mHttpService;
    private GroupAdapter                              mGroupAdapter;
    private List<SelectVoucherBean.VoucherlistEntity> mVoucherlist;
    private ArrayList<SelectVoucherBean.VoucherlistEntity> allList     = new ArrayList();
    private SparseArray<Object>                            selectArray = new SparseArray<>();//当前选中代金券ID 和金额

    private boolean isLastPage = false;
    private int                  mTotalPage;
    private double               mVoucherRate;//折扣率
    private Double               money;//项目总额
    private double               selectedVoucher;//已选优惠券总额
    private double               calcedMoney;
    private VoucherEvent         mVoucherEvent;
    private CommonDialogFragment mDialogFragment;
    private boolean              isFrist = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mPid = bundle.getString(ProductInvestActivity.PRODUCT_ID);
            money = bundle.getDouble(ProductInvestActivity.NAME_MONEY);
            mVoucherEvent = bundle.getParcelable(ProductInvestActivity.VOUCHEREVENT);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_select_voucher, container, false);
        ButterKnife.bind(this, view);
        mHttpService = new HttpService(getContext(), this);
        if (!TextUtils.isEmpty(mPid)) {
            mHttpService.getVoucherlist("" + page, mPid);
            mHttpService.getVoucherRate(mPid);
        }

        mDialogFragment = CommonDialogFragment.newInstance("温馨提示", "加息券不能和代金券叠加使用", "确定", "不再提醒");

        init();
        return view;
    }

    private void init() {

        mGroupAdapter = new GroupAdapter(getContext());
        mVoucherLV.setAdapter(mGroupAdapter);

        if (mVoucherEvent != null) {
            HashMap<Integer, Boolean> isSelected = mVoucherEvent.getIsSelected();
            if (isSelected != null) {
                mGroupAdapter.getIsSelected().putAll(isSelected);
            }

            if (mVoucherEvent.getSelectArray() != null) {
                selectArray = mVoucherEvent.getSelectArray();
            }
            calcedMoney = mVoucherEvent.getCalcedMoney();
            mVoucherRate = mVoucherEvent.getVoucherrate();
            selectedVoucher = mVoucherEvent.getVoucherValue();
            calcMoney();
        }

        mRefresh.setColorSchemeResources(R.color.main_color);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                allList.clear();
//                mGroupAdapter.getIsSelected().clear();
//                selectArray.clear();
                mHttpService.getVoucherlist("" + page, mPid);
            }
        });

        mVoucherLV.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    page++;

                    isLastPage = page > mTotalPage ? true : false;
                    if (isLastPage)
                        return;
                    mHttpService.getVoucherlist(page + "", mPid);
                    mRefresh.setRefreshing(true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        mVoucherLV.setOnItemClickListener(this);
    }


    public static NewSelectVoucherFragment newInstance(String pid, Double money, VoucherEvent voucherEvent) {
        NewSelectVoucherFragment fragment = new NewSelectVoucherFragment();
        Bundle args = new Bundle();
        args.putString(ProductInvestActivity.PRODUCT_ID, pid);
        args.putDouble(ProductInvestActivity.NAME_MONEY, money);
        args.putParcelable(ProductInvestActivity.VOUCHEREVENT, voucherEvent);
        fragment.setArguments(args);
        return fragment;
    }

    public void postEvent() {
        int size = selectArray.size();
        int[] retArray = new int[size];
        for (int i = 0; i < size; i++) {
            int key = selectArray.keyAt(i);
            retArray[i] = key;
        }
        VoucherEvent voucherEvent = VoucherEvent.CREATOR.createFromParcel(Parcel.obtain());
        voucherEvent.setType("1");
        voucherEvent.setSelectArray(selectArray);
        voucherEvent.setIsSelected(mGroupAdapter.getIsSelected());
        voucherEvent.setCalcedMoney(calcedMoney);
        voucherEvent.setVoucherrate(mVoucherRate);
        voucherEvent.setVoucherValue(selectedVoucher);
        voucherEvent.setVouchersArray(retArray);
        EventBus.getDefault().post(voucherEvent);
        getActivity().finish();
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_VOUCHERLIST_V2.ordinal()) {//代金券列表
            mRefresh.setRefreshing(false);
            SelectVoucherBean selectVoucherBean = mHttpService.onGetVoucherlist(json + "");
            if (selectVoucherBean != null) {
                String totalPage = selectVoucherBean.getTotalPage();
                try {
                    mTotalPage = Integer.parseInt(totalPage);
                    mVoucherlist = selectVoucherBean.getVoucherlist();

                    if (mVoucherlist == null || mVoucherlist.size() == 0) {
                        mTextview.setVisibility(View.VISIBLE);
                        mTextview.setText("您没有代金券！");
                    }else {
                        mTextview.setVisibility(View.GONE);
                        allList.addAll(mVoucherlist);
                        mGroupAdapter.setData(allList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (reqId == ServiceCmd.CmdId.CMD_voucherRate.ordinal()) {//代金券抵扣比率
            String voucherRate = json.optString("rate");
            try {
                mVoucherRate = Double.parseDouble(voucherRate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        super.onHttpError(reqId, errmsg);
        mRefresh.setRefreshing(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

        SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(App.getAppContext());

        if (isFrist) {

            if (((NewSelectVoucherActivity) getActivity()).alertDialog("2") && !"1".equals(sp.getStringValue(SharedPreferencesHelper.VOUCHER_ISSHOW_DIALOG))) {

                    mDialogFragment.show(getActivity().getFragmentManager(), "CommonDialogFragment");
                    mDialogFragment.setOnAllLinstener(new CommonDialogFragment.onAllListener() {
                        @Override
                        public void clickOk() {
                            isFrist = false;
                        }

                        @Override
                        public void clickCancel() {
                            isFrist = false;
                            SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(App.getAppContext());
                            sp.putStringValue(SharedPreferencesHelper.VOUCHER_ISSHOW_DIALOG, "1");

                        }
                    });

                        return;
            }

            }

            ((NewSelectVoucherActivity) getActivity()).cleanData("2");
            GroupAdapter.ViewHolder viewHolder = (GroupAdapter.ViewHolder) view.getTag();
            if (viewHolder.status == 2) return;
            viewHolder.checkBox.toggle();
            mGroupAdapter.getIsSelected().put(position, viewHolder.checkBox.isChecked());
            SelectVoucherBean.VoucherlistEntity bean = allList.get(position);
            if (bean != null) {
                if (viewHolder.checkBox.isChecked()) {
                    if ("1".equals(bean.getActivity_type())) {//抢红包获得代金券
                        selectArray.put(bean.getId(), bean.getVoucher_money());
                    } else {
                        if (bean.getVoucher() != null) {
                            selectArray.put(bean.getId(), bean.getVoucher().getAmount());
                        }
                    }
                } else {
                    selectArray.remove(bean.getId());
                }
            }

            calcMoney();



    }

    private void calcMoney() {
        selectedVoucher = 0;

        int size = selectArray.size();
        for (int i = 0; i < size; i++) {
            int key = selectArray.keyAt(i);
            selectedVoucher += (Double)selectArray.get(key);
        }

        if (mVoucherRate >= 0) {
            calcedMoney = money * mVoucherRate;
            calcedMoney = Math.min(calcedMoney, selectedVoucher);
        } else {
            calcedMoney = 0.00;
        }

        CalcMoneyBean calcMoneyBean = null;
        if (calcMoneyBean == null) {
            calcMoneyBean = new CalcMoneyBean();
        }
        calcMoneyBean.setType("1");
        calcMoneyBean.setCalcedMoney(calcedMoney);
        EventBus.getDefault().post(calcMoneyBean);
    }

    public void cleanData() {
        mGroupAdapter.getIsSelected().clear();
        selectArray.clear();
        mGroupAdapter.setData(allList);
    }

    public boolean isPost() {
        return selectArray != null && selectArray.size() != 0;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
