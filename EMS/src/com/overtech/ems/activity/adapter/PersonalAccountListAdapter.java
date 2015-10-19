package com.overtech.ems.activity.adapter;

import java.util.ArrayList;
import com.overtech.ems.R;
import com.overtech.ems.entity.Data2;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PersonalAccountListAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<Data2> list = new ArrayList<Data2>();
	
	public PersonalAccountListAdapter(Context context, ArrayList<Data2> list) {
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
		Data2 data=list.get(position);
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
		holder.mVillageName.setText(data.getElevtorName());
		holder.mMaintenanceTime.setText(data.getElevtorProductor());
		holder.mPersonalAccount.setText(data.getElevtorNo());
		holder.mMaintenanceEndTime.setText(data.getElevtorType());
		return convertView;
	}

	class ViewHolder {
		public TextView mVillageName;
		public TextView mMaintenanceTime;
		public TextView mPersonalAccount;
		public TextView mMaintenanceEndTime;
	}
}
