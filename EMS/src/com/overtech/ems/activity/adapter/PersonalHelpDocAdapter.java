package com.overtech.ems.activity.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.overtech.ems.R;

public class PersonalHelpDocAdapter extends BaseAdapter {
	private Context context;
	private String[] descs = { "1、如何抢单", "2、如何退单", "3、如何更换手机号", "4、奖励机制说明" };
	private int[][] iconIds = new int[][] {
			{ R.drawable.pic_grab1, R.drawable.pic_grab2, R.drawable.pic_grab3,
					R.drawable.pic_grab4 }, { R.drawable.pic_chargeback },
			{ R.drawable.pic_change_phone },
			{ R.drawable.pic_bonus1, R.drawable.pic_bonus2 } };

	public PersonalHelpDocAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return descs.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return descs[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_list_help_doc, null);
		}
		LinearLayout container = (LinearLayout) convertView
				.findViewById(R.id.ll_layout_container);
		TextView mDescs=(TextView) convertView.findViewById(R.id.tv_descriptions);
		container.removeAllViews();
		for (int i = 0; i < iconIds[position].length; i++) {
			ImageView image = new ImageView(context);
			image.setBackgroundResource(iconIds[position][i]);

			LinearLayout.LayoutParams params = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL;
			params.leftMargin = 10;
			params.topMargin = 5;
			params.rightMargin = 5;
			params.bottomMargin = 5;
			container.addView(image, params);
		}
		mDescs.setText(descs[position]);
		return convertView;
	}

}
