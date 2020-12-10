package com.examplesonly.android.ui.videoSwipe;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.examplesonly.android.databinding.ActivityVideoSwipeBinding;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.video.VideoInterface;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class VideoSwipeActivity extends AppCompatActivity {

    public static final String VIDEO_DATA = "video_data";

    ActivityVideoSwipeBinding binding;

    private ArrayList<Video> videoList = new ArrayList<>();
    private int currentItem = 0;
    private VideoInterface videoInterface;
    private VideoSwipeAdapter swipeAdapter;
    private Video currentVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVideoSwipeBinding.inflate(getLayoutInflater());
        videoInterface = new Api(this).getClient().create(VideoInterface.class);
        setContentView(binding.getRoot());

        currentVideo = getIntent().getParcelableExtra(VIDEO_DATA);
        videoList.add(currentVideo);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        init();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onDestroy() {
        if (binding != null)
            binding.viewPlayList.releasePlayer();
        super.onDestroy();
    }

    private void init() {
//        LinearSnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(binding.viewPlayList);

//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        binding.viewPlayList.setLayoutManager(layoutManager);
        binding.viewPlayList.setOrientation(DSVOrientation.VERTICAL);
        binding.viewPlayList.setSlideOnFling(false);
        binding.viewPlayList.setItemTransitionTimeMillis(100);
        binding.viewPlayList.setVideoList(videoList);

        swipeAdapter = new VideoSwipeAdapter(videoList, this, null);
        binding.viewPlayList.setAdapter(swipeAdapter);

//        binding.viewPlayList.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//        });

//        binding.viewPlayList.addOnItemChangedListener((viewHolder, adapterPosition) -> {
//            currentItem = adapterPosition;
//            swipeAdapter.setCurrentItem(currentItem);
//            Timber.e("addOnItemChanged: %s", String.valueOf(currentItem));
//            swipeAdapter.notifyDataSetChanged();
//        });

        videoInterface.getVideos().enqueue(new Callback<ArrayList<Video>>() {
            @Override
            public void onResponse(final Call<ArrayList<Video>> call, final Response<ArrayList<Video>> response) {
                if (response.isSuccessful()) {

                    Timber.e("swipeActivity isSuccessful %s", response.body().size());

                    ArrayList<Video> videos = response.body();
                    videoList.addAll(videos);
                    swipeAdapter.notifyDataSetChanged();
                } else {
                    Timber.e("swipeActivity error");
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
                Timber.e("swipeActivity error");
                if (t instanceof IOException) {
//                    binding.exampleList.setVisibility(View.GONE);
//                    binding.noInternet.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void getRelatedVideos() {

    }
}