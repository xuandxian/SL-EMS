package com.overtech.ems.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.config.SystemConfig;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tony1213 on 15/11/23.
 */
public class SharePreferencesUtils {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static SharePreferencesUtils sharedPrefUtils;
    private Map<String, SoftReference<String>> prefCache = new HashMap<String, SoftReference<String>>();

    private SharePreferencesUtils(){
        sharedPreferences = MyApplication.getInstance().getSharedPreferences(SystemConfig.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public synchronized static SharePreferencesUtils getInstance(){
        if (sharedPrefUtils == null) {
            sharedPrefUtils = new SharePreferencesUtils();
        }
        return sharedPrefUtils;
    }


}
