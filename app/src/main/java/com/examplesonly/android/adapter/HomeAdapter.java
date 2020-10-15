package com.examplesonly.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.examplesonly.android.databinding.ExampleItemOneBinding;
import com.examplesonly.android.model.Example;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private static final int VIEW_TYPE_EXAMPLE_FULL = 0;

    private List<Example> mList;
    private Context context;
    LayoutInflater inflater;

    public HomeAdapter(List<Example> mList, Context context) {
        this.mList = mList;
        this.context = context;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {

        View v = null;

        switch (viewType) {
            case VIEW_TYPE_EXAMPLE_FULL:
                return new ViewHolder(ExampleItemOneBinding.inflate(inflater, parent, false));
            default:
                return new ViewHolder(ExampleItemOneBinding.inflate(inflater, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemViewType(final int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ExampleItemOneBinding mItemOneBinding;

        public ViewHolder(@NonNull final ExampleItemOneBinding itemView) {
            super(itemView.getRoot());
            mItemOneBinding = itemView;
        }

        void bind(Example example) {
            Glide.with(context).load(example.getThumbUrl()).into(mItemOneBinding.thumbnail);
            Glide.with(context).load(example.getUser().getProfilePhoto()).into(mItemOneBinding.profileImage);
            mItemOneBinding.title.setText(example.getTitle());
        }
    }
}
