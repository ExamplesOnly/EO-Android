package com.examplesonly.android.ui.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import com.examplesonly.android.R;
import com.examplesonly.android.databinding.FragmentAuthBinding;
import com.examplesonly.android.ui.activity.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class AuthFragment extends Fragment {

    private FragmentAuthBinding binding;
    private LoginActivity parentActivity;

    public AuthFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Transition transition = TransitionInflater.from(getContext()).
                inflateTransition(R.transition.auth_card_collapse_transition);
        transition.setInterpolator(AnimationUtils.loadInterpolator(getContext(), R.anim.ease_interpolator));
        setExitTransition(transition);

        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);

        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(final List<String> names, final Map<String, View> sharedElements) {
                sharedElements.put(names.get(0), binding.loginBtn);
            }
        });

        parentActivity = ((LoginActivity) requireActivity());

        binding = FragmentAuthBinding.inflate(getLayoutInflater());
        binding.loginBtn.setOnClickListener(v -> launchLogin());
        binding.googleLoginBtn.setOnClickListener(v -> parentActivity.googleSignIn());

        binding.policyText.setMovementMethod(LinkMovementMethod.getInstance());

        return binding.getRoot();
    }


    public void launchLogin() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.setReorderingAllowed(true)
                .addSharedElement(binding.loginBtn,
                        binding.loginBtn.getTransitionName())
                .replace(R.id.login_root, new LoginFragment())
                .addToBackStack(null).commit();
    }

//    public void launchSignUp() {
//        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
//        ft.setReorderingAllowed(true)
//                .addSharedElement(binding.signUpBtn,
//                        binding.signUpBtn.getTransitionName())
//                .replace(R.id.login_root, new SignUpFragment())
//                .addToBackStack(null).commit();
//    }
}