package com.overtech.ems.test;

import java.util.ArrayList;
import com.overtech.ems.R;
import com.overtech.ems.entity.test.Data7;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TestAdapter extends BaseAdapter {
	
	private ArrayList<Data7> list;
	private Context context;

	public TestAdapter(ArrayList<Data7> list, Context context) {
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
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Data7 data=list.get(position);
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_test,null);
			new ViewHolder(convertView);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.content.setText(data.getContent());
		return convertView;
		
	}
	class ViewHolder {
		TextView content;

		public ViewHolder(View view) {
			content = (TextView) view.findViewById(R.id.test_content);
			view.setTag(this);
		}
	}

}
