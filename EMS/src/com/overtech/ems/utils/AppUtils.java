package com.overtech.ems.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;

import java.io.File;

public class AppUtils {
	
	public static boolean isSDCardAvailable(final Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			if(context.getExternalCacheDir() != null){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isApkInstalled(Context context, String packageName) {
		try {
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return info != null;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
	
	public static boolean isEmulator(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			if (imei != null && imei.equals("000000000000000")) {
				return true;
			}
			return (Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk"));
		} catch (Exception ioe) {
		}
		return false;
	}
	
	public static File getCacheDir(Context context) {
		File cacheDir;
		if (isSDCardAvailable(context)) {
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "TongCheng");
		} else {
			cacheDir = context.getCacheDir();
		}
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		return cacheDir;
	}
}
