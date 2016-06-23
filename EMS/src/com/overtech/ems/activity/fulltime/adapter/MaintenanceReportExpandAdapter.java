package com.overtech.ems.activity.fulltime.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.entity.fulltime.MaintenanceReportBean.Children;
import com.overtech.ems.entity.fulltime.MaintenanceReportBean.Parent;

/**
 * 维保单数据适配器
 * 
 * @author Overtech Will
 * 
 */
public class MaintenanceReportExpandAdapter extends BaseExpandableListAdapter {
	private Context ctx;
	private List<Parent> datas;

	public MaintenanceReportExpandAdapter(Context ctx, List<Parent> datas) {
		this.ctx = ctx;
		this.datas = datas;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return datas.get(groupPosition).children.get(childPosition).name;
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
		TextView tvChild = (TextView) convertView
				.findViewById(R.id.tv_child_view);
		tvChild.setText(datas.get(groupPosition).children.get(childPosition).name);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		if (datas.get(groupPosition).children.get(0).children == null) {
			return 0;
		} else {
			return datas.get(groupPosition).children.size();
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return datas.get(groupPosition).name;
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
		Parent data=datas.get(groupPosition);
		tvGroup.setText(data.name);
		if (isExpanded) {
			tvGroup.setTextColor(ctx.getResources().getColor(
					R.color.accent_material_light));
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
		if (datas.get(groupPosition).children.get(0).children == null) {
			return false;
		} else if (datas.get(groupPosition).children.get(0).children.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public Parent getParent(int groupPosition) {
		if (datas == null) {
			return null;
		}
		return datas.get(groupPosition);
	}

	public Children getChildren(int groupPosition, int childPosition) {
		if (datas == null) {
			return null;
		}
		return datas.get(groupPosition).children.get(childPosition);
	}

}
