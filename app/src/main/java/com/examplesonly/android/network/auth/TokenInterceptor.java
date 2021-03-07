package com.examplesonly.android.network.auth;

import android.content.Context;

import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.network.Api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

public class TokenInterceptor implements Interceptor {

    private final UserDataProvider userDataProvider;

    public TokenInterceptor(UserDataProvider userDataProvider) {
        this.userDataProvider = userDataProvider;
    }

    public TokenInterceptor(Context context) {
        this.userDataProvider = UserDataProvider.getInstance(context);
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        if (userDataProvider.isAuthorized()) {
            String token = userDataProvider.getAccessToken();
            Timber.e("TokenInterceptor intercept");

            Request authenticatedRequest = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(authenticatedRequest);
        } else {
            return chain.proceed(chain.request());
        }
    }
}
