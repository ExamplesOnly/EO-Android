package com.examplesonly.android.network.video;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.examplesonly.android.R;
import com.examplesonly.android.ui.activity.MainActivity;
import com.examplesonly.android.util.NotificationHelper;

import java.util.Objects;

public class UploadProgressReceiver extends BroadcastReceiver {

    private static final String TAG = "FileProgressReceiver";
    public static final String ACTION_CLEAR_NOTIFICATION = "com.examplesonly.ACTION_CLEAR_NOTIFICATION";
    public static final String ACTION_PROGRESS_NOTIFICATION = "com.examplesonly.ACTION_PROGRESS_NOTIFICATION";
    public static final String ACTION_UPLOADED = "com.examplesonly.ACTION_UPLOADED";
    NotificationHelper mNotificationHelper;
    public static final int NOTIFICATION_ID = 1010;
    NotificationCompat.Builder notification;


    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationHelper = new NotificationHelper(context);
        // Get notification id
        int notificationId = intent.getIntExtra("notificationId", 1);
        // Receive progress
        int progress = intent.getIntExtra("progress", 0);
        switch (Objects.requireNonNull(intent.getAction())) {
            case ACTION_PROGRESS_NOTIFICATION:
                notification = mNotificationHelper.buildNotification(context.getString(R.string.video_uploading),
                        progress + "% uploaded", progress);
                mNotificationHelper.notify(NOTIFICATION_ID, notification);
                break;
            case ACTION_CLEAR_NOTIFICATION:
                mNotificationHelper.cancelNotification(notificationId);
                break;
            case ACTION_UPLOADED:
                Intent resultIntent = new Intent(context, MainActivity.class);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                        0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                notification =
                        mNotificationHelper.buildNotification(context.getString(R.string.message_upload_success),
                                context.getString(R.string.message_upload_success_desc), resultPendingIntent);
                mNotificationHelper.notify(NOTIFICATION_ID, notification);
                break;
            default:
                break;
        }
    }
}
