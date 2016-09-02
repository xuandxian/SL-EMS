package com.overtech.ems.activity.adapter;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.overtech.ems.R;
import com.overtech.ems.activity.MyApplication;

public class GrabTaskAdapter extends BaseAdapter {

	private List<Map<String,Object>> list;
	private Context context;
	private LatLng mLocation;
	private double curLatitude;
	private double curLongitude;

	public List<Map<String,Object>> getData() {
		return list;
	}

	public void setData(List<Map<String,Object>> data) {
		this.list = data;
	}

	public GrabTaskAdapter(List<Map<String,Object>> list, Context context) {
		super();
		this.list = list;
		this.context = context;
		curLatitude = ((MyApplication) context.getApplicationContext()).latitude;
		curLongitude = ((MyApplication) context.getApplicationContext()).longitude;
		mLocation = new LatLng(curLatitude, curLongitude);
	}
	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(context,
					R.layout.item_list_parttime_hot, null);
			new ViewHolder(convertView);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		Map<String,Object> data = list.get(position);
		holder.tv_name.setText(data.get("taskPackageName").toString());
		holder.elevtorNum.setText((int)Double.parseDouble(data.get("elevatorAmounts").toString())+"");
		holder.addressName.setText(data.get("maintenanceAddress").toString());
		String latitude = data.get("latitude").toString();
		String longitude = data.get("longitude").toString();
		if (null == latitude || TextUtils.equals("", latitude)
				|| null == longitude || TextUtils.equals("", longitude)) {
			holder.distance.setText("距离计算错误");
		} else {
			NumberFormat numberFormat = NumberFormat.getNumberInstance();// 保留两位小数
			numberFormat.setMaximumFractionDigits(2);
			LatLng latlng = new LatLng(Double.valueOf(data.get("latitude").toString()),
					Double.valueOf(data.get("longitude").toString()));
			if (curLatitude == 0 || curLongitude == 0) {
				holder.distance.setText("未获取到位置信息");
			} else {
				holder.distance.setText(numberFormat.format(DistanceUtil
						.getDistance(mLocation, latlng) / 1000.0) + "km");
			}
		}
		holder.date.setText(data.get("maintenanceDate").toString());
		if (TextUtils.equals(data.get("grabNums").toString(), "0")) {
			holder.iv_icon.setImageResource(R.drawable.icon_task_none);
		} else if (TextUtils.equals(data.get("grabNums").toString(), "1")) {
			holder.iv_icon.setImageResource(R.drawable.icon_task_done);
		}
		if (TextUtils.equals(data.get("topState").toString(), "1")) {
			holder.hot.setVisibility(View.VISIBLE);
		} else {
			holder.hot.setVisibility(View.GONE);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView elevtorNum;
		TextView addressName;
		TextView distance;
		TextView date;
		ImageView hot;

		public ViewHolder(View view) {
			iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
			tv_name = (TextView) view.findViewById(R.id.tv_name);
			elevtorNum = (TextView) view.findViewById(R.id.tv_relevator_num);
			addressName = (TextView) view.findViewById(R.id.tv_address);
			distance = (TextView) view.findViewById(R.id.tv_distance);
			date = (TextView) view.findViewById(R.id.tv_maintence_date);
			hot = (ImageView) view.findViewById(R.id.iv_hot);
			view.setTag(this);
		}
	}
}
