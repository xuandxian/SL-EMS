package com.overtech.ems.utils;

import android.util.Log;
import com.overtech.ems.BuildConfig;

/**
 * Created by Tony1213 on 15/12/8.
 */
final class Logr {
    public static void d(String message) {
        if (BuildConfig.DEBUG) {
            Log.d("EMS", message);
        }
    }

    public static void d(String message, Object... args) {
        if (BuildConfig.DEBUG) {
            d(String.format(message, args));
        }
    }
}
