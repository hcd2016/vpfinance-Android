package cn.vpfinance.vpjr.module.product.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.module.common.LoginActivity;
import cn.vpfinance.vpjr.gson.NewWritingAndPicBean;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.PictureZoomHelper;

/**
 * Created by Administrator on 2016/10/24.
 * 标的详情  文字+图片fragment  对应datatype 为3
 */
public class NewWritingAndPicFragment extends BaseFragment {

    private static final String PRODUCT_ID = "loanId";
    //    private static final String SHOW_TYPE = "showType";
    private static final String NET_URL    = "netUrl";
    @Bind(R.id.list_view)
    ListView       mListView;
    @Bind(R.id.lookOtherProduct)
    Button         mLookOtherProduct;
    @Bind(R.id.rl_show_login)
    RelativeLayout mRlShowLogin;
    private long        mLoanId;
    private String      mShowType;
    private String      mNetUrl;
    private HttpService mHttpService;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHttpService = new HttpService(mContext, this);
        Bundle args = getArguments();
        if (args != null) {
            //            mShowType = args.getString(SHOW_TYPE);
            mNetUrl = args.getString(NET_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_writingandpic_fragment, container, false);
        ButterKnife.bind(this, view);
        mHttpService.getRegularTab(mNetUrl);
        return view;
    }

    @Override
    public void onResume() {
        if (AppState.instance().logined()) {
            mRlShowLogin.setVisibility(View.GONE);
        } else {
            mRlShowLogin.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (reqId == ServiceCmd.CmdId.CMD_Regular_Tab.ordinal()) {
            Gson gson = new Gson();
            try {
                NewWritingAndPicBean newWritingAndPicBean = gson.fromJson(json.toString(), NewWritingAndPicBean.class);
                //                Logger.e(newWritingAndPicBean.toString());
                if (newWritingAndPicBean == null || (newWritingAndPicBean.data == null))
                    return;

                if (newWritingAndPicBean.dataType == 3) {//datatype为3的数据格式（文字+图片，如融租宝的融租详情）
                    ListViewAdapter adapter = new ListViewAdapter(mContext);
                    mListView.setAdapter(adapter);
                    adapter.setData(newWritingAndPicBean.data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.lookOtherProduct)
    public void onClick() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
    }

    private class ListViewAdapter extends BaseAdapter {

        private Context mContext;
        /**
         * 注意一定要从0开始定义
         */
        private static final int TEXT_TYPE         = 0;//文字类型
        private static final int SINGLE_IMAGE_TYPE = 1;//单个图片
        private static final int MANY_IMAGE_TYPE   = 2;//多组图片

        private List<NewWritingAndPicBean.DataBean> data;

        public ListViewAdapter(Context context) {
            mContext = context;
        }

        public void setData(List<NewWritingAndPicBean.DataBean> data) {
            if (data == null)
                return;
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public NewWritingAndPicBean.DataBean getItem(int position) {
            return data == null ? null : data.get(position);
        }

        /**
         * 告诉有几个type,也就是需要几个convertView
         *
         * @return
         */
        @Override
        public int getViewTypeCount() {
            return 3;
        }

        /**
         * 当前type是什么
         *
         * @param position
         * @return
         */
        @Override
        public int getItemViewType(int position) {
            if (data != null) {
                switch (data.get(position).type) {//这是服务器返回的type
                    case 1:
                        return TEXT_TYPE;
                    case 2:
                        return SINGLE_IMAGE_TYPE;
                    case 3:
                        return MANY_IMAGE_TYPE;
                }
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            NewWritingAndPicBean.DataBean bean = data.get(position);
            WritingViewHolder writingViewHolder;
            SinglePicViewHolder singlePicViewHolder;
            ManyPicViewHolder manyPicViewHolder;
            if (type == TEXT_TYPE) {//文字
                if (null == convertView) {
                    writingViewHolder = new WritingViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_new_writing, null);
                    writingViewHolder.mTitle = (TextView) convertView.findViewById(R.id.title);
                    writingViewHolder.mTextView = (TextView) convertView.findViewById(R.id.text_view);
                    convertView.setTag(writingViewHolder);
                } else {
                    writingViewHolder = (WritingViewHolder) convertView.getTag();
                }
                if (bean != null) {
                    writingViewHolder.mTitle.setText(bean.key);
                    writingViewHolder.mTextView.setText("\u3000\u3000" + bean.value);
                }
            } else if (type == SINGLE_IMAGE_TYPE) {//图片

                if (null == convertView) {
                    singlePicViewHolder = new SinglePicViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_new_pic, null);
                    singlePicViewHolder.mTitle = (TextView) convertView.findViewById(R.id.title);
                    singlePicViewHolder.mImageView = (ImageView) convertView.findViewById(R.id.image_view);
                    convertView.setTag(singlePicViewHolder);
                } else {
                    singlePicViewHolder = (SinglePicViewHolder) convertView.getTag();
                }

                if (bean != null) {
                    singlePicViewHolder.mTitle.setText(bean.key == null ? "" : bean.key);
                    final String imageUrl = bean.value;
                    final PictureZoomHelper pictureZoomHelper = new PictureZoomHelper();
                    if (imageUrl != null) {
                        singlePicViewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pictureZoomHelper.showPicture(mContext, imageUrl,null);
                            }
                        });
                        ImageLoader.getInstance().displayImage(imageUrl, singlePicViewHolder.mImageView, pictureZoomHelper.getDisplayImgOptions());
                    }
                }
            } else if (type == MANY_IMAGE_TYPE) {//多个图片

                if (null == convertView) {
                    manyPicViewHolder = new ManyPicViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_new_many_pic, null);
                    manyPicViewHolder.mTitle = (TextView) convertView.findViewById(R.id.title);
                    manyPicViewHolder.mContent = (LinearLayout) convertView.findViewById(R.id.content);
                    convertView.setTag(manyPicViewHolder);
                } else {
                    manyPicViewHolder = (ManyPicViewHolder) convertView.getTag();
                }
                if (bean != null) {
                    manyPicViewHolder.mTitle.setText(bean.key == null ? "" : bean.key);
                    List<NewWritingAndPicBean.FileDataBean> fileData = bean.fileData;
                    manyPicViewHolder.mContent.removeAllViews();

                    if (fileData != null && fileData.size() != 0) {
                        final ArrayList<String> images = new ArrayList<>();
                        for (int i=0; i<fileData.size(); i++) {
                            NewWritingAndPicBean.FileDataBean fileDataBean = fileData.get(i);
                            if (fileDataBean.type == 2) {
                                String imageUrl = fileDataBean.value;
                                images.add(imageUrl);
                            }
                        }
                        for (NewWritingAndPicBean.FileDataBean fileDataBean : fileData) {
                            if (fileDataBean.type == 2) {
                                final String imageUrl = fileDataBean.value;
                                String title = fileDataBean.key;

                                View view = LayoutInflater.from(mContext).inflate(R.layout.item_pic_info, null);
                                ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                                ImageLoader.getInstance().displayImage(imageUrl, imageView);

                                ((TextView) view.findViewById(R.id.textView)).setText(title);

                                view.findViewById(R.id.itemView).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new PictureZoomHelper().showPicture(mContext, imageUrl,images);
                                    }
                                });
                                manyPicViewHolder.mContent.addView(view);
                            }
                        }
                    }
                }
                convertView.setTag(manyPicViewHolder);
            }
            return convertView;
        }

        public class WritingViewHolder {
            public TextView mTitle;
            public TextView mTextView;
        }

        public class SinglePicViewHolder {
            public TextView  mTitle;
            public ImageView mImageView;
        }

        public class ManyPicViewHolder {
            public TextView     mTitle;
            public LinearLayout mContent;
        }
    }

    public static NewWritingAndPicFragment newInstance(String url) {
        NewWritingAndPicFragment fragment = new NewWritingAndPicFragment();
        Bundle args = new Bundle();
        //        args.putString(SHOW_TYPE, showType);
        args.putString(NET_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
