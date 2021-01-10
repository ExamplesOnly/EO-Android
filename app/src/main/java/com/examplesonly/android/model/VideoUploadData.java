package com.examplesonly.android.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoUploadData implements Parcelable {
    String title,
            description,
            categoryValue,
            metaDuration,
            metaHeight,
            metaWidth,
            demand,
            videoFilePath,
            thumbFilePath;

    public VideoUploadData() {

    }


    protected VideoUploadData(Parcel in) {
        title = in.readString();
        description = in.readString();
        categoryValue = in.readString();
        metaDuration = in.readString();
        metaHeight = in.readString();
        metaWidth = in.readString();
        demand = in.readString();
        videoFilePath = in.readString();
        thumbFilePath = in.readString();
    }

    public static final Creator<VideoUploadData> CREATOR = new Creator<VideoUploadData>() {
        @Override
        public VideoUploadData createFromParcel(Parcel in) {
            return new VideoUploadData(in);
        }

        @Override
        public VideoUploadData[] newArray(int size) {
            return new VideoUploadData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(categoryValue);
        dest.writeString(metaDuration);
        dest.writeString(metaHeight);
        dest.writeString(metaWidth);
        dest.writeString(demand);
        dest.writeString(videoFilePath);
        dest.writeString(thumbFilePath);
    }

    public String getTitle() {
        return title;
    }

    public VideoUploadData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public VideoUploadData setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getCategoryValue() {
        return categoryValue;
    }

    public VideoUploadData setCategoryValue(String categoryValue) {
        this.categoryValue = categoryValue;
        return this;
    }

    public String getMetaDuration() {
        return metaDuration;
    }

    public VideoUploadData setMetaDuration(String metaDuration) {
        this.metaDuration = metaDuration;
        return this;
    }

    public String getMetaHeight() {
        return metaHeight;
    }

    public VideoUploadData setMetaHeight(String metaHeight) {
        this.metaHeight = metaHeight;
        return this;
    }

    public String getMetaWidth() {
        return metaWidth;
    }

    public VideoUploadData setMetaWidth(String metaWidth) {
        this.metaWidth = metaWidth;
        return this;
    }

    public String getDemand() {
        return demand;
    }

    public VideoUploadData setDemand(String demand) {
        this.demand = demand;
        return this;
    }

    public String getVideoFilePath() {
        return videoFilePath;
    }

    public VideoUploadData setVideoFilePath(String videoFilePath) {
        this.videoFilePath = videoFilePath;
        return this;
    }

    public String getThumbFilePath() {
        return thumbFilePath;
    }

    public VideoUploadData setThumbFilePath(String thumbFilePath) {
        this.thumbFilePath = thumbFilePath;
        return this;
    }
}
