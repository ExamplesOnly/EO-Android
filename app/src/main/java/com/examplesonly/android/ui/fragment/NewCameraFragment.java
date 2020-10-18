package com.examplesonly.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.examplesonly.android.databinding.FragmentNewCameraBinding;

public class NewCameraFragment extends Fragment {

    FragmentNewCameraBinding binding;

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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.cameraView.setLifecycleOwner(this);

    }
}