package cn.vpfinance.vpjr.module.product.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.PersonalInfo;
import cn.vpfinance.vpjr.model.QualificationMaterial;
import de.greenrobot.event.EventBus;

public class RegularProductBorrowerQualificationMaterialFragment extends BaseFragment {

    private static final String ARGS_KEY_LOAN_ID = "loanId";

    private Context mContext = null;
    private HttpService mHttpService = null;
    private ItemBaseAdapter mItemBaseAdapter = null;
    private long mLoanId = 1;
    private static DisplayImageOptions mOptions;

    public static RegularProductBorrowerQualificationMaterialFragment newInstance(long loanId) {
        RegularProductBorrowerQualificationMaterialFragment fragment = new RegularProductBorrowerQualificationMaterialFragment();
        Bundle args = new Bundle();
        args.putLong(ARGS_KEY_LOAN_ID, loanId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mContext = activity;
        mHttpService = new HttpService(mContext, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle args = getArguments();
        if (args != null) {
            mLoanId = args.getLong(ARGS_KEY_LOAN_ID, 1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_borrower_regular_product_qualification_material, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.qualificationMaterialGridView);
        gridView.setOnItemClickListener(mItemClickListener);
        mItemBaseAdapter = new ItemBaseAdapter(mContext);
        gridView.setAdapter(mItemBaseAdapter);
//        mHttpService.getLoanSignAttr("" + mLoanId);
        return view;
    }

//    @Override
//    public void onHttpSuccess(int reqId, JSONObject json) {
//
//        if (reqId == ServiceCmd.CmdId.CMD_LOAN_SING_ATTR.ordinal()) {
//            if (json != null) {
//                ArrayList<QualificationMaterial> list = mHttpService.onGetLoanSignAttr(json);
//                mItemBaseAdapter.setDate(list);
//                mItemBaseAdapter.notifyDataSetChanged();
//            }
//        }
//    }

    public void onEventMainThread(PersonalInfo event) {
        if (event != null && isAdded()) {
            PersonalInfo.BorrowerAudit borrowerAudit = event.borrowerAudit;
            if (borrowerAudit != null)
            {
                List<PersonalInfo.Files> files = borrowerAudit.files;
                ArrayList<QualificationMaterial> list = new ArrayList<>();
                for (PersonalInfo.Files file : files) {
                    QualificationMaterial qualificationMaterial = new QualificationMaterial();
                    if (!TextUtils.isEmpty(file.fileName)){
                        qualificationMaterial.setFileName(file.fileName);
                    }
                    if (!TextUtils.isEmpty(file.auditStatus)){
                        qualificationMaterial.setFilePath(file.auditStatus);
                    }
                    list.add(qualificationMaterial);
                }
                mItemBaseAdapter.setDate(list);
                mItemBaseAdapter.notifyDataSetChanged();
            }
        }
    }

    private class ItemBaseAdapter extends BaseAdapter {

        private Context mContext = null;
        private LayoutInflater mLayoutInflater = null;
        private ArrayList<QualificationMaterial> mRiskControlList = null;

        public ItemBaseAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        public void setDate(ArrayList<QualificationMaterial> list) {
            mRiskControlList = list;
        }

        @Override
        public int getCount() {
            if (null != mRiskControlList) {
                return mRiskControlList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (null != mRiskControlList) {
                return mRiskControlList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.item_regular_product_qualification_material, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.pager_regular_product_qualification_material_imageview);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.pager_regular_product_qualification_material_textview);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String imageUrl = mRiskControlList.get(position).getFilePath();
            ImageLoader.getInstance().displayImage(imageUrl, viewHolder.imageView, getDisplayImgOptions());
            viewHolder.textView.setText(mRiskControlList.get(position).getFileName());

            convertView.setTag(viewHolder);
            return convertView;
        }

        public class ViewHolder {
            public ImageView imageView;
            public TextView textView;
        }
    }

    public static DisplayImageOptions getDisplayImgOptions()
    {
        if(mOptions != null)
        {
            return mOptions;
        }

        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_loading_vertical)
                .showImageForEmptyUri(R.drawable.img_load_error_vertical)
                .showImageOnFail(R.drawable.img_load_error_vertical)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        return mOptions;
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            QualificationMaterial qualificationMaterial = (QualificationMaterial)mItemBaseAdapter.getItem(position);
            String imageUrl = qualificationMaterial.getFilePath();
            ImageLoader.getInstance().loadImage(imageUrl, getDisplayImgOptions(), new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (null != bitmap) {

                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
