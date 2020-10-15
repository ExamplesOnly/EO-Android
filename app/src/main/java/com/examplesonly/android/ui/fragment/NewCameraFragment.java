package com.examplesonly.android.ui.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.examplesonly.android.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewCameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewCameraFragment extends Fragment {

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_camera, container, false);
    }
}