package com.examplesonly.android.ui.activity;

import static com.examplesonly.android.ui.activity.NewEoActivity.ARG_DEMAND;
import static com.examplesonly.android.ui.activity.NewEoActivity.ARG_LAUNCH_MODE;
import static com.examplesonly.android.ui.activity.NewEoActivity.FRAGMENT_CAMERA;
import static com.examplesonly.android.ui.activity.NewEoActivity.FRAGMENT_CHOOSE_VIDEO;
import static com.examplesonly.android.ui.activity.NewEoActivity.FRAGMENT_NEW_DEMAND;
import static com.examplesonly.android.ui.activity.VideoPlayerActivity.VIDEO_DATA;
import static com.examplesonly.android.ui.activity.VideoPlayerActivity.VIDEO_DATA_TYPE;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.examplesonly.android.BuildConfig;
import com.examplesonly.android.R;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.component.BottomSheetOptionsDialog;
import com.examplesonly.android.component.BottomSheetOptionsDialog.BottomSheetOptionChooseListener;
import com.examplesonly.android.databinding.ActivityMainBinding;
import com.examplesonly.android.handler.FeedClickListener;
import com.examplesonly.android.handler.FragmentChangeListener;
import com.examplesonly.android.handler.VideoClickListener;
import com.examplesonly.android.model.BottomSheetOption;
import com.examplesonly.android.model.Demand;
import com.examplesonly.android.model.User;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.user.UserInterface;
import com.examplesonly.android.network.video.VideoInterface;
import com.examplesonly.android.ui.fragment.DemandDetailsFragment;
import com.examplesonly.android.ui.fragment.DemandFragment;
import com.examplesonly.android.ui.fragment.ExploreFragment;
import com.examplesonly.android.ui.fragment.FeedFragment;
import com.examplesonly.android.ui.fragment.HomeFragment;
import com.examplesonly.android.ui.fragment.NotificationFragment;
import com.examplesonly.android.ui.fragment.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ncapdevi.fragnav.FragNavController;
import com.ncapdevi.fragnav.FragNavController.RootFragmentListener;
import com.ncapdevi.fragnav.tabhistory.UniqueTabHistoryStrategy;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements VideoClickListener, RootFragmentListener, BottomSheetOptionChooseListener, FragmentChangeListener,
        FragNavController.TransactionListener {

    private static final int UPDATE_REQUEST_CODE = 1010;
    private static final int INDEX_HOME = 0;
    //  private static final int INDEX_EXPLORE = 1;
    private static final int INDEX_EOD = 1;
    public static final int INDEX_NOTIFICATION = 2;
    public static final int INDEX_PROFILE = 3;
    public static final int INDEX_DEMAND_DETAILS = 4;

    private final int OPTION_CHOOSE_VIDEO = 101;
    private final int OPTION_RECORD_VIDEO = 102;
    private final int OPTION_CREATE_EOD = 103;
    public static final int OPTION_CHOOSE_VIDEO_EOD = 104;
    public static final int OPTION_RECORD_VIDEO_EOD = 105;

    private ActivityMainBinding binding;
    private final FragmentManager fm = getSupportFragmentManager();
    private UserDataProvider userDataProvider;
    private FragNavController fragNavController;
    private final ArrayList<Fragment> fragments = new ArrayList<>();

    InstallStateUpdatedListener updateListener;
    AppUpdateManager appUpdateManager;

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

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Timber.e(task.getException(), "Fetching FCM registration token failed");
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();


                    Timber.e("FCM TOKEN %s", token);
                }).addOnFailureListener(e -> Timber.e("FCM FAIL %s", e.getMessage()));

        init();
        setupFragments();
        checkUpdate();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                fragNavController.switchTab(INDEX_HOME);

            }
//            else if (itemId == R.id.explore) {
//                fragNavController.switchTab(INDEX_EXPLORE);
//            }
            else if (itemId == R.id.ask_eo) {
                fragNavController.switchTab(INDEX_EOD);
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
            } else if (itemId == R.id.notification) {
                fragNavController.switchTab(INDEX_NOTIFICATION);
            } else if (itemId == R.id.profile) {
                fragNavController.switchTab(INDEX_PROFILE);
            }

            invalidateOptionsMenu();
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

        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(appUpdateInfo -> {
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdate();
                    }
                });
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
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Timber.e("Update failed");
            }
        }
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
//            case INDEX_EXPLORE:
//                return new ExploreFragment();
            case INDEX_EOD:
                return new DemandFragment();
            case INDEX_NOTIFICATION:
                return new NotificationFragment();
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
        startActivity(videoPlayer);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        MenuItem settingsMenu = menu.findItem(R.id.settings);
        MenuItem notificationMenu = menu.findItem(R.id.notification);

        if (fragNavController.getCurrentFrag() instanceof ProfileFragment) {
            settingsMenu.setVisible(true);
            notificationMenu.setVisible(false);
        } else {
            settingsMenu.setVisible(false);
            notificationMenu.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.notification) {
            Intent notification = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(notification);
        } else if (itemId == R.id.settings) {
            Intent notification = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(notification);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
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
        userDataProvider = UserDataProvider.getInstance(this);
        appUpdateManager = AppUpdateManagerFactory.create(this);

        fragNavController = new FragNavController(fm, R.id.frame_container);
        fragNavController.setTransactionListener(this);
        fragNavController.setNavigationStrategy(new UniqueTabHistoryStrategy((i, fragNavTransactionOptions) -> {
        }));
    }

    private void setupFragments() {
        fragments.add(new HomeFragment());
//        fragments.add(new ExploreFragment());
        fragments.add(new DemandFragment());
        fragments.add(new NotificationFragment());
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

            if (fragNavController.getCurrentFrag() instanceof HomeFragment) {
                binding.eoTitle.setVisibility(View.VISIBLE);
                binding.pageTitle.setText("");
            } else if (fragNavController.getCurrentFrag() instanceof DemandFragment) {
                binding.eoTitle.setVisibility(View.GONE);
                binding.pageTitle.setText("Questions");
            } else if (fragNavController.getCurrentFrag() instanceof NotificationFragment) {
                binding.eoTitle.setVisibility(View.GONE);
                binding.pageTitle.setText("Notifications");
            } else {
                binding.eoTitle.setVisibility(View.VISIBLE);
                binding.pageTitle.setText("");
            }
        } else {
            binding.eoTitle.setVisibility(View.GONE);
        }
    }

    // check for in app update
    private void checkUpdate() {

        updateListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
                appUpdateManager.unregisterListener(updateListener);
            }
        };


        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateManager.registerListener(updateListener);

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // Request the update.

                Timber.e("%s %s", BuildConfig.VERSION_CODE, appUpdateInfo.availableVersionCode());

                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            } else {
                Timber.e("MAIN: Update not available");
            }
        });
    }

//    private void validateToken() {
//        String token = userDataProvider.getToken();
//        try {
//            DecodedJWT jwt = JWT.decode(token);
//            if (jwt.getExpiresAt().before(new Date())) {
//                EoAlertDialog logoutDialog = new EoAlertDialog(MainActivity.this)
//                        .setDialogIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_sign_in_alt, getTheme()))
//                        .setIconTint(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getTheme()))
//                        .setTitle("Session Expired")
//                        .setDescription("Your login session has expired. Please login again to keep enjoying the excitement!")
//                        .setPositiveText("Login")
//                        .setPositiveClickListener(dialog -> {
//                            dialog.dismiss();
//                            userDataProvider.clearUserData();
//                            Intent reLaunch = new Intent(MainActivity.this, LaunchActivity.class);
//                            startActivity(reLaunch);
//                            this.finish();
//
//                        });
//
//                logoutDialog.setCancelable(false);
//                logoutDialog.show();
//            }
//        } catch (Exception exception) {
//
////            if(exception instanceof JWTDecodeException)
//            //Invalid token
//        }
//    }

    void popupSnackbarForCompleteUpdate() {
        Snackbar updateSnackBar = Snackbar.make(binding.getRoot(),
                "A new update of ExamplesOnly is downloaded.", BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setAction("Install", v -> appUpdateManager.completeUpdate());

        updateSnackBar.setAnchorView(binding.bottomNavigation);

        updateSnackBar.show();
    }
}