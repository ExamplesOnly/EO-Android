package com.examplesonly.android.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.examplesonly.android.BuildConfig;
import com.examplesonly.android.R;
import com.examplesonly.android.account.UserDataProvider;
import com.examplesonly.android.component.EoLoadingDialog;
import com.examplesonly.android.handler.UserAccountHandler;
import com.examplesonly.android.network.Api;
import com.examplesonly.android.network.auth.AuthInterface;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

public class MainSettings extends PreferenceFragmentCompat {


    EoLoadingDialog loadingDialog;
    AuthInterface authInterface;
    UserDataProvider userDataProvider;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_main, rootKey);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        authInterface = new Api(requireActivity()).getClient().create(AuthInterface.class);
        userDataProvider = UserDataProvider.getInstance(requireActivity());

        getPreferenceManager().findPreference("version").setSummary(BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
        getPreferenceManager().findPreference("feedback").setOnPreferenceClickListener(preference -> {
            sendFeedback();
            return false;
        });

        getPreferenceManager().findPreference("logout").setOnPreferenceClickListener(preference -> {
            logout();
            return false;
        });

        getPreferenceManager().findPreference("oss_licenses").setOnPreferenceClickListener(preference -> {
            openSourceLicences();
            return false;
        });

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    void sendFeedback() {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"dev@examplesonly.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for ExamplesOnly");
//            intent.putExtra(Intent.EXTRA_TEXT, "Feedback for ExamplesOnly");
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(),
                    "No email client found on your device. Kindly email us at dev@examplesonly.com",
                    Toast.LENGTH_LONG).show();
        }
    }

    void logout() {
        loadingDialog = new EoLoadingDialog(requireActivity()).setLoadingText(getString(R.string.logging_out));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        UserDataProvider.getInstance(getContext()).logout(authInterface, (isSuccess) -> {
            userDataProvider.getGoogleSignInClient().signOut();
            loadingDialog.dismiss();
            requireActivity().finish();

            if (isSuccess)
                requireActivity().finish();
            else
                Toast.makeText(requireActivity(), "Could not logout. Are you connected to internet?", Toast.LENGTH_LONG).show();
        });
    }

    void openSourceLicences() {
        startActivity(new Intent(requireActivity(), OssLicensesMenuActivity.class));
    }
}
