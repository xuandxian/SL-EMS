package com.overtech.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.Toast;

/**
 * @author Tony 2015-10-08
 */
public class Utilities {

	/**
	 * 公用提示框
	 * 
	 * @param message
	 * @param context
	 */
	public static void showToast(CharSequence message, Context context) {
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context.getApplicationContext(), message,
				duration);
		toast.show();
	}

	@SuppressLint("SimpleDateFormat")
	public static String setThroughTime() {
		Date date = new Date();
		SimpleDateFormat sdformat = new SimpleDateFormat(("yyyy-MM-dd"));// 24小时制
		String LgTime = sdformat.format(date);
		return LgTime;
	}

	public static String inputStream2String(InputStream in) throws IOException {
		BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = inReader.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
	}

	public static boolean checkSdCardIsExist() {
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static String getSdCardPath() {
		// In this method,Lenovo pad return StorageDirectory,not "/mnt/sdcard1"
		String sdPath = null;
		if (checkSdCardIsExist()) {
			sdPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		}
		return sdPath.toString();
	}

	/**
	 * 判断是否是Integer类型
	 * 
	 * @author daichangfu
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		if (str != null && !"".equals(str.trim())) {
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher isNum = pattern.matcher(str);
			Long number = 0l;
			if (isNum.matches()) {
				number = Long.parseLong(str);
			} else {
				return false;
			}
			if (number > 2147483647) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	// ^(?:(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])|(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9])|(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9])|(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9])).{6,}|(?:(?=.*[A-Z])(?=.*[a-z])|(?=.*[A-Z])(?=.*[0-9])|(?=.*[A-Z])(?=.*[^A-Za-z0-9])|(?=.*[a-z])(?=.*[0-9])|(?=.*[a-z])(?=.*[^A-Za-z0-9])|(?=.*[0-9])(?=.*[^A-Za-z0-9])|).{8,}

	// 获取网络状态和网络类型 create it at 2015-08-10 15:53
	public static String getNetworkStateName(Context context) {
		if (isNetworkConnected(context)) {
			int netWorkType = getConnectedType(context);
			if (netWorkType == ConnectivityManager.TYPE_WIFI) {
				return "wifi";
			} else if (netWorkType == ConnectivityManager.TYPE_MOBILE) {
				return "3G";
			} else {
				return "其他方式";
			}
		} else {
			return "无网络";
		}
	}

	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static int getConnectedType(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
			return mNetworkInfo.getType();
		}
		return -1;
	}

	// 手机号码正则表达式
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	// 密码强度正则表达式
	public static boolean isPasswordStronger(String password) {
		String string = "^(?:(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])|(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9])|(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9])|(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9])).{6,}|(?:(?=.*[A-Z])(?=.*[a-z])|(?=.*[A-Z])(?=.*[0-9])|(?=.*[A-Z])(?=.*[^A-Za-z0-9])|(?=.*[a-z])(?=.*[0-9])|(?=.*[a-z])(?=.*[^A-Za-z0-9])|(?=.*[0-9])(?=.*[^A-Za-z0-9])|).{8,}";
		Pattern p = Pattern.compile(string);
		Matcher m = p.matcher(password);
		return m.matches();
	}
}
