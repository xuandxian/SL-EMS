package com.overtech.ems.activity.adapter;

import java.util.ArrayList;
import com.overtech.ems.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchHistoryAdapter extends BaseAdapter {

	private ArrayList<String> list;
	private Context context;

	public SearchHistoryAdapter(Context context, ArrayList<String> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size()==0 ? 0:list.size();
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
			convertView = View.inflate(context,R.layout.item_list_keyword_search, null);
			new ViewHolder(convertView);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.result.setText(list.get(position));
		return convertView;
	}
	class ViewHolder {
		TextView result;
		TextView type;

		public ViewHolder(View view) {
			result = (TextView) view.findViewById(R.id.tv_result_name);
			type = (TextView) view.findViewById(R.id.result_type);
			view.setTag(this);
		}
	}

}
