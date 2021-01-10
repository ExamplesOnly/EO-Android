package com.examplesonly.android.network.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.examplesonly.android.model.VideoUploadData;
import com.examplesonly.android.util.NotificationHelper;

import java.util.Objects;

public class UploadRetryReceiver extends BroadcastReceiver {

    public static final String ACTION_RETRY = "com.examplesonly.ACTION_RETRY";
    public static final String ACTION_CLEAR = "com.examplesonly.ACTION_CLEAR";
    NotificationHelper mNotificationHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
/**
 * Handle notification user actions
 */
        mNotificationHelper = new NotificationHelper(context);
        int notificationId = intent.getIntExtra("notificationId", 0);
        VideoUploadData videoData = intent.getParcelableExtra("videoData");

        switch (Objects.requireNonNull(intent.getAction())) {
            case ACTION_RETRY:
                mNotificationHelper.cancelNotification(notificationId);
                Intent mIntent = new Intent(context, VideoUploadService.class);
                mIntent.putExtra("videoData", videoData);
                VideoUploadService.enqueueWork(context, mIntent);
                break;
            case ACTION_CLEAR:
                mNotificationHelper.cancelNotification(notificationId);
                break;
            default:
                break;
        }
    }
}
