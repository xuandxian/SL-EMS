package com.overtech.ems.activity.fulltime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.entity.fulltime.MaintenanceBean;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.http.OkHttpClientManager.ResultCallback;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Request;

public class MaintenanceResponseActivity extends BaseActivity {
	private TextView title;
	private EditText etCurrentSituation;
	private EditText etMaintenanceSolve;
	private Button btSubmit;
	private String certificate;
	private String uid;
	private String workorderCode;
	private MaintenanceResponseActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maintenance_response);
		stackInstance.pushActivity(activity);
		certificate = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.CERTIFICATED, "");
		uid = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.UID, "");
		workorderCode = getIntent().getStringExtra(Constant.WORKORDERCODE);
		activity = this;
		initView();
		initEvent();
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		btSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String sit = etCurrentSituation.getText().toString().trim();
				String solve = etMaintenanceSolve.getText().toString().trim();
				if (TextUtils.isEmpty(sit) || TextUtils.isEmpty(solve)) {
					Utilities.showToast("输入不能为空", activity);
					return;
				}
				startSubmit(sit, solve);
			}

			private void startSubmit(String sit, String solve) {
				// TODO Auto-generated method stub
				Requester requester = new Requester();
				requester.certificate = certificate;
				requester.uid = uid;
				requester.body.put("workorderCode", workorderCode);
				requester.body.put("liveSituation", sit);
				requester.body.put("repairResult", solve);
				ResultCallback<MaintenanceBean> callback = new OkHttpClientManager.ResultCallback<MaintenanceBean>() {

					@Override
					public void onError(Request request, Exception e) {
						// TODO Auto-generated method stub
						Logr.e(request.toString());
					}

					@Override
					public void onResponse(MaintenanceBean response) {
						// TODO Auto-generated method stub
						if (response == null) {
							Utilities.showToast("提交失败,请重新尝试", activity);
							return;
						}
						int st = response.st;
						String msg = response.msg;
						if (st != 0) {
							if (st == -1 || st == -2) {
								Utilities.showToast(msg, activity);
								SharePreferencesUtils.put(activity,
										SharedPreferencesKeys.UID, "");
								SharePreferencesUtils.put(activity,
										SharedPreferencesKeys.CERTIFICATED, "");
								Intent intent = new Intent(activity,
										LoginActivity.class);
								startActivity(intent);
							} else {
								Utilities.showToast(msg, activity);
							}
						} else {
							Utilities.showToast("提交成功", activity);
							// 关闭之前的activity
						}
					}
				};
			}

		});
	}

	private void initView() {
		// TODO Auto-generated method stub
		title = (TextView) findViewById(R.id.tv_headTitle);
		etCurrentSituation = (EditText) findViewById(R.id.et_current_situcation);
		etMaintenanceSolve = (EditText) findViewById(R.id.et_maintenance_result);
		btSubmit = (Button) findViewById(R.id.bt_submit);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}
}
