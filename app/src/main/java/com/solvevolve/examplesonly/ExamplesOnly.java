package com.solvevolve.examplesonly;

import android.app.Application;
import androidx.lifecycle.LifecycleObserver;
import com.google.firebase.FirebaseApp;

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
