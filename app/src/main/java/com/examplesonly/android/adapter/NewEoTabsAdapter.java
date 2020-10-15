package com.examplesonly.android.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.examplesonly.android.ui.fragment.NewCameraFragment;
import com.examplesonly.android.ui.fragment.NewGalleryFragment;

public class NewEoTabsAdapter extends FragmentStateAdapter {

    public NewEoTabsAdapter(@NonNull final FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(final int position) {
        switch (position) {
            case 0:
                return new NewGalleryFragment();
            case 1:
                return new NewCameraFragment();
            default:
                return new NewGalleryFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
