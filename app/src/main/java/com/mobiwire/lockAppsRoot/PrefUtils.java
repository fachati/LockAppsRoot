package com.mobiwire.lockAppsRoot;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

// shared preference for saving state of lock (ON | OFF)

public class PrefUtils {
    private static final String PREF_LOCK_MODE = "pref_lock_mode";

    // return state of lock
    public static boolean isKioskModeActive(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_LOCK_MODE, false);
    }

    // change lock state
    public static void setKioskModeActive(final boolean active, final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_LOCK_MODE, active).commit();
    }
}