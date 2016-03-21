package com.overtech.ems.activity.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.entity.parttime.Bonus;

public class PersonalBonusListAdapter extends BaseAdapter {
	private List<Bonus> datas;
	private Context context;

	public PersonalBonusListAdapter(List<Bonus> list, Context context) {
		this.datas = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return datas.size()==0 ? 0:datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Bonus bonus = datas.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_expandablelistview_bonus, null);
		}
		TextView mAwardSum = (TextView) convertView.findViewById(R.id.tv_bonus_money);
		TextView mAwardDate = (TextView) convertView
				.findViewById(R.id.tv_bonus_date);
		TextView mAwardRemark = (TextView) convertView
				.findViewById(R.id.tv_bonus_remark);

		mAwardSum.setText("￥" + bonus.getAwardSum());
		mAwardDate.setText("奖励日期：" + bonus.getAwardDate());
		mAwardRemark.setText("奖励原因：" + bonus.getAwardRemark());
		return convertView;
	}

}
/*
 * public class PersonalBonusListAdapter extends BaseExpandableListAdapter {
 * private List<Bonus> group; private Context context;
 * 
 * public PersonalBonusListAdapter(List<Bonus> list,Context context){
 * this.group=list; this.context=context; }
 * 
 * @Override public int getGroupCount() { return group.size(); }
 * 
 * @Override public int getChildrenCount(int groupPosition) { return 1; }
 * 
 * @Override public Object getGroup(int groupPosition) {
 * 
 * return group.get(groupPosition).getAwardRemark(); }
 * 
 * @Override public Object getChild(int groupPosition, int childPosition) {
 * return group.get(groupPosition).getAwardRemark(); }
 * 
 * @Override public long getGroupId(int groupPosition) {
 * 
 * return groupPosition; }
 * 
 * @Override public long getChildId(int groupPosition, int childPosition) {
 * return 0; }
 * 
 * @Override public boolean hasStableIds() { return true; }
 * 
 * @Override public View getGroupView(int groupPosition, boolean isExpanded,
 * View convertView, ViewGroup parent) { Bonus data=group.get(groupPosition);
 * if(convertView==null){
 * convertView=LayoutInflater.from(context).inflate(R.layout
 * .item_expandablelistview_bonus, null); } TextView mAwardSum=(TextView)
 * convertView.findViewById(R.id.tv_bonus); TextView mAwardDate=(TextView)
 * convertView.findViewById(R.id.tv_bonus_date);
 * mAwardSum.setText("奖励金额:"+data.getAwardSum());
 * mAwardDate.setText("奖励日期："+data.getAwardDate()); return convertView; }
 * 
 * @Override public View getChildView(int groupPosition, int childPosition,
 * boolean isLastChild, View convertView, ViewGroup parent) { TextView tv=new
 * TextView(context); tv.setPadding(40, 10, 0, 10);
 * tv.setTextColor(context.getResources().getColor(R.color.main_secondary));
 * tv.setText("奖励原因："+group.get(groupPosition).getAwardRemark()); return tv; }
 * 
 * @Override public boolean isChildSelectable(int groupPosition, int
 * childPosition) { return true; } }
 */
