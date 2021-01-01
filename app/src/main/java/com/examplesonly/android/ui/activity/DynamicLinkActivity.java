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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_link);

        FirebaseApp.initializeApp(getApplication());
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Timber.e("getDynamicLink:addOnSuccessListener");
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();
                        Timber.e("getDynamicLink:deepLink: %s %s", deepLink, deepLink.getQueryParameter("token"));
                        Intent main = new Intent(this, VerificationActivity.class);
                        main.putExtra(VerificationActivity.VERIFICATION_LINK, deepLink.toString());
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