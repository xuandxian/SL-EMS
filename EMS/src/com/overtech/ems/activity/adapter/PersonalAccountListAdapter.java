package com.overtech.ems.activity.adapter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.entity.parttime.Bill;

public class PersonalAccountListAdapter extends BaseAdapter {
	
	private Context context;
	private List<Bill> list ;
	
	public PersonalAccountListAdapter(Context context, List<Bill> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Bill data=list.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_personal_account_list, null);
			holder.mVillageName = (TextView) convertView
					.findViewById(R.id.tv_personal_village_name);
			holder.mMaintenanceTime = (TextView) convertView
					.findViewById(R.id.tv_personal_maintenance_time);
			holder.mPersonalAccount = (TextView) convertView
					.findViewById(R.id.tv_personal_account);
			holder.mMaintenanceEndTime = (TextView) convertView
					.findViewById(R.id.tv_personal_maintenance_endtime);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mVillageName.setText(data.getTaskPackageName());
		
		
		holder.mMaintenanceTime.setText(data.getMaintenanceDate());
		holder.mPersonalAccount.setText(data.getTotalPrice());
		
		return convertView;
	}

	class ViewHolder {
		public TextView mVillageName;
		public TextView mMaintenanceTime;
		public TextView mPersonalAccount;
		public TextView mMaintenanceEndTime;
	}
}
