package com.example.libappupdate.util;

/**
 * Created by xingliuhua on 2016/6/30 0030.
 */
public class DownloadManagerOptations {
    private int mAllowedNetworkTypes = -1;
    private String mDescription;
    private String mTitle;
    private String apkUrl;
    private String newVersionCode;
    private boolean forceUpdate;

    public DownloadManagerOptations(String newVersionCode, boolean forceUpdate, String apkUrl) {
        this.newVersionCode = newVersionCode;
        this.forceUpdate = forceUpdate;
        this.apkUrl = apkUrl;
    }

    public DownloadManagerOptations setAllowedNetworkTypes(int allowedNetworkTypes) {
        mAllowedNetworkTypes = allowedNetworkTypes;
        return this;
    }


    public DownloadManagerOptations setDescription(String description) {
        mDescription = description;
        return this;
    }

    public DownloadManagerOptations setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public int getAllowedNetworkTypes() {
        return mAllowedNetworkTypes;
    }


    public String getDescription() {
        return mDescription;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public String getNewVersionCode() {
        return newVersionCode;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }
}
