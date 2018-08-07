package cn.vpfinance.vpjr.module.user.asset;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.ReturnEventBean;
import cn.vpfinance.vpjr.gson.ReturnMonthBean;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.view.RobotoCalendarView;

/**
 * Created by Administrator on 2016/11/28.
 */
public class ReturnMoneyCalendarActivity extends BaseActivity implements RobotoCalendarView.RobotoCalendarListener {

    /*RobotoCalendarView mCanlendar;
    TextView           mReturnNum;
    TextView           mReturnMoney;*/
    @Bind(R.id.listview)
    ListView           mListview;
    @Bind(R.id.titleBar)
    ActionBarLayout    mTitleBar;
    @Bind(R.id.canlendar)
    RobotoCalendarView mCanlendar;
    @Bind(R.id.returnNum)
    TextView           mReturnNum;
    @Bind(R.id.returnMoney)
    TextView           mReturnMoney;
    @Bind(R.id.ll_no_data)
    LinearLayout       mLlNoData;
    private HttpService mHttpService;
    private MyAdapter   mMyAdapter;
    private int         mYear;
    private int         mMonth;
    private int         mDay;
    private int accountType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.return_money_calendar_activity);
        ButterKnife.bind(this);
        //        View headerView = View.inflate(this, R.layout.calendar_listview_header, null);
        //        mCanlendar = (RobotoCalendarView) headerView.findViewById(R.id.canlendar);
        //        mReturnNum = (TextView) headerView.findViewById(R.id.returnNum);
        //        mReturnMoney = (TextView) headerView.findViewById(R.id.returnMoney);
        Intent intent = getIntent();
        if (intent != null){
            accountType = intent.getIntExtra(Constant.AccountType,0);
        }

        mHttpService = new HttpService(this, this);
        mHttpService.getReturnCalendarTime("", "",accountType);

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        getSelectDay(calendar);
        mTitleBar.reset().setTitle("回款日历").setHeadBackVisible(View.VISIBLE)
        .setImageButtonRight(R.drawable.img_return_list_switcher, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(FinanceApplication.getAppContext());
                sp.putStringValue(SharedPreferencesHelper.STATE_RETURN_CALENDER_OR_LIST,"2");
                Intent intent = new Intent(ReturnMoneyCalendarActivity.this,QueryReturnMoneyListActivity.class);
                intent.putExtra(Constant.AccountType,accountType);
                gotoActivity(intent);
                Utils.Toast(FinanceApplication.getAppContext(), "回款查询已切换至列表视图");
                finish();
            }
        });
        
        //        mListview.addHeaderView(headerView);
        mMyAdapter = new MyAdapter(this);
        mListview.setAdapter(mMyAdapter);


        mCanlendar.setRobotoCalendarListener(this);

        mCanlendar.setShortWeekDays(false);

        mCanlendar.showDateTitle(true);

        mCanlendar.updateView();
    }


    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_Return_Calendar_Time.ordinal()) {
            ReturnMonthBean returnMonthBean = mHttpService.onGetReturnCalendarTime(json);
            if (returnMonthBean != null) {
                String str = "本月预计有" + returnMonthBean.getReturnCount() + "笔回款";
                Utils.setTwoTextColor(str, returnMonthBean.getReturnCount(), getResources().getColor(R.color.text_ff5757), mReturnNum);

                String str2 = "共" + returnMonthBean.getReturnMoney() + "元";
                Utils.setTwoTextColor(str2, returnMonthBean.getReturnMoney(), getResources().getColor(R.color.text_ff5757), mReturnMoney);

                Calendar calendar = Calendar.getInstance();

                List<String> eventDays = returnMonthBean.getEventDays();
                if (eventDays != null && eventDays.size() != 0) {
                    for (String eventDay : eventDays) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                            Date date = sdf.parse(eventDay);
                            calendar.setTime(date);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mCanlendar.markCircleImage1(calendar);
                    }
                }

            }
        }

        if (reqId == ServiceCmd.CmdId.CMD_Get_Event_ByDay.ordinal()) {
            ReturnEventBean returnEventBean = mHttpService.onGetEventByDay(json);
            if (returnEventBean != null) {
                List<ReturnEventBean.AlreadyReturnRecordEntity> alreadyReturnRecord = returnEventBean.getAlreadyReturnRecord();
                List<ReturnEventBean.AlreadyReturnRecordEntity> stayReturnRecord = returnEventBean.getStayReturnRecord();

                if ((alreadyReturnRecord == null || alreadyReturnRecord.size() == 0) && (stayReturnRecord == null || stayReturnRecord.size() == 0)) {
                    mLlNoData.setVisibility(View.VISIBLE);
                    mListview.setVisibility(View.GONE);
                } else {
                    mListview.setVisibility(View.VISIBLE);
                    mLlNoData.setVisibility(View.GONE);
                    mMyAdapter.setData(returnEventBean);
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    public void getSelectDay(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(calendar.getTime());
        mHttpService.getEventByDay(dateStr,accountType);
    }

    @Override
    public void onDayClick(Calendar daySelectedCalendar) {
        mYear = daySelectedCalendar.get(Calendar.YEAR);
        mMonth = daySelectedCalendar.get(Calendar.MONTH) + 1;
        mDay = daySelectedCalendar.get(Calendar.DAY_OF_MONTH);
        getSelectDay(daySelectedCalendar);
    }

    @Override
    public void onDayLongClick(Calendar daySelectedCalendar) {
        //        Toast.makeText(this, "onDayLongClick: " + daySelectedCalendar.getTime(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRightButtonClick(Calendar currentCalendar) {
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        mHttpService.getReturnCalendarTime(year + "", (month + 1) + "",accountType);
    }

    @Override
    public void onLeftButtonClick(Calendar currentCalendar) {
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        mHttpService.getReturnCalendarTime(year + "", (month + 1) + "",accountType);
    }

    @Override
    public void onTvTodayClick(Calendar currentCalendar) {
        getSelectDay(currentCalendar);
        mHttpService.getReturnCalendarTime("", "",accountType);
    }

    @Override
    public void onCurrentMonthClick(Calendar currentCalendar) {
        getSelectDay(currentCalendar);
        mHttpService.getReturnCalendarTime("", "",accountType);
    }

    class MyAdapter extends BaseAdapter {

        public static final int ONE_TYPE = 0;
        public static final int TWO_TYPE = 1;
        private List<ReturnEventBean.AlreadyReturnRecordEntity> mAllData;
        private Context                                         mContext;
        private List<ReturnEventBean.AlreadyReturnRecordEntity> mAlreadyReturnRecord;
        private List<ReturnEventBean.AlreadyReturnRecordEntity> mStayReturnRecord;
        private ReturnEventBean.AlreadyReturnRecordEntity       mOne;
        private ReturnEventBean.AlreadyReturnRecordEntity mTwo;

        public MyAdapter(Context context) {
            mContext = context;
            mAllData = new ArrayList<>();
        }


        public void setData(ReturnEventBean returnEventBean) {
            if (returnEventBean == null)
                return;
            mAllData.clear();
            mAlreadyReturnRecord = returnEventBean.getAlreadyReturnRecord();//今日已回款
            mStayReturnRecord = returnEventBean.getStayReturnRecord();//今日待回款
            if (mOne == null) {
                mOne = new ReturnEventBean.AlreadyReturnRecordEntity();
            }
            mOne.setLoanTitle("今日待回款项");
            mAllData.add(mOne);
            mAllData.addAll(mStayReturnRecord);
            if (mTwo == null) {
                mTwo = new ReturnEventBean.AlreadyReturnRecordEntity();
            }
            mTwo.setLoanTitle("今日已回款项");
            mAllData.add(mTwo);
            mAllData.addAll(mAlreadyReturnRecord);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mAllData == null ? 0 : mAllData.size();
        }

        @Override
        public Object getItem(int position) {
            return mAllData == null ? null : mAllData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == ONE_TYPE) {
                OneViewHolder oneViewHolder = null;
                if (convertView == null) {
                    oneViewHolder = new OneViewHolder();
                    convertView = View.inflate(mContext, R.layout.item_calendar_return_money_type, null);
                    oneViewHolder.tvReturnDes = (TextView) convertView.findViewById(R.id.tvReturnDes);
                    oneViewHolder.tvVisible = (TextView) convertView.findViewById(R.id.tvVisible);
                    convertView.setTag(oneViewHolder);
                } else {
                    oneViewHolder = (OneViewHolder) convertView.getTag();
                }
                ReturnEventBean.AlreadyReturnRecordEntity bean = mAllData.get(position);

                if (bean != null) {
                    String loanTitle = bean.getLoanTitle();
                    oneViewHolder.tvReturnDes.setText(loanTitle);

                    if ("今日待回款项".equals(loanTitle)) {
                        if (mStayReturnRecord == null || mStayReturnRecord.size() == 0) {
                            oneViewHolder.tvVisible.setVisibility(View.VISIBLE);
                            oneViewHolder.tvVisible.setText("暂无回款记录");
                        } else {
                            oneViewHolder.tvVisible.setVisibility(View.GONE);
                        }
                    } else {
                        if (mAlreadyReturnRecord == null || mAlreadyReturnRecord.size() == 0) {
                            oneViewHolder.tvVisible.setVisibility(View.VISIBLE);
                            oneViewHolder.tvVisible.setText("暂无回款记录");
                        } else {
                            oneViewHolder.tvVisible.setVisibility(View.GONE);
                        }
                    }

                }
            } else if (getItemViewType(position) == TWO_TYPE) {

                TwoViewHolder twoViewHolder = null;
                if (convertView == null) {
                    twoViewHolder = new TwoViewHolder();
                    convertView = View.inflate(mContext, R.layout.item_calendar_return_money, null);
                    twoViewHolder.tvMonthDay = (TextView) convertView.findViewById(R.id.tvMonthDay);
                    twoViewHolder.tvYear = (TextView) convertView.findViewById(R.id.tvYear);
                    twoViewHolder.tvProduct = (TextView) convertView.findViewById(R.id.tvProduct);
                    twoViewHolder.tvMoney = (TextView) convertView.findViewById(R.id.tvMoney);
                    twoViewHolder.tvAheadReturnInfo = (TextView) convertView.findViewById(R.id.tvAheadReturnInfo);

                    convertView.setTag(twoViewHolder);
                } else {
                    twoViewHolder = (TwoViewHolder) convertView.getTag();
                }
                ReturnEventBean.AlreadyReturnRecordEntity bean = mAllData.get(position);

                if (mStayReturnRecord != null && position <= mStayReturnRecord.size() + 1) {//待回款的数据

                    if (bean != null) {

                        twoViewHolder.tvMoney.setTextColor(getResources().getColor(R.color.text_999999));
                        twoViewHolder.tvMonthDay.setTextColor(getResources().getColor(R.color.text_333333));
                        twoViewHolder.tvProduct.setTextColor(getResources().getColor(R.color.text_333333));
                        twoViewHolder.tvYear.setTextColor(getResources().getColor(R.color.text_cccccc));

                        twoViewHolder.tvMonthDay.setText(mMonth + "/" + mDay);
                        twoViewHolder.tvYear.setText(mYear + "年");
                        twoViewHolder.tvProduct.setText(bean.getLoanTitle() + "    " + bean.getPeriods());

                        String str = "本金" + bean.getCapitalAmount() + "元+收益" + bean.getProfitAmount() + "元";

                        int fstart = str.indexOf(bean.getCapitalAmount());
                        int fend = fstart + bean.getCapitalAmount().length();

                        int fstart2 = str.indexOf(bean.getProfitAmount());
                        int fend2 = fstart2 + bean.getProfitAmount().length();

                        SpannableStringBuilder style = new SpannableStringBuilder(str);
                        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_ff5757)), fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_ff5757)), fstart2, fend2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                        twoViewHolder.tvMoney.setText(style);
                        //Utils.setTwoTextColor(str2, bean.getCapitalAmount(), getResources().getColor(R.color.text_ff5757), twoViewHolder.tvMoney);
                        if (!TextUtils.isEmpty(bean.getAttribute1())){
                            twoViewHolder.tvAheadReturnInfo.setVisibility(View.VISIBLE);
                            twoViewHolder.tvAheadReturnInfo.setText(bean.getAttribute1()+bean.getAttribute2());
                        }else{
                            twoViewHolder.tvAheadReturnInfo.setVisibility(View.GONE);
                        }
                    }
                } else {//已回款的数据
                    if (bean != null) {
                        twoViewHolder.tvMoney.setTextColor(getResources().getColor(R.color.text_999999));
                        twoViewHolder.tvMonthDay.setTextColor(getResources().getColor(R.color.text_999999));
                        twoViewHolder.tvProduct.setTextColor(getResources().getColor(R.color.text_999999));
                        twoViewHolder.tvYear.setTextColor(getResources().getColor(R.color.text_999999));

                        twoViewHolder.tvMonthDay.setText(mMonth + "/" + mDay);
                        twoViewHolder.tvYear.setText(mYear + "年");
                        twoViewHolder.tvProduct.setText(bean.getLoanTitle() + "    " + bean.getPeriods());

                        String str2 = "本金" + bean.getCapitalAmount() + "元+收益" + bean.getProfitAmount() + "元";
                        twoViewHolder.tvMoney.setText(str2);
                        if (!TextUtils.isEmpty(bean.getAttribute1())){
                            twoViewHolder.tvAheadReturnInfo.setVisibility(View.VISIBLE);
                            twoViewHolder.tvAheadReturnInfo.setText(bean.getAttribute1()+bean.getAttribute2());
                        }else{
                            twoViewHolder.tvAheadReturnInfo.setVisibility(View.GONE);
                        }
                    }
                }
            }

            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {

            if (position == 0 || (mStayReturnRecord == null ? position == 1 : position == mStayReturnRecord.size() + 1)) {
                return ONE_TYPE;
            } else {
                return TWO_TYPE;
            }

        }
    }

    class TwoViewHolder {
        TextView tvMonthDay;
        TextView tvYear;
        TextView tvProduct;
        TextView tvMoney;
        TextView tvAheadReturnInfo;
    }

    class OneViewHolder {
        TextView tvReturnDes;
        TextView tvVisible;
    }
}
