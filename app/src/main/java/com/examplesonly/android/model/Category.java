package com.examplesonly.android.model;

public class Category {

    int id;
    String title;
    String thumbUrl;
    String slug;
    boolean selected = false;

    public Category() {
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
