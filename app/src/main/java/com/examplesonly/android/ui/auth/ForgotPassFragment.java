package com.examplesonly.android.ui.auth;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialog;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.examplesonly.android.R;
import com.examplesonly.android.component.EoAlertDialog.ClickListener;
import com.examplesonly.android.component.EoAlertDialog.EoAlertDialog;
import com.examplesonly.android.databinding.FragmentForgotPassBinding;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.auth.AuthInterface;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassFragment extends Fragment {

    private FragmentForgotPassBinding binding;
    private AuthInterface authInterface;

    public ForgotPassFragment() {
        // Required empty public constructor
    }

    public static ForgotPassFragment newInstance(String param1, String param2) {
        ForgotPassFragment fragment = new ForgotPassFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentForgotPassBinding.inflate(inflater);
        authInterface = new Api(requireContext()).getClient().create(AuthInterface.class);
        binding.nextBtn.setEnabled(false);

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


        binding.closeBtn.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.nextBtn.setOnClickListener(v -> {
            if (binding.emailTxt.getText() != null && binding.emailTxt.getText().toString().length() < 1) {
                return;
            }

            binding.nextBtn.setEnabled(false);
            authInterface.forgotPassword(binding.emailTxt.getText().toString()).enqueue(new Callback<HashMap<String, String>>() {
                @Override
                public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                    EoAlertDialog dialog = new EoAlertDialog.Builder(requireContext())
                            .setTitle("Reset Password")
                            .setDescription("If an account was found on our system linked with " + binding.emailTxt.getText().toString()
                                    + ", we have sent an email with a link to reset your password.")
                            .setDialogIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_envelope_upload, requireActivity().getTheme()))
                            .setIconTint(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, requireActivity().getTheme()))
                            .setCenterText(false)
                            .setCancelable(true)
                            .setPositiveText("Got it!")
                            .setPositiveClickListener(d -> {
                                d.dismiss();
                                binding.nextBtn.setEnabled(true);
                                requireActivity().onBackPressed();
                            })
                            .create();
                    dialog.show();
                }

                @Override
                public void onFailure(Call<HashMap<String, String>> call, Throwable t) {

                }
            });
        });

        binding.emailTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.nextBtn.setEnabled(count > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return binding.getRoot();
    }
}