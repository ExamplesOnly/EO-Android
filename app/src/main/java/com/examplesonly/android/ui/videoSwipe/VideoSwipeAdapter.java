package com.examplesonly.android.ui.videoSwipe;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.request.transition.Transition;
import com.examplesonly.android.R;
import com.examplesonly.android.databinding.ViewVideoSwipeBinding;
import com.examplesonly.android.handler.VideoClickListener;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.ui.videoSwipe.helper.VideoSwipeViewHolder;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class VideoSwipeAdapter extends RecyclerView.Adapter<VideoSwipeViewHolder> {

    private ArrayList<Video> videoList;
    private final Activity activity;
    LayoutInflater inflater;
    private final VideoClickListener clickListener;

    public VideoSwipeAdapter(ArrayList<Video> videoList, Activity activity, VideoClickListener clickListener) {
        this.videoList = videoList;
        this.activity = activity;
        this.clickListener = clickListener;

        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public VideoSwipeViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new VideoSwipeViewHolder(ViewVideoSwipeBinding.inflate(inflater, parent, false),
                activity);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoSwipeViewHolder holder, final int position) {
        holder.bind(videoList.get(position));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        Context context;
//        ViewVideoSwipeBinding binding;
//        DrawableCrossFadeFactory factory =
//                new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
//
//
//        public ViewHolder(@NonNull final ViewVideoSwipeBinding itemView,
//                          @NonNull Context context) {
//            super(itemView.getRoot());
//            this.binding = itemView;
//            this.context = context;
//        }
//
//        void bind(Video video, int position, int currentItem) {
//            if (video == null)
//                return;
//
//            binding.title.setText(video.getTitle());
//
//            Glide.with(context)
//                    .load(video.getThumbUrl())
//                    .placeholder(R.color.md_grey_100)
//                    .transition(withCrossFade(factory))
//                    .into(binding.thumbnail);
//
////            if(position == currentItem) {
////                TransitionManager.beginDelayedTransition(binding.root);
////                binding.overlay.setVisibility(View.GONE);
////            } else {
////                TransitionManager.beginDelayedTransition(binding.root);
////                binding.overlay.setVisibility(View.VISIBLE);
////            }
//
////            binding.videoPlayer.setBackground();x
//        }
//    }
}
