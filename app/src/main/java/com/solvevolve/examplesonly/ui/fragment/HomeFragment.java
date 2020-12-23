package com.solvevolve.examplesonly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.solvevolve.examplesonly.adapter.ExampleAdapter;
import com.solvevolve.examplesonly.adapter.HomeGridAdapter;
import com.solvevolve.examplesonly.databinding.FragmentHomeBinding;
import com.solvevolve.examplesonly.handler.VideoClickListener;
import com.solvevolve.examplesonly.model.Video;
import com.solvevolve.examplesonly.network.Api;
import com.solvevolve.examplesonly.network.video.VideoInterface;
import com.solvevolve.examplesonly.ui.view.ProfileGridDecoration;

import java.io.IOException;
import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ExampleAdapter mExampleAdapter;
    private HomeGridAdapter mHomeGridAdapter;
    private VideoInterface videoInterface;
    private ArrayList<Video> mExampleList = new ArrayList<>();

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

        binding.noInternet.setVisibility(View.GONE);

        setupGridExamples();

        if (mExampleList.size() == 0)
            getVideos();

        binding.swipeRefresh.setOnRefreshListener(this::getVideos);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    void getVideos() {
        videoInterface.getVideos().enqueue(new Callback<ArrayList<Video>>() {
            @Override
            public void onResponse(final Call<ArrayList<Video>> call, final Response<ArrayList<Video>> response) {

                binding.exampleList.setVisibility(View.VISIBLE);
                binding.noInternet.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Timber.e("HOME isSuccessful");
                    ArrayList<Video> videos = response.body();
                    mExampleList.clear();
                    mExampleList.addAll(videos);
                    mHomeGridAdapter.notifyDataSetChanged();
                } else {
                    Timber.e("HOME error");
                    try {
                        Timber.e(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                binding.swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(final @NotNull Call<ArrayList<Video>> call, final @NotNull Throwable t) {
                t.printStackTrace();
                if (t instanceof IOException) {
                    binding.exampleList.setVisibility(View.GONE);
                    binding.noInternet.setVisibility(View.VISIBLE);
                }
                binding.swipeRefresh.setRefreshing(false);
            }
        });
    }

    void setupGridExamples() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        binding.exampleList.setLayoutManager(layoutManager);
        binding.exampleList.addItemDecoration(new ProfileGridDecoration(20, 2));

        mHomeGridAdapter = new HomeGridAdapter(mExampleList, getActivity(),
                (VideoClickListener) getActivity());
        binding.exampleList.setAdapter(mHomeGridAdapter);
    }
}