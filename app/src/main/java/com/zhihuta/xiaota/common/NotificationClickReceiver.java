package com.zhihuta.xiaota.common;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 点击通知栏下载项目，下载完成前点击都会进来，下载完成后点击不会进来。
 */
public class NotificationClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("NotifClickReceiver", "接收到广播" + intent.getDataString());
        long[] completeIds = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
        //正在下载的任务ID
//        long downloadTaskId = mAppUpgradePersistent.getDownloadTaskId(context);
//        if (completeIds == null || completeIds.length <= 0) {
//            openDownloadsPage(appContext);
//            return;
//        }

//        for (long completeId : completeIds) {
//            if (completeId == downloadTaskId) {
//                openDownloadsPage(appContext);
//                break;
//            }
//        }
    }

    /**
     * Open the Activity which shows a list of all downloads.
     *
     * @param context 上下文
     */
    private void openDownloadsPage(Context context) {
        Intent pageView = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        pageView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(pageView);
    }
}