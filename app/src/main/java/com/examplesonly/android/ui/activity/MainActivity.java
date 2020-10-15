package com.examplesonly.android.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
                setFragment(new HomeFragment(), R.id.frame_container);
            } else if (itemId == R.id.explore) {
                setFragment(new ExploreFragment(), R.id.frame_container);
            } else if (itemId == R.id.add) {
                NewEoSheetFragment bottomSheet = new NewEoSheetFragment();
                bottomSheet.show(getSupportFragmentManager(),
                        "ModalBottomSheet");
            } else if (itemId == R.id.notification) {
                setFragment(new NotificationFragment(), R.id.frame_container);
            } else if (itemId == R.id.profile) {
                setFragment(new ProfileFragment(), R.id.frame_container);
            }

            return item.getItemId() != R.id.add;
        });

        setFragment(new HomeFragment(), R.id.frame_container);

    }

    public void setFragment(Fragment frag, int parentView) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(parentView, frag).commit();

    }
}