package com.solvevolve.examplesonly.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.solvevolve.examplesonly.R;
import com.solvevolve.examplesonly.account.UserDataProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseApp;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

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

        fetchConfig();
        latestVersion = mFirebaseRemoteConfig.getDouble(LATEST_VERSION_KEY);
        forceUpdateVersion = mFirebaseRemoteConfig.getDouble(FORCE_UPDATE_MIN_VERSION_KEY);
        forceUpdate = mFirebaseRemoteConfig.getBoolean(FORCE_UPDATE_KEY);
        showUpdateDialog = mFirebaseRemoteConfig.getBoolean(SHOW_UPDATE_DIALOG_KEY);
        updateUrl = mFirebaseRemoteConfig.getString(LATEST_UPDATE_URL);

        FirebaseApp.initializeApp(getApplication());
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();
                        verify.putExtra(VerificationActivity.VERIFICATION_LINK, deepLink.getPath());
                        startActivity(verify);
                        finish();
                    } else {
                        launchProcedure();
                    }
                })
                .addOnFailureListener(this, e -> {
                    launchProcedure();
                });
    }

    public void launchProcedure() {

        PackageInfo pInfo = null;
        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        if (pInfo == null) {
            launch();
        }

        int versionCode = pInfo.versionCode;

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (forceUpdate && versionCode < (int) forceUpdateVersion) {
                        new MaterialAlertDialogBuilder(LaunchActivity.this).setTitle("Update required")
                                .setMessage("There is a new version of ExamplesOnly available for download.")
                                .setPositiveButton(
                                        "Update", (dialogInterface, i) -> {
                                            Uri uri = Uri.parse(updateUrl);
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(Intent.createChooser(intent, "Open with"));
                                        })
                                .setNegativeButton("Quit", (dialogInterface, i) -> finish())
                                .setCancelable(false).show();
                    } else if (showUpdateDialog && versionCode < (int) latestVersion) {
                        new MaterialAlertDialogBuilder(LaunchActivity.this).setTitle("Update required")
                                .setMessage("There is a new makeup available. ")
                                .setPositiveButton(
                                        "Update", (dialogInterface, i) -> {
                                            Uri uri = Uri.parse(updateUrl);
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(Intent.createChooser(intent, "Open with"));
                                        })
                                .setNegativeButton("Cancel", (dialogInterface, i) -> launch())
                                .setCancelable(false).show();
                    } else {
                        launch();
                    }
                });
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
        String token = mUserDataProvider.getToken();
        return (token != null);
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