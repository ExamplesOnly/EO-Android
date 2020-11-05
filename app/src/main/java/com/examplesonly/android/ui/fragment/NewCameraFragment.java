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
import com.examplesonly.android.databinding.FragmentNewCameraBinding;
import com.examplesonly.gallerypicker.view.VideosFragment.VideoChooseListener;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Mode;
import java.io.File;

public class NewCameraFragment extends Fragment {

    private FragmentNewCameraBinding binding;
    private boolean isRecording;
    private String filename = "video.mp4";
    private String filepath = "videos";
    private File myExternalFile;
    private Context context;
    private VideoChooseListener mVideoChooseListener;

    public static NewCameraFragment newInstance(String param1, String param2) {
        NewCameraFragment fragment = new NewCameraFragment();
        return fragment;
    }

    public NewCameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentNewCameraBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        context = inflater.getContext();
        return view;
    }

    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);

        if (context instanceof VideoChooseListener) {
            mVideoChooseListener = (VideoChooseListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement VideoChooseListener");
        }
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.cameraView.setLifecycleOwner(this);
        binding.cameraView.setMode(Mode.VIDEO);

        binding.cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onVideoTaken(VideoResult result) {
                // Video was taken!
                // Use result.getFile() to access a file holding
                // the recorded video.
                mVideoChooseListener.onVideoChosen(result.getFile().getPath());
            }
        });

        binding.record.setOnClickListener(v -> {
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
        });

    }
}