package com.solvevolve.examplesonly.ui.activity;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.solvevolve.examplesonly.account.UserDataProvider;
import com.solvevolve.examplesonly.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());

        binding.logoutBtn.setOnClickListener(view1 -> {
            UserDataProvider.getInstance(this).logout();
            finish();
        });

        View view = binding.getRoot();
        setContentView(view);
    }
}