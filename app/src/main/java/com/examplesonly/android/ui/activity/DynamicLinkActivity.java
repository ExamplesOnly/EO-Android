package com.examplesonly.android.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.examplesonly.android.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class DynamicLinkActivity extends AppCompatActivity {

    private final String TAG = DynamicLinkActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_link);

        FirebaseApp.initializeApp(getApplication());
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Log.e(TAG, "getDynamicLink:addOnSuccessListener");
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();
                        Log.e(TAG, "getDynamicLink:deepLink: " + deepLink.getPath());
                        Intent main = new Intent(this, VerificationActivity.class);
                        main.putExtra(VerificationActivity.VERIFICATION_LINK, deepLink.getPath());
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