package com.examplesonly.android.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.TransitionManager;

import com.examplesonly.android.R;
import com.examplesonly.android.databinding.ActivityVerificationBinding;
import com.examplesonly.android.model.User;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.network.user.UserInterface;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.examplesonly.android.ui.activity.DynamicLinkActivity.VERIFICATION_TOKEN;

public class VerificationActivity extends AppCompatActivity {

    private final String TAG = VerificationActivity.class.getCanonicalName();
    private ActivityVerificationBinding binding;
    private UserInterface mUserInterface;
    private UserDataProvider mUserDataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVerificationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();

        String verificationLink = getIntent().getStringExtra(VERIFICATION_TOKEN);
        if (verificationLink != null) {

            Uri verifyUri = Uri.parse(verificationLink);
            String token = verifyUri.getQueryParameter("token");

            Timber.e("Token %s %s", verificationLink, token);

            verifyLink(token);
        } else {
            updateUser();
        }
    }

    void init() {
        mUserInterface = new Api(this).getClient().create(UserInterface.class);
        mUserDataProvider = UserDataProvider.getInstance(this);

        Timber.tag("VerificationActivity").e(mUserDataProvider.toString());

        binding.verifyTextTitle.setText(getResources().getString(R.string.verify_email_title, mUserDataProvider.getUserEmail()));

        binding.refreshBtn.setOnClickListener(v -> {
            updateUser();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        updateUser();

    }

    void updateUser() {
        isVerifying(true);
        mUserInterface.me().enqueue(new Callback<User>() {
            @Override
            public void onResponse(final Call<User> call,
                                   final Response<User> response) {
                if (response.isSuccessful()) {

                    Timber.e("updateUser %s %s", response.body().getEmail(), response.body().getFirstName());

                    mUserDataProvider.saveUserData(response.body());
                    if (mUserDataProvider.isVerified()) {
                        Intent main = new Intent(getApplicationContext(), ChooseCategoryActivity.class);
                        startActivity(main);
                        finish();
                    } else {
                        isVerifying(false);
                        Toast.makeText(VerificationActivity.this, "Ops! You are still not verified.", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    try {
                        Timber.e(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Timber.e("updateUser Not Successful");

                    isVerifying(false);
                    Toast.makeText(VerificationActivity.this, "Ops! You are still not verified.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(final Call<User> call, final Throwable t) {
                t.printStackTrace();
                isVerifying(false);

                Toast.makeText(VerificationActivity.this, "Ops! Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void verifyLink(String token) {
        mUserInterface.verifyAccount(token).enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(final @NotNull Call<HashMap<String, String>> call,
                                   final @NotNull Response<HashMap<String, String>> response) {
                if (response.isSuccessful()) {
                    updateUser();
                    Timber.tag("VerificationActivity").e("verifyLink SUCCESS");
                } else {
                    try {
                        Timber.tag("VerificationActivity").e("verifyLink ERROR %s", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(final @NotNull Call<HashMap<String, String>> call, final @NotNull Throwable t) {
                Timber.tag("VerificationActivity").e("verifyLink FAIL");
                t.printStackTrace();
            }
        });
    }

    void isVerifying(boolean status) {
        if (status) {
            TransitionManager.beginDelayedTransition(binding.refreshParent);
            binding.loadingProgress.setVisibility(View.VISIBLE);
            binding.refreshBtn.setVisibility(View.GONE);
            binding.loadingText.setVisibility(View.GONE);
        } else {
            TransitionManager.beginDelayedTransition(binding.refreshParent);
            binding.loadingProgress.setVisibility(View.GONE);
            binding.refreshBtn.setVisibility(View.VISIBLE);
            binding.loadingText.setVisibility(View.VISIBLE);
        }
    }
}