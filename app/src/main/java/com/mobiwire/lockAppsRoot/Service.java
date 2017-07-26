package com.mobiwire.lockAppsRoot;

import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/*
service for changing files system
 */
public class Service extends android.app.Service  {


    private String TAG="OneApps";

    public static final String ACTION_DISABLE_HOME= "com.mobiwire.DISABLE_HOME";
    public static final String ACTION_ENABLE_HOME = "com.mobiwire.ENABLE_HOME";

    public static volatile ScheduledExecutorService sPool = Executors.newScheduledThreadPool(2);



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Service start start");

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {

            if (intent.getAction().equals(ACTION_DISABLE_HOME)) {       // start whene Lock Apps
                Log.e(TAG,"ACTION_DISABLE_HOME");
                sPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        PrefUtils.setKioskModeActive(true, getApplicationContext());    // set lock state (ON)
                        runShellCommand(true); // run system commands
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        copyFile();
                    }
                });
            }

            else if (intent.getAction().equals(ACTION_ENABLE_HOME)) {   // start whene unLock Apps
                Log.e(TAG,"ACTION_ENABLE_HOME");
                sPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        PrefUtils.setKioskModeActive(false, getApplicationContext());    // set lock state (OFF)
                        runShellCommand(false);// run system commands
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        copyFile();
                    }
                });
            }

        }
        return START_STICKY;
    }



    public void runShellCommand(boolean enable){ //true : screen is locked
        Log.e(TAG,"runShellCommand");
        try {

            /*
            key information is stocked in /system/usr/keylayout/mtk-kpd.kl
            the keycode for key home is "102"
            if we want to disable home button, we add '#' in line
             */

            // create a temporary file in /Download with the same name "mtk-kpd.kl"
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard,"/Download/mtk-kpd.kl");
            if(file.exists())       // if file exist we remove it
                file.delete();
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);

            String line;
            Process process = Runtime.getRuntime().exec("su");
            OutputStream stdin = process.getOutputStream();
            InputStream stdout = process.getInputStream();

            stdin.write(("mount -o remount,rw /system\n").getBytes());          // mount system
            stdin.write("cat /system/usr/keylayout/mtk-kpd.kl\n".getBytes());   // read system file
            stdin.flush();
            stdin.close();

            BufferedReader br =
                    new BufferedReader(new InputStreamReader(stdout));
            while ((line = br.readLine()) != null) { // browse file, line by line

                if(line.contains("102")){            // if line contain 102 ==> the home key
                    if(enable==true){                // if lock apps we add # in line, else we remove it from line
                        if(!line.contains("#"))
                            line="#"+line;
                    }else{
                        if(line.contains("#"))
                            line=line.substring(1,line.length());
                    }
                }
                Log.e(TAG, line);
                pw.print(line+"\n");

            }
            br.close();
            stdin.flush();
            stdin.close();

            process.waitFor();
            process.destroy();
            pw.flush();
            pw.close();
            f.close();

        } catch (Exception ex) {
        }
    }


    // function for copy file from /Download to /system/usr/keylayout
    public void copyFile(){
        Log.e(TAG,"copyFile");
        try {

            Process process = Runtime.getRuntime().exec("su");
            OutputStream stdin = process.getOutputStream();
            stdin.write("rm /system/usr/keylayout/mtk-kpd.kl\n".getBytes());
            stdin.write("cp sdcard/Download/mtk-kpd.kl /system/usr/keylayout/\n".getBytes());
            process.waitFor();
            process.destroy();

        } catch (Exception ex) {
        }
    }


}
