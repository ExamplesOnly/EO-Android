package com.solvevolve.examplesonly.ui.auth;

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
import com.solvevolve.examplesonly.R;
import com.solvevolve.examplesonly.databinding.FragmentAuthBinding;
import java.util.List;
import java.util.Map;

public class AuthFragment extends Fragment {

    private FragmentAuthBinding binding;

    public AuthFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Transition transition = TransitionInflater.from(getContext()).
                inflateTransition(R.transition.auth_card_collapse_transition);
        transition.setInterpolator(AnimationUtils.loadInterpolator(getContext(), R.anim.ease_interpolator));
        setExitTransition(transition);

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);

        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(final List<String> names, final Map<String, View> sharedElements) {
                if (names.get(0).equals("signup_transition")) {
                    sharedElements.put(names.get(0), binding.signUpBtn);
                } else {
                    sharedElements.put(names.get(0), binding.loginBtn);
                }
            }
        });

        binding = FragmentAuthBinding.inflate(getLayoutInflater());
        binding.loginBtn.setOnClickListener(v -> launchLogin());
        binding.signUpBtn.setOnClickListener(v -> launchSignUp());

        binding.policyText.setMovementMethod(LinkMovementMethod.getInstance());

        return binding.getRoot();
    }

    public void launchLogin() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setReorderingAllowed(true)
                .addSharedElement(binding.loginBtn,
                        binding.loginBtn.getTransitionName())
                .replace(R.id.login_root, new LoginFragment())
                .addToBackStack(null).commit();
    }

    public void launchSignUp() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setReorderingAllowed(true)
                .addSharedElement(binding.signUpBtn,
                        binding.signUpBtn.getTransitionName())
                .replace(R.id.login_root, new SignUpFragment())
                .addToBackStack(null).commit();
    }
}