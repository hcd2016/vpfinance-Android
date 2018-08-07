package com.jewelcredit.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;
import android.telephony.TelephonyManager;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.tdk.control.ProgressDialog;
import com.tdk.utils.HttpDownloader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import cn.vpfinance.vpjr.module.common.ServerDownActivity;

public class HttpLoader {

    private static String mSessionKey = "";
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private HttpDownloader.HttpDownloaderListener mListener;

    private String mTips = "加载中";

    public HttpLoader(Context context, HttpDownloader.HttpDownloaderListener listener) {
        mContext = context;
        mListener = listener;

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });

    }


    public void setTips(String tips) {
        mTips = tips;
    }


    private void onError(int reqId, String errmsg) {
        if (mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mListener.onHttpError(reqId, errmsg);
    }


    private void onSuccess(int reqId, JSONObject json) {
        if (mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (IllegalArgumentException e) {
            } catch (Exception e) {
            }
        }

        mListener.onHttpSuccess(reqId, json);
    }

    private void onSuccess(int reqId, JSONArray json) {
        if (mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (IllegalArgumentException e) {
            } catch (Exception e) {
            }
        }

        mListener.onHttpSuccess(reqId, json);
    }


    private boolean doHttpRequest(String url, String method, Map<String, String> params, int reqId, boolean islogin, boolean showProgress) {
        if (!Utils.isNetworkConnected(mContext)) {
            //mListener.onHttpSuccess(reqId, null);
            mListener.onHttpError(reqId, "网络不可用，请检查网络是否正常。");
            //触发加载缓存数据
            mListener.onHttpCache(reqId);
            return false;
        }

        if (showProgress && !mProgressDialog.isShowing()) {
            try {
                if (!((Activity) mContext).isFinishing()) {
                    mProgressDialog.show();
                    mProgressDialog.setTips(mTips);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (params == null) {
            params = new ArrayMap();
        }

        params.put("regChannel", "1");
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            String imei = telephonyManager.getDeviceId();
            params.put("clientId", imei);
        }

        if (method.equals("get")) {

//            FinanceApplication.getAppContext().requestInfoMap.put(reqId,new ServerDownRequestInfo(ServerDownRequestInfo.METHOD.GET,url,params));
            HttpDownloader.doGet(url + Utils.buildGetUrl(params, true), new HttpResponseHandler(reqId));
        } else {
//            FinanceApplication.getAppContext().requestInfoMap.put(reqId,new ServerDownRequestInfo(ServerDownRequestInfo.METHOD.POST,url,params));
//            url = "http://192.168.1.129:17190/AuthorizationApplication";
            HttpDownloader.doPost(url, params, new HttpResponseHandler(reqId));
        }
        return true;
    }

    public boolean doGet(String url, Map<String, String> params, int req, boolean islogin) {
        return doHttpRequest(url, "get", params, req, islogin, true);
    }

    public boolean doGet(String url, Map<String, String> params, int req, boolean islogin, boolean showProgress) {
        return doHttpRequest(url, "get", params, req, islogin, showProgress);
    }


    public boolean doPost(String url, Map<String, String> params, int req, boolean islogin) {
        return doHttpRequest(url, "post", params, req, islogin, true);
    }

    public boolean doPost(String url, Map<String, String> params, int req, boolean islogin, boolean showProgress) {
        return doHttpRequest(url, "post", params, req, islogin, showProgress);
    }

    public boolean doSoap(String url, Map<String, String> params, int req, boolean islogin, boolean showProgress) {
        return doPost(url, params, req, islogin, showProgress);
    }


    class HttpResponseHandler extends JsonHttpResponseHandler {
        private int req;

        public HttpResponseHandler(int req) {
            this.req = req;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            if (statusCode == 502) {//服务器维护状态
                ServerDownActivity.goThis(mContext);
            } else {
                HttpLoader.this.onError(this.req, "加载数据失败!");
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
            if (statusCode == 502) {//服务器维护状态
                ServerDownActivity.goThis(mContext);
            } else {
                HttpLoader.this.onError(req, "加载数据失败!");
            }
        }


        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (statusCode == 502) {//服务器维护状态
                ServerDownActivity.goThis(mContext);
            } else {
                HttpLoader.this.onSuccess(this.req, response);
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            if (statusCode == 502) {//服务器维护状态
                ServerDownActivity.goThis(mContext);
            } else {
                HttpLoader.this.onSuccess(this.req, response);
            }
        }
    }
}
