package com.examplesonly.android.adapter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.examplesonly.android.ui.activity.MainActivity.INDEX_PROFILE;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
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
import com.examplesonly.android.model.User;
import com.examplesonly.gallerypicker.utils.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FeedGridAdapter extends RecyclerView.Adapter<FeedGridAdapter.ViewHolder> {

    private final ArrayList<FeedQuery.Feed> videoList;
    private final Activity activity;
    LayoutInflater inflater;
    private final FeedClickListener clickListener;

    public FeedGridAdapter(ArrayList<FeedQuery.Feed> videoList, Activity activity, FeedClickListener clickListener) {
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

//            if (video.getDemand() != null) {
//                binding.answerTag.setVisibility(View.VISIBLE);
//                binding.title.setText(video.getDemand().getTitle());
//            } else {
//                binding.answerTag.setVisibility(View.GONE);
            binding.title.setText(video.title());
//            }

            binding.viewCount.setText(String.valueOf(video.view()));
            binding.bowCount.setText(String.valueOf(video.bow()));

            binding.exampleCard.setOnClickListener(v -> {
                clickListener.onVideoClicked(video);
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
}
