package com.examplesonly.android.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.examplesonly.android.R;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.databinding.ActivitySettingsBinding;
import com.examplesonly.android.ui.fragment.AddDemandFragment;
import com.examplesonly.android.ui.fragment.CameraFragment;
import com.examplesonly.android.ui.settings.MainSettings;
import com.examplesonly.gallerypicker.view.VideosFragment;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.topAppBar);

        binding.topAppBar.setNavigationOnClickListener(v -> onBackPressed());

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.root, new MainSettings(), "MainSettings");

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root);
        if (fragment instanceof MainSettings) {
            finish();
        }

        super.onBackPressed();
    }
}