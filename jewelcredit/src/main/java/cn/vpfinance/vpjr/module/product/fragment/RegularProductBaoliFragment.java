package cn.vpfinance.vpjr.module.product.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.FinanceProduct;
import cn.vpfinance.vpjr.model.Baoli;
import cn.vpfinance.vpjr.model.BaoliItem;
import cn.vpfinance.vpjr.model.CarInfo;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.PictureZoomHelper;

/**
 * 保理信息
 */
public class RegularProductBaoliFragment extends BaseFragment {

    private static final String ARGS_KEY_LOAN_ID = "loanId";
    private Context mContext = null;
    private HttpService mHttpService = null;
    private ItemBaseAdapter mItemBaseAdapter = null;
    private long mLoanId = 1;

    public static RegularProductBaoliFragment newInstance(long loanId){
        RegularProductBaoliFragment fragment = new RegularProductBaoliFragment();
        Bundle args = new Bundle();
        args.putLong(ARGS_KEY_LOAN_ID, loanId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
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
        View view = inflater.inflate(R.layout.pager_regular_product_baoli, container, false);
        ListView listView = (ListView)view.findViewById(R.id.baoliListView);
        if(isAdded())
        {
            mContext = getActivity();
        }
        mItemBaseAdapter = new ItemBaseAdapter(mContext);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mItemBaseAdapter!=null)
                {
                    Item item = (Item)mItemBaseAdapter.getItem(position);
                    if(item!=null)
                    {

                    }
                }
            }
        });
        listView.setAdapter(mItemBaseAdapter);
        if(mHttpService==null)
        {
            mHttpService = new HttpService(mContext, this);
        }
        mHttpService.getBaoliLoansignDesc("" + mLoanId);
        return view;
    }

    public void onEventMainThread(FinanceProduct event) {
        if (event != null && isAdded()) {
            long product = event.getProduct();

        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if(reqId == ServiceCmd.CmdId.CMD_baoliLoansignDesc.ordinal() && isAdded())
        {
            if (json != null) {
                Baoli baoli = mHttpService.onGetBaoli(json);
                List<BaoliItem> list = baoli.files;
                ArrayList<Item> iList = new ArrayList<Item>();
                if(list!=null && list.size()>0)
                {
                    for(BaoliItem bl:list)
                    {
                        if(bl.getCtype()!=5)
                        {
                            continue;
                        }
                        List<BaoliItem.File> files = bl.getFiles();
                        Item item = null;
                        if(files!=null && files.size()>0)
                        {
                            item = new Item();
                            item.type = 1;
                            item.name = bl.getName();
                            item.content = null;
                            //iList.add(item);
                            for(BaoliItem.File f:files)
                            {
                                item = new Item();
                                item.type = 2;
                                item.name = f.getFileName();
                                item.content = f.getFilePath();
                                iList.add(item);
                            }
                        }
                        else
                        {
                            item = new Item();
                            item.type = 0;
                            item.name = bl.getName();
                            item.content = bl.getContent();
                            iList.add(item);
                        }

                    }
                }

                mItemBaseAdapter.setDate(iList);
                mItemBaseAdapter.notifyDataSetChanged();
            }
        }
    }

    public class Item{
        private String content;
        private String name;
        private int type;//0: string 1:null 2: image url 3
        private CarInfo car;
    }

    public class ItemBaseAdapter extends BaseAdapter {

        private Context mContext = null;
        private LayoutInflater mLayoutInflater = null;
        private List<Item> mProjectIntroductionList = null;

        DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_loading_vertical)
        .showImageForEmptyUri(R.drawable.img_load_error_vertical)
        .showImageOnFail(R.drawable.img_load_error_vertical)
        .cacheInMemory(false)
        .cacheOnDisk(true)
        .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .build();

        public ItemBaseAdapter(Context context){
            mContext = context;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        public void setDate(List<Item> list){
            mProjectIntroductionList = list;
        }

        @Override
        public int getCount() {
            if(null != mProjectIntroductionList){
                return mProjectIntroductionList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if(null != mProjectIntroductionList){
                return mProjectIntroductionList.get(position);
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
            if(null == convertView) {
                viewHolder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.item_regular_product_baoli_item, null);
                viewHolder.titlTextView = (TextView)convertView.findViewById(R.id.item_regular_product_project_introduction_title);
                viewHolder.contentTextView = (TextView)convertView.findViewById(R.id.item_regular_product_project_introduction_content);
                viewHolder.imageView = (ImageView)convertView.findViewById(R.id.imageView);
                viewHolder.divider = convertView.findViewById(R.id.divider);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            final Item item = mProjectIntroductionList.get(position);
            viewHolder.titlTextView.setText(item.name);
            if(item.type==0)
            {
                viewHolder.imageView.setVisibility(View.GONE);
                viewHolder.contentTextView.setVisibility(View.VISIBLE);
                viewHolder.contentTextView.setText(item.content);
                viewHolder.divider.setVisibility(View.VISIBLE);
            }
            if(item.type==1)
            {
                viewHolder.imageView.setVisibility(View.GONE);
                viewHolder.contentTextView.setVisibility(View.GONE);
                viewHolder.divider.setVisibility(View.INVISIBLE);
            }
            else if(item.type==2)
            {
                viewHolder.imageView.setVisibility(View.VISIBLE);
                viewHolder.contentTextView.setVisibility(View.GONE);
                viewHolder.divider.setVisibility(View.VISIBLE);
                final String uri = item.content;
                ImageLoader.getInstance().displayImage(uri, viewHolder.imageView, mOptions);
                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new PictureZoomHelper().showPicture(mContext,uri,null);
                    }
                });
            }
            else if(item.type==3)
            {
                viewHolder.imageView.setVisibility(View.GONE);
                viewHolder.contentTextView.setVisibility(View.VISIBLE);
                viewHolder.contentTextView.setText(item.content);
                viewHolder.divider.setVisibility(View.VISIBLE);
            }

            convertView.setTag(viewHolder);
            return convertView;
        }

        public class ViewHolder{
            public TextView titlTextView;
            public TextView contentTextView;
            public ImageView imageView;
            public View divider;
        }
    }
}
