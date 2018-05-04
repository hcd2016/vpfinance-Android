package cn.vpfinance.vpjr.module.product.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.ProductCarInfo;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.PictureZoomHelper;
import cn.vpfinance.vpjr.view.TagCloudView;

/**
 */
public class RegularProductCarInfoFragment extends BaseFragment {

    private static final String ARGS_PRODUCT_TYPE = "type";
    private static final String ARGS_PRODUCT_ID = "id";
    private static DisplayImageOptions mOptions;

    @Bind(R.id.carTag)
    TagCloudView mCarTag;
    @Bind(R.id.carBrand)
    TextView mCarBrand;
    @Bind(R.id.buyTime)
    TextView mBuyTime;
    @Bind(R.id.carNum)
    TextView mCarNum;
    @Bind(R.id.carPrice)
    TextView mCarPrice;
    @Bind(R.id.carDistance)
    TextView mCarDistance;
    @Bind(R.id.carPriceMaybe)
    TextView mCarPriceMaybe;
    @Bind(R.id.borrower)
    TextView mBorrower;
    @Bind(R.id.borrowMoney)
    TextView mBorrowMoney;
    @Bind(R.id.gender)
    TextView mGender;
    @Bind(R.id.borrowMoneyRemain)
    TextView mBorrowMoneyRemain;
    @Bind(R.id.age)
    TextView mAge;
    @Bind(R.id.borrowTime)
    TextView mBorrowTime;
    @Bind(R.id.audit)
    LinearLayout mAudit;

    private HttpService mHttpService;
    private long pid;
    private int type;

//    private View.OnClickListener clickImageListener =

//    private String mImg_url;

    public static RegularProductCarInfoFragment newInstance(long pid, int type) {
        RegularProductCarInfoFragment frag = new RegularProductCarInfoFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_PRODUCT_TYPE, type);
        args.putLong(ARGS_PRODUCT_ID, pid);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            pid = args.getLong(ARGS_PRODUCT_ID);
            type = args.getInt(ARGS_PRODUCT_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mHttpService = new HttpService(getActivity(), this);
        View view = View.inflate(getActivity(), R.layout.fragment_car_info, null);
        ButterKnife.bind(this, view);

        if (pid != 0){
            mHttpService.getProductCarInfo(""+pid);
        }
        return view;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_loanSignInfo.ordinal() && isAdded()){
            ProductCarInfo productCarInfo = mHttpService.onGetProductCarInfo(json);
            setViewData(productCarInfo);
        }
    }

    private void setViewData(ProductCarInfo productCarInfo) {
        if (productCarInfo == null) return;

        List<String> tags = new ArrayList<>();

        final List<ProductCarInfo.CarsInfoBean> carsInfo = productCarInfo.getCarsInfo();
        if (carsInfo != null && carsInfo.size() != 0){
            //初始化
            setCarData(carsInfo.get(0));

            for (ProductCarInfo.CarsInfoBean car : carsInfo) {
                if (!TextUtils.isEmpty(car.getCarinfonum())){
                    tags.add(car.getCarinfonum());
                }
            }
        }
        mCarTag.setTags(tags);
        mCarTag.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onTagClick(int position) {
                if (position == -1) {
//                    mCarTag.singleLine(mIsOpenTag);
//                    mIsOpenTag = !mIsOpenTag;
                } else {
                    if (carsInfo.get(position) != null){
                        setCarData(carsInfo.get(position));
                    }
                }
            }
        });

    }

    private void setCarData(ProductCarInfo.CarsInfoBean carsInfoBean) {
        if (carsInfoBean == null)   return;
        ProductCarInfo.CarsInfoBean.CarinfoBean carinfo = carsInfoBean.getCarinfo();
        if (carinfo != null){
            mCarBrand.setText(carinfo.getCar_name());
            mBuyTime.setText(carinfo.getBuy_time());
            mCarNum.setText(carinfo.getCar_num());
            mCarPrice.setText(carinfo.getBuy_price()+"元");
            mCarDistance.setText(carinfo.getKilometres());
            mCarPriceMaybe.setText(carinfo.getPledge_price()+"元");

            mBorrower.setText(carinfo.getPredebtor());
            mAge.setText(carinfo.getDebtor_age());
            mGender.setText(carinfo.getDebtor_sex());
            mBorrowMoney.setText(carinfo.getSumloanMoney()+"元");
            mBorrowMoneyRemain.setText(carinfo.getRemainloanMoney()+"元");
            mBorrowTime.setText(carinfo.getLoanperiod());
        }
        List<ProductCarInfo.CarsInfoBean.CarinfodetailBean> carinfodetail = carsInfoBean.getCarinfodetail();
        mAudit.removeAllViews();
        if (carinfodetail != null && carinfodetail.size() != 0){
            final ArrayList<String> images = new ArrayList<>();
            for (int i=0; i<carinfodetail.size(); i++){
                ProductCarInfo.CarsInfoBean.CarinfodetailBean bean = carinfodetail.get(i);
                images.add(i,bean.getImg_url());
            }
            for (ProductCarInfo.CarsInfoBean.CarinfodetailBean bean : carinfodetail) {
                final String mImg_url = bean.getImg_url();

                View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_car_info, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                ImageLoader.getInstance().displayImage(mImg_url, imageView);

                ((TextView) view.findViewById(R.id.textView)).setText(bean.getImg_name());

                view.findViewById(R.id.itemView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new PictureZoomHelper().showPicture(mContext,mImg_url,images);
                    }
                });
                mAudit.addView(view);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
