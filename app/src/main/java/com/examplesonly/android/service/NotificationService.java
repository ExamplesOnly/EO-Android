package com.examplesonly.android.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.examplesonly.android.R;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.category.CategoryInterface;
import com.examplesonly.android.network.user.UserInterface;
import com.examplesonly.android.ui.activity.LaunchActivity;
import com.examplesonly.android.util.NotificationHelper;
import com.examplesonly.android.worker.FcmTokenUpdater;
import com.examplesonly.android.worker.VStretchLogger;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import timber.log.Timber;

import static com.examplesonly.android.util.Constant.ACCOUNT_CHANNEL_ID;
import static com.examplesonly.android.util.Constant.NOTIFICATION_ACTION_VIDEO;
import static com.examplesonly.android.util.Constant.NOTIFICATION_DESTINATION;
import static com.examplesonly.android.util.Constant.NOTIFICATION_DESTINATION_ID;
import static com.examplesonly.android.util.Constant.NOTIFICATION_DESTINATION_VIDEO;
import static com.examplesonly.android.util.Constant.NOTIFICATION_LAUNCH;

public class NotificationService extends FirebaseMessagingService {

    NotificationManagerCompat notificationManager;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
        createNotificationChannel();
        notificationManager = NotificationManagerCompat.from(this);

        Timber.d("From: %s", remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Timber.d("Message data payload: %s", remoteMessage.getData());

            if (remoteMessage.getData().containsKey("notificationText") &&
                    remoteMessage.getData().containsKey("notificationType") &&
                    remoteMessage.getData().containsKey("notificationThumb")) {

                String contentText = remoteMessage.getData().get("notificationText");
                String largeIconUrl = remoteMessage.getData().get("notificationThumb");

                Intent launchIntent = new Intent(this, LaunchActivity.class);
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                if (remoteMessage.getData().containsKey("actionType") &&
                        remoteMessage.getData().containsKey("actionId")) {

                    switch (remoteMessage.getData().get("actionType")) {
                        case NOTIFICATION_ACTION_VIDEO:
                            launchIntent.putExtra(NOTIFICATION_LAUNCH, true);
                            launchIntent.putExtra(NOTIFICATION_DESTINATION, NOTIFICATION_DESTINATION_VIDEO);
                            launchIntent.putExtra(NOTIFICATION_DESTINATION_ID, remoteMessage.getData().get("actionId"));
                            break;
                        default:
                            break;
                    }
                }

                PendingIntent launch = PendingIntent.getActivity(this, 0,
                        launchIntent, 0);

                Glide.with(this)
                        .asBitmap()
                        .load(largeIconUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                                        NotificationService.this, ACCOUNT_CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_stat_icon)
                                        .setLargeIcon(resource)
                                        .setContentTitle("ExamplesOnly")
                                        .setContentText(contentText)
                                        .setAutoCancel(true)
                                        .setContentIntent(launch)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                notificationManager.notify((int) System.currentTimeMillis(), builder.build());
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Timber.d("Message Notification Body: %s", remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Timber.d("onNewToken %s", s);
        publishFcmToken(s);
    }

    // Publish the new FCM token using a service worker
    private void publishFcmToken(String token) {
        WorkRequest fcmTokenUpdater =
                new OneTimeWorkRequest.Builder(FcmTokenUpdater.class)
                        .setConstraints(new Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build())
                        .setInputData(new Data.Builder()
                                .putString("token", token)
                                .build())
                        .build();
        WorkManager
                .getInstance(this)
                .enqueue(fcmTokenUpdater);
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_account_name);
            String description = getString(R.string.notification_channel_account_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(ACCOUNT_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
