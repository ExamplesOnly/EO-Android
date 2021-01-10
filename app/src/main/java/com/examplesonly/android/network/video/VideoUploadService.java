package com.examplesonly.android.network.video;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.examplesonly.android.R;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.model.VideoUploadData;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.ui.activity.MainActivity;
import com.examplesonly.android.util.NotificationHelper;
import com.google.android.exoplayer2.util.MimeTypes;

import java.io.File;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import timber.log.Timber;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import static com.examplesonly.android.network.video.UploadProgressReceiver.ACTION_CLEAR_NOTIFICATION;
import static com.examplesonly.android.network.video.UploadProgressReceiver.ACTION_PROGRESS_NOTIFICATION;
import static com.examplesonly.android.network.video.UploadProgressReceiver.ACTION_UPLOADED;
import static com.examplesonly.android.network.video.UploadRetryReceiver.ACTION_CLEAR;
import static com.examplesonly.android.network.video.UploadRetryReceiver.ACTION_RETRY;
import static java.security.AccessController.getContext;

public class VideoUploadService extends JobIntentService {

    private static final String TAG = "FileUploadService";
    VideoInterface videoInterface;
    Disposable mDisposable;
    public static final int NOTIFICATION_ID = 1010;
    public static final int NOTIFICATION_RETRY_ID = 1020;

    private static final int JOB_ID = 1020;
    NotificationHelper mNotificationHelper;
    NotificationCompat.Builder progressNotification;
    VideoUploadData videoData;
    long mLastClickTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationHelper = new NotificationHelper(this);
    }

    @Override
    protected void onHandleWork(@NonNull final Intent intent) {
        this.onProgress(-1);

        videoData = intent.getParcelableExtra("videoData");
        if (videoData == null) {
            this.onErrors(null);
            Timber.e("onHandleWork: Invalid video data");
            return;
        }

        videoInterface = new Api(getApplication()).getClient().create(VideoInterface.class);
        String convertedFile = getApplication().getCacheDir().getPath() + "/" + System
                .currentTimeMillis() + ".mp4";
        String videoFileName = videoData.getVideoFilePath().replace(" ", "\\ ");

        int compress = FFmpeg.execute(
                "-y -i \"" + videoFileName + "\" -c:v libx264 -crf 23 "
                        + "-vf scale=\"480:-2\" "
                        + "-preset ultrafast "
                        + "-x264-params opencl=true "
//                        + "-hwaccel auto "
                        + "-movflags +faststart "
//                        + "-me_method zero "
                        + "-tune fastdecode "
                        + "-tune zerolatency "
                        + convertedFile);

        if (compress != 0) {
            this.onErrors(null);
            return;
        }

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(convertedFile);

        String metaHeight = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String metaWidth = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String metaDuration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String metaRotation = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        int rotation = metaRotation == null ? 0 : Integer.parseInt(metaRotation);

        if (rotation == 90 || rotation == 270) {
            String oldHeight = metaHeight;
            metaHeight = metaWidth;
            metaWidth = oldHeight;
        }

        String finalMetaHeight = metaHeight;
        String finalMetaWidth = metaWidth;
        Flowable<Double> fileObservable = Flowable.create(emitter -> {
            ResponseBody responseBody = videoInterface.uploadService(
                    createRequestBodyFromText(videoData.getTitle()),
                    createRequestBodyFromText(videoData.getDescription()),
                    createRequestBodyFromText(videoData.getCategoryValue()),
                    createRequestBodyFromText(metaDuration),
                    createRequestBodyFromText(finalMetaHeight),
                    createRequestBodyFromText(finalMetaWidth),
                    createRequestBodyFromText(videoData.getDemand()),
                    createMultipartBody("file", convertedFile, "video/mp4", emitter),
                    createMultipartBody("thumbnail", videoData.getThumbFilePath(), "image/png", null)
            ).blockingGet();
            emitter.onComplete();
        }, BackpressureStrategy.LATEST);


        // call onProgress()
        // call onSuccess() while file upload successful
        // call onErrors() if error occurred during file upload
        mDisposable = fileObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(VideoUploadService.this::onProgress,
                        VideoUploadService.this::onErrors,
                        VideoUploadService.this::onSuccess);
    }

    private void onErrors(Throwable throwable) {
        /**
         * Error occurred in file uploading
         */
        Intent successIntent = new Intent(ACTION_CLEAR_NOTIFICATION);
        successIntent.putExtra("notificationId", NOTIFICATION_ID);
        sendBroadcast(successIntent);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                0 /* Request code */, new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        /**
         * Add retry action button in notification
         */
        Intent retryIntent = new Intent(this, UploadRetryReceiver.class);
        retryIntent.putExtra("notificationId", NOTIFICATION_RETRY_ID);
        retryIntent.putExtra("videoData", videoData);
        retryIntent.setAction(ACTION_RETRY);
        /**
         * Add clear action button in notification
         */
        Intent clearIntent = new Intent(this, UploadRetryReceiver.class);
        clearIntent.putExtra("notificationId", NOTIFICATION_RETRY_ID);
        clearIntent.putExtra("videoData", videoData);
        clearIntent.setAction(ACTION_CLEAR);
        PendingIntent retryPendingIntent = PendingIntent.getBroadcast(this, 0, retryIntent, 0);
        PendingIntent clearPendingIntent = PendingIntent.getBroadcast(this, 0, clearIntent, 0);

        NotificationCompat.Builder mBuilder =
                mNotificationHelper.buildNotification(getString(R.string.error_upload_failed),
                        getString(R.string.message_upload_failed), resultPendingIntent);
        // attached Retry action in notification
        mBuilder.addAction(android.R.drawable.ic_menu_revert, getString(R.string.btn_retry),
                retryPendingIntent);
        // attached Cancel action in notification
        mBuilder.addAction(android.R.drawable.ic_menu_revert, getString(R.string.btn_cancel),
                clearPendingIntent);
        // Notify notification
        mNotificationHelper.notify(NOTIFICATION_RETRY_ID, mBuilder);

        mNotificationHelper.cancelNotification(NOTIFICATION_ID);
    }

    private void onProgress(double progress) {

        int currProgress = -1;

        if (progress >= 0)
            currProgress = (int) (100 * progress);

//        if (progressNotification == null) {
//            Timber.e("Service: onProgress: null");
//            progressNotification = mNotificationHelper.buildNotification(
//                    getBaseContext().getString(R.string.video_uploading),
//                    currProgress + "% uploaded", currProgress);
//            mLastClickTime = SystemClock.elapsedRealtime();
//            mNotificationHelper.notify(NOTIFICATION_ID, progressNotification);
//
//        } else if (SystemClock.elapsedRealtime() - mLastClickTime > 1300) {
//            mLastClickTime = SystemClock.elapsedRealtime();
//
//
//            Timber.e("Service: onProgress: not null");
//            progressNotification.setOngoing(true)
//                    .setContentText(currProgress + "% uploaded")
//                    .setProgress(100, currProgress, false);
//            if (progress == 100) {
//                progressNotification.setProgress(0, 0, false);
//            }
//
//            mNotificationHelper.notify(NOTIFICATION_ID, progressNotification);
//
//        }

        if (SystemClock.elapsedRealtime() - mLastClickTime > 1300) {
            mLastClickTime = SystemClock.elapsedRealtime();

            Intent progressIntent = new Intent(this, UploadProgressReceiver.class);
            progressIntent.setAction(ACTION_PROGRESS_NOTIFICATION);
            progressIntent.putExtra("notificationId", NOTIFICATION_ID);
            progressIntent.putExtra("progress", currProgress);
            sendBroadcast(progressIntent);
        }

    }

    private void onSuccess() {
        Intent successIntent = new Intent(this, UploadProgressReceiver.class);
        successIntent.setAction(ACTION_UPLOADED);
        successIntent.putExtra("notificationId", NOTIFICATION_ID);
        successIntent.putExtra("progress", 100);
        sendBroadcast(successIntent);
    }

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, VideoUploadService.class, JOB_ID, intent);
    }


    private MultipartBody.Part createMultipartBody(String fieldName, String filePath, String
            mimeType, FlowableEmitter<Double> emitter) {
        File file = new File(filePath);
        return MultipartBody.Part.createFormData(fieldName, file.getName(),
                createUploadRequestBody(file, mimeType, emitter));
    }

    private RequestBody createUploadRequestBody(File file, String mimeType,
                                                final FlowableEmitter<Double> emitter) {
        RequestBody requestBody = createRequestBodyFromFile(file, mimeType);
        return new UploadRequestBody(requestBody, new UploadRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(double progress, long bytesWritten, long contentLength) {

                double newProgress = (1.0 * bytesWritten) / contentLength;

                if (emitter != null)
                    emitter.onNext(newProgress);
            }

            @Override
            public void onError() {

            }

            @Override
            public void onFinish() {

            }
//            @Override
//            public void onRequestProgress(long bytesWritten, long contentLength) {
//                double progress = (1.0 * bytesWritten) / contentLength;
//                emitter.onNext(progress);
//            }
        });
    }

    private RequestBody createRequestBodyFromFile(File file, String mimeType) {
        return RequestBody.create(MediaType.parse(mimeType), file);
    }

    private RequestBody createRequestBodyFromText(String mText) {
        return RequestBody.create(MediaType.parse("text/plain"), mText == null ? "" : mText);
    }
}
