package com.overtech.ems.activity.common.password;

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
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;
import com.overtech.ems.widget.TimeButton;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class LostPasswordActivity extends BaseActivity {
	private String mPhoneNo;
	private String mSMSCode;
	private EditTextWithDelete mPhoneNoEditText;
	private EditTextWithDelete mSMSCodeEditText;
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private Button mNext;
	private TimeButton mGetValicateCode;
	private LostPasswordActivity activity;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case StatusCode.SUBMIT_PHONENO_SUCCESS:// 首先请求服务器对该手机号进行验证，根据结果确定要不要进行下一步的验证
				String json = (String) msg.obj;
				Logr.e(json);
				CommonBean phoneBean = gson.fromJson(json, CommonBean.class);
				int phoneSt = phoneBean.st;
				String phoneMsg = phoneBean.msg;
				Utilities.showToast(phoneMsg, activity);
				break;

			case StatusCode.COMMOM_SUBMIT_SMS_CODE:
				String json1 = (String) msg.obj;
				Logr.e(json1);
				CommonBean smsBean = gson.fromJson(json1, CommonBean.class);
				int smsSt = smsBean.st;
				String smsMsg = smsBean.msg;
				if (smsSt == 100) {
					Utilities.showToast(smsMsg, activity);
					Intent intent = new Intent(LostPasswordActivity.this,
							ResetPasswordActivity.class);
					intent.putExtra("mPhoneNo", mPhoneNo);
					startActivity(intent);
				} else {
					Utilities.showToast(smsMsg, activity);
				}
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast(R.string.request_error_msg, activity);
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast(R.string.response_failure_msg, activity);
				break;
			}
			stopProgressDialog();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_password);
		activity = LostPasswordActivity.this;
		findViewById();
		init();
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mPhoneNoEditText = (EditTextWithDelete) findViewById(R.id.et_lost_password_phone);
		mSMSCodeEditText = (EditTextWithDelete) findViewById(R.id.et_valicate_code);
		mGetValicateCode = (TimeButton) findViewById(R.id.btn_get_valicate_code);
		mNext = (Button) findViewById(R.id.bt_submit_smscode);
	}

	private void init() {
		mHeadContent.setText("密码重置");
		mHeadBack.setVisibility(View.VISIBLE);
		mPhoneNoEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(arg0)
						&& Utilities.isMobileNO(arg0.toString())) {
					mGetValicateCode.setEnabled(true);
				} else {
					mGetValicateCode.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
		mNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				submitVerificationCode();
			}
		});
		mGetValicateCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				getSmsCode();
			}
		});
		mHeadBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private void getSmsCode() {
		mPhoneNo = mPhoneNoEditText.getText().toString().trim();
		if (Utilities.isMobileNO(mPhoneNo)) {
			startProgressDialog(getResources().getString(R.string.loading_public_default));
			Requester requester = new Requester();
			requester.cmd = 10;
			requester.body.put(Constant.PHONENO, mPhoneNo);
			requester.body.put(Constant.FLAG, "1");// 如果存在该手机号时可以发送验证码

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
			Utilities.showToast("请输入正确的手机号", activity);
		}
	}

	public void submitVerificationCode() {
		startProgressDialog(getResources().getString(R.string.loading_public_sms));
		mSMSCode = mSMSCodeEditText.getText().toString().trim();
		if (TextUtils.isEmpty(mSMSCode)) {
			Utilities.showToast("输入不能为空", activity);
		} else {
			Requester requester = new Requester();
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
}
