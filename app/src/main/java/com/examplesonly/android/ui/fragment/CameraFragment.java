package com.examplesonly.android.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import com.examplesonly.android.R;
import com.examplesonly.android.databinding.FragmentCameraBinding;
import com.examplesonly.gallerypicker.view.PhotosFragment.ImageChooseListener;
import com.examplesonly.gallerypicker.view.VideosFragment.VideoChooseListener;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Mode;
import java.io.File;
import java.util.Objects;

public class CameraFragment extends Fragment {

    private static final String ARG_CAMERA_MODE = "camera_mode";
    public static final int MODE_STILL = 101;
    public static final int MODE_VIDEO = 102;

    private FragmentCameraBinding binding;
    private boolean isRecording;
    private String filename = "video.mp4";
    private String filepath = "videos";
    private File myExternalFile;
    private Context context;
    private VideoChooseListener mVideoChooseListener;
    private ImageChooseListener mImageChooseListener;

    private int cameraMode = 0;

    public static CameraFragment newInstance(int mode) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CAMERA_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        if (getArguments() != null) {
            cameraMode = getArguments().getInt(ARG_CAMERA_MODE);
        } else {
            throw new RuntimeException("Pass a camera mode");
        }

        if (cameraMode == MODE_STILL) {
            if (getContext() instanceof ImageChooseListener) {
                mImageChooseListener = (ImageChooseListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement ImageChooseListener");
            }
        } else if (cameraMode == MODE_VIDEO) {
            if (getContext() instanceof VideoChooseListener) {
                mVideoChooseListener = (VideoChooseListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement VideoChooseListener");
            }
        } else {
            throw new RuntimeException("Wrong camera mode passed");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentCameraBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initCamera(cameraMode);

        binding.cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onVideoTaken(VideoResult result) {
                mVideoChooseListener.onVideoChosen(result.getFile().getPath());
            }

            @Override
            public void onPictureTaken(@NonNull final PictureResult result) {
                result.toFile(
                        new File(Objects.requireNonNull(getActivity()).getExternalFilesDir(filepath), "photo.jpg"), new FileCallback() {
                            @Override
                            public void onFileReady(@Nullable final File file) {
                                if (file != null) {
                                    mImageChooseListener.onImageChosen(file.getAbsolutePath());
                                }
                            }
                        });
            }
        });

        binding.record.setOnClickListener(v -> {
            if (cameraMode == MODE_STILL) {
                binding.cameraView.takePicture();
            } else if (cameraMode == MODE_VIDEO) {
                if (!isRecording) {
                    myExternalFile = new File(getActivity().getExternalFilesDir(filepath), filename);
                    binding.cameraView.takeVideo(myExternalFile);
                    binding.record.setBackground(
                            ResourcesCompat.getDrawable(getResources(), R.drawable.bg_camera_btn_clicked, null));
//                binding.record.setText("Stop Recording");
                } else {
//                binding.record.setText("Record");
                    binding.cameraView.stopVideo();
                    binding.record.setBackground(
                            ResourcesCompat.getDrawable(getResources(), R.drawable.bg_camera_btn_normal, null));
                }
                isRecording = !isRecording;
            }
        });
    }

    void initCamera(int mode) {
        binding.cameraView.setLifecycleOwner(getViewLifecycleOwner());
        if (mode == MODE_STILL) {
            binding.topAppBar.setTitle("Take Photo");
            binding.cameraView.setMode(Mode.PICTURE);
        } else if (mode == MODE_VIDEO) {
            binding.topAppBar.setTitle("Record Video");
            binding.cameraView.setMode(Mode.VIDEO);
//            binding.cameraView.setPict
        }
    }
}