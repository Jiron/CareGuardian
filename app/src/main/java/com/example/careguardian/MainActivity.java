package com.example.careguardian;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private EditText name;
    private EditText contacts;
    private TextView inputHint;
    private TextView status;
    private Button alarmToggler;

    private String nameValue;
    private String contactsValue;
    private boolean isAlarmActivated = false;
    private boolean isCountdownActive = false;
    private boolean alreadyRequestedLocationPerms = false;
    private boolean alreadyRequestedSMSPerms = false;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    // Adjustable sensitivity for shake detection
    private static final float SHAKE_THRESHOLD = 25.0f;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int SMS_PERMISSION_REQUEST_CODE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.inputName);
        contacts = findViewById(R.id.inputContacts);
        alarmToggler = findViewById(R.id.alarmToggler);
        status = findViewById(R.id.status);
        inputHint = findViewById(R.id.inputHint);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        nameValue = prefs.getString("nameTextView", "");
        contactsValue = prefs.getString("contactsTextView", "");
        alreadyRequestedLocationPerms = prefs.getBoolean("alreadyRequestedLocationPerms", false);
        alreadyRequestedSMSPerms = prefs.getBoolean("alreadyRequestedSMSPerms", false);
        updateAll(false);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                String updatedText = editable.toString();
                if (!updatedText.isEmpty()) {
                    nameValue = updatedText;
                } else {
                    nameValue = "";
                }
                updateName(true);
            }
        });
        contacts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                String updatedText = editable.toString();
                if (!updatedText.isEmpty()) {
                    contactsValue = updatedText;
                } else {
                    contactsValue = "";
                }
                updateContacts(true);
            }
        });
        alarmToggler.setOnClickListener(view -> toggleAlarm());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                // Handle the case where the accelerometer is not available on the device
                Toast.makeText(this, "Accelerometer not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void toggleAlarm() {
        if(!isAlarmActivated && !nameValue.equals("") && !contactsValue.equals("")) {
            boolean permLocation = checkAndRequestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_PERMISSION_REQUEST_CODE
            );
            if(!permLocation) {
                return;
            }

            boolean permSMS = checkAndRequestPermission(
                    Manifest.permission.SEND_SMS,
                    SMS_PERMISSION_REQUEST_CODE
            );
            if(!permSMS) {
                return;
            }

            alarmToggler.setText("Deactivate");
            status.setText("Activated");
            status.setTextColor(Color.parseColor("#00FF00"));
            name.setEnabled(false);
            contacts.setEnabled(false);
            isAlarmActivated = true;
        } else {
            alarmToggler.setText("Activate");
            status.setText("Deactivated");
            name.setEnabled(true);
            contacts.setEnabled(true);
            status.setTextColor(Color.parseColor("#FF0000"));
            isAlarmActivated = false;
        }
        validateInputs();
    }

    public void updateAll(boolean ignoreSetText) {
        updateName(ignoreSetText);
        updateContacts(ignoreSetText);
    }

    private void updateName(boolean ignoreSetText) {
        if(!ignoreSetText) {
            name.setText(nameValue);
        }

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nameTextView", nameValue);
        editor.apply();
        validateInputs();
    }

    private void updateContacts(boolean ignoreSetText) {
        if(!ignoreSetText) {
            contacts.setText(contactsValue);
        }

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("contactsTextView", contactsValue);
        editor.apply();
        validateInputs();
    }

    private void validateInputs() {
        if(!nameValue.equals("") && !contactsValue.equals("")) {
            if (isAlarmActivated) {
                inputHint.setText("(Please deactivate to change input data)");
            } else {
                inputHint.setText("");
            }
            inputHint.setTextColor(Color.parseColor("#000000"));
            alarmToggler.setEnabled(true);
        } else {
            inputHint.setText("(All fields must be filled out before activating)");
            inputHint.setTextColor(Color.parseColor("#FF0000"));
            alarmToggler.setEnabled(false);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && isAlarmActivated && !isCountdownActive) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float acceleration = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

            if (acceleration > SHAKE_THRESHOLD) {
                isCountdownActive = true;
                Intent intent = new Intent(this, CountdownActivity.class);
                intent.putExtra("name", nameValue);
                intent.putExtra("contacts", contactsValue);
                startCountdownActivityForResult.launch(intent);
            }
        }
    }

    private final ActivityResultLauncher<Intent> startCountdownActivityForResult =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // if (result.getResultCode() == RESULT_OK) {
                //     // toggleAlarm();
                // } else if (result.getResultCode() == RESULT_CANCELED) {
                //     // toggleAlarm();
                // }
                toggleAlarm();
                isCountdownActive = false;
            });


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private boolean checkAndRequestPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            boolean isSms = permission ==Manifest.permission.SEND_SMS;
            boolean isLocation = permission == Manifest.permission.ACCESS_FINE_LOCATION;
            if((isSms && alreadyRequestedSMSPerms) || (isLocation && alreadyRequestedLocationPerms)) {
                showToast("Please enable location and/or SMS perms in settings.");
            } else {
                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                if(isLocation) {
                    alreadyRequestedLocationPerms = true;
                    editor.putBoolean("alreadyRequestedLocationPerms", true);

                } else if (isSms) {
                    alreadyRequestedSMSPerms = true;
                    editor.putBoolean("alreadyRequestedSMSPerms", true);

                }
                editor.apply();
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}