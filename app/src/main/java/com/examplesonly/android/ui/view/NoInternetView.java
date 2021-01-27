package com.examplesonly.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.examplesonly.android.R;

public class NoInternetView extends LinearLayout {

    ConstraintLayout lottieView;
    TextView title, description;
    Button refresh;

    public NoInternetView(Context context) {
        super(context);
        initView();
    }

    public NoInternetView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_no_internet, this);

        lottieView = findViewById(R.id.lottieView);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        refresh = findViewById(R.id.refresh);

        isOffline(false);
    }

    public void onRefreshListener(OnClickListener clickListener) {
        refresh.setOnClickListener(clickListener);
    }

    public void isOffline(boolean offline) {
        int visibility = offline ? View.VISIBLE : View.GONE;

        lottieView.setVisibility(visibility);
        title.setVisibility(visibility);
        description.setVisibility(visibility);
        refresh.setVisibility(visibility);
        invalidate();
    }

}
