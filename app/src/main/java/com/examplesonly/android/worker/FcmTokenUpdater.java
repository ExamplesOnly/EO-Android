package com.examplesonly.android.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.user.UserInterface;
import com.examplesonly.android.network.video.VideoInterface;

import java.util.HashMap;

import retrofit2.Response;
import timber.log.Timber;

public class FcmTokenUpdater extends Worker {

    private String token = null;

    private UserInterface userInterface;
    private UserDataProvider userDataProvider;

    public FcmTokenUpdater(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        userDataProvider = UserDataProvider.getInstance(getApplicationContext());
        userInterface = new Api(getApplicationContext()).getClient().create(UserInterface.class);
    }

    @NonNull
    @Override
    public Result doWork() {

        token = getInputData().getString("token");

        try {
            Response<HashMap<String, String>> response = userInterface
                    .updateFcmToken(userDataProvider.getRefreshToken(), token).execute();
            if (response.isSuccessful()) {
                Timber.e("FcmTokenUpdater %s", response.body().toString());
                return Result.success();
            } else {
                return Result.retry();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}
