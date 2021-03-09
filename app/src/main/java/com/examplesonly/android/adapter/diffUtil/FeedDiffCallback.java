package com.examplesonly.android.adapter.diffUtil;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.examplesonly.android.FeedQuery;

import java.util.List;

public class FeedDiffCallback extends DiffUtil.Callback {

    List<FeedQuery.Feed> oldFeed;
    List<FeedQuery.Feed> newFeed;

    public FeedDiffCallback(List<FeedQuery.Feed> oldFeed, List<FeedQuery.Feed> newFeed) {
        this.oldFeed = oldFeed;
        this.newFeed = newFeed;
    }


    @Override
    public int getOldListSize() {
        return oldFeed.size();
    }

    @Override
    public int getNewListSize() {
        return newFeed.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldFeed.get(oldItemPosition).videoId() == newFeed.get(newItemPosition).videoId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldFeed.get(oldItemPosition).equals(newFeed.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
