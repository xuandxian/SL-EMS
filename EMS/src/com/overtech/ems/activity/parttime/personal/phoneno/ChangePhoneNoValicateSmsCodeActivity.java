package com.overtech.ems.activity.parttime.personal.phoneno;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.CommonBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;
import com.overtech.ems.widget.TimeButton;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/*
 *修改手机号功能（验证手机号和验证验证短信验证码）
 *@author Will
 *@date change on 2016-06-15
 *
 */

public class ChangePhoneNoValicateSmsCodeActivity extends BaseActivity
		implements OnClickListener {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private Button mNextContent;
	private TimeButton mGetMessageCode;
	private EditTextWithDelete mPhoneNoEditText;
	private EditTextWithDelete mValidateCodeEditText;
	private String mPhoneNo;
	private String mSMSCode;
	private String uid;
	private String certificate;
	private ChangePhoneNoValicateSmsCodeActivity activity;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.SUBMIT_PHONENO_SUCCESS:
				String json = (String) msg.obj;
				CommonBean phoneBean = gson.fromJson(json, CommonBean.class);
				int st = phoneBean.st;
				if (st != 0) {
					Utilities.showToast(phoneBean.msg, activity);
				} else {
					Utilities.showToast(phoneBean.msg, activity);
				}
				break;
			case StatusCode.COMMOM_SUBMIT_SMS_CODE:
				String json2 = (String) msg.obj;
				CommonBean smsBean = gson.fromJson(json2, CommonBean.class);
				int smsSt = smsBean.st;
				if (smsSt == 100) {
					Utilities.showToast(smsBean.msg, activity);
					updatePhoneNo();
				} else {
					Utilities.showToast(smsBean.msg, activity);
				}
				break;
			case StatusCode.UPDATE_PHONENO_SUCCESS:
				String json3 = (String) msg.obj;
				CommonBean updateBean = gson.fromJson(json3, CommonBean.class);
				int updateSt = updateBean.st;
				if (updateSt == 0) {
					String success = updateBean.msg;
					if (success.equals("1")) {
						Utilities.showToast(updateBean.msg, activity);
						Intent intent = new Intent(activity,
								ChangePhoneNoSuccessActivity.class);
						startActivity(intent);
					}
				} else {
					Utilities.showToast(updateBean.msg, activity);
				}

				break;
			case StatusCode.UPDATE_PHONENO_FAILURE:
				Utilities.showToast("手机号更新失败", context);
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast("服务端异常", context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", context);
				break;
			}
			stopProgressDialog();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_phoneno_in_vc);
		stackInstance.pushActivity(this);
		initView();
		initEvent();
	}

	private void initEvent() {
		activity = this;
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
		mDoBack.setOnClickListener(this);
		mNextContent.setOnClickListener(this);
		mGetMessageCode.setOnClickListener(this);
		mPhoneNoEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (!TextUtils.isEmpty(arg0)
						&& Utilities.isMobileNO(arg0.toString())) {
					mGetMessageCode.setEnabled(true);
				} else {
					mGetMessageCode.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

	}

	private void initView() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mGetMessageCode = (TimeButton) findViewById(R.id.get_verification_code);
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mNextContent = (Button) findViewById(R.id.btn_next);
		mPhoneNoEditText = (EditTextWithDelete) findViewById(R.id.et_update_phone);
		mValidateCodeEditText = (EditTextWithDelete) findViewById(R.id.et_update_verificate_password);
		mHeadContent.setText("更换手机号");
		mDoBack.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.get_verification_code:
			getSmsCode();
			break;
		case R.id.btn_next:
			submitVerificateSmsCode();
			break;
		case R.id.iv_headBack:
			finish();
			break;
		}
	}

	private void getSmsCode() {
		// TODO Auto-generated method stub
		mPhoneNo = mPhoneNoEditText.getText().toString().trim();
		if (Utilities.isMobileNO(mPhoneNo)) {
			Requester requester = new Requester();
			requester.uid = uid;
			requester.certificate = certificate;
			requester.cmd = 10;
			requester.body.put("flag", "0");// 数据库中不存在该手机号时发送验证码
			requester.body.put(Constant.PHONENO, mPhoneNo);
			Request request = httpEngine.createRequest(SystemConfig.NEWIP,
					gson.toJson(requester));
			Call call = httpEngine.createRequestCall(request);
			call.enqueue(new Callback() {

				@Override
				public void onResponse(Response response) throws IOException {
					Message msg = new Message();
					if (response.isSuccessful()) {
						String result = response.body().string();
						msg.what = StatusCode.SUBMIT_PHONENO_SUCCESS;
						msg.obj = result;
					} else {
						msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
					}
					handler.sendMessage(msg);
				}

				@Override
				public void onFailure(Request request, IOException e) {
					Message msg = new Message();
					msg.what = StatusCode.RESPONSE_NET_FAILED;
					handler.sendMessage(msg);
				}
			});
		} else {
			Utilities.showToast("请输入正确的手机号", context);
		}
	}

	private void submitVerificateSmsCode() {
		startProgressDialog("正在验证...");
		mSMSCode = mValidateCodeEditText.getText().toString().trim();
		if (TextUtils.isEmpty(mSMSCode)) {
			Utilities.showToast("输入不能为空", context);
		} else {
			Requester requester = new Requester();
			requester.certificate = certificate;
			requester.uid = uid;
			requester.cmd = 11;
			requester.body.put(Constant.PHONENO, mPhoneNo);
			requester.body.put(Constant.SMSCODE, mSMSCode);
			Request request = httpEngine.createRequest(SystemConfig.NEWIP,
					gson.toJson(requester));
			Call call = httpEngine.createRequestCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onResponse(Response response) throws IOException {
					Message msg = new Message();
					if (response.isSuccessful()) {
						String result = response.body().string();
						msg.what = StatusCode.COMMOM_SUBMIT_SMS_CODE;
						msg.obj = result;
					} else {
						msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
					}
					handler.sendMessage(msg);
				}

				@Override
				public void onFailure(Request request, IOException e) {
					Message msg = new Message();
					msg.what = StatusCode.RESPONSE_NET_FAILED;
					handler.sendMessage(msg);
				}
			});
		}
	}

	private void updatePhoneNo() {
		Requester requester = new Requester();
		requester.uid = uid;
		requester.certificate = certificate;
		requester.cmd = 20073;
		requester.body.put("phone", mPhoneNo);

		Request request = httpEngine.createRequest(SystemConfig.NEWIP,
				gson.toJson(requester));
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.UPDATE_PHONENO_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
					msg.obj = "服务器正在维护中...";
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request request, IOException e) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				handler.sendMessage(msg);
			}
		});
	}
}
