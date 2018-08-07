package com.jewelcredit.model;

/**
 * Created by Administrator on 2015/7/25.
 */
public class AppUpdateInfo {
    public String appVersion;
    public String downloadUrl;
    public String updateLog;

    @Override
    public String toString() {
        return "AppUpdateInfo{" +
                "appVersion='" + appVersion + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", updateLog='" + updateLog + '\'' +
                '}';
    }
}
