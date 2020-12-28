package com.examplesonly.android.ui.activity;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.examplesonly.android.adapter.ExampleAdapter.VIEW_TYPE_EXAMPLE_THREE;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.request.transition.Transition;
import com.examplesonly.android.R;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.adapter.ExampleAdapter;
import com.examplesonly.android.databinding.ActivityVideoPlayerBinding;
import com.examplesonly.android.handler.VideoClickListener;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.video.VideoInterface;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.examplesonly.android.worker.VStretchLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.BlurTransformation;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class VideoPlayerActivity extends AppCompatActivity implements VideoClickListener {

    public static final String VIDEO_DATA = "video_data";

    private ActivityVideoPlayerBinding binding;
    private SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private CountDownTimer viewTimer;
    private long millisVideoPlayed = 0;
    private long millisUntilView = 10000;
    private String currentViewId = null;

    private final ArrayList<Video> exampleList = new ArrayList<>();
    private VideoInterface videoInterface;
    private ExampleAdapter mExampleAdapter;
    private UserDataProvider userDataProvider;
    private TextView titleText, viewCount, bowCount;
    private RelativeLayout controllerParent;
    private ImageButton fullScreenBtn;
    private ImageButton exoPlay;
    private ImageButton exoPause;
    private boolean isFullScreen = false;
    ConstraintSet defaultConstrains = new ConstraintSet();

    DrawableCrossFadeFactory factory =
            new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

    private Video currentVideo;
    private String currentVideoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Video newVideo = getIntent().getParcelableExtra(VIDEO_DATA);
        currentVideoId = newVideo.getVideoId();

        init();
        setupExamples();
        getVideos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player == null) {
            loadPlayer();
        }
        playVideo(currentVideo, true);
        loadState();
    }

    @Override
    public void onPause() {
        this.millisVideoPlayed = player.getContentPosition();
        super.onPause();
        if (viewTimer != null)
            viewTimer.cancel();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
        LogVStretch();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (viewTimer != null)
            viewTimer.cancel();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
        LogVStretch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onVideoClicked(final Video video) {
        clearUI();
        getVideoAndPlay(video.getVideoId());
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen) {
            clearFullScreenVideo();
        } else {
            super.onBackPressed();
        }
    }

    void init() {
        userDataProvider = UserDataProvider.getInstance(this);
        videoInterface = new Api(this).getClient().create(VideoInterface.class);

        controllerParent = findViewById(R.id.controller_parent);
        titleText = findViewById(R.id.exo_text);
        fullScreenBtn = findViewById(R.id.bt_fullscreen);
        exoPlay = findViewById(R.id.exo_play);
        exoPause = findViewById(R.id.exo_pause);
        viewCount = findViewById(R.id.view_count);
        bowCount = findViewById(R.id.bow_count);

        binding.videoLoading.setVisibility(View.GONE);
        exoPlay.setVisibility(View.GONE);
        exoPause.setVisibility(View.GONE);

        binding.bowBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                binding.bowBtn.setEnabled(false);
                videoInterface.postBow(currentVideo.getVideoId(), userDataProvider.getUserUuid()).enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                        if (response.isSuccessful()) {
                            binding.bowBtn.setEnabled(true);
                        } else {
                            binding.bowBtn.setLiked(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                        binding.bowBtn.setLiked(false);
                    }
                });
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                binding.bowBtn.setEnabled(false);
                videoInterface.postBow(currentVideo.getVideoId(), userDataProvider.getUserUuid()).enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                        if (response.isSuccessful()) {
                            binding.bowBtn.setEnabled(true);
                        } else {
                            binding.bowBtn.setLiked(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                        binding.bowBtn.setLiked(true);
                    }
                });
            }
        });

        binding.bookmarkBtn.setOnClickListener(v -> {
            toggleBookmark();
        });

        defaultConstrains.clone(binding.playerParent);

        fullScreenBtn.setOnClickListener(v -> {
            if (isFullScreen) {
                clearFullScreenVideo();
            } else {
                fullScreenVideo();
            }
        });

        loadPlayer();

        clearUI();
        getVideoAndPlay(currentVideoId);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        binding.exoplayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }

    private void loadPlayer() {
        LoadControl loadControl = new DefaultLoadControl();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(this).build();
        TrackSelector trackSelector = new DefaultTrackSelector(this);

        player = new SimpleExoPlayer.Builder(this)
                .setLoadControl(loadControl).setBandwidthMeter(bandwidthMeter)
                .setTrackSelector(trackSelector)
                .build();
        player.setPlayWhenReady(playWhenReady);
        player.addListener(new EventListener() {
            @Override
            public void onPlaybackStateChanged(final int state) {
                Timber.e("State %s", state);
                // keep screen on when video is playing
//                if (state == Player.STATE_IDLE || state == Player.STATE_ENDED ||
//                        !playWhenReady) {
//                    binding.exoplayer.setKeepScreenOn(false);
//                } else {
//                    binding.exoplayer.setKeepScreenOn(true);
//                }

                // setup loading
                switch (state) {
                    case Player.STATE_IDLE:
                    case Player.STATE_READY:
                    case Player.STATE_ENDED:
                        binding.videoLoading.setVisibility(View.GONE);
                        exoPlay.setVisibility(View.VISIBLE);
                        exoPause.setVisibility(View.VISIBLE);
                        break;
                    case Player.STATE_BUFFERING:
                        binding.exoplayer.setKeepScreenOn(true);
                        binding.videoLoading.setVisibility(View.VISIBLE);
                        exoPlay.setVisibility(View.GONE);
                        exoPause.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    binding.exoplayer.setKeepScreenOn(true);
                    viewTimer = new CountDownTimer(millisUntilView, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            millisUntilView = millisUntilFinished;
                        }

                        @Override
                        public void onFinish() {
                            postVideoView(currentVideo.getVideoId());
                        }
                    };
                    viewTimer.start();
                } else {
                    binding.exoplayer.setKeepScreenOn(false);
                    viewTimer.cancel();
                }
            }
        });

        binding.exoplayer.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        binding.exoplayer.setPlayer(player);
    }

    private void loadState() {
        if (player != null) {
            player.seekTo(currentWindow, playbackPosition);
        }
    }

    private void playVideo(Video video, boolean isResume) {

        if (currentVideo == null) {
            return;
        }

        LogVStretch();
        this.currentVideo = video;
        this.millisUntilView = 10000;

        if (!isResume) {
            this.currentViewId = null;
        }

        controllerParent.setVisibility(View.VISIBLE);
        binding.relatedVideos.setVisibility(View.VISIBLE);
        binding.buttonParent.setVisibility(View.VISIBLE);

        Uri uri = Uri.parse(currentVideo.getUrl());
        MediaSource mediaSource = buildMediaSource(uri);
//        player.setMediaSource(mediaSource, false);
        player.prepare(mediaSource, false, false);

        if (titleText != null)
            titleText.setText(currentVideo.getTitle());

        if (viewCount != null)
            viewCount.setText(String.valueOf(currentVideo.getViewCount()));

        if (bowCount != null)
            bowCount.setText(String.valueOf(currentVideo.getBow()));

        binding.bowBtn.setLiked(currentVideo.isUserBowed());

        if (currentVideo.isUserBookmarked()) {
            setBookmarkIcon(true);
        } else {
            setBookmarkIcon(false);
        }

        Glide.with(this)
                .load(video.getThumbUrl())
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                .placeholder(R.color.md_grey_100)
                .transition(withCrossFade(factory))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull final Drawable resource,
                                                @Nullable final Transition<? super Drawable> transition) {
                        binding.exoplayer.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable final Drawable placeholder) {

                    }
                });
        player.seekTo(0);
    }

    void getVideos() {
        videoInterface.getVideos().enqueue(new Callback<ArrayList<Video>>() {
            @Override
            public void onResponse(final Call<ArrayList<Video>> call, final Response<ArrayList<Video>> response) {

//                binding.relatedVideos.setVisibility(View.VISIBLE);
//                binding.noInternet.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    ArrayList<Video> videos = response.body();
                    Timber.e("VideoPlayer getVideos success: %s", videos.size());

                    exampleList.clear();
                    exampleList.addAll(videos);
                    mExampleAdapter.notifyDataSetChanged();
                } else {
                    Timber.e("VideoPlayer getVideos error");
                    try {
                        Timber.e(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final @NotNull Call<ArrayList<Video>> call, final @NotNull Throwable t) {
                t.printStackTrace();
                if (t instanceof IOException) {
//                    binding.exampleList.setVisibility(View.GONE);
//                    binding.noInternet.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory("eo-player");
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        return new ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory)
                .createMediaSource(MediaItem.fromUri(uri));
    }

    private void fullScreenVideo() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setRequestedOrientation(Integer.parseInt(currentVideo.getHeight()) < Integer.parseInt(currentVideo.getWidth())
                ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ConstraintSet landscapeConstrains = new ConstraintSet();
        landscapeConstrains.clone(binding.playerParent);
        landscapeConstrains
                .connect(R.id.exoplayer, ConstraintSet.START, R.id.player_parent, ConstraintSet.START, 0);
        landscapeConstrains
                .connect(R.id.exoplayer, ConstraintSet.END, R.id.player_parent, ConstraintSet.END, 0);
        landscapeConstrains
                .connect(R.id.exoplayer, ConstraintSet.TOP, R.id.player_parent, ConstraintSet.TOP, 0);
        landscapeConstrains
                .connect(R.id.exoplayer, ConstraintSet.BOTTOM, R.id.player_parent, ConstraintSet.BOTTOM, 0);
        landscapeConstrains.applyTo(binding.playerParent);

        ConstraintLayout.LayoutParams exoParams = (ConstraintLayout.LayoutParams) binding.exoplayer
                .getLayoutParams();
        exoParams.dimensionRatio = null;
        binding.exoplayer.setLayoutParams(exoParams);

        isFullScreen = true;
    }

    private void clearFullScreenVideo() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        defaultConstrains.applyTo(binding.playerParent);

        isFullScreen = false;
    }

    void setupExamples() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.relatedVideos.setLayoutManager(layoutManager);

        mExampleAdapter = new ExampleAdapter(exampleList, this, this,
                VIEW_TYPE_EXAMPLE_THREE);
        binding.relatedVideos.setAdapter(mExampleAdapter);
    }

    void postVideoView(String videoId) {
        videoInterface.postView(videoId, userDataProvider.getUserUuid()).enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                if (response.isSuccessful()) {
                    currentViewId = response.body().get("uuid");
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
            }
        });
    }

    void toggleBookmark() {
        boolean currentStatus;
        binding.bookmarkBtn.setEnabled(false);

        if (currentVideo.isUserBookmarked()) {
            setBookmarkIcon(false);
            currentStatus = false;
        } else {
            setBookmarkIcon(true);
            currentStatus = true;
        }

        videoInterface.postBookmark(currentVideo.getVideoId()).enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                if (response.isSuccessful()) {
                    Timber.e("Bookmark %s", response.body().toString());
                    currentVideo.setUserBookmarked(currentVideo.isUserBookmarked() ? 0 : 1);
                } else {
                    try {
                        Timber.e("Bookmark %s", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setBookmarkIcon(!currentStatus);
                }
                binding.bookmarkBtn.setEnabled(true);
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                setBookmarkIcon(!currentStatus);
                binding.bookmarkBtn.setEnabled(true);
                Timber.e("Bookmark fail");
                t.printStackTrace();
            }
        });
    }

    void setBookmarkIcon(boolean status) {
        if (status) {
            binding.bookmarkBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_bookmark_post_fill, getTheme()));
            binding.bookmarkBtn.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getTheme()));
        } else {
            binding.bookmarkBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_bookmark_post, getTheme()));
            binding.bookmarkBtn.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.dark, getTheme()));
        }
    }

    void LogVStretch() {
        Timber.e("LogVStretch");
        if (player != null) {
            Timber.e("LogVStretch player");
            this.millisVideoPlayed = player.getContentPosition();
        }

        if (currentViewId != null) {
            Timber.e("LogVStretch currentViewId");
            WorkRequest stretchPostRequest =
                    new OneTimeWorkRequest.Builder(VStretchLogger.class)
                            .setConstraints(new Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build())
                            .setInputData(new Data.Builder()
                                    .putString("videoId", currentVideo.getVideoId())
                                    .putString("viewId", currentViewId)
                                    .putLong("stretch", millisVideoPlayed)
                                    .build())
                            .build();
            WorkManager
                    .getInstance(this)
                    .enqueue(stretchPostRequest);

        }
    }

    void getVideoAndPlay(String videoId) {
        videoInterface.getVideo(videoId).enqueue(new Callback<Video>() {
            @Override
            public void onResponse(@NotNull Call<Video> call, @NotNull Response<Video> response) {
                if (response.isSuccessful()) {
                    Timber.e("getVideoAndPlay %s", response.body().toString());
                    currentVideo = response.body();
                    playVideo(currentVideo, false);
                } else {
                    try {
                        Timber.e("getVideoAndPlay error %s", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<Video> call, @NotNull Throwable t) {

                Timber.e("getVideoAndPlay fail");
                t.printStackTrace();
            }
        });
    }

    void clearUI() {
        player.stop(true);
        controllerParent.setVisibility(View.GONE);
        binding.relatedVideos.setVisibility(View.GONE);
        binding.buttonParent.setVisibility(View.GONE);
        binding.videoLoading.setVisibility(View.VISIBLE);
    }

}