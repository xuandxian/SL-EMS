package com.overtech.ems.activity.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.entity.parttime.Announcement;

public class PersonalAnnounceAdapter extends BaseAdapter {

	private Context ctx;
	private List<Announcement> list;
	private HashSet<String> announceItemPosition;
	private final String ANNOUNCEITEMPOSITION="announce_item_position";

	public PersonalAnnounceAdapter(Context context, List<Announcement> list,HashSet<String> set) {
		this.ctx = context;
		this.list = list;
		announceItemPosition = set;
	}

	/**
	 * 当listview条目点击改变时，将刷新后的set集合让adapter使用
	 * 
	 * @param set
	 */
	public void setHashSet(HashSet<String> set) {
		announceItemPosition = set;
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
		Announcement data = list.get(position);
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(ctx).inflate(
					R.layout.item_list_personal_announcement, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		
		if (announceItemPosition.contains(String.valueOf(position))) {
			vh.tvAnnouncementSummary.setTextColor(ctx.getResources().getColor(
					R.color.announce_after));
			vh.tvAnnouncementTitle.setTextColor(ctx.getResources().getColor(
					R.color.announce_after));
			vh.tvAnnouncementTime.setTextColor(ctx.getResources().getColor(
					R.color.announce_after));
		} else {
			vh.tvAnnouncementSummary.setTextColor(ctx.getResources().getColor(
					R.color.announce_before));
			vh.tvAnnouncementTitle.setTextColor(ctx.getResources().getColor(
					R.color.announce_before));
			vh.tvAnnouncementTime.setTextColor(ctx.getResources().getColor(
					R.color.announce_before));
		}
		vh.tvAnnouncementSummary.setText(data.getSummary());
		vh.tvAnnouncementTitle.setText(data.getTheme());
		vh.tvAnnouncementTime.setText(data.getReleaseDate());

		return convertView;
	}

	private class ViewHolder {
		TextView tvAnnouncementTitle;
		TextView tvAnnouncementSummary;
		TextView tvAnnouncementTime;

		public ViewHolder(View view) {
			tvAnnouncementTitle = (TextView) view
					.findViewById(R.id.tv_announcement_title);
			tvAnnouncementSummary = (TextView) view
					.findViewById(R.id.tv_announcement_abstract);
			tvAnnouncementTime = (TextView) view
					.findViewById(R.id.tv_announcement_time);
		}
	}
}
