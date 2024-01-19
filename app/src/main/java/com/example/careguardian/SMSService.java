package com.example.careguardian;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
            smsCallbackReturn(false, callback);
            return;
        }
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener(location -> {
                sendSMSWithLocation(location, phoneNumbers, message, callback);
            });
    }

    private void sendSMSWithLocation(Location location, String[] phoneNumbers, String message, SmsSendCallback callback) {
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

                if (failedAtLeastOnce) {
                    smsCallbackReturn(false, callback);
                } else {
                    smsCallbackReturn(true, callback);
                }
            } else {
                smsCallbackReturn(false, callback);
            }
        } catch (Exception e) {
            smsCallbackReturn(false, callback);
        }
    }

    private void smsCallbackReturn(boolean success, SmsSendCallback callback) {
        if (callback != null) {
            if(success) {
                callback.onSendSuccess();
            } else {
                callback.onSendFailure();
            }
        }
    }
}
