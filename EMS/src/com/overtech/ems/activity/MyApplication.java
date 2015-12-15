package com.overtech.ems.activity;

import com.overtech.ems.config.SystemConfig;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApplication extends Application {

	private SharedPreferences sp;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if (sp == null) {
			sp = getSharedPreferences(SystemConfig.PREFERENCES_NAME,
					Context.MODE_PRIVATE);
		}
	}

	public SharedPreferences getSharePreference() {
		return sp;
	}

}
