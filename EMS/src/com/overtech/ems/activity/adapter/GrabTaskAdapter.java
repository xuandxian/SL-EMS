package com.overtech.ems.activity.adapter;

import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.overtech.ems.R;
import com.overtech.ems.entity.test.Data5;

public class GrabTaskAdapter extends BaseAdapter {

	private List<Data5> list;
	private Context context;

	public List<Data5> getData() {
		return list;
	}

	public void setData(List<Data5> data) {
		this.list = data;
	}

	public GrabTaskAdapter(List<Data5> list, Context context) {
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
		Data5 data=list.get(position);
		holder.tv_name.setText(data.getName());
		holder.elevtorNum.setText(data.getElevtorNum());
		holder.addressName.setText(data.getAddress());
		holder.distance.setText(data.getDistance());
		holder.date.setText(data.getDate());
		if(data.getImageStatusUrl().equals("0")){
			holder.iv_icon.setImageResource(R.drawable.icon_task_none);
		}else if(data.getImageStatusUrl().equals("1")){
			holder.iv_icon.setImageResource(R.drawable.icon_task_done);
		}
		if(data.getHot().equals("1")){
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
