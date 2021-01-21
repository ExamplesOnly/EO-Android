package com.examplesonly.android.network;

import android.content.Context;

import com.examplesonly.android.BuildConfig;
import com.examplesonly.android.network.auth.AccessTokenAuthenticator;
import com.examplesonly.android.network.auth.TokenInterceptor;
import com.examplesonly.android.network.auth.UserAgentInterceptor;

import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    Context context;
    TokenInterceptor tokenInterceptor;
    UserAgentInterceptor userAgentInterceptor;
    AccessTokenAuthenticator accessTokenAuthenticator;

    public Api(Context context) {
        this.context = context;
        this.tokenInterceptor = new TokenInterceptor(context);
        this.accessTokenAuthenticator = new AccessTokenAuthenticator(context);
        this.userAgentInterceptor = new UserAgentInterceptor();
    }

    public Api(TokenInterceptor tokenInterceptor) {
        this.tokenInterceptor = tokenInterceptor;
    }

    public Retrofit getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(1500, TimeUnit.SECONDS)
                .addInterceptor(tokenInterceptor)
                .addInterceptor(userAgentInterceptor)
                .authenticator(accessTokenAuthenticator)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
