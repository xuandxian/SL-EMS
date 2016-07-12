package com.overtech.ems.activity.fulltime.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.overtech.ems.R;

/**
 * 维修单 二级目录展示适配器
 * 
 * @author Overtech Will
 * 
 */
public class MaintenanceChildAdapter extends BaseAdapter {
	private Context ctx;
	private List<Map<String, Object>> datas;

	public MaintenanceChildAdapter(Context ctx, List<Map<String, Object>> datas) {
		this.ctx = ctx;
		this.datas = datas;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas == null ? 0 : datas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ChildViewHolder vh = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(ctx).inflate(
					R.layout.item_listview_maintenance_detail, parent, false);
			vh = new ChildViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ChildViewHolder) convertView.getTag();
		}
		vh.tvName.setText(datas.get(position).get("name").toString());
		vh.tvCode.setText(datas.get(position).get("code").toString());
		if (datas.get(position).get("checked").equals("1")) {
			vh.cbStatus.setChecked(true);
		} else {
			vh.cbStatus.setChecked(false);
		}
		return convertView;
	}

	public void setData(List<Map<String, Object>> newDatas) {
		this.datas = newDatas;
	}

	public class ChildViewHolder {
		public TextView tvName;
		public TextView tvCode;
		public CheckBox cbStatus;

		public ChildViewHolder(View convertView) {
			tvName = (TextView) convertView
					.findViewById(R.id.tv_maintenance_item_name);
			tvCode = (TextView) convertView
					.findViewById(R.id.tv_maintenance_item_code);
			cbStatus = (CheckBox) convertView
					.findViewById(R.id.cb_maintenance_detail);
		}
	}
}
