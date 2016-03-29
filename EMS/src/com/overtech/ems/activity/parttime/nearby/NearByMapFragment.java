package com.overtech.ems.activity.parttime.nearby;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.parttime.common.PackageDetailActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.TaskPackageBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.TaskPackage;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class NearByMapFragment extends BaseFragment {

	ArrayList<TaskPackageBean> dataList = new ArrayList<TaskPackageBean>();
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private BitmapDescriptor bitmap;
	private MarkerOptions mOverlayOptions;
	private Marker mMarker;
	private View view;
	private TaskPackage data;
	private LatLng myLocation, longPressLocation;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Gson gson = new Gson();
			switch (msg.what) {
			case StatusCode.GET_DATA_BY_LOCATION_SUCCESS:
				String json = (String) msg.obj;
				TaskPackageBean tasks = gson.fromJson(json,TaskPackageBean.class);
				ArrayList<TaskPackage> newData = (ArrayList<TaskPackage>) tasks.getModel();
				addOverLay(newData);
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast("服务器异常", context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", context);
				break;
			}
			stopProgressDialog();
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_nearby_map, container,
				false);
		initView(view);
		getExtralData();
		setMarkerClick();
		setMapLongClick();
		return view;
	}

	@SuppressWarnings("unchecked")
	private void getExtralData() {
		Bundle bundle = getArguments();
		if (null == bundle) {
			return;
		}
		ArrayList<TaskPackage> list = (ArrayList<TaskPackage>) bundle
				.getSerializable("taskPackage");
		myLocation = new LatLng(bundle.getDouble(Constant.LATITUDE),
				bundle.getDouble(Constant.LONGITUDE));
		addOverLay(list);
		setMyLocationMarker(myLocation);
	}

	private void initView(View view) {
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(17.0f));// 缩放到16
	}

	private void getDataByLatlng(String url, String flag, Param... params) {
		if (TextUtils.equals(flag, "1")) {
			startProgressDialog("正在查询...");
		}
		Request request = httpEngine.createRequest(url, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.GET_DATA_BY_LOCATION_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request request, IOException e) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				handler.sendMessage(msg);
			}
		});
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
		if (null == dataList || dataList.size() == 0) {
			Utilities.showToast("无数据", context);
		} else {
			if (null != longPressLocation) {
				OverlayOptions ooCircle = new CircleOptions()
						.fillColor(0x6663B8FF).center(longPressLocation)
						.stroke(new Stroke(1, 0x330000ff)).radius(10000);
				mBaiduMap.addOverlay(ooCircle);
				MapStatusUpdate u = MapStatusUpdateFactory
						.newLatLng(longPressLocation);
				mBaiduMap.animateMapStatus(u);
			}
			for (int i = 0; i < dataList.size(); i++) {
				data = dataList.get(i);
				String lat = data.getLatitude();
				String lon = data.getLongitude();
				if (!(TextUtils.isEmpty(lat) || TextUtils.isEmpty(lon))) {
					LatLng ll = new LatLng(Double.parseDouble(lat),
							Double.parseDouble(lon));
					Button button = new Button(getActivity());
					button.setBackgroundResource(R.drawable.map_popcontent);
					button.setGravity(Gravity.CENTER);
					button.setPadding(5, 0, 5, 30);
					button.setTextColor(Color.WHITE);
					button.setText(data.getTaskPackageName());
					bitmap = BitmapDescriptorFactory.fromView(button);
					mOverlayOptions = new MarkerOptions().position(ll)
							.icon(bitmap).zIndex(11).draggable(false)
							.period(10);
					mOverlayOptions.animateType(MarkerAnimateType.drop);
					mMarker = (Marker) (mBaiduMap.addOverlay(mOverlayOptions));
					Bundle bundle = new Bundle();
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
				if (marker.getExtraInfo()!=null) {
					TaskPackage taskPackage = (TaskPackage) marker.getExtraInfo().get("taskPackage");
					Intent intent = new Intent(activity,PackageDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("CommunityName",taskPackage.getTaskPackageName());
					bundle.putString("TaskNo", taskPackage.getTaskNo());
					bundle.putString("Longitude", taskPackage.getLongitude());
					bundle.putString("Latitude", taskPackage.getLatitude());
					intent.putExtras(bundle);
					startActivity(intent);
				}
				return true;
			}
		});
	}

	private void setMapLongClick() {
		mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng point) {
				mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(12.0f));
				longPressLocation = point;
				Param latitude = new Param(Constant.LATITUDE, String
						.valueOf(point.latitude));
				Param longitude = new Param(Constant.LONGITUDE, String
						.valueOf(point.longitude));
				getDataByLatlng(ServicesConfig.NEARBY, "1", latitude, longitude);
			}
		});
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
