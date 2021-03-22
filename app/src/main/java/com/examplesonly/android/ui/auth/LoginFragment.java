package com.examplesonly.android.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import com.examplesonly.android.R;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.databinding.FragmentLoginBinding;
import com.examplesonly.android.model.User;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.auth.AuthInterface;
import com.examplesonly.android.network.user.UserInterface;
import com.examplesonly.android.ui.activity.LoginActivity;
import com.examplesonly.android.ui.activity.MainActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LoginFragment extends Fragment {

    FragmentLoginBinding binding;
    private AuthInterface mAuthInterface;
    private UserInterface mUserInterface;
    private UserDataProvider mUserDataProvider;

    private Context mContext;

    public LoginFragment() {
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

        binding = FragmentLoginBinding.inflate(getLayoutInflater());

        Transition transition =
                TransitionInflater.from(getContext())
                        .inflateTransition(R.transition.auth_card_expand_transition);
        transition.setInterpolator(AnimationUtils.loadInterpolator(getContext(), R.anim.ease_interpolator));
        setSharedElementEnterTransition(transition);

        setEnterSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(
                            List<String> names, Map<String, View> sharedElements) {
                        sharedElements.put(names.get(0), binding.cardView);
                    }
                });

        init();
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void init() {
        mAuthInterface = new Api(mContext).getClient().create(AuthInterface.class);
        mUserInterface = new Api(mContext).getClient().create(UserInterface.class);
        mUserDataProvider = UserDataProvider.getInstance(getContext());

        binding.loginBtn.setOnClickListener(v -> signIn());
        binding.closeBtn.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.signUpBtn.setOnClickListener(v -> launchSignUp());
        binding.forgotPassword.setOnClickListener(v -> launchForgotPass());
    }

    private void signIn() {
        User user = new User();
        user.setEmail(binding.emailTxt.getText().toString());
        user.setPassword(binding.passwordTxt.getText().toString());

        isLoading(true);
        mAuthInterface.login(user).enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(@NotNull final Call<HashMap<String, String>> call,
                                   @NotNull final Response<HashMap<String, String>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().containsKey("accessToken")
                        && response.body().containsKey("refreshToken")) {

                    HashMap<String, String> body = response.body();
                    mUserDataProvider.saveToken(body.get("accessToken"), body.get("refreshToken"));

                    mUserInterface = new Api(getContext()).getClient().create(UserInterface.class);
                    mUserInterface.me().enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(final @NotNull Call<User> call,
                                               final @NotNull Response<User> response) {
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

                            // Start FCM token publishing process
                            ((LoginActivity) requireActivity()).publishFcmToken();
                            Intent main = new Intent(getContext(), MainActivity.class);
                            startActivity(main);
                            getActivity().finish();
                        }

                        @Override
                        public void onFailure(final Call<User> call, final Throwable t) {
                            Toast.makeText(getContext(), "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    isLoading(false);
                    Toast.makeText(mContext, "Email or password incorrect", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(final Call<HashMap<String, String>> call, final Throwable t) {
                isLoading(false);
                Toast.makeText(getContext(), "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    void isLoading(Boolean loading) {
        binding.emailTextField.setEnabled(!loading);
        binding.passwordTextField.setEnabled(!loading);
        binding.loginBtn.setEnabled(!loading);
    }

    public void launchSignUp() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.setReorderingAllowed(true)
                .addSharedElement(binding.cardView,
                        binding.cardView.getTransitionName())
                .replace(R.id.login_root, new SignUpFragment())
                .addToBackStack(null).commit();
    }

    public void launchForgotPass() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.setReorderingAllowed(true)
                .addSharedElement(binding.cardView,
                        binding.cardView.getTransitionName())
                .replace(R.id.login_root, new ForgotPassFragment())
                .addToBackStack(null).commit();
    }
}