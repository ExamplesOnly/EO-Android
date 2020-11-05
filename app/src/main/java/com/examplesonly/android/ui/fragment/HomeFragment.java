package com.examplesonly.android.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.examplesonly.android.adapter.HomeAdapter;
import com.examplesonly.android.adapter.HomeAdapter.VideoClickListener;
import com.examplesonly.android.databinding.FragmentHomeBinding;
import com.examplesonly.android.model.User;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.user.UserInterface;
import com.examplesonly.android.network.video.VideoInterface;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeAdapter mHomeAdapter;
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

        setupExamples();
        getVideos();
//        setDummyData();

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getVideos();
    }

    void getVideos() {
        Log.e("Home", "getVideos");
        videoInterface.getVideos().enqueue(new Callback<ArrayList<Video>>() {
            @Override
            public void onResponse(final Call<ArrayList<Video>> call, final Response<ArrayList<Video>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Video> videos = response.body();
                    mExampleListList.clear();
                    for (int i = 0; i < videos.size(); i++) {
                        mExampleListList.add(videos.get(i));
                        mHomeAdapter.notifyDataSetChanged();
                    }
                } else {
                    try {
                        Log.e("Home", response.errorBody().string());
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
}