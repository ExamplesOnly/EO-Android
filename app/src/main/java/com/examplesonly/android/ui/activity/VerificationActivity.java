package com.examplesonly.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.examplesonly.android.databinding.ActivityVerificationBinding;
import com.examplesonly.android.model.User;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.network.user.UserInterface;
import java.io.IOException;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity {

    public static String VERIFICATION_LINK = "verification_link";
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

        String verificationLink = getIntent().getStringExtra(VERIFICATION_LINK);
        if (verificationLink != null) {
            String token = verificationLink.split("/")[4];
            Log.e("VERIFY LINK", token);
            verifyLink(token);
        } else {
            updateUser();
        }
    }

    void init() {
        mUserInterface = new Api(this).getClient().create(UserInterface.class);
        mUserDataProvider = new UserDataProvider(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUser();

    }

    void updateUser() {
        mUserInterface.me().enqueue(new Callback<User>() {
            @Override
            public void onResponse(final Call<User> call,
                    final Response<User> response) {
                if (response.isSuccessful()) {
                    mUserDataProvider.saveUserData(response.body());
                    if (mUserDataProvider.isVerified()) {
                        Intent main = new Intent(getApplicationContext(), ChooseCategoryActivity.class);
                        startActivity(main);
                        finish();
                    }
                    Log.e(TAG, response.body().toString());
                } else {
                    try {
                        Log.e(TAG, "updateUser NOT SUCCESS" + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("VerificationActivity", "updateUser ERROR");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(final Call<User> call, final Throwable t) {
                Log.e("VerificationActivity", "updateUser FAIL");
                t.printStackTrace();
            }
        });
    }

    void verifyLink(String token) {
        mUserInterface.verifyAccount(token).enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(final Call<HashMap<String, String>> call,
                    final Response<HashMap<String, String>> response) {

                if (response.isSuccessful()) {
//                    String token = response.body().get("token");
//                    mUserDataProvider.saveToken(token);
                    updateUser();
                    Log.e("VerificationActivity", "verifyLink SUCCESS");
                } else {
                    try {
                        Log.e("VerificationActivity", "verifyLink ERROR " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(final Call<HashMap<String, String>> call, final Throwable t) {
                Log.e("VerificationActivity", "verifyLink FAIL");
                t.printStackTrace();
            }
        });
    }
}