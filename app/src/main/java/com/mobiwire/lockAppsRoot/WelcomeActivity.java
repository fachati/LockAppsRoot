package com.mobiwire.lockAppsRoot;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

// need reboot for apply change in system
// for lock & unlock

/*
user interface
 */
public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if(PrefUtils.isKioskModeActive(this)) {
            Intent svc = new Intent(this, Service.class);   //start service in background
            svc.setAction(Service.ACTION_DISABLE_HOME);     // change system file [/system/usr/keylayout/mtk-kpd.kl]
            startService(svc);
        }
    }
}
