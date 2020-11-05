package com.examplesonly.android.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.examplesonly.android.model.Category;
import com.examplesonly.android.ui.fragment.ExploreContentFragment;
import java.util.ArrayList;
public class ExploreTabAdapter extends FragmentPagerAdapter {

    private ArrayList<Category> videoList;

    public ExploreTabAdapter(@NonNull final FragmentManager fm, final int behavior) {
        super(fm, behavior);
    }

    public ExploreTabAdapter(@NonNull final FragmentManager fm, ArrayList<Category> videos) {
        super(fm);
        this.videoList = videos;
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @NonNull
    @Override
    public Fragment getItem(final int position) {
        return ExploreContentFragment.newInstance(videoList.get(position).getSlug());
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(final int position) {
//        return super.getPageTitle(position);
        return videoList.get(position).getTitle();
    }
}
