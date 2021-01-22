package com.examplesonly.android.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.examplesonly.android.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import timber.log.Timber;

public class DynamicLinkActivity extends AppCompatActivity {


    public static String VERIFICATION_TOKEN = "verification_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_link);

        FirebaseApp.initializeApp(getApplication());
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null && pendingDynamicLinkData.getLink() != null) {
                        deepLink = pendingDynamicLinkData.getLink();
                        String path = deepLink.getPath();
                        Intent main;
                        if (path.contains("verify")) {
                            main = new Intent(this, VerificationActivity.class);
                        } else {
                            main = new Intent(this, MainActivity.class);
                        }

                        main.putExtra(VERIFICATION_TOKEN, deepLink.toString());
                        startActivity(main);
                        finish();

                    }
                })
                .addOnFailureListener(this, e -> {
                    Intent main = new Intent(this, LaunchActivity.class);
                    startActivity(main);
                    finish();
                });
    }
}