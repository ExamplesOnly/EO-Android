package com.solvevolve.examplesonly.ui.videoSwipe;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.solvevolve.examplesonly.model.Video;
import com.solvevolve.examplesonly.ui.videoSwipe.helper.VideoSwipeViewHolder;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.yarolegovich.discretescrollview.DiscreteScrollLayoutManager;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;

import timber.log.Timber;

public class VideoPlayerRecyclerView extends DiscreteScrollView {

    private enum VolumeState {ON, OFF}

    // ui

    private PlayerView videoSurfaceView;
    private SimpleExoPlayer videoPlayer;
    private View viewHolderParent;
    private ImageView thumbnail;
    private ConstraintLayout rootLayout, videoCardConstraint;
    private CardView videoCard;

    private ArrayList<Video> videoList = new ArrayList<>();
    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;
    private Context context;
    private int playPosition = -1;
    private boolean isVideoViewAdded;
    private RequestManager requestManager;
    private OnItemChangedListener itemChangedListener;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    // controlling playback state
    private VolumeState volumeState;

    public VideoPlayerRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public VideoPlayerRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context.getApplicationContext();
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        videoSurfaceDefaultHeight = point.x;
        screenDefaultHeight = point.y;

        videoSurfaceView = new PlayerView(this.context);
        videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

        LoadControl loadControl = new DefaultLoadControl();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(context).build();
        TrackSelector trackSelector = new DefaultTrackSelector(context);

        // 2. Create the player
        videoPlayer = new SimpleExoPlayer.Builder(context)
                .setLoadControl(loadControl).setBandwidthMeter(bandwidthMeter)
                .setTrackSelector(trackSelector)
                .build();
        // Bind the player to the view.
        videoSurfaceView.setUseController(false);
        videoSurfaceView.setPlayer(videoPlayer);
        setVolumeControl(VolumeState.ON);

        onGlobalLayoutListener = () -> {
            playVideo(0);
            getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        };

        getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);


        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Timber.e("onScrollStateChanged: called.");
                    if (thumbnail != null) { // show the old thumbnail
                        thumbnail.setVisibility(VISIBLE);
                    }

                    playVideo(-1);

                    // There's a special case when the end of the list has been reached.
                    // Need to handle that with this bit of logic
//                    if (!recyclerView.canScrollVertically(1)) {
//                        playVideo(true);
//                    } else {
//                        playVideo(false);
//                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                if (viewHolderParent != null && viewHolderParent.equals(view)) {
                    resetVideoView();
                }

            }
        });

        videoPlayer.addListener(new Player.EventListener() {

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onPlaybackStateChanged(int state) {
                Timber.e("videoPlayer onPlaybackStateChanged %s", state);
                switch (state) {

                    case Player.STATE_BUFFERING:
                        Timber.e("onPlayerStateChanged: Buffering video.");
//                        if (progressBar != null) {
//                            progressBar.setVisibility(VISIBLE);
//                        }

                        break;
                    case Player.STATE_ENDED:
                        Timber.e("onPlayerStateChanged: Video ended.");
                        videoPlayer.seekTo(0);
                        break;
                    case Player.STATE_IDLE:

                        break;
                    case Player.STATE_READY:
                        Timber.e("onPlayerStateChanged: Ready to play.");
//                        if (progressBar != null) {
//                            progressBar.setVisibility(GONE);
//                        }
                        if (!isVideoViewAdded) {
                            addVideoView();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void playVideo(int index) {

        Timber.e("--------------------");

        int targetPosition = ((DiscreteScrollLayoutManager) getLayoutManager()).getCurrentPosition();

        View child = null;

        Timber.e("1");

        switch (getChildCount()) {
            case 1:
            case 2:
                getAdapter().getItemCount();
                child = getChildAt(0);
                break;
            case 3:
                child = getChildAt(1);
                break;
        }

        Timber.e("2 %s", getChildCount());
        if (index >= 0) {
            child = getChildAt(index);
        }

        Timber.e("3");
        if (child == null) {
            return;
        }

        Timber.e("4");
        VideoSwipeViewHolder holder = (VideoSwipeViewHolder) child.getTag();

        if (holder == null) {
            playPosition = -1;
            return;
        }

        Timber.e("5");
        thumbnail = holder.binding.thumbnail;
        viewHolderParent = holder.itemView;
        rootLayout = holder.binding.root;
        videoCardConstraint = holder.binding.videoCardConstraint;

        videoSurfaceView.setPlayer(videoPlayer);

//        viewHolderParent.setOnClickListener(videoViewClickListener);

        Timber.e("6");
        String mediaUrl = videoList.get(targetPosition).getUrl();
        if (mediaUrl != null) {
            Uri mediaUri = Uri.parse(mediaUrl);
            MediaSource mediaSource = buildMediaSource(mediaUri);
            videoPlayer.prepare(mediaSource, false, false);
            videoPlayer.setPlayWhenReady(true);

            Timber.e("7");
        }
    }

    /**
     * Returns the visible region of the video surface on the screen.
     * if some is cut off, it will return less than the @videoSurfaceDefaultHeight
     *
     * @param playPosition
     * @return
     */
    private int getVisibleVideoSurfaceHeight(int playPosition) {
        int at = playPosition - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        Timber.e("getVisibleVideoSurfaceHeight: at: %s", at);

        View child = getChildAt(at);
        if (child == null) {
            return 0;
        }

        int[] location = new int[2];
        child.getLocationInWindow(location);

        if (location[1] < 0) {
            return location[1] + videoSurfaceDefaultHeight;
        } else {
            return screenDefaultHeight - location[1];
        }
    }


    // Remove the old player
    private void removeVideoView(PlayerView videoView) {
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }

        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            isVideoViewAdded = false;
            viewHolderParent.setOnClickListener(null);
        }

    }

    private void addVideoView() {
        ConstraintSet set = new ConstraintSet();
        set.clone(videoCardConstraint);

        videoSurfaceView.setId(View.generateViewId());
        videoCardConstraint.addView(videoSurfaceView);

        set.connect(videoSurfaceView.getId(), ConstraintSet.TOP, rootLayout.getId(), ConstraintSet.TOP);
        set.connect(videoSurfaceView.getId(), ConstraintSet.BOTTOM, rootLayout.getId(), ConstraintSet.BOTTOM);
        set.connect(videoSurfaceView.getId(), ConstraintSet.START, rootLayout.getId(), ConstraintSet.START);
        set.connect(videoSurfaceView.getId(), ConstraintSet.END, rootLayout.getId(), ConstraintSet.END);
        set.applyTo(videoCardConstraint);

//        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) videoSurfaceView.getLayoutParams();
//        lp.height = LayoutParams.MATCH_PARENT;
//        lp.width = LayoutParams.MATCH_PARENT;
        videoSurfaceView.setLayoutParams(new ConstraintLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        isVideoViewAdded = true;
        videoSurfaceView.requestFocus();
        videoSurfaceView.setVisibility(VISIBLE);
        videoSurfaceView.setAlpha(1);
        thumbnail.setVisibility(GONE);
    }

    private void resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(videoSurfaceView);
            playPosition = -1;
            videoSurfaceView.setVisibility(INVISIBLE);
            thumbnail.setVisibility(VISIBLE);
        }
    }

    public void releasePlayer() {

        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }

        viewHolderParent = null;
    }

    private void toggleVolume() {
        if (videoPlayer != null) {
            if (volumeState == VolumeState.OFF) {
                Timber.e("togglePlaybackState: enabling volume.");
                setVolumeControl(VolumeState.ON);

            } else if (volumeState == VolumeState.ON) {
                Timber.e("togglePlaybackState: disabling volume.");
                setVolumeControl(VolumeState.OFF);

            }
        }
    }

    private void setVolumeControl(VolumeState state) {
        volumeState = state;
        if (state == VolumeState.OFF) {
            videoPlayer.setVolume(0f);
            animateVolumeControl();
        } else if (state == VolumeState.ON) {
            videoPlayer.setVolume(1f);
            animateVolumeControl();
        }
    }

    private void animateVolumeControl() {
//        if(volumeControl != null){
//            volumeControl.bringToFront();
//            if(volumeState == VolumeState.OFF){
//                requestManager.load(R.drawable.ic_volume_off_grey_24dp)
//                        .into(volumeControl);
//            }
//            else if(volumeState == VolumeState.ON){
//                requestManager.load(R.drawable.ic_volume_up_grey_24dp)
//                        .into(volumeControl);
//            }
//            volumeControl.animate().cancel();
//
//            volumeControl.setAlpha(1f);
//
//            volumeControl.animate()
//                    .alpha(0f)
//                    .setDuration(600).setStartDelay(1000);
//        }
    }

    public void setVideoList(ArrayList<Video> videoList) {
        this.videoList = videoList;
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory("eo-player");
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        return new ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory)
                .createMediaSource(MediaItem.fromUri(uri));
    }

}
