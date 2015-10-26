package com.overtech.ems.activity.parttime.tasklist;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;

public class TaskListPackageTaskDetailActivity extends BaseActivity {
	private ImageView mDoBack;
	private TextView mHeadContent;
	private TextView mCourtName;
	private TextView mCourtAddress;
	private TextView mPropertyName;
	private TextView mPropertyManager;
	private TextView mPropertyPhone;
	private TextView mPartnerName;
	private TextView mPartnerPhone;
	private TextView mMaintainTime;
	private RelativeLayout mElavator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tasklist_package_task_detail);
		initView();
		init();
	}

	private void init() {
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mHeadContent.setText("任务详情");
		mElavator.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
	}

	private void initView() {
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
		mHeadContent=(TextView) findViewById(R.id.tv_headTitle);
		mCourtName=(TextView) findViewById(R.id.tv_task_detail_court_name);
		mCourtAddress=(TextView) findViewById(R.id.tv_task_detail_court_address);
		mPropertyName=(TextView) findViewById(R.id.tv_task_detail_property_name);
		mPropertyManager=(TextView) findViewById(R.id.tv_task_detail_property_manager);
		mPropertyPhone=(TextView) findViewById(R.id.tv_task_detail_property_phone);
		mPartnerName=(TextView) findViewById(R.id.tv_task_detail_partner_name);
		mPartnerPhone=(TextView) findViewById(R.id.tv_task_detail_partner_phone);
		mMaintainTime=(TextView) findViewById(R.id.tv_task_detail_maintain_time);
		mElavator=(RelativeLayout) findViewById(R.id.rl_elevator_detail);
	}
}
