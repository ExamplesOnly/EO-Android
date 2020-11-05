package com.examplesonly.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.examplesonly.android.ui.view.CategoryGridItemDecoration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseCategoryActivity extends AppCompatActivity implements CategorySelectionListener {

    private ActivityChooseCategoryBinding binding;
    private InterestChooserAdapter adapter;
    private final List<Category> categories = new ArrayList<>();
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
        Log.e("ChooseCategory", "selected " + index);
        Category category = categories.get(index);
        categories.set(index, category.setSelected(!category.isSelected()));
        adapter.notifyItemChanged(index);
        updateButton();
    }

    void init() {
        mCategoryInterface = new Api(this).getClient().create(CategoryInterface.class);

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
                        Log.e("ChooseCategory",
                                response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull final Call<ArrayList<Category>> call, final Throwable t) {
                Log.e("ChooseCategory", "Fail");
            }
        });
    }

    void updateButton() {
        int selected = 0;
        TranslateAnimation animate = null;

        for (Category category : categories) {
            if (category.isSelected()) {
                selected++;
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
                    Intent main = new Intent(ChooseCategoryActivity.this, MainActivity.class);
                    startActivity(main);
                    finish();
                });
                break;
        }
    }
}