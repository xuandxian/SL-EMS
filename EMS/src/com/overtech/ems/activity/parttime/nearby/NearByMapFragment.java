package com.overtech.ems.activity.parttime.nearby;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.parttime.common.PackageDetailActivity;
import com.overtech.ems.entity.bean.TaskPackageBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.TaskPackage;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class NearByMapFragment extends BaseFragment {

	ArrayList<TaskPackageBean> dataList = new ArrayList<TaskPackageBean>();
	private HashMap<String, TaskPackage> mHashMap = new HashMap<String, TaskPackage>();
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private BitmapDescriptor bitmap;
	private MarkerOptions mOverlayOptions;
	private Marker mMarker;
	private View view;
	private TaskPackage data;
	public LocationClient mLocationClient = null;
	private LatLng myLocation, clickLocation;
	public BDLocationListener myListener = new MyLocationListener();

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String json = (String) msg.obj;
			Gson gson = new Gson();
			TaskPackageBean tasks = gson.fromJson(json, TaskPackageBean.class);
			addOverLay((ArrayList<TaskPackage>) tasks.getModel());
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_nearby_map, container,
				false);
		initBaiduMapView(view);
		setMarkerClick();
		setMapLongClick();
		return view;
	}

	private void initBaiduMapView(View view) {
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mLocationClient = new LocationClient(activity.getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPRS
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(1000); // 设置发起定位请求的间隔时间为1000ms
		mLocationClient.setLocOption(option); // 设置定位参数
		mLocationClient.start();
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(17.0f));// 缩放到16
	}

	private void initData(String url, String flag, Param... params) {
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

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || mMapView == null) {
				Log.e("NearByMapFragment", "定位失败");
				return;
			}
			Log.e("NearByMapFragment","定位成功,location:" + "(" + location.getLatitude() + ","+ location.getLongitude() + ")");
			myLocation = new LatLng(location.getLatitude(),location.getLongitude());
			Param latitude = new Param("latitude", String.valueOf(location.getLatitude()));
			Param longitude = new Param("longitude", String.valueOf(location.getLongitude()));
			initData(ServicesConfig.NEARBY, "0", latitude, longitude);
		}
	}

	private void setMyLocationMarker(LatLng point) {
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_location);
		MarkerOptions option = new MarkerOptions().position(point).icon(bitmap).zIndex(0);
		option.animateType(MarkerAnimateType.grow);
		mBaiduMap.addOverlay(option);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(myLocation);
		mBaiduMap.animateMapStatus(u);
	}

	public void addOverLay(ArrayList<TaskPackage> dataList) {
		if (mMapView == null) {
			mMapView = (MapView) view.findViewById(R.id.bmapView);
		}
		mBaiduMap = mMapView.getMap();
		if (null != mBaiduMap) {
			mBaiduMap.clear();
		} else {
			return;
		}
		setMyLocationMarker(myLocation);
		if (dataList.size() == 0) {
			Utilities.showToast("无数据", context);
		} else {
			if (null != clickLocation) {
				OverlayOptions ooCircle = new CircleOptions().fillColor(0x6663B8FF).center(clickLocation).stroke(new Stroke(1, 0x330000ff)).radius(10000);
				mBaiduMap.addOverlay(ooCircle);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(clickLocation);
				mBaiduMap.animateMapStatus(u);
			}
			for (int i = 0; i < dataList.size(); i++) {
				data = dataList.get(i);
				String lat = data.getLatitude();
				String lon = data.getLongitude();
				if (!(TextUtils.isEmpty(lat) || TextUtils.isEmpty(lon))) {
					LatLng ll = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
					Button button = new Button(getActivity());
					button.setBackgroundResource(R.drawable.map_popcontent);
					button.setGravity(Gravity.CENTER);
					button.setPadding(5, 0, 5, 30);
					button.setTextColor(Color.WHITE);
					button.setText(data.getProjectName());
					bitmap = BitmapDescriptorFactory.fromView(button);
					mOverlayOptions = new MarkerOptions().position(ll).icon(bitmap).zIndex(11).draggable(false).period(10);
					mOverlayOptions.animateType(MarkerAnimateType.drop);
					mMarker = (Marker) (mBaiduMap.addOverlay(mOverlayOptions));
					Bundle bundle=new Bundle();
					bundle.putSerializable("taskPackage", data);
					mMarker.setExtraInfo(bundle);
				}
			}
		}
	}
	
	private void setMarkerClick() {
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker marker) {
				TaskPackage taskPackage=(TaskPackage)marker.getExtraInfo().get("taskPackage");
				Intent intent = new Intent(activity,PackageDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("CommunityName", taskPackage.getProjectName());
				bundle.putString("TaskNo", taskPackage.getTaskNo());
				bundle.putString("Longitude", taskPackage.getLongitude());
				bundle.putString("Latitude", taskPackage.getLatitude());
				intent.putExtras(bundle);
				startActivity(intent);
				return true;
			}
		});
	}

	private void setMapLongClick() {
		mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng point) {
				mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(12.0f));
				clickLocation = point;
				Param latitude = new Param("latitude", String.valueOf(point.latitude));
				Param longitude = new Param("longitude", String.valueOf(point.longitude));
				initData(ServicesConfig.NEARBY, "1", latitude, longitude);
			}
		});
	}

	@Override
	public void onDestroy() {
		if (mLocationClient.isStarted()) {
			mLocationClient.unRegisterLocationListener(myListener);
			mLocationClient.stop();
		}
		// 关闭定位图层
		if (mBaiduMap.isMyLocationEnabled()) {
			mBaiduMap.setMyLocationEnabled(false);
		}
		if (mMapView != null) {
			mMapView.onDestroy();
		}
		if (null != bitmap) {
			bitmap.recycle();
		}
		super.onDestroy();
	}
}
