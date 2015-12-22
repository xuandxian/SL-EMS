package com.overtech.ems.activity.adapter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.overtech.ems.R;
import com.overtech.ems.entity.parttime.TaskPackage;
import com.overtech.ems.utils.Utilities;

public class GrabTaskAdapter extends BaseAdapter {

	private List<TaskPackage> list;
	private Context context;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	private LatLng mLocation;

	public List<TaskPackage> getData() {
		return list;
	}

	public void setData(List<TaskPackage> data) {
		this.list = data;
	}
	
	public GrabTaskAdapter(List<TaskPackage> list,LatLng myLocation, Context context) {
		super();
		this.list = list;
		this.context = context;
		this.mLocation=myLocation;
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
			convertView = View.inflate(context,R.layout.item_list_parttime_hot, null);
			new ViewHolder(convertView);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		TaskPackage data = list.get(position);
		holder.tv_name.setText(data.getProjectName());
		holder.elevtorNum.setText(data.getElevatorAmounts() + "");
		holder.addressName.setText(data.getMaintenanceAddress());
		LatLng latlng = new LatLng(Double.valueOf(data.getLatitude()),Double.valueOf(data.getLongitude()));
		NumberFormat numberFormat=NumberFormat.getNumberInstance();//保留两位小数
		numberFormat.setMaximumFractionDigits(2);
		holder.distance.setText(numberFormat.format(DistanceUtil.getDistance(mLocation, latlng)/1000.0)+ "km");
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
}
