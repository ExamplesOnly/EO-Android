package com.examplesonly.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.examplesonly.android.adapter.ExampleAdapter;
import com.examplesonly.android.adapter.HomeGridAdapter;
import com.examplesonly.android.databinding.FragmentHomeBinding;
import com.examplesonly.android.handler.VideoClickListener;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.video.VideoInterface;
import com.examplesonly.android.ui.view.ProfileGridDecoration;
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

        binding.noInternet.setVisibility(View.GONE);

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

                binding.exampleList.setVisibility(View.VISIBLE);
                binding.noInternet.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Timber.e("HOME isSuccessful");
                    ArrayList<Video> videos = response.body();
                    mExampleListList.clear();
                    mExampleListList.addAll(videos);
                    mHomeGridAdapter.notifyDataSetChanged();
                } else {
                    Timber.e("HOME error");
                    try {
                        Timber.e(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final @NotNull Call<ArrayList<Video>> call, final @NotNull Throwable t) {
                t.printStackTrace();
                if (t instanceof IOException) {
                    binding.exampleList.setVisibility(View.GONE);
                    binding.noInternet.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    void setupGridExamples() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        binding.exampleList.setLayoutManager(layoutManager);
        binding.exampleList.addItemDecoration(new ProfileGridDecoration(20, 2));

        mHomeGridAdapter = new HomeGridAdapter(mExampleListList, getActivity(),
                (VideoClickListener) getActivity());
        binding.exampleList.setAdapter(mHomeGridAdapter);
    }
}