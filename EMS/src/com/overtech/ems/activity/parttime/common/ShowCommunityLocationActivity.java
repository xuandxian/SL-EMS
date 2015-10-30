package com.overtech.ems.activity.parttime.common;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.overtech.ems.entity.test.Data;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowCommunityLocationActivity extends Activity {
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private LocationClient mLocationClient;
	private LatLng location;
	private BitmapDescriptor bitmap;
	private InfoWindow mInfoWindow;
	private Data data;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_community_location);
		findViewById();
		getLocationData();
		initBaiduMap();
		initView();
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
	}

	private void getLocationData() {
		data = new Data("1", "虹枫大楼", "121.437466", "31.200715");
		String lat = data.latitude;
		String lon = data.longitude;
		location = new LatLng(Double.parseDouble(lon), Double.parseDouble(lat));
	}

	private void initBaiduMap() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 实例化定位服务，LocationClient类必须在主线程中声明
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(new BDLocationListenerImpl());// 注册定位监听接口
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPRS
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
		mLocationClient.setLocOption(option); // 设置定位参数
		mLocationClient.start();
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(17.0f));// 缩放到16
	}

	private void initView() {
		context = ShowCommunityLocationActivity.this;
		mHeadContent.setText("小区位置");
		mHeadBack.setVisibility(View.VISIBLE);
		setCommunityMarker(location);
		BaiduMapInfoWindow infoWindow = new BaiduMapInfoWindow(context,
				data);
		mInfoWindow = new InfoWindow(infoWindow, location, -45);
		mBaiduMap.showInfoWindow(mInfoWindow);
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker arg0) {
				return false;
			}
		});
		mHeadBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private void setCommunityMarker(LatLng ll) {
		if (mMapView == null) {
			mMapView = (MapView) findViewById(R.id.bmapView);
		}
		mBaiduMap = mMapView.getMap();
		if (null != mBaiduMap) {
			mBaiduMap.clear();
		} else {
			return;
		}
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_community);
		MarkerOptions option = new MarkerOptions().position(ll).icon(bitmap)
				.zIndex(0);
		option.animateType(MarkerAnimateType.grow);
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
			setMyLocationMarker(ll);
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
	}

	public void setMyLocationMarker(LatLng point) {
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_location);
		MarkerOptions option = new MarkerOptions().position(point).icon(bitmap)
				.zIndex(0);
		option.animateType(MarkerAnimateType.drop);
		mBaiduMap.addOverlay(option);
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
