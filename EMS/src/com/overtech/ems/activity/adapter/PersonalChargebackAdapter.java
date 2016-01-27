package com.overtech.ems.activity.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.entity.bean.ChargebackBean;

public class PersonalChargebackAdapter extends BaseAdapter {
	private Context context;
	private List<ChargebackBean> list;
	private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
	public PersonalChargebackAdapter(Context context,List<ChargebackBean> list){
		this.context=context;
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
		// TODO Auto-generated method stub
		ViewHolder vh=null;
		if(convertView==null){
			convertView=View.inflate(context, R.layout.item_list_personal_chargeback, null);
			vh=new ViewHolder(convertView);
			convertView.setTag(vh);
		}else{
			vh=(ViewHolder) convertView.getTag();
		}
		ChargebackBean data=list.get(position);
		Date grabDate=new Date(data.getGrabTime());
		Date chargebackDate=new Date(data.getChargeBackTime());
		vh.mTaskNo.setText(data.getTaskNo());
		vh.mTaskPackageTime.setText(data.getTaskPackageName());
		vh.mChargebackTime.setText("退单时间:"+format.format(chargebackDate));
		vh.mGrabTime.setText("抢单时间:"+format.format(grabDate));
		
		return convertView;
	}
	public class ViewHolder{
		TextView mTaskNo;
		TextView mGrabTime;
		TextView mTaskPackageTime;
		TextView mChargebackTime;
		public ViewHolder(View view){
			mTaskNo=(TextView) view.findViewById(R.id.tv_task_no);
			mGrabTime=(TextView) view.findViewById(R.id.tv_grab_time);
			mTaskPackageTime=(TextView) view.findViewById(R.id.tv_taskpackage_name);
			mChargebackTime=(TextView) view.findViewById(R.id.tv_chargeback_time);
		}
	}
}
