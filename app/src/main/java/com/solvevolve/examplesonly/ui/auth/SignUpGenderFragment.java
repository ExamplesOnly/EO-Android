package com.solvevolve.examplesonly.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.solvevolve.examplesonly.R;
import com.solvevolve.examplesonly.databinding.FragmentSignUpGenderBinding;
import timber.log.Timber;

public class SignUpGenderFragment extends Fragment {

    private static final String ARG_GENDER = "gender";

    FragmentSignUpGenderBinding binding;
    private String gender;

    public SignUpGenderFragment() {
        // Required empty public constructor
    }

    public static SignUpGenderFragment newInstance(String gender) {
        SignUpGenderFragment fragment = new SignUpGenderFragment();
        Bundle args = new Bundle();
        if (!TextUtils.isEmpty(gender)) {
            args.putString(ARG_GENDER, gender);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gender = getArguments().getString(ARG_GENDER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignUpGenderBinding.inflate(getLayoutInflater());

        if (!TextUtils.isEmpty(gender)) {
            switch (gender) {
                case "Male":
                    binding.chipMale.setChecked(true);
                    break;
                case "Female":
                    binding.chipFemale.setChecked(true);
                    break;
                case "Other":
                    binding.chipOthers.setChecked(true);
                    break;
            }
        }

        binding.chipGroup.setSingleSelection(true);
        binding.chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Timber.e(String.valueOf(checkedId));
            SignUpFragment parentFragment = (SignUpFragment) getParentFragment();

            if (checkedId == R.id.chip_male) {
                parentFragment.setGender("Male");
            } else if (checkedId == R.id.chip_female) {
                parentFragment.setGender("Female");
            } else if (checkedId == R.id.chip_others) {
                parentFragment.setGender("Other");
            }
        });

        return binding.getRoot();
    }
}