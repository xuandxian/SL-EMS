package com.overtech.ems.activity.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.entity.test.Data6;
/**
 * 公告栏的数据适配器
 * */
public class PersonalAnnouncementAdapter extends BaseAdapter {

	private List<Data6> list;
	private Context context;
	
	public PersonalAnnouncementAdapter(List<Data6> list, Context context) {
		super();
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Data6 data=list.get(position);
		if(convertView==null){
			convertView=LayoutInflater.from(context)
					.inflate(R.layout.item_list_personal_announcement, null);
		}
		TextView textView=(TextView) convertView.findViewById(R.id.tv_announcement_item);
		textView.setText(data.getMessage());
		return convertView;
	}

}
