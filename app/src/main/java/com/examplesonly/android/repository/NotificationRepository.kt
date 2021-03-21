package com.examplesonly.android.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.examplesonly.android.NotificationsQuery
import com.examplesonly.android.datasource.FeedDataSourceKt
import com.examplesonly.android.datasource.NotificationDataSource
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val context: Context) {

    fun getNotificationStream(): Flow<PagingData<NotificationsQuery.Notification>> {
        return Pager(
                config = PagingConfig(
                        pageSize = NETWORK_PAGE_SIZE,
                        initialLoadSize = NETWORK_PAGE_SIZE,
                        enablePlaceholders = false
                ),
                pagingSourceFactory = { NotificationDataSource(context = context) }
        ).flow
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 20
    }
}