package cn.vpfinance.vpjr.module.product.shenyang;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.joda.time.Interval;
import org.joda.time.Period;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.module.product.fragment.H5Fragment;
import cn.vpfinance.vpjr.module.product.fragment.PresellProductInvestListFragment;
import cn.vpfinance.vpjr.module.product.fragment.RegularProductQualificationMaterialFragment;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.greendao.UserDao;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import cn.vpfinance.vpjr.model.PresellProductInfo;
import cn.vpfinance.vpjr.module.product.invest.PresellProductInvestActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.view.InsideScrollView;
import cn.vpfinance.vpjr.view.InsideVP;

/**
 * 沈阳众筹项目
 */
public class PresellProductActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_PRODUCT_TYPE       = "type";
    public static final String EXTRA_PRODUCT_ID         = "id";
    public static final String EXTRA_PRODUCT_DATABSE_ID = "databse_id";
    public static final String EXTRA_PRODUCT_PRODUCT    = "product";
    private PagerSlidingTabStrip    tabs;
    private InsideVP                mViewPager;
    private FinanceProduct          product;
    private DaoMaster.DevOpenHelper dbHelper;
    private SQLiteDatabase          db;
    private DaoMaster               daoMaster;
    private DaoSession              daoSession;
    //    private FinanceProductDao dao;
    private UserDao                 userDao;

    private long               productLong;
    private MyAdapter          myAdapter;
    private ActionBarLayout    titleBar;
    private ImageView          ivProduct;
    private Button             btnInvest;
    private String             pid;
    private HttpService        mHttpService;
    private PresellProductInfo presellProductInfo;
    private TextView           tvPresellState;
    private NumberProgressBar  presellNumberProgressBar;
    private TextView           tvPresellTotalMoney;
    private TextView           tvMinRate;
    private TextView           tvMaxRate;
    private TextView           tvMonth;
    private TextView           tvRefundWay;
    private View               countDown;
    private TextView           countdown_day;
    private TextView           countdown_hour;
    private TextView           countdown_minute;
    private TextView           countdown_second;
    private MyCounter          counter;
    private InsideScrollView   scrollView;
    private View               splitLine;
    private LinearLayout       llTop;
    private int                height;

    public static void goPresellProductActivity(Context context, String id) {
        if (context != null) {
            Intent intent = new Intent(context, PresellProductActivity.class);
            intent.putExtra(EXTRA_PRODUCT_ID, id);
            //            intent.putExtra(EXTRA_PRODUCT_TYPE, type);
            //            intent.putExtra(EXTRA_PRODUCT_PRODUCT, product);
            //            intent.putExtra(EXTRA_PRODUCT_DATABSE_ID, dbId);
            context.startActivity(intent);
        }
    }

    private void initFind() {
        scrollView = ((InsideScrollView) findViewById(R.id.scrollView));
        tabs = ((PagerSlidingTabStrip) findViewById(R.id.tabs));
        mViewPager = ((InsideVP) findViewById(R.id.pager));
        titleBar = (ActionBarLayout) findViewById(R.id.titleBar);
        ivProduct = ((ImageView) findViewById(R.id.ivProduct));
        btnInvest = ((Button) findViewById(R.id.btnInvest));
        tvPresellState = (TextView) findViewById(R.id.tvPresellState);
        presellNumberProgressBar = (NumberProgressBar) findViewById(R.id.presellNumberProgressBar);
        tvPresellTotalMoney = (TextView) findViewById(R.id.tvPresellTotalMoney);
        tvMinRate = (TextView) findViewById(R.id.tvMinRate);
        tvMaxRate = (TextView) findViewById(R.id.tvMaxRate);
        tvMonth = (TextView) findViewById(R.id.tvMonth);
        tvRefundWay = (TextView) findViewById(R.id.tvRefundWay);
        countDown = findViewById(R.id.countDown);
        countdown_day = (TextView) findViewById(R.id.countdown_day);
        countdown_hour = (TextView) findViewById(R.id.countdown_hour);
        countdown_minute = (TextView) findViewById(R.id.countdown_minute);
        countdown_second = (TextView) findViewById(R.id.countdown_second);
        splitLine = findViewById(R.id.splitLine);
        llTop = ((LinearLayout) findViewById(R.id.llTop));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presell_product);
        if (mHttpService == null) {
            mHttpService = new HttpService(this, this);
        }
        initFind();
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void initView() {
        titleBar.setHeadBackVisible(View.VISIBLE);
        ivProduct.requestFocus();

        tabs.setIndicatorColor(0xFFFF3035);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mViewPager.setPageMargin(pageMargin);
        mViewPager.setOffscreenPageLimit(5);

        mViewPager.setScroll(false);

        final Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        final Rect rect = new Rect(0, 0, point.x, point.y);
        int[] location = new int[2];
        splitLine.getLocationInWindow(location);

        ViewTreeObserver vto1 = llTop.getViewTreeObserver();
        vto1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                llTop.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                height = llTop.getHeight();
            }
        });


        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int[] location = new int[2];
                splitLine.getLocationOnScreen(location);
                if (splitLine.getLocalVisibleRect(rect)){
                    mViewPager.setScroll(false);
                }else{
//                    int scrollY = scrollView.getScrollY();
                    mViewPager.setScroll(true);
                }
                return false;
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            pid = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
            mHttpService.getPresellProductInfo(pid);
        }
        btnInvest.setOnClickListener(this);

    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_PresellProductH5.ordinal()) {
            if (json == null) {
                return;
            }
            ArrayList<String> urls = new ArrayList<>();
            if (!TextUtils.isEmpty(json.optString("imageUrl1"))){
                urls.add(json.optString("imageUrl1"));
            }
            if (!TextUtils.isEmpty(json.optString("imageUrl2"))){
                urls.add(json.optString("imageUrl2"));
            }
            if (!TextUtils.isEmpty(json.optString("imageUrl3"))){
                urls.add(json.optString("imageUrl3"));
            }
            String borrowId = presellProductInfo.borrowId;
            try {
                long pid = Long.parseLong(borrowId);
                myAdapter = new MyAdapter(getSupportFragmentManager(), urls,pid);
                mViewPager.setAdapter(myAdapter);
                tabs.setViewPager(mViewPager);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if (reqId == ServiceCmd.CmdId.CMD_PresellProductInfo.ordinal()) {
            if (json == null) {
                return;
            }
            mHttpService.getPresellProductH5();
            presellProductInfo = mHttpService.onGetPresellProductInfo(json);

            titleBar.setTitle(TextUtils.isEmpty(presellProductInfo.borrowTitle) ? "沈阳众筹项目" : presellProductInfo.borrowTitle);
            if (!TextUtils.isEmpty(presellProductInfo.borrowStatus)){
                String borrowStatus = presellProductInfo.borrowStatus;
                String stateStr = "";
                stateStr = borrowStatus.equals("1") ? getString(R.string.productState1) :
                        borrowStatus.equals("2") ? "进行中" :
                                borrowStatus.equals("3") ? getString(R.string.productState3) :
                                        borrowStatus.equals("4") ? getString(R.string.productState4) : "";
                tvPresellState.setText(stateStr);

                btnInvest.setEnabled(borrowStatus.equals("1") ? false :
                        borrowStatus.equals("2") ? true :
                                borrowStatus.equals("3") ? false :
                                        borrowStatus.equals("4") ? false : true);
            }
            tvPresellTotalMoney.setText("¥"+presellProductInfo.issueLoan);
            tvMinRate.setText(presellProductInfo.minRate+"%");
            tvMaxRate.setText("预期可超过"+presellProductInfo.maxRate+"%");
            tvMonth.setText("最长"+presellProductInfo.month+"个月(3个月后可转让)");
            if (!TextUtils.isEmpty(presellProductInfo.refundWay)){//  1按月等额本息 2按月付息到期还本  3到期一次性还本息
                String refundWay = presellProductInfo.refundWay;
                String refundWayStr = "";
                refundWayStr = refundWay.equals("1") ? getString(R.string.refundState1) :
                        refundWay.equals("2") ? getString(R.string.refundState2) :
                                refundWay.equals("3") ? getString(R.string.refundState3) : "";
                tvRefundWay.setText(refundWayStr);
            }
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(false)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            if (!TextUtils.isEmpty(presellProductInfo.imageUrl)) {
                ImageLoader.getInstance().displayImage(presellProductInfo.imageUrl, ivProduct, options);
            }

            if (!TextUtils.isEmpty(presellProductInfo.issueLoan) && !TextUtils.isEmpty(presellProductInfo.total_tend_money)){
                String issueLoan = presellProductInfo.issueLoan;
                String total_tend_money = presellProductInfo.total_tend_money;
                try{
                    float total = Float.parseFloat(issueLoan);
                    float part = Float.parseFloat(total_tend_money);
                    presellNumberProgressBar.setProgress((float)(part*100/total));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            int day = 0;
            int hour = 0;
            int minute = 0;
            int second = 0;
            countdown_day.setText("" + day);
            countdown_hour.setText("" + hour);
            countdown_minute.setText("" + minute);
            countdown_second.setText("" + second);

            if (!TextUtils.isEmpty(presellProductInfo.borrowEndTime)){
                String borrowEndTime = presellProductInfo.borrowEndTime;
                try{
                    long endTime = Long.parseLong(borrowEndTime);
                    if(counter!=null)
                    {
                        counter.cancel();
                    }

                    long time = endTime - System.currentTimeMillis();

//            time = 20*24*60*60 * 1000 + 8*60*60 * 1000 + 18*60 * 1000   + 16 * 1000;
                    time = time/1000;
                    if(time>0 && time < 30*24*60*60)
                    {
                        //System.currentTimeMillis() + 20*24*60*60 * 1000 + 8*60*60 * 1000   + 18*60 * 1000   + 16 * 1000
                        Interval interval = new Interval(System.currentTimeMillis(), endTime);//
                        Period p = interval.toPeriod();
                        time = Math.max(time,0);
                        //Period p = Period.seconds((int)time);
                        countDown.setVisibility(View.VISIBLE);

                        countdown_day.setText("" + (p.getWeeks() * 7 + p.getDays()));
                        countdown_hour.setText("" + p.getHours());
                        countdown_minute.setText("" + p.getMinutes());
                        countdown_second.setText("" + p.getSeconds());
                        //countDown.setText(p.getDays()+"天 " + p.getHours()+ "时 "+p.getMinutes()+"分 "+p.getSeconds()+" 秒");

                        counter = new MyCounter(endTime - System.currentTimeMillis(), 1000, countDown,endTime);
                        counter.start();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onHttpError(int reqId, String errmsg) {
        super.onHttpError(reqId, errmsg);
        if (reqId == ServiceCmd.CmdId.CMD_PresellProductInfo.ordinal()) {
            titleBar.setTitle("沈阳众筹项目");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnInvest:
                Intent intent = new Intent(this,PresellProductInvestActivity.class);
                if (presellProductInfo.borrowId != null){
                    intent.putExtra(PresellProductInvestActivity.PID,presellProductInfo.borrowId);
                    startActivity(intent);
                }
                break;
        }
    }

    class MyAdapter extends FragmentStatePagerAdapter {

        private SparseIntArray names;

        private SparseArray<String> mFragmentTags;

        private final FragmentManager mFragmentManager;
        private ArrayList<String> urls;
        private long pid;
        private int type = -1;

        public MyAdapter(FragmentManager fm,ArrayList<String> urls,long pid) {
            super(fm);
            this.urls =urls;
            this.pid = pid;
            mFragmentManager = fm;
            mFragmentTags = new SparseArray<String>();

            names = new SparseIntArray();
            names.put(0, R.string.product_presell_name);
            names.put(1, R.string.product_presell_detail);
            names.put(2, R.string.product_presell_qualification_material);
            names.put(3, R.string.product_presell_risk_control);
            names.put(4, R.string.product_presell_inverst_record);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return H5Fragment.newInstance(urls.get(0));
                case 1:
                    return H5Fragment.newInstance(urls.get(1));
                case 2:
                    return RegularProductQualificationMaterialFragment.newInstance(pid);
                case 3:
                    return H5Fragment.newInstance(urls.get(2));
                case 4:
                    return PresellProductInvestListFragment.newInstance(pid, type);

                default:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (names != null) {
                return getString(names.get(position, R.string.app_name));
            }
            return getString(R.string.app_name);
        }

        public Fragment getFragment(int position) {
            String tag = mFragmentTags.get(position);
            if (tag == null)
                return null;
            return mFragmentManager.findFragmentByTag(tag);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            //super.setPrimaryItem(container, position, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            fragment.setMenuVisibility(true);
            fragment.setUserVisibleHint(true);
            return fragment;
        }
    }

    /* 定义一个倒计时的内部类 */
    class MyCounter extends CountDownTimer
    {
        private WeakReference<View> viewRef;
        private long endTime;
        public MyCounter(long millisInFuture, long countDownInterval,View countDown,long endTime)
        {
            super(millisInFuture, countDownInterval);
            viewRef = new WeakReference<View>(countDown);
            this.endTime = endTime;
        }

        @Override
        public void onFinish()
        {
            if (viewRef != null)
            {
                View countDown = viewRef.get();
                if (countDown != null)
                {
                    countDown.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            if (viewRef != null)
            {
                View countDown = viewRef.get();
                if (countDown != null)
                {
                    TextView countdown_day = (TextView)countDown.findViewById(R.id.countdown_day);
                    TextView countdown_hour = (TextView)countDown.findViewById(R.id.countdown_hour);
                    TextView countdown_minute = (TextView)countDown.findViewById(R.id.countdown_minute);
                    TextView countdown_second = (TextView)countDown.findViewById(R.id.countdown_second);


                    long time = millisUntilFinished;
//            time = 20*24*60*60 * 1000 + 8*60*60 * 1000 + 18*60 * 1000   + 16 * 1000;
                    time = time/1000;
                    if(time>0 && time < 30*24*60*60)
                    {
                        //System.currentTimeMillis() + 20*24*60*60 * 1000 + 8*60*60 * 1000   + 18*60 * 1000   + 16 * 1000
                        Interval interval = new Interval(System.currentTimeMillis(), endTime);//
                        Period p = interval.toPeriod();
                        time = Math.max(time,0);
                        //Period p = Period.seconds((int)time);
                        countDown.setVisibility(View.VISIBLE);

                        countdown_day.setText("" + (p.getWeeks() * 7 + p.getDays()));
                        countdown_hour.setText("" + p.getHours());
                        countdown_minute.setText("" + p.getMinutes());
                        countdown_second.setText("" + p.getSeconds());

                    }
                    else
                    {
                        countDown.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }
}
