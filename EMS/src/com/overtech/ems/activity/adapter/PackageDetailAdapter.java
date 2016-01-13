package com.overtech.ems.activity.adapter;

import java.util.ArrayList;
import com.overtech.ems.R;
import com.overtech.ems.entity.parttime.TaskPackageDetail;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PackageDetailAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<TaskPackageDetail> list = new ArrayList<TaskPackageDetail>();

	public PackageDetailAdapter(Context context, ArrayList<TaskPackageDetail> list) {
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
		TaskPackageDetail data = list.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_grab_task_package_detail, null);
			holder.mRelativeLayout=(RelativeLayout) convertView
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
		holder.mElevtorName.setText(data.getElevatorName());
		if(data.getWorkType().equals("0")){
			holder.mWorkType.setText("(半月保)");
		}else if(data.getWorkType().equals("1")){
			holder.mWorkType.setText("(季度保)");
		}else if(data.getWorkType().equals("2")){
			holder.mWorkType.setText("(半年保)");
		}else{
			holder.mWorkType.setText("(年保)");
		}
		holder.mElevtorProductor.setText(data.getElevatorBrand());
		holder.mElevtorNo.setText(data.getElevatorNo());
		holder.mElevtorType.setText(data.getElevatorFloor());
		if(data.getIsFinish().equalsIgnoreCase("1")){
			holder.mRelativeLayout.setBackgroundResource(R.color.package_detail);
		}else{
			holder.mRelativeLayout.setBackgroundDrawable(null);
		}
		return convertView;
	}
	/**
	 * 对电梯的完成状态进行遍历，如果全部完成则返回true，否则返回false；
	 * @return
	 */
	public boolean isAllCompleted(){
		for(int i=0;i<getCount();i++){
			if("0".equals(list.get(i).getIsFinish())){
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
	}
}
