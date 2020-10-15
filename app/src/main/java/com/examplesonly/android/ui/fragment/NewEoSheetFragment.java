package com.examplesonly.android.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.examplesonly.android.databinding.FragmentNewEoSheetBinding;
import com.examplesonly.android.ui.activity.NewEoActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class NewEoSheetFragment extends BottomSheetDialogFragment {

    private FragmentNewEoSheetBinding binding;

    public NewEoSheetFragment() {
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

        binding = FragmentNewEoSheetBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        init();

        return view;
    }

    void init() {
        binding.galleryCard.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), NewEoActivity.class));
            dismiss();
        });
    }
}