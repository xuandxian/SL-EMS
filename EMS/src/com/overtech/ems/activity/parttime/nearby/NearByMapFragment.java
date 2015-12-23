package com.overtech.ems.activity.parttime.nearby;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.adapter.GrabTaskAdapter;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.TaskPackage;
import com.overtech.ems.entity.parttime.TaskPackageBean;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
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

public class NearByMapFragment extends BaseFragment{

	ArrayList<TaskPackageBean> dataList = new ArrayList<TaskPackageBean>();
	private HashMap<String, TaskPackage> mHashMap = new HashMap<String, TaskPackage>();
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private BitmapDescriptor bitmap;
	private MarkerOptions mOverlayOptions;
	private Marker mMarker;
	private View view;
	private TaskPackage data;
	private ArrayList<TaskPackage> list;
	
//	private Handler handler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			String json = (String) msg.obj;
//			Gson gson = new Gson();
//			TaskPackageBean tasks = gson.fromJson(json, TaskPackageBean.class);
//			list = (ArrayList<TaskPackage>) tasks.getModel();
//			if (null==myLocation) {
//				Utilities.showToast("null==myLocation", context);
//				return;
//			}
//		};
//	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Param latitude=new Param("latitude", getmLatitude());
		Param longitude=new Param("longitude", getmLongitude());
		getDataList(ServicesConfig.NEARBY,latitude,longitude);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_nearby_map, container,false);
		initBaiduMapView(view);
//		setMarkerClick();
		return view;
	}

	private void initBaiduMapView(View view) {
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(17.0f));// 缩放到16
	}

	private void getDataList(String url,Param... params) {
		startProgressDialog("正在查询...");
		Request request=httpEngine.createRequest(url, params);
        Call call=httpEngine.createRequestCall(request);
        call.enqueue(new Callback() {
			
			@Override
			public void onResponse(Response response) throws IOException {
				stopProgressDialog();
				Log.e("NearByMapFragment:onResponse", response.body().string());
//				Message msg = new Message();
//				msg.obj = response.body().string();
//				handler.sendMessage(msg);
			}
			
			@Override
			public void onFailure(Request request, IOException e) {
				stopProgressDialog();
				Log.e("NearByMapFragment", "onFailure");
			}
		});
	}
	
	private void setBaiduMapMarker(LatLng point) {
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_location);
		MarkerOptions option = new MarkerOptions().position(point).icon(bitmap).zIndex(0);
		option.animateType(MarkerAnimateType.grow);
		mBaiduMap.addOverlay(option);
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
		for (int i = 0; i < dataList.size(); i++) {
			data = dataList.get(i);
			String lat = data.getLatitude();
			String lon = data.getLongitude();
			if (!(TextUtils.isEmpty(lat) || TextUtils.isEmpty(lon))) {
				LatLng ll = new LatLng(Double.parseDouble(lon),Double.parseDouble(lat));
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
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
				mMarker.setZIndex(Integer.parseInt(data.getId()));
				mHashMap.put(data.getId(), data);
			}
		}

	}

//	private void setMarkerClick() {
//		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
//
//			@Override
//			public boolean onMarkerClick(final Marker marker) {
//				data = mHashMap.get(String.valueOf(marker.getZIndex()));
//				if (data == null) {
//					return false;
//				}
//				Intent intent = new Intent(mActivity,TaskPackageListActivity.class);
//				Bundle bundle = new Bundle();
//				bundle.putString("name", data.getName());
//				intent.putExtras(bundle);
//				startActivity(intent);
//				return true;
//			}
//		});
//	}

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
