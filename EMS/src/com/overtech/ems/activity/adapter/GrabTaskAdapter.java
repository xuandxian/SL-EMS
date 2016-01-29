package com.overtech.ems.activity.adapter;

import java.text.NumberFormat;
import java.util.List;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.overtech.ems.R;
import com.overtech.ems.entity.parttime.TaskPackage;

public class GrabTaskAdapter extends BaseAdapter {

	private List<TaskPackage> list;
	private Context context;
	private LatLng mLocation;

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
	}

	public GrabTaskAdapter(List<TaskPackage> list, LatLng myLocation,
			Context context) {
		super();
		this.list = list;
		this.context = context;
		this.mLocation = myLocation;
	}

	@Override
	public int getCount() {
		return list.size() == 0 ? 0 : list.size();
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
		holder.tv_name.setText(data.getTaskPackageName());
		holder.elevtorNum.setText(data.getElevatorAmounts() + "");
		holder.addressName.setText(data.getMaintenanceAddress());
		String latitude=data.getLatitude().trim();
		String longitude=data.getLongitude().trim();
		if (null==latitude || TextUtils.equals("", latitude) ||null==longitude||TextUtils.equals("", longitude) ) {
			holder.distance.setText("距离计算错误");
		}else {
			NumberFormat numberFormat = NumberFormat.getNumberInstance();// 保留两位小数
			numberFormat.setMaximumFractionDigits(2);
			LatLng latlng = new LatLng(Double.valueOf(data.getLatitude()),Double.valueOf(data.getLongitude()));
			holder.distance.setText(numberFormat.format(DistanceUtil.getDistance(mLocation, latlng) / 1000.0) + "km");
		}
		holder.date.setText(data.getMaintenanceDate());
		if (TextUtils.equals(data.getIsFinish(), "0")) {
			holder.iv_icon.setImageResource(R.drawable.icon_task_none);
		} else if (TextUtils.equals(data.getIsFinish(), "1")) {
			holder.iv_icon.setImageResource(R.drawable.icon_task_done);
		}
		if (TextUtils.equals(data.getTopState(), "1")) {
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
