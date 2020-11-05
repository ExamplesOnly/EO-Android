package com.examplesonly.android.ui.activity;

import static org.buffer.android.thumby.ThumbyActivity.EXTRA_THUMBNAIL_POSITION;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.examplesonly.android.R;
import com.examplesonly.android.databinding.ActivityNewEoBinding;
import com.examplesonly.android.ui.fragment.EditVideoFragment;
import com.examplesonly.android.ui.fragment.NewCameraFragment;
import com.examplesonly.gallerypicker.view.VideosFragment;
import com.examplesonly.gallerypicker.view.VideosFragment.VideoChooseListener;
import org.jetbrains.annotations.NotNull;

public class NewEoActivity extends AppCompatActivity implements VideoChooseListener {

    private final String TAG = NewEoActivity.class.getCanonicalName();
    private ActivityNewEoBinding binding;
    private final FragmentManager fm = getSupportFragmentManager();
    private Bundle mBundle;
    private ThumbnailChooseListener thumbnailChooseListener;

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
//                setFragment(new NewCameraFragment());
                switchFragment(mode, "choose_video", null);
                break;
            default:
//                setFragment(new VideosFragment());
                switchFragment(mode, "record_video", null);
                break;
        }
    }

    @Override
    public void onVideoChosen(@NotNull final String videoUri) {
        switchFragment(2, "edit_video", videoUri);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = fm.findFragmentById(R.id.root);
        if (fragment instanceof VideosFragment || fragment instanceof NewCameraFragment) {
            finish();
        }

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (thumbnailChooseListener != null && data != null) {
            long location = data.getLongExtra(EXTRA_THUMBNAIL_POSITION, 0);
            thumbnailChooseListener.onThumbnailChosen(location);
        }
    }

    public void setFragment(Fragment frag) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.root, frag).commit();
    }

    public void switchFragment(int step, String tag, String data) {
        Fragment fragment;
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragment = fm.findFragmentByTag(tag);

        switch (step) {
            case 1:
                if (fragment == null) {
                    fragment = new NewCameraFragment();
                }
                break;
            case 2:
                if (fragment == null) {
                    fragment = EditVideoFragment.newInstance(data);
                    if (fragment instanceof ThumbnailChooseListener) {
                        thumbnailChooseListener = (ThumbnailChooseListener) fragment;
                    }
                }
                break;
            default:
                if (fragment == null) {
                    fragment = new VideosFragment();
                }
                break;
        }

        fragmentTransaction.replace(R.id.root, fragment, tag);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public interface ThumbnailChooseListener {

        void onThumbnailChosen(long thumbPosition);
    }

}
