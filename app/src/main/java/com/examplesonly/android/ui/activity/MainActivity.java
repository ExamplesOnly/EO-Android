package com.examplesonly.android.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.examplesonly.android.R;
import com.examplesonly.android.databinding.ActivityMainBinding;
import com.examplesonly.android.ui.fragment.NewEoSheetFragment;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getCanonicalName();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Log.e(TAG, item.getTitle().toString());

            switch (item.getItemId()) {
                case R.id.home:
                    break;
                case R.id.explore:
                    break;
                case R.id.add:
                    NewEoSheetFragment bottomSheet = new NewEoSheetFragment();
                    bottomSheet.show(getSupportFragmentManager(),
                            "ModalBottomSheet");
                    break;
                case R.id.notification:
                    break;
                case R.id.profile:
                    break;
            }

            return item.getItemId() != R.id.add;
        });

//        binding.newVideoBtn.setOnClickListener(view1 -> {
//            Intent newVideo = new Intent(this, NewEoActivity.class);
//            startActivity(newVideo);
//
//        });
    }
}