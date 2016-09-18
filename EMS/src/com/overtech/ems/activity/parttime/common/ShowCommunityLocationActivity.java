package com.overtech.ems.activity.parttime.common;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

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
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.utils.Logr;

public class ShowCommunityLocationActivity extends BaseActivity {
	private Toolbar toolbar;
	private ActionBar actionBar;
	private AppCompatTextView tvTitle;
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private LatLng communituLocation;
	private BitmapDescriptor bitmap;
	private InfoWindow mInfoWindow;
	private Context context;
	private String mCommunityName;
	private String mLongitude;
	private String mLatitude;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_show_community_location;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getExtrasData();
		findViewById();
		getLocationData();
		initBaiduMap();
		initView();
	}

	private void findViewById() {
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
	}

	private void getExtrasData() {
		Bundle bundle = getIntent().getExtras();
		mCommunityName = bundle.getString("CommunityName");
		mLongitude = bundle.getString("Longitude");
		mLatitude = bundle.getString("Latitude");
		Logr.e("longitude==" + mLongitude + "==latitude==" + mLatitude);
	}

	private void getLocationData() {
		communituLocation = new LatLng(Double.parseDouble(mLatitude),
				Double.parseDouble(mLongitude));
	}

	private void initBaiduMap() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));// 缩放到15
	}

	private void initView() {
		context = ShowCommunityLocationActivity.this;
		tvTitle.setText("小区位置");
		toolbar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		setCommunityMarker(communituLocation);
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker arg0) {
				BaiduMapInfoWindow infoWindow = new BaiduMapInfoWindow(context,
						mCommunityName);
				mInfoWindow = new InfoWindow(infoWindow, communituLocation, -40);
				mBaiduMap.showInfoWindow(mInfoWindow);
				return true;
			}
		});
	}

	private void setCommunityMarker(LatLng ll) {

		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_map_community);
		MarkerOptions option = new MarkerOptions().position(ll).icon(bitmap)
				.zIndex(14);
		option.animateType(MarkerAnimateType.grow);
		mBaiduMap.addOverlay(option);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);
		BaiduMapInfoWindow infoWindow = new BaiduMapInfoWindow(context,
				mCommunityName);
		mInfoWindow = new InfoWindow(infoWindow, communituLocation, -40);
		mBaiduMap.showInfoWindow(mInfoWindow);
	}

	@Override
	public void onDestroy() {
		if (mMapView != null) {
			mMapView.onDestroy();
		}
		if (null != bitmap) {
			bitmap.recycle();
		}
		super.onDestroy();
	}

}
