package com.examplesonly.android.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.examplesonly.android.FeedQuery
import com.examplesonly.android.TrendingFeedQuery
import com.examplesonly.android.datasource.FeedDataSourceKt
import com.examplesonly.android.datasource.TrendingFeedDataSource
import kotlinx.coroutines.flow.Flow

class TrendingFeedRepository(private val context: Context) {

    fun getFeedStream(): Flow<PagingData<TrendingFeedQuery.TrendingFeed>> {
        return Pager(
                config = PagingConfig(
                        pageSize = NETWORK_PAGE_SIZE,
                        initialLoadSize = NETWORK_PAGE_SIZE,
                        enablePlaceholders = false
                ),
                pagingSourceFactory = { TrendingFeedDataSource(context = context) }
        ).flow
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 20
    }
}
