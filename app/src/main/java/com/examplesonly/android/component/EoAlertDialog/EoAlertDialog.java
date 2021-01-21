package com.examplesonly.android.component.EoAlertDialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.examplesonly.android.databinding.ViewDialogAlertBinding;

public class EoAlertDialog extends AlertDialog {

    private String title;
    private String description;
    private String positiveText;
    private String negativeText;
    private Drawable dialogIcon;
    private int iconTint = -1;
    private ClickListener mPositiveClickListener;
    private ClickListener mNegativeClickListener;
    private Boolean isCenterText = false;

    private ViewDialogAlertBinding binding;
    private Context context;

    public EoAlertDialog(@NonNull final Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        binding = ViewDialogAlertBinding.inflate(getLayoutInflater());
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(binding.getRoot());

        setView();
    }

    private void setView() {

        if (dialogIcon != null) {
            binding.dialogIcon.setImageDrawable(dialogIcon);
            binding.dialogIcon.setVisibility(View.VISIBLE);
        } else {
            binding.dialogIcon.setVisibility(View.GONE);
        }

        if (iconTint != -1) {
            ImageViewCompat.setImageTintList(binding.dialogIcon, ColorStateList.valueOf(iconTint));
        }

        if (title != null) {
            binding.title.setText(title);
        }
        if (description != null) {
            binding.description.setText(description);
        }
        if (positiveText != null) {
            binding.positiveBtn.setText(positiveText);
        }
        if (negativeText != null) {
            binding.negativeBtn.setText(negativeText);
        } else {
            binding.negativeBtn.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(pxToDp(20), 0, pxToDp(20), pxToDp(14));
            binding.positiveBtn.setLayoutParams(params);
        }

        if (mNegativeClickListener != null) {
            binding.negativeBtn.setOnClickListener(v -> {
                mNegativeClickListener.onClick(this);
            });
        }
        if (mPositiveClickListener != null) {
            binding.positiveBtn.setOnClickListener(v -> {
                mPositiveClickListener.onClick(this);

            });
        }
    }

    public Drawable getDialogIcon() {
        return this.dialogIcon;
    }

    public EoAlertDialog setDialogIcon(Drawable dialogIcon) {
        this.dialogIcon = dialogIcon;
        return this;
    }

    public int getIconTint() {
        return this.iconTint;
    }

    public EoAlertDialog setIconTint(int iconTint) {
        this.iconTint = iconTint;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public EoAlertDialog setTitle(final String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public EoAlertDialog setDescription(final String description) {
        this.description = description;
        return this;
    }

    public String getPositiveText() {
        return positiveText;
    }

    public EoAlertDialog setPositiveText(final String positiveText) {
        this.positiveText = positiveText;
        return this;
    }

    public String getNegativeText() {
        return negativeText;
    }

    public EoAlertDialog setNegativeText(final String negativeText) {
        this.negativeText = negativeText;
        return this;
    }

    public ClickListener getPositiveClickListener() {
        return mPositiveClickListener;
    }

    public EoAlertDialog setPositiveClickListener(final ClickListener positiveClickListener) {
        mPositiveClickListener = positiveClickListener;
        return this;
    }

    public ClickListener getNegativeClickListener() {
        return mNegativeClickListener;
    }

    public EoAlertDialog setNegativeClickListener(final ClickListener negativeClickListener) {
        mNegativeClickListener = negativeClickListener;
        return this;
    }

    public Boolean getCenterText() {
        return isCenterText;
    }

    public EoAlertDialog setCenterText(Boolean centerText) {
        isCenterText = centerText;
        return this;
    }

    private int pxToDp(int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
