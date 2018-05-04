package cn.vpfinance.vpjr.module.voucher.fragment;

import android.os.Bundle;
import android.os.Parcel;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.dialog.CommonDialogFragment;
import cn.vpfinance.vpjr.module.voucher.NewSelectVoucherActivity;
import cn.vpfinance.vpjr.module.product.invest.ProductInvestActivity;
import cn.vpfinance.vpjr.adapter.SingleAdapter;
import cn.vpfinance.vpjr.gson.AddRateBean;
import cn.vpfinance.vpjr.model.AddRateInfo;
import cn.vpfinance.vpjr.model.CalcMoneyBean;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/7/28.
 */
public class NewSelectAddRateFragment extends BaseFragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {


    @Bind(R.id.voucherLV)
    ListView           mVoucherLV;
    @Bind(R.id.textview)
    TextView           mTextview;
    @Bind(R.id.refresh)
    SwipeRefreshLayout mRefresh;

    private String      mPid;
    private HttpService mHttpService;
    private int page = 1;
    private double        money;
    private int           mTotalPage;
    private SingleAdapter mSingleAdapter;
    private ArrayList<AddRateBean.CouponUserRelationsEntity> allList    = new ArrayList();
    private boolean                                          isLastPage = false;
    private int                                              mAddRateId = -1;
    private double mAddRateMoey;
    private int    mAddRatePeriod;//加息天数
    private double mValue;//加息利率
    private int temp = -1;
    private AddRateInfo          mAddRateInfo;
    private CommonDialogFragment mDialogFragment;
    private boolean              isFrist = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mPid = bundle.getString(ProductInvestActivity.PRODUCT_ID);
            money = bundle.getDouble(ProductInvestActivity.NAME_MONEY, 0);
            mAddRateInfo = bundle.getParcelable(ProductInvestActivity.ADDRATEEVENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_add_rate, container, false);
        ButterKnife.bind(this, view);
        mHttpService = new HttpService(getContext(), this);
        if (mPid != null) {
            mHttpService.getAddRateInvest(page + "", mPid, money + "");
        }

        mDialogFragment = CommonDialogFragment.newInstance("温馨提示", "加息券不能和代金券叠加使用", "确定", "不再提醒");

        init();
        return view;
    }

    private void init() {

        mSingleAdapter = new SingleAdapter(getActivity());
        mVoucherLV.setAdapter(mSingleAdapter);
        if (mAddRateInfo != null) {
            temp = mAddRateInfo.getTemp();
            mSingleAdapter.setTemp(temp);
            mAddRateId = mAddRateInfo.getAddRateId();
            mAddRateMoey = mAddRateInfo.getAddRateMoey();
            EventBus.getDefault().post(new CalcMoneyBean("2",0,mAddRateMoey));
        }

        mRefresh.setColorSchemeResources(R.color.main_color);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                allList.clear();
//                mSingleAdapter.setTemp(-1);
//                mAddRateId = -1;
                mHttpService.getAddRateInvest(page + "", mPid, money + "");
            }
        });


        mVoucherLV.setOnScrollListener(this);
        mVoucherLV.setOnItemClickListener(this);
    }

    public static NewSelectAddRateFragment newInstance(String pid, Double money, AddRateInfo addRateInfo) {
        NewSelectAddRateFragment fragment = new NewSelectAddRateFragment();
        Bundle args = new Bundle();
        args.putString(ProductInvestActivity.PRODUCT_ID, pid);
        args.putDouble(ProductInvestActivity.NAME_MONEY, money);
        args.putParcelable(ProductInvestActivity.ADDRATEEVENT, addRateInfo);
        fragment.setArguments(args);
        return fragment;
    }

    public void postEvent() {

        AddRateInfo addRateInfo = AddRateInfo.CREATOR.createFromParcel(Parcel.obtain());
        addRateInfo.setType("2");
        addRateInfo.setTemp(temp);
        addRateInfo.setAddRateId(mAddRateId);
        addRateInfo.setAddRateMoey(mAddRateMoey);
        addRateInfo.setValue(mValue);
        addRateInfo.setAddRatePeriod(mAddRatePeriod);
        EventBus.getDefault().post(addRateInfo);
        getActivity().finish();
    }


    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (mRefresh != null){
            mRefresh.setRefreshing(false);
        }
        if (reqId == ServiceCmd.CmdId.CMD_Addrate_Ticket_invest.ordinal()) {
            AddRateBean addRateBean = mHttpService.onGetAddRateInvest(json);
            if (addRateBean != null) {
                String totalPage = addRateBean.getTotalPage();
                try {
                    mTotalPage = Integer.parseInt(totalPage);

                    List<AddRateBean.CouponUserRelationsEntity> data = addRateBean.getCouponUserRelations();

                    if (data == null || data.size() == 0) {
                        mTextview.setVisibility(View.VISIBLE);
                        mTextview.setText("您没有加息券！");
                    } else {
                        mTextview.setVisibility(View.GONE);
                        allList.addAll(data);
                        mSingleAdapter.setData(allList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        super.onHttpError(reqId, errmsg);
        mRefresh.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

        SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(FinanceApplication.getAppContext());
        if (isFrist) {

            if (((NewSelectVoucherActivity) getActivity()).alertDialog("1") && !"1".equals(sp.getStringValue(SharedPreferencesHelper.VOUCHER_ISSHOW_DIALOG))) {

                mDialogFragment.show(getActivity().getFragmentManager(), "CommonDialogFragment");
                mDialogFragment.setOnAllLinstener(new CommonDialogFragment.onAllListener() {
                    @Override
                    public void clickOk() {

                        isFrist = false;
                    }

                    @Override
                    public void clickCancel() {
                        isFrist = false;
                        SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(FinanceApplication.getAppContext());
                        sp.putStringValue(SharedPreferencesHelper.VOUCHER_ISSHOW_DIALOG, "1");
                    }
                });

                return;

            }
        }

            ((NewSelectVoucherActivity)getActivity()).cleanData("1");
            SingleAdapter.ViewHolder viewHolder = (SingleAdapter.ViewHolder) view.getTag();
            if (viewHolder.status == 2) return;
            viewHolder.checkBox.toggle();
            if (viewHolder.checkBox.isChecked()) {
                int index = viewHolder.checkBox.getId();
                temp = index;
                AddRateBean.CouponUserRelationsEntity bean = allList.get(index);
                if (bean != null) {
                    try {
                        mAddRateId = bean.getId();
                        mValue = Double.parseDouble(bean.getValue());
                        mAddRatePeriod = bean.getAddRatePeriod();

                        mAddRateMoey = money * mValue / 360 * mAddRatePeriod;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                temp = -1;
                mAddRateId = 0;
                mAddRateMoey = 0.00;
            }

            CalcMoneyBean calcMoneyBean = null;
            if (calcMoneyBean == null) {
                calcMoneyBean = new CalcMoneyBean();
            }
            calcMoneyBean.setType("2");
            calcMoneyBean.setAddRateMoey(mAddRateMoey);
            EventBus.getDefault().post(calcMoneyBean);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (view.getLastVisiblePosition() == view.getCount() - 1) {
            page++;

            isLastPage = page > mTotalPage ? true : false;
            if (isLastPage)
                return;
            mHttpService.getAddRateInvest(page + "", mPid, money + "");
            mRefresh.setRefreshing(true);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public void cleanData() {
        mSingleAdapter.setTemp(-1);
        mAddRateId = -1;
        temp = -1;

        mSingleAdapter.notifyDataSetChanged();
    }

    public boolean isPost() {
        return mAddRateId >= 0 && temp >= 0;
    }

}
