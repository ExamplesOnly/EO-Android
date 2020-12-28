package com.examplesonly.android.handler;

import com.examplesonly.android.ui.view.ToggleIconButton;

public interface OnToggleListener {
    void liked(ToggleIconButton toggleIconButton);
    void unLiked(ToggleIconButton toggleIconButton);
}
