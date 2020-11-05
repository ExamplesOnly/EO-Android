package com.examplesonly.android.ui.activity;

import static com.examplesonly.android.ui.activity.VideoPlayerActivity.VIDEO_LINK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.examplesonly.android.R;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.adapter.HomeAdapter.VideoClickListener;
import com.examplesonly.android.databinding.ActivityMainBinding;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.user.UserInterface;
import com.examplesonly.android.network.video.VideoInterface;
import com.examplesonly.android.ui.fragment.ExploreFragment;
import com.examplesonly.android.ui.fragment.HomeFragment;
import com.examplesonly.android.ui.fragment.NewEoSheetFragment;
import com.examplesonly.android.ui.fragment.NotificationFragment;
import com.examplesonly.android.ui.fragment.ProfileFragment;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements VideoClickListener {

    private final String TAG = MainActivity.class.getCanonicalName();
    private ActivityMainBinding binding;
    private FragmentManager fm = getSupportFragmentManager();
    private UserInterface mUserInterface;
    private VideoInterface videoInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
        getVideos();

        getUserData();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Log.e(TAG, item.getTitle().toString());

            int itemId = item.getItemId();
            if (itemId == R.id.home) {
//                switchFragment(itemId, "Home");
                setFragment(new HomeFragment(), R.id.frame_container);
            } else if (itemId == R.id.explore) {
//                switchFragment(itemId, "Explore");
                setFragment(new ExploreFragment(), R.id.frame_container);
            } else if (itemId == R.id.add) {
                NewEoSheetFragment bottomSheet = new NewEoSheetFragment();
                bottomSheet.show(getSupportFragmentManager(),
                        "ModalBottomSheet");
            } else if (itemId == R.id.notification) {
//                switchFragment(itemId, "Notification");
                setFragment(new NotificationFragment(), R.id.frame_container);
            } else if (itemId == R.id.profile) {
//                switchFragment(itemId, "Profile");
                setFragment(new ProfileFragment(), R.id.frame_container);
            }

            return item.getItemId() != R.id.add;
        });

        setFragment(new HomeFragment(), R.id.frame_container);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!new UserDataProvider(this).isAuthorized()) {
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(login);
            finish();
        }
    }

    @Override
    public void onVideoClicked(final String url) {
        Log.e("Home", url);

        Intent videoPlayer = new Intent(MainActivity.this, VideoPlayerActivity.class);
        videoPlayer.putExtra(VIDEO_LINK, url);
        startActivity(videoPlayer);

    }

    private void init() {
        mUserInterface = new Api(this).getClient().create(UserInterface.class);
        videoInterface = new Api(this).getClient().create(VideoInterface.class);
    }

    private void getVideos() {
        videoInterface.getVideos().enqueue(new Callback<ArrayList<Video>>() {
            @Override
            public void onResponse(final Call<ArrayList<Video>> call, final Response<ArrayList<Video>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Video> videos = response.body();
                    for (int i = 0; i < videos.size() - 1; i++) {
                        Log.e("VIDEO ", "Title " + videos.get(0).getTitle());
                    }
                }
            }

            @Override
            public void onFailure(final Call<ArrayList<Video>> call, final Throwable t) {

            }
        });
    }

    public void setFragment(Fragment frag, int parentView) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(parentView, frag).commit();
    }

    private void switchFragment(int itemId, String pageTag) {
//        if(pageTag == fm.)
        Fragment fragment;
        fragment = fm.findFragmentByTag(pageTag);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        if (itemId == R.id.home) {
            if (fragment instanceof HomeFragment) {
                fm.popBackStack(pageTag, 0);
                return;
            }
            if (fragment == null) {
                fragment = new HomeFragment();
            }
        } else if (itemId == R.id.explore) {
            if (fragment instanceof ExploreFragment) {
                fm.popBackStack(pageTag, 0);
                return;
            }
            if (fragment == null) {
                fragment = new ExploreFragment();
            }
        } else if (itemId == R.id.notification) {
            if (fragment instanceof NotificationFragment) {
                fm.popBackStack(pageTag, 0);
                return;
            }
            if (fragment == null) {
                fragment = new NotificationFragment();
            }
        } else if (itemId == R.id.profile) {
            if (fragment instanceof ProfileFragment) {

                return;
            }
            if (fragment == null) {
                fragment = new ProfileFragment();
            }
        }

        fragmentTransaction.replace(R.id.frame_container, fragment, pageTag);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getUserData() {
//        mUserInterface.me(email);
    }
}