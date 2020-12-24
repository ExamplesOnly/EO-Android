package com.examplesonly.android.ui.fragment;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
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

    private FragmentProfileBinding binding;
    private ProfileVideosAdapter profileVideosAdapter;
    private UserDataProvider userDataProvider;
    private UserInterface mUserInterface;
    private String interests = "";
    private final ArrayList<Video> mExampleListList = new ArrayList<>();

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
        updateProfile();

        binding.esmIcon.setImageDrawable(
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_esm_1, getActivity().getTheme()));
        binding.eoiIcon.setImageDrawable(
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_eoi_1, getActivity().getTheme()));

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProfile();
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

        binding.settings.setOnClickListener(view -> {
            Intent settings = new Intent(getActivity(), SettingsActivity.class);
            startActivity(settings);
        });

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        binding.exampleList.setLayoutManager(layoutManager);
        binding.exampleList.addItemDecoration(new ProfileGridDecoration(10, 2));

        profileVideosAdapter = new ProfileVideosAdapter(mExampleListList, getContext(),
                (VideoClickListener) getActivity());
        binding.exampleList.setAdapter(profileVideosAdapter);

        List<Fragment> profileFragments = new Vector<Fragment>();
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

        binding.statParent.setVisibility(View.GONE);
        binding.exampleList.setVisibility(View.VISIBLE);

        binding.clipsTab.setOnClickListener(view -> {
            binding.statParent.setVisibility(View.GONE);
            binding.exampleList.setVisibility(View.VISIBLE);
            binding.clipSelected.setBackground(
                    ResourcesCompat.getDrawable(getResources(), R.color.md_grey_600, getActivity().getTheme()));
            binding.statSelected.setBackground(
                    ResourcesCompat.getDrawable(getResources(), R.color.transparent, getActivity().getTheme()));
        });
        binding.statsTab.setOnClickListener(view -> {
            binding.statParent.setVisibility(View.VISIBLE);
            binding.exampleList.setVisibility(View.GONE);
            binding.statSelected.setBackground(
                    ResourcesCompat.getDrawable(getResources(), R.color.md_grey_600, getActivity().getTheme()));
            binding.clipSelected.setBackground(
                    ResourcesCompat.getDrawable(getResources(), R.color.transparent, getActivity().getTheme()));
        });
    }

    void updateProfile() {
        if (currentUser.getUuid().equals(userDataProvider.getCurrentUser().getUuid())) {
            currentUser = userDataProvider.getCurrentUser();

            binding.profileActionButton
                    .setBackgroundColor(getResources().getColor(R.color.md_grey_100, getContext().getTheme()));
            binding.profileActionButton
                    .setStrokeColorResource(R.color.md_grey_500);
            binding.profileActionButton
                    .setText(R.string.edit_profile);
            binding.profileActionButton
                    .setTextColor(getResources().getColor(R.color.dark, getContext().getTheme()));
            binding.profileActionButton.setStrokeWidth(4);
            binding.profileActionButton.setOnClickListener(view -> {
                Intent editProfile = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(editProfile);
            });
//
//            mUserInterface.myVideos().enqueue(new Callback<ArrayList<Video>>() {
//                @Override
//                public void onResponse(final @NotNull Call<ArrayList<Video>> call,
//                        final @NotNull Response<ArrayList<Video>> response) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        mExampleListList.clear();
//                        mExampleListList.addAll(response.body());
//                        profileVideosAdapter.notifyDataSetChanged();
//                    } else {
//                    }
//                }
//
//                @Override
//                public void onFailure(final Call<ArrayList<Video>> call, final Throwable t) {
//                    Log.e("PROFILE", "FAIL");
//                }
//            });
//
//            mUserInterface.getInterest().enqueue(new Callback<ArrayList<Category>>() {
//                @Override
//                public void onResponse(final @NotNull Call<ArrayList<Category>> call,
//                        final @NotNull Response<ArrayList<Category>> response) {
//                    if (response.isSuccessful()) {
//
//                        Timber.e("getInterest");
//                        ArrayList<Category> interestList = response.body();
//
//                        if (interestList.size() > 0) {
//                            interests = "";
//                            for (int i = 0; i < interestList.size(); i++) {
//                                Timber.e(interestList.get(i).getTitle());
//                                if (interests.length() > 0) {
//                                    interests += ", ";
//                                }
//                                interests += interestList.get(i).getTitle();
//                            }
//                            binding.interest.setText(interests);
//                            binding.interest.setVisibility(View.VISIBLE);
//                        }
//
//                    } else {
//                        try {
//                            Timber.e("getInterest ERROR %s", response.errorBody().string());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(final @NotNull Call<ArrayList<Category>> call, final @NotNull Throwable t) {
//                    t.printStackTrace();
//                    Timber.e("getInterest onFailure");
//                }
//            });
        } else {

        }

        mUserInterface.getUserProfile(currentUser.getUuid()).enqueue(new Callback<User>() {
                @Override
                public void onResponse(final @NotNull Call<User> call, final @NotNull Response<User> response) {
                    if (response.isSuccessful()) {
                        mExampleListList.clear();
                        mExampleListList.addAll(response.body().getVideos());
                        profileVideosAdapter.notifyDataSetChanged();

                        Glide.with(getActivity())
                                .load(response.body().getCoverPhoto())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.color.md_grey_200)
                                .transition(withCrossFade(factory))
                                .into(binding.coverImage);
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
                    Timber.e(t.getMessage());
                }
            });

        binding.name.setText(currentUser.getFirstName());
        binding.bio.setText(currentUser.getBio());

        Glide.with(Objects.requireNonNull(getActivity()))
                .load(currentUser.getProfilePhoto())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_user)
                .transition(withCrossFade(factory))
                .into(binding.profileImage);

        Glide.with(Objects.requireNonNull(getActivity()))
                .load(currentUser.getCoverPhoto())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.color.md_grey_200)
                .transition(withCrossFade(factory))
                .into(binding.coverImage);
    }


}