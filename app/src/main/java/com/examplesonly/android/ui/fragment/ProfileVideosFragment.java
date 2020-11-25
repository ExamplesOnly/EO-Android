package com.examplesonly.android.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.examplesonly.android.adapter.ProfileVideosAdapter;
import com.examplesonly.android.databinding.FragmentProfileVideosBinding;
import com.examplesonly.android.handler.VideoClickListener;
import com.examplesonly.android.model.User;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.user.UserInterface;
import com.examplesonly.android.ui.view.ProfileGridDecoration;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileVideosFragment extends Fragment {

    private FragmentProfileVideosBinding binding;
    private static final String ARG_USER = "user";

    private User user;
    private UserInterface mUserInterface;
    private ProfileVideosAdapter profileVideosAdapter;
    private final ArrayList<Video> mExampleListList = new ArrayList<>();

    public static ProfileVideosFragment newInstance(User user) {
        ProfileVideosFragment fragment = new ProfileVideosFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileVideosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_USER);
        }
        mUserInterface = new Api(getContext()).getClient().create(UserInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileVideosBinding.inflate(inflater, container, false);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        binding.exampleList.setLayoutManager(layoutManager);
        binding.exampleList.addItemDecoration(new ProfileGridDecoration(10, 2));

        profileVideosAdapter = new ProfileVideosAdapter(mExampleListList, getContext(),
                (VideoClickListener) getActivity());
        binding.exampleList.setAdapter(profileVideosAdapter);

        updateVideos();

        return binding.getRoot();
    }

    void updateVideos() {
        mUserInterface.myVideos().enqueue(new Callback<ArrayList<Video>>() {
            @Override
            public void onResponse(final @NotNull Call<ArrayList<Video>> call,
                    final @NotNull Response<ArrayList<Video>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mExampleListList.clear();
                    mExampleListList.addAll(response.body());
                    profileVideosAdapter.notifyDataSetChanged();
                } else {
                }
            }

            @Override
            public void onFailure(final Call<ArrayList<Video>> call, final Throwable t) {
                Log.e("PROFILE", "FAIL");
            }
        });
    }
}