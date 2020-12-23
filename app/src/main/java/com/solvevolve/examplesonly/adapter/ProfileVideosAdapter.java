package com.solvevolve.examplesonly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.solvevolve.examplesonly.component.EoAlertDialog.EoAlertDialog;
import com.solvevolve.examplesonly.databinding.ViewExampleProfileListBinding;
import com.solvevolve.examplesonly.handler.VideoClickListener;
import com.solvevolve.examplesonly.model.Video;
import com.solvevolve.examplesonly.network.Api;
import com.solvevolve.examplesonly.network.video.VideoInterface;
import com.solvevolve.examplesonly.util.MediaUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
public class ProfileVideosAdapter extends Adapter<ProfileVideosAdapter.VIewHolder> {

    private final ArrayList<Video> videos;
    private final Context context;
    private final LayoutInflater inflater;
    private final VideoClickListener clickListener;
    private VideoInterface mVideoInterface;

    public ProfileVideosAdapter(ArrayList<Video> videos, Context context, VideoClickListener clickListener) {
        this.videos = videos;
        this.context = context;
        this.clickListener = clickListener;

        mVideoInterface = new Api(context).getClient().create(VideoInterface.class);

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
                clickListener.onVideoClicked(video);
            });

            mChooseCategoryBinding.imageCard.setOnLongClickListener(v -> {
                EoAlertDialog deleteDialog = new EoAlertDialog(context)
                        .setTitle("Delete")
                        .setPositiveText("Delete")
                        .setNegativeText("Cancel")
                        .setPositiveClickListener(dialog -> {
                            dialog.dismiss();
                            mVideoInterface.deleteVideo(video.getVideoId()).enqueue(
                                    new Callback<HashMap<String, String>>() {
                                        @Override
                                        public void onResponse(final Call<HashMap<String, String>> call,
                                                final Response<HashMap<String, String>> response) {
                                            if (response.isSuccessful()) {
                                                Toast.makeText(context, "Video deleted", Toast.LENGTH_SHORT).show();
                                            } else {
                                                try {
                                                    Timber.e("deleteVideo error %s", response.errorBody().string());
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                Toast.makeText(context, "Could not delete video", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(final Call<HashMap<String, String>> call,
                                                final Throwable t) {
                                            Toast.makeText(context, "Could not delete video", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        })
                        .setNegativeClickListener(AppCompatDialog::dismiss);

                deleteDialog.show();
                return false;
            });
        }
    }
}
