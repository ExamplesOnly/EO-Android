package com.examplesonly.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.examplesonly.android.adapter.ExploreTabAdapter;
import com.examplesonly.android.databinding.FragmentExploreBinding;
import com.examplesonly.android.model.Category;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.category.CategoryInterface;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    private CategoryInterface mCategoryInterface;
    private FragmentPagerItemAdapter fragmentAdapter;
    private ExploreTabAdapter mExploreTabAdapter;
    private final ArrayList<Category> categoryList = new ArrayList<>();

    public ExploreFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        mCategoryInterface = new Api(getContext()).getClient().create(CategoryInterface.class);

        mExploreTabAdapter = new ExploreTabAdapter(getChildFragmentManager(), categoryList);
        binding.viewpager.setAdapter(mExploreTabAdapter);
        binding.tabs.setViewPager(binding.viewpager);

        updateList();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    void updateList() {
        mCategoryInterface.getCategories().enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(final @NotNull Call<ArrayList<Category>> call,
                    final @NotNull Response<ArrayList<Category>> response) {
                if (response.isSuccessful() && categoryList.size() == 0) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    mExploreTabAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(final Call<ArrayList<Category>> call, final Throwable t) {

            }
        });
    }
}