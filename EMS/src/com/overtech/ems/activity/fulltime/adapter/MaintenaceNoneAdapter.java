package com.overtech.ems.activity.fulltime.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.overtech.ems.R;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.utils.Utilities;

public class MaintenaceNoneAdapter extends BaseAdapter {
	private Context ctx;
	private List<Map<String,Object>> data;
	private double latitude;
	private double longitude;
	private LatLng curLatLng;

	public MaintenaceNoneAdapter(Context ctx, List<Map<String,Object>> data) {
		this.ctx = ctx;
		this.data = data;
		latitude = ((MyApplication) ctx.getApplicationContext()).latitude;
		longitude = ((MyApplication) ctx.getApplicationContext()).longitude;
		curLatLng = new LatLng(latitude, longitude);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data == null ? 0 : data.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position).get("workorderCode").toString();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = ((MainActivity) ctx).getLayoutInflater().inflate(
					R.layout.item_maintenance_none, parent, false);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		Map<String,Object> workorder = data.get(position);
		vh.tvFaultType.setText(workorder.get("faultType").toString());
		vh.tvMaintenanceNoneAddress.setText(workorder.get("address").toString());
		if (latitude == 0 || longitude == 0) {
			vh.tvMaintenanceNoneDistance.setText("??");
		} else {
			double distance = DistanceUtil.getDistance(
					curLatLng,
					new LatLng(Double.parseDouble(workorder.get("latitude").toString()), Double
							.parseDouble(workorder.get("longitude").toString())));
			vh.tvMaintenanceNoneDistance.setText(Utilities.format2decimal(distance / 1000.0) + "km");
		}
		vh.tvMaintenancePublishTime.setText(workorder.get("publishDatetime").toString());
		return convertView;
	}

	public void setData(List<Map<String,Object>> data) {
		this.data = data;
	}

	public class ViewHolder {
		TextView tvFaultType;
		TextView tvMaintenanceNoneAddress;
		TextView tvMaintenanceNoneDistance;
		TextView tvMaintenancePublishTime;

		public ViewHolder(View convertView) {
			tvFaultType = (TextView) convertView
					.findViewById(R.id.tv_fault_type);
			tvMaintenanceNoneAddress = (TextView) convertView
					.findViewById(R.id.tv_maintenance_none_address);
			tvMaintenanceNoneDistance = (TextView) convertView
					.findViewById(R.id.tv_maintenance_none_distance);
			tvMaintenancePublishTime = (TextView) convertView
					.findViewById(R.id.tv_maintenance_publish_time);
		}
	}
}
