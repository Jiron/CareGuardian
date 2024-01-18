package com.example.careguardian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText name;
    private EditText contacts;
    private TextView status;
    private Button alarmToggler;

    private String nameValue;
    private String contactsValue;
    private boolean isAlarmActivated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.inputName);
        contacts = findViewById(R.id.inputContacts);
        alarmToggler = findViewById(R.id.alarmToggler);
        status = findViewById(R.id.status);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        nameValue = prefs.getString("nameTextView", "");
        contactsValue = prefs.getString("contactsTextView", "");
        updateAll(false);
        toggleAlarm();

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
        alarmToggler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleAlarm();
            }
        });
    }

    public void toggleAlarm() {
        if(isAlarmActivated) {
            alarmToggler.setText("Deactivate");
            status.setText("Activated");
            status.setTextColor(Color.parseColor("#00FF00"));
            name.setEnabled(false);
            contacts.setEnabled(false);
            isAlarmActivated = false;
        } else {
            alarmToggler.setText("Activate");
            status.setText("Deactivated");
            name.setEnabled(true);
            contacts.setEnabled(true);
            status.setTextColor(Color.parseColor("#FF0000"));
            isAlarmActivated = true;
        }
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
    }

    private void updateContacts(boolean ignoreSetText) {
        if(!ignoreSetText) {
            contacts.setText(contactsValue);
        }

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("contactsTextView", contactsValue);
        editor.apply();
    }
}