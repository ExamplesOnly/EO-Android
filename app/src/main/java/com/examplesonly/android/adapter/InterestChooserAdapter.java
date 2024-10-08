package com.examplesonly.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.examplesonly.android.databinding.ViewChooseCategoryBinding;
import com.examplesonly.android.model.Category;

import java.util.ArrayList;
import java.util.List;

public class InterestChooserAdapter extends Adapter<InterestChooserAdapter.VIewHolder> {

    private final List<Category> categories;
    private final Context context;
    private final LayoutInflater inflater;
    private final CategorySelectionListener mCategorySelectionListener;
    private final ArrayList<Category> preSelectedCategories = new ArrayList<>();

    public InterestChooserAdapter(List<Category> mList, Context context,
                                  CategorySelectionListener mCategorySelectionListener) {
        this.categories = mList;
        this.context = context;
        this.mCategorySelectionListener = mCategorySelectionListener;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public VIewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new VIewHolder(ViewChooseCategoryBinding.inflate(inflater));
    }

    @Override
    public void onBindViewHolder(@NonNull final VIewHolder holder, final int position) {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class VIewHolder extends RecyclerView.ViewHolder {

        ViewChooseCategoryBinding mChooseCategoryBinding;

        public VIewHolder(@NonNull final ViewChooseCategoryBinding itemView) {
            super(itemView.getRoot());
            mChooseCategoryBinding = itemView;
        }

        void bind(Category category) {
            Glide.with(context).load(category.getThumbUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(mChooseCategoryBinding.thumbImage);
            mChooseCategoryBinding.title.setText(category.getTitle());
            mChooseCategoryBinding.imageCard.setChecked(category.isSelected());
            mChooseCategoryBinding.imageCard.setOnClickListener(v ->
                    mCategorySelectionListener.onCategorySelection(categories.indexOf(category)));
            mChooseCategoryBinding.parent.setOnClickListener(v ->
                    mCategorySelectionListener.onCategorySelection(categories.indexOf(category)));
        }
    }

    public interface CategorySelectionListener {

        void onCategorySelection(int index);
    }
}
