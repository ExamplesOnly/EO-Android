package com.examplesonly.android;

import android.app.Application;
import androidx.lifecycle.LifecycleObserver;
import com.google.firebase.FirebaseApp;
public class ExamplesOnly extends Application implements LifecycleObserver {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(getBaseContext());
    }
}
