package com.examplesonly.android.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.examplesonly.android.FeedQuery
import com.examplesonly.android.datasource.FeedDataSourceKt
import kotlinx.coroutines.flow.Flow

class FeedRepositoryKt(private val context: Context) {

    fun getFeedStream(): Flow<PagingData<FeedQuery.Feed>> {
        return Pager(
                config = PagingConfig(
                        pageSize = NETWORK_PAGE_SIZE,
                        initialLoadSize = NETWORK_PAGE_SIZE,
                        enablePlaceholders = false
                ),
                pagingSourceFactory = { FeedDataSourceKt(context = context) }
        ).flow
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 20
    }
}