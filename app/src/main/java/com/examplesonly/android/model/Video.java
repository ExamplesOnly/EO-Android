package com.examplesonly.android.model;

import com.google.gson.annotations.SerializedName;
public class Video {

    String videoId;
    int size;
    String duration;
    String height;
    String width;
    String title;
    String description;
    String blurHash;
    String url;
    String thumbUrl;
    String createdAt;
    int demandId;

    @SerializedName("ExampleDemand")
    Demand demand;

    @SerializedName("User")
    User user;

    public Video() {
    }

    public Video(final String videoId, final int size, final String length, final String title,
            final String description, final String url,
            final String thumbUrl) {
        this.videoId = videoId;
        this.size = size;
        this.duration = duration;
        this.title = title;
        this.description = description;
        this.url = url;
        this.thumbUrl = thumbUrl;
    }

    public String getVideoId() {
        return videoId;
    }

    public Video setVideoId(final String videoId) {
        this.videoId = videoId;
        return this;
    }

    public int getSize() {
        return size;
    }

    public Video setSize(final int size) {
        this.size = size;
        return this;
    }

    public String getDuration() {
        return duration;
    }

    public Video setDuration(final String duration) {
        this.duration = duration;
        return this;
    }

    public String getHeight() {
        return height;
    }

    public Video setHeight(final String height) {
        this.height = height;
        return this;
    }

    public String getWidth() {
        return width;
    }

    public Video setWidth(final String width) {
        this.width = width;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Video setTitle(final String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Video setDescription(final String description) {
        this.description = description;
        return this;
    }

    public String getBlurHash() {
        return blurHash;
    }

    public Video setBlurHash(final String blurHash) {
        this.blurHash = blurHash;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Video setUrl(final String url) {
        this.url = url;
        return this;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public Video setThumbUrl(final String thumbUrl) {
        this.thumbUrl = thumbUrl;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Video setUser(final User user) {
        this.user = user;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Video setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public int getDemandId() {
        return demandId;
    }

    public Video setDemandId(final int demandId) {
        this.demandId = demandId;
        return this;
    }

    public Demand getDemand() {
        return demand;
    }

    public Video setDemand(final Demand demand) {
        this.demand = demand;
        return this;
    }
}
