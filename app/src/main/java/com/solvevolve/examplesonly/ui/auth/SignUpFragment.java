package com.solvevolve.examplesonly.ui.auth;

import static com.solvevolve.examplesonly.account.UserDataProvider.TOKEN_KEY;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import com.solvevolve.examplesonly.R;
import com.solvevolve.examplesonly.account.UserDataProvider;
import com.solvevolve.examplesonly.databinding.FragmentSignUpBinding;
import com.solvevolve.examplesonly.model.User;
import com.solvevolve.examplesonly.network.Api;
import com.solvevolve.examplesonly.network.auth.AuthInterface;
import com.solvevolve.examplesonly.network.user.UserInterface;
import com.solvevolve.examplesonly.ui.activity.LoginActivity;
import com.solvevolve.examplesonly.ui.activity.VerificationActivity;
import com.robinhood.ticker.TickerUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;
    private AuthInterface mAuthInterface;
    private UserInterface mUserInterface;
    private UserDataProvider mUserDataProvider;

    private int currentStep = 0;
    private final User userData = new User();
    private boolean setDOb = false;

    public SignUpFragment() {
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
        binding = FragmentSignUpBinding.inflate(getLayoutInflater());

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

    private void init() {

        mUserDataProvider = UserDataProvider.getInstance(getContext());
        mAuthInterface = new Api(getContext()).getClient().create(AuthInterface.class);
        mUserInterface = new Api(getContext()).getClient().create(UserInterface.class);

        binding.closeBtn.setOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());
        binding.backFab.hide();

        binding.descText.setAnimationInterpolator(new FastOutSlowInInterpolator());
        binding.descText.setAnimationDuration(500);
        binding.descText.setCharacterLists(TickerUtils.provideAlphabeticalList());
        binding.descText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_bold));

        binding.greetingText.setAnimationInterpolator(new FastOutSlowInInterpolator());
        binding.greetingText.setAnimationDuration(500);
        binding.greetingText.setCharacterLists(TickerUtils.provideAlphabeticalList());
        binding.greetingText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_bold));

        switchStep(currentStep + 1);
        updateUi();

        binding.nextFab.setOnClickListener(v -> {
            currentStep = currentStep + 1;
            switchStep(currentStep);
            updateUi();
        });

        binding.backFab.setOnClickListener(v -> {
            currentStep = currentStep - 1;
            if (currentStep < 2) {
                binding.backFab.hide();
            }
            getChildFragmentManager().popBackStack();
            updateUi();
        });
    }

    private void switchStep(int step) {
        switch (step) {
            case 1:
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.field_container, SignUpNameFragment.newInstance(userData.getFirstName()))
                        .addToBackStack(null).commit();
                currentStep = step;
                break;
            case 2:
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.field_container, SignUpEmailFragment.newInstance(userData.getEmail()))
                        .addToBackStack(null).commit();
                binding.backFab.show();
                currentStep = step;
                break;
            case 3:
                getChildFragmentManager().beginTransaction().replace(R.id.field_container, new SignUpGenderFragment())
                        .addToBackStack(null).commit();
                binding.backFab.show();
                currentStep = step;
                break;
            case 4:
                getChildFragmentManager().beginTransaction().replace(R.id.field_container, new SignUpDobFragment())
                        .addToBackStack(null).commit();
                binding.backFab.show();
                currentStep = step;
                break;
            case 5:
                getChildFragmentManager().beginTransaction().replace(R.id.field_container, new SignUpPassFragment())
                        .addToBackStack(null).commit();
                binding.backFab.show();
                currentStep = step;
                break;
            case 6:
                createAccount(userData);
                break;
        }
    }

    private void updateUi() {
        switch (currentStep) {
            case 1:
                binding.greetingText.setText("Welcome,", true);
                binding.descText.setText("What can we call you?", true);
                if (TextUtils.isEmpty(userData.getFirstName()) || userData.getFirstName().length() < 3) {
                    binding.nextFab.setEnabled(false);
                } else {
                    binding.nextFab.setEnabled(true);
                }
                break;
            case 2:
                binding.greetingText.setText("Great, " + userData.getFirstName().split(" ")[0], true);
                binding.descText.setText("What is your email id?", true);
                String email = userData.getEmail();
                if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.nextFab.setEnabled(false);
                } else {
                    binding.nextFab.setEnabled(true);
                }
                break;
            case 3:
                binding.descText.setText("What is your gender identity?", true);
                String gender = userData.getGender();
                if (TextUtils.isEmpty(gender)) {
                    binding.nextFab.setEnabled(false);
                } else {
                    binding.nextFab.setEnabled(true);
                }
                break;
            case 4:
                binding.descText.setText("When were you born?", true);
                if (!setDOb) {
                    binding.nextFab.setEnabled(false);
                } else {
                    binding.nextFab.setEnabled(true);
                }
                break;
            case 5:
                binding.descText.setText("Lets make it secure!", true);
                String pass = userData.getPassword();
                if (TextUtils.isEmpty(pass) || !(pass.length() >= 8)) {
                    binding.nextFab.setEnabled(false);
                } else {
                    binding.nextFab.setEnabled(true);
                }
                break;
        }
    }

    public void setName(String name) {
        userData.setFirstName(name);
        updateUi();
    }

    public void setEmail(String email) {
        userData.setEmail(email);
        updateUi();
    }

    public void setGender(String gender) {
        userData.setGender(gender);
        updateUi();
    }

    public void setDob(boolean dob) {
        setDOb = dob;
        updateUi();
    }

    public void setPassword(String pass) {
        userData.setPassword(pass);
        updateUi();
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
                                Timber.e(response.body().toString());

                            } else {
                                try {
                                    Timber.e(response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            Intent main = new Intent(getContext(), VerificationActivity.class);
                            startActivity(main);
                            getActivity().finish();
                        }

                        @Override
                        public void onFailure(final Call<User> call, final Throwable t) {
                            t.getStackTrace();
                        }
                    });
                } else {
                    try {
                        Timber.e(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isLoading(false);
                    Toast.makeText(getContext(), "Could not create account.", Toast.LENGTH_LONG).show();
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
        LoginActivity loginActivity = (LoginActivity) getActivity();
        loginActivity.isLoading(loading);

        binding.nextFab.setEnabled(!loading);
        binding.nextFab.setEnabled(!loading);

    }
}