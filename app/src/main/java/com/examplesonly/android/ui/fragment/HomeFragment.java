package com.examplesonly.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.examplesonly.android.adapter.HomeAdapter;
import com.examplesonly.android.adapter.HomeGridAdapter;
import com.examplesonly.android.databinding.FragmentHomeBinding;
import com.examplesonly.android.handler.VideoClickListener;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.video.VideoInterface;
import com.examplesonly.android.ui.view.ProfileGridDecoration;
import java.io.IOException;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeAdapter mHomeAdapter;
    private HomeGridAdapter mHomeGridAdapter;
    private VideoInterface videoInterface;
    private ArrayList<Video> mExampleListList = new ArrayList<>();

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

//        setupExamples();
        setupGridExamples();
        getVideos();

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getVideos();
    }

    void getVideos() {
        videoInterface.getVideos().enqueue(new Callback<ArrayList<Video>>() {
            @Override
            public void onResponse(final Call<ArrayList<Video>> call, final Response<ArrayList<Video>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Video> videos = response.body();
                    mExampleListList.clear();
                    mExampleListList.addAll(videos);
                    mHomeGridAdapter.notifyDataSetChanged();
                    for (int i = 0; i < videos.size(); i++) {
                        Timber.e("HomeFragment %s %s", videos.get(i).getUser().getFirstName(),
                                videos.get(i).getUser().getProfilePhoto());
                    }
                } else {
                    try {
                        Timber.e(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final Call<ArrayList<Video>> call, final Throwable t) {
                t.printStackTrace();
            }
        });
    }

    void setupExamples() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.exampleList.setLayoutManager(layoutManager);

        mHomeAdapter = new HomeAdapter(mExampleListList, getContext(), (VideoClickListener) getActivity());
        binding.exampleList.setAdapter(mHomeAdapter);
    }

    void setupGridExamples() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        binding.exampleList.setLayoutManager(layoutManager);
        binding.exampleList.addItemDecoration(new ProfileGridDecoration(20, 2));

        mHomeGridAdapter = new HomeGridAdapter(mExampleListList, getContext(),
                (VideoClickListener) getActivity());
        binding.exampleList.setAdapter(mHomeGridAdapter);
    }
}