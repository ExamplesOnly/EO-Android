package com.examplesonly.android.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
public class Demand implements Parcelable {

    String uuid;
    String title;
    String description;
    int categoryId;
    int videoCount;
    ArrayList<Video> videos;
    @SerializedName("Category")
    Category category;
    User user;

    public Demand() {
    }

    protected Demand(Parcel in) {
        uuid = in.readString();
        title = in.readString();
        description = in.readString();
        categoryId = in.readInt();
        category = in.readParcelable(Category.class.getClassLoader());
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Demand> CREATOR = new Creator<Demand>() {
        @Override
        public Demand createFromParcel(Parcel in) {
            return new Demand(in);
        }

        @Override
        public Demand[] newArray(int size) {
            return new Demand[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeString(uuid);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeInt(categoryId);
        parcel.writeParcelable(category, i);
        parcel.writeParcelable(user, i);
    }

    public String getUuid() {
        return uuid;
    }

    public Demand setUuid(final String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Demand setTitle(final String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Demand setDescription(final String description) {
        this.description = description;
        return this;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public Demand setCategoryId(final int categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public Demand setVideoCount(final int videoCount) {
        this.videoCount = videoCount;
        return this;
    }

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public Demand setVideos(final ArrayList<Video> videos) {
        this.videos = videos;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public Demand setCategory(final Category category) {
        this.category = category;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Demand setUser(final User user) {
        this.user = user;
        return this;
    }
}
