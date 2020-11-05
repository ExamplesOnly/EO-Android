package com.examplesonly.android.ui.activity;

import static com.examplesonly.android.account.UserDataProvider.TOKEN_KEY;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.examplesonly.android.R;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.databinding.ActivitySignupBinding;
import com.examplesonly.android.model.User;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.auth.AuthInterface;
import com.examplesonly.android.network.user.UserInterface;
import com.examplesonly.android.ui.view.KeyboardSensitiveRelativeLayout.OnKeyboardShowHideListener;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import java.io.IOException;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity implements OnKeyboardShowHideListener {

    private final String TAG = SignupActivity.class.getCanonicalName();
    private ActivitySignupBinding binding;
    private AuthInterface mAuthInterface;
    private UserInterface mUserInterface;
    private UserDataProvider mUserDataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
    }

    @Override
    public void onKeyboardShowHide(final boolean visible) {
        if (binding == null) {
            return;
        }

        if (visible) {
            binding.socialButtons.setVisibility(View.GONE);
        } else {
            binding.socialButtons.setVisibility(View.VISIBLE);
        }
    }

    void init() {
        String[] genders = {"Male", "Female", "Other", "Prefer not to say"};
        binding.genderTxt.setAdapter(new ArrayAdapter(this, R.layout.view_dropdown_list_item, genders));

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .setCalendarConstraints(new CalendarConstraints.Builder().build());
        MaterialDatePicker<?> picker = builder.build();
        binding.dobTxt.setOnClickListener(v -> {
            picker.show(getSupportFragmentManager(), picker.toString());
        });

        picker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Object>) selection -> {
            String dob = picker.getHeaderText();
            binding.dobTxt.setText(dob);
        });

        mUserDataProvider = new UserDataProvider(this);
        mAuthInterface = new Api(this).getClient().create(AuthInterface.class);
        mUserInterface = new Api(this).getClient().create(UserInterface.class);

        binding.signupRealtiveLayout.setKeyboardListener(this);
    }

    public void validateSignUp(View view) {
        User user = new User();
        user.setFirstName(binding.firstNameTxt.getText().toString());
        user.setLastName(binding.lastNameTxt.getText().toString());
        user.setEmail(binding.emailTxt.getText().toString());
        user.setGender(binding.genderTxt.getText().toString());
        user.setDob(binding.genderTxt.getText().toString());
        user.setGender(binding.dobTxt.getText().toString());
        user.setPassword(binding.passwordTxt.getText().toString());

        if (user.getFirstName().length() <= 3) {
            binding.firstNameTxt.requestFocus();
            binding.firstNameTextField.setError("First name must be minimum 3 characters");
        } else if (user.getLastName().length() == 0) {
            binding.lastNameTxt.requestFocus();
            binding.lastNameTextField.setError("Last name can not be empty");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailTxt.getText()).matches()) {
            binding.emailTxt.requestFocus();
            binding.emailTextField.setError("Enter a valid email address");
        } else if (binding.genderTxt.getText().length() == 0) {
            binding.genderTxt.requestFocus();
            binding.genderTextField.setError("Select gender");
        } else {
            createAccount(user);
        }
//        else if()
    }

    public void createAccount(User user) {
        isLoading(true);
        mAuthInterface.signUp(user).enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(final Call<HashMap<String, String>> call,
                    final Response<HashMap<String, String>> response) {

                if (response.isSuccessful()) {
                    String token = response.body().get(TOKEN_KEY);
                    mUserDataProvider.saveToken(token);
                    mUserInterface.me().enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(final Call<User> call, final Response<User> response) {
                            if (response.isSuccessful()) {
                                mUserDataProvider.saveUserData(response.body());
                                Log.e(TAG, response.body().toString());

                            } else {
                                try {
                                    Log.e(TAG, response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            Intent main = new Intent(getApplicationContext(), VerificationActivity.class);
                            startActivity(main);
                            finish();
                        }

                        @Override
                        public void onFailure(final Call<User> call, final Throwable t) {
                        }
                    });
                } else {
                    isLoading(false);
                    Toast.makeText(getApplication(), "Could not create account.", Toast.LENGTH_LONG).show();
                }

                try {
                    Log.e(TAG, "Success: " + response.toString() + " " + (response.body() != null ? response.body()
                            .toString()
                            : "No Body") + " " + (response.errorBody() != null ? response.errorBody().string()
                            : "NO ERROR"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(final Call<HashMap<String, String>> call, final Throwable t) {
                isLoading(false);
                t.printStackTrace();
            }
        });
    }

    void isLoading(Boolean loading) {
        binding.firstNameTextField.setEnabled(!loading);
        binding.lastNameTextField.setEnabled(!loading);
        binding.emailTextField.setEnabled(!loading);
        binding.genderTextField.setEnabled(!loading);
        binding.dobTextField.setEnabled(!loading);
        binding.passwordTextField.setEnabled(!loading);
        binding.signUpBtn.setEnabled(!loading);
        binding.googleLogin.setEnabled(!loading);
        binding.linkedinLogin.setEnabled(!loading);
    }
}
