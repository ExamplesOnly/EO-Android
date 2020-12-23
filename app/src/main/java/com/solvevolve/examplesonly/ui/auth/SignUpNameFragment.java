package com.solvevolve.examplesonly.ui.auth;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import androidx.fragment.app.Fragment;
import com.solvevolve.examplesonly.databinding.FragmentSignUpNameBinding;
import org.jetbrains.annotations.NotNull;

public class SignUpNameFragment extends Fragment {

    private static final String ARG_NAME = "name";

    private FragmentSignUpNameBinding binding;
    private String name;

    public SignUpNameFragment() {
        // Required empty public constructor
    }

    public static SignUpNameFragment newInstance(String name) {
        SignUpNameFragment fragment = new SignUpNameFragment();
        Bundle args = new Bundle();
        if (!TextUtils.isEmpty(name)) {
            args.putString(ARG_NAME, name);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentSignUpNameBinding.inflate(getLayoutInflater());

        if (!TextUtils.isEmpty(name)) {
            binding.nameTxt.setText(name);
        }

        binding.nameTxt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        binding.nameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
                SignUpFragment parentFragment = (SignUpFragment) getParentFragment();
                parentFragment.setName(binding.nameTxt.getText().toString());
            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });

        return binding.getRoot();
    }
}