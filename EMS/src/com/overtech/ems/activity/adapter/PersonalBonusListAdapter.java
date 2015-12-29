package com.overtech.ems.activity.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.entity.parttime.Bill;
import com.overtech.ems.entity.parttime.Bonus;

public class PersonalBonusListAdapter extends BaseExpandableListAdapter {
	private List<Bonus> group;
	private Context context;
	
	public PersonalBonusListAdapter(List<Bonus> list,Context context){
		this.group=list;
		this.context=context;
	}
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return group.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		
		return group.get(groupPosition).getAwardRemark();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return group.get(groupPosition).getAwardRemark();
	}

	@Override
	public long getGroupId(int groupPosition) {
		
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		Bonus data=group.get(groupPosition);
		if(convertView==null){
			convertView=LayoutInflater.from(context).inflate(R.layout.item_expandablelistview_bonus, null);
		}
		TextView mAwardSum=(TextView) convertView.findViewById(R.id.tv_bonus);
		TextView mAwardDate=(TextView) convertView.findViewById(R.id.tv_bonus_date);
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date awardDate=new Date(data.getAwardTime());
		
		mAwardSum.setText("奖励金额:"+data.getAwardSum());
		mAwardDate.setText(sdf.format(awardDate));
		
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		TextView tv=new TextView(context);
		tv.setPadding(40, 10, 0, 10);
		tv.setTextColor(context.getResources().getColor(R.color.main_secondary));
		tv.setText("奖励原因："+group.get(groupPosition).getAwardRemark());
		return tv;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	
}
