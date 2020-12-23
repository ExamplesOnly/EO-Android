package com.solvevolve.examplesonly.ui.activity;

import static com.solvevolve.examplesonly.ui.fragment.CameraFragment.MODE_STILL;
import static com.yalantis.ucrop.UCrop.EXTRA_OUTPUT_URI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.solvevolve.examplesonly.R;
import com.solvevolve.examplesonly.ui.fragment.CameraFragment;
import com.solvevolve.examplesonly.ui.fragment.CropImageFragment;
import com.solvevolve.gallerypicker.view.PhotosFragment;
import com.solvevolve.gallerypicker.view.PhotosFragment.ImageChooseListener;
import com.solvevolve.gallerypicker.view.VideosFragment.VideoChooseListener;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.yalantis.ucrop.UCropFragment.UCropResult;
import com.yalantis.ucrop.UCropFragmentCallback;
import java.io.File;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class ProfileImageActivity extends AppCompatActivity
        implements ImageChooseListener, VideoChooseListener, UCropFragmentCallback {

    public static final int FRAGMENT_CHOOSE_IMAGE = 0;
    public static final int FRAGMENT_CAMERA = 1;
    public static final int FRAGMENT_CROP = 2;
    public static final String ARG_LAUNCH_MODE = "mode";
    public static final String ARG_CROP_RATIO = "crop_ratio";

    private int launchMode = FRAGMENT_CHOOSE_IMAGE;
    private int[] cropRatio = {1, 1};
    private ProgressDialog loadingProgressDialog;
    private UCrop.Options uCropOptions;
    private final FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            launchMode = bundle.getInt(ARG_LAUNCH_MODE);
            cropRatio = bundle.getIntArray(ARG_CROP_RATIO);
            if (cropRatio == null) {
                cropRatio = new int[]{1, 1};
            }
        }

        switch (launchMode) {
            case FRAGMENT_CAMERA:
                switchFragment(FRAGMENT_CAMERA, "click_image", null);
                break;
            default:
                switchFragment(FRAGMENT_CHOOSE_IMAGE, "choose_image", null);
                break;
        }

        uCropOptions = new UCrop.Options();
        uCropOptions.setCompressionFormat(Bitmap.CompressFormat.PNG);
        uCropOptions.setCompressionFormat(Bitmap.CompressFormat.PNG);
        uCropOptions.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        uCropOptions.setRootViewBackgroundColor(getResources().getColor(R.color.white, getTheme()));

        loadingProgressDialog = new ProgressDialog(this);
        loadingProgressDialog.create();
        loadingProgressDialog.setProgress(0);
        loadingProgressDialog.setCancelable(false);
        loadingProgressDialog.setIndeterminate(true);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root);
        if (fragment instanceof PhotosFragment || fragment instanceof CameraFragment) {
            finish();
        }

        super.onBackPressed();
    }

    @Override
    public void onImageChosen(@NotNull final String imageLocation) {
        File imageFile = new File(imageLocation);
        switchFragment(FRAGMENT_CROP, "crop_image", imageLocation);
    }

    @Override
    public void loadingProgress(final boolean showLoader) {
        if (showLoader) {
            loadingProgressDialog.setMessage("Cropping image...");
            loadingProgressDialog.show();
        } else {
            loadingProgressDialog.dismiss();
        }
    }

    @Override
    public void onCropFinish(final UCropResult result) {
        Timber.e("onCropFinish %s %s", result.mResultCode, RESULT_OK);
        switch (result.mResultCode) {
            case RESULT_OK:
                handleCropResult(result.mResultData);
                break;
            case UCrop.RESULT_ERROR:
                Toast.makeText(ProfileImageActivity.this, "Could not crop image", Toast.LENGTH_SHORT).show();
//                handleCropError(result.mResultData);
                break;
        }
    }

    @Override
    public void onVideoChosen(@NotNull final String videouri) {

    }

    public void switchFragment(int step, String tag, String data) {
        Fragment fragment;
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragment = fm.findFragmentByTag(tag);

        switch (step) {
            case FRAGMENT_CAMERA:
                if (fragment == null) {
                    fragment = CameraFragment.newInstance(MODE_STILL);
                    ;
                }
                break;
            case FRAGMENT_CHOOSE_IMAGE:
                if (fragment == null) {
                    fragment = new PhotosFragment();
//                    new UCrop
                }
                break;
            case FRAGMENT_CROP:
                if (fragment == null) {
                    fragment = CropImageFragment.newInstance(data, cropRatio);
                }
        }

        fragmentTransaction.replace(R.id.root, fragment, tag);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void handleCropResult(@NonNull Intent result) {
        Timber.e("handleCropResult");
        final Uri resultUri = UCrop.getOutput(result);
        Timber.e("resultUri %s", resultUri);
        if (resultUri != null) {
            setResult(RESULT_OK, new Intent().putExtra(EXTRA_OUTPUT_URI, resultUri));
            finish();
        } else {
            Toast.makeText(ProfileImageActivity.this, "Could not crop image", Toast.LENGTH_SHORT).show();
        }
    }
}