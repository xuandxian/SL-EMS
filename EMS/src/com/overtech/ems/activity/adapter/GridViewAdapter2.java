package com.overtech.ems.activity.adapter;

import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.overtech.ems.R;

public class GridViewAdapter2 extends BaseAdapter {
	
	private String[] data;
	private Context context;
	private HashMap<Integer,Boolean> isSelected;
	public GridViewAdapter2(String[] data,Context context) {
		this.data=data;
		this.context=context;
		isSelected=new HashMap<Integer, Boolean>();
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

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView=LayoutInflater.from(context).inflate(R.layout.item_gridview_elevator_brand, null);
		}
		CheckBox box=(CheckBox) convertView.findViewById(R.id.cb_elevator);
		box.setText(data[position]);
		if(box.isChecked()){
			isSelected.put(position, true);
		}else{
			if(isSelected.containsKey(position)){
				isSelected.remove(position);
			}
		}
		box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					isSelected.put(position, isChecked);
				}else{
					isSelected.remove(position);
				}
			}
		});
		return convertView;
	}
	public HashMap getCheckBox(){
		return isSelected;
	}

}
