package cn.vpfinance.vpjr.module.user.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.InvestSummaryTab3Bean;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.view.MyMarkerView;

/**
 */
public class InvestSummaryType3Fragment extends BaseFragment implements OnChartValueSelectedListener {

    @Bind(R.id.otherIncome)
    TextView mOtherIncome;
    @Bind(R.id.inviteIncome)
    TextView mInviteIncome;
    @Bind(R.id.voucherIncome)
    TextView mVoucherIncome;
    @Bind(R.id.investIncome)
    TextView mInvestIncome;
    @Bind(R.id.totalIncome)
    TextView mTotalIncome;
    @Bind(R.id.startTime)
    TextView mStartTime;
    @Bind(R.id.endTime)
    TextView mEndTime;
    private Context mContext;
//    private ArrayList<Pair<String, Float>> toastValues = new ArrayList<>();

    @Bind(R.id.main_linechart)
    LineChart mLinechart;

    private HttpService mHttpService;
    private InvestSummaryTab3Bean mInfo;
    private ArrayList<String> mXValues;
    private ArrayList<Entry> mYValues;
    private int accountType = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public static InvestSummaryType3Fragment newInstance(int account) {
        InvestSummaryType3Fragment fragment = new InvestSummaryType3Fragment();
        Bundle args = new Bundle();
        args.putInt(Constant.AccountType, account);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(mContext, R.layout.fragment_invest_summary_type3, null);
        ButterKnife.bind(this, view);

        Bundle arguments = getArguments();
        if (arguments != null) {
            accountType = arguments.getInt(Constant.AccountType);
        }

        mHttpService = new HttpService(mContext, this);
        mHttpService.getTenderPromit(accountType);
        initLineChart(mLinechart);
        return view;
    }

    private void initLineChart(LineChart lineChart) {
        lineChart.setOnChartValueSelectedListener(this);
        lineChart.setDrawBorders(false);  //是否在折线图上添加边框
        lineChart.getAxisLeft().setStartAtZero(true);
        // no description text
        lineChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        lineChart.setNoDataTextDescription("数据加载失败");

        // enable / disable grid background
        lineChart.setDrawGridBackground(false); // 是否显示表格颜色
        lineChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度

        // enable touch gestures
        lineChart.setTouchEnabled(true); // 设置是否可以触摸

        // enable scaling and dragging
        lineChart.setDragEnabled(false);// 是否可以拖拽
        lineChart.setScaleEnabled(false);// 是否可以缩放

        //隐藏背后网格
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setEnabled(false);

        //去掉左边Y轴
        YAxis leftYAxis = lineChart.getAxisLeft();
//        leftYAxis.setEnabled(false);
//        leftYAxis.setDrawGridLines(false);
        leftYAxis.setDrawAxisLine(false);
        leftYAxis.setTextColor(getResources().getColor(R.color.text_black_no5_gray));

        ////去掉右边Y轴
        YAxis RightYAxis = lineChart.getAxisRight();
        RightYAxis.setEnabled(false);
        RightYAxis.setDrawAxisLine(false);
        RightYAxis.setDrawGridLines(false);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(false);//

//        lineChart.setBackgroundColor(color);// 设置背景
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_TenderPromit.ordinal()) {
            mInfo = mHttpService.onGetTenderPromit(json);
            mTotalIncome.setText(mInfo.getAllTotalMoney() == 0 ? "0.00" : ("" + mInfo.getAllTotalMoney()));
            mInvestIncome.setText(mInfo.getCapitalMoney() == 0 ? "¥0.00" : ("¥" + mInfo.getCapitalMoney()));
            mVoucherIncome.setText(mInfo.getVoucherSumMoney() == 0 ? "¥0.00" : ("¥" + mInfo.getVoucherSumMoney()));
            mInviteIncome.setText(mInfo.getInviteMoney() == 0 ? "¥0.00" : ("¥" + mInfo.getInviteMoney()));
            mOtherIncome.setText(mInfo.getOtherMoney() == 0 ? "¥0.00" : ("¥" + mInfo.getOtherMoney()));
            setData(mInfo);
        }
    }

    private void setData(InvestSummaryTab3Bean info) {
        // add data
        LineData lineData = getLineData(info);
        if (lineData != null) {
            mLinechart.setData(lineData); // 设置数据
        }
        //去掉每一个点上面显示的数据
        List<ILineDataSet> sets = mLinechart.getData().getDataSets();
        for (ILineDataSet iSet : sets) {
            LineDataSet set = (LineDataSet) iSet;
            set.setDrawValues(!set.isDrawValuesEnabled());
        }

        // get the legend (only possible after setting data)
        Legend mLegend = mLinechart.getLegend(); // 设置比例图标示，就是那个一组y的value的

        mLegend.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
        // modify the legend ...
        mLegend.setForm(Legend.LegendForm.SQUARE);// 样式
        mLegend.setFormSize(4f);// 字体
        mLegend.setTextColor(getResources().getColor(R.color.text_black_no5_gray));// 颜色

        MyMarkerView mv = new MyMarkerView(mContext, R.layout.custom_marker_view);
        mv.setXValues(mXValues, mYValues);
        // set the marker to the chart
        mLinechart.setMarkerView(mv);

        mLinechart.animateX(2500); // 立即执行的动画,x轴
    }

    private LineData getLineData(InvestSummaryTab3Bean info) {

        ArrayList<Map<String, Double>> data = info.getData();
        if (data == null) return null;

        mXValues = new ArrayList<String>();
        mYValues = new ArrayList<Entry>();
//        toastValues.clear();

        //开始第一点圆点
        mXValues.add("startTime");
        mYValues.add(new Entry(0f, 0));
//        toastValues.add(new Pair<String, Float>("startTime",0f));

        for (Map<String, Double> map : data) {
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                double value = map.get(key);
                mXValues.add(key);
//                String format = String.format("%.2f", value);
//                float val = Float.parseFloat(format);
                mYValues.add(new Entry((float) value, mYValues.size()));
//                toastValues.add(new Pair<String, Float>(key, val));
            }
        }
        //最后一点
        mXValues.add("endTime");
        mYValues.add(new Entry(0f, mYValues.size()));
//        toastValues.add(new Pair<String, Float>("endTime",0f));

        //startTime+endTime
        try {
            mStartTime.setText(mXValues.get(1));
            mEndTime.setText(mXValues.get(mXValues.size() - 2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //投资总额(元)
        LineDataSet lineDataSet = new LineDataSet(mYValues, "收益统计" /*显示在比例图上*/);
        // create a dataset and give it a type
        // y轴的数据集合
        // mLineDataSet.setFillAlpha(110);
        // mLineDataSet.setFillColor(Color.RED);

        //用y轴的集合来设置参数
        lineDataSet.setLineWidth(1.75f); // 线宽
        lineDataSet.setCircleSize(2f);// 显示的圆形大小
        lineDataSet.setValueTextSize(10);
        lineDataSet.setColor(getResources().getColor(R.color.text_black_no5_gray));// 显示颜色
        lineDataSet.setCircleColor(Color.WHITE);// 圆形的颜色
        lineDataSet.setHighLightColor(Color.WHITE); // 高亮的线的颜色
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawVerticalHighlightIndicator(false);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.parseColor("#D08685"));
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.fade_red);
            lineDataSet.setFillDrawable(drawable);
        } else {
            lineDataSet.setFillColor(Color.BLACK);
        }

        ArrayList<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();
        lineDataSets.add(lineDataSet); // add the datasets

        // create a data object with the datasets
        LineData lineData = new LineData(mXValues, lineDataSets);

        return lineData;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        boolean isHideHighlight = (e.getXIndex() == 0 || e.getXIndex() == mLinechart.getXValCount() - 1) ? true : false;
        if (mLinechart.getData() != null && !isHideHighlight) {
//            Logger.e("出现");
            mLinechart.setDrawMarkerViews(true);
        } else {
//            Logger.e("隐藏");
            mLinechart.setDrawMarkerViews(false);
        }
    }

    @Override
    public void onNothingSelected() {
    }
}
