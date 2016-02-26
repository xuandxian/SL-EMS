package com.overtech.ems.activity;

import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.utils.Utilities;

public class MyApplication extends Application {

	private SharedPreferences sp;
	public LocationClient mLocationClient;
	public LocationClientOption options;
	public MyLocationListener mMyLocationClient;
	public double latitude;
	public double longitude;
	// 用于存放倒计时时间
	public static Map<String, Long> map;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if (sp == null) {
			sp = getSharedPreferences(SystemConfig.PREFERENCES_NAME,
					Context.MODE_PRIVATE);
		}
		SDKInitializer.initialize(getApplicationContext());
		mLocationClient = new LocationClient(getApplicationContext());
		options = new LocationClientOption();
		options.setCoorType("bd09ll");// 默认的定位结果是国测局标准，会影响导航的初始位置，设置的代表百度经纬度标准
		mMyLocationClient = new MyLocationListener();
		mLocationClient.setLocOption(options);
		mLocationClient.registerLocationListener(mMyLocationClient);
		mLocationClient.start();
		mLocationClient.requestLocation();
		JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush
		Log.e("==application==", "=======onCreate======");
	}

	public SharedPreferences getSharePreference() {
		return sp;
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			Log.e("==收到定位信息===", "ssssssbbbbbb");
			if (location == null) {
				Utilities.showToast("定位失败", getApplicationContext());
				return;
			}
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}

	}

}
