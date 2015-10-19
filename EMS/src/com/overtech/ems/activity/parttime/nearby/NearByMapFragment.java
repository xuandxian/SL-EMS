package com.overtech.ems.activity.parttime.nearby;

import java.util.ArrayList;
import java.util.HashMap;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.overtech.ems.R;
import com.overtech.ems.entity.test.Data;
import com.overtech.ems.utils.Utilities;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class NearByMapFragment extends Fragment {

	ArrayList<Data> dataList = new ArrayList<Data>();
	private HashMap<String, Data> mHashMap = new HashMap<String, Data>();
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private LocationClient mLocationClient;
	private BitmapDescriptor bitmap;
	private OverlayOptions mOverlayOptions;
	private Marker mMarker;
	private View view;
	private Data data;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_nearby_map, container,
				false);
		initBaiduMapView(view);
		getDataList();
		addOverLay(dataList);
		setMarkerClick();
		return view;
	}

	private void initBaiduMapView(View view) {
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 实例化定位服务，LocationClient类必须在主线程中声明
		mLocationClient = new LocationClient(getActivity());
		mLocationClient.registerLocationListener(new BDLocationListenerImpl());// 注册定位监听接口
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPRS
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
		mLocationClient.setLocOption(option); // 设置定位参数
		mLocationClient.start();
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(16.0f));// 缩放到16
	}

	private void setBaiduMapMarker(LatLng point) {
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_location);
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		mBaiduMap.addOverlay(option);
	}

	public class BDLocationListenerImpl implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			double mLatitude = location.getLatitude();
			double mLongitude = location.getLongitude();
			Log.e("经纬度", "经纬度：" + "(" + mLongitude + "," + mLatitude + ")");
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			setBaiduMapMarker(ll);
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
	}

	private ArrayList<Data> getDataList() {
		dataList = new ArrayList<Data>();
		Data data1 = new Data("1", "虹枫大楼", "121.437466", "31.200715");
		Data data2 = new Data("2", "虹交小区", "121.439693", "31.200386");
		Data data3 = new Data("3", "明仕苑", "121.441279", "31.202749");
		Data data4 = new Data("4", "乐山公寓", "121.439312", "31.203274");
		Data data5 = new Data("5", "番禺大楼", "121.43429", "31.202101");
		dataList.add(data1);
		dataList.add(data2);
		dataList.add(data3);
		dataList.add(data4);
		dataList.add(data5);
		return dataList;
	}

	public void addOverLay(ArrayList<Data> dataList) {
		if (mMapView == null) {
			mMapView = (MapView) view.findViewById(R.id.bmapView);
		}
		mBaiduMap = mMapView.getMap();
		if (null != mBaiduMap) {
			mBaiduMap.clear();
		} else {
			return;
		}
		for (int i = 0; i < dataList.size(); i++) {
			data = dataList.get(i);
			String lat = data.latitude;
			String lon = data.longitude;
			if (!(TextUtils.isEmpty(lat) || TextUtils.isEmpty(lon))) {
				LatLng ll = new LatLng(Double.parseDouble(lon),
						Double.parseDouble(lat));
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				Button button = new Button(getActivity());
				button.setBackgroundResource(R.drawable.map_popcontent);
				button.setGravity(Gravity.CENTER);
				button.setPadding(5, 0, 5, 30);
				button.setTextColor(Color.WHITE);
				button.setText(data.name);
				bitmap = BitmapDescriptorFactory.fromView(button);
				mOverlayOptions = new MarkerOptions().position(ll).icon(bitmap)
						.zIndex(11).draggable(false);
				mMarker = (Marker) (mBaiduMap.addOverlay(mOverlayOptions));
				mMarker.setZIndex(Integer.parseInt(data.id));
				mHashMap.put(data.id, data);
			}
		}

	}

	private void setMarkerClick() {
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker marker) {
				data = mHashMap.get(String.valueOf(marker.getZIndex()));
				Utilities.showToast("你点击了：" + data.name, getActivity());
				return true;
			}
		});
	}

	@Override
	public void onDestroy() {
		// 退出时销毁定位
		if (mLocationClient.isStarted()) {
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
