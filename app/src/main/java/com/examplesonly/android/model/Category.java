package com.examplesonly.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public class Category implements Parcelable {

    int id;
    String title;
    String thumbUrl;
    String slug;
    boolean selected = false;

    public Category() {
    }

    protected Category(Parcel in) {
        id = in.readInt();
        title = in.readString();
        thumbUrl = in.readString();
        slug = in.readString();
        selected = in.readByte() != 0;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(thumbUrl);
        parcel.writeString(slug);
        parcel.writeByte((byte) (selected ? 1 : 0));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean equal = false;

        if (obj instanceof Category) {
            Category category = (Category) obj;
            equal = this.id == category.id;
        }
        return equal;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Category setTitle(final String title) {
        this.title = title;
        return this;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public Category setThumbUrl(final String thumbUrl) {
        this.thumbUrl = thumbUrl;
        return this;
    }

    public String getSlug() {
        return slug;
    }

    public Category setSlug(final String slug) {
        this.slug = slug;
        return this;
    }

    public boolean isSelected() {
        return selected;
    }

    public Category setSelected(final boolean selected) {
        this.selected = selected;
        return this;
    }
}
