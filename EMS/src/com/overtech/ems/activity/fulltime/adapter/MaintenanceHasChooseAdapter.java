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
import com.overtech.ems.activity.fulltime.adapter.MaintenanceHasChooseAdapter.MyViewHolder;

public class MaintenanceHasChooseAdapter extends Adapter<MyViewHolder> {
	private Context ctx;
	private List<Map<String, Object>> datas;

	public MaintenanceHasChooseAdapter(Context ctx,
			List<Map<String, Object>> datas) {
		this.ctx = ctx;
		this.datas = datas;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return datas == null ? 0 : datas.size();
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		// TODO Auto-generated method stub
		Map<String,Object> data=datas.get(position);
		holder.tvCode.setText(data.get("name").toString());
		holder.tvName.setText(data.get("code").toString());
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int type) {
		// TODO Auto-generated method stub
		MyViewHolder holder = new MyViewHolder(LayoutInflater.from(ctx)
				.inflate(R.layout.item_recyclerview_maintenance_has_choose,
						parent, false));
		return holder;
	}

	public class MyViewHolder extends ViewHolder {
		AppCompatTextView tvName;
		AppCompatTextView tvCode;

		public MyViewHolder(View v) {
			super(v);
			// TODO Auto-generated constructor stub
			tvName = (AppCompatTextView) v.findViewById(R.id.tv_name);
			tvCode = (AppCompatTextView) v.findViewById(R.id.tv_code);
		}

	}

	public void setDatas(List<Map<String, Object>> lists) {
		// TODO Auto-generated method stub
		this.datas=lists;
		notifyDataSetChanged();
	}
}
