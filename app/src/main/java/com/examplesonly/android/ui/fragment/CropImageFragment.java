package com.examplesonly.android.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.examplesonly.android.R;
import com.examplesonly.android.databinding.FragmentCropImageBinding;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.yalantis.ucrop.UCropFragment;
import java.io.File;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class CropImageFragment extends Fragment {

    private static final String ARG_SOURCE_IMAGE = "source_image";
    private static final String ARG_CROP_RATIO = "crop_ratio";
    private FragmentCropImageBinding binding;
    private String sourceImage;
    private int[] cropRatio = {1, 1};

    private UCrop ucrop;
    private UCrop.Options uCropOptions;
    private UCropFragment mFragment;

    public static CropImageFragment newInstance(String sourceImage, int[] aspectRatio) {
        CropImageFragment fragment = new CropImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SOURCE_IMAGE, sourceImage);
        args.putIntArray(ARG_CROP_RATIO, aspectRatio);
        fragment.setArguments(args);
        return fragment;
    }

    public CropImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sourceImage = getArguments().getString(ARG_SOURCE_IMAGE);
            cropRatio = getArguments().getIntArray(ARG_CROP_RATIO);
        }

        uCropOptions = new UCrop.Options();
        uCropOptions.setCompressionFormat(CompressFormat.WEBP);
        uCropOptions.setCompressionQuality(50);
        uCropOptions.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        uCropOptions.setRootViewBackgroundColor(getResources().getColor(R.color.white, Objects
                .requireNonNull(getActivity()).getTheme()));

        Uri imageUri = Uri.fromFile(new File(sourceImage));
        ucrop = UCrop
                .of(imageUri, Uri.fromFile(
                        new File(getActivity().getCacheDir(), System.currentTimeMillis() + ".webp")))
                .withAspectRatio(cropRatio[0], cropRatio[1])
                .withOptions(uCropOptions);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCropImageBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        binding.topAppBar.setNavigationOnClickListener(view1 -> {
            Objects.requireNonNull(getActivity()).onBackPressed();
        });

        binding.topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.publish && mFragment != null) {
                mFragment.cropAndSaveImage();
                return true;
            }
            return false;
        });

        FragmentManager fm = getChildFragmentManager();
        mFragment = ucrop.getFragment(ucrop.getIntent(
                Objects.requireNonNull(getContext())).getExtras());
        fm.beginTransaction().replace(R.id.fragment_holder, mFragment)
                .commit();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}