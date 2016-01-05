package com.overtech.ems.activity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.overtech.ems.config.SystemConfig;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApplication extends Application {

	private SharedPreferences sp;
	public LocationClient mLocationClient;
	public LocationClientOption options;
	public MyLocationListener mMyLocationClient;
	public double latitude;
	public double longitude;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if (sp == null) {
			sp = getSharedPreferences(SystemConfig.PREFERENCES_NAME,Context.MODE_PRIVATE);
		}
		mLocationClient=new LocationClient(getApplicationContext());
		options=new LocationClientOption();
		options.setCoorType("bd09ll");//默认的定位结果是国测局标准，会影响导航的初始位置，设置的代表百度经纬度标准
		mMyLocationClient=new MyLocationListener();
		mLocationClient.setLocOption(options);
		mLocationClient.registerLocationListener(mMyLocationClient);
	}

	public SharedPreferences getSharePreference() {
		return sp;
	}
	public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			latitude=location.getLatitude();
			longitude=location.getLongitude();
		}
		
	}

}
