package com.examplesonly.android;

import android.app.Application;
import androidx.lifecycle.LifecycleObserver;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import timber.log.Timber;
import timber.log.Timber.DebugTree;
public class ExamplesOnly extends Application implements LifecycleObserver {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(getBaseContext());

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        } else {
//            Timber.plant(new CrashReportingTree());
        }
    }
}
