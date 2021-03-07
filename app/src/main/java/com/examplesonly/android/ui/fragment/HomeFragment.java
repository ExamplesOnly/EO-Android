package com.examplesonly.android.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.examplesonly.android.adapter.ExampleAdapter;
import com.examplesonly.android.adapter.HomeGridAdapter;
import com.examplesonly.android.databinding.FragmentHomeBinding;
import com.examplesonly.android.handler.FeedClickListener;
import com.examplesonly.android.handler.VideoClickListener;
import com.examplesonly.android.model.FeedDiffCallback;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.graphql.GqlClient;
import com.examplesonly.android.network.video.VideoInterface;
import com.examplesonly.android.ui.view.ProfileGridDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeGridAdapter mHomeGridAdapter;
    private VideoInterface videoInterface;
    private UserDataProvider userDataProvider;
    private ApolloClient gqlClient;

    private boolean loading = true;
    private int limit = 20, page = 1, pastVisibleItems, visibleItemCount, totalItemCount;
    Handler uiHandler = new Handler(Looper.getMainLooper());

    private final ArrayList<FeedQuery.Feed> videoFeedList = new ArrayList<>();
    private ArrayList<FeedQuery.Feed> oldVideoFeedList = new ArrayList<>();

//    private ArrayList<FeedQuery.Feed> mExampleList = new ArrayList<>();

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
        videoInterface = new Api(getContext()).getClient().create(VideoInterface.class);
        gqlClient = new GqlClient(getContext()).getClient();

        userDataProvider = UserDataProvider.getInstance(getContext());
        userDataProvider.setAccessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJBcGlBdXRoIiwic3ViIjoiYmlzaHdhanlvdGk0NTZAZ21haWwuY29tIiwiaWF0IjoxNjE1MTI3ODI0LCJleHAiOjE2MTUxMjc4MjR9.Vh-PJnuIAN7tuufs7g2JRv7XOP_hwPPpvkUt3loLnXQ");

        binding.noInternet.setVisibility(View.GONE);

        setupGridExamples();

        if (oldVideoFeedList.size() == 0)
            getVideos(false, limit, page);

        binding.swipeRefresh.setOnRefreshListener(() -> getVideos(true, limit, page));

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    void getVideos(boolean clear, int limit, int page) {
        if (clear)
            page = 1;
        loading = true;
        int offset = limit * (page - 1);

        gqlClient.query(new FeedQuery(limit, offset))
                .enqueue(new ApolloCallback<>(new ApolloCall.Callback<FeedQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<FeedQuery.Data> response) {
                        loading = false;
                        HomeFragment.this.page = HomeFragment.this.page + 1;

                        Timber.e(response.getData().toString());
                        try {
                            if (clear)
                                videoFeedList.clear();
                            oldVideoFeedList = videoFeedList;
                            videoFeedList.addAll(Objects.requireNonNull(Objects.requireNonNull(response.getData()).Feed()));
                            mHomeGridAdapter.notifyDataSetChanged();
//                            updateList(exampleList);

                            Timber.e("GQL DONE | items: %s %s", videoFeedList.size(), oldVideoFeedList.size());
                        } catch (Exception e) {
                            e.printStackTrace();
                            requireActivity().runOnUiThread(() -> {
                                binding.exampleList.setVisibility(View.GONE);
                                binding.noInternet.setVisibility(View.VISIBLE);
                            });
                        }
                        binding.swipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        e.printStackTrace();
                        loading = false;
                        binding.swipeRefresh.setRefreshing(false);
                    }
                }, uiHandler));

//        videoInterface.getVideos().enqueue(new Callback<ArrayList<Video>>() {
//            @Override
//            public void onResponse(final Call<ArrayList<Video>> call, final Response<ArrayList<Video>> response) {
//
//                binding.exampleList.setVisibility(View.VISIBLE);
//                binding.noInternet.setVisibility(View.GONE);
//
//                if (response.isSuccessful()) {
//                    Timber.e("HOME isSuccessful");
//                    ArrayList<Video> videos = response.body();
//                    mExampleList.clear();
//                    mExampleList.addAll(videos);
//                    mHomeGridAdapter.notifyDataSetChanged();
//                } else {
//                    Timber.e("HOME error");
//                    try {
//                        Timber.e(response.errorBody().string());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                binding.swipeRefresh.setRefreshing(false);
//            }
//
//            @Override
//            public void onFailure(final @NotNull Call<ArrayList<Video>> call, final @NotNull Throwable t) {
//                t.printStackTrace();
//                if (t instanceof IOException) {
//                    binding.exampleList.setVisibility(View.GONE);
//                    binding.noInternet.setVisibility(View.VISIBLE);
//                }
//                binding.swipeRefresh.setRefreshing(false);
//            }
//        });
    }


    public void updateList(ArrayList<FeedQuery.Feed> newList) {
        Timber.e("updateList");
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new FeedDiffCallback(this.oldVideoFeedList, newList));
        diffResult.dispatchUpdatesTo(mHomeGridAdapter);
    }


    void setupGridExamples() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);

        binding.exampleList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                int[] firstVisibleItems = null;
                firstVisibleItems = layoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                if (firstVisibleItems != null && firstVisibleItems.length > 0) {
                    pastVisibleItems = firstVisibleItems[0];
                }
                Timber.e("scroll %s %s %s", totalItemCount, visibleItemCount, pastVisibleItems);

                if (totalItemCount - pastVisibleItems < 12 && !loading) {
                    getVideos(false, limit, page);
                }

                if (!loading) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = false;
                    }
                }
            }
        });

        binding.exampleList.setLayoutManager(layoutManager);
        binding.exampleList.addItemDecoration(new ProfileGridDecoration(20, 2));

        mHomeGridAdapter = new HomeGridAdapter(videoFeedList, getActivity(),
                (FeedClickListener) getActivity());
        binding.exampleList.setAdapter(mHomeGridAdapter);
    }
}