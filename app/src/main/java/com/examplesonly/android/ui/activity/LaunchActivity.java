package com.examplesonly.android.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.examplesonly.android.R;
import com.examplesonly.android.account.UserDataProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseApp;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import timber.log.Timber;

public class LaunchActivity extends AppCompatActivity {

    // Remote Config keys
    private static final String LATEST_VERSION_KEY = "app_latest_version_code";
    private static final String LATEST_UPDATE_URL = "app_update_url";
    private static final String SHOW_UPDATE_DIALOG_KEY = "show_update_dialog";
    private static final String FORCE_UPDATE_KEY = "force_update";
    private static final String FORCE_UPDATE_MIN_VERSION_KEY = "force_min_version_code";

    private final String TAG = LaunchActivity.class.getCanonicalName();
    private UserDataProvider mUserDataProvider;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private Intent login, verify, main;
    private double latestVersion, forceUpdateVersion;
    private boolean forceUpdate, showUpdateDialog;
    private String updateUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        mUserDataProvider = UserDataProvider.getInstance(this);

        login = new Intent(this, LoginActivity.class);
        verify = new Intent(this, VerificationActivity.class);
        main = new Intent(this, MainActivity.class);

        Timber.e(mUserDataProvider.toString());
        launch();
    }

    public void launch() {
        if (isLoggedIn()) {
            if (!isVerified()) {
                startActivity(verify);
            } else {
                startActivity(main);
            }
        } else {
            startActivity(login);
        }
        finish();
    }

    public boolean isLoggedIn() {
        return mUserDataProvider.isAuthorized();
    }

    public boolean isVerified() {
        return mUserDataProvider.isVerified();
    }

    private void fetchConfig() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
    }
}