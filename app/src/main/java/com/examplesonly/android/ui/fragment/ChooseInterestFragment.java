package com.examplesonly.android.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;

import com.examplesonly.android.R;
import com.examplesonly.android.adapter.InterestChooserAdapter;
import com.examplesonly.android.databinding.FragmentChooseInterestBinding;
import com.examplesonly.android.model.Category;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.category.CategoryInterface;
import com.examplesonly.android.network.user.UserInterface;
import com.examplesonly.android.ui.activity.ChooseCategoryActivity;
import com.examplesonly.android.ui.activity.MainActivity;
import com.examplesonly.android.ui.view.CategoryGridItemDecoration;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ChooseInterestFragment extends Fragment implements InterestChooserAdapter.CategorySelectionListener {

    private static final String ARG_SELECTED_CATEGORIES = "selected_categories";

    private FragmentChooseInterestBinding binding;
    private final List<Category> categories = new ArrayList<>();
    private final ArrayList<Category> selectedCategories = new ArrayList<>();
    private final ArrayList<Category> preSelectedCategories = new ArrayList<>();
    private InterestChooserAdapter adapter;
    private UserInterface userInterface;
    private CategoryInterface mCategoryInterface;

    private int selectCount = 0;

    public ChooseInterestFragment() {
        // Required empty public constructor
    }

    public static ChooseInterestFragment newInstance(String selectedCategories) {
        ChooseInterestFragment fragment = new ChooseInterestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SELECTED_CATEGORIES, selectedCategories);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            preSelectedCategories.addAll(getArguments().getParcelableArrayList(ARG_SELECTED_CATEGORIES));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChooseInterestBinding.inflate(inflater);
        binding.noInternet.isOffline(false);
        binding.spinner.setVisibility(View.VISIBLE);

        mCategoryInterface = new Api(requireContext()).getClient().create(CategoryInterface.class);
        userInterface = new Api(requireContext()).getClient().create(UserInterface.class);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        binding.categoryList.setLayoutManager(layoutManager);
        binding.categoryList.addItemDecoration(new CategoryGridItemDecoration(10, 2));

        adapter = new InterestChooserAdapter(categories, requireContext(), this);
        binding.categoryList.setAdapter(adapter);

        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                250);
        animate.setDuration(0);
        animate.setFillAfter(true);
        binding.next.startAnimation(animate);

        binding.noInternet.onRefreshListener(v -> {
            binding.noInternet.isOffline(false);
            binding.spinner.setVisibility(View.VISIBLE);
            updateCategoryList();
        });

        updateCategoryList();

        return binding.getRoot();
    }

    private void updateCategoryList() {
        mCategoryInterface.getCategories().enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(@NotNull final Call<ArrayList<Category>> call,
                                   @NotNull final Response<ArrayList<Category>> response) {

                binding.spinner.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    binding.noInternet.isOffline(false);
                    categories.clear();

                    for (int i = 0; i < response.body().size(); i++) {
                        Category category = response.body().get(i);
                        if (preSelectedCategories.contains(category)) {
                            category.setSelected(true);
                        }
                        categories.add(category);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    binding.noInternet.isOffline(true);
                    try {
                        Timber.e(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull final Call<ArrayList<Category>> call, final Throwable t) {
                binding.noInternet.isOffline(true);
                binding.spinner.setVisibility(View.GONE);
                Timber.e("updateCategoryList: Fail");
            }
        });
    }

    @Override
    public void onCategorySelection(int index) {
        Timber.e("selected %s", index);
        Category category = categories.get(index);
        categories.set(index, category.setSelected(!category.isSelected()));
        adapter.notifyItemChanged(index);
        updateButton();
    }


    void updateButton() {
        int selected = 0;
        TranslateAnimation animate = null;
        selectedCategories.clear();
        for (Category category : categories) {
            if (category.isSelected()) {
                selected++;
                selectedCategories.add(category);
            }
        }

        if (selected > 0 && selectCount == 0) {
            animate = new TranslateAnimation(
                    0,
                    0,
                    250,
                    0);
            animate.setInterpolator(new FastOutSlowInInterpolator());
            animate.setDuration(600);
        } else if (selected == 0) {
            animate = new TranslateAnimation(
                    0,
                    0,
                    0,
                    250);
            animate.setInterpolator(new FastOutSlowInInterpolator());
            animate.setDuration(300);
        }

        if (animate != null) {
            animate.setFillAfter(true);
            binding.next.startAnimation(animate);
        }

        selectCount = selected;

        switch (selectCount) {
            case 0:
            case 1:
                binding.next.setBackgroundColor(getResources().getColor(R.color.md_grey_600, requireActivity().getTheme()));
                binding.next.setText(R.string.one_more_to_go);
                binding.next.setIcon(null);
                binding.next.setOnClickListener(null);
                break;
            default:
                binding.next.setBackgroundColor(getResources().getColor(R.color.colorPrimary, requireActivity().getTheme()));
                binding.next.setText(R.string.get_started);
                binding.next.setIconResource(R.drawable.ic_arrow_right);
                binding.next.setOnClickListener(view -> {
                    binding.next.setOnClickListener(null);
                    binding.next.setText(R.string.updating);
                    binding.next.setIcon(null);
                    String categoryValue = buildCategoryRequest(selectedCategories);
                    Timber.e("categoryValue %s", categoryValue);
                    userInterface.updateInterest(categoryValue).enqueue(new Callback<HashMap<String, String>>() {
                        @Override
                        public void onResponse(final Call<HashMap<String, String>> call,
                                               final Response<HashMap<String, String>> response) {
                            if (response.isSuccessful()) {
                                Intent main = new Intent(requireActivity(), MainActivity.class);
                                startActivity(main);
                                requireActivity().finish();
                            } else {
                                binding.next.setBackgroundColor(getResources().getColor(R.color.colorPrimary, requireActivity().getTheme()));
                                binding.next.setText(R.string.get_started);
                                binding.next.setIconResource(R.drawable.ic_arrow_right);
                                Timber.e("Category Update error");
                            }
                        }

                        @Override
                        public void onFailure(final Call<HashMap<String, String>> call, final Throwable t) {
                            Timber.e("Category Update fail %s", t.getMessage());
                            binding.next.setBackgroundColor(getResources().getColor(R.color.colorPrimary, requireActivity().getTheme()));
                            binding.next.setText(R.string.get_started);
                            binding.next.setIconResource(R.drawable.ic_arrow_right);
                        }
                    });
                });
                break;
        }
    }

    String buildCategoryRequest(List<Category> categoryList) {
        StringBuilder start = new StringBuilder("{ \"categories\": [");
        String end = "]}";

        for (int i = 0; i < categoryList.size(); i++) {
            if (i == 0) {
                start.append("\"").append(categoryList.get(i).getId()).append("\"");
            } else {
                start.append(",\"").append(categoryList.get(i).getId()).append("\"");
            }
        }
        start.append(end);
        return start.toString();
    }
}