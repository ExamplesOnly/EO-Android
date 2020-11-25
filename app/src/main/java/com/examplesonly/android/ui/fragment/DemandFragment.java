package com.examplesonly.android.ui.fragment;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.examplesonly.android.adapter.ViewPagerAdapter;
import com.examplesonly.android.databinding.FragmentDemandBinding;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class DemandFragment extends Fragment {

    FragmentDemandBinding binding;
    ViewPagerAdapter fragmentPagerAdapter;

    public DemandFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentDemandBinding.inflate(inflater, container, false);

        binding.tab.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(final Tab tab) {

            }

            @Override
            public void onTabUnselected(final Tab tab) {

            }

            @Override
            public void onTabReselected(final Tab tab) {

            }
        });
        binding.tab.setupWithViewPager(binding.viewpager);

        fragmentPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        fragmentPagerAdapter.addFrag(new DemandListFragment(), "For you");
        fragmentPagerAdapter.addFrag(new DemandRequestsFragment(), "Questions");
//        fragmentPagerAdapter.addFrag(new DemandBookmarkFragment(), "Bookmark");
        binding.viewpager.setAdapter(fragmentPagerAdapter);

        return binding.getRoot();
    }
}