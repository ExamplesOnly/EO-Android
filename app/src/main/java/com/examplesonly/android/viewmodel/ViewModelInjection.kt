package com.examplesonly.android.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.examplesonly.android.datasource.FeedDataSourceKt
import com.examplesonly.android.repository.FeedRepositoryKt
import com.examplesonly.android.repository.NotificationRepository
import com.examplesonly.android.repository.TrendingFeedRepository

object ViewModelInjection {
    /**
     * Creates an instance of [FeedRepositoryKt]
     */
    private fun provideFeedRepository(context: Context): FeedRepositoryKt {
        return FeedRepositoryKt(context)
    }

    /**
     * Creates an instance of [TrendingFeedRepository]
     */
    private fun provideTrendingFeedRepository(context: Context): TrendingFeedRepository {
        return TrendingFeedRepository(context)
    }

    /**
     * Creates an instance of [NotificationRepository]
     */
    private fun provideNotificationRepository(context: Context): NotificationRepository {
        return NotificationRepository(context)
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [ViewModel] objects.
     */
    fun provideFeedViewModelFactory(context: Context): ViewModelProvider.Factory {
        return FeedViewModelFactory(provideFeedRepository(context))
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [ViewModel] objects.
     */
    fun provideTrendingFeedViewModelFactory(context: Context): ViewModelProvider.Factory {
        return TrendingFeedViewModelFactory(provideTrendingFeedRepository(context))
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [ViewModel] objects.
     */
    fun provideNotificationViewModelFactory(context: Context): ViewModelProvider.Factory {
        return NotificationViewModelFactory(provideNotificationRepository(context))
    }
}


class FeedViewModelFactory(private val repository: FeedRepositoryKt) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModelKt::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FeedViewModelKt(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class NotificationViewModelFactory(private val repository: NotificationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class TrendingFeedViewModelFactory(private val repository: TrendingFeedRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrendingFeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrendingFeedViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}