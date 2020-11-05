package com.examplesonly.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());

        binding.logoutBtn.setOnClickListener(view1 -> {
            new UserDataProvider(this).logout();
            finish();
        });

        View view = binding.getRoot();
        setContentView(view);
    }
}