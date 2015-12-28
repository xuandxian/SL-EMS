package com.overtech.ems.activity.parttime.fragment;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.parttime.nearby.NearByListFragment;
import com.overtech.ems.activity.parttime.nearby.NearByMapFragment;
import com.overtech.ems.entity.bean.TaskPackageBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.TaskPackage;
import com.overtech.ems.http.HttpEngine.Param;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class NearByFragment extends BaseFragment implements OnClickListener {

	private TextView mNearByMapTextView;
	private TextView mNearByListTextView;
	private View view;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private Fragment mNearByMap;
	private Fragment mNearByList;
	private TextView mHeadTitle;
	private ArrayList<TaskPackage> list;
	private LatLng myLocation;
	private double mLatitude;
	private double mLongitude;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			String json = (String) msg.obj;
			Gson gson = new Gson();
			TaskPackageBean tasks = gson.fromJson(json, TaskPackageBean.class);
			list=(ArrayList<TaskPackage>) tasks.getModel();
			setDefaultView(list);
//			addOverLay((ArrayList<TaskPackage>) tasks.getModel());
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_nearby, container, false);
		initBaiduMapView(view);
		init();
		return view;
	}


	private void initBaiduMapView(View v) {
		mLocationClient = new LocationClient(activity.getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPRS
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(1000); // 设置发起定位请求的间隔时间为1000ms
		mLocationClient.setLocOption(option); // 设置定位参数
		mLocationClient.start();
	}


	private void init() {
		mNearByMapTextView = (TextView) view.findViewById(R.id.tv_nearby_map);
		mNearByListTextView = (TextView) view.findViewById(R.id.tv_nearby_list);
		mHeadTitle=(TextView)view.findViewById(R.id.tv_headTitle);
		mHeadTitle.setText("附近");
		mNearByMapTextView.setOnClickListener(this);
		mNearByListTextView.setOnClickListener(this);
		manager = getFragmentManager();
		mNearByMap = new NearByMapFragment();
		mNearByList=new NearByListFragment();
	}

	private void setDefaultView(ArrayList<TaskPackage> list) {
		setExtralData(mNearByMap);
		transaction = manager.beginTransaction();
		transaction.replace(R.id.rl_nearby_content, mNearByMap, "mNearByMap");
		transaction.commit();
	}
	
	public void switchContent(Fragment from, Fragment to) {
		transaction = manager.beginTransaction();
		if (!to.isAdded()) { 
			setExtralData(to);
			transaction.hide(from).add(R.id.rl_nearby_content, to).commit(); 
		} else {
			transaction.hide(from).show(to).commit();
		}
	}
	
	private void setExtralData(Fragment fragment){
		Bundle bundle=new Bundle();
		bundle.putSerializable("taskPackage", list);
		bundle.putDouble("longitude", mLongitude);
		bundle.putDouble("latitude", mLatitude);
		fragment.setArguments(bundle);
	}
	
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				Log.e("NearByMapFragment", "定位失败");
				return;
			}
			Log.e("NearByMapFragment","定位成功,location:" + "(" + location.getLatitude() + ","+ location.getLongitude() + ")");
			mLatitude=location.getLatitude();
			mLongitude=location.getLongitude();
			myLocation = new LatLng(location.getLatitude(),location.getLongitude());
			Param latitude = new Param("latitude", String.valueOf(location.getLatitude()));
			Param longitude = new Param("longitude", String.valueOf(location.getLongitude()));
			getDataByLocation(ServicesConfig.NEARBY, "0", latitude, longitude);
		}
	}
	private void getDataByLocation(String url, String flag, Param... params) {
		if (TextUtils.equals(flag, "0")) {
			startProgressDialog("正在定位...");
		} else {
			startProgressDialog("正在查询...");
		}
		Request request = httpEngine.createRequest(url, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				stopProgressDialog();
				Message msg = new Message();
				msg.obj = response.body().string();
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request request, IOException e) {
				stopProgressDialog();
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_nearby_map:
			switchContent(mNearByList, mNearByMap);
			mNearByMapTextView.setBackgroundResource(R.drawable.horizontal_line);
			mNearByListTextView.setBackgroundResource(R.color.main_white);
			mNearByMapTextView.setTextColor(Color.rgb(0, 163, 233));
			mNearByListTextView.setTextColor(getResources().getColor(R.color.main_secondary));
			break;
		case R.id.tv_nearby_list:
			switchContent(mNearByMap, mNearByList);
			mNearByMapTextView.setBackgroundResource(R.color.main_white);
			mNearByListTextView.setBackgroundResource(R.drawable.horizontal_line);
			mNearByMapTextView.setTextColor(getResources().getColor(R.color.main_secondary));
			mNearByListTextView.setTextColor(Color.rgb(0, 163, 233));
			break;
		}
	}
}
