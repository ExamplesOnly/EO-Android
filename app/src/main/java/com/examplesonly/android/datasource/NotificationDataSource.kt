package com.examplesonly.android.datasource

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.examplesonly.android.FeedQuery
import com.examplesonly.android.NotificationsQuery
import com.examplesonly.android.network.graphql.GqlClient
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class NotificationDataSource(var context: Context) : PagingSource<Int, NotificationsQuery.Notification>() {

    private val gqlClient = GqlClient(context).client

    override fun getRefreshKey(state: PagingState<Int, NotificationsQuery.Notification>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationsQuery.Notification> {

        return try {

            val nextPageNumber = params.key ?: 1
            val offset = params.loadSize * (nextPageNumber - 1)

            val response = gqlClient.query(NotificationsQuery(params.loadSize, offset)).execute()

            if (response.hasErrors()) {
                Timber.e("Could not load result: %s", response.errors)
                return LoadResult.Error(Exception("Could not load result"))
            } else {
                Timber.e("Notifications count: %s", response.data?.Notifications()?.size)
            }

            val nextPage = if (response.data?.Notifications()?.size ?: 0 < params.loadSize) null else nextPageNumber + 1

            LoadResult.Page(
                    data = response.data?.Notifications().orEmpty(),
                    prevKey = null,
                    nextKey = nextPage
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }

    }

    private suspend fun <T> ApolloCall<T>.execute() = suspendCoroutine<Response<T>> { cont ->
        enqueue(object : ApolloCall.Callback<T>() {

            override fun onFailure(e: ApolloException) {
                cont.resumeWithException(e)
            }

            override fun onResponse(response: Response<T>) {
                cont.resume(response)
            }
        })
    }
}