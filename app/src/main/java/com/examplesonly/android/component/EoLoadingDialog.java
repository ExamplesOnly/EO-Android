package com.examplesonly.android.component;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.examplesonly.android.databinding.ViewDialogAlertBinding;
import com.examplesonly.android.databinding.ViewDialogLoadingBinding;

public class EoLoadingDialog extends AlertDialog {

    private String loadingText;

    private ViewDialogLoadingBinding binding;
    private Context context;

    public EoLoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        binding = ViewDialogLoadingBinding.inflate(getLayoutInflater());
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(binding.getRoot());

        setView();
    }

    private void setView() {
        if (!TextUtils.isEmpty(loadingText)) {
            binding.description.setText(loadingText);
        }
    }

    public String getLoadingText() {
        return loadingText;
    }

    public EoLoadingDialog setLoadingText(String loadingText) {
        this.loadingText = loadingText;
        return this;
    }
}
