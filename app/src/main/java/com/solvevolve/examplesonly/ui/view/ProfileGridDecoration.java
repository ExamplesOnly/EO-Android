package com.solvevolve.examplesonly.ui.view;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.State;
public class ProfileGridDecoration extends ItemDecoration {

    private final int gridSpacingPx;
    private final int gridSize;
    private boolean needLeftSpacing = false;

    public ProfileGridDecoration(int gridSpacingPx, int gridSize) {
        this.gridSpacingPx = gridSpacingPx;
        this.gridSize = gridSize;
    }

    @Override
    public void getItemOffsets(@NonNull final Rect outRect, @NonNull final View view,
            @NonNull final RecyclerView parent,
            @NonNull final State state) {

        int frameWidth = ((parent.getWidth() - gridSpacingPx * (gridSize - 1)) / gridSize);
        int padding = parent.getWidth() / gridSize - frameWidth;
        int itemPosition = ((LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
        outRect.top = gridSpacingPx;
//        if (itemPosition < gridSize) {
//            outRect.top = 0;
//        } else {
//            outRect.top = gridSpacingPx;
//        }

        if (itemPosition % gridSize == 0) {
            outRect.left = gridSpacingPx;
            outRect.right = padding;
            needLeftSpacing = true;
        } else if ((itemPosition + 1) % gridSize == 0) {
            needLeftSpacing = false;
            outRect.right = gridSpacingPx;
            outRect.left = padding;
        } else if (needLeftSpacing) {
            needLeftSpacing = false;
            outRect.left = gridSpacingPx - padding;
            if ((itemPosition + 2) % gridSize == 0) {
                outRect.right = gridSpacingPx - padding;
            } else {
                outRect.right = gridSpacingPx / 2;
            }
        } else if ((itemPosition + 2) % gridSize == 0) {
            needLeftSpacing = false;
            outRect.left = gridSpacingPx / 2;
            outRect.right = gridSpacingPx - padding;
        } else {
            needLeftSpacing = false;
            outRect.left = gridSpacingPx / 2;
            outRect.right = gridSpacingPx / 2;
        }
        outRect.bottom = 0;
    }
}
