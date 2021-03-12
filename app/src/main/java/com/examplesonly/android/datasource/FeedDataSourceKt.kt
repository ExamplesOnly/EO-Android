package com.examplesonly.android.datasource

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.examplesonly.android.FeedQuery
import com.examplesonly.android.network.graphql.GqlClient
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FeedDataSourceKt(var context: Context) : PagingSource<Int, FeedQuery.Feed>() {


    private val gqlClient = GqlClient(context).client

    override fun getRefreshKey(state: PagingState<Int, FeedQuery.Feed>): Int? {
        Timber.e("FeedDataSourceKt getRefreshKey")
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedQuery.Feed> {
        return try {

            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            val offset = params.loadSize * (nextPageNumber - 1)

            Timber.e("FeedDataSourceKt load Size: %s Page: %s Offset: %s", params.loadSize, nextPageNumber, offset)

            val response = gqlClient.query(FeedQuery(params.loadSize, offset)).execute()

            if (response.hasErrors()) {
                Timber.e("Could not load result")
                return LoadResult.Error(Exception("Could not load result"))
            } else {

                Timber.e("Could not load result")
            }


            val nextPage = if (response.data?.Feed()?.size ?: 0 < params.loadSize) null else nextPageNumber + 1

            LoadResult.Page(
                    data = response.data?.Feed().orEmpty(),
                    prevKey = null,
                    nextKey = nextPage
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e("FeedDataSourceKt load LoadResult.Error %s", e.message)
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