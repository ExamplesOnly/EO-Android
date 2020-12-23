package com.solvevolve.examplesonly.network.video;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import com.solvevolve.examplesonly.network.Api;

class VideoUploadService extends JobIntentService {

    private static final int JOB_ID = 1090;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleWork(@NonNull final Intent intent) {
        VideoInterface videoInterface = new Api(getApplication()).getClient().create(VideoInterface.class);

    }

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, VideoUploadService.class, JOB_ID, intent);
    }
}
