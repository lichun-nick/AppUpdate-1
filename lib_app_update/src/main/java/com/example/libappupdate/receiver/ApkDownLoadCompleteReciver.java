package com.example.libappupdate.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.libappupdate.util.UpdateUtil;


/**
 * Created by xingliuhua on 2016/4/26 0026.
 */
public class ApkDownLoadCompleteReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id != -1 && UpdateUtil.getUpDateDownLoadId(context) == id) {
                //安装
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Intent install = new Intent(Intent.ACTION_VIEW);
                Uri downloadFileUri = downloadManager.getUriForDownloadedFile(id);
                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);

            }
        }
    }
}
