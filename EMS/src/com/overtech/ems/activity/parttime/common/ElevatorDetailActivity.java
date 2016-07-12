package com.overtech.ems.activity.parttime.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.entity.parttime.ElevatorBean;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.http.OkHttpClientManager.ResultCallback;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Request;

/*
 * 电梯详情
 */
public class ElevatorDetailActivity extends BaseActivity {

	private ImageView mGoBack;
	private TextView mHeadContent;
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_elevator_detail);
		activity = this;
		stackInstance.pushActivity(activity);
		findViewById();
		getExtraData();
		init();
	}

	private void findViewById() {
		mGoBack = (ImageView) findViewById(R.id.iv_headBack);
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
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
		mGoBack.setVisibility(View.VISIBLE);
		mHeadContent.setText("电梯详情");
		getDataByElevatorNo();
		mGoBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				stackInstance.popActivity(activity);
			}
		});
	}

	private void getDataByElevatorNo() {
		startProgressDialog(getResources().getString(R.string.loading_public_default));
		Requester requester = new Requester();
		requester.cmd = 20003;
		requester.certificate = certificate;
		requester.uid = uid;
		requester.body.put("elevatorNo", sElevatorNo);
		ResultCallback<ElevatorBean> callback = new ResultCallback<ElevatorBean>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				Logr.e(request.toString());
			}

			@Override
			public void onResponse(ElevatorBean response) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				if (response == null) {
					Utilities.showToast(R.string.response_no_object, activity);
					return;
				}
				int st = response.st;
				String msg = response.msg;
				if (st != 0) {
					if (st == -1 || st == -2) {
						if (activity != null) {
							Utilities.showToast(msg, activity);
							SharePreferencesUtils.put(activity,
									SharedPreferencesKeys.UID, "");
							SharePreferencesUtils.put(activity,
									SharedPreferencesKeys.CERTIFICATED, "");
							Intent intent = new Intent(activity,
									LoginActivity.class);
							startActivity(intent);
						}
					} else {
						Utilities.showToast(msg, activity);
					}
				} else {
					mProjectName.setText(response.body.projectName);
					mElevatorBrand.setText(response.body.elevatorBrand);
					mElevatorModel.setText(response.body.elevatorModel);
					mElevatorNo.setText(response.body.elevatorNo);
					mElevatorAliase.setText(response.body.elevatorAliase);
					mTenementCompany.setText(response.body.tenementCompany);
					mTenementPerson.setText(response.body.tenementPerson);
					mTenementTel.setText(response.body.tenementTel);
					mMaintenanceCompany
							.setText(response.body.maintenanceCompany);
					mLoadCapacity.setText(response.body.loadCapacity);
					mNominalSpeed.setText(response.body.nominalSpeed);
					mStoreyPlatformDoor
							.setText(response.body.storeyPlatformDoor);
					mElevatorHigher.setText(response.body.elevatorHigher);
					mMaintenanceType.setText(response.body.maintenanceType);
					mDeviceAddress.setText(response.body.deviceAddress);
					mAnnualInspectionDate
							.setText(response.body.annualInspectionDate);
					mLastMaintenanceDate
							.setText(response.body.lastMaintenanceDate);
				}
			}
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}
}
