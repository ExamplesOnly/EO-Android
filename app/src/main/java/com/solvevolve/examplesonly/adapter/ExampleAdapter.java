package com.solvevolve.examplesonly.adapter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.solvevolve.examplesonly.R;
import com.solvevolve.examplesonly.databinding.ViewExampleItemFourBinding;
import com.solvevolve.examplesonly.databinding.ViewExampleItemThreeBinding;
import com.solvevolve.examplesonly.databinding.ViewExampleItemTwoBinding;
import com.solvevolve.examplesonly.handler.VideoClickListener;
import com.solvevolve.examplesonly.model.Video;
import com.solvevolve.examplesonly.util.MediaUtil;
import com.solvevolve.gallerypicker.utils.DateUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import timber.log.Timber;

public class ExampleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_EXAMPLE_ONE = 0;
    public static final int VIEW_TYPE_EXAMPLE_TWO = 1;
    public static final int VIEW_TYPE_EXAMPLE_THREE = 2;
    public static final int VIEW_TYPE_EXAMPLE_FOUR = 3;

    private final ArrayList<Video> mList;
    private final Context context;
    LayoutInflater inflater;
    private final VideoClickListener clickListener;

    private int viewType;

    public ExampleAdapter(ArrayList<Video> mList, Context context, VideoClickListener clickListener, int viewType) {
        this.mList = mList;
        this.context = context;
        this.clickListener = clickListener;
        this.viewType = viewType;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {

        View v = null;
//        return new ExampleItemTwoViewHolder(ViewExampleItemTwoBinding.inflate(inflater, parent, false), context);
//        return new ViewHolder(ViewEx.inflate(inflater, parent, false), context);

        switch (viewType) {
            case VIEW_TYPE_EXAMPLE_TWO:
            case VIEW_TYPE_EXAMPLE_ONE:
            default:
                return new ExampleItemTwoViewHolder(ViewExampleItemTwoBinding.inflate(inflater, parent, false),
                        context);
            case VIEW_TYPE_EXAMPLE_THREE:
                return new ExampleItemThreeViewHolder(ViewExampleItemThreeBinding.inflate(inflater, parent, false),
                        context);
            case VIEW_TYPE_EXAMPLE_FOUR:
                return new ExampleItemFourViewHolder(ViewExampleItemFourBinding.inflate(inflater, parent, false),
                        context);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_EXAMPLE_ONE:
            case VIEW_TYPE_EXAMPLE_TWO:
            default:
                ((ExampleItemTwoViewHolder) holder).bind(mList.get(position));
                break;
            case VIEW_TYPE_EXAMPLE_THREE:
                ((ExampleItemThreeViewHolder) holder).bind(mList.get(position));
                break;
        }
    }

    @Override
    public int getItemViewType(final int position) {
        return viewType;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ExampleItemFourViewHolder extends RecyclerView.ViewHolder {

        public ExampleItemFourViewHolder(@NonNull final ViewExampleItemFourBinding itemView,
                @NonNull Context context) {
            super(itemView.getRoot());

        }
    }

    public class ExampleItemThreeViewHolder extends RecyclerView.ViewHolder {

        Context context;
        ViewExampleItemThreeBinding binding;
        DrawableCrossFadeFactory factory =
                new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

        public ExampleItemThreeViewHolder(@NonNull final ViewExampleItemThreeBinding itemView,
                @NonNull Context context) {
            super(itemView.getRoot());
            binding = itemView;
            this.context = context;
        }

        void bind(Video video) {
            if (video != null) {
                Glide.with(context).load(video.getThumbUrl()).into(binding.thumbnail);

                if (video.getDemand() != null) {
//                    Spannable spannable = new SpannableString(video.getDemand().getTitle() + " answer");

                    binding.title.setText(video.getDemand().getTitle());
                } else {
                    binding.title.setText(video.getTitle());
                }

                binding.metaData.setText(
                        String.format("%s • %s", new DateUtil().millisToTime(Long.parseLong(video.getDuration())),
                                new DateUtil()
                                        .getPrettyDateString(StringToDate(video.getCreatedAt()).getTime()))
                );

                if (video.getUser() != null) {

                    Glide.with(context)
                            .load(video.getUser().getProfilePhoto())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.color.md_grey_400)
                            .transition(withCrossFade(factory))
                            .into(binding.profileImage);

                    binding.userName.setText(video.getUser().getFirstName());

                }

                binding.exampleCard.setOnClickListener(view -> {
                    clickListener.onVideoClicked(video);
                });
            }
        }
    }

    public class ExampleItemTwoViewHolder extends RecyclerView.ViewHolder {

        Context context;
        ViewExampleItemTwoBinding mItemOneBinding;
        DrawableCrossFadeFactory factory =
                new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

        public ExampleItemTwoViewHolder(@NonNull final ViewExampleItemTwoBinding itemView, @NonNull Context context) {
            super(itemView.getRoot());
            mItemOneBinding = itemView;
            this.context = context;
        }

        void bind(Video video) {
            if (video != null) {
                Glide.with(context).load(video.getThumbUrl()).into(mItemOneBinding.thumbnail);

                if (video.getDemand() != null) {
//                    Spannable spannable = new SpannableString(video.getDemand().getTitle() + " answer");

                    mItemOneBinding.title.setText(video.getDemand().getTitle());
                } else {
                    mItemOneBinding.title.setText(video.getTitle());
                }
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
                    Timber.e(video.getUser().getFirstName(), video.getUser().getProfilePhoto());
                    Glide.with(context)
                            .load(video.getUser().getProfilePhoto())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.color.md_grey_400)
                            .transition(withCrossFade(factory))
                            .into(mItemOneBinding.profileImage);

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
                    clickListener.onVideoClicked(video);
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
}
