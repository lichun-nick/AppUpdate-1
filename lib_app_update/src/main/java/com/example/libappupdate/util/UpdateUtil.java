package com.example.libappupdate.util;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.libappupdate.fragment.DownloadApkProgressDialogFragment;


/**
 * Created by xingliuhua on 2016/4/26 0026.
 */
public class UpdateUtil {
    private static final String KEY_DOWN_LOAD_ID = "keyDownLoadId";
    private static final String KEY_DOWN_LOAD_NEW_VERSIONCODE = "keyDownLoadNewVersion";

    public static long getUpDateDownLoadId(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getLong(KEY_DOWN_LOAD_ID, -1);
    }

    public static void setUpDateDownLoadId(Context context, long downLoadId) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(KEY_DOWN_LOAD_ID, downLoadId).commit();
    }

    private static String getUpDateDownLoadNewVersion(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getString(KEY_DOWN_LOAD_NEW_VERSIONCODE, "");
    }

    private static void setUpDateDownLoadNewVersion(Context context, String newVersion) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_DOWN_LOAD_NEW_VERSIONCODE, newVersion).commit();
    }

    private static boolean apkHadDownLoaded(Context context, int newVersionCode) {
        long downLoadId = getUpDateDownLoadId(context.getApplicationContext());
        if (downLoadId == -1) {
            return false;
        }
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downLoadId);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor c = downloadManager.query(query);
        if (c != null && c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            return status == DownloadManager.STATUS_SUCCESSFUL && getUpDateDownLoadNewVersion(context).equals(newVersionCode);
        } else {
            return false;
        }
    }


    public static void update(Activity activity, DownloadManagerOptations downloadManagerOptations) {
        if (activity == null || downloadManagerOptations == null || TextUtils.isEmpty(downloadManagerOptations.getApkUrl())) {
            return;
        }
        long downLoadId = getUpDateDownLoadId(activity.getApplicationContext());

        //检查下载状态
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downLoadId);
        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor c = downloadManager.query(query);
        if (c != null && c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_SUCCESSFUL:
                    //已经下载完成
                    if (getUpDateDownLoadNewVersion(activity).equals(downloadManagerOptations.getNewVersionCode())) {
                        //直接安装
                        installApk(activity, downloadManager, downLoadId);
                    } else {
                        //版本不一致
                        //重置id,可以重新下载
                        setUpDateDownLoadId(activity, -1);
                        newDownLoadTask(activity, downloadManagerOptations);
                    }
                    break;
                case DownloadManager.STATUS_FAILED:
                    //重置id,可以重新下载
                    setUpDateDownLoadId(activity, -1);
                    newDownLoadTask(activity, downloadManagerOptations);
                    break;
                default:
                    if (downloadManagerOptations.isForceUpdate()) {
                        DownloadApkProgressDialogFragment downloadApkProgressDialogFragment = DownloadApkProgressDialogFragment.newInstance(downloadManagerOptations.isForceUpdate(), downloadManagerOptations.getTitle(), downloadManagerOptations.getDescription());
                        downloadApkProgressDialogFragment.show(activity.getFragmentManager(), "updateApk");
                    }
                    break;
            }
        } else {
            //重置id,可以重新下载
            setUpDateDownLoadId(activity, -1);
            newDownLoadTask(activity, downloadManagerOptations);
        }
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void installApk(Activity activity, DownloadManager downloadManager, long downLoadId) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        Uri downloadFileUri = downloadManager.getUriForDownloadedFile(downLoadId);
        install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(install);
    }


    /**
     * 开启一个新的下载任务
     *
     * @param activity
     * @param downloadManagerOptations
     * @return
     */
    private static boolean newDownLoadTask(Activity activity, DownloadManagerOptations downloadManagerOptations) {
        if (activity == null || TextUtils.isEmpty(downloadManagerOptations.getApkUrl())) {
            return false;
        }
        if (-1 == (getUpDateDownLoadId(activity))) {
            DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(downloadManagerOptations.getApkUrl());
            DownloadManager.Request request = new DownloadManager.Request(uri);
            if (downloadManagerOptations.getAllowedNetworkTypes() != -1) {
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            }
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
            request.allowScanningByMediaScanner();
            if (!TextUtils.isEmpty(downloadManagerOptations.getTitle())) {
                request.setTitle("正在更新");
            }
            if (!TextUtils.isEmpty(downloadManagerOptations.getDescription())) {
                request.setDescription("正在更新");
            }
            request.setVisibleInDownloadsUi(true);
            try {
                request.setDestinationInExternalFilesDir(activity, Environment.DIRECTORY_DOWNLOADS, "newVersion.apk");
            } catch (Exception e) {
                e.printStackTrace();
            }
            long reference = downloadManager.enqueue(request);
            setUpDateDownLoadId(activity, reference);
            setUpDateDownLoadNewVersion(activity, downloadManagerOptations.getNewVersionCode());
            DownloadApkProgressDialogFragment downloadApkProgressDialogFragment = DownloadApkProgressDialogFragment.newInstance(downloadManagerOptations.isForceUpdate(), downloadManagerOptations.getTitle(), downloadManagerOptations.getDescription());
            downloadApkProgressDialogFragment.show(activity.getFragmentManager(), "updateApk");

            return true;
        } else {
            return false;
        }
    }
}
