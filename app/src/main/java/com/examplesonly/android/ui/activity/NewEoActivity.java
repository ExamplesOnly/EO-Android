package com.examplesonly.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.examplesonly.android.R;
import com.examplesonly.android.adapter.NewEoTabsAdapter;
import com.examplesonly.android.databinding.ActivityNewEoBinding;
import com.examplesonly.gallerypicker.view.VideosFragment;

public class NewEoActivity extends AppCompatActivity {

    private ActivityNewEoBinding binding;
    private NewEoTabsAdapter mNewEoTabsAdapter;
    private FragmentManager fragmentManager;

    // tab titles
    private String[] titles = new String[]{"Gallery", "Camera"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewEoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        initialize();
    }

    void initialize() {
        fragmentManager = getSupportFragmentManager();
        mNewEoTabsAdapter = new NewEoTabsAdapter(this);

        setFragment(new VideosFragment());
    }

    public void setFragment(Fragment frag) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(R.id.root) == null) {
            fm.beginTransaction().replace(R.id.root, frag).commit();
        }

    }

}
