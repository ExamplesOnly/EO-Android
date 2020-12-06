package com.examplesonly.android.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
public class Video implements Parcelable {

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

    protected Video(Parcel in) {
        videoId = in.readString();
        size = in.readInt();
        duration = in.readString();
        height = in.readString();
        width = in.readString();
        title = in.readString();
        description = in.readString();
        blurHash = in.readString();
        url = in.readString();
        thumbUrl = in.readString();
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
        parcel.writeString(duration);
        parcel.writeString(height);
        parcel.writeString(width);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(blurHash);
        parcel.writeString(url);
        parcel.writeString(thumbUrl);
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
