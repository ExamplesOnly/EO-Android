package com.examplesonly.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Video implements Parcelable {

    String videoId;
    int size;
    int duration;
    int height;
    int width;
    String title;
    String description;
    String blurHash;
    String url;
    String thumbUrl;
    int bow;
    int view;
    int userBowed;
    int userBookmarked;
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

    protected Video(Parcel in) {
        videoId = in.readString();
        size = in.readInt();
        duration = in.readInt();
        height = in.readInt();
        width = in.readInt();
        title = in.readString();
        description = in.readString();
        blurHash = in.readString();
        url = in.readString();
        thumbUrl = in.readString();
        bow = in.readInt();
        view = in.readInt();
        userBowed = in.readInt();
        userBookmarked = in.readInt();
        createdAt = in.readString();
        demandId = in.readInt();
        demand = in.readParcelable(Demand.class.getClassLoader());
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeString(videoId);
        parcel.writeInt(size);
        parcel.writeInt(duration);
        parcel.writeInt(height);
        parcel.writeInt(width);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(blurHash);
        parcel.writeString(url);
        parcel.writeString(thumbUrl);
        parcel.writeInt(bow);
        parcel.writeInt(view);
        parcel.writeInt(userBowed);
        parcel.writeInt(userBookmarked);
        parcel.writeString(createdAt);
        parcel.writeInt(demandId);
        parcel.writeParcelable(demand, i);
        parcel.writeParcelable(user, i);
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

    public int getDuration() {
        return duration;
    }

    public Video setDuration(final int duration) {
        this.duration = duration;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public Video setHeight(final int height) {
        this.height = height;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public Video setWidth(final int width) {
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

    public int getBow() {
        return bow;
    }

    public Video setBow(int bow) {
        this.bow = bow;
        return this;
    }

    public int getViewCount() {
        return view;
    }

    public Video setViewCount(int view) {
        this.view = view;
        return this;
    }

    public boolean isUserBowed() {
        return userBowed == 1;
    }

    public Video setUserBowed(int userBowed) {
        this.userBowed = userBowed;
        return this;
    }

    public boolean isUserBookmarked() {
        return userBookmarked == 1;
    }

    public Video setUserBookmarked(int userBookmarked) {
        this.userBookmarked = userBookmarked;
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

    public User getUser() {
        return user;
    }

    public Video setUser(final User user) {
        this.user = user;
        return this;
    }
}
