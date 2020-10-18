package com.examplesonly.android.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.examplesonly.android.R;
import com.examplesonly.android.databinding.ActivityMainBinding;
import com.examplesonly.android.ui.fragment.ExploreFragment;
import com.examplesonly.android.ui.fragment.HomeFragment;
import com.examplesonly.android.ui.fragment.NewEoSheetFragment;
import com.examplesonly.android.ui.fragment.NotificationFragment;
import com.examplesonly.android.ui.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getCanonicalName();
    private ActivityMainBinding binding;
    private FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Log.e(TAG, item.getTitle().toString());

            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                switchFragment(itemId, "Home");
//                setFragment(new HomeFragment(), R.id.frame_container);
            } else if (itemId == R.id.explore) {
                switchFragment(itemId, "Explore");
//                setFragment(new ExploreFragment(), R.id.frame_container);
            } else if (itemId == R.id.add) {
                NewEoSheetFragment bottomSheet = new NewEoSheetFragment();
                bottomSheet.show(getSupportFragmentManager(),
                        "ModalBottomSheet");
            } else if (itemId == R.id.notification) {
                switchFragment(itemId, "Notification");
//                setFragment(new NotificationFragment(), R.id.frame_container);
            } else if (itemId == R.id.profile) {
                switchFragment(itemId, "Profile");
//                setFragment(new ProfileFragment(), R.id.frame_container);
            }

            return item.getItemId() != R.id.add;
        });

        setFragment(new HomeFragment(), R.id.frame_container);

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
}