package com.overtech.ems.activity.fulltime.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.overtech.ems.R;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceComponentAdapter.NormalViewHolder;

public class MaintenanceComponentAdapter extends Adapter<NormalViewHolder> {
	private Context ctx;
	private List<Map<String, Object>> datas;

	public MaintenanceComponentAdapter(Context ctx,
			List<Map<String, Object>> datas) {
		// TODO Auto-generated constructor stub
		this.datas = datas;
		this.ctx = ctx;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return datas == null ? 0 : datas.size();
	}

	@Override
	public void onBindViewHolder(NormalViewHolder holder, int position) {
		// TODO Auto-generated method stub
		Map<String, Object> data = datas.get(position);
		holder.tvName.setText(data.get("name").toString());
		holder.tvCount.setText(data.get("count").toString());
	}

	@Override
	public NormalViewHolder onCreateViewHolder(ViewGroup parent, int type) {
		// TODO Auto-generated method stub
		NormalViewHolder holder = new NormalViewHolder(LayoutInflater.from(ctx)
				.inflate(R.layout.item_recyclerview_maintenance_component,
						parent, false));
		return holder;
	}

	class NormalViewHolder extends ViewHolder {
		private AppCompatTextView tvName;
		private AppCompatTextView tvCount;

		public NormalViewHolder(View v) {
			super(v);
			// TODO Auto-generated constructor stub
			tvName = (AppCompatTextView) v.findViewById(R.id.tv_name);
			tvCount = (AppCompatTextView) v.findViewById(R.id.tv_count);
		}

	}

	public void setData(List<Map<String, Object>> datas2) {
		// TODO Auto-generated method stub
		this.datas=datas2;
		notifyDataSetChanged();
	}
}
