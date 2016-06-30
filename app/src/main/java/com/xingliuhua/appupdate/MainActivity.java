package com.xingliuhua.appupdate;

import android.app.Activity;
import android.app.DownloadManager;
import android.os.Bundle;
import android.view.View;

import com.example.libappupdate.util.DownloadManagerOptations;
import com.example.libappupdate.util.UpdateUtil;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apkUrl = "http://www.lanxiniu.com/Statistic/addRecord?op=web_index_android_down&url=%2FUpdateApk%2Fapp-lanxiniu_pc-release.apk";

                DownloadManagerOptations downloadManagerOptations = new DownloadManagerOptations("1", false, apkUrl)
                        .setTitle("正在更新")
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                        .setDescription("正在努力更新");

                UpdateUtil.update(MainActivity.this, downloadManagerOptations);
            }
        });

        findViewById(R.id.btn_forcedUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apkUrl = "http://www.lanxiniu.com/Statistic/addRecord?op=web_index_android_down&url=%2FUpdateApk%2Fapp-lanxiniu_pc-release.apk";
                DownloadManagerOptations downloadManagerOptations = new DownloadManagerOptations("1", true, apkUrl);

                UpdateUtil.update(MainActivity.this, downloadManagerOptations);
            }
        });
    }
}
