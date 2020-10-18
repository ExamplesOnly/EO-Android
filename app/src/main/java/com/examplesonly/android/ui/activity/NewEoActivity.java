package com.examplesonly.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.examplesonly.android.R;
import com.examplesonly.android.databinding.ActivityNewEoBinding;
import com.examplesonly.android.ui.fragment.NewCameraFragment;
import com.examplesonly.gallerypicker.view.VideosFragment;

public class NewEoActivity extends AppCompatActivity {

    private ActivityNewEoBinding binding;
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewEoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mBundle = getIntent().getExtras();
        int mode = mBundle.getInt("mode");

        switch (mode) {
            case 1:
                setFragment(new NewCameraFragment());
                break;
            default:
                setFragment(new VideosFragment());
                break;
        }
    }

    public void setFragment(Fragment frag) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.root, frag).commit();

    }

}
