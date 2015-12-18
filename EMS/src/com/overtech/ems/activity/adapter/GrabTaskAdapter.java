package com.overtech.ems.activity.adapter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.util.LangUtils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.overtech.ems.R;
import com.overtech.ems.entity.parttime.TaskPackage;

public class GrabTaskAdapter extends BaseAdapter {

	private List<TaskPackage> list;
	private Context context;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	private double lat;
	private double lng;

	public List<TaskPackage> getData() {
		return list;
	}

	public void setData(List<TaskPackage> data) {
		this.list = data;
	}

	public GrabTaskAdapter(List<TaskPackage> list, Context context) {
		super();
		this.list = list;
		this.context = context;
		getCurrentLocation();
	}

	public GrabTaskAdapter(Context context) {
		super();
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(context,
					R.layout.item_list_parttime_hot, null);
			new ViewHolder(convertView);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		TaskPackage data = list.get(position);
		holder.tv_name.setText(data.getProjectName());
		holder.elevtorNum.setText(data.getElevatorAmounts() + "");
		holder.addressName.setText(data.getMaintenanceAddress());

		Log.e("当前经纬度", lat + "---------" + lng);
		LatLng latlng1 = new LatLng(Double.parseDouble(data.getLatitude()),
				Double.parseDouble(data.getLongitude()));
		Log.e("后台的经纬度", Double.parseDouble(data.getLatitude())
				+ "-------------" + Double.parseDouble(data.getLongitude()));
		LatLng latlng2 = new LatLng(lat, lng);
		
		NumberFormat numberFormat=NumberFormat.getNumberInstance();//保留两位小数
		numberFormat.setMaximumFractionDigits(2);
		holder.distance.setText(numberFormat.format(DistanceUtil.getDistance(latlng1, latlng2)/1000.0)
				+ "千米");// 当前还没有获取手机的实时经纬度，暂用经度表示
		holder.date.setText(format.format(new Date(data.getMaintenanceDate())));// 时间尚未刷新
		if (data.getIsFinish() == 0) {
			holder.iv_icon.setImageResource(R.drawable.icon_task_none);
		} else if (data.getIsFinish() == 1) {
			holder.iv_icon.setImageResource(R.drawable.icon_task_done);
		}
		if (data.getTopState() == 1) {
			holder.hot.setVisibility(View.VISIBLE);
		} else {
			holder.hot.setVisibility(View.GONE);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView elevtorNum;
		TextView addressName;
		TextView distance;
		TextView date;
		ImageView hot;

		public ViewHolder(View view) {
			iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
			tv_name = (TextView) view.findViewById(R.id.tv_name);
			elevtorNum = (TextView) view.findViewById(R.id.textView2);
			addressName = (TextView) view.findViewById(R.id.textView1);
			distance = (TextView) view.findViewById(R.id.textView3);
			date = (TextView) view.findViewById(R.id.textView4);
			hot = (ImageView) view.findViewById(R.id.imageView1);
			view.setTag(this);
		}
	}

	public void getCurrentLocation() {

		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null) {
				lat = location.getLatitude();
				lng = location.getLongitude();
			}
		} else {
			
			locationManager
					.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							1000, 0, locationListener);
			Location location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null) {
				lat = location.getLatitude(); // 经度
				lng = location.getLongitude(); // 纬度
			}
		}
	}
	LocationListener locationListener = new LocationListener() {

		// Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
		@Override
		public void onStatusChanged(String provider, int status,
				Bundle extras) {

		}

		// Provider被enable时触发此函数，比如GPS被打开
		@Override
		public void onProviderEnabled(String provider) {

		}

		// Provider被disable时触发此函数，比如GPS被关闭
		@Override
		public void onProviderDisabled(String provider) {

		}

		// 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				Log.e("Map",
						"Location changed : Lat: "
								+ location.getLatitude() + " Lng: "
								+ location.getLongitude());
			}
		}
	};

}
