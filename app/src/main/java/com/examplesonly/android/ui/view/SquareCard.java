package com.examplesonly.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.google.android.material.card.MaterialCardView;

public class SquareCard extends MaterialCardView {

    public SquareCard(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        int width = getMeasuredWidth();
//        setMeasuredDimension(width, width);

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View v = getChildAt(i);
            v.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight(), MeasureSpec.EXACTLY));
        }
    }
}
