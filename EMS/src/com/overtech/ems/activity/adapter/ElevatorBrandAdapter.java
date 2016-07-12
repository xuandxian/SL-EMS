package com.overtech.ems.activity.adapter;

import java.util.HashMap;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.overtech.ems.R;

public class ElevatorBrandAdapter extends BaseAdapter {

	private String[] data;
	private Context context;
	public static HashMap<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>();

	public ElevatorBrandAdapter(String[] data, Context context) {
		this.data = data;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	ViewHolder vh = null;

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_gridview_elevator_brand, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.cbElevator.setText(data[position]);
		if (isSelected.get(position) == null) {
			vh.cbElevator.setChecked(false);
		} else {
			vh.cbElevator.setChecked(isSelected.get(position));
		}
		return convertView;
	}

	class ViewHolder {
		AppCompatCheckBox cbElevator;

		ViewHolder(View view) {
			cbElevator = (AppCompatCheckBox) view
					.findViewById(R.id.cb_elevator);
		}
	}

}
