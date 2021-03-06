package com.overtech.ems.activity;

import java.util.Map;

import android.app.Application;
import android.content.SharedPreferences;
import cn.jpush.android.api.JPushInterface;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.overtech.ems.service.LocationService;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
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
		String process = Utilities.getCurProcessName(getApplicationContext());
		Logr.e(process);
		if (process.equals("com.overtech.ems")) {
			SDKInitializer.initialize(getApplicationContext());
			mMyLocationClient = new MyLocationListener();
			locationService = new LocationService(getApplicationContext());
			locationService.registerListener(mMyLocationClient);
			locationService.start();
			JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
			JPushInterface.init(this); // 初始化 JPush
			Logr.e("==MyApplication= 执行了");
			
			latitude=Double.parseDouble((String)SharePreferencesUtils.get(getApplicationContext(), "latitude", "0.0"));
			longitude= Double.parseDouble((String)SharePreferencesUtils.get(getApplicationContext(), "longitude", "0.0"));
			Logr.e("文件存取的latitude=="+latitude+"==文件存取的longitude=="+longitude);
		} else {
			Logr.e("myapplication++++remote进行执行了");
		}
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
				Logr.e("latitude==" + latitude + "==longitude==" + longitude);
//				locationService.stop();
				SharePreferencesUtils.put(getApplicationContext(), "latitude", latitude+"");
				SharePreferencesUtils.put(getApplicationContext(), "longitude", longitude+"");
			}
		}

	}
	

}
