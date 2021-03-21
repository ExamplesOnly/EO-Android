package com.examplesonly.android.network.graphql

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.examplesonly.android.BuildConfig
import com.examplesonly.android.network.auth.GqlTokenInterceptor
import com.examplesonly.android.network.auth.UserAgentInterceptor
import okhttp3.OkHttpClient

class GqlClient(var context: Context) {
    val client: ApolloClient
        get() = ApolloClient.builder()
                .serverUrl(BuildConfig.API_URL + "graphql")
                .okHttpClient(OkHttpClient.Builder()
                        .addInterceptor(GqlTokenInterceptor(context))
                        .addInterceptor(UserAgentInterceptor())
                        .build()
                )
                .build()
}