package com.examplesonly.android.network.graphql;

import android.content.Context;

import com.apollographql.apollo.ApolloClient;
import com.examplesonly.android.BuildConfig;
import com.examplesonly.android.network.auth.GqlTokenInterceptor;
import com.examplesonly.android.network.auth.UserAgentInterceptor;

import okhttp3.OkHttpClient;

public class GqlClient {

    Context context;

    public GqlClient(Context context) {
        this.context = context;
    }

    public ApolloClient getClient() {
        return ApolloClient.builder()
                .serverUrl(BuildConfig.API_URL + "graphql")
                .okHttpClient(new OkHttpClient.Builder()
                        .addInterceptor(new GqlTokenInterceptor(context))
                        .addInterceptor(new UserAgentInterceptor())
                        .build()
                )
                .build();
    }

}
