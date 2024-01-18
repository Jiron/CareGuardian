package com.example.careguardian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class CountdownActivity extends AppCompatActivity {

    private boolean countDownRanOut = false;
    private int countDownSeconds = 10;
    private TextView countDownText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        countDownText = findViewById(R.id.countDownText);

        refreshCountdown();
    }

    private void refreshCountdown() {
        if(countDownSeconds >= 0) {
            new Handler().postDelayed(() -> {
                countDownText.setText(countDownSeconds + " Seconds left");
                countDownSeconds--;
                refreshCountdown();
            }, 1000);
        } else {
            countDownRanOut = true;
            countDownText.setText("Help is on the way!");
        }
    }
}