package com.solvevolve.examplesonly.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.google.android.material.card.MaterialCardView;

public class ExampleCard extends MaterialCardView {

    public ExampleCard(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = (int) (width / 1.7778);

        setMeasuredDimension(width, height);

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View v = getChildAt(i);
            v.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight(), MeasureSpec.EXACTLY));
        }
    }
}
