package com.overtech.ems.activity.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.overtech.ems.R;

public class PersonalAnnounceAdapter extends BaseAdapter {

	private Context ctx;
	private List<Map<String, Object>> list;
	private HashSet<String> announceItemPosition;
	private final String ANNOUNCEITEMPOSITION = "announce_item_position";

	public PersonalAnnounceAdapter(Context context,
			List<Map<String, Object>> list, HashSet<String> set) {
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
		Map<String, Object> data = list.get(position);
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
					R.color.colorPrimary30));
			vh.tvAnnouncementTitle.setTextColor(ctx.getResources().getColor(
					R.color.colorPrimary30));
			vh.tvAnnouncementTime.setTextColor(ctx.getResources().getColor(
					R.color.colorPrimary30));
		} else {
			vh.tvAnnouncementSummary.setTextColor(ctx.getResources().getColor(
					R.color.colorPrimary));
			vh.tvAnnouncementTitle.setTextColor(ctx.getResources().getColor(
					R.color.colorPrimary));
			vh.tvAnnouncementTime.setTextColor(ctx.getResources().getColor(
					R.color.colorPrimary));
		}
		vh.tvAnnouncementSummary.setText(data.get("summary").toString());
		vh.tvAnnouncementTitle.setText(data.get("theme").toString());
		vh.tvAnnouncementTime.setText(data.get("releaseDate").toString());

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
