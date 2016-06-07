package com.overtech.ems.activity;

import java.util.Map;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import cn.jpush.android.api.JPushInterface;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.service.LocationService;
import com.overtech.ems.utils.Utilities;

public class MyApplication extends Application {

	private SharedPreferences sp;
	public MyLocationListener mMyLocationClient;
	public LocationService locationService;
	public double latitude;
	public double longitude;
	// 用于存放倒计时时间
	public static Map<String, Long> map;

	@Override
	public void onCreate() {
		super.onCreate();
		if (sp == null) {
			sp = getSharedPreferences(SystemConfig.PREFERENCES_NAME,
					Context.MODE_PRIVATE);
		}
		SDKInitializer.initialize(getApplicationContext());
		mMyLocationClient = new MyLocationListener();
		locationService = new LocationService(getApplicationContext());
		locationService.registerListener(mMyLocationClient);
		locationService.start();
		JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush
	}

	public SharedPreferences getSharePreference() {
		return sp;
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				Utilities.showToast("定位失败", getApplicationContext());
				locationService.start();
			} else {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				locationService.stop();
			}
		}

	}

}
