package com.examplesonly.android.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.examplesonly.android.R;
import com.examplesonly.android.handler.OnToggleListener;
import com.like.Icon;
import com.like.OnAnimationEndListener;
import com.like.Utils;

import java.util.List;

public class ToggleIconButton extends FrameLayout implements View.OnClickListener {

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private ImageView icon;
    private OnToggleListener toggleListener;
    private OnAnimationEndListener animationEndListener;
    private int iconSize;

    private float animationScaleFactor;

    private boolean isChecked;


    private boolean isEnabled;
    private AnimatorSet animatorSet;

    private Drawable toggleEnableDrawable;
    private Drawable toggleDisableDrawable;

    public ToggleIconButton(@NonNull Context context) {
        super(context);
    }

    public ToggleIconButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleIconButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (!isInEditMode())
            init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater.from(getContext()).inflate(R.layout.likeview, this, true);
        icon = findViewById(R.id.icon);

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ToggleIconButton, defStyle, 0);

        iconSize = array.getDimensionPixelSize(R.styleable.ToggleIconButton_icon_size, -1);
        if (iconSize == -1)
            iconSize = 40;

        toggleEnableDrawable = getDrawableFromResource(array, R.styleable.ToggleIconButton_toggle_enable_drawable);

        if (toggleEnableDrawable != null)
            setToggleEnableDrawable(toggleEnableDrawable);

        toggleDisableDrawable = getDrawableFromResource(array, R.styleable.ToggleIconButton_toggle_disable_drawable);

        if (toggleDisableDrawable != null)
            setToggleDisableDrawable(toggleDisableDrawable);

        if (toggleEnableDrawable == null || toggleDisableDrawable == null) {
            throw new RuntimeException("Enable drawable and disable drawable both are required.");
        }

        setEnabled(array.getBoolean(R.styleable.ToggleIconButton_is_enabled, true));
        Boolean status = array.getBoolean(R.styleable.ToggleIconButton_toggle_enabled, false);
        setAnimationScaleFactor(array.getFloat(R.styleable.ToggleIconButton_anim_scale_factor, 3));
        setToggled(status);
        setOnClickListener(this);
        array.recycle();
    }

    @Override
    public void onClick(View v) {
        if (!isEnabled)
            return;

        isChecked = !isChecked;

        icon.setImageDrawable(isChecked ? toggleEnableDrawable : toggleDisableDrawable);

        if (toggleListener != null) {
            if (isChecked) {
                toggleListener.liked(this);
            } else {
                toggleListener.unLiked(this);
            }
        }

        if (animatorSet != null) {
            animatorSet.cancel();
        }

        if (isChecked) {
            this.animate().cancel();
            this.setScaleX(0);
            this.setScaleY(0);

            animatorSet = new AnimatorSet();

            ObjectAnimator starScaleYAnimator = ObjectAnimator.ofFloat(this, ImageView.SCALE_Y, 0.2f, 1f);
            starScaleYAnimator.setDuration(350);
            starScaleYAnimator.setStartDelay(250);
            starScaleYAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

            ObjectAnimator starScaleXAnimator = ObjectAnimator.ofFloat(this, ImageView.SCALE_X, 0.2f, 1f);
            starScaleXAnimator.setDuration(350);
            starScaleXAnimator.setStartDelay(250);
            starScaleXAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);
            animatorSet.playTogether(
                    starScaleYAnimator,
                    starScaleXAnimator
            );

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    icon.setScaleX(1);
                    icon.setScaleY(1);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (animationEndListener != null) {
//                        animationEndListener.onAnimationEnd(LikeButton.this);
                    }
                }
            });

            animatorSet.start();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled)
            return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /*
                Commented out this line and moved the animation effect to the action up event due to
                conflicts that were occurring when library is used in sliding type views.

                icon.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150).setInterpolator(DECCELERATE_INTERPOLATOR);
                */
                setPressed(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                boolean isInside = (x > 0 && x < getWidth() && y > 0 && y < getHeight());
                if (isPressed() != isInside) {
                    setPressed(isInside);
                }
                break;

            case MotionEvent.ACTION_UP:
                this.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150).setInterpolator(DECCELERATE_INTERPOLATOR);
                this.animate().scaleX(1).scaleY(1).setInterpolator(DECCELERATE_INTERPOLATOR);
                if (isPressed()) {
                    performClick();
                    setPressed(false);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
                break;
        }
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public void setToggleEnableDrawable(Drawable likeDrawable) {
        this.toggleEnableDrawable = likeDrawable;

        if (isChecked) {
            icon.setImageDrawable(this.toggleEnableDrawable);
        }
    }

    public void setToggleEnableDrawable(@DrawableRes int resId) {
        toggleEnableDrawable = ContextCompat.getDrawable(getContext(), resId);

        if (iconSize != 0) {
            toggleEnableDrawable = Utils.resizeDrawable(getContext(), toggleEnableDrawable, iconSize, iconSize);
        }

        if (isChecked) {
            icon.setImageDrawable(toggleEnableDrawable);
        }
    }

    public void setToggleDisableDrawable(Drawable unLikeDrawable) {
        this.toggleDisableDrawable = unLikeDrawable;

        if (!isChecked) {
            icon.setImageDrawable(this.toggleEnableDrawable);
        }
    }

    public void setToggleDisableDrawable(@DrawableRes int resId) {
        toggleDisableDrawable = ContextCompat.getDrawable(getContext(), resId);

        if (iconSize != 0) {
            toggleDisableDrawable = Utils.resizeDrawable(getContext(), toggleDisableDrawable, iconSize, iconSize);
        }

        if (!isChecked) {
            icon.setImageDrawable(toggleDisableDrawable);
        }
    }


    public void setToggled(Boolean status) {
        if (status) {
            isChecked = true;
            icon.setImageDrawable(toggleEnableDrawable);
        } else {
            isChecked = false;
            icon.setImageDrawable(toggleDisableDrawable);
        }
    }

    private Drawable getDrawableFromResource(TypedArray array, int styleableIndexId) {
        int id = array.getResourceId(styleableIndexId, -1);

        return (-1 != id) ? ContextCompat.getDrawable(getContext(), id) : null;
    }

    private Icon parseIconType(String iconType) {
        List<Icon> icons = Utils.getIcons();

        for (Icon icon : icons) {
            if (icon.getIconType().name().toLowerCase().equals(iconType.toLowerCase())) {
                return icon;
            }
        }

        throw new IllegalArgumentException("Correct icon type not specified.");
    }

    private void setEffectsViewSize() {
        if (iconSize != 0) {
//            dotsView.setSize((int) (iconSize * animationScaleFactor), (int) (iconSize * animationScaleFactor));
//            circleView.setSize(iconSize, iconSize);
        }
    }

    public void setAnimationScaleFactor(float animationScaleFactor) {
        this.animationScaleFactor = animationScaleFactor;

        setEffectsViewSize();
    }

}
