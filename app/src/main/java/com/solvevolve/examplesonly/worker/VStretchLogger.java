package com.solvevolve.examplesonly.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.solvevolve.examplesonly.account.UserDataProvider;
import com.solvevolve.examplesonly.network.Api;
import com.solvevolve.examplesonly.network.video.VideoInterface;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class VStretchLogger extends Worker {

    private String viewId = null;
    private String videoId = null;
    private long stretch = 0;
    private VideoInterface videoInterface;
    private UserDataProvider userDataProvider;

    public VStretchLogger(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        userDataProvider = UserDataProvider.getInstance(getApplicationContext());
        videoInterface = new Api(getApplicationContext()).getClient().create(VideoInterface.class);
    }

    @NonNull
    @Override
    public Result doWork() {
        videoId = getInputData().getString("videoId");
        viewId = getInputData().getString("viewId");
        stretch = getInputData().getLong("stretch", 0);

        if (viewId == null || stretch == 0) {
            return Result.failure();
        }

        Call<HashMap<String, String>> postCall = videoInterface.postPlayTime(videoId, userDataProvider.getUserUuid(), viewId, stretch);
        try {
            Response<HashMap<String, String>> response = postCall.execute();

            if (response.isSuccessful()) {
                Timber.e("VStretchLogger %s", response.body().toString());
                Timber.e("VStretchLogger %s %s %s", videoId, viewId, stretch);
                return Result.success();
            } else {
                return Result.failure();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}
