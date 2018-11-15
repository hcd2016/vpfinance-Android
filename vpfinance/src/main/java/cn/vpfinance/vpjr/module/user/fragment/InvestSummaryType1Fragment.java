package cn.vpfinance.vpjr.module.user.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.InvestSummaryTab1Bean;
import cn.vpfinance.vpjr.util.Common;

/**
 */
public class InvestSummaryType1Fragment extends BaseFragment {

    @Bind(R.id.type1)
    TextView mType1;
    @Bind(R.id.type2)
    TextView mType2;
    @Bind(R.id.type3)
    TextView mType3;
    @Bind(R.id.type4)
    TextView mType4;
    @Bind(R.id.type5)
    TextView mType5;
    @Bind(R.id.totalInvestCount)
    TextView mTotalInvestCount;
    private Context mContext;

    @Bind(R.id.type1_piechart)
    PieChart mPieChart;
    private HttpService mHttpService;
    private ArrayList<Integer> colors = new ArrayList<Integer>();
    private int accountType = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public static InvestSummaryType1Fragment newInstance(int account) {
        InvestSummaryType1Fragment fragment = new InvestSummaryType1Fragment();
        Bundle args = new Bundle();
        args.putInt(Constant.AccountType, account);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(mContext, R.layout.fragment_invest_summary_type1, null);
        ButterKnife.bind(this, view);

        Bundle arguments = getArguments();
        if (arguments != null) {
            accountType = arguments.getInt(Constant.AccountType);
        }

        mHttpService = new HttpService(mContext, this);
        initPieChart();
        mHttpService.getProductInvestDistribution(accountType);
        return view;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_ProductInvestDistribution.ordinal()) {
            InvestSummaryTab1Bean info = mHttpService.onGetProductInvestDistribution(json);
            if (info == null) return;

            initView(info);
            ArrayList<Pair<String, Float>> list = getData(info);
            setPieChartDate(list, colors);
        }
    }

    private void initView(InvestSummaryTab1Bean info) {
        int count = info.getCount1() + info.getCount2() + info.getCount3() + info.getCount4() + info.getCount5();
        mTotalInvestCount.setText("共出借\n" + count + "个项目");
        mType1.setText(info.getCount1() + "个");
        mType2.setText(info.getCount2() + "个");
        mType3.setText(info.getCount3() + "个");
        mType4.setText(info.getCount4() + "个");
        mType5.setText(info.getCount5() + "个");
    }

    private ArrayList<Pair<String, Float>> getData(InvestSummaryTab1Bean info) {
        colors.clear();

        ArrayList<Pair<String, Float>> list = new ArrayList<>();
        float sum = info.getCount1() + info.getCount2() + info.getCount3() + info.getCount4() + info.getCount5();

        if (info.getCount1() != 0) {
            float v1 = info.getCount1() / sum;
            list.add(new Pair<String, Float>(String.format("%.2f", v1 * 100) + "%", v1));
            colors.add(getResources().getColor(R.color.product_invest_distributed_1));
        }
        if (info.getCount2() != 0) {
            float v2 = info.getCount2() / sum;
            list.add(new Pair<String, Float>(String.format("%.2f", v2 * 100) + "%", v2));
            colors.add(getResources().getColor(R.color.product_invest_distributed_2));
        }
        if (info.getCount3() != 0) {
            float v3 = info.getCount3() / sum;
            list.add(new Pair<String, Float>(String.format("%.2f", v3 * 100) + "%", v3));
            colors.add(getResources().getColor(R.color.product_invest_distributed_3));
        }
        if (info.getCount4() != 0) {
            float v4 = info.getCount4() / sum;
            list.add(new Pair<String, Float>(String.format("%.2f", v4 * 100) + "%", v4));
            colors.add(getResources().getColor(R.color.product_invest_distributed_4));
        }
        if (info.getCount5() != 0) {
            float v5 = info.getCount5() / sum;
            list.add(new Pair<String, Float>(String.format("%.2f", v5 * 100) + "%", v5));
            colors.add(getResources().getColor(R.color.product_invest_distributed_5));
        }
        return list;
    }


    private void initPieChart() {
        mPieChart.setUsePercentValues(false);
        mPieChart.setDescription("");
        mPieChart.setDragDecelerationFrictionCoef(0.95f);
        mPieChart.setExtraOffsets(0f, 5f, 0f, 5f);
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.parseColor("#444444"));
        mPieChart.setTransparentCircleColor(Color.parseColor("#444444"));
        mPieChart.setTransparentCircleAlpha(110);

        mPieChart.setHoleRadius(60f);//白色的内圈半径
//        mPieChart.setTransparentCircleRadius(90f);//扇形半径
        mPieChart.setDrawCenterText(true);
//        setValueLinePart1OffsetPercentage
//        mPieChart.setCenterTextRadiusPercent(0.8f);
        mPieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mPieChart.setRotationEnabled(false);//不能旋转
        mPieChart.setHighlightPerTapEnabled(false);//点击变大
    }

    private void setPieChartDate(ArrayList<Pair<String, Float>> list, ArrayList<Integer> colors) {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        int i = 0;
        for (Pair<String, Float> pair : list) {
            xVals.add(pair.first);
            yVals1.add(new Entry(pair.second, i));
            i++;
        }

        PieDataSet dataSet = new PieDataSet(yVals1, "Election Results");
//        dataSet.setSliceSpace(3f);//每个比例之间的间隔
//        dataSet.setSelectionShift(5f);//选中后稍微会扩大一点点
        dataSet.setColors(colors);

        dataSet.setValueLinePart1OffsetPercentage(80.f);//标示线指向环内多少
        dataSet.setDrawValues(false);//不显示值
        dataSet.setValueLinePart1Length(0.4f);//标识百分比短线的长度
        dataSet.setValueLinePart2Length(0.8f);//标识百分比长线的长度
        dataSet.setValueLineColor(Color.WHITE);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);//设置X（环外）显示内容，也就是显示x坐标

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(9f);
//        data.setValueTextColor(Color.parseColor("#556080"));
        data.setValueTextColor(Color.WHITE);
        mPieChart.setData(data);

        // undo all highlights
        mPieChart.highlightValues(null);

        mPieChart.invalidate();

        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = mPieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setEnabled(false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
