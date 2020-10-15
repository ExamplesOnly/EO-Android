package com.examplesonly.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.examplesonly.android.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewGalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewGalleryFragment extends Fragment {

    public static NewGalleryFragment newInstance(String param1, String param2) {
        NewGalleryFragment fragment = new NewGalleryFragment();
        return fragment;
    }

    public NewGalleryFragment() {
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
        return inflater.inflate(R.layout.fragment_new_gallery, container, false);
    }
}