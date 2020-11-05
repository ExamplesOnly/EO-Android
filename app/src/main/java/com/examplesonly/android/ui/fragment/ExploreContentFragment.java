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
import com.examplesonly.android.databinding.FragmentExploreContentBinding;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.category.CategoryInterface;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreContentFragment extends Fragment {

    private final String TAG = ExploreContentFragment.class.getCanonicalName();
    private static final String ARG_SLUG = "slug";
    private FragmentExploreContentBinding binding;
    private CategoryInterface categoryInterface;
    private HomeAdapter mHomeAdapter;
    private ArrayList<Video> videoList = new ArrayList<>();

    private String slug;

    public static ExploreContentFragment newInstance(String slug) {
        ExploreContentFragment fragment = new ExploreContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SLUG, slug);
        fragment.setArguments(args);
        return fragment;
    }

    public ExploreContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            slug = getArguments().getString(ARG_SLUG);
            Log.e(TAG, "SLUG " + slug);
        }

        categoryInterface = new Api(getContext()).getClient().create(CategoryInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = com.examplesonly.android.databinding.FragmentExploreContentBinding
                .inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        setupExamples();

        categoryInterface.getVideos(slug).enqueue(new Callback<ArrayList<Video>>() {
            @Override
            public void onResponse(final Call<ArrayList<Video>> call, final Response<ArrayList<Video>> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "REQ SUCCESS " + response.body());
                    videoList = response.body();

                    for(Video video: videoList) {
                        Log.e(TAG, video.getTitle());
                    }

                    mHomeAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "REQ ERROR " + response.errorBody());
                }
            }

            @Override
            public void onFailure(final Call<ArrayList<Video>> call, final Throwable t) {

                t.printStackTrace();
                Log.e(TAG, "REQ FAIL " + t.getMessage());
            }
        });

        return view;
    }

    void setupExamples() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.exampleList.setLayoutManager(layoutManager);

        mHomeAdapter = new HomeAdapter(videoList, getContext(), (VideoClickListener) getActivity());
        binding.exampleList.setAdapter(mHomeAdapter);
    }
}