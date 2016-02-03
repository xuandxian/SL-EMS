package com.overtech.ems.activity.adapter;

import java.text.NumberFormat;
import java.util.List;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.overtech.ems.R;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.entity.parttime.TaskPackage;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TaskListAdapter extends BaseAdapter {

	private List<TaskPackage> list;
	private Context context;
	private NumberFormat numFormat = NumberFormat.getNumberInstance();
	private double latitude;
	private double longitude;
	private LatLng mLatLng;

	public List<TaskPackage> getData() {
		return list;
	}

	public TaskListAdapter(List<TaskPackage> list, Context context) {
		super();
		this.list = list;
		this.context = context;
		latitude = ((MyApplication) context.getApplicationContext()).latitude;
		longitude = ((MyApplication) context.getApplicationContext()).longitude;
		mLatLng = new LatLng(latitude, longitude);
	}

	public TaskListAdapter(Context context) {
		super();
		this.context = context;
	}
	/**
	 * 解决目前的设计缺陷，当数据为空之后，之前的adapter没有被清空，而新加载的数据如果为空的话，之前的数据依然显示在那里，所有手动清空一下
	 */
	public void clearAdapter(){
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
		TaskPackage data = list.get(position);
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_list_tasklist,null);
			new ViewHolder(convertView);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.taskPackageName.setText(data.getTaskPackageName());
		holder.elevatorAmounts.setText(data.getElevatorAmounts());
		holder.maintenanceAddress.setText(data.getMaintenanceAddress());
		holder.maintenanceDate.setText(data.getMaintenanceDate());
		LatLng latlng = new LatLng(Double.parseDouble(data.getLatitude()),Double.parseDouble(data.getLongitude()));
		numFormat.setMaximumFractionDigits(2);
		holder.distance.setText(numFormat.format(DistanceUtil.getDistance(mLatLng, latlng) / 1000.0) + "千米");
		return convertView;
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
		TaskPackage data = list.get(position);
		String desLat = data.getLatitude();
		String desLng = data.getLongitude();
		return new LatLng(Double.parseDouble(desLat),Double.parseDouble(desLng));
	}
	/**
	 *	目的地名称
	 * @param position
	 * @return
	 */
	public String getDesName(int position){
		return list.get(position).getTaskPackageName();
	}

	class ViewHolder {
		TextView taskPackageName;
		TextView elevatorAmounts;
		TextView maintenanceAddress;
		TextView distance;
		TextView maintenanceDate;

		public ViewHolder(View view) {
			taskPackageName = (TextView) view.findViewById(R.id.tv_name);
			elevatorAmounts = (TextView) view.findViewById(R.id.tv_num);
			maintenanceAddress = (TextView) view.findViewById(R.id.tv_address);
			distance = (TextView) view.findViewById(R.id.tv_distance);
			maintenanceDate = (TextView) view.findViewById(R.id.tv_date);
			view.setTag(this);
		}
	}

}
