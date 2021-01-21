package com.examplesonly.android.network.auth;

import android.content.Context;

import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.network.Api;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import timber.log.Timber;

public class AccessTokenAuthenticator implements Authenticator {

    private final UserDataProvider userDataProvider;
    private final Context context;

    public AccessTokenAuthenticator(Context context) {
        this.context = context;
        this.userDataProvider = UserDataProvider.getInstance(context);
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {

        Timber.e("AccessTokenAuthenticator");
        if (response.code() == 401) {
            Timber.e("AccessTokenAuthenticator 401 %s", userDataProvider.getAccessToken());
            retrofit2.Response<HashMap<String, String>> refreshTokenResponse = new Api(context).getClient()
                    .create(AuthInterface.class).refreshToken(userDataProvider.getRefreshToken()).execute();

            if (refreshTokenResponse.code() == 200
                    && refreshTokenResponse.body() != null
                    && refreshTokenResponse.body().containsKey("accessToken")) {
                userDataProvider.setAccessToken(refreshTokenResponse.body().get("accessToken"));

                Timber.e("AccessTokenAuthenticator 200 %s", userDataProvider.getAccessToken());
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + userDataProvider.getAccessToken()).build();
            } else {
                Timber.e("AccessTokenAuthenticator else 1");
                return null;
            }
        } else {
            Timber.e("AccessTokenAuthenticator else 2");
            return null;
        }
    }
}
