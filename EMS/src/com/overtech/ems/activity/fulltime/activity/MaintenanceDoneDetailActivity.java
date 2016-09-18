package com.overtech.ems.activity.fulltime.activity;

import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;

public class MaintenanceDoneDetailActivity extends BaseActivity {
	private Toolbar toolbar;
	private ActionBar actionBar;
	private AppCompatTextView tvTitle;
	private AppCompatTextView tvAddress;
	private AppCompatTextView tvElevatorNo;
	private AppCompatTextView tvFaultFrom;
	private AppCompatTextView tvRepairContent;
	private AppCompatTextView tvPeopleInEmergency;
	private String uid;
	private String certificate;
	private String sWorkorderCode;
	private MaintenanceDoneDetailActivity activity;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_maintenance_done_detail;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = this;
		stackInstance.pushActivity(activity);
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
		sWorkorderCode = getIntent().getStringExtra("workorderCode");
		initView();
		initEvent();
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		toolbar.setNavigationOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stackInstance.popActivity(activity);
			}
		});
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		tvTitle.setText(sWorkorderCode);
		
		startProgressDialog("加载中...");
		HashMap<String,Object> body=new HashMap<String,Object>();
		body.put("workorderCode", sWorkorderCode);
		HttpConnector<Bean> conn=new HttpConnector<Bean>(20013,uid,certificate,body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				tvAddress.setText("地址："+response.body.get("repairAddress").toString());
				tvElevatorNo.setText("梯号："+response.body.get("elevatorNo").toString());
				tvFaultFrom.setText("故障来源："+response.body.get("faultFrom").toString());
				tvRepairContent.setText("报修内容："+response.body.get("faultContent").toString());
				if(TextUtils.equals("1", response.body.get("peopleInEmergency").toString())){
					tvPeopleInEmergency.setText("是否关人：是");
				}else{
					tvPeopleInEmergency.setText("是否关人：否");
				}
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void bizStIs1Deal() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
			}
		};
	}

	private void initView() {
		// TODO Auto-generated method stub
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
		tvAddress = (AppCompatTextView) findViewById(R.id.tv_maintenance_address);
		tvElevatorNo = (AppCompatTextView) findViewById(R.id.tv_maintenance_elevator_no);
		tvFaultFrom = (AppCompatTextView) findViewById(R.id.tv_maintenance_fault_from);
		tvRepairContent = (AppCompatTextView) findViewById(R.id.tv_maintenace_repair_content);
		tvPeopleInEmergency = (AppCompatTextView) findViewById(R.id.tv_maintenance_close);
	}

}
