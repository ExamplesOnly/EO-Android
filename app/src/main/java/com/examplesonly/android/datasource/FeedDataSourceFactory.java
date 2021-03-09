package com.examplesonly.android.datasource;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.examplesonly.android.FeedQuery;

import timber.log.Timber;

public class FeedDataSourceFactory extends DataSource.Factory<Integer, FeedQuery.Feed> {

    private FeedDataSource feedDataSource;
    private final Context context;

    public MutableLiveData<FeedDataSource> feedLiveData;

    public FeedDataSourceFactory(Context context) {
        Timber.e("FeedDataSourceFactory Init Start");
        this.feedDataSource = new FeedDataSource(context);
        this.context = context;
        this.feedLiveData = new MutableLiveData<>();
    }

    @NonNull
    @Override
    public DataSource<Integer, FeedQuery.Feed> create() {
        Timber.e("FeedDataSourceFactory create Start");
        feedDataSource = new FeedDataSource(context);
        feedLiveData.postValue(feedDataSource);
        return feedDataSource;
    }

    public MutableLiveData<FeedDataSource> getFeedLiveData() {
        return feedLiveData;
    }
}
