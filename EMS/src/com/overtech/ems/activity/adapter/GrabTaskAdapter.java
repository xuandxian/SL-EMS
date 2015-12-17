package com.overtech.ems.activity.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.entity.parttime.GrabTaskBean.TaskPackage;

public class GrabTaskAdapter extends BaseAdapter {

	private List<TaskPackage> list;
	private Context context;

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

	public GrabTaskAdapter(Context context) {
		super();
		this.context = context;
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
			convertView = View.inflate(context,
					R.layout.item_list_parttime_hot, null);
			new ViewHolder(convertView);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		TaskPackage data=list.get(position);
		holder.tv_name.setText(data.projectName);
		holder.elevtorNum.setText(data.elevatorAmounts+"");
		holder.addressName.setText(data.maintenanceAddress);
		holder.distance.setText(data.latitude);//当前还没有获取手机的实时经纬度，暂用经度表示
		holder.date.setText(data.maintenanceDate);//时间尚未刷新
		if(data.isFinish==0){
			holder.iv_icon.setImageResource(R.drawable.icon_task_none);
		}else if(data.isFinish==1){
			holder.iv_icon.setImageResource(R.drawable.icon_task_done);
		}
		if(data.topState==1){
			holder.hot.setVisibility(View.VISIBLE);
		}else{
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
			elevtorNum=(TextView) view.findViewById(R.id.textView2);
			addressName=(TextView) view.findViewById(R.id.textView1);
			distance=(TextView) view.findViewById(R.id.textView3);
			date=(TextView) view.findViewById(R.id.textView4);
			hot=(ImageView) view.findViewById(R.id.imageView1);
			view.setTag(this);
		}
	}

}
