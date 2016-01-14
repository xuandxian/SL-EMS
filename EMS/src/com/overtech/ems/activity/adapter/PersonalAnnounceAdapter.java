package com.overtech.ems.activity.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.entity.parttime.Announcement;

public class PersonalAnnounceAdapter extends BaseAdapter {
	
	private Context ctx;
	private List<Announcement> list;
	public  PersonalAnnounceAdapter(Context context,List<Announcement> list){
		this.ctx=context;
		this.list=list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Announcement data=list.get(position);
		ViewHolder vh=null;
		if(convertView==null){
			convertView=LayoutInflater.from(ctx).inflate(R.layout.item_list_personal_announcement, null);
			vh=new ViewHolder(convertView);
			convertView.setTag(vh);
		}else{
			vh=(ViewHolder) convertView.getTag();
		}
		vh.tvAnnouncementSummary.setText(data.getSummary());
		vh.tvAnnouncementTitle.setText(data.getTheme());
		vh.tvAnnouncementTime.setText(data.getReleaseDate());
		
		return convertView;
	}
	private class ViewHolder{
		TextView tvAnnouncementTitle;
		TextView tvAnnouncementSummary;
		TextView tvAnnouncementTime;
		public ViewHolder(View view){
			tvAnnouncementTitle=(TextView) view.findViewById(R.id.tv_announcement_title);
			tvAnnouncementSummary=(TextView) view.findViewById(R.id.tv_announcement_abstract);
			tvAnnouncementTime=(TextView) view.findViewById(R.id.tv_announcement_time);
		}
	}
}
