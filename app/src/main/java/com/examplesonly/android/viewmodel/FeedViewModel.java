package com.examplesonly.android.viewmodel;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.examplesonly.android.FeedQuery;
import com.examplesonly.android.datasource.FeedDataSource;
import com.examplesonly.android.datasource.FeedDataSourceFactory;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import timber.log.Timber;

public class FeedViewModel extends ViewModel {


    private final MutableLiveData<FeedDataSource> feedMutableDataSource;
    private final LiveData<PagedList<FeedQuery.Feed>> pagedFeedList;
    private Executor executor;


    public FeedViewModel(Context context) {
        Timber.e("FeedViewModel Init Start");
        FeedDataSourceFactory feedDataSourceFactory = new FeedDataSourceFactory(context);
        feedMutableDataSource = feedDataSourceFactory.getFeedLiveData();

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(20)
//                .setPrefetchDistance(8)
                .build();

        executor = Executors.newFixedThreadPool(5);

        pagedFeedList = new LivePagedListBuilder<>(feedDataSourceFactory, config).build();

        Timber.e("FeedViewModel Init End");
    }


    public LiveData<PagedList<FeedQuery.Feed>> getPagedFeedList() {
        Timber.e("FeedViewModel getPagedFeedList");
        return pagedFeedList;
    }

    public  MutableLiveData<FeedDataSource> getFeedMutableDataSource() {
        return feedMutableDataSource;
    }
}
