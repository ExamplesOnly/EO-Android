package com.examplesonly.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.examplesonly.android.FeedQuery;
import com.examplesonly.android.R;
import com.examplesonly.android.databinding.ViewExampleItemFourBinding;
import com.examplesonly.android.handler.FeedClickListener;
import com.examplesonly.android.handler.FragmentChangeListener;
import com.examplesonly.android.handler.VideoClickListener;
import com.examplesonly.android.model.User;
import com.examplesonly.android.model.Video;
import com.examplesonly.gallerypicker.utils.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.examplesonly.android.ui.activity.MainActivity.INDEX_PROFILE;

public class FeedRecentPagingAdapter extends PagingDataAdapter<FeedQuery.Feed, FeedRecentPagingAdapter.ViewHolder> {

    private final Activity activity;
    LayoutInflater inflater;
    private final VideoClickListener clickListener;

    public FeedRecentPagingAdapter(Activity activity, VideoClickListener clickListener) {
        super(FeedRecentPagingAdapter.CALLBACK);
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
        if (getItem(position) != null) {
            holder.bind(getItem(position));
        }
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

        void bind(FeedQuery.Feed video) {
            Glide.with(context)
                    .load(video.publisher().profileImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_user)
                    .transition(withCrossFade(factory))
                    .into(binding.profileImage);
            RequestManager loadImage = Glide.with(context);

            binding.underReview.setVisibility(View.GONE);
            binding.overlay.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_example_grid, context.getTheme()));
            loadImage.load(video.thumbUrl())
                    .placeholder(R.color.md_grey_100)
                    .transition(withCrossFade(factory))
                    .into(binding.thumbnail);

            binding.duration.setText(new DateUtil().millisToTime(video.duration()));

            // Hide onDemand tag if it is not a demand
            if (!video.isDemand()) {
                binding.answerTag.setVisibility(View.GONE);
            }

            binding.title.setText(video.title());

            binding.viewCount.setText(String.valueOf(video.view()));
            binding.bowCount.setText(String.valueOf(video.bow()));

            binding.exampleCard.setOnClickListener(v -> {

                Video newVideo = new Video();
                newVideo.setVideoId(video.videoId());
                newVideo.setSize(video.size());
                newVideo.setDuration(video.duration());
                newVideo.setTitle(video.title());
                newVideo.setDescription(video.description());
                newVideo.setUrl(video.url());
                newVideo.setThumbUrl(video.thumbUrl());
                newVideo.setBow(video.bow() != null ? video.bow() : 0);
                newVideo.setViewCount(video.view() != null ? video.view() : 0);
                newVideo.setUserBowed(video.userBowed() != null && video.userBowed() ? 1 : 0);
                newVideo.setUserBookmarked(video.userBookmarked() != null && video.userBookmarked() ? 1 : 0);
                newVideo.setUser(new User()
                        .setUuid(video.publisher().uuid())
                        .setEmail(video.publisher().email())
                        .setFirstName(video.publisher().firstName())
                        .setMiddleName(video.publisher().middleName())
                        .setLastName(video.publisher().lastName())
                        .setProfilePhoto(video.publisher().profileImage())
                        .setCoverPhoto(video.publisher().coverImage())
                );
                clickListener.onVideoClicked(newVideo);
            });

            binding.profileCard.setOnClickListener(v -> {
                if (activity instanceof FragmentChangeListener) {
                    FragmentChangeListener fragmentChangeListener = (FragmentChangeListener) activity;

                    User currUser = new User()
                            .setUuid(video.publisher().uuid())
                            .setEmail(video.publisher().email())
                            .setFirstName(video.publisher().firstName())
                            .setMiddleName(video.publisher().middleName())
                            .setLastName(video.publisher().lastName())
                            .setProfilePhoto(video.publisher().profileImage())
                            .setCoverPhoto(video.publisher().coverImage());

                    fragmentChangeListener.switchFragment(INDEX_PROFILE, currUser);
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

    public static final DiffUtil.ItemCallback<FeedQuery.Feed> CALLBACK = new DiffUtil.ItemCallback<FeedQuery.Feed>() {
        @Override
        public boolean areItemsTheSame(@NonNull FeedQuery.Feed oldItem, @NonNull FeedQuery.Feed newItem) {
            return oldItem.videoId().equals(newItem.videoId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull FeedQuery.Feed oldItem, @NonNull FeedQuery.Feed newItem) {
            return true;
        }
    };
}
