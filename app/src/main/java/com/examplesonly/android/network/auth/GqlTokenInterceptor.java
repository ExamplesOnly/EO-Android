package com.examplesonly.android.network.auth;

import android.content.Context;

import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.model.AccessToken;
import com.examplesonly.android.network.Api;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class GqlTokenInterceptor implements Interceptor {

    private final UserDataProvider userDataProvider;
    private final Context context;

//    public GqlTokenInterceptor(UserDataProvider userDataProvider) {
//        this.userDataProvider = userDataProvider;
//    }

    public GqlTokenInterceptor(Context context) {
        this.context = context;
        this.userDataProvider = UserDataProvider.getInstance(context);
    }

    @Override
    public Response intercept(final @NotNull Chain chain) throws IOException {
        if (userDataProvider.isAuthenticated()) {
            Timber.e("GqlTokenInterceptor isAuthenticated");
            String token = userDataProvider.getAccessToken();

            Request authenticatedRequest = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(authenticatedRequest);
        } else {
            // Create a new request to get new access token
            Response refreshTokenResponse = chain.proceed(new Api(context).getClient()
                    .create(AuthInterface.class).refreshToken(userDataProvider.getRefreshToken()).request());

            if (refreshTokenResponse.isSuccessful()
                    && refreshTokenResponse.body() != null) {
                AccessToken responseToken = new Gson().fromJson(refreshTokenResponse.body().string(), AccessToken.class);

                Timber.e("GqlTokenInterceptor refreshToken %s", responseToken.getAccessToken());
                Request authenticatedRequest = chain.request()
                        .newBuilder()
                        .addHeader("Authorization", "Bearer " + responseToken.getAccessToken())
                        .build();
                return chain.proceed(authenticatedRequest);
            } else {
                Timber.e("GqlTokenInterceptor refreshTokenResponse NOT Successful %s %s %s", refreshTokenResponse.code(),
                        refreshTokenResponse.body().contentLength(), refreshTokenResponse.body().string());
                Timber.e("RefreshToken %s", userDataProvider.getRefreshToken());
                refreshTokenResponse.close();
                return chain.proceed(chain.request());
            }
        }
    }
}
