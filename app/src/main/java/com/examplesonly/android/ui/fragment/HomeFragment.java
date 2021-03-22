package com.examplesonly.android.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.examplesonly.android.adapter.ViewPagerAdapter;
import com.examplesonly.android.databinding.FragmentHomeBinding;
import com.examplesonly.android.ui.view.ProfileGridDecoration;

import timber.log.Timber;

import static com.examplesonly.android.ui.fragment.FeedFragmentKt.FEED_TYPE_RECENT;
import static com.examplesonly.android.ui.fragment.FeedFragmentKt.FEED_TYPE_TRENDING;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ViewPagerAdapter fragmentPagerAdapter;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        binding.tab.setupWithViewPager(binding.viewpager);
        fragmentPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        fragmentPagerAdapter.addFrag(FeedFragment.newInstance(FEED_TYPE_RECENT), "For You");
        fragmentPagerAdapter.addFrag(FeedFragment.newInstance(FEED_TYPE_TRENDING), "Trending");
        binding.viewpager.setAdapter(fragmentPagerAdapter);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}