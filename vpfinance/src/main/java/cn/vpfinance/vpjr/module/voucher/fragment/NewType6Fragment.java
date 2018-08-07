package cn.vpfinance.vpjr.module.voucher.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jewelcredit.util.HttpService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseFragment;
import cn.vpfinance.vpjr.gson.NewType6Bean;

/**
 * Created by zzlz13 on 2017/11/6.
 */

public class NewType6Fragment extends BaseFragment{

    private HttpService mHttpService;
    private String mRequestUrl;
    private RecyclerView basicInfo;
    private RecyclerView certificateInfo;
    private TextView basicInfoText;
    private TextView certificateInfoText;

    public static NewType6Fragment newInstance(String url) {
        NewType6Fragment fragment = new NewType6Fragment();
        Bundle args = new Bundle();
        args.putString("url",url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_new_type_6,container,false);
        mHttpService = new HttpService(getActivity(), this);
        mRequestUrl = getArguments().getString("url");
        mHttpService.getRegularTab(mRequestUrl);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        basicInfo = ((RecyclerView) view.findViewById(R.id.basicInfo));
        basicInfoText = ((TextView) view.findViewById(R.id.basicInfoText));
        certificateInfo = ((RecyclerView) view.findViewById(R.id.certificateInfo));
        certificateInfoText = ((TextView) view.findViewById(R.id.certificateInfoText));
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;
        if (isAdded() || json != null){
            NewType6Bean bean = new Gson().fromJson(json.toString(), NewType6Bean.class);
            if (bean.dataType != 6) return;
            basicInfoText.setText(bean.infos.title);
            certificateInfoText.setText(bean.credits.title);
            basicInfo.setHasFixedSize(false);
            basicInfo.setLayoutManager(new LinearLayoutManager(getActivity()));
            basicInfo.setAdapter(new BasicInfoAdapter(bean.infos.key,bean.infos.value));
            certificateInfo.setHasFixedSize(false);
            certificateInfo.setLayoutManager(new GridLayoutManager(getActivity(),4));
            certificateInfo.setAdapter(new CertificateInfoAdapter(bean.credits.value,bean.credits.key));
        }
    }
    class CertificateInfoAdapter extends RecyclerView.Adapter<CertificateViewHolder>{
        private ArrayList<String> icons;
        private ArrayList<String> values;

        public CertificateInfoAdapter(ArrayList<String> icons, ArrayList<String> values) {
            if (icons == null){
                this.icons = new ArrayList<>();
            }else{
                this.icons = icons;
            }
            if (values == null){
                this.values = new ArrayList<>();
            }else{
                this.values = values;
            }
        }

        @Override
        public CertificateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CertificateViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_new_type_6_certificate,parent,false));
        }

        @Override
        public void onBindViewHolder(CertificateViewHolder holder, int position) {
            String icon = HttpService.mBaseUrl + icons.get(position) + "1.png";
            Glide.with(getActivity()).load(icon).into(holder.ivIcon);
            String value = values.get(position);
            holder.tvValue.setText(value);
        }

        @Override
        public int getItemCount() {
            return icons.size() > values.size() ? values.size() : icons.size();
        }
    }

    class BasicInfoAdapter extends RecyclerView.Adapter<BasicViewHolder>{
        private ArrayList<String> keys;
        private ArrayList<String> values;

        public BasicInfoAdapter(ArrayList<String> keys, ArrayList<String> values) {
            if (keys == null){
                this.keys = new ArrayList<>();
            }else{
                this.keys = keys;
            }
            if (values == null){
                this.values = new ArrayList<>();
            }else{
                this.values = values;
            }
        }

        @Override
        public BasicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BasicViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_new_type_6_basic,parent,false));
        }

        @Override
        public void onBindViewHolder(BasicViewHolder holder, int position) {
            String key = keys.get(position);
            String value = values.get(position);
            holder.tvKey.setText(key);
            holder.tvValue.setText(value);
        }

        @Override
        public int getItemCount() {
            return keys.size() > values.size() ? values.size() : keys.size();
        }
    }

    private static class BasicViewHolder extends RecyclerView.ViewHolder{
        TextView tvKey;
        TextView tvValue;
        public BasicViewHolder(View itemView) {
            super(itemView);
            tvKey = ((TextView) itemView.findViewById(R.id.tvKey));
            tvValue = ((TextView) itemView.findViewById(R.id.tvValue));
        }
    }
    private static class CertificateViewHolder extends RecyclerView.ViewHolder{
        ImageView ivIcon;
        TextView tvValue;
        public CertificateViewHolder(View itemView) {
            super(itemView);
            ivIcon = ((ImageView) itemView.findViewById(R.id.ivIcon));
            tvValue = ((TextView) itemView.findViewById(R.id.tvValue));
        }
    }
}
