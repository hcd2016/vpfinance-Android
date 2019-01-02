package cn.vpfinance.vpjr.module.user;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.user.personal.MedalDetailActivity;
import cn.vpfinance.vpjr.module.user.personal.MyFriendActivity;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.PersonalCardBean;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.SharedPreferencesHelper;
import cn.vpfinance.vpjr.view.CircleImg;
import cn.vpfinance.vpjr.view.MedalXImageView;
import cn.vpfinance.vpjr.view.pulltozoomview.PullToZoomScrollViewEx;


public class OtherPersonalCardActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.scroll_view)
    PullToZoomScrollViewEx scrollView;
    @Bind(R.id.backButton)
    ImageButton            mBackButton;

    private HttpService mHttpService;
    ArrayList<Integer>     colors    = new ArrayList<Integer>();
    private ArrayList<Pair<String,Float>> pieList = new ArrayList<Pair<String,Float>>();
    private ArrayList<Pair<String,Float>> barList = new ArrayList<Pair<String,Float>>();
    private User user;
    public static final String UID = "uid";
    private List<PersonalCardBean.UserMedalsEntity> mUserMedals;
    private List<PersonalCardBean.MyFriendsEntity>  mMyFriends;
    private String                                  mUserId;
    private CircleImg                               mUserHead;
    private TextView                                mUsername;
    private TextView                                mMyDescribe;
    private TextView                                mLoginTime;
    private TextView                                medals_tv;
    private TextView                                medals_tv1;
    private TextView                                friend_tv;
    private TextView                                friend_hide;
    private ImageView                               ivZoom;
    private PieChart                                mPieChart;
    private BarChart                                mBarChart;
    private GridLayout                              mMedals;
    private LinearLayout                            mInviteFriends;
    private LinearLayout                            isShowFriend;
    private LinearLayout                            isShowTender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarCompat1.translucentStatusBar(this);
        setContentView(R.layout.activity_personal_card);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            mUserId = intent.getStringExtra(UID);
        }
        loadHeadPullZoom();
        mHttpService = new HttpService(this, this);
        mHttpService.getPersonalCard(mUserId);
        //setHeadImage();
        //setUserBg();
        medals_tv1.setText("Ta的勋章");
        friend_tv.setText("Ta邀请的好友");

        initListener();
        initPieChart();
        initBarChart();

    }

    private void loadHeadPullZoom() {
        //        PullToZoomScrollViewEx scrollView = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);
        View mHeadView = LayoutInflater.from(this).inflate(R.layout.part_personal_card_header, null, false);
        View mZoomView = LayoutInflater.from(this).inflate(R.layout.part_personal_card_zoom, null, false);
        View mContentView = LayoutInflater.from(this).inflate(R.layout.part_personal_card_content, null, false);

        mUserHead = ((CircleImg) mHeadView.findViewById(R.id.userHead));
        mUsername = ((TextView) mHeadView.findViewById(R.id.username));
        mMyDescribe = ((TextView) mHeadView.findViewById(R.id.my_describe));
        mLoginTime = ((TextView) mHeadView.findViewById(R.id.loginTime));
        ivZoom = ((ImageView) mZoomView.findViewById(R.id.iv_zoom));
        mPieChart = ((PieChart) mContentView.findViewById(R.id.pieChart));
        mBarChart = ((BarChart) mContentView.findViewById(R.id.barChart));
        mMedals = ((GridLayout) mContentView.findViewById(R.id.medals));
        mInviteFriends = ((LinearLayout) mContentView.findViewById(R.id.inviteFriends));
        isShowFriend = ((LinearLayout) mContentView.findViewById(R.id.isShowFriend));
        isShowTender = ((LinearLayout) mContentView.findViewById(R.id.isShowTender));
        medals_tv = ((TextView) mContentView.findViewById(R.id.medals_tv));
        medals_tv1 = ((TextView) mContentView.findViewById(R.id.medals_tv1));
        friend_tv = ((TextView) mContentView.findViewById(R.id.friend_tv));
        friend_hide = ((TextView) mContentView.findViewById(R.id.friend_hide));

        scrollView.setHeaderView(mHeadView);
        scrollView.setZoomView(mZoomView);
        scrollView.setScrollContentView(mContentView);
        scrollView.setParallax(false);

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        scrollView.setHeaderLayoutParams(localObject);
    }


    private void initListener() {
        mBackButton.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void init(final PersonalCardBean personalCardBean) {

        if(personalCardBean == null) return;
        if (mUserMedals == null || mUserMedals.size() == 0) {
            medals_tv.setVisibility(View.VISIBLE);
            medals_tv.setText("Ta还未获得勋章");
        }
        if (mMyFriends == null || mMyFriends.size() == 0) {
            friend_hide.setVisibility(View.VISIBLE);
            friend_hide.setText("Ta还未邀请过好友");
        }

        //*----------勋章和朋友数据-------------*//*
        if (mUserMedals != null) {
            for (int j = 0; j < mUserMedals.size(); j++) {
//                ImageView imageView = new ImageView(this);
//                ImageLoader.getInstance().displayImage(mUserMedals.get(j).logo + "3.png", imageView);
                MedalXImageView medalXImageView = new MedalXImageView(this);
                medalXImageView.setStyle(MedalXImageView.STYLE_SMALL);
                medalXImageView.setBackground(mUserMedals.get(j).logo + "3.png");
                medalXImageView.setXNum(mUserMedals.get(j).number);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((getScreenWidth() - Utils.dip2px(this, 30)) / 5, (getScreenWidth() - Utils.dip2px(this, 30)) / 5);
                params.setMargins(Utils.dip2px(this, 10), 0, 0, 0);
                final int y = j;
                medalXImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OtherPersonalCardActivity.this, MedalDetailActivity.class);
                        intent.putExtra(MedalDetailActivity.MEDAL_ID, "" + personalCardBean.userMedals.get(y).id);
                        intent.putExtra(MedalDetailActivity.MEDAL_STATUS, true);
                        intent.putExtra(MedalDetailActivity.MEDAL_NAME, "" + personalCardBean.userMedals.get(y).name);
                        intent.putExtra(MedalDetailActivity.MEDAL_DESCRIPTION, "" + personalCardBean.userMedals.get(y).description);
                        intent.putExtra(MedalDetailActivity.MEDAL_LOGO, "" + personalCardBean.userMedals.get(y).logo);
                        intent.putExtra(MedalDetailActivity.MEDAL_CONDITION, "" + personalCardBean.userMedals.get(y).condition);
                        gotoActivity(intent);
                    }
                });
                medalXImageView.setLayoutParams(params);
                mMedals.addView(medalXImageView);
            }
        }

        CircleImg circleImg = null;
        if(mMyFriends == null)  return;
        int temp = mMyFriends.size() > 6 ? 6 : mMyFriends.size();

        for (int j = 0; j < temp; j++) {
            if (temp == 6 && j == temp - 1) {
                circleImg = new CircleImg(this);
                circleImg.setImageResource(R.drawable.tv_more);
                circleImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OtherPersonalCardActivity.this, MyFriendActivity.class);
                        gotoActivity(intent);
                    }
                });
            } else {
                circleImg = new CircleImg(this);
                ImageLoader.getInstance().displayImage(HttpService.mBaseUrl + mMyFriends.get(j).imgUrl, circleImg);
                final int finalJ = j;
                circleImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OtherPersonalCardActivity.this, OtherPersonalCardActivity.class);
                        intent.putExtra(UID, personalCardBean.myFriends.get(finalJ).userId);
                        gotoActivity(intent);
                    }
                });
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (getScreenWidth() - Utils.dip2px(this, 5) * 7) / 6
                    , (getScreenWidth() - Utils.dip2px(this, 5) * 7) / 6);
            params.setMargins(Utils.dip2px(this, 5), 0, 0, Utils.dip2px(this, 10));
            circleImg.setLayoutParams(params);
            mInviteFriends.addView(circleImg);

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setUserBg(String url) {
        if (TextUtils.isEmpty(url)) {
            ivZoom.setImageDrawable(getResources().getDrawable(R.drawable.bg_personalcard_header));
        } else {
            ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    ivZoom.setImageDrawable(getResources().getDrawable(R.drawable.bg_personalcard_header));
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    Drawable drawable = new BitmapDrawable(null,bitmap);
                    ivZoom.setImageDrawable(drawable);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_Personal_Card.ordinal()) {
            PersonalCardBean personalCardBean = mHttpService.onGetPersonalCard(json);
            colors.clear();
            try {
                mUserMedals = personalCardBean.userMedals;
                mMyFriends = personalCardBean.myFriends;
                isShowTender.setVisibility(personalCardBean.isShowInvestInfo ? View.VISIBLE : View.GONE);
                isShowFriend.setVisibility(personalCardBean.isShowFriends ? View.VISIBLE : View.GONE);

                init(personalCardBean);
                if (personalCardBean.month1 != null) {
                    float month1 = Float.parseFloat(personalCardBean.month1);
                    if (0 < month1 && month1 < 1.5) {
                        colors.add(getResources().getColor(R.color.product_invest_distributed_1));
                        pieList.add(new Pair<String, Float>("1个月", 1.5f));
                    } else if (month1 != 0) {
                        colors.add(getResources().getColor(R.color.product_invest_distributed_1));
                        pieList.add(new Pair<String, Float>("1个月", month1));
                    }
                }
                if (personalCardBean.month2 != null) {
                    float month2 = Float.parseFloat(personalCardBean.month2);
                    if (0 < month2 && month2 < 1.5) {
                        colors.add(getResources().getColor(R.color.product_invest_distributed_2));
                        pieList.add(new Pair<String, Float>("2个月", 1.5f));
                    } else if (month2 != 0) {
                        colors.add(getResources().getColor(R.color.product_invest_distributed_2));
                        pieList.add(new Pair<String, Float>("2个月", month2));
                    }
                }
                if (personalCardBean.month3 != null) {
                    float month3 = Float.parseFloat(personalCardBean.month3);
                    if (0 < month3 && month3 < 1.5) {
                        colors.add(getResources().getColor(R.color.product_invest_distributed_3));
                        pieList.add(new Pair<String, Float>("3个月", 1.5f));
                    } else if (month3 != 0) {
                        colors.add(getResources().getColor(R.color.product_invest_distributed_3));
                        pieList.add(new Pair<String, Float>("3个月", month3));
                    }
                }
                if (personalCardBean.month4 != null) {
                    float month4 = Float.parseFloat(personalCardBean.month4);
                    if (0 < month4 && month4 < 1.5) {
                        colors.add(getResources().getColor(R.color.product_invest_distributed_4));
                        pieList.add(new Pair<String, Float>("4-6个月", 1.5f));
                    } else if (month4 != 0) {
                        colors.add(getResources().getColor(R.color.product_invest_distributed_4));
                        pieList.add(new Pair<String, Float>("4-6个月", month4));
                    }
                }
                if (personalCardBean.month5 != null) {
                    float month5 = Float.parseFloat(personalCardBean.month5);
                    if (0 < month5 && month5 < 1.5) {
                        colors.add(getResources().getColor(R.color.product_invest_distributed_5));
                        pieList.add(new Pair<String, Float>("6个月以上", 1.5f));
                    } else if (month5 != 0) {
                        colors.add(getResources().getColor(R.color.product_invest_distributed_5));
                        pieList.add(new Pair<String, Float>("6个月以上", month5));
                    }
                }

                setPieChartDate(pieList, colors);

                if (personalCardBean.returnRate != null) {
                    barList.add(new Pair<String, Float>("Ta的", Float.parseFloat(personalCardBean.returnRate.myProfit)));
                    barList.add(new Pair<String, Float>("最高", Float.parseFloat(personalCardBean.returnRate.maxProfit)));
                    barList.add(new Pair<String, Float>("平均", Float.parseFloat(personalCardBean.returnRate.avgProfit)));
                }
                setBarChartDate(barList);

                mUsername.setText(personalCardBean.name);
                mLoginTime.setText(personalCardBean.loginStatus);
                mMyDescribe.setText(TextUtils.isEmpty(personalCardBean.signature)? "未设置个人签名" : personalCardBean.signature);

                if (!TextUtils.isEmpty(personalCardBean.imageUrl)) {
                    ImageLoader.getInstance().displayImage(HttpService.mBaseUrl + personalCardBean.imageUrl, mUserHead);
                }

                setUserBg(HttpService.mBaseUrl + personalCardBean.background);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //                        ImageLoader.getInstance().displayImage(BaseUrl.mBaseUrl + personalCardBean.imageUrl, mUserHead);
        }
    }

    private void setBarChartDate(ArrayList<Pair<String, Float>> list) {
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<String>();

        yVals.add(new BarEntry(0f, -1));

        for (int i=0; i<list.size(); i++){
            Pair<String, Float> pair = list.get(i);
            xVals.add(pair.first);
            yVals.add(new BarEntry(pair.second, i));
        }
        xVals.add("");

        BarDataSet set = new BarDataSet(yVals, "Data Set");
        set.setValueFormatter(new PercentFormatter());
        set.setValueTextSize(10f);

        ArrayList<Integer> clos = new ArrayList<>();
        clos.add(getResources().getColor(R.color.product_invest_distributed_3));
        clos.add(getResources().getColor(R.color.product_invest_distributed_3));
        clos.add(getResources().getColor(R.color.product_invest_distributed_4));
        clos.add(getResources().getColor(R.color.product_invest_distributed_1));
        set.setColors(clos);
        set.setDrawValues(true);

        BarData data = new BarData(xVals, set);

        mBarChart.setData(data);
        mBarChart.invalidate();
        mBarChart.animateY(800);
    }

    private void initPieChart() {
        mPieChart.setUsePercentValues(false);
        mPieChart.setDescription("");
        mPieChart.setNoDataTextDescription("Ta还未出借过");
        mPieChart.setDragDecelerationFrictionCoef(0.95f);
        mPieChart.setExtraOffsets(20f, 10f, 20f, 10f);
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.WHITE);
        mPieChart.setTransparentCircleColor(Color.WHITE);
        mPieChart.setTransparentCircleAlpha(110);
        mPieChart.setNoDataTextDescription("数据加载失败");

        mPieChart.setHoleRadius(40f);//白色的内圈半径
        mPieChart.setTransparentCircleRadius(0f);//扇形半径

        mPieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mPieChart.setRotationEnabled(false);//不能旋转
        mPieChart.setHighlightPerTapEnabled(false);//点击变大
    }

    private void initBarChart() {
        mBarChart.setDescription("");
        mBarChart.setHighlightPerTapEnabled(false);
        mBarChart.setExtraOffsets(10, 10, 10, 10);
        mBarChart.setPinchZoom(false);
        mBarChart.setTouchEnabled(false);
        mBarChart.setNoDataTextDescription("数据加载失败");

        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawGridBackground(false);
        mBarChart.setSaveFromParentEnabled(true);
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelsToSkip(0);
        xAxis.setDrawGridLines(false);

        mBarChart.getAxisLeft().setDrawGridLines(false);
        mBarChart.getAxisRight().setDrawGridLines(false);

        YAxis RightYAxis = mBarChart.getAxisRight();
        RightYAxis.setEnabled(false);
        RightYAxis.setDrawAxisLine(false);

        YAxis leftYAxis = mBarChart.getAxisLeft();
        leftYAxis.setEnabled(false);
        leftYAxis.setDrawGridLines(false);
        leftYAxis.setDrawAxisLine(false);

        mBarChart.getLegend().setEnabled(false);
    }

    private void setPieChartDate(ArrayList<Pair<String, Float>> list, ArrayList<Integer> colors) {

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        for (int i=0; i<list.size(); i++){
            Pair<String, Float> pair = list.get(i);
            xVals.add(pair.first);
            yVals1.add(new Entry(pair.second, i));
        }

        PieDataSet dataSet = new PieDataSet(yVals1, "产品期限");
        dataSet.setSliceSpace(3f);//每个比例之间的间隔
        dataSet.setSelectionShift(5f);//选中后稍微会扩大一点点
        dataSet.setColors(colors);

        dataSet.setValueLinePart1OffsetPercentage(80.f);//标示线指向环内多少
        dataSet.setDrawValues(false);//不显示值
        //        dataSet.setValueLinePart1Length(0.2f);//标识百分比短线的长度
        dataSet.setValueLinePart2Length(0.4f);//标识百分比长线的长度
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);//设置X（环外）显示内容，也就是显示x坐标

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(9f);
        //        data.setValueTextColor(Color.parseColor("#556080"));
        data.setValueTextColor(Color.BLACK);
        mPieChart.setData(data);

        // undo all highlights
        mPieChart.highlightValues(null);

        mPieChart.invalidate();

        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = mPieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setEnabled(false);
    }

    private void setHeadImage() {
        String headUrl = SharedPreferencesHelper.getInstance(this).getStringValue(SharedPreferencesHelper.USER_HEAD_URL + user.getUserId());
        if (headUrl == null) {
            mUserHead.setImageResource(R.drawable.user_head);
        } else {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(headUrl, mUserHead);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                finish();
                break;
        }
    }

    /**
     * 得到屏幕宽度
     *
     * @return
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
