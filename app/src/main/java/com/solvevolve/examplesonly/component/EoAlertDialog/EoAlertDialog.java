package com.solvevolve.examplesonly.component.EoAlertDialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.solvevolve.examplesonly.databinding.ViewAlertDialogBinding;
public class EoAlertDialog extends AlertDialog {

    private String title;
    private String description;
    private String positiveText;
    private String negativeText;
    private ClickListener mPositiveClickListener;
    private ClickListener mNegativeClickListener;

    private ViewAlertDialogBinding binding;
    private Context context;

    public EoAlertDialog(@NonNull final Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        binding = ViewAlertDialogBinding.inflate(getLayoutInflater());
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(binding.getRoot());

        setView();
    }

    private void setView() {
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
}
