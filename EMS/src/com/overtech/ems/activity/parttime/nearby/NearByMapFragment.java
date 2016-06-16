package com.overtech.ems.activity.parttime.nearby;

import java.util.List;

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
import com.overtech.ems.activity.parttime.common.PackageDetailActivity;
import com.overtech.ems.activity.parttime.fragment.NearByFragment;
import com.overtech.ems.activity.parttime.fragment.NearByFragment.NearByMapCallback;
import com.overtech.ems.entity.bean.TaskPackageBean.TaskPackage;
import com.overtech.ems.utils.Utilities;

public class NearByMapFragment extends BaseFragment implements NearByMapCallback {

	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private BitmapDescriptor bitmap;
	private InfoWindow mInfoWindow;
	private MarkerOptions mOverlayOptions;
	private Marker mMarker;
	private View view;
	private TaskPackage data;
	private double latitude;
	private double longitude;
	private LatLng myLocation, longPressLocation;
	private List<TaskPackage> list;
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
		
		((NearByFragment) getParentFragment()).setNearByMapCallback(this);
		latitude = ((MyApplication) getActivity().getApplicationContext()).latitude;
		longitude = ((MyApplication) getActivity().getApplicationContext()).longitude;
		if (latitude == 0 || longitude == 0) {
			Utilities.showToast("定位失败", activity);
			return;
		}
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

	public void addOverLay(List<TaskPackage> dataList) {
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
			Utilities.showToast("无数据", activity);
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
				String lat = data.latitude;
				String lon = data.longitude;
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
					// Bundle bundle = new Bundle();
					// bundle.putSerializable("taskPackage", data);
					// mMarker.setExtraInfo(bundle);
				}
			}
		}
	}

	private void setMarkerClick() {
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker marker) {
				if (marker.getExtraInfo() != null) {
					final TaskPackage taskPackage = (TaskPackage) marker
							.getExtraInfo().get("taskPackage");
					BaiduMapInfoWindow infoWindow = new BaiduMapInfoWindow(
							getActivity(), taskPackage);
					mInfoWindow = new InfoWindow(infoWindow, marker
							.getPosition(), -40);
					mBaiduMap.showInfoWindow(mInfoWindow);
					infoWindow.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(activity,
									PackageDetailActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("CommunityName",
									taskPackage.taskPackageName);
							bundle.putString("TaskNo", taskPackage.taskNo);
							bundle.putString("Longitude", taskPackage.longitude);
							bundle.putString("Latitude", taskPackage.latitude);
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

	@Override
	public void callback() {
		// TODO Auto-generated method stub
		list=((NearByFragment)getParentFragment()).getData();
		myLocation = new LatLng(latitude, longitude);
		addOverLay(list);
		setMyLocationMarker(myLocation);
	}
}
