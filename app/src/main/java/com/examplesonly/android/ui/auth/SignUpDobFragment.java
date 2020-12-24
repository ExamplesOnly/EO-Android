package com.examplesonly.android.ui.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.examplesonly.android.databinding.FragmentSignUpDobBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

public class SignUpDobFragment extends Fragment {

    FragmentSignUpDobBinding binding;

    public SignUpDobFragment() {
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
        binding = FragmentSignUpDobBinding.inflate(getLayoutInflater());
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .setCalendarConstraints(new CalendarConstraints.Builder().build());
        MaterialDatePicker<?> picker = builder.build();
        binding.dobTxt.setOnClickListener(v -> {
            picker.show(getChildFragmentManager(), picker.toString());
        });

        picker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Object>) selection -> {
            String dob = picker.getHeaderText();
            binding.dobTxt.setText(dob);
        });

        binding.dobTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
                SignUpFragment parentFragment = (SignUpFragment) getParentFragment();
                parentFragment.setDob(true);
            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });

        return binding.getRoot();
    }
}