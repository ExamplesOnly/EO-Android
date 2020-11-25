package com.examplesonly.android.ui.fragment;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Intent;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.examplesonly.android.R;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.adapter.ProfileVideosAdapter;
import com.examplesonly.android.databinding.FragmentProfileBinding;
import com.examplesonly.android.handler.VideoClickListener;
import com.examplesonly.android.model.Category;
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
import java.util.Timer;
import java.util.TimerTask;
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
    private int currentEsmIcon, currentEoiIcon;
    private final ArrayList<Video> mExampleListList = new ArrayList<>();
    private final ArrayList<Drawable> esmDrawableList = new ArrayList<>();
    private final ArrayList<Drawable> eoiDrawableList = new ArrayList<>();

    DrawableCrossFadeFactory factory =
            new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

    private User user;

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
            user = getArguments().getParcelable(ARG_USER);
            Timber.e("FName %s", user.getFirstName());
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

        currentEsmIcon = 0;
        currentEoiIcon = 0;
        esmDrawableList.add(getResources().getDrawable(R.drawable.ic_esm_1));
        esmDrawableList.add(getResources().getDrawable(R.drawable.ic_esm_2));
        esmDrawableList.add(getResources().getDrawable(R.drawable.ic_esm_3));
        esmDrawableList.add(getResources().getDrawable(R.drawable.ic_esm_4));
        esmDrawableList.add(getResources().getDrawable(R.drawable.ic_esm_5));

        eoiDrawableList.add(getResources().getDrawable(R.drawable.ic_eoi_1));
        eoiDrawableList.add(getResources().getDrawable(R.drawable.ic_eoi_2));
        eoiDrawableList.add(getResources().getDrawable(R.drawable.ic_eoi_3));
        eoiDrawableList.add(getResources().getDrawable(R.drawable.ic_eoi_4));
        eoiDrawableList.add(getResources().getDrawable(R.drawable.ic_eoi_5));

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                binding.esmIcon.setImageDrawable(esmDrawableList.get(currentEsmIcon));
                currentEsmIcon++;
                if (currentEsmIcon == 5) {
                    currentEsmIcon = 0;
                }
            }
        }, 2000, 2000);

        Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                binding.eoiIcon.setImageDrawable(eoiDrawableList.get(currentEoiIcon));
                currentEoiIcon++;
                if (currentEoiIcon == 5) {
                    currentEoiIcon = 0;
                }
            }
        }, 2000, 2000);

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
        profileFragments.add(ProfileVideosFragment.newInstance(user));
        profileFragments.add(ProfileStatsFragment.newInstance(user));

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

        binding.contentPager.setAdapter(pagerAdapter);
        binding.contentPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset,
                    final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                updateTab();
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });
        binding.clipsTab.setOnClickListener(view -> {
            binding.contentPager.setCurrentItem(0);
            binding.clipSelected.setBackground(getResources().getDrawable(R.color.md_grey_600));
            binding.statSelected.setBackground(getResources().getDrawable(R.color.transparent));
        });
        binding.statsTab.setOnClickListener(view -> {
            binding.contentPager.setCurrentItem(1);
            binding.statSelected.setBackground(getResources().getDrawable(R.color.md_grey_600));
            binding.clipSelected.setBackground(getResources().getDrawable(R.color.transparent));
        });
    }

    void updateTab() {
        if (binding.contentPager.getCurrentItem() == 0) {
            binding.clipSelected.setBackground(getResources().getDrawable(R.color.md_grey_600));
            binding.statSelected.setBackground(getResources().getDrawable(R.color.transparent));
        } else if (binding.contentPager.getCurrentItem() == 1) {
            binding.statSelected.setBackground(getResources().getDrawable(R.color.md_grey_600));
            binding.clipSelected.setBackground(getResources().getDrawable(R.color.transparent));
        }
    }

    void updateProfile() {
        if (user.getUuid().equals(userDataProvider.getCurrentUser().getUuid())) {
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

            binding.name
                    .setText(getResources().getString(R.string.user_full_name, userDataProvider.getUserFirstName(),
                            userDataProvider.getUserLastName()));
            binding.bio.setText(userDataProvider.getUserBio());

            Glide.with(Objects.requireNonNull(getActivity()))
                    .load(userDataProvider.getUserProfileImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.md_grey_400)
                    .transition(withCrossFade(factory))
                    .into(binding.profileImage);

            Glide.with(Objects.requireNonNull(getActivity()))
                    .load(userDataProvider.getUserCoverImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.md_grey_200)
                    .transition(withCrossFade(factory))
                    .into(binding.coverImage);

            mUserInterface.getInterest().enqueue(new Callback<ArrayList<Category>>() {
                @Override
                public void onResponse(final Call<ArrayList<Category>> call,
                        final Response<ArrayList<Category>> response) {
                    if (response.isSuccessful()) {

                        Timber.e("getInterest");
                        ArrayList<Category> interestList = response.body();

                        if (interestList.size() > 0) {
                            interests = "";
                            for (int i = 0; i < interestList.size(); i++) {
                                Timber.e(interestList.get(i).getTitle());
                                if (interests.length() > 0) {
                                    interests += ", ";
                                }
                                interests += interestList.get(i).getTitle();
                            }
                            binding.interest.setText(interests);
                            binding.interest.setVisibility(View.VISIBLE);
                        }

                    } else {
                        try {
                            Timber.e("getInterest ERROR %s", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(final Call<ArrayList<Category>> call, final Throwable t) {

                    Timber.e("getInterest onFailure");
                }
            });

        } else {
            binding.name.setText(getResources().getString(R.string.user_full_name, user.getFirstName(),
                    user.getLastName()));
        }

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