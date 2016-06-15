package com.overtech.ems.activity.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.entity.bean.TaskPackageDetailBean.TaskPackage;

public class PackageDetailAdapter extends BaseAdapter {

	private Context context;
	private List<TaskPackage> list;

	public PackageDetailAdapter(Context context, List<TaskPackage> list) {
		super();
		this.context = context;
		this.list = list;
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
	public void setData(List<TaskPackage> list){
		this.list=list;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		TaskPackage data = list.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_grab_task_package_detail, parent, false);
			holder.mRelativeLayout = (RelativeLayout) convertView
					.findViewById(R.id.rl_item_package_detail);
			holder.mElevtorName = (TextView) convertView
					.findViewById(R.id.tv_grab_task_package_name);
			holder.mWorkType = (TextView) convertView
					.findViewById(R.id.tv_grab_task_package_work_type);
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
		String temp = data.elevatorName;
		// if (temp.contains("号")) {
		// if (temp.endsWith("号")) {
		// holder.mElevtorName.setText(temp);
		// } else {
		// holder.mElevtorName.setText(temp.split("号")[1]);
		// }
		// } else {
		holder.mElevtorName.setText(temp);
		// }
		if (data.workType.equals("0")) {
			holder.mWorkType.setText("(半月保)");
		} else if (data.workType.equals("1")) {
			holder.mWorkType.setText("(季度保)");
		} else if (data.workType.equals("2")) {
			holder.mWorkType.setText("(半年保)");
		} else {
			holder.mWorkType.setText("(年保)");
		}
		holder.mElevtorProductor.setText(data.elevatorBrand);
		holder.mElevtorNo.setText(data.elevatorNo);
		String contentFloor = data.storeySite;
		if (contentFloor.contains("/")) {
			String[] floor = data.storeySite.split("/");
			holder.mElevtorType.setText(floor[0] + "层/" + floor[1] + "站");
		} else {
			holder.mElevtorType.setText(contentFloor);
		}
		return convertView;
	}

	class ViewHolder {
		public RelativeLayout mRelativeLayout;
		public TextView mElevtorName;
		public TextView mWorkType;
		public TextView mElevtorProductor;
		public TextView mElevtorNo;
		public TextView mElevtorType;
	}
}
