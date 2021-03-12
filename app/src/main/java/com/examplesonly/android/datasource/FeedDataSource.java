package com.examplesonly.android.datasource;

import android.content.Context;

import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloQueryCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.rx3.Rx3Apollo;
import com.examplesonly.android.FeedQuery;
import com.examplesonly.android.network.graphql.GqlClient;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class FeedDataSource extends RxPagingSource<Integer, FeedQuery.Feed> {


    private ApolloClient gqlClient;
    private Context context;

    public FeedDataSource(Context context) {
        Timber.e("FeedDataSource Init Start");
        this.context = context;
        gqlClient = new GqlClient(context).getClient();
    }

//    @Override
//    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<FeedQuery.Feed> callback) {
//
//        Timber.e("FeedDataSource loadInitial");
//
//        gqlClient.query(new FeedQuery(params.requestedLoadSize, params.requestedStartPosition))
//                .enqueue(new ApolloCall.Callback<FeedQuery.Data>() {
//                    @Override
//                    public void onResponse(@NotNull Response<FeedQuery.Data> response) {
//                        try {
//                            Timber.e("FeedDataSource loadInitial onResponse %s", response.getData().Feed().size());
//                            List<FeedQuery.Feed> feed = Objects.requireNonNull(response.getData()).Feed();
//                            assert feed != null;
//
//                            callback.onResult(feed, 0, feed.size());
//                            Timber.e("FeedDataSource loadInitial onResponse callback");
//                        } catch (Exception e) {
//                            List<FeedQuery.Feed> feed = new ArrayList<>();
//                            callback.onResult(feed, 0, 0);
//                            Timber.e("FeedDataSource loadInitial onResponse callback 0");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull ApolloException e) {
//                        Timber.e("FeedDataSource loadInitial onFailure %s", e.getMessage());
//                        e.printStackTrace();
//                        List<FeedQuery.Feed> feed = new ArrayList<>();
//                        callback.onResult(feed, 0, 0);
//                        Timber.e("FeedDataSource loadInitial onFailure callback 0");
//                    }
//                });
//    }

//    @Override
//    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<FeedQuery.Feed> callback) {
//
//        Timber.e("FeedDataSource loadRange Start");
//        gqlClient.query(new FeedQuery(params.loadSize, params.startPosition))
//                .enqueue(new ApolloCall.Callback<FeedQuery.Data>() {
//                    @Override
//                    public void onResponse(@NotNull Response<FeedQuery.Data> response) {
//                        Timber.e("FeedDataSource loadRange onResponse %s", response.getData().Feed().size());
//                        try {
//                            List<FeedQuery.Feed> feed = Objects.requireNonNull(response.getData()).Feed();
//                            assert feed != null;
//                            callback.onResult(feed);
//                        } catch (Exception e) {
//                            List<FeedQuery.Feed> feed = new ArrayList<>();
//                            callback.onResult(feed);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull ApolloException e) {
//                        e.printStackTrace();
//                        List<FeedQuery.Feed> feed = new ArrayList<>();
//                        callback.onResult(feed);
//                    }
//                });
//    }

    @Override
    public @NotNull Single<LoadResult<Integer, FeedQuery.Feed>> loadSingle(@NotNull LoadParams<Integer> loadParams) {
        int page = loadParams.getKey() != null ? loadParams.getKey() : 1;
        int offset = loadParams.getLoadSize() * (page - 1);

        try {
            ApolloCall<FeedQuery.Data> query = gqlClient.query(new FeedQuery(loadParams.getLoadSize(), offset));
            query.toBuilder().build();

            Single<Response<FeedQuery.Data>> queryResponse = Rx3Apollo.from(query).single(null);


            return queryResponse.subscribeOn(Schedulers.io())
                    .map(m -> toLoadResult(m.getData().Feed(), page))
                    .onErrorReturn(LoadResult.Error::new);
        } catch (Exception e) {
            return Single.just(new LoadResult.Error(e));
        }

    }

    private LoadResult<Integer, FeedQuery.Feed> toLoadResult(List<FeedQuery.Feed> feedList, int page) {
        return new LoadResult.Page(feedList, page == 1 ? null : page - 1, page + 1);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NotNull PagingState<Integer, FeedQuery.Feed> pagingState) {
        return null; // new // LoadResult.Page(FeedQuery.Feed, page == 1 ? null : page - 1, page + 1);
    }
}
