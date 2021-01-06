package com.examplesonly.android.adapter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.examplesonly.android.ui.activity.MainActivity.INDEX_PROFILE;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.examplesonly.android.R;
import com.examplesonly.android.databinding.ViewExampleItemFourBinding;
import com.examplesonly.android.handler.FragmentChangeListener;
import com.examplesonly.android.handler.VideoClickListener;
import com.examplesonly.android.model.Video;
import com.examplesonly.gallerypicker.utils.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class HomeGridAdapter extends RecyclerView.Adapter<HomeGridAdapter.ViewHolder> {

    private final ArrayList<Video> videoList;
    private final Activity activity;
    LayoutInflater inflater;
    private final VideoClickListener clickListener;

    public HomeGridAdapter(ArrayList<Video> videoList, Activity activity, VideoClickListener clickListener) {
        this.videoList = videoList;
        this.activity = activity;
        this.clickListener = clickListener;

        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new ViewHolder(ViewExampleItemFourBinding.inflate(inflater, parent, false),
                activity);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bind(videoList.get(position));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        ViewExampleItemFourBinding binding;
        DrawableCrossFadeFactory factory =
                new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

        public ViewHolder(@NonNull final ViewExampleItemFourBinding itemView,
                          @NonNull Context context) {
            super(itemView.getRoot());
            this.binding = itemView;
            this.context = context;
        }

        void bind(Video video) {
            if (video.getUser() != null) {
                Glide.with(context)
                        .load(video.getUser().getProfilePhoto())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_user)
                        .transition(withCrossFade(factory))
                        .into(binding.profileImage);
            }
            RequestManager loadImage = Glide.with(context);

//            if (Math.random() < 0.5) {
//                binding.underReview.setVisibility(View.VISIBLE);
//                binding.overlay.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_example_grid_fill, context.getTheme()));
//                loadImage.load(video.getThumbUrl()).apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
//                        .placeholder(R.color.md_grey_100)
//                        .transition(withCrossFade(factory))
//                        .into(binding.thumbnail);
//            } else {
            binding.underReview.setVisibility(View.GONE);
            binding.overlay.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_example_grid, context.getTheme()));
            loadImage.load(video.getThumbUrl())
                    .placeholder(R.color.md_grey_100)
                    .transition(withCrossFade(factory))
                    .into(binding.thumbnail);
//            }

            binding.duration.setText(new DateUtil().millisToTime(Long.parseLong(video.getDuration())));

            if (video.getDemand() != null) {
                binding.answerTag.setVisibility(View.VISIBLE);
                binding.title.setText(video.getDemand().getTitle());
            } else {
                binding.answerTag.setVisibility(View.GONE);
                binding.title.setText(video.getTitle());
            }

            binding.viewCount.setText(String.valueOf(video.getViewCount()));
            binding.bowCount.setText(String.valueOf(video.getBow()));

            binding.exampleCard.setOnClickListener(v -> {
                clickListener.onVideoClicked(video);
            });

            binding.profileCard.setOnClickListener(v -> {
                if (activity instanceof FragmentChangeListener) {
                    FragmentChangeListener fragmentChangeListener = (FragmentChangeListener) activity;
                    fragmentChangeListener.switchFragment(INDEX_PROFILE, video.getUser());
                }
            });
        }
    }

    public Date StringToDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
