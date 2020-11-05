package com.examplesonly.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.examplesonly.android.databinding.ViewExampleItemTwoBinding;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.util.MediaUtil;
import com.examplesonly.gallerypicker.utils.DateUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private static final int VIEW_TYPE_EXAMPLE_FULL = 0;

    private final ArrayList<Video> mList;
    private final Context context;
    LayoutInflater inflater;
    private final VideoClickListener clickListener;

    public HomeAdapter(ArrayList<Video> mList, Context context, VideoClickListener clickListener) {
        this.mList = mList;
        this.context = context;
        this.clickListener = clickListener;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {

        View v = null;

        switch (viewType) {
            case VIEW_TYPE_EXAMPLE_FULL:
                return new ViewHolder(ViewExampleItemTwoBinding.inflate(inflater, parent, false));
            default:
                return new ViewHolder(ViewExampleItemTwoBinding.inflate(inflater, parent, false));
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

        ViewExampleItemTwoBinding mItemOneBinding;

        public ViewHolder(@NonNull final ViewExampleItemTwoBinding itemView) {
            super(itemView.getRoot());
            mItemOneBinding = itemView;
        }

        void bind(Video video) {
            if (video != null) {
                Glide.with(context).load(video.getThumbUrl()).into(mItemOneBinding.thumbnail);
//            Glide.with(context).load(example.getUser().getProfilePhoto()).into(mItemOneBinding.profileImage);
                mItemOneBinding.title.setText(video.getTitle());
                mItemOneBinding.duration.setText(new DateUtil().millisToTime(Long.parseLong(video.getDuration())));

                ConstraintLayout thumbnailConstraintLayout = mItemOneBinding.thumbConstrains;
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(thumbnailConstraintLayout);

                if (MediaUtil
                        .isVideoLarger(Integer.parseInt(video.getHeight()), Integer.parseInt(video.getWidth()))) {
                    constraintSet.setDimensionRatio(mItemOneBinding.thumbnail.getId(), "9:11");
                } else {
                    constraintSet.setDimensionRatio(mItemOneBinding.thumbnail.getId(),
                            video.getWidth() + ":" + video.getHeight());
                }

                constraintSet.applyTo(thumbnailConstraintLayout);

                if (video.getUser() != null) {
                    mItemOneBinding.metaData.setText(
                            String.format("%s %s • %s", video.getUser().getFirstName(), video.getUser().getLastName(),
                                    new DateUtil()
                                            .getPrettyDateString(StringToDate(video.getCreatedAt()).getTime())));
                } else {
                    mItemOneBinding.metaData.setText(
                            String.format("%s", new DateUtil()
                                    .getPrettyDateString(StringToDate(video.getCreatedAt()).getTime())));
                }

                mItemOneBinding.exampleCard.setOnClickListener(view -> {
                    clickListener.onVideoClicked(video.getUrl());
                });
            }
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

    public interface VideoClickListener {

        void onVideoClicked(String url);
    }
}
