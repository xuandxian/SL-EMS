package com.overtech.ems.activity.parttime.personal.phoneno;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.common.password.ResetPasswordActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.CommonBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.security.MD5Util;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/*
 *修改手机号功能（验证登录密码）
 *@author Will
 *@date change on 2016-06-15
 *
 */
public class ChangePhoneNoValidatePasswordActivity extends BaseActivity
		implements OnClickListener {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private Button mNextContent;
	private String mPhoneNo, flag;
	private EditTextWithDelete mPhone;
	private EditTextWithDelete mPassword;
	private ChangePhoneNoValidatePasswordActivity activity;
	public static final String CHANGEPHONE = "0";
	public static final String RESETPASSWORD = "1";
	private String uid;
	private String certificate;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			stopProgressDialog();
			switch (msg.what) {
			case StatusCode.RESPONSE_VALICATE_PASSWORD_SUCCESS:
				String json = (String) msg.obj;
				Logr.e("后台传过来的数据==" + json);
				CommonBean bean = gson.fromJson(json, CommonBean.class);
				int st = bean.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(bean.msg, activity);
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(activity, LoginActivity.class);
					startActivity(intent);
					return;
				}
				String success = bean.body.success;
				if (TextUtils.equals(CHANGEPHONE, flag)) {
					if (success.equals("1")) {
						Intent intent = new Intent(activity,
								ChangePhoneNoValicateSmsCodeActivity.class);
						startActivity(intent);
					} else {
						Utilities.showToast(bean.msg, activity);
					}
				} else if (TextUtils.equals(RESETPASSWORD, flag)) {
					if (success.equals("1")) {
						Intent intent = new Intent(activity,
								ResetPasswordActivity.class);
						intent.putExtra("mPhoneNo", mPhoneNo);
						startActivity(intent);
					} else {
						Utilities.showToast(bean.msg, activity);
					}
				}
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast(R.string.response_failure_msg, activity);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast(R.string.response_no_data, activity);
				break;
			}
			stopProgressDialog();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_phoneno_vc);
		activity = this;
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
		stackInstance.pushActivity(this);
		mPhoneNo = getIntent().getStringExtra("phone");
		flag = getIntent().getStringExtra("flag");
		initView();
		initEvent();
	}

	private void initEvent() {
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(this);
		mNextContent.setOnClickListener(this);
	}

	private void initView() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		if (TextUtils.equals(CHANGEPHONE, flag)) {
			mHeadContent.setText("更换手机号");
		} else if (TextUtils.equals(RESETPASSWORD, flag)) {
			mHeadContent.setText("修改密码");
		}
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mNextContent = (Button) findViewById(R.id.btn_next);
		mPhone = (EditTextWithDelete) findViewById(R.id.et_phone);
		mPhone.setHint(mPhoneNo);
		mPassword = (EditTextWithDelete) findViewById(R.id.et_verificate_password);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_headBack:
			stackInstance.popActivity(activity);
			break;
		case R.id.btn_next:
			String phoneNo = mPhone.getHint().toString().trim();
			String password = mPassword.getText().toString().trim();
			valicatePassword(phoneNo, password);
			break;
		default:
			break;
		}
	}

	private void valicatePassword(String phoneNo, String password) {
		startProgressDialog(getResources().getString(R.string.loading_public_default));
		Requester requester = new Requester();
		requester.cmd = 20072;
		requester.uid = uid;
		requester.certificate = certificate;
		requester.body.put("phone", phoneNo);
		try {
			requester.body.put("password", MD5Util.md5Encode(password));
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Request request = httpEngine.createRequest(SystemConfig.NEWIP,
				gson.toJson(requester));
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					String result = response.body().string();
					msg.what = StatusCode.RESPONSE_VALICATE_PASSWORD_SUCCESS;
					msg.obj = result;
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
					msg.obj = response.body().string();
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request request, IOException e) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				msg.obj = "服务器正在维护";
				handler.sendMessage(msg);
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}
}
