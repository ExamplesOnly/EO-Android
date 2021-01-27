package com.examplesonly.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.examplesonly.android.R;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.component.EoLoadingDialog;
import com.examplesonly.android.databinding.ActivityLoginVideoBinding;
import com.examplesonly.android.model.User;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.auth.AuthInterface;
import com.examplesonly.android.network.user.UserInterface;
import com.examplesonly.android.ui.auth.AuthFragment;
import com.examplesonly.android.ui.auth.ForgotPassFragment;
import com.examplesonly.android.ui.auth.SetPasswordFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1011;

    private ActivityLoginVideoBinding binding;
    private Boolean loading = false;
    private GoogleSignInClient mGoogleSignInClient;
    private AuthInterface authInterface;
    private UserInterface userInterface;
    private UserDataProvider userDataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authInterface = new Api(this).getClient().create(AuthInterface.class);
        userInterface = new Api(this).getClient().create(UserInterface.class);
        userDataProvider = UserDataProvider.getInstance(this);

        getSupportFragmentManager().beginTransaction()
                .replace(binding.loginRoot.getId(), new AuthFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.login_root);
        if (fragment instanceof AuthFragment) {
            finish();
        }

        if (!loading) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    public void isLoading(boolean loading) {
        this.loading = loading;
    }


    public void googleSignIn() {
        Intent signInIntent = userDataProvider.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        EoLoadingDialog loadingDialog = new EoLoadingDialog(this).setLoadingText(getString(R.string.logging_in));
        loadingDialog.setCancelable(false);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            authInterface.googleLogin(account.getIdToken(), account.getServerAuthCode()).enqueue(new Callback<HashMap<String, String>>() {
                @Override
                public void onResponse(@NotNull Call<HashMap<String, String>> call, @NotNull Response<HashMap<String, String>> authResponse) {
                    if (authResponse.isSuccessful() && authResponse.body() != null
                            && authResponse.body().containsKey("accessToken")
                            && authResponse.body().containsKey("refreshToken")) {

                        HashMap<String, String> body = authResponse.body();
                        userDataProvider.saveToken(body.get("accessToken"), body.get("refreshToken"));

                        userInterface.me().enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(final @NotNull Call<User> call, final @NotNull Response<User> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    loadingDialog.dismiss();
                                    userDataProvider.saveUserData(response.body());
                                    if (authResponse.body().containsKey("newAccount")) {
                                        launchSetPass();
                                    } else {
                                        Intent main = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(main);
                                        finish();
                                    }
                                } else {
                                    loadingDialog.dismiss();
                                    Snackbar.make(binding.getRoot(), "Something went wrong. Please try again.",
                                            BaseTransientBottomBar.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(final Call<User> call, final Throwable t) {
                                Timber.e("6");
                                loadingDialog.dismiss();
                                Snackbar.make(binding.getRoot(), "Something went wrong. Please try again.",
                                        BaseTransientBottomBar.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        loadingDialog.dismiss();
                        Snackbar.make(binding.getRoot(), "Something went wrong. Please try again.",
                                BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<HashMap<String, String>> call, @NotNull Throwable t) {
                    loadingDialog.dismiss();
                    Snackbar.make(binding.getRoot(), "Something went wrong. Please try again.",
                            BaseTransientBottomBar.LENGTH_LONG).show();
                }
            });
        } catch (ApiException e) {
            loadingDialog.dismiss();
            Toast.makeText(this, "Couldn't log in", Toast.LENGTH_SHORT).show();
        }
    }

    public void launchSetPass() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.login_root, new SetPasswordFragment()).commit();
    }
}