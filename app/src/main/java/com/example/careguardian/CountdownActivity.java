package com.example.careguardian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CountdownActivity extends AppCompatActivity {

    private boolean countDownRanOut = false;
    private int countDownSeconds = 10;
    private SMSService smsService;
    private boolean isSMSServiceBound = false;
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

    @Override
    protected void onStart() {
        super.onStart();
        Intent bindSMSServiceIntent = new Intent(this, SMSService.class);
        bindService(bindSMSServiceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        isSMSServiceBound = false;
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

            Intent caller = getIntent();
            SMSService smsService = new SMSService();
            String[] contactsSplitByComma = caller.getStringExtra("contacts").split(",");
            for (int i = 0; i < contactsSplitByComma.length; i++) {
                String actualNumber = contactsSplitByComma[i].trim().replace(" ", "").replace("\n", ""); // remove spaces and line breaks
                smsService.sendSMS(actualNumber, caller.getStringExtra("name").trim() + " seems to have fallen down! Please check up on them asap!\nCurrent Location:", this);
            }
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

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SMSService.SMSBinder binder = (SMSService.SMSBinder)  iBinder;
            smsService = binder.getService();
            isSMSServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isSMSServiceBound = false;
        }
    };
}