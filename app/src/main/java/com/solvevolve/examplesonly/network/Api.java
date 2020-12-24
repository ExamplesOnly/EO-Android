package com.solvevolve.examplesonly.network;

import android.content.Context;

import com.solvevolve.examplesonly.BuildConfig;
import com.solvevolve.examplesonly.network.auth.TokenInterceptor;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    Context context;
    TokenInterceptor tokenInterceptor;

    public Api(Context context) {
        this.context = context;
        this.tokenInterceptor = new TokenInterceptor(context);
    }

    public Api(TokenInterceptor tokenInterceptor) {
        this.tokenInterceptor = tokenInterceptor;
    }

    public Retrofit getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(1500, TimeUnit.SECONDS)
                .addInterceptor(tokenInterceptor).build();

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
