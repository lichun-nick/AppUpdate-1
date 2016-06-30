package com.example.libappupdate.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.libappupdate.R;
import com.example.libappupdate.util.UpdateUtil;

import java.text.DecimalFormat;

/**
 * Created by xingliuhua on 2016/4/26 0026.
 */
public class DownloadApkProgressDialogFragment extends DialogFragment {
    private long downLoadId = -1;
    private final int MSG_UPDATE_PROGRESS = 0x1;

    public static DownloadApkProgressDialogFragment newInstance(boolean forceUpdate, String title, String description) {
        DownloadApkProgressDialogFragment downloadApkProgressDialogFragment = new DownloadApkProgressDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("forceUpdate", forceUpdate);
        args.putString("title", title);
        args.putString("description", description);
        downloadApkProgressDialogFragment.setArguments(args);
        return downloadApkProgressDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean forceUpdate = getArguments().getBoolean("forceUpdate");
        setCancelable(!forceUpdate);
        //判断是否
        downLoadId = UpdateUtil.getUpDateDownLoadId(this.getActivity().getApplicationContext());
        if (downLoadId != -1) {
            queryProgress();
        } else {
            dismiss();
        }
    }

    private TextView mTvProgeress;
    private ProgressBar mPb;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String description = getArguments().getString("description");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        View conentView = View.inflate(getActivity(), R.layout.dialog_download_apk, null);
        TextView tvDescription = (TextView) conentView.findViewById(R.id.tv_description);
        if (!TextUtils.isEmpty(description)) {
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(description);

        }
        mPb = (ProgressBar) conentView.findViewById(R.id.pb);
        mTvProgeress = (TextView) conentView.findViewById(R.id.tv_progress);
        builder.setView(conentView);
        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_UPDATE_PROGRESS);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_PROGRESS:
                    DownLoadApkInfo downLoadApkInfo = (DownLoadApkInfo) msg.obj;
                    if (downLoadApkInfo != null) {
                        mPb.setProgress(downLoadApkInfo.sizeHadDownLoaded);
                        mPb.setMax(downLoadApkInfo.sizeTotal);
                        DecimalFormat decimalFormat = new DecimalFormat("0.0%");
                        String format = decimalFormat.format(downLoadApkInfo.sizeHadDownLoaded / (float) downLoadApkInfo.sizeTotal);
                        mTvProgeress.setText(format);
                        queryProgress();
                    }
                    break;
            }

        }
    };

    private void queryProgress() {
        new Thread() {
            @Override
            public void run() {
                DownLoadApkInfo downLoadApkInfo = queryDownloadStatus();
                Message msg = new Message();
                msg.obj = downLoadApkInfo;
                msg.what = MSG_UPDATE_PROGRESS;
                mHandler.sendMessageDelayed(msg, 500);
            }
        }.start();
    }

    private DownLoadApkInfo queryDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downLoadId);
        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor c = downloadManager.query(query);
        if (c != null && c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_FAILED:
                    UpdateUtil.setUpDateDownLoadId(getActivity(), -1);
                    dismiss();
                    c.close();
                    return null;
                case DownloadManager.STATUS_SUCCESSFUL:
                    dismiss();
                    c.close();
                    return null;
                default:
                    break;
            }

            int fileSizeIdx =
                    c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
            int bytesDLIdx =
                    c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
            int fileSize = c.getInt(fileSizeIdx);
            int bytesDL = c.getInt(bytesDLIdx);
            c.close();
            return new DownLoadApkInfo(fileSize, bytesDL);
        } else {
            UpdateUtil.setUpDateDownLoadId(getActivity(), -1);
        }
        c.close();
        return null;
    }

    private class DownLoadApkInfo {
        public DownLoadApkInfo(int sizeTotal, int sizeHadDownLoaded) {
            this.sizeTotal = sizeTotal;
            this.sizeHadDownLoaded = sizeHadDownLoaded;
        }

        int sizeHadDownLoaded;
        int sizeTotal;
    }
}
