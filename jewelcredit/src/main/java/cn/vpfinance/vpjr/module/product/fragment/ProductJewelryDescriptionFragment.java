package cn.vpfinance.vpjr.module.product.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.JewelryBean;
import cn.vpfinance.vpjr.model.JewelryContentListEvent;
import cn.vpfinance.vpjr.util.PictureZoomHelper;
import de.greenrobot.event.EventBus;

public class ProductJewelryDescriptionFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.description)
    ImageView mDescription;
    @Bind(R.id.yidianTitle)
    TextView mYidianTitle;
    @Bind(R.id.yidianContent)
    TextView mYidianContent;
    @Bind(R.id.yidian_1)
    ImageView mYidian1;
    @Bind(R.id.yidian_2)
    ImageView mYidian2;
    @Bind(R.id.insuranceTitle)
    TextView mInsuranceTitle;
    @Bind(R.id.insuranceContent)
    TextView mInsuranceContent;
    @Bind(R.id.insurance_1)
    ImageView mInsurance1;
    @Bind(R.id.insurance_2)
    ImageView mInsurance2;
    @Bind(R.id.repurchaseTitle)
    TextView mRepurchaseTitle;
    @Bind(R.id.repurchaseContent)
    TextView mRepurchaseContent;
    @Bind(R.id.repurchase_1)
    ImageView mRepurchase1;
    @Bind(R.id.repurchase_2)
    ImageView mRepurchase2;
    @Bind(R.id.appraisalTitle)
    TextView mAppraisalTitle;
    @Bind(R.id.appraisalContent)
    TextView mAppraisalContent;
    @Bind(R.id.appraisal_1)
    ImageView mAppraisal1;
    @Bind(R.id.appraisal_2)
    ImageView mAppraisal2;

    private HttpService mHttpService = null;
    private long pid;
    private int type;

    private static final String ARGS_PRODUCT_TYPE = "type";
    private static final String ARGS_PRODUCT_ID = "id";


    public static ProductJewelryDescriptionFragment newInstance(long pid, int type) {
        ProductJewelryDescriptionFragment frag = new ProductJewelryDescriptionFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_PRODUCT_TYPE, type);
        args.putLong(ARGS_PRODUCT_ID, pid);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle args = getArguments();
        if (args != null) {
            pid = args.getLong(ARGS_PRODUCT_ID);
            type = args.getInt(ARGS_PRODUCT_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mHttpService = new HttpService(getActivity(), this);

        View mContentView = inflater.inflate(R.layout.fragment_product_jewelry_description, null);
        ButterKnife.bind(this, mContentView);
        mDescription.setOnClickListener(this);
        return mContentView;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        super.onHttpSuccess(reqId,json);
    }

    public void onEventMainThread(JewelryContentListEvent bean) {
        if (isAdded()) {
            List<JewelryBean.ContentListEntity> event = bean.list;
            try {
                JewelryBean.ContentListEntity entity = event.get(0);
                setView(mYidianTitle, mYidianContent, mYidian1, mYidian2, entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JewelryBean.ContentListEntity entity = event.get(1);
                setView(mInsuranceTitle, mInsuranceContent, mInsurance1, mInsurance2, entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JewelryBean.ContentListEntity entity = event.get(2);
                setView(mRepurchaseTitle, mRepurchaseContent, mRepurchase1, mRepurchase2, entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JewelryBean.ContentListEntity entity = event.get(3);
                setView(mAppraisalTitle, mAppraisalContent, mAppraisal1, mAppraisal2, entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    private PictureZoomHelper pictureZoomHelper;
    private void setView(TextView tv1, TextView tv2, final ImageView iv1, final ImageView iv2, JewelryBean.ContentListEntity entity) {
        try {
            String title = TextUtils.isEmpty(entity.getTitle()) ? "" : entity.getTitle();
            String content = TextUtils.isEmpty(entity.getContent()) ? "" : entity.getContent();
            tv1.setText(title);
            tv2.setText(content);

            List<JewelryBean.ContentListEntity.ImgUrlsEntity> imgUrls = entity.getImgUrls();
            pictureZoomHelper = new PictureZoomHelper();
            if (imgUrls != null) {
                if (imgUrls.size() == 1) {
                    final String url = HttpService.mBaseUrl + imgUrls.get(0).getImgUrl();
                    iv2.setVisibility(View.GONE);
                    iv1.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(url, iv1);
                    iv1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pictureZoomHelper.showPicture(mContext, url,null);
                        }
                    });
                } else if (imgUrls.size() == 2) {
                    iv2.setVisibility(View.VISIBLE);
                    iv1.setVisibility(View.VISIBLE);
                    final String url1 = HttpService.mBaseUrl + imgUrls.get(0).getImgUrl();
                    final String url2 = HttpService.mBaseUrl + imgUrls.get(1).getImgUrl();
                    ImageLoader.getInstance().displayImage(url1, iv1);
                    ImageLoader.getInstance().displayImage(url2, iv2);
                    final ArrayList<String> images = new ArrayList<>();
                    images.add(0,url1);
                    images.add(1,url2);
                    iv1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pictureZoomHelper.showPicture(mContext, url1,images);
                        }
                    });
                    iv2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pictureZoomHelper.showPicture(mContext, url2,images);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.description:
                pictureZoomHelper.showPicture(mContext, mDescription.getDrawable());
                break;
            case R.id.yidian_1:
                pictureZoomHelper.showPicture(mContext, mYidian1.getDrawable());
                break;
            case R.id.yidian_2:
                pictureZoomHelper.showPicture(mContext, mYidian2.getDrawable());
                break;
            case R.id.insurance_1:
                pictureZoomHelper.showPicture(mContext, mInsurance1.getDrawable());
                break;
            case R.id.insurance_2:
                pictureZoomHelper.showPicture(mContext, mInsurance2.getDrawable());
                break;
            case R.id.repurchase_1:
                pictureZoomHelper.showPicture(mContext, mRepurchase1.getDrawable());
                break;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
