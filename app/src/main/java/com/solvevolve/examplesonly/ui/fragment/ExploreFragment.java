package com.solvevolve.examplesonly.ui.fragment;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.solvevolve.examplesonly.adapter.ExploreTabAdapter;
import com.solvevolve.examplesonly.databinding.FragmentExploreBinding;
import com.solvevolve.examplesonly.model.Category;
import com.solvevolve.examplesonly.network.Api;
import com.solvevolve.examplesonly.network.category.CategoryInterface;
import java.io.IOException;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    private CategoryInterface mCategoryInterface;
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

        binding.noInternet.setVisibility(View.GONE);

        mCategoryInterface = new Api(getContext()).getClient().create(CategoryInterface.class);
        mExploreTabAdapter = new ExploreTabAdapter(getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                categoryList);
        binding.viewpager.setAdapter(mExploreTabAdapter);
        binding.tab.setupWithViewPager(binding.viewpager);

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

                binding.tab.setVisibility(View.VISIBLE);
                binding.viewpager.setVisibility(View.VISIBLE);
                binding.noInternet.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && categoryList.size() == 0) {
                    Timber.e("isSuccessful");
                    categoryList.addAll(response.body());
                    mExploreTabAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(final @NotNull Call<ArrayList<Category>> call, final @NotNull Throwable t) {
                if (t instanceof IOException) {
                    binding.tab.setVisibility(View.GONE);
                    binding.viewpager.setVisibility(View.GONE);
                    binding.noInternet.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}