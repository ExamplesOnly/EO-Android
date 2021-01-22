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

    private EoAlertDialog(@NonNull final Context context) {
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

    private Drawable getDialogIcon() {
        return this.dialogIcon;
    }

    private void setDialogIcon(Drawable dialogIcon) {
        this.dialogIcon = dialogIcon;
    }

    private int getIconTint() {
        return this.iconTint;
    }

    private void setIconTint(int iconTint) {
        this.iconTint = iconTint;
    }

    private String getTitle() {
        return title;
    }

    private void setTitle(final String title) {
        this.title = title;
    }

    private String getDescription() {
        return description;
    }

    private void setDescription(final String description) {
        this.description = description;
    }

    private String getPositiveText() {
        return positiveText;
    }

    private void setPositiveText(final String positiveText) {
        this.positiveText = positiveText;
    }

    private String getNegativeText() {
        return negativeText;
    }

    private void setNegativeText(final String negativeText) {
        this.negativeText = negativeText;
    }

    private ClickListener getPositiveClickListener() {
        return mPositiveClickListener;
    }

    private void setPositiveClickListener(final ClickListener positiveClickListener) {
        mPositiveClickListener = positiveClickListener;
    }

    private ClickListener getNegativeClickListener() {
        return mNegativeClickListener;
    }

    private void setNegativeClickListener(final ClickListener negativeClickListener) {
        mNegativeClickListener = negativeClickListener;
    }

    private Boolean getCenterText() {
        return isCenterText;
    }

    private void setCenterText(Boolean centerText) {
        isCenterText = centerText;
    }

    public static class Builder {
        private EoAlertDialog alertDialog;

        public Builder(Context context) {
            this.alertDialog = new EoAlertDialog(context);
        }

        public Builder setDialogIcon(Drawable dialogIcon) {
            this.alertDialog.setDialogIcon(dialogIcon);
            return this;
        }

        public Builder setIconTint(int iconTint) {
            this.alertDialog.setIconTint(iconTint);
            return this;
        }

        public Builder setTitle(String title) {
            this.alertDialog.setTitle(title);
            return this;
        }

        public Builder setDescription(String description) {
            this.alertDialog.setDescription(description);
            return this;
        }

        public Builder setPositiveText(String positiveText) {
            this.alertDialog.setPositiveText(positiveText);
            return this;
        }

        public Builder setNegativeText(String negativeText) {
            this.alertDialog.setNegativeText(negativeText);
            return this;
        }

        public Builder setPositiveClickListener(final ClickListener positiveClickListener) {
            this.alertDialog.setPositiveClickListener(positiveClickListener);
            return this;
        }

        public Builder setNegativeClickListener(final ClickListener negativeClickListener) {
            this.alertDialog.setNegativeClickListener(negativeClickListener);
            return this;
        }

        public Builder setCenterText(boolean centerText) {
            this.alertDialog.setCenterText(centerText);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.alertDialog.setCancelable(cancelable);
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.alertDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
            return this;
        }

        public EoAlertDialog create() {
            return this.alertDialog;
        }
    }

    private int pxToDp(int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
