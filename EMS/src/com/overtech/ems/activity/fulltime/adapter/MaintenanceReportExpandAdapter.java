package com.overtech.ems.activity.fulltime.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.overtech.ems.R;

/**
 * 维保单数据适配器
 * 
 * @author Overtech Will
 * 
 */
public class MaintenanceReportExpandAdapter extends BaseExpandableListAdapter {
	private Context ctx;
	private List<Map<String, Object>> datas;

	public MaintenanceReportExpandAdapter(Context ctx,
			List<Map<String, Object>> datas) {
		this.ctx = ctx;
		this.datas = datas;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> children = (List<Map<String, Object>>) datas
				.get(groupPosition).get("children");
		return children.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(ctx).inflate(
					R.layout.item_expand_maintenance_child, parent, false);
		}
		TextView tvChildName = (TextView) convertView
				.findViewById(R.id.tv_child_view);
		TextView tvChildCode = (TextView) convertView
				.findViewById(R.id.tv_child_code);
		List<Map<String, Object>> children = (List<Map<String, Object>>) datas
				.get(groupPosition).get("children");
		tvChildName.setText(children.get(childPosition).get("name").toString());
		tvChildCode.setText(children.get(childPosition).get("code").toString());
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> children = (List<Map<String, Object>>) datas
				.get(groupPosition).get("children");
		if (children.get(0).get("children") == null) {
			return 0;
		} else if (((List<Map<String, Object>>) children.get(0).get("children"))
				.size() == 0) {
			return 0;
		} else {
			return children.size();
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return datas.get(groupPosition).get("name");
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return datas == null ? 0 : datas.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
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
		Map<String, Object> data = datas.get(groupPosition);
		tvGroup.setText(data.get("name").toString());
		if (isExpanded) {
			tvGroup.setTextColor(ctx.getResources().getColor(
					R.color.colorPrimary));
		} else {
			tvGroup.setTextColor(ctx.getResources().getColor(
					R.color.material_blue_grey_800));
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * 是否有第三级数据
	 * 
	 * @param groupPosition
	 * @return
	 */
	public boolean isHaveThreeLevel(int groupPosition) {
		if (datas == null) {
			return false;
		}
		List<Map<String, Object>> children = (List<Map<String, Object>>) datas
				.get(groupPosition).get("children");
		if (children.get(0).get("children") == null) {
			return false;
		} else if (((List<Map<String, Object>>) children.get(0).get("children"))
				.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public List<Map<String, Object>> getData() {
		return datas;
	}

	public Map<String, Object> getParent(int groupPosition) {
		if (datas == null) {
			return null;
		}
		return datas.get(groupPosition);
	}

	public Map<String, Object> getChildren(int groupPosition, int childPosition) {
		if (datas == null) {
			return null;
		}
		List<Map<String, Object>> children = (List<Map<String, Object>>) datas
				.get(groupPosition).get("children");
		return children.get(childPosition);
	}

}
