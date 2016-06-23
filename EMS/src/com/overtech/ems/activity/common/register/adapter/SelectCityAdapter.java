package com.overtech.ems.activity.common.register.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.overtech.ems.R;

public class SelectCityAdapter extends BaseExpandableListAdapter {
	private Context ctx;
	private Map<String, Object> body;
	private List<Map<String, Object>> data;

	public SelectCityAdapter(Context ctx, Map<String, Object> body) {
		this.ctx = ctx;
		this.body = body;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		if (body != null) {
			data = (List<Map<String, Object>>) body.get("data");
			if (data != null) {
				return data.size();
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		if (body != null) {
			Map<String, Object> map = data.get(groupPosition);
			if (map != null) {
				List<Map<String, Object>> list = (List<Map<String, Object>>) map
						.get("list");
				if (list != null) {
					return list.size();
				} else {
					return 0;
				}
			} else {
				return 0;
			}

		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return data.get(groupPosition).get("parentName");
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub

		return ((List<Map<String, Object>>) data.get(groupPosition).get("list"))
				.get(childPosition).get("name");
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub

		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub

		return 0;
	}

	public String getChildCode(int groupPosition, int childPosition) {
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(
				groupPosition).get("list");
		return list.get(childPosition).get("code").toString();
	}

	public boolean isHasChild(int groupPosition) {
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(
				groupPosition).get("list");
		if (list == null || list.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(ctx).inflate(
					R.layout.item_expand_maintenance_group, parent, false);
		}
		TextView tvGroup = (TextView) convertView.findViewById(R.id.tv_group);
		tvGroup.setText(data.get(groupPosition).get("parentName").toString());
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(ctx).inflate(
					R.layout.item_expand_maintenance_child, parent, false);
		}
		TextView tvChild = (TextView) convertView
				.findViewById(R.id.tv_child_view);
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(
				groupPosition).get("list");
		tvChild.setText(list.get(childPosition).get("name").toString());
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
