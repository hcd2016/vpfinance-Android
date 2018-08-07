package cn.vpfinance.vpjr.module.product.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.model.QualificationMaterial;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.PictureZoomHelper;

public class RegularProductQualificationMaterialFragment extends BaseFragment {

    private static final String ARGS_KEY_LOAN_ID = "loanId";

    private Context mContext = null;
    private HttpService mHttpService = null;
    private ItemBaseAdapter mItemBaseAdapter = null;
    private long mLoanId = 1;

    public static RegularProductQualificationMaterialFragment newInstance(long loanId) {
        RegularProductQualificationMaterialFragment fragment = new RegularProductQualificationMaterialFragment();
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

        Bundle args = getArguments();
        if (args != null) {
            mLoanId = args.getLong(ARGS_KEY_LOAN_ID, 1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_regular_product_qualification_material, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.qualificationMaterialGridView);
        gridView.setOnItemClickListener(mItemClickListener);
        mItemBaseAdapter = new ItemBaseAdapter(mContext);
        gridView.setAdapter(mItemBaseAdapter);
        mHttpService.getLoanSignAttr("" + mLoanId);
        return view;
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_LOAN_SING_ATTR.ordinal() && isAdded()) {
            if (json != null) {
                ArrayList<QualificationMaterial> list = mHttpService.onGetLoanSignAttr(json);
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
            ImageLoader.getInstance().displayImage(imageUrl, viewHolder.imageView, new PictureZoomHelper().getDisplayImgOptions());
            viewHolder.textView.setText(mRiskControlList.get(position).getFileName());

            convertView.setTag(viewHolder);
            return convertView;
        }

        public class ViewHolder {
            public ImageView imageView;
            public TextView textView;
        }
    }



    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            QualificationMaterial qualificationMaterial = (QualificationMaterial)mItemBaseAdapter.getItem(position);
            String imageUrl = qualificationMaterial.getFilePath();
            new PictureZoomHelper().showPicture(mContext,imageUrl,null);
        }
    };
}
