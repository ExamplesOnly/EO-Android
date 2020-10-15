package com.examplesonly.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.examplesonly.android.R;

public class LaunchActivity extends AppCompatActivity {

    boolean isLoggedIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Intent login = new Intent(this, LoginActivity.class);
        Intent main = new Intent(this, MainActivity.class);

        if (isLoggedIn) {
            startActivity(main);
            finish();
        } else {
            startActivity(login);
            finish();
        }


    }
}