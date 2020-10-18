package com.examplesonly.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import com.examplesonly.android.R;
import com.examplesonly.android.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    private final String TAG = SignupActivity.class.getCanonicalName();
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
    }

    void init() {
        String[] genders = {"Male", "Female", "Other", "Prefer not to say"};
        binding.genderTxt.setAdapter(new ArrayAdapter(this, R.layout.dropdown_list_item, genders));
    }
}
