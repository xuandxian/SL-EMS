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
import com.baidu.mapapi.utils.DistanceUtil;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowCommunityLocationActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private LocationClient mLocationClient;
	private LatLng communituLocation;
	private BitmapDescriptor bitmap;
	private InfoWindow mInfoWindow;
	private Context context;
	private String mCommunityName;
	private String mLongitude;
	private String mLatitude;
	private LatLng myLocation;
	public BDLocationListener myListener=new MyLocationListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_community_location);
		getExtrasData();
		findViewById();
		getLocationData();
		initBaiduMap();
		initView();
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
	}
	
	private void getExtrasData() {
		Bundle bundle = getIntent().getExtras();
		mCommunityName = bundle.getString("CommunityName");
		mLongitude=bundle.getString("Longitude");
		mLatitude=bundle.getString("Latitude");
	}

	private void getLocationData() {
		communituLocation = new LatLng(Double.parseDouble(mLatitude), Double.parseDouble(mLongitude));
	}

	private void initBaiduMap() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(myListener);// 注册定位监听接口
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPRS
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(1000); // 设置发起定位请求的间隔时间为1000ms
		mLocationClient.setLocOption(option); // 设置定位参数
		mLocationClient.start();
	}

	private void initView() {
		context = ShowCommunityLocationActivity.this;
		mHeadContent.setText("小区位置");
		mHeadBack.setVisibility(View.VISIBLE);
		setCommunityMarker(communituLocation);
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
		MarkerOptions option = new MarkerOptions().position(ll).icon(bitmap).zIndex(0);
		option.animateType(MarkerAnimateType.grow);
		mBaiduMap.addOverlay(option);
		BaiduMapInfoWindow infoWindow = new BaiduMapInfoWindow(context,mCommunityName);
		mInfoWindow = new InfoWindow(infoWindow, communituLocation, -40);
		mBaiduMap.showInfoWindow(mInfoWindow);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);
	}

	public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (null==location) {
				Log.e("ShowCommunityLocationActivity", "定位失败");
				return;
			}
			Log.e("ShowCommunityLocationActivity", "定位成功");
			myLocation=new LatLng(location.getLatitude(), location.getLongitude());
			setMyLocationMarker(myLocation);
			double distance=DistanceUtil.getDistance(myLocation,communituLocation);
			animateMapStatusByDistance(distance);
		}
	}
	public void setMyLocationMarker(LatLng point) {
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_location);
		MarkerOptions option = new MarkerOptions().position(point).icon(bitmap).zIndex(0);
		option.animateType(MarkerAnimateType.drop);
		mBaiduMap.addOverlay(option);
	}

	public void animateMapStatusByDistance(double distance) {
		if (distance>=20000) {
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(11.0f));
		}else if (distance<20000 && distance>=1000) {
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(12.0f));
		}else if(distance<10000 && distance>=5000){
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(13.0f));
		}else if (distance<5000 && distance>=2000) {
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(14.0f));
		}else if (distance<2000 && distance>=1000) {
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));
		}else if (distance<1000 && distance>=500) {
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(16.0f));
		}else if (distance<500 && distance>=200) {
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(17.0f));
		}else {
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(19.0f));
		}
	}

	@Override
	public void onDestroy() {
		// 退出时销毁定位
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
