package com.examplesonly.android.network.auth;

import android.content.Context;
import com.examplesonly.android.account.UserDataProvider;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
public class TokenInterceptor implements Interceptor {

    UserDataProvider userDataProvider;

    public TokenInterceptor(UserDataProvider userDataProvider) {
        this.userDataProvider = userDataProvider;
    }

    public TokenInterceptor(Context context) {
        this.userDataProvider = new UserDataProvider(context);
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        if (userDataProvider.isAuthorized()) {
            String token = userDataProvider.getToken();
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
