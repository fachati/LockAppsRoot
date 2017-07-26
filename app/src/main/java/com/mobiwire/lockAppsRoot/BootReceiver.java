package com.mobiwire.lockAppsRoot;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// receiver OnBoot : for starting apps in boot
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(PrefUtils.isKioskModeActive(context)) {                          // if LockScreen is ON
            Intent myIntent = new Intent(context, WelcomeActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);
        }

    }
}