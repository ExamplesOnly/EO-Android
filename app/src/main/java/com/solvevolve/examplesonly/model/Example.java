package com.solvevolve.examplesonly.model;

public class Example {

    String id;
    String videoUrl;
    String thumbUrl;
    String title;
    User user;

    public Example(final String id, final String videoUrl, final String thumbUrl, final String title,
            final User user) {
        this.id = id;
        this.videoUrl = videoUrl;
        this.thumbUrl = thumbUrl;
        this.title = title;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(final String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(final String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }
}
