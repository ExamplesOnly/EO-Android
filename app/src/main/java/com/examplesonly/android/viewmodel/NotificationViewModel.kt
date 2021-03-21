package com.examplesonly.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.examplesonly.android.FeedQuery
import com.examplesonly.android.NotificationsQuery
import com.examplesonly.android.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow

class NotificationViewModel(private val repository: NotificationRepository) : ViewModel() {

    private var currentSearchResult: Flow<PagingData<NotificationsQuery.Notification>>? = null

    fun getNotifications(): Flow<PagingData<NotificationsQuery.Notification>> {
        val newResult: Flow<PagingData<NotificationsQuery.Notification>> = repository.getNotificationStream()
                .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }
}