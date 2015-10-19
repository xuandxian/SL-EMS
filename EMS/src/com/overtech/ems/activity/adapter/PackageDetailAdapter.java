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

public class PackageDetailAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Data2> list = new ArrayList<Data2>();

	public PackageDetailAdapter(Context context, ArrayList<Data2> list) {
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
		return list.get(position);
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
					R.layout.item_grab_task_package_detail, null);
			holder.mElevtorName = (TextView) convertView
					.findViewById(R.id.tv_grab_task_package_name);
			holder.mElevtorProductor = (TextView) convertView
					.findViewById(R.id.tv_grab_task_package_productor);
			holder.mElevtorNo = (TextView) convertView
					.findViewById(R.id.tv_grab_task_package_eveltor_no);
			holder.mElevtorType = (TextView) convertView
					.findViewById(R.id.tv_grab_task_package_type);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mElevtorName.setText(data.getElevtorName());
		holder.mElevtorProductor.setText(data.getElevtorProductor());
		holder.mElevtorNo.setText(data.getElevtorNo());
		holder.mElevtorType.setText(data.getElevtorType());
		return convertView;
	}

	class ViewHolder {
		public TextView mElevtorName;
		public TextView mElevtorProductor;
		public TextView mElevtorNo;
		public TextView mElevtorType;
	}

}
