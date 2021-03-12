package com.examplesonly.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.examplesonly.android.FeedQuery
import com.examplesonly.android.TrendingFeedQuery
import com.examplesonly.android.repository.FeedRepositoryKt
import com.examplesonly.android.repository.TrendingFeedRepository
import kotlinx.coroutines.flow.Flow

class TrendingFeedViewModel(private val repositoryKt: TrendingFeedRepository) : ViewModel() {

    private var currentSearchResult: Flow<PagingData<TrendingFeedQuery.TrendingFeed>>? = null

    fun getFeed(): Flow<PagingData<TrendingFeedQuery.TrendingFeed>> {
        val newResult: Flow<PagingData<TrendingFeedQuery.TrendingFeed>> = repositoryKt.getFeedStream()
                .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

}