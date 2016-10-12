package com.overtech.ems.activity.fulltime.activity;

import java.util.HashMap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.AppUtils;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;

public class MaintenanceResponseActivity extends BaseActivity implements
		OnClickListener {
	private Toolbar toolbar;
	private ActionBar actionBar;
	private AppCompatTextView tvTitle;
	private AppCompatTextView tvRepairTime;
	private AppCompatTextView tvPeopleInEmergency;
	private AppCompatTextView tvArrivalTime;
	private EditText etCurrentSituation;
	private EditText etMaintenanceSolve;
	private Button btSubmit;
	private String certificate;
	private String uid;
	private String workorderCode;
	private String isMain;
	private String sRepairTime;
	private String sPeopleInEmergency;
	private String sArrivalTime;
	private String sLiveSituation;
	private String sRepairResult;
	private MaintenanceResponseActivity activity;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_maintenance_response;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = this;
		stackInstance.pushActivity(activity);
		certificate = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.CERTIFICATED, "");
		uid = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.UID, "");
		workorderCode = getIntent().getStringExtra(Constant.WORKORDERCODE);
		isMain = getIntent().getStringExtra("isMain");
		initView();
		initEvent();
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("workorderCode", workorderCode);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20012, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				sRepairTime = response.body.get("repairTime").toString();
				sPeopleInEmergency = response.body.get("peopleInEmergency")
						.toString();
				sArrivalTime = response.body.get("arrivalTime").toString();
				sLiveSituation = response.body.get("liveSituation").toString();
				sRepairResult = response.body.get("repairResult").toString();

				tvRepairTime.setText("报修时间：" + sRepairTime);
				if (!TextUtils.isEmpty(sPeopleInEmergency)) {
					tvPeopleInEmergency.setVisibility(View.VISIBLE);
					tvPeopleInEmergency.setText("关人时间：" + sPeopleInEmergency);
				}
				if (!TextUtils.isEmpty(sArrivalTime)) {
					tvArrivalTime.setVisibility(View.VISIBLE);
					tvArrivalTime.setText("到达时间：" + sArrivalTime);
				}
				if (!TextUtils.isEmpty(sLiveSituation)) {
					etCurrentSituation.setText(sLiveSituation);
				}
				if (!TextUtils.isEmpty(sRepairResult)) {
					etMaintenanceSolve.setText(sRepairResult);
				}
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal(Bean response) {
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

	private void initView() {
		// TODO Auto-generated method stub
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
		tvRepairTime = (AppCompatTextView) findViewById(R.id.tv_repair_time);
		tvPeopleInEmergency = (AppCompatTextView) findViewById(R.id.tv_people_in_emergency);
		tvArrivalTime = (AppCompatTextView) findViewById(R.id.tv_arrival_time);
		etCurrentSituation = (EditText) findViewById(R.id.et_current_situcation);
		etMaintenanceSolve = (EditText) findViewById(R.id.et_maintenance_result);
		btSubmit = (Button) findViewById(R.id.bt_submit);
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
		tvTitle.setText("维修结果");

		btSubmit.setOnClickListener(this);
		Logr.e("maintenanceResponse===isMain" + isMain);
		if (TextUtils.equals(isMain, "0")) {// 主修
			etCurrentSituation.setEnabled(true);
			etMaintenanceSolve.setEnabled(true);
		} else {
			etCurrentSituation.setEnabled(false);
			etMaintenanceSolve.setEnabled(false);
		}
	}

	private void submitRepairSolve(String live, String solve) {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("workorderCode", workorderCode);
		body.put("isMain", isMain);
		body.put("liveSituation", live);
		body.put("repairResult", solve);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20006, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				toMainActivity();
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal(Bean response) {
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

	private void toMainActivity() {
		Intent i = new Intent(activity, MainActivity.class);
		startActivity(i);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_submit:
			if (AppUtils.isValidTagAndAlias(etCurrentSituation.getText()
					.toString())
					&& AppUtils.isValidTagAndAlias(etMaintenanceSolve.getText()
							.toString())){
				alertBuilder
				.setTitle("提示")
				.setMessage("此次维修任务完成？")
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						})
				.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								if (TextUtils.equals(isMain, "0")) {
									sLiveSituation = etCurrentSituation
											.getText().toString();
									sRepairResult = etMaintenanceSolve
											.getText().toString();
									submitRepairSolve(sLiveSituation,
											sRepairResult);
								} else {
									toMainActivity();
								}
							}
						}).show();
			}else{
				Utilities.showToast("输入数据不合法", activity);
			}
				
			break;

		default:
			break;
		}
	}

}
