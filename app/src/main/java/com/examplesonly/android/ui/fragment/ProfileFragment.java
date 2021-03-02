package com.examplesonly.android.ui.fragment;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.examplesonly.android.R;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.adapter.ProfileVideosAdapter;
import com.examplesonly.android.databinding.FragmentProfileBinding;
import com.examplesonly.android.handler.VideoClickListener;
import com.examplesonly.android.model.User;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.user.UserInterface;
import com.examplesonly.android.ui.activity.EditProfileActivity;
import com.examplesonly.android.ui.activity.SettingsActivity;
import com.examplesonly.android.ui.view.ProfileGridDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ProfileFragment extends Fragment {

    private static final String ARG_USER = "user";
    private static final int TAB_USER_VIDEOS = 1;
    private static final int TAB_SAVED_VIDEOS = 2;

    Activity parentActivity;
    private FragmentProfileBinding binding;
    private ProfileVideosAdapter profileVideosAdapter, savedVideosAdapter;
    private UserDataProvider userDataProvider;
    private UserInterface mUserInterface;
    private String interests = "";
    private int currentTab = TAB_USER_VIDEOS;
    private boolean exampleLoaded = false, saveLoaded = false;
    private final ArrayList<Video> mExampleList = new ArrayList<>();
    private final ArrayList<Video> mSaveList = new ArrayList<>();

    DrawableCrossFadeFactory factory =
            new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

    private User currentUser;

    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = getActivity();

        if (getArguments() != null) {
            currentUser = getArguments().getParcelable(ARG_USER);
        } else {
            throw new RuntimeException("User data not found");
        }
        mUserInterface = new Api(getContext()).getClient().create(UserInterface.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        init();
        updateProfile(currentUser, true);


        mUserInterface.getUserProfile(currentUser.getUuid()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(final @NotNull Call<User> call, final @NotNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    updateProfile(currentUser, false);
                } else {
                    try {
                        Timber.e(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final @NotNull Call<User> call, final @NotNull Throwable t) {
                t.printStackTrace();
                Timber.e("getUserProfile onFailure");
                Timber.e(t);
            }
        });

        binding.esmIcon.setImageDrawable(
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_esm_4, requireActivity().getTheme()));
        binding.eoiIcon.setImageDrawable(
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_eye_3, requireActivity().getTheme()));

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
//        updateProfile(currentUser);
    }

    void init() {
        userDataProvider = UserDataProvider.getInstance(getContext());

        binding.coverImage.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(final View view, final Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), (int) (view.getHeight() + 40F), 40F);
            }
        });
        binding.coverImage.setClipToOutline(true);

        StaggeredGridLayoutManager exampleLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);

        StaggeredGridLayoutManager savedLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);

        binding.exampleList.setLayoutManager(exampleLayoutManager);
        binding.exampleList.addItemDecoration(new ProfileGridDecoration(10, 2));

        profileVideosAdapter = new ProfileVideosAdapter(mExampleList, getContext(),
                (VideoClickListener) getActivity());
        profileVideosAdapter.setIsLoggedInUser(currentUser.getUuid().equals(userDataProvider.getUserUuid()));
        binding.exampleList.setAdapter(profileVideosAdapter);

        binding.saveList.setLayoutManager(savedLayoutManager);
        binding.saveList.addItemDecoration(new ProfileGridDecoration(10, 2));

        savedVideosAdapter = new ProfileVideosAdapter(mSaveList, getContext(),
                (VideoClickListener) getActivity());
        savedVideosAdapter.setIsLoggedInUser(false);
        binding.saveList.setAdapter(savedVideosAdapter);

        List<Fragment> profileFragments = new Vector<>();
        profileFragments.add(ProfileVideosFragment.newInstance(currentUser));
        profileFragments.add(ProfileStatsFragment.newInstance(currentUser));

        PagerAdapter pagerAdapter = new FragmentPagerAdapter(getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public int getCount() {
                return profileFragments.size();
            }

            @NonNull
            @Override
            public Fragment getItem(final int position) {
                return profileFragments.get(position);
            }
        };

//        binding.statParent.setVisibility(View.GONE);
        binding.exampleList.setVisibility(View.VISIBLE);

        binding.clipsTab.setOnClickListener(view -> {
            currentTab = TAB_USER_VIDEOS;

            binding.saveList.setVisibility(View.GONE);

            if (exampleLoaded && mExampleList.size() == 0) {
                binding.noExamples.setVisibility(View.VISIBLE);
            } else {
                binding.exampleList.setVisibility(View.VISIBLE);
                binding.noExamples.setVisibility(View.GONE);
            }

            binding.clipSelected.setBackground(
                    ResourcesCompat.getDrawable(getResources(), R.color.md_grey_600, requireActivity().getTheme()));
            binding.statSelected.setBackground(
                    ResourcesCompat.getDrawable(getResources(), R.color.transparent, requireActivity().getTheme()));
        });
        binding.statsTab.setOnClickListener(view -> {
            currentTab = TAB_SAVED_VIDEOS;

            binding.exampleList.setVisibility(View.GONE);

            if (saveLoaded && mSaveList.size() == 0) {
                binding.noExamples.setVisibility(View.VISIBLE);
            } else {
                binding.saveList.setVisibility(View.VISIBLE);
                binding.noExamples.setVisibility(View.GONE);
            }

            binding.statSelected.setBackground(
                    ResourcesCompat.getDrawable(getResources(), R.color.md_grey_600, requireActivity().getTheme()));
            binding.clipSelected.setBackground(
                    ResourcesCompat.getDrawable(getResources(), R.color.transparent, requireActivity().getTheme()));
        });
    }

    void updateProfile(User userProfile, boolean isLoading) {
        binding.saveList.setVisibility(View.GONE);
        binding.noExamples.setVisibility(View.GONE);

        if (isLoading) {
            setButtonActionActive(false);
            binding.profileActionButton.setText("Loading");
        } else {
            updateActionButton(userProfile);
        }

        mExampleList.clear();
        mExampleList.addAll(userProfile.getVideos());
        profileVideosAdapter.notifyDataSetChanged();

        if (parentActivity != null && isAdded())
            Glide.with(requireActivity())
                    .load(userProfile.getCoverPhoto())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.md_grey_200)
                    .transition(withCrossFade(factory))
                    .into(binding.coverImage);
        exampleLoaded = true;

        if (mExampleList.size() == 0 && currentTab == TAB_USER_VIDEOS) {
            binding.noExamples.setVisibility(View.VISIBLE);
        }

        binding.name.setText(userProfile.getFirstName());
        binding.bio.setText(userProfile.getBio());

        if (parentActivity != null && isAdded()) {
            Glide.with(requireActivity())
                    .load(userProfile.getProfilePhoto())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_user)
                    .transition(withCrossFade(factory))
                    .into(binding.profileImage);

            Glide.with(requireActivity())
                    .load(userProfile.getCoverPhoto())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.md_grey_200)
                    .transition(withCrossFade(factory))
                    .into(binding.coverImage);
        }

        updateVideos();
    }

    void updateActionButton(User userProfile) {

        // profile is of logged in user
        if (userProfile.getUuid().equals(userDataProvider.getCurrentUser().getUuid())) {
            currentUser = userDataProvider.getCurrentUser();

            setButtonActionActive(false);
            binding.profileActionButton
                    .setText(R.string.edit_profile);

            binding.profileActionButton.setOnClickListener(view -> {
                Intent editProfile = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(editProfile);
            });
        }

        // profile is of other user
        else {
            // if current user is following this user
            if (userProfile.isFollowing()) {
                setButtonActionActive(false);
                binding.profileActionButton
                        .setText(R.string.following);

                // UnFollow the user
                binding.profileActionButton.setOnClickListener(view -> unFollowUser(userProfile));
            }
            // this user is following current user
            else if (userProfile.isFollowedBy()) {
                setButtonActionActive(true);
                binding.profileActionButton
                        .setText(R.string.follow_back);

                // Follow the user
                binding.profileActionButton.setOnClickListener(view -> followUser(userProfile));
            }
            // Nothing. Ask the current user to follow this user
            else {
                binding.profileActionButton
                        .setText(R.string.follow);
                setButtonActionActive(true);

                // Follow the user
                binding.profileActionButton.setOnClickListener(view -> followUser(userProfile));
            }
            binding.statsTab.setVisibility(View.GONE);
        }
    }

    void setButtonActionActive(boolean isActive) {
        if (isActive) {
            binding.profileActionButton
                    .setBackgroundColor(getResources().getColor(R.color.colorPrimary, requireContext().getTheme()));
            binding.profileActionButton
                    .setTextColor(getResources().getColor(R.color.white, requireContext().getTheme()));
            binding.profileActionButton.setStrokeWidth(0);
        } else {
            binding.profileActionButton
                    .setBackgroundColor(getResources().getColor(R.color.md_grey_100, requireContext().getTheme()));
            binding.profileActionButton
                    .setStrokeColorResource(R.color.md_grey_500);
            binding.profileActionButton
                    .setText(R.string.edit_profile);
            binding.profileActionButton
                    .setTextColor(getResources().getColor(R.color.dark, requireContext().getTheme()));
            binding.profileActionButton.setStrokeWidth(4);
        }
    }

    void unFollowUser(User userProfile) {
        currentUser.setFollowing(false);
        updateActionButton(currentUser);
        mUserInterface.unFollowUser(userProfile.getUuid())
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                        if (!response.isSuccessful()) {
                            currentUser.setFollowing(true);
                            updateActionButton(currentUser);
                            Toast.makeText(requireContext(), "Couldn't unfollow " + userProfile.getFirstName(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                        currentUser.setFollowing(true);
                        updateActionButton(currentUser);
                        Toast.makeText(requireContext(), "Couldn't unfollow " + userProfile.getFirstName(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    void followUser(User userProfile) {
        currentUser.setFollowing(true);
        updateActionButton(currentUser);
        mUserInterface.followUser(userProfile.getUuid())
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                        if (!response.isSuccessful()) {
                            currentUser.setFollowing(false);
                            updateActionButton(currentUser);
                            Toast.makeText(requireContext(), "Couldn't follow " + userProfile.getFirstName(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                        currentUser.setFollowing(true);
                        updateActionButton(currentUser);
                        Toast.makeText(requireContext(), "Couldn't follow " + userProfile.getFirstName(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    void updateVideos() {
        mUserInterface.getBookmarks().enqueue(new Callback<ArrayList<Video>>() {
            @Override
            public void onResponse(final @NotNull Call<ArrayList<Video>> call,
                                   final @NotNull Response<ArrayList<Video>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mSaveList.clear();
                    mSaveList.addAll(response.body());
                    savedVideosAdapter.notifyDataSetChanged();
                    saveLoaded = true;

                    if (mSaveList.size() == 0 && currentTab == TAB_SAVED_VIDEOS) {
                        binding.noExamples.setVisibility(View.VISIBLE);
                    }
                } else {
                }
            }

            @Override
            public void onFailure(final Call<ArrayList<Video>> call, final Throwable t) {
                Timber.e("FAIL");
            }
        });
    }

}