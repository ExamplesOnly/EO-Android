package com.examplesonly.android.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearSnapHelper;
import com.examplesonly.android.databinding.ActivityVideoSwipeBinding;
import com.examplesonly.android.model.Video;
import com.examplesonly.android.network.video.VideoInterface;
import java.util.ArrayList;

public class VideoSwipeActivity extends AppCompatActivity {

    public static final String VIDEO_DATA = "video_data";

    ActivityVideoSwipeBinding binding;

    private ArrayList<Video> exampleList = new ArrayList<>();
    private VideoInterface videoInterface;
    private Video currentVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVideoSwipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentVideo = getIntent().getParcelableExtra(VIDEO_DATA);

    }

    private void init() {
        VideoSwipeActivity startSnapHelper = new VideoSwipeActivity();
        new LinearSnapHelper().attachToRecyclerView(binding.viewPlayList);
    }

    private void getRelatedVideos() {

    }
}