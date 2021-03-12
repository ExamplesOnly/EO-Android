package com.examplesonly.android.handler;

import com.examplesonly.android.FeedQuery;
import com.examplesonly.android.model.Video;

public interface FeedClickListener {
    void onVideoClicked(FeedQuery.Feed video);
}