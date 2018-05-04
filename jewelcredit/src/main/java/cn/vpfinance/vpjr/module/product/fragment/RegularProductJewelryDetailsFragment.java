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
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.JewelryBean;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.PictureZoomHelper;
import cn.vpfinance.vpjr.view.TagCloudView;

/**
 */
public class RegularProductJewelryDetailsFragment extends BaseFragment {

    private static final String ARGS_PRODUCT_TYPE = "type";
    private static final String ARGS_PRODUCT_ID = "id";
    @Bind(R.id.carTag)
    TagCloudView mCarTag;
    @Bind(R.id.jewelry_name)
    TextView mJewelryName;
    @Bind(R.id.jewelry_num)
    TextView mJewelryNum;
    @Bind(R.id.jewelry_model)
    TextView mJewelryModel;
    @Bind(R.id.jewelry_assess)
    TextView mJewelryAssess;
    @Bind(R.id.facade_des)
    TextView mFacadeDes;
    @Bind(R.id.borrower)
    TextView mBorrower;
    @Bind(R.id.borrowMoney)
    TextView mBorrowMoney;
    @Bind(R.id.idCard)
    TextView mIdCard;
    @Bind(R.id.borrowTime)
    TextView mBorrowTime;
    @Bind(R.id.audit)
    LinearLayout mAudit;


    private HttpService mHttpService;
    private long pid;
    private int type;

    //    private View.OnClickListener clickImageListener =

    //    private String mImg_url;

    public static RegularProductJewelryDetailsFragment newInstance(long pid, int type) {
        RegularProductJewelryDetailsFragment frag = new RegularProductJewelryDetailsFragment();
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
        View view = View.inflate(getActivity(), R.layout.fragment_jewelry_details, null);
        ButterKnife.bind(this, view);

        if (pid != 0) {
            mHttpService.getProductCarInfo("" + pid);
        }
        return view;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_loanSignInfo.ordinal() && isAdded()) {
            JewelryBean jewelryBean = mHttpService.onGetProductJewelryInfo(json);
            setViewData(jewelryBean);
        }
    }

    private void setViewData(JewelryBean jewelryBean) {
        if (jewelryBean == null)
            return;

        List<String> tags = new ArrayList<>();

        final List<JewelryBean.JewelListEntity> jewelInfo = jewelryBean.getJewelList();
        if (jewelInfo != null && jewelInfo.size() != 0) {
            //初始化
            setjewelryData(jewelInfo.get(0));

            for (JewelryBean.JewelListEntity jewel : jewelInfo) {
                if (jewel.getJewelinfo() != null && !TextUtils.isEmpty(jewel.getJewelinfo().getTab_name())) {
                    tags.add(jewel.getJewelinfo().getTab_name());
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
                    if (jewelInfo.get(position) != null) {
                        setjewelryData(jewelInfo.get(position));
                    }
                }
            }
        });

    }

    private void setjewelryData(JewelryBean.JewelListEntity jewelInfo) {
        if (jewelInfo == null)
            return;
        JewelryBean.JewelListEntity.JewelinfoEntity info = jewelInfo.getJewelinfo();
        if (info != null) {
            try {
                mJewelryName.setText(info.getJewel_name());
                mJewelryNum.setText(info.getJewel_num());
                mJewelryModel.setText(info.getJewel_spec());
                mJewelryAssess.setText(info.getJudge_price());
                mFacadeDes.setText(info.getSurface_describe());
                mBorrower.setText(info.getPredebtor());
                mBorrowMoney.setText(info.getSumloanMoney());
                mIdCard.setText(info.getDebtor_cardid());
                mBorrowTime.setText(info.getLoanPeriod());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        List<JewelryBean.JewelListEntity.JewelinfodetailEntity> jewelinfodetail = jewelInfo.getJewelinfodetail();
        mAudit.removeAllViews();
        if (jewelinfodetail != null && jewelinfodetail.size() != 0) {
            final ArrayList<String> images = new ArrayList<>();
            for (int i=0; i<jewelinfodetail.size(); i++){
                JewelryBean.JewelListEntity.JewelinfodetailEntity entity = jewelinfodetail.get(i);
                images.add(i,entity.getImg_url());
            }
            for (JewelryBean.JewelListEntity.JewelinfodetailEntity bean : jewelinfodetail) {
                final String mImg_url = bean.getImg_url();

                View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_car_info, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                ImageLoader.getInstance().displayImage(mImg_url, imageView);

                ((TextView) view.findViewById(R.id.textView)).setText(bean.getImg_name());

                view.findViewById(R.id.itemView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new PictureZoomHelper().showPicture(mContext, mImg_url,images);
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
