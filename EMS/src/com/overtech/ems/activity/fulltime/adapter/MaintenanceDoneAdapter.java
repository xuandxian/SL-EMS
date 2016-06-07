package com.overtech.ems.activity.fulltime.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.entity.fulltime.MaintenanceBean.Workorder;

public class MaintenanceDoneAdapter extends BaseAdapter {
	private Context ctx;
	private List<Workorder> data;

	public MaintenanceDoneAdapter(Context ctx, List<Workorder> data) {
		this.ctx = ctx;
		this.data = data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data == null ? 0 : data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
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
					R.layout.item_maintenance_done, parent, false);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		Workorder workorder = data.get(position);
		vh.tvFaultType.setText(workorder.faultType);
		vh.tvMaintenanceNoneAddress.setText(workorder.address);
		vh.tvMaintenanceAccomplishTime.setText(workorder.accomplishDate);
		return convertView;
	}

	public void setData(List<Workorder> data) {
		this.data = data;
	}

	public class ViewHolder {
		TextView tvFaultType;
		TextView tvMaintenanceNoneAddress;
		TextView tvMaintenanceNoneDistance;
		TextView tvMaintenanceAccomplishTime;

		public ViewHolder(View convertView) {
			tvFaultType = (TextView) convertView
					.findViewById(R.id.tv_fault_type);
			tvMaintenanceNoneAddress = (TextView) convertView
					.findViewById(R.id.tv_maintenance_none_address);
			tvMaintenanceNoneDistance = (TextView) convertView
					.findViewById(R.id.tv_maintenance_none_distance);
			tvMaintenanceAccomplishTime = (TextView) convertView
					.findViewById(R.id.tv_maintenance_accomplish_time);
		}
	}
}
