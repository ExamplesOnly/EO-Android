package com.examplesonly.android.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.examplesonly.android.R;
import com.examplesonly.android.adapter.ViewPagerAdapter;
import com.examplesonly.android.databinding.FragmentHomeBinding;
import com.examplesonly.android.databinding.FragmentSearchBinding;

import static com.examplesonly.android.ui.fragment.FeedFragmentKt.FEED_TYPE_RECENT;
import static com.examplesonly.android.ui.fragment.FeedFragmentKt.FEED_TYPE_TRENDING;

public class SearchFragment extends Fragment {


    private FragmentSearchBinding binding;
    private ViewPagerAdapter fragmentPagerAdapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        binding.tab.setupWithViewPager(binding.viewpager);
        fragmentPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        fragmentPagerAdapter.addFrag(new SearchListFragment(), "Examples");
        fragmentPagerAdapter.addFrag(new SearchListFragment(), "Creators");
        binding.viewpager.setAdapter(fragmentPagerAdapter);


        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}