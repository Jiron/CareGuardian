package com.example.careguardian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.os.Handler;

public class CountdownActivity extends AppCompatActivity {

    private boolean countDownRanOut = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        new Handler().postDelayed(() -> {
            setResult(RESULT_OK);
            finish();
        }, 10000);
    }
}