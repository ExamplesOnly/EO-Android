package com.examplesonly.android.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.examplesonly.android.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationHelper extends ContextWrapper {
    private NotificationManager manager;
    public static final String WAVE_CHANNEL = "default";

    NotificationCompat.Builder progressBuilder;

    public NotificationHelper(Context mContext) {
        super(mContext);
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(WAVE_CHANNEL,
                    getString(R.string.default_notification_channel_id), NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setLightColor(Color.GREEN);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(mChannel);
        }
    }

//    public NotificationCompat.Builder getNotification(String title, String body, int progress) {
//
//        if (progressBuilder == null) {
//            progressBuilder = buildNotification(title, body, progress);
//            return progressBuilder;
//        }
//
//        progressBuilder.setOngoing(true);
//        progressBuilder.setProgress(100, progress, false);
//        if (progress == 100) {
//            progressBuilder.setProgress(100, 0, true);
//            progressBuilder.setContentTitle("Processing Example")
//                    .setContentText("Please wait");
//        }
//        return progressBuilder;
//    }

    public NotificationCompat.Builder buildNotification(String title, String body, int progress) {
        progressBuilder = new NotificationCompat.Builder(getApplicationContext(), WAVE_CHANNEL);
        progressBuilder.setSmallIcon(getSmallIcon());
        progressBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        progressBuilder.setContentTitle(title)
                .setContentText(body)
                .setOngoing(true)
                //.setContentIntent(resultPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        progressBuilder.setVibrate(new long[]{0L});
        progressBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        progressBuilder.setProgress(100, progress, false);

        if (progress == -1) {
            progressBuilder.setProgress(100, 0, true);
            progressBuilder.setContentTitle("Processing Example")
                    .setContentText("Please wait");
        } else if (progress == 100) {
            progressBuilder.setProgress(0, 0, true);
            progressBuilder.setContentText(body);
        }
        return progressBuilder;
    }

    public NotificationCompat.Builder buildNotification(String title, String body,
                                                        PendingIntent resultPendingIntent) {
        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(getApplicationContext(), WAVE_CHANNEL);
        mBuilder.setSmallIcon(getSmallIcon());
        mBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        mBuilder.setContentTitle(title)
                .setContentText(body)
                .setContentIntent(resultPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setVibrate(new long[]{0L});
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        return mBuilder;
    }

    /**
     * Send a notification.
     *
     * @param id           The ID of the notification
     * @param notification The notification object
     */
    public void notify(int id, NotificationCompat.Builder notification) {
        getManager().notify(id, notification.build());
    }

    /**
     * Get the small icon for this app
     *
     * @return The small icon resource id
     */
    private int getSmallIcon() {
        return R.drawable.ic_stat_icon;
    }

    /**
     * Get the notification manager.
     * <p>
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public void cancelNotification(int notificationId) {
        getManager().cancel(notificationId);
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}