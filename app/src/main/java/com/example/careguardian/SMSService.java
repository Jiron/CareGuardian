package com.example.careguardian;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.SmsManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class SMSService extends Service {

    private final IBinder binder = new SMSBinder();

    public class SMSBinder extends Binder {
        public SMSService getService() {
            return SMSService.this;
        }
    }

    public SMSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public interface SmsSendCallback {
        void onSendSuccess();
        void onSendFailure();
    }

    public void sendSMS(String[] phoneNumbers, String message, Activity activity, SmsSendCallback callback) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        if (
                ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
        ) {
            if (callback != null) {
                callback.onSendFailure();
            }
            return;
        }
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener(location -> {
                try {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        SmsManager smsManager = SmsManager.getDefault();

                        boolean failedAtLeastOnce = false;

                        String fullMessage = message + "\n\n" + "https://maps.google.com/?q=" + latitude + "," + longitude;
                        for (String phoneNumber : phoneNumbers) {
                            try {
                                smsManager.sendTextMessage(phoneNumber, null, fullMessage, null, null);
                            } catch (Exception e) {
                                failedAtLeastOnce = true;
                            }
                        }

                        if (callback != null) {
                            if (failedAtLeastOnce) {
                                callback.onSendFailure();
                            } else {
                                callback.onSendSuccess();
                            }
                        }
                    } else {
                        if (callback != null) {
                            callback.onSendFailure();
                        }
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onSendFailure();
                    }
                }
            });
    }
}
