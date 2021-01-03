package com.examplesonly.android.ui.auth;

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
import com.examplesonly.android.databinding.FragmentSignUpEmailBinding;

public class SignUpEmailFragment extends Fragment {

    private static final String ARG_EMAIL = "email";

    private FragmentSignUpEmailBinding binding;
    private String email;

    public SignUpEmailFragment() {
        // Required empty public constructor
    }

    public static SignUpEmailFragment newInstance(String email) {
        SignUpEmailFragment fragment = new SignUpEmailFragment();
        Bundle args = new Bundle();
        if (!TextUtils.isEmpty(email)) {
            args.putString(ARG_EMAIL, email);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(ARG_EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignUpEmailBinding.inflate(getLayoutInflater());

        if (!TextUtils.isEmpty(email)) {
            binding.emailTxt.setText(email);
        }

        binding.emailTxt.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        binding.emailTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
                SignUpFragment parentFragment = (SignUpFragment) getParentFragment();
                parentFragment.setEmail(binding.emailTxt.getText().toString());
            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });

        return binding.getRoot();
    }
}