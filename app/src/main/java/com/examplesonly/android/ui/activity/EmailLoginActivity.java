package com.examplesonly.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.examplesonly.android.databinding.ActivityEmailLoginBinding;

public class EmailLoginActivity extends AppCompatActivity {

    private ActivityEmailLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

}
