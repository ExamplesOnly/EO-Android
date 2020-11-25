package com.examplesonly.android.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.examplesonly.android.R;
import com.examplesonly.android.account.UserDataProvider;
import com.google.firebase.FirebaseApp;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class LaunchActivity extends AppCompatActivity {

    private final String TAG = LaunchActivity.class.getCanonicalName();
    private UserDataProvider mUserDataProvider;
    private Intent login, verify, main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        mUserDataProvider = UserDataProvider.getInstance(this);
        Log.e("LAUNCH USER", mUserDataProvider.toString());

        login = new Intent(this, LoginActivity.class);
        verify = new Intent(this, VerificationActivity.class);
        main = new Intent(this, MainActivity.class);

        FirebaseApp.initializeApp(getApplication());
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Log.e(TAG, "getDynamicLink:addOnSuccessListener");
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();
                        Log.e(TAG, "getDynamicLink:deepLink: " + deepLink.getPath());
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

//    public User getUser() {
//        User currentUser;
//
//
//
//
//        return
//    }
}