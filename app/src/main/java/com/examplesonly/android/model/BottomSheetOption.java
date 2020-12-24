package com.examplesonly.android.model;

import android.graphics.drawable.Drawable;
public class BottomSheetOption {

    int id;
    String title;
    Drawable drawable;
    Object data;

    public BottomSheetOption() {
    }

    public BottomSheetOption(final int id, final String title, final Drawable drawable) {
        this.id = id;
        this.title = title;
        this.drawable = drawable;
    }

    public BottomSheetOption(final int id, final String title, final Drawable drawable, final Object data) {
        this.id = id;
        this.title = title;
        this.drawable = drawable;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public BottomSheetOption setId(final int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public BottomSheetOption setTitle(final String title) {
        this.title = title;
        return this;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public BottomSheetOption setDrawable(final Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    public Object getData() {
        return data;
    }

    public BottomSheetOption setData(final Object data) {
        this.data = data;
        return this;
    }
}
