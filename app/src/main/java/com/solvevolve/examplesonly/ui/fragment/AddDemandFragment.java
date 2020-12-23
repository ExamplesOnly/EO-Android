package com.solvevolve.examplesonly.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.solvevolve.examplesonly.R;
import com.solvevolve.examplesonly.adapter.CategoryListAdapter;
import com.solvevolve.examplesonly.databinding.FragmentAddDemandBinding;
import com.solvevolve.examplesonly.model.Category;
import com.solvevolve.examplesonly.model.Demand;
import com.solvevolve.examplesonly.network.Api;
import com.solvevolve.examplesonly.network.category.CategoryInterface;
import com.solvevolve.examplesonly.network.demand.DemandInterface;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AddDemandFragment extends Fragment {

    private FragmentAddDemandBinding binding;
    private DemandInterface demandInterface;
    private CategoryInterface categoryInterface;
    private final ArrayList<Category> categoryList = new ArrayList<>();

    public AddDemandFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        demandInterface = new Api(getContext()).getClient().create(DemandInterface.class);
        categoryInterface = new Api(getContext()).getClient().create(CategoryInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentAddDemandBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        init();

        return view;
    }

    void init() {
        binding.categoryTxt.setEnabled(false);
        binding.categoryTextField.setEnabled(false);

        binding.topAppBar.setNavigationOnClickListener(view1 -> {
            Objects.requireNonNull(getActivity()).onBackPressed();
        });

        binding.topAppBar.setOnMenuItemClickListener(menu -> {
            if (menu.getItemId() == R.id.publish) {

                if (Objects.requireNonNull(binding.demandTitleTxt.getText()).toString().length() == 0) {
                    Toast.makeText(getContext(), "Add a title", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (Objects.requireNonNull(binding.demandDescTxt.getText()).toString().length() == 0) {
                    Toast.makeText(getContext(), "Add a description", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (binding.categoryTxt.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Select a category", Toast.LENGTH_SHORT).show();
                    return true;
                }

                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.create();
                progressDialog.setProgress(0);
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                postDemand(binding.demandTitleTxt.getText().toString(),
                        binding.demandDescTxt.getText().toString(),
                        getCategoryIdFromTitle(binding.categoryTxt.getText().toString()),
                        progressDialog);
            }
            return true;
        });

        categoryInterface.getCategories().enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(final Call<ArrayList<Category>> call,
                    final Response<ArrayList<Category>> response) {
                if (response.isSuccessful()) {

                    binding.categoryTxt.setEnabled(true);
                    binding.categoryTextField.setEnabled(true);

                    categoryList.addAll(response.body());
                    binding.categoryTxt.setAdapter(
                            new CategoryListAdapter(Objects.requireNonNull(getContext()),
                                    R.layout.view_dropdown_list_item,
                                    categoryList));

                }
            }

            @Override
            public void onFailure(final Call<ArrayList<Category>> call, final Throwable t) {

            }
        });
    }

    void postDemand(String title, String description, int category, ProgressDialog progressDialog) {
        demandInterface.addDemand(
                new Demand()
                        .setTitle(title)
                        .setDescription(description)
                        .setCategoryId(category)).enqueue(new Callback<Demand>() {
            @Override
            public void onResponse(final Call<Demand> call, final Response<Demand> response) {
                if (response.isSuccessful()) {
                    Demand demand = response.body();
                    Timber.e("Demand Added: %s", demand.getTitle());
                    getActivity().finish();
                } else {
                    try {
                        Timber.e("Demand Add error: %s", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(final Call<Demand> call, final Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
                Timber.e("Demand Add failed: %s", t.getMessage());
            }
        });
    }

    int getCategoryIdFromTitle(String title) {
        int i = 0;
        int catId = -1;
        for (i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getTitle().equals(title)) {
                catId = categoryList.get(i).getId();
            }
        }
        return catId;
    }
}