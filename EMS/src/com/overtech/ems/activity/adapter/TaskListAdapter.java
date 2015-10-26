package com.overtech.ems.activity.adapter;

import java.util.List;
import com.overtech.ems.R;
import com.overtech.ems.entity.test.Data4;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TaskListAdapter extends BaseAdapter {

	private List<Data4> list;
	private Context context;

	public List<Data4> getData() {
		return list;
	}

	public void setData(List<Data4> data) {
		this.list = data;
	}

	public TaskListAdapter(List<Data4> list, Context context) {
		super();
		this.list = list;
		this.context = context;
	}

	public TaskListAdapter(Context context) {
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
		Data4 data=list.get(position);
		if (convertView == null) {
			convertView = View.inflate(context,
					R.layout.item_list_tasklist, null);
			new ViewHolder(convertView);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.address.setText(data.getAddress());
		holder.elevtorNum.setText(data.getElevtorNum());
		holder.addressName.setText(data.getAddressName());
		holder.distance.setText(data.getDistance());
		holder.date.setText(data.getDate());
		return convertView;
	}

	class ViewHolder {
		TextView address;
		TextView elevtorNum;
		TextView addressName;
		TextView distance;
		TextView date;

		public ViewHolder(View view) {
			address = (TextView) view.findViewById(R.id.tv_name);
			elevtorNum = (TextView) view.findViewById(R.id.tv_num);
			addressName = (TextView) view.findViewById(R.id.tv_address);
			distance = (TextView) view.findViewById(R.id.tv_distance);
			date = (TextView) view.findViewById(R.id.tv_date);
			view.setTag(this);
		}
	}

}
