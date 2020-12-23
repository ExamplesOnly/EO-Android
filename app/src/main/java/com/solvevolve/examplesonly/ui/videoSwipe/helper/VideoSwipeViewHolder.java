package com.solvevolve.examplesonly.ui.videoSwipe.helper;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.solvevolve.examplesonly.R;
import com.solvevolve.examplesonly.databinding.ViewVideoSwipeBinding;
import com.solvevolve.examplesonly.model.Video;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class VideoSwipeViewHolder extends RecyclerView.ViewHolder {

    Context context;
    public ViewVideoSwipeBinding binding;
    DrawableCrossFadeFactory factory =
            new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

    public VideoSwipeViewHolder(@NonNull ViewVideoSwipeBinding itemView,
                                @NonNull Context context) {
        super(itemView.getRoot());
        this.binding = itemView;
        this.context = context;
    }

    public void bind(Video video) {
        if (video == null)
            return;

        binding.getRoot().setTag(this);
        binding.title.setText(video.getTitle());

        Glide.with(context)
                .load(video.getThumbUrl())
                .placeholder(R.color.md_grey_100)
                .transition(withCrossFade(factory))
                .into(binding.thumbnail);
    }
}
