package com.overtech.ems.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.overtech.ems.R;
import com.overtech.ems.http.HttpEngine;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.bitmap.ImageLoader;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;

/**
 * Created by Tony1213 on 15/12/21.
 */
public class BaseFragment extends Fragment {

    public ImageLoader imageLoader;

    public Activity activity;

    public Context context;

    public FragmentManager fragmentManager;

    public OkHttpClientManager okHttpClientManager;

    public HttpEngine httpEngine;

    public CustomProgressDialog progressDialog;

    public NiftyDialogBuilder dialogBuilder;
    
    public LocationClient mLocationClient;
    
    public LocationClientOption option;
    
    public String mLatitude;
    
    public String mLongitude;
    
    public LatLng myLocation;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        context =getActivity();
        SDKInitializer.initialize(activity);
        registerBaiduMapLocationListener();
        fragmentManager = getFragmentManager();
        dialogBuilder = NiftyDialogBuilder.getInstance(activity);
        httpEngine = HttpEngine.getInstance();
        httpEngine.initContext(context);
        imageLoader = ImageLoader.getInstance();
        imageLoader.initContext(context);
        progressDialog = CustomProgressDialog.createDialog(context);
        progressDialog.setMessage(context.getString(R.string.loading_public_default));
    }
    private void registerBaiduMapLocationListener() {
    	// 实例化定位服务，LocationClient类必须在主线程中声明
		mLocationClient = new LocationClient(activity.getApplicationContext());
		mLocationClient.registerLocationListener(new BDLocationListener() {
			
			@Override
			public void onReceiveLocation(BDLocation location) {
				// map view 销毁后不在处理新接收的位置
				if (location == null) {
					return;
				}
				mLatitude = new String(String.valueOf(location.getLatitude()));
				mLongitude = new String(String.valueOf(location.getLongitude()));
				myLocation=new LatLng(location.getLatitude(), location.getLongitude());
			}
		});// 注册定位监听接口
		option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPRS
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
		mLocationClient.setLocOption(option); // 设置定位参数
		mLocationClient.start();
	}
    
    public String getmLatitude(){
    	return mLatitude;
    }
    
    public String getmLongitude() {
		return mLongitude;
	}
    
	@Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if (mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
    }

    public void startProgressDialog(String content) {
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(context);
            progressDialog.setMessage(content);
        }
        progressDialog.show();
    }

    public void stopProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
