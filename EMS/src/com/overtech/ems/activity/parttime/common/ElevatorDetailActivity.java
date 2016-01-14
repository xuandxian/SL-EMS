package com.overtech.ems.activity.parttime.common;

import java.io.IOException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.ElevatorInfoBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.ElevatorInfo;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

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

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.GET_ELEVATOR_DETAILS_SUCCESS:
				String json = (String) msg.obj;
				Gson gson = new Gson();
				ElevatorInfoBean tasks = gson.fromJson(json,ElevatorInfoBean.class);
				ElevatorInfo model = tasks.getModel();
				mProjectName.setText(model.getProjectName());
				mElevatorBrand.setText(model.getElevatorBrand());
				mElevatorModel.setText(model.getElevatorModel());
				mElevatorNo.setText(model.getElevatorNo());
				mElevatorAliase.setText(model.getElevatorAliase());
				mTenementCompany.setText(model.getTenementCompany());
				mTenementPerson.setText(model.getTenementPerson());
				mTenementTel.setText(model.getTenementTel());
				mMaintenanceCompany.setText(model.getMaintenanceCompany());
				mLoadCapacity.setText(model.getLoadCapacity());
				mNominalSpeed.setText(model.getNominalSpeed());
				mStoreyPlatformDoor.setText(model.getStoreyPlatformDoor());
				mElevatorHigher.setText(model.getElevatorHigher());
				mMaintenanceType.setText(model.getMaintenanceType());
				mDeviceAddress.setText(model.getDeviceAddress());
				mAnnualInspectionDate.setText(model.getAnnualInspectionDate());
				mLastMaintenanceDate.setText(model.getLastMaintenanceDate());
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", context);
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast("服务端异常", context);
				break;
			}
			stopProgressDialog();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_elevator_detail);
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
		Bundle bundle = getIntent().getExtras();
		sElevatorNo = bundle.getString(Constant.ELEVATORNO);
	}

	private void init() {
		mGoBack.setVisibility(View.VISIBLE);
		mHeadContent.setText("电梯详情");
		getDataByElevatorNo();
		mGoBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private void getDataByElevatorNo() {
		startProgressDialog("正在加载数据");
		Param param = new Param(Constant.ELEVATORNO, sElevatorNo);
		Request request = httpEngine.createRequest(ServicesConfig.ELEVATOR_DETAIL, param);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.GET_ELEVATOR_DETAILS_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
					msg.obj = "服务器异常";
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request request, IOException exception) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				msg.obj = "网络异常";
				handler.sendMessage(msg);
			}
		});
	}
}
