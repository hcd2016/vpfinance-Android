package cn.vpfinance.vpjr.module.home.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.adapter.HomeDepositAdapter;
import cn.vpfinance.vpjr.adapter.HomeRegularAdapter;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.greendao.User;
import cn.vpfinance.vpjr.gson.AppmemberIndexBean;
import cn.vpfinance.vpjr.gson.IndexPacketBean;
import cn.vpfinance.vpjr.model.RefreshCountDown;
import cn.vpfinance.vpjr.module.home.IndexRedPacketActivity;
import cn.vpfinance.vpjr.module.home.InviteGiftIntroduceActivity;
import cn.vpfinance.vpjr.module.home.NewWelfareActivity;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.util.DBUtils;
import cn.vpfinance.vpjr.view.FloatingAdView;
import cn.vpfinance.vpjr.view.LinearLayoutForListView;
import cn.vpfinance.vpjr.view.VerticalScrollTextView;
import de.greenrobot.event.EventBus;

public class HomeFragment extends BaseFragment implements View.OnClickListener {

    private static final int REFRESH_HOME = 1;
    private Context mContext;
    private HttpService mHttpService;

    private View view;
    private ArrayList<Pair<String, String>> informsList = new ArrayList<>();
    private VerticalScrollTextView informs;

    private ConvenientBanner mBanner;

    private FloatingAdView mFloatingAdView;
    private RelativeLayout mRelativeLayout;
    private LinearLayoutForListView mDepositProduct;
    private LinearLayoutForListView mRegularProduct;
    private LinearLayout mBusinessMode;
    private boolean mIsfirst = true;//是否是第一次加载数据进来，
    private SwipeRefreshLayout vSwipeRefreshLayout;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("HomeFragment");
        if (mHttpService != null) {
            mHttpService.getAppmemberIndex(false);
            mHttpService.getShareRedPacketList();
        }
        if (mBanner != null) {
            mBanner.startTurning(3000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HomeFragment");
        if (mBanner != null) {
            mBanner.stopTurning();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_home, null);

        String str = "";
        if (HttpService.mBaseUrl.contains("http://www.vpfinance.cn/") || HttpService.mBaseUrl.contains("https://www.vpfinance.cn/")) {
            str = getString(R.string.app_name);
        } else {
            str = HttpService.mBaseUrl;
        }
        ((ActionBarLayout) view.findViewById(R.id.titleBar)).reset().setTitle(str);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            titleBar.setFakeStatusBar(true);
//        }

        view.findViewById(R.id.informs_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = DBUtils.getUser(mContext);
                String uid = "";
                if (user != null) {
                    uid = "?uid=" + user.getUserId();
                }
                String activiceUrl = "/AppContent/toPlatformBox" + uid;
                gotoWeb(activiceUrl, "活动专区");
            }
        });
        informs = ((VerticalScrollTextView) view.findViewById(R.id.informs));
        mBusinessMode = ((LinearLayout) view.findViewById(R.id.business_mode));
        mRelativeLayout = ((RelativeLayout) view.findViewById(R.id.floatingAdViewParent));
        view.findViewById(R.id.clickNewWelfare).setOnClickListener(this);
        view.findViewById(R.id.clickInviteGift).setOnClickListener(this);
        view.findViewById(R.id.clickHelp).setOnClickListener(this);
        mFloatingAdView = ((FloatingAdView) view.findViewById(R.id.floatingAdView));
        mDepositProduct = ((LinearLayoutForListView) view.findViewById(R.id.deposit_products));
        mRegularProduct = ((LinearLayoutForListView) view.findViewById(R.id.regular_products));

        if (mHttpService == null) {
            mHttpService = new HttpService(getActivity(), this);
        }
        mHttpService.getAppmemberIndex(false);

        vSwipeRefreshLayout = ((SwipeRefreshLayout) view.findViewById(R.id.vSwipeRefreshLayout));
        vSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.main_color));
        vSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHttpService.getShareRedPacketList();
                mHttpService.getAppmemberIndex(false);
            }
        });

        initView(view);

        return view;
    }

    private void initView(View view) {
        //获得状态栏高度
        Rect frame = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int mStatusBarHeight = frame.top;
        int mActionBarHeight = (int) (getActivity().getResources().getDimension(R.dimen.bar_height));
        mFloatingAdView.setExtraHeight(mStatusBarHeight, mActionBarHeight);

        mBanner = ((ConvenientBanner) view.findViewById(R.id.convenientBanner));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onHttpSuccess(int req, final JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (req == ServiceCmd.CmdId.CMD_SHARE_RED_PACKET_LIST.ordinal()) {
            final String content = json.toString();
            IndexPacketBean indexPacketBean = new Gson().fromJson(content, IndexPacketBean.class);
            if (indexPacketBean.count == 0) {
                mHttpService.getHomeEvent();
            } else {
                mRelativeLayout.setVisibility(View.VISIBLE);
                mFloatingAdView.setVisibility(View.VISIBLE);
                mFloatingAdView.setImageResource(R.drawable.index_red_packet);
                mFloatingAdView.setOnFloatingAdClickListener(new FloatingAdView.onFloadingAdClickListener() {
                    @Override
                    public void onAdClick() {
                        IndexRedPacketActivity.goThis(mContext, content);
                    }
                });
            }
        }
        if (req == ServiceCmd.CmdId.CMD_APPMEMBER_INDEX.ordinal()) {
            vSwipeRefreshLayout.setRefreshing(false);
            AppmemberIndexBean appmemberIndexBean = mHttpService.onGetAppmemberIndex(json);
            if (appmemberIndexBean != null) {
                List<AppmemberIndexBean.BannerBean> banner = appmemberIndexBean.banner;
//                initBanner(banner);
                if (mIsfirst) {
                    //只有是第一次进来才设置banner，避免重复刷新数据 页面切换时banner又回到第一页
                    mBanner.setPages(
                            new CBViewHolderCreator<LocalImageHolderView>() {
                                @Override
                                public LocalImageHolderView createHolder() {
                                    return new LocalImageHolderView();
                                }
                            }, banner)
                            //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                            .setPageIndicator(new int[]{R.drawable.icon_yuan2, R.drawable.icon_yuan})
                            //设置指示器的方向
                            .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                            .setBackgroundResource(R.drawable.img_loading);
                    mIsfirst = false;
                }

                //小薇头条
                List<AppmemberIndexBean.ContentsBean> contents = appmemberIndexBean.contents;
                initXiaoWei(contents);

                //首页推荐标
                AppmemberIndexBean.LoanDataBean loanData = appmemberIndexBean.loanData;
                initLoanProduct(loanData);
                informs.setScrollList(informsList);

                //产品模式
                List<AppmemberIndexBean.UrlsBean> urls = appmemberIndexBean.urls;
                initBusinessMode(urls);
            }
        }

        if (req == ServiceCmd.CmdId.CMD_Home_Event.ordinal()) {
            String showImage = json.optString("showImage");
            if ("true".equals(showImage)) {
                mRelativeLayout.setVisibility(View.VISIBLE);
                mFloatingAdView.setVisibility(View.VISIBLE);
                String imageUrl = json.optString("imageUrl");
                //android端后台只返回一个url，不携带参数。如果登陆状态本地手动添加userAppId
                String tempUrl = json.optString("pageUrl") + "?TYPE=android";
                //Logger.e("tempUrl:"+tempUrl);
                if (AppState.instance().logined()) {
                    User user = DBUtils.getUser(mContext);
                    if (user != null) {
                        tempUrl = tempUrl + "&userAppId=" + user.getUserId();
                    }
                }
                final String pageUrl = tempUrl;
                //Logger.e("pageUrl:"+pageUrl);
                ImageLoader.getInstance().displayImage(HttpService.mBaseUrl + imageUrl + "3.png", mFloatingAdView);
                mFloatingAdView.setOnFloatingAdClickListener(new FloatingAdView.onFloadingAdClickListener() {
                    @Override
                    public void onAdClick() {
                        if (!TextUtils.isEmpty(pageUrl))
                            gotoWeb(pageUrl, "");
                    }
                });
            } else {
                mFloatingAdView.setVisibility(View.GONE);
                mRelativeLayout.setVisibility(View.GONE);
            }
        }
    }

    private void initBusinessMode(List<AppmemberIndexBean.UrlsBean> urls) {
        if (mBusinessMode == null) return;
        mBusinessMode.removeAllViews();
        for (final AppmemberIndexBean.UrlsBean url : urls) {
            if (url == null) return;
            View business = LayoutInflater.from(mContext).inflate(R.layout.item_home_business, null);
            final ImageView imageView = (ImageView) business.findViewById(R.id.image);
            final TextView textView = (TextView) business.findViewById(R.id.name);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(url.linkUrl)) {
                        gotoWeb(url.linkUrl, "");
                    }
                }
            });
            ImageLoader.getInstance().loadImage(url.imageUrl, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                }

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                    textView.setText(url.title);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
            mBusinessMode.addView(business);
        }
    }

    private void initLoanProduct(AppmemberIndexBean.LoanDataBean loanData) {
        if (loanData == null) return;

        informsList.add(new Pair<String, String>("累计投资金额(元):" + loanData.totalMoney, ""));
        informsList.add(new Pair<String, String>("累计收益金额(元):" + loanData.totalInterest, ""));
        //普通标
        List<AppmemberIndexBean.LoanDataBean.LoansignsBean> loansigns = loanData.loansigns;
        if (loansigns != null && loansigns.size() != 0) {
            mRegularProduct.setVisibility(View.VISIBLE);
            HomeRegularAdapter adapter = new HomeRegularAdapter();
            adapter.setData(mContext, loansigns);
            mRegularProduct.setAdapter(adapter);
        } else {
            mRegularProduct.setVisibility(View.GONE);
        }
        //定存宝
        List<AppmemberIndexBean.LoanDataBean.LoansignPoolBean> loansignPool = loanData.loansignPool;
        if (loansignPool != null && loansignPool.size() != 0) {
            mDepositProduct.setVisibility(View.VISIBLE);
            HomeDepositAdapter adapter = new HomeDepositAdapter();
            adapter.setData(mContext, loansignPool);
            mDepositProduct.setAdapter(adapter);
        } else {
            mDepositProduct.setVisibility(View.GONE);
        }

    }

    private void initXiaoWei(List<AppmemberIndexBean.ContentsBean> contents) {
        informsList.clear();
        for (AppmemberIndexBean.ContentsBean content : contents) {
            Pair<String, String> pair = new Pair<>(content.title, content.linkUrl);
            informsList.add(pair);
        }
    }

    public void onEventMainThread(RefreshCountDown event) {
//        Logger.e("Time1"+System.currentTimeMillis());
        if (event != null && event.isRefresh == true && isAdded()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mHttpService != null) {
//                        Logger.e("Time2"+System.currentTimeMillis());
                        mHttpService.getAppmemberIndex(false);
                    }
                }
            }, Constant.delay);
        }
    }

    public void onHttpError(int reqId, String errmsg) {
        if (isAdded() && vSwipeRefreshLayout != null) {
            vSwipeRefreshLayout.setRefreshing(false);
        }
        onHttpCache(reqId);
    }

    public class LocalImageHolderView implements Holder<AppmemberIndexBean.BannerBean> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, final AppmemberIndexBean.BannerBean data) {

            imageView.setImageResource(R.drawable.img_loading);
            if (!TextUtils.isEmpty(data.imgurl)) {
//                Glide.with(getActivity()).load(data.imgurl).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.img_loading).into(imageView);
                ImageLoader.getInstance().displayImage(data.imgurl, imageView);
            }
            final String dataUrl = data.url;
            final int type = data.type;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    switch (type) {
                        case 1:
                            if (!TextUtils.isEmpty(dataUrl)) {
                                gotoWeb(dataUrl, "");
                            }
                            break;
                        case 2:
                            try {
                                long id = Long.parseLong(dataUrl);
                                NewRegularProductActivity.goNewRegularProductActivity(mContext, id, 0, "产品详情", false, 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            });
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clickInviteGift:
                gotoActivity(InviteGiftIntroduceActivity.class);
                break;
            case R.id.clickHelp:
                gotoWeb("/AppContent/commonproblem", "常见问题");
                break;
            case R.id.clickNewWelfare:
                gotoActivity(NewWelfareActivity.class);
                break;
        }
    }
}
