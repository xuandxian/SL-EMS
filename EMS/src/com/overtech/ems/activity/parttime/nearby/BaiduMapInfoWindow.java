package com.overtech.ems.activity.parttime.nearby;

import com.overtech.ems.R;
import com.overtech.ems.entity.bean.TaskPackageBean.TaskPackage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaiduMapInfoWindow extends LinearLayout {

	private Context mContext = null;
	private View mView = null;
	private TextView mCommunityName;
	private TaskPackage mData;


	public BaiduMapInfoWindow(Context context, TaskPackage data) {
		super(context);
		mContext = context;
		mData = data;
		initView();
	}

	public BaiduMapInfoWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}

	public BaiduMapInfoWindow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView();
	}

	private void initView() {
		mView = LayoutInflater.from(mContext).inflate(R.layout.map_info_window,null);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		this.addView(mView, params);
		mCommunityName = (TextView) (mView).findViewById(R.id.tv_baidumap_infowindow);
		mCommunityName.setText(mData.taskPackageName);
	}

	public void setBackgroundResource(int resid) {
		mView.setBackgroundResource(resid);
	}

}

