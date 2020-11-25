package com.examplesonly.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.examplesonly.android.databinding.ActivityEmailLoginBinding;
import com.examplesonly.android.model.User;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.auth.AuthInterface;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.network.user.UserInterface;
import java.io.IOException;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class EmailLoginActivity extends AppCompatActivity {

    private final String TAG = EmailLoginActivity.class.getCanonicalName();
    private ActivityEmailLoginBinding binding;
    private AuthInterface mAuthInterface;
    private UserInterface mUserInterface;
    private UserDataProvider mUserDataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
    }

    private void init() {
        mAuthInterface = new Api(this).getClient().create(AuthInterface.class);
        mUserInterface = new Api(this).getClient().create(UserInterface.class);
        mUserDataProvider = UserDataProvider.getInstance(this);
    }

    public void signIn(View view) {
        User user = new User();
        user.setEmail(binding.emailTxt.getText().toString());
        user.setPassword(binding.passwordTxt.getText().toString());

        isLoading(true);
        mAuthInterface.login(user).enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(@NotNull final Call<HashMap<String, String>> call,
                    @NotNull final Response<HashMap<String, String>> response) {
                if (response.isSuccessful()) {
                    String token = response.body().get("token");
                    Timber.e("login token: %s", token);
                    mUserDataProvider.saveToken(token);

                    mUserInterface = new Api(getApplicationContext()).getClient().create(UserInterface.class);
                    mUserInterface.me().enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(final Call<User> call,
                                final Response<User> response) {
                            if (response.isSuccessful()) {
                                mUserDataProvider.saveUserData(response.body());
                                Timber.e(response.body().toString());
                            } else {
                                try {
                                    Timber.e(response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            Intent main = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(main);
                            finish();
                        }

                        @Override
                        public void onFailure(final Call<User> call, final Throwable t) {

                        }
//                        @Override
//                        public void onResponse(final Call<User> call, final Response<HashMap<String, String>> response) {
//                            mUserDataProvider.saveUserData(response.body());
//                            Log.e(TAG, response.body().getFirstName() + " " + response.body().getLastName() + " "
//                                    + response.body().getEmail());
//
//                            Intent main = new Intent(getApplicationContext(), MainActivity.class);
//                            startActivity(main);
//                            finish();
//                        }
//
//                        @Override
//                        public void onFailure(final Call<User> call, final Throwable t) {
//
//                        }
                    });
                } else {
                    isLoading(false);
                    Toast.makeText(getApplication(), "Email or password incorrect", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(final Call<HashMap<String, String>> call, final Throwable t) {
                isLoading(false);
            }
        });
    }


    void isLoading(Boolean loading) {
        binding.emailTextField.setEnabled(!loading);
        binding.passwordTextField.setEnabled(!loading);
        binding.loginBtn.setEnabled(!loading);
    }
}
