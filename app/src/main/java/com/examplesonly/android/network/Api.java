package com.examplesonly.android.network;

import retrofit2.Retrofit;

public class Api {

    public static Retrofit getClient() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .build();

        return retrofit;
    }
}
