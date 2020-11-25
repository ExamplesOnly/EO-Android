package com.examplesonly.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.examplesonly.android.databinding.ViewExampleProfileListBinding;
import com.examplesonly.android.handler.VideoClickListener;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.util.MediaUtil;
import java.util.ArrayList;
public class ProfileVideosAdapter extends Adapter<ProfileVideosAdapter.VIewHolder> {

    private final ArrayList<Video> videos;
    private final Context context;
    private final LayoutInflater inflater;
    private final VideoClickListener clickListener;

    public ProfileVideosAdapter(ArrayList<Video> videos, Context context, VideoClickListener clickListener) {
        this.videos = videos;
        this.context = context;
        this.clickListener = clickListener;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public VIewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new VIewHolder(ViewExampleProfileListBinding.inflate(inflater));
    }

    @Override
    public void onBindViewHolder(@NonNull final VIewHolder holder, final int position) {
        holder.bind(videos.get(position));
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class VIewHolder extends RecyclerView.ViewHolder {

        ViewExampleProfileListBinding mChooseCategoryBinding;

        public VIewHolder(@NonNull final ViewExampleProfileListBinding itemView) {
            super(itemView.getRoot());
            mChooseCategoryBinding = itemView;
        }

        void bind(Video video) {
            Glide.with(context).load(video.getThumbUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(mChooseCategoryBinding.thumbnail);
            mChooseCategoryBinding.title.setText(video.getTitle());

            ConstraintLayout thumbnailConstraintLayout = mChooseCategoryBinding.imageContainer;
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(thumbnailConstraintLayout);

            if (!MediaUtil
                    .isVideoLarger(Integer.parseInt(video.getHeight()), Integer.parseInt(video.getWidth()))) {

                constraintSet.setDimensionRatio(mChooseCategoryBinding.thumbnail.getId(),
                        video.getWidth() + ":" + video.getHeight());
                constraintSet.applyTo(thumbnailConstraintLayout);
            }

            mChooseCategoryBinding.imageCard.setOnClickListener(v -> {
                clickListener.onVideoClicked(video.getUrl());
            });
        }
    }
}
