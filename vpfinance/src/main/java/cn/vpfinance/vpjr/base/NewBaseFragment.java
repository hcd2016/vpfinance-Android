package cn.vpfinance.vpjr.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jewelcredit.util.Utils;
import com.tdk.utils.HttpDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 新basefragment
 */
public class NewBaseFragment extends Fragment implements HttpDownloader.HttpDownloaderListener{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {

    }

    @Override
    public void onHttpSuccess(int reqId, JSONArray json) {

    }

    @Override
    public void onHttpCache(int reqId) {

    }

    @Override
    public void onHttpError(int reqId, String errmsg) {

    }

    public void gotoWeb(String url, String title)
    {
        if (isAdded()) {
            Utils.goToWeb(getActivity(), url, title);
        }
    }
}
