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

public class MaintenanceHasChooseAdapter extends Adapter<ViewHolder> {
	private Context ctx;
	private List<Map<String, Object>> datas;
	private final int NORMAL = 0x00123;
	private final int FOOTER = 0x00234;

	public MaintenanceHasChooseAdapter(Context ctx,
			List<Map<String, Object>> datas) {
		this.ctx = ctx;
		this.datas = datas;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return datas == null ? 1 : datas.size() + 1;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		if(holder instanceof MyViewHolder){
			MyViewHolder mvh=(MyViewHolder) holder;
			Map<String, Object> data = datas.get(position);
			mvh.tvCode.setText(data.get("name").toString());
			mvh.tvName.setText(data.get("code").toString());
		}else{
			FooterViewHolder fvh=(FooterViewHolder) holder;
			fvh.text.setText("暂无更多数据");
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
		// TODO Auto-generated method stub
		if (type == NORMAL) {
			MyViewHolder holder = new MyViewHolder(LayoutInflater.from(ctx)
					.inflate(R.layout.item_recyclerview_maintenance_has_choose,
							parent, false));
			return holder;
		} else {
			FooterViewHolder fvh = new FooterViewHolder(LayoutInflater
					.from(ctx).inflate(R.layout.item_footer_textview, parent,
							false));
			return fvh;
		}
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if (position == getItemCount() - 1) {
			return FOOTER;
		} else {
			return NORMAL;
		}
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

	public class FooterViewHolder extends ViewHolder {
		AppCompatTextView text;

		public FooterViewHolder(View v) {
			super(v);
			// TODO Auto-generated constructor stub
			text = (AppCompatTextView) v.findViewById(R.id.text);
		}

	}

	public void setDatas(List<Map<String, Object>> lists) {
		// TODO Auto-generated method stub
		this.datas = lists;
	}
}
