package com.overtech.ems.activity.adapter;

import java.util.ArrayList;
import com.overtech.ems.R;
import com.overtech.ems.entity.test.Data3;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TaskListDetailsAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<Data3> list = new ArrayList<Data3>();
	public static final int ITEM_TITLE = 0;
	public static final int ITEM_CONTENT = 1;

	public TaskListDetailsAdapter(Context context, ArrayList<Data3> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {

		if (position == 0) {
			return ITEM_TITLE;
		}
		return ITEM_CONTENT;
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
		ViewHolder2 holder2 = null;
		int type = getItemViewType(position);
		Data3 data = list.get(position);
		if (convertView == null) {
			switch (type) {
			case ITEM_TITLE:
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.item_task_details_title, null);
				convertView.setTag(holder);
				break;
			case ITEM_CONTENT:
				holder2 = new ViewHolder2();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.item_task_details_content, null);
				
				holder2.mContentId=(TextView)convertView.findViewById(R.id.title_id);
				holder2.mContentType=(TextView)convertView.findViewById(R.id.title_type);
				holder2.mContentContent=(TextView)convertView.findViewById(R.id.title_content);
				holder2.mContentId.setText(data.getId());
				holder2.mContentType.setText(data.getType());
				holder2.mContentContent.setText(data.getContent());
				convertView.setTag(holder2);
				break;
			}
		} else {
			switch (type) {
			case ITEM_TITLE:
				holder = (ViewHolder) convertView.getTag();
				break;
			case ITEM_CONTENT:
				holder2 = (ViewHolder2) convertView.getTag();
				holder2.mContentId.setText(data.getId());
				holder2.mContentType.setText(data.getType());
				holder2.mContentContent.setText(data.getContent());
				break;
			}
		}
		return convertView;
	}

	static class ViewHolder {
		public TextView mTitleId;
		public TextView mTitleType;
		public TextView mTitleContent;
	}

	static class ViewHolder2 {
		public TextView mContentId;
		public TextView mContentType;
		public TextView mContentContent;
	}

}
