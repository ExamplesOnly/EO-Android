package com.examplesonly.android.ui.fragment

import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.examplesonly.android.adapter.NotificationPagingAdapter
import com.examplesonly.android.databinding.FragmentNotificationBinding
import com.examplesonly.android.handler.VideoClickListener
import com.examplesonly.android.model.Video
import com.examplesonly.android.ui.activity.VideoPlayerActivity
import com.examplesonly.android.viewmodel.NotificationViewModel
import com.examplesonly.android.viewmodel.ViewModelInjection
import kotlinx.android.synthetic.main.fragment_notification.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotificationFragment : Fragment(), VideoClickListener {

    private lateinit var binding: FragmentNotificationBinding
    private lateinit var notificationViewModel: NotificationViewModel

    private lateinit var notificationAdapter: NotificationPagingAdapter
    private lateinit var view: ConstraintLayout

    private var feedJob: Job? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater)
        view = binding.root

        view.notificationList.visibility = View.VISIBLE
        initRecyclerView()

        notificationViewModel = ViewModelProvider(this, ViewModelInjection.provideNotificationViewModelFactory(requireContext()))
                .get(NotificationViewModel::class.java)

        notificationAdapter.addLoadStateListener { loadState ->
            updateViewState(loadState)
        }
        getNotifications()

        return view;
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        view.notificationList.layoutManager = layoutManager

        notificationAdapter = NotificationPagingAdapter(activity, this)
        view.notificationList.adapter = notificationAdapter

        view.swipe_refresh.setOnRefreshListener {
            view.swipe_refresh.isRefreshing = false
        }
    }

    private fun getNotifications() {
        feedJob?.cancel()
        feedJob = lifecycleScope.launch {
            notificationViewModel.getNotifications().collect {
                notificationAdapter.submitData(it)
            }
        }
    }

    private fun updateViewState(loadState: CombinedLoadStates) {
        // Only show the list if refresh succeeds.
        view.notificationList.isVisible = loadState.source.refresh is LoadState.NotLoading && loadState.source.refresh !is LoadState.Error
        view.loading_shimmer.isVisible = loadState.source.refresh is LoadState.Loading && loadState.source.refresh !is LoadState.Error
        // Show loading spinner during initial load or refresh.
//            view.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
        // Show the retry state if initial load or refresh fails.
        view.no_internet.isOffline(loadState.source.refresh is LoadState.Error)
    }

    override fun onVideoClicked(video: Video) {
        val videoPlayer = Intent(context, VideoPlayerActivity::class.java)
        videoPlayer.putExtra(VideoPlayerActivity.VIDEO_DATA, video)
        startActivity(videoPlayer)
    }
}