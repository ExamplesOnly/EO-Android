package com.examplesonly.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.TranslateAnimation;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.examplesonly.android.R;
import com.examplesonly.android.adapter.InterestChooserAdapter;
import com.examplesonly.android.adapter.InterestChooserAdapter.CategorySelectionListener;
import com.examplesonly.android.databinding.ActivityChooseCategoryBinding;
import com.examplesonly.android.model.Category;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.category.CategoryInterface;
import com.examplesonly.android.network.user.UserInterface;
import com.examplesonly.android.ui.view.CategoryGridItemDecoration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ChooseCategoryActivity extends AppCompatActivity implements CategorySelectionListener {

    private ActivityChooseCategoryBinding binding;
    private InterestChooserAdapter adapter;
    private UserInterface userInterface;
    private final List<Category> categories = new ArrayList<>();
    private final List<Category> selectedCategories = new ArrayList<>();
    CategoryInterface mCategoryInterface;
    private int selectCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setCategories();

    }

    @Override
    public void onCategorySelection(final int index) {
        Timber.e("selected %s", index);
        Category category = categories.get(index);
        categories.set(index, category.setSelected(!category.isSelected()));
        adapter.notifyItemChanged(index);
        updateButton();
    }

    void init() {
        mCategoryInterface = new Api(this).getClient().create(CategoryInterface.class);
        userInterface = new Api(this).getClient().create(UserInterface.class);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        binding.categoryList.setLayoutManager(layoutManager);
        binding.categoryList.addItemDecoration(new CategoryGridItemDecoration(10, 2));

        adapter = new InterestChooserAdapter(categories, this, this);
        binding.categoryList.setAdapter(adapter);

        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                250);
        animate.setDuration(0);
        animate.setFillAfter(true);
        binding.next.startAnimation(animate);
    }

    void setCategories() {
        mCategoryInterface.getCategories().enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(@NotNull final Call<ArrayList<Category>> call,
                    @NotNull final Response<ArrayList<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.clear();
                    categories.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    try {
                        Timber.e(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull final Call<ArrayList<Category>> call, final Throwable t) {
                Timber.e("Fail");
            }
        });
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
                binding.next.setBackgroundColor(getResources().getColor(R.color.md_grey_600, getTheme()));
                binding.next.setText("1 more to go");
                binding.next.setIcon(null);
                binding.next.setOnClickListener(null);
                break;
            default:
                binding.next.setBackgroundColor(getResources().getColor(R.color.colorPrimary, getTheme()));
                binding.next.setText("Get Started");
                binding.next.setIconResource(R.drawable.ic_arrow_right);
                binding.next.setOnClickListener(view -> {
                    binding.next.setEnabled(false);
                    String categoryValue = buildCategoryRequest(selectedCategories);
                    Timber.e("categoryValue %s", categoryValue);
                    userInterface.updateInterest(categoryValue).enqueue(new Callback<HashMap<String, String>>() {
                        @Override
                        public void onResponse(final Call<HashMap<String, String>> call,
                                final Response<HashMap<String, String>> response) {
                            if (response.isSuccessful()) {
                                Intent main = new Intent(ChooseCategoryActivity.this, MainActivity.class);
                                startActivity(main);
                                finish();
                            } else {
                                Timber.e("Category Update error");
                            }
                        }

                        @Override
                        public void onFailure(final Call<HashMap<String, String>> call, final Throwable t) {
                            Timber.e("Category Update fail %s", t.getMessage());
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