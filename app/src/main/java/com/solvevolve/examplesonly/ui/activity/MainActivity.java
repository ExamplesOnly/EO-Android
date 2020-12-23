package com.solvevolve.examplesonly.ui.activity;

import static com.solvevolve.examplesonly.ui.activity.NewEoActivity.ARG_DEMAND;
import static com.solvevolve.examplesonly.ui.activity.NewEoActivity.ARG_LAUNCH_MODE;
import static com.solvevolve.examplesonly.ui.activity.NewEoActivity.FRAGMENT_CAMERA;
import static com.solvevolve.examplesonly.ui.activity.NewEoActivity.FRAGMENT_CHOOSE_VIDEO;
import static com.solvevolve.examplesonly.ui.activity.NewEoActivity.FRAGMENT_NEW_DEMAND;
import static com.solvevolve.examplesonly.ui.activity.VideoPlayerActivity.VIDEO_DATA;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.solvevolve.examplesonly.R;
import com.solvevolve.examplesonly.account.UserDataProvider;
import com.solvevolve.examplesonly.component.BottomSheetOptionsDialog;
import com.solvevolve.examplesonly.component.BottomSheetOptionsDialog.BottomSheetOptionChooseListener;
import com.solvevolve.examplesonly.databinding.ActivityMainBinding;
import com.solvevolve.examplesonly.handler.FragmentChangeListener;
import com.solvevolve.examplesonly.handler.VideoClickListener;
import com.solvevolve.examplesonly.model.BottomSheetOption;
import com.solvevolve.examplesonly.model.Demand;
import com.solvevolve.examplesonly.model.User;
import com.solvevolve.examplesonly.model.Video;
import com.solvevolve.examplesonly.network.Api;
import com.solvevolve.examplesonly.network.user.UserInterface;
import com.solvevolve.examplesonly.network.video.VideoInterface;
import com.solvevolve.examplesonly.ui.fragment.DemandDetailsFragment;
import com.solvevolve.examplesonly.ui.fragment.DemandFragment;
import com.solvevolve.examplesonly.ui.fragment.ExploreFragment;
import com.solvevolve.examplesonly.ui.fragment.HomeFragment;
import com.solvevolve.examplesonly.ui.fragment.ProfileFragment;
import com.ncapdevi.fragnav.FragNavController;
import com.ncapdevi.fragnav.FragNavController.RootFragmentListener;
import com.ncapdevi.fragnav.tabhistory.UniqueTabHistoryStrategy;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MainActivity extends AppCompatActivity
        implements VideoClickListener, RootFragmentListener, BottomSheetOptionChooseListener, FragmentChangeListener,
        FragNavController.TransactionListener {

    private final int INDEX_HOME = 0;
    private final int INDEX_EXPLORE = 1;
    private final int INDEX_EOD = 2;
    public static final int INDEX_PROFILE = 3;
    public static final int INDEX_DEMAND_DETAILS = 4;

    private final int OPTION_CHOOSE_VIDEO = 101;
    private final int OPTION_RECORD_VIDEO = 102;
    private final int OPTION_CREATE_EOD = 103;
    public static final int OPTION_CHOOSE_VIDEO_EOD = 104;
    public static final int OPTION_RECORD_VIDEO_EOD = 105;

    private ActivityMainBinding binding;
    private FragmentManager fm = getSupportFragmentManager();
    private UserInterface userInterface;
    private VideoInterface videoInterface;
    private UserDataProvider userDataProvider;
    private FragNavController fragNavController;
    private final ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.topAppBar);
        getSupportActionBar().setTitle("");
        binding.topAppBar.setNavigationOnClickListener(view1 -> {
            if (!fragNavController.popFragment()) {
                super.onBackPressed();
            }
        });

        init();
        setupFragments();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                fragNavController.switchTab(INDEX_HOME);
            } else if (itemId == R.id.explore) {
                fragNavController.switchTab(INDEX_EXPLORE);
            } else if (itemId == R.id.add) {
                ArrayList<BottomSheetOption> optionList = new ArrayList<>();
                optionList.add(new BottomSheetOption(OPTION_CHOOSE_VIDEO, "Upload from device",
                        ContextCompat.getDrawable(this, R.drawable.ic_upload_mono)));
                optionList.add(new BottomSheetOption(OPTION_RECORD_VIDEO, "Record a video",
                        ContextCompat.getDrawable(this, R.drawable.ic_camera_mono)));
                optionList.add(new BottomSheetOption(OPTION_CREATE_EOD, "Request an Example",
                        ContextCompat.getDrawable(this, R.drawable.ic_video_question_mono)));
                BottomSheetOptionsDialog bottomSheet = new BottomSheetOptionsDialog("Create", optionList);
                bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
            } else if (itemId == R.id.ask_eo) {
                fragNavController.switchTab(INDEX_EOD);
            } else if (itemId == R.id.profile) {
                fragNavController.switchTab(INDEX_PROFILE);
            }
            return item.getItemId() != R.id.add;
        });

        binding.bottomNavigation.setOnNavigationItemReselectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId != R.id.add) {
                fragNavController.clearStack();
            }
        });

        fragNavController.switchTab(INDEX_HOME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!userDataProvider.isAuthorized()) {
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(login);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!fragNavController.popFragment()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState,
                                    @NonNull final PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        fragNavController.onSaveInstanceState(outState);
    }

    @Override
    public int getNumberOfRootFragments() {
        return fragments.size();
    }

    @NotNull
    @Override
    public Fragment getRootFragment(final int i) {
        switch (i) {
            case INDEX_HOME:
                return new HomeFragment();
            case INDEX_EXPLORE:
                return new ExploreFragment();
            case INDEX_EOD:
                return new DemandFragment();
            case INDEX_PROFILE:
                return ProfileFragment.newInstance(userDataProvider.getCurrentUser());
        }

        return null;
    }

    @Override
    public void onFragmentTransaction(@Nullable final Fragment fragment,
                                      @NotNull final FragNavController.TransactionType transactionType) {
        updateToolBar();
    }

    @Override
    public void onTabTransaction(@Nullable final Fragment fragment, final int i) {
        updateToolBar();
    }

    @Override
    public void onBottomSheetOptionChosen(final int index, final int id, final Object data) {
        switch (id) {
            case OPTION_CHOOSE_VIDEO:
                launchNewEo(FRAGMENT_CHOOSE_VIDEO, null);
                break;
            case OPTION_RECORD_VIDEO:
                launchNewEo(FRAGMENT_CAMERA, null);
                break;
            case OPTION_CREATE_EOD:
                launchNewEo(FRAGMENT_NEW_DEMAND, null);
                break;
            case OPTION_CHOOSE_VIDEO_EOD:
                launchNewEo(FRAGMENT_CHOOSE_VIDEO, (Demand) data);
                break;
            case OPTION_RECORD_VIDEO_EOD:
                launchNewEo(FRAGMENT_CAMERA, (Demand) data);
                break;
        }
    }

    @Override
    public void onVideoClicked(final Video video) {
        Intent videoPlayer = new Intent(MainActivity.this, VideoPlayerActivity.class);
//        Intent videoPlayer = new Intent(MainActivity.this, VideoSwipeActivity.class);
        videoPlayer.putExtra(VIDEO_DATA, video);

//        Pair<View, String> p1 = Pair.create(findViewById(R.id.thumbnail), "thumbnail");
//        Pair<View, String> p2 = Pair.create(vPalette, "palette");
//        Pair<View, String> p3 = Pair.create((View) tvName, "text");
//        ActivityOptionsCompat options = ActivityOptionsCompat.
//                makeSceneTransitionAnimation(this, p1);
//        startActivity(videoPlayer, options.toBundle());

        startActivity(videoPlayer);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.notification) {
            Intent notification = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(notification);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void switchFragment(final int fragmentId, Object data) {
        switch (fragmentId) {
            case INDEX_DEMAND_DETAILS:
                fragNavController.pushFragment(DemandDetailsFragment.newInstance((Demand) data));
                break;
            case INDEX_PROFILE:
                fragNavController.pushFragment(ProfileFragment.newInstance((User) data));
                break;
        }
    }

    private void init() {
        userInterface = new Api(this).getClient().create(UserInterface.class);
        videoInterface = new Api(this).getClient().create(VideoInterface.class);

        userDataProvider = UserDataProvider.getInstance(this);
        fragNavController = new FragNavController(fm, R.id.frame_container);
        fragNavController.setTransactionListener(this);
//        fragNavController.setDefaultTransactionOptions(new Builder()
//                .customAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left,
//                        R.anim.slide_out_to_right).build());
        fragNavController.setNavigationStrategy(new UniqueTabHistoryStrategy((i, fragNavTransactionOptions) -> {
        }));
    }

    private void setupFragments() {
        fragments.add(new HomeFragment());
        fragments.add(new ExploreFragment());
        fragments.add(new DemandFragment());
        fragments.add(ProfileFragment.newInstance(userDataProvider.getCurrentUser()));

        fragNavController.setRootFragments(fragments);
        fragNavController.initialize(FragNavController.TAB3, null);
    }

    void launchNewEo(int mode, Demand demand) {
        Intent intent = new Intent(this, NewEoActivity.class);
        Bundle b = new Bundle();
        b.putInt(ARG_LAUNCH_MODE, mode);
        b.putParcelable(ARG_DEMAND, demand);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void getUserData() {
//        mUserInterface.me(email);
    }

    private void updateToolBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(!fragNavController.isRootFragment());
        if (fragNavController.isRootFragment()) {
            binding.eoTitle.setVisibility(View.VISIBLE);
        } else {
            binding.eoTitle.setVisibility(View.GONE);
        }
    }
}