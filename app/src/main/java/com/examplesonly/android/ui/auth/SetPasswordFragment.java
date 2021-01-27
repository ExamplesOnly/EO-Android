package com.examplesonly.android.ui.auth;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.examplesonly.android.R;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.databinding.FragmentAuthBinding;
import com.examplesonly.android.databinding.FragmentSetPasswordBinding;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.auth.AuthInterface;
import com.examplesonly.android.ui.fragment.ChooseInterestFragment;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SetPasswordFragment extends Fragment {

    FragmentSetPasswordBinding binding;
    AuthInterface authInterface;
    UserDataProvider userDataProvider;

    public SetPasswordFragment() {
        // Required empty public constructor
    }

    public static SetPasswordFragment newInstance(String param1, String param2) {
        SetPasswordFragment fragment = new SetPasswordFragment();
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
        binding = FragmentSetPasswordBinding.inflate(getLayoutInflater());
        authInterface = new Api(requireContext()).getClient().create(AuthInterface.class);
        userDataProvider = UserDataProvider.getInstance(requireContext());

        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.WHITE);

        binding.next.setEnabled(false);
        binding.next.setOnClickListener(v -> updatePassword());
        binding.passTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String str = s.toString();
                boolean isEightChars = s.toString().length() >= 8;
                boolean numberFlag = false;
                boolean capitalFlag = false;
                boolean lowerCaseFlag = false;


                for (int i = 0; i < str.length(); i++) {
                    char ch = str.charAt(i);
                    if (Character.isDigit(ch)) {
                        numberFlag = true;
                        Timber.e("isDigit");
                    } else if (Character.isUpperCase(ch)) {
                        capitalFlag = true;
                        Timber.e("isUpperCase");
                    } else if (Character.isLowerCase(ch)) {
                        lowerCaseFlag = true;
                        Timber.e("isLowerCase");
                    }
                }

                isEightChars(isEightChars);
                isOneUpperCaseChar(capitalFlag);
                isOneLowerCaseChar(lowerCaseFlag);
                isOneNumberChar(numberFlag);

                binding.next.setEnabled(isEightChars && capitalFlag && lowerCaseFlag && numberFlag);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return binding.getRoot();
    }

    private void isEightChars(boolean match) {
        int color = match ? ResourcesCompat.getColor(getResources(), R.color.md_green_500, requireActivity().getTheme()) :
                ResourcesCompat.getColor(getResources(), R.color.md_grey_500, requireActivity().getTheme());
        binding.eightCharText.setTextColor(color);
        binding.eightCharTick.setColorFilter(color);
    }

    private void isOneUpperCaseChar(boolean match) {
        int color = match ? ResourcesCompat.getColor(getResources(), R.color.md_green_500, requireActivity().getTheme()) :
                ResourcesCompat.getColor(getResources(), R.color.md_grey_500, requireActivity().getTheme());
        binding.oneCapText.setTextColor(color);
        binding.oneCapTick.setColorFilter(color);
    }

    private void isOneLowerCaseChar(boolean match) {
        int color = match ? ResourcesCompat.getColor(getResources(), R.color.md_green_500, requireActivity().getTheme()) :
                ResourcesCompat.getColor(getResources(), R.color.md_grey_500, requireActivity().getTheme());
        binding.oneSmallText.setTextColor(color);
        binding.oneSmallTick.setColorFilter(color);
    }

    private void isOneNumberChar(boolean match) {
        int color = match ? ResourcesCompat.getColor(getResources(), R.color.md_green_500, requireActivity().getTheme()) :
                ResourcesCompat.getColor(getResources(), R.color.md_grey_500, requireActivity().getTheme());
        binding.oneNumberText.setTextColor(color);
        binding.oneNumberTick.setColorFilter(color);
    }

    private void updatePassword() {
        binding.next.setEnabled(false);
        binding.next.setText(getResources().getString(R.string.updating));
        authInterface.setPassword(binding.passTxt.getText().toString(), userDataProvider.getAccessToken())
                .enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                        if (response.isSuccessful()) {
                            launchChooseInterest();
                        } else {
                            Toast.makeText(requireContext(),
                                    getResources().getString(R.string.something_went_wrong),
                                    Toast.LENGTH_LONG).show();
                            binding.next.setEnabled(true);
                            binding.next.setText(getResources().getString(R.string.next));
                        }
                    }

                    @Override
                    public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                        Toast.makeText(requireContext(),
                                getResources().getString(R.string.something_went_wrong),
                                Toast.LENGTH_LONG).show();
                        binding.next.setEnabled(true);
                        binding.next.setText(getResources().getString(R.string.next));
                    }
                });
    }

    private void launchChooseInterest() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.login_root, new ChooseInterestFragment()).commit();
    }
}