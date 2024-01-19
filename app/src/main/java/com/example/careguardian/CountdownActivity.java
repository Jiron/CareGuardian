package com.example.careguardian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CountdownActivity extends AppCompatActivity {

    private boolean countDownRanOut = false;
    private int countDownSeconds = 10;
    private TextView countDownText;
    private TextView countDownTextDetail;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        countDownText = findViewById(R.id.countDownText);
        countDownTextDetail = findViewById(R.id.countDownTextDetail);
        backButton = findViewById(R.id.backButton);

        refreshCountdown();

        backButton.setOnClickListener(view -> finish());
    }

    private void refreshCountdown() {
        if(countDownSeconds >= 0) {
            new Handler().postDelayed(() -> {
                countDownText.setText(countDownSeconds + " Seconds");
                countDownSeconds--;
                refreshCountdown();

                vibrate(new long[] { 100, 100, 100, 200 }, new int[] { 100, 150, 0, 255 }, -1); // transition up, short pause, strong vibration, 500ms total
            }, 1000);
        } else {
            countDownRanOut = true;
            countDownText.setText("Emergency contacts\nhave been notified");
            countDownTextDetail.setText("");
            backButton.setText("Back to Home");
        }
    }

    private void vibrate(long[] timings, int[] amplitudes, int repeatIndex) {
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex));
        } else {
            v.vibrate(500);
        }
    }
}