package com.overtech.ems.activity.parttime.nearby;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.model.LatLng;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.activity.parttime.common.PackageDetailActivity;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.http.OkHttpClientManager.ResultCallback;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Request;

public class NearByMapFragment extends BaseFragment {

	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private BitmapDescriptor bitmap;
	private InfoWindow mInfoWindow;
	private MarkerOptions mOverlayOptions;
	private Marker mMarker;
	private View view;
	private Map<String, Object> data;
	private double latitude;
	private double longitude;
	private String uid;
	private String certificate;
	private LatLng myLocation, longPressLocation;
	private List<Map<String, Object>> list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_nearby_map, container,
				false);
		initView(view);
		getExtralData();
		setMarkerClick();
		// setMapLongClick();
		return view;
	}

	@SuppressWarnings("unchecked")
	private void getExtralData() {
		uid = ((MainActivity) getActivity()).getUid();
		certificate = ((MainActivity) getActivity()).getCertificate();
		latitude = ((MyApplication) getActivity().getApplicationContext()).latitude;
		longitude = ((MyApplication) getActivity().getApplicationContext()).longitude;
		if (latitude == 0 || longitude == 0) {
			Utilities.showToast("定位失败", activity);
			return;
		}
		onRefresh();
	}
	public void onRefresh(){
		Requester requester = new Requester();
		requester.cmd = 20030;
		requester.certificate = certificate;
		requester.uid = uid;
		requester.body.put("latitude", String.valueOf(latitude));
		requester.body.put("longitude", String.valueOf(longitude));
		ResultCallback<Bean> nearCallback = new ResultCallback<Bean>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				Logr.e(request.toString());
			}

			@Override
			public void onResponse(Bean response) {
				// TODO Auto-generated method stub
				int st = response.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(response.msg, activity);
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(activity, LoginActivity.class);
					startActivity(intent);
					return;
				} else if (st == 1) {// 上岗证相关
					Utilities.showToast(response.msg, activity);
				}
				list = (List<Map<String, Object>>) response.body.get("data");
				myLocation = new LatLng(latitude, longitude);
				addOverLay(list);
				setMyLocationMarker(myLocation);
			}

		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, nearCallback,
				gson.toJson(requester));
	}
	private void initView(View view) {
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(13.0f));// 缩放到13
	}

	private void setMyLocationMarker(LatLng point) {
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_location);
		MarkerOptions option = new MarkerOptions().position(point).icon(bitmap)
				.zIndex(0);
		option.animateType(MarkerAnimateType.grow);
		mBaiduMap.addOverlay(option);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(myLocation);
		mBaiduMap.animateMapStatus(u);
		Button button = new Button(getActivity());
		button.setBackgroundResource(R.drawable.map_bubble_info);
		button.setGravity(Gravity.CENTER);
		button.setPadding(5, 0, 5, 30);
		button.setTextColor(Color.GRAY);
		button.setText("我的位置");
		InfoWindow myInfoWindow = new InfoWindow(button, point, -47);
		mBaiduMap.showInfoWindow(myInfoWindow);
	}

	public void addOverLay(List<Map<String, Object>> dataList) {
		if (mMapView == null) {
			mMapView = (MapView) view.findViewById(R.id.bmapView);
		}
		mBaiduMap = mMapView.getMap();
		if (null != mBaiduMap) {
			mBaiduMap.clear();
		} else {
			return;
		}
		if (null == dataList || dataList.size() == 0) {
			// Utilities.showToast("无数据", activity);
		} else {
			// if (null != longPressLocation) {
			// OverlayOptions ooCircle = new CircleOptions()
			// .fillColor(0x6663B8FF).center(longPressLocation)
			// .stroke(new Stroke(1, 0x330000ff)).radius(10000);
			// mBaiduMap.addOverlay(ooCircle);
			// MapStatusUpdate u = MapStatusUpdateFactory
			// .newLatLng(longPressLocation);
			// mBaiduMap.animateMapStatus(u);
			// }
			for (int i = 0; i < dataList.size(); i++) {
				data = dataList.get(i);
				String lat = data.get("latitude").toString();
				String lon = data.get("longitude").toString();
				if (!(TextUtils.isEmpty(lat) || TextUtils.isEmpty(lon))) {

					LatLng ll = new LatLng(Double.parseDouble(lat),
							Double.parseDouble(lon));
					// Button button = new Button(getActivity());
					// button.setBackgroundResource(R.drawable.map_popcontent);
					// button.setGravity(Gravity.CENTER);
					// button.setPadding(5, 0, 5, 30);
					// button.setTextColor(Color.WHITE);
					// button.setText(data.getTaskPackageName());
					bitmap = BitmapDescriptorFactory
							.fromResource(R.drawable.icon_map_community);
					mOverlayOptions = new MarkerOptions().position(ll)
							.icon(bitmap).zIndex(0).draggable(false).period(10);
					mOverlayOptions.animateType(MarkerAnimateType.drop);
					mMarker = (Marker) (mBaiduMap.addOverlay(mOverlayOptions));
					Bundle bundle = new Bundle();
					bundle.putString("taskPackageName",
							data.get("taskPackageName").toString());
					bundle.putString("taskNo", data.get("taskNo").toString());
					bundle.putString("latitude", data.get("latitude")
							.toString());
					bundle.putString("longitude", data.get("longitude")
							.toString());
					mMarker.setExtraInfo(bundle);
				}
			}
		}
	}

	private void setMarkerClick() {
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker marker) {
				if (marker.getExtraInfo() != null) {
					Bundle markerBundle = marker.getExtraInfo();
					final String mTaskPackageName = markerBundle
							.getString("taskPackageName");
					final String mTaskNo = markerBundle.getString("taskNo");
					final String mLatitude = markerBundle.getString("latitude");
					final String mLongitude = markerBundle
							.getString("longitude");
					BaiduMapInfoWindow infoWindow = new BaiduMapInfoWindow(
							getActivity(), mTaskPackageName);
					mInfoWindow = new InfoWindow(infoWindow, marker
							.getPosition(), -40);
					mBaiduMap.showInfoWindow(mInfoWindow);
					infoWindow.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(activity,
									PackageDetailActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("CommunityName", mTaskPackageName);
							bundle.putString("TaskNo", mTaskNo);
							bundle.putString("Longitude", mLatitude);
							bundle.putString("Latitude", mLongitude);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					});
				}
				return true;
			}
		});
	}

	// 长按地图事件，显示周围10km的维保任务包
	// private void setMapLongClick() {
	// mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {
	//
	// @Override
	// public void onMapLongClick(LatLng point) {
	// mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(12.0f));
	// longPressLocation = point;
	// Param latitude = new Param(Constant.LATITUDE, String
	// .valueOf(point.latitude));
	// Param longitude = new Param(Constant.LONGITUDE, String
	// .valueOf(point.longitude));
	// getDataByLatlng(ServicesConfig.NEARBY, "1", latitude, longitude);
	// }
	// });
	// }

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		Logr.e("NearByMapFragment=="+hidden);
		if(!hidden){
			Logr.e("地图刷新了");
			onRefresh();
		}
	}
	@Override
	public void onDestroy() {
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
