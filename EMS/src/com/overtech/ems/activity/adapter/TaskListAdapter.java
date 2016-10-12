package com.overtech.ems.activity.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.overtech.ems.R;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.utils.Utilities;

public class TaskListAdapter extends BaseAdapter {

	private List<Map<String, Object>> list;
	private Context context;
	private double latitude;
	private double longitude;
	private LatLng mLatLng;
	/**
	 * 完成状态
	 */
	private int state;

	public List<Map<String, Object>> getData() {
		return list;
	}

	public TaskListAdapter(List<Map<String, Object>> list, Context context,
			int state) {
		super();
		this.list = list;
		this.context = context;
		latitude = ((MyApplication) context.getApplicationContext()).latitude;
		longitude = ((MyApplication) context.getApplicationContext()).longitude;
		mLatLng = new LatLng(latitude, longitude);
		this.state = state;
	}

	public TaskListAdapter(Context context) {
		super();
		this.context = context;
	}

	/**
	 * 解决目前的设计缺陷，当数据为空之后，之前的adapter没有被清空，而新加载的数据如果为空的话，之前的数据依然显示在那里，所有手动清空一下
	 */
	public void clearAdapter() {
		list.clear();
		this.notifyDataSetChanged();
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
		Map<String, Object> data = list.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_list_tasklist, parent, false);
			convertView.setTag(new ViewHolder(convertView));
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if(TextUtils.equals("1", data.get("isAs").toString())){
			holder.taskPackageName.setText(data.get("taskPackageName").toString()+"(年检)");
		}else{
			holder.taskPackageName.setText(data.get("taskPackageName").toString());
		}
		String amounts = data.get("elevatorAmounts").toString();
		holder.elevatorAmounts.setText(amounts.substring(0,
				amounts.indexOf(".")));
		holder.maintenanceAddress.setText(data.get("maintenanceAddress")
				.toString());
		holder.maintenanceDate.setText(data.get("maintenanceDate").toString());
		if (state == StatusCode.TASK_NO) {
			holder.taskNo.setVisibility(View.GONE);
		} else if (state == StatusCode.TASK_DO) {
			holder.taskNo.setVisibility(View.VISIBLE);
			holder.taskNo.setText(data.get("taskNo").toString());
		}
		LatLng latlng = new LatLng(Double.parseDouble(data.get("latitude")
				.toString()), Double.parseDouble(data.get("longitude")
				.toString()));
		holder.distance.setText(Utilities.format2decimal(DistanceUtil
				.getDistance(mLatLng, latlng) / 1000.0) + "km");
		return convertView;
	}

	public void setData(List<Map<String, Object>> data) {
		this.list = data;
	}

	/**
	 * 当前位置
	 * 
	 * @return
	 */
	public LatLng getCurrentLocation() {
		return mLatLng;
	}

	/**
	 * 指定位置
	 * 
	 * @param position
	 * @return
	 */
	public LatLng getDestination(int position) {
		Map<String, Object> data = list.get(position);
		String desLat = data.get("latitude").toString();
		String desLng = data.get("longitude").toString();
		return new LatLng(Double.parseDouble(desLat),
				Double.parseDouble(desLng));
	}
	public double getLatitude(int position){
		Map<String,Object> data=list.get(position);
		return Double.parseDouble(data.get("latitude").toString());
	}
	public double getLongitude(int position){
		Map<String,Object> data=list.get(position);
		return Double.parseDouble(data.get("longitude").toString());
	}

	/**
	 * 目的地名称
	 * 
	 * @param position
	 * @return
	 */
	public String getDesName(int position) {
		return list.get(position).get("taskPackageName").toString();
	}

	class ViewHolder {
		TextView taskPackageName;
		TextView elevatorAmounts;
		TextView maintenanceAddress;
		TextView distance;
		TextView maintenanceDate;
		TextView taskNo;

		public ViewHolder(View view) {
			taskPackageName = (TextView) view.findViewById(R.id.tv_name);
			elevatorAmounts = (TextView) view.findViewById(R.id.tv_num);
			maintenanceAddress = (TextView) view.findViewById(R.id.tv_address);
			distance = (TextView) view.findViewById(R.id.tv_distance);
			maintenanceDate = (TextView) view.findViewById(R.id.tv_date);
			taskNo = (TextView) view.findViewById(R.id.tv_taskno);
			view.setTag(this);
		}
	}

}
