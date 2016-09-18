package com.overtech.ems.activity.parttime.common;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;

/*
 * 电梯详情
 */
public class ElevatorDetailActivity extends BaseActivity {

	private Toolbar toolbar;
	private ActionBar actionBar;
	private AppCompatTextView tvTitle;
	private TextView mProjectName;
	private TextView mElevatorBrand;
	private TextView mElevatorModel;
	private TextView mElevatorNo;
	private TextView mElevatorAliase;
	private TextView mTenementCompany;
	private TextView mTenementPerson;
	private TextView mTenementTel;
	private TextView mMaintenanceCompany;
	private TextView mLoadCapacity;
	private TextView mNominalSpeed;
	private TextView mStoreyPlatformDoor;
	private TextView mElevatorHigher;
	private TextView mMaintenanceType;
	private TextView mDeviceAddress;
	private TextView mAnnualInspectionDate;
	private TextView mLastMaintenanceDate;
	private String sElevatorNo;
	private String uid;
	private String certificate;
	private Activity activity;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_elevator_detail;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = this;
		stackInstance.pushActivity(activity);
		findViewById();
		getExtraData();
		init();
	}

	private void findViewById() {
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
		mProjectName = (TextView) findViewById(R.id.tv_project_name);
		mElevatorBrand = (TextView) findViewById(R.id.tv_elevator_brand);
		mElevatorModel = (TextView) findViewById(R.id.tv_elevator_model);
		mElevatorNo = (TextView) findViewById(R.id.tv_elevator_no);
		mElevatorAliase = (TextView) findViewById(R.id.tv_elevator_aliase);
		mTenementCompany = (TextView) findViewById(R.id.tv_tenement_company);
		mTenementPerson = (TextView) findViewById(R.id.tv_tenement_person);
		mTenementTel = (TextView) findViewById(R.id.tv_tenement_tel);
		mMaintenanceCompany = (TextView) findViewById(R.id.tv_maitenance_company);
		mLoadCapacity = (TextView) findViewById(R.id.tv_load_capacity);
		mNominalSpeed = (TextView) findViewById(R.id.tv_nominal_speed);
		mStoreyPlatformDoor = (TextView) findViewById(R.id.tv_storey_platform_door);
		mElevatorHigher = (TextView) findViewById(R.id.tv_elevator_higher);
		mMaintenanceType = (TextView) findViewById(R.id.tv_maintenance_type);
		mDeviceAddress = (TextView) findViewById(R.id.tv_device_address);
		mAnnualInspectionDate = (TextView) findViewById(R.id.tv_annual_inspection_date);
		mLastMaintenanceDate = (TextView) findViewById(R.id.tv_last_maintenance_date);
	}

	private void getExtraData() {
		sElevatorNo = getIntent().getStringExtra(Constant.ELEVATORNO);
		uid = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.CERTIFICATED, "");
	}

	private void init() {
		tvTitle.setText("电梯详情");
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
		
		getDataByElevatorNo();
	}

	private void getDataByElevatorNo() {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("elevatorNo", sElevatorNo);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20003, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				mProjectName.setText(response.body.get("projectName")
						.toString());
				mElevatorBrand.setText(response.body.get("elevatorBrand")
						.toString());
				mElevatorModel.setText(response.body.get("elevatorModel")
						.toString());
				mElevatorNo.setText(response.body.get("elevatorNo").toString());
				mElevatorAliase.setText(response.body.get("elevatorAliase")
						.toString());
				mTenementCompany.setText(response.body.get("tenementCompany")
						.toString());
				mTenementPerson.setText(response.body.get("tenementPerson")
						.toString());
				mTenementTel.setText(response.body.get("tenementTel")
						.toString());
				mMaintenanceCompany.setText(response.body.get(
						"maintenanceCompany").toString());
				mLoadCapacity.setText(response.body.get("loadCapacity")
						.toString());
				mNominalSpeed.setText(response.body.get("nominalSpeed")
						.toString());
				mStoreyPlatformDoor.setText(response.body.get(
						"storeyPlatformDoor").toString());
				mElevatorHigher.setText(response.body.get("elevatorHigher")
						.toString());
				mMaintenanceType.setText(response.body.get("maintenanceType")
						.toString());
				mDeviceAddress.setText(response.body.get("deviceAddress")
						.toString());
				mAnnualInspectionDate.setText(response.body.get(
						"annualInspectionDate").toString());
				mLastMaintenanceDate.setText(response.body.get(
						"lastMaintenanceDate").toString());
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
		conn.sendRequest();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}

}
