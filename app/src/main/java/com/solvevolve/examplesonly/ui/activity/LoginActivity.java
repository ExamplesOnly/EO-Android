package com.solvevolve.examplesonly.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.solvevolve.examplesonly.R;
import com.solvevolve.examplesonly.databinding.ActivityLoginVideoBinding;
import com.solvevolve.examplesonly.ui.auth.AuthFragment;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginVideoBinding binding;
    private Boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction()
                .replace(binding.loginRoot.getId(), new AuthFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.login_root);
        if (fragment instanceof AuthFragment) {
            finish();
        }

        if (!loading) {
            super.onBackPressed();
        }
    }

    public void isLoading(boolean loading) {
        this.loading = loading;
    }
}