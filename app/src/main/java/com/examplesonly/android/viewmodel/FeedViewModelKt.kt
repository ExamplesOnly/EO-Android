package com.examplesonly.android.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.examplesonly.android.FeedQuery
import com.examplesonly.android.repository.FeedRepositoryKt
import kotlinx.coroutines.flow.Flow

class FeedViewModelKt(private val repositoryKt: FeedRepositoryKt) : ViewModel() {

    private var currentSearchResult: Flow<PagingData<FeedQuery.Feed>>? = null

    fun getFeed(): Flow<PagingData<FeedQuery.Feed>> {
        val newResult: Flow<PagingData<FeedQuery.Feed>> = repositoryKt.getFeedStream()
                .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }
}