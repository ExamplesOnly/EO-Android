package com.solvevolve.examplesonly.ui.activity;

import static com.solvevolve.examplesonly.ui.fragment.CameraFragment.MODE_VIDEO;
import static org.buffer.android.thumby.ThumbyActivity.EXTRA_THUMBNAIL_POSITION;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.solvevolve.examplesonly.R;
import com.solvevolve.examplesonly.databinding.ActivityNewEoBinding;
import com.solvevolve.examplesonly.model.Demand;
import com.solvevolve.examplesonly.ui.fragment.AddDemandFragment;
import com.solvevolve.examplesonly.ui.fragment.CameraFragment;
import com.solvevolve.examplesonly.ui.fragment.PostVideoFragment;
import com.solvevolve.gallerypicker.view.VideosFragment;
import com.solvevolve.gallerypicker.view.VideosFragment.VideoChooseListener;
import org.jetbrains.annotations.NotNull;

public class NewEoActivity extends AppCompatActivity implements VideoChooseListener {

    public static final int FRAGMENT_CHOOSE_VIDEO = 0;
    public static final int FRAGMENT_CAMERA = 1;
    public static final int FRAGMENT_EDIT_VIDEO = 2;
    public static final int FRAGMENT_NEW_DEMAND = 3;

    public static final String ARG_LAUNCH_MODE = "mode";
    public static final String ARG_DEMAND = "demand";

    private ActivityNewEoBinding binding;
    private final FragmentManager fm = getSupportFragmentManager();
    private ThumbnailChooseListener thumbnailChooseListener;
    private Demand demand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewEoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        final Bundle bundle = getIntent().getExtras();
        int mode = bundle.getInt(ARG_LAUNCH_MODE);
        demand = bundle.getParcelable(ARG_DEMAND);

        switch (mode) {
            case FRAGMENT_CHOOSE_VIDEO:
                switchFragment(mode, "choose_video", null);
                break;
            case FRAGMENT_CAMERA:
                switchFragment(mode, "record_video", null);
                break;
            case FRAGMENT_NEW_DEMAND:
                switchFragment(mode, "new_demand", null);
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
        if (fragment instanceof VideosFragment || fragment instanceof CameraFragment
                || fragment instanceof AddDemandFragment) {
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
            case FRAGMENT_CAMERA:
                if (fragment == null) {
                    fragment = CameraFragment.newInstance(MODE_VIDEO);
                }
                break;
            case FRAGMENT_CHOOSE_VIDEO:
                if (fragment == null) {
                    fragment = new VideosFragment();
                }
                break;
            case FRAGMENT_EDIT_VIDEO:
                if (fragment == null) {
                    fragment = PostVideoFragment.newInstance(data, demand);
                    thumbnailChooseListener = (ThumbnailChooseListener) fragment;
                }
                break;
            case FRAGMENT_NEW_DEMAND:
                if (fragment == null) {
                    fragment = new AddDemandFragment();
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
