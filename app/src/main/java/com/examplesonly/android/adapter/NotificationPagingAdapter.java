package com.examplesonly.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Html;
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
import com.examplesonly.android.NotificationsQuery;
import com.examplesonly.android.R;
import com.examplesonly.android.TrendingFeedQuery;
import com.examplesonly.android.databinding.ViewExampleItemFourBinding;
import com.examplesonly.android.databinding.ViewNotificationItemBinding;
import com.examplesonly.android.handler.FragmentChangeListener;
import com.examplesonly.android.handler.VideoClickListener;
import com.examplesonly.android.model.User;
import com.examplesonly.android.model.Video;
import com.examplesonly.gallerypicker.utils.DateUtil;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.examplesonly.android.ui.activity.MainActivity.INDEX_PROFILE;

public class NotificationPagingAdapter extends PagingDataAdapter<NotificationsQuery.Notification, NotificationPagingAdapter.ViewHolder> {

    private final Activity activity;
    LayoutInflater inflater;

    public NotificationPagingAdapter(Activity activity) {
        super(CALLBACK);
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new ViewHolder(ViewNotificationItemBinding.inflate(inflater, parent, false),
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
        ViewNotificationItemBinding binding;
        DrawableCrossFadeFactory factory =
                new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

        public ViewHolder(@NonNull final ViewNotificationItemBinding itemView,
                          @NonNull Context context) {
            super(itemView.getRoot());
            this.binding = itemView;
            this.context = context;
        }

        void bind(NotificationsQuery.Notification notification) {
            Glide.with(context)
                    .load(notification.thumb())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_user)
                    .transition(withCrossFade(factory))
                    .into(binding.thumbImage);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.textContent.setText(Html.fromHtml(notification.text(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                binding.textContent.setText(Html.fromHtml(notification.text()));
            }

            PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
            String ago = prettyTime.format(new Date(Long.parseLong(notification.createdAt())));
            binding.time.setText(ago);
        }
    }

    public static final DiffUtil.ItemCallback<NotificationsQuery.Notification> CALLBACK = new DiffUtil.ItemCallback<NotificationsQuery.Notification>() {
        @Override
        public boolean areItemsTheSame(@NonNull NotificationsQuery.Notification oldItem, @NonNull NotificationsQuery.Notification newItem) {
            return oldItem.uuid().equals(newItem.uuid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull NotificationsQuery.Notification oldItem, @NonNull NotificationsQuery.Notification newItem) {
            return true;
        }
    };
}
