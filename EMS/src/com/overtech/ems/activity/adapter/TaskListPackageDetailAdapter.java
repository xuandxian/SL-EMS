package com.overtech.ems.activity.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.overtech.ems.R;

public class TaskListPackageDetailAdapter extends BaseAdapter {

	private Context context;
	private List<Map<String, Object>> list;

	public TaskListPackageDetailAdapter(Context context,
			List<Map<String, Object>> list) {
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
		Map<String, Object> data = list.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_grab_task_package_detail, null);
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
			holder.mElevatorComplete = (ImageView) convertView
					.findViewById(R.id.iv_complete_state);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String temp = data.get("elevatorName").toString();
		// if (temp.contains("号")) {
		// if(temp.split("号").length==2){
		// holder.mElevtorName.setText(temp.split("号")[1]);
		// }else{
		// holder.mElevtorName.setText(temp);
		// }
		// }else {
		holder.mElevtorName.setText(temp);
		// }
		if (data.get("workType").equals("0")) {
			holder.mWorkType.setText("(半月保)");
		} else if (data.get("workType").equals("1")) {
			holder.mWorkType.setText("(季度保)");
		} else if (data.get("workType").equals("2")) {
			holder.mWorkType.setText("(半年保)");
		} else {
			holder.mWorkType.setText("(年保)");
		}
		holder.mElevtorProductor.setText(data.get("elevatorBrand").toString());
		holder.mElevtorNo.setText(data.get("elevatorNo").toString());
		String contentFloor = data.get("storeySite").toString();
		if (contentFloor.contains("/")) {
			String[] floor = data.get("storeySite").toString().split("/");
			holder.mElevtorType.setText(floor[0] + "层/" + floor[1] + "站");
		} else {
			holder.mElevtorType.setText(contentFloor);
		}
		if (data.get("isFinish").equals("2")) {// isFinish
			// 的值等于1表示有一人完成，等于2表示两人都已完成 等于0
			// 是默认值，都没有完成
			// holder.mRelativeLayout.setBackgroundResource(R.color.package_detail);
			holder.mElevatorComplete
					.setImageResource(R.drawable.icon_elevator_complete);
		} else {
			// holder.mRelativeLayout.setBackgroundDrawable(null);
			holder.mElevatorComplete.setImageBitmap(null);
		}
		return convertView;
	}

	/**
	 * 对电梯的完成状态进行遍历，如果全部完成则返回true，否则返回false；
	 * 
	 * @return
	 */
	public boolean isAllCompleted() {
		for (int i = 0; i < getCount(); i++) {
			Map<String, Object> item = list.get(i);
			if ("0".equals(item.get("isFinish"))
					|| "1".equals(item.get("isFinish"))) {
				return false;
			}
		}
		return true;
	}

	class ViewHolder {
		public RelativeLayout mRelativeLayout;
		public TextView mElevtorName;
		public TextView mWorkType;
		public TextView mElevtorProductor;
		public TextView mElevtorNo;
		public TextView mElevtorType;
		public ImageView mElevatorComplete;
	}
}
