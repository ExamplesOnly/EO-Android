package com.examplesonly.android.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloCallback;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.examplesonly.android.FeedQuery;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.adapter.FeedGridAdapter;
import com.examplesonly.android.adapter.FeedPagedAdapter;
import com.examplesonly.android.databinding.FragmentHomeBinding;
import com.examplesonly.android.datasource.FeedDataSourceFactory;
import com.examplesonly.android.handler.FeedClickListener;
import com.examplesonly.android.adapter.diffUtil.FeedDiffCallback;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.graphql.GqlClient;
import com.examplesonly.android.network.video.VideoInterface;
import com.examplesonly.android.ui.view.ProfileGridDecoration;
import com.examplesonly.android.viewmodel.FeedViewModel;
import com.examplesonly.android.viewmodel.FeedViewModelFactory;

import java.util.ArrayList;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FeedPagedAdapter feedPagedAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        FeedViewModel feedViewModel = new ViewModelProvider(this, new FeedViewModelFactory(getContext())).get(FeedViewModel.class);

        binding.noInternet.setVisibility(View.GONE);
        binding.exampleList.setVisibility(View.VISIBLE);

        setupRecyclerView();

        feedViewModel.getPagedFeedList().observe(getViewLifecycleOwner(), feeds -> {
            Timber.e("HomeFragment feedViewModel.getPagedFeedList() %s", feeds.size());
            feedPagedAdapter.submitList(feeds);
            binding.swipeRefresh.setRefreshing(false);
        });

        binding.swipeRefresh.setOnRefreshListener(() -> {
            if (feedViewModel.getFeedMutableDataSource().getValue() != null) {
                binding.swipeRefresh.setRefreshing(false);
//                feedViewModel.getFeedMutableDataSource().getValue().invalidate();
            }
        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    void setupRecyclerView() {
        Timber.e("HomeFragment setupRecyclerView");
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);

        binding.exampleList.setLayoutManager(layoutManager);
        binding.exampleList.addItemDecoration(new ProfileGridDecoration(20, 2));

        feedPagedAdapter = new FeedPagedAdapter(getActivity(),
                (FeedClickListener) getActivity());
        binding.exampleList.setAdapter(feedPagedAdapter);
    }
}