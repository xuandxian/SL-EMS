package com.overtech.ems.activity.adapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.overtech.ems.R;

public class PersonalChargebackAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> list;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	public PersonalChargebackAdapter(Context context,
			List<Map<String, Object>> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = View.inflate(context,
					R.layout.item_list_personal_chargeback, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		Map<String, Object> data = list.get(position);
		vh.mTaskNo.setText(data.get("taskNo").toString());
		vh.mTaskPackageTime.setText(data.get("taskPackageName").toString());
		vh.mChargebackTime.setText("退单时间:" + data.get("chargebackTime"));
		vh.mGrabTime.setText("抢单时间:" + data.get("grabTime"));

		return convertView;
	}

	public class ViewHolder {
		TextView mTaskNo;
		TextView mGrabTime;
		TextView mTaskPackageTime;
		TextView mChargebackTime;

		public ViewHolder(View view) {
			mTaskNo = (TextView) view.findViewById(R.id.tv_task_no);
			mGrabTime = (TextView) view.findViewById(R.id.tv_grab_time);
			mTaskPackageTime = (TextView) view
					.findViewById(R.id.tv_taskpackage_name);
			mChargebackTime = (TextView) view
					.findViewById(R.id.tv_chargeback_time);
		}
	}
}
