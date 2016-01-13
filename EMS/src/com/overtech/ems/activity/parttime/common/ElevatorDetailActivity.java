package com.overtech.ems.activity.parttime.common;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.common.ServicesConfig;
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
	private Context mActivity;
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
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.ELEVATOR_SUCCESS:
				String json=(String) msg.obj;
				Log.e("========", json);
				try {
					JSONObject jsonObj=new JSONObject(json);
					if(!jsonObj.isNull("model")){
						JSONObject model=jsonObj.getJSONObject("model");
						Log.e("=====", "length="+model.length());
						mProjectName.setText(model.getString("projectName"));
						mElevatorBrand.setText(model.getString("elevatorBrand"));
						mElevatorModel.setText(model.getString("elevatorModel"));
						mElevatorNo.setText(model.getString("elevatorNo"));
						mElevatorAliase.setText(model.getString("elevatorAliase"));
						mTenementCompany.setText(model.getString("tenementCompany"));
						mTenementPerson.setText(model.getString("tenementPerson"));
						mTenementTel.setText(model.getString("tenementTel"));
						mMaintenanceCompany.setText(model.getString("maintenanceCompany"));
						mLoadCapacity.setText(model.getString("loadCapacity"));
						mNominalSpeed.setText(model.getString("nominalSpeed"));
						mStoreyPlatformDoor.setText(model.getString("storeyPlatformDoor"));
						mElevatorHigher.setText(model.getString("elevatorHigher"));
						mMaintenanceType.setText(model.getString("maintenanceType"));
						mDeviceAddress.setText(model.getString("deviceAddress"));
						mAnnualInspectionDate.setText(model.getString("annualInspectionDate"));
						mLastMaintenanceDate.setText(model.getString("lastMaintenanceDate"));
						
						stopProgressDialog();
					}else{
						Utilities.showToast("没有查询到电梯信息", mActivity);
						stopProgressDialog();
						finish();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
			case StatusCode.ELEVATOR_FAILED:
				String info=(String) msg.obj;
				Utilities.showToast(info, mActivity);
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_elevator_detail);
		initView();
		init();
	}

	private void initView() {
		mActivity=ElevatorDetailActivity.this;
		mGoBack = (ImageView) findViewById(R.id.iv_headBack);
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mProjectName=(TextView) findViewById(R.id.tv_project_name);
		mElevatorBrand=(TextView) findViewById(R.id.tv_elevator_brand);
		mElevatorModel=(TextView) findViewById(R.id.tv_elevator_model);
		mElevatorNo=(TextView) findViewById(R.id.tv_elevator_no);
		mElevatorAliase=(TextView) findViewById(R.id.tv_elevator_aliase);
		mTenementCompany=(TextView) findViewById(R.id.tv_tenement_company);
		mTenementPerson=(TextView) findViewById(R.id.tv_tenement_person);
		mTenementTel=(TextView) findViewById(R.id.tv_tenement_tel);
		mMaintenanceCompany=(TextView) findViewById(R.id.tv_maitenance_company);
		mLoadCapacity=(TextView) findViewById(R.id.tv_load_capacity);
		mNominalSpeed=(TextView) findViewById(R.id.tv_nominal_speed);
		mStoreyPlatformDoor=(TextView) findViewById(R.id.tv_storey_platform_door);
		mElevatorHigher=(TextView) findViewById(R.id.tv_elevator_higher);
		mMaintenanceType=(TextView) findViewById(R.id.tv_maintenance_type);
		mDeviceAddress=(TextView) findViewById(R.id.tv_device_address);
		mAnnualInspectionDate=(TextView) findViewById(R.id.tv_annual_inspection_date);
		mLastMaintenanceDate=(TextView) findViewById(R.id.tv_last_maintenance_date);
	}

	private void init() {
		Bundle bundle=getIntent().getExtras();
		sElevatorNo=bundle.getString(Constant.RESULT);
		mGoBack.setVisibility(View.VISIBLE);
		mHeadContent.setText("电梯详情");
		mGoBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		startLoading();
	}

	private void startLoading() {
		// TODO Auto-generated method stub
		startProgressDialog("正在加载数据");
		Param param=new Param(Constant.ELEVATORNO,sElevatorNo);
		Request request=httpEngine.createRequest(ServicesConfig.ELEVATOR_DETAIL, param);
		Call call=httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			
			@Override
			public void onResponse(Response response) throws IOException {
				Message msg=new Message();
				if(response.isSuccessful()){
					msg.what=StatusCode.ELEVATOR_SUCCESS;
					msg.obj=response.body().string();
				}else{
					msg.what=StatusCode.ELEVATOR_FAILED;
					msg.obj="后台君出了点问题";
				}
				handler.sendMessage(msg);
			}
			
			@Override
			public void onFailure(Request request, IOException exception) {
				Message msg=new Message();
				msg.what=StatusCode.ELEVATOR_FAILED;
				msg.obj="网络异常，请检查网络";
				handler.sendMessage(msg);
			}
		});
		
	}
}
