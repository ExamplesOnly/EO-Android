package com.solvevolve.examplesonly.ui.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.solvevolve.examplesonly.R;
import com.solvevolve.examplesonly.account.UserDataProvider;
import com.solvevolve.examplesonly.databinding.FragmentChangePasswordBinding;
import com.solvevolve.examplesonly.network.Api;
import com.solvevolve.examplesonly.network.auth.AuthInterface;
import com.solvevolve.examplesonly.network.user.UserInterface;
import com.solvevolve.examplesonly.ui.activity.EditProfileActivity;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ChangePasswordFragment extends Fragment {

    private FragmentChangePasswordBinding binding;
    private String currentPass = "", newPass = "", confirmPass = "";
    private boolean isDataValid = false;

    private AuthInterface authInterface;
    private UserDataProvider userDataProvider;

    public ChangePasswordFragment() {
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
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);

        setHasOptionsMenu(true);
        init();

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.save_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        SpannableString s = new SpannableString("SAVE");
        if (isDataValid) {
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary, getActivity().getTheme()))
                    , 0, s.length(), 0);

            menu.getItem(0).setTitle(s);
            menu.getItem(0).setEnabled(true);
        } else {
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.md_grey_700, getActivity().getTheme()))
                    , 0, s.length(), 0);

            menu.getItem(0).setTitle(s);
            menu.getItem(0).setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == R.id.save) {
            updatePassword();
        }
        return true;
    }

    void init() {
        authInterface = new Api(getContext()).getClient().create(AuthInterface.class);
        userDataProvider = UserDataProvider.getInstance(getContext());
        ((EditProfileActivity) getActivity()).getSupportActionBar().setTitle("Change Password");

        binding.currentPasswordTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentPass = s.toString();
                validate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.newPasswordTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newPass = s.toString();
                validate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.confirmPasswordTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmPass = s.toString();
                validate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.newPasswordTxt.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && newPass.length() > 0 && newPass.length() < 8) {
                Snackbar.make(getView(), "Password must be at least 8 characters long", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.error, getActivity().getTheme())).show();
            } else if (!hasFocus && confirmPass.length() > 0 && !newPass.equals(confirmPass)) {
                Snackbar.make(getView(), "Password does not match", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.error, getActivity().getTheme())).show();
            }
        });

        binding.confirmPasswordTxt.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && newPass.length() > 0 && confirmPass.length() > 0 && !newPass.equals(confirmPass)) {
                Snackbar.make(getView(), "Password does not match", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.error, getActivity().getTheme())).show();
            }
        });
    }

    private void updatePassword() {
        isLoading(true);
        authInterface.changePassword(userDataProvider.getUserEmail(), currentPass, newPass).enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                if (response.isSuccessful()) {
                    Timber.e("updatePassword %s", response.body().toString());
                    Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {

                    try {
                        Timber.e("updatePassword %s", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                }
                isLoading(false);
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                t.printStackTrace();
                isLoading(false);
                Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isLoading(boolean loading) {
        binding.currentPasswordField.setEnabled(!loading);
        binding.newPasswordField.setEnabled(!loading);
        binding.confirmPasswordField.setEnabled(!loading);
    }

    private void validate() {
        Timber.e("validate");
        if (currentPass.length() >= 8 && newPass.length() >= 8 && newPass.equals(confirmPass)) {
            isDataValid = true;
        } else {
            isDataValid = false;
        }
        getActivity().invalidateOptionsMenu();
    }
}