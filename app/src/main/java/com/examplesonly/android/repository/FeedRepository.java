package com.examplesonly.android.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.apollographql.apollo.ApolloClient;
import com.examplesonly.android.FeedQuery;
import com.examplesonly.android.network.graphql.GqlClient;

import java.util.ArrayList;
import java.util.List;

public class FeedRepository {

    private static FeedRepository instance;
    private Context context;

    private ArrayList<FeedQuery.Feed> feedList;
    private ApolloClient gqlClient;

    private FeedRepository(Context context) {
        this.context = context;
    }

    public static FeedRepository getInstance(Context context) {
        if (instance == null) {
            instance = new FeedRepository(context);
        }
        return instance;
    }

    public MutableLiveData<List<FeedQuery.Feed>> getFeed() {

        gqlClient = new GqlClient(context).getClient();
        return null;
    }


}
