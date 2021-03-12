package com.examplesonly.android.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.examplesonly.android.adapter.FeedRecentPagingAdapter
import com.examplesonly.android.adapter.FeedTrendingPagingAdapter
import com.examplesonly.android.databinding.FragmentFeedBinding
import com.examplesonly.android.databinding.FragmentHomeBinding
import com.examplesonly.android.handler.FeedClickListener
import com.examplesonly.android.handler.VideoClickListener
import com.examplesonly.android.ui.view.ProfileGridDecoration
import com.examplesonly.android.viewmodel.FeedViewModelKt
import com.examplesonly.android.viewmodel.TrendingFeedViewModel
import com.examplesonly.android.viewmodel.ViewModelInjection
import kotlinx.android.synthetic.main.fragment_feed.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

private const val ARG_FEED_TYPE = "feed_type"
public const val FEED_TYPE_RECENT = "feed_type_recent"
public const val FEED_TYPE_TRENDING = "feed_type_trending"

class FeedFragment : Fragment() {

    private lateinit var binding: FragmentFeedBinding
    private lateinit var recentFeedViewModel: FeedViewModelKt
    private lateinit var trendingFeedViewModel: TrendingFeedViewModel

    private lateinit var adapterRecent: FeedRecentPagingAdapter
    private lateinit var adapterTrending: FeedTrendingPagingAdapter
    private lateinit var view: ConstraintLayout

    private var feedJob: Job? = null

    private var feedType: String? = FEED_TYPE_RECENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            feedType = it.getString(ARG_FEED_TYPE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentFeedBinding.inflate(inflater)
        view = binding.root

        view.exampleList.visibility = View.VISIBLE


        initView()
        initRecyclerView()

        when (feedType) {
            FEED_TYPE_RECENT -> {
                recentFeedViewModel = ViewModelProvider(this, ViewModelInjection.provideFeedViewModelFactory(requireContext()))
                        .get(FeedViewModelKt::class.java)

                adapterRecent = FeedRecentPagingAdapter(activity, activity as VideoClickListener?)
                view.exampleList.adapter = adapterRecent

                adapterRecent.addLoadStateListener { loadState ->
                    updateViewState(loadState)
                }
                getRecentFeed()

            }
            FEED_TYPE_TRENDING -> {
                trendingFeedViewModel = ViewModelProvider(this, ViewModelInjection.provideTrendingFeedViewModelFactory(requireContext()))
                        .get(TrendingFeedViewModel::class.java)

                adapterTrending = FeedTrendingPagingAdapter(activity, activity as VideoClickListener?)
                view.exampleList.adapter = adapterTrending

                adapterTrending.addLoadStateListener { loadState ->
                    updateViewState(loadState)
                }
                getTrendingFeed()
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        retainInstance = true
    }

    private fun initView() {
        view.no_internet.onRefreshListener {
            Timber.e("Refresh")
            getRecentFeed()
        }
    }

    private fun initRecyclerView() {
        val layoutManager = StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL)

        view.exampleList.layoutManager = layoutManager
        view.exampleList.addItemDecoration(ProfileGridDecoration(20, 2))
        view.swipe_refresh.setOnRefreshListener {
            view.swipe_refresh.isRefreshing = false
        }
    }

    private fun updateViewState(loadState: CombinedLoadStates) {
        val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error

        // Only show the list if refresh succeeds. // 36466 - 10k
        view.exampleList.isVisible = loadState.source.refresh is LoadState.NotLoading && loadState.source.refresh !is LoadState.Error
        view.loading_shimmer.isVisible = loadState.source.refresh is LoadState.Loading && loadState.source.refresh !is LoadState.Error
        // Show loading spinner during initial load or refresh.
//            view.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
        // Show the retry state if initial load or refresh fails.
        view.no_internet.isOffline(loadState.source.refresh is LoadState.Error)
    }

    private fun getRecentFeed() {
        feedJob?.cancel()
        feedJob = lifecycleScope.launch {
            recentFeedViewModel.getFeed().collect {
                adapterRecent.submitData(it)
            }
        }
    }

    private fun getTrendingFeed() {
        feedJob?.cancel()
        feedJob = lifecycleScope.launch {
            trendingFeedViewModel.getFeed().collect {
                adapterTrending.submitData(it)
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param feedType type of feed this fragment will show.
         * @return A new instance of fragment TrendingFeedFragment.
         */
        @JvmStatic
        fun newInstance(feedType: String) =
                FeedFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_FEED_TYPE, feedType)
                    }
                }
    }

}