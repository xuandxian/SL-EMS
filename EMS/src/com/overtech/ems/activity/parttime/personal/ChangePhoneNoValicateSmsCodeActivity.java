package com.overtech.ems.activity.parttime.personal;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
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
 *@author Tony
 *@date change on 2016-01-13
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

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.SUBMIT_PHONENO_SUCCESS:
				String json=(String)msg.obj;
				try {
					JSONObject jsonObj=new JSONObject(json);
					String model=jsonObj.getString("model");
					if (model.equals("0")) {
						Utilities.showToast("手机号被占用", context);
					}else if (model.equals("1")) {
						Utilities.showToast("验证码发送成功", context);
					}else if (model.equals("2")) {
						Utilities.showToast("验证码发送失败", context);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case StatusCode.COMMOM_SUBMIT_SMS_CODE:
				String json2=(String)msg.obj;
				try {
					JSONObject jsonObj=new JSONObject(json2);
					String model=jsonObj.getString("model");
					if (model.equals("3")) {
						Utilities.showToast("验证成功", context);
						UpdatePhoneNo();
					}else if (model.equals("4")) {
						Utilities.showToast("验证失败", context);
					}else if (model.equals("5")) {
						Utilities.showToast("验证码失效", context);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case StatusCode.UPDATE_PHONENO_SUCCESS:
				Intent intent = new Intent(ChangePhoneNoValicateSmsCodeActivity.this,ChangePhoneNoSuccessActivity.class);
				startActivity(intent);
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
		initView();
		initEvent();
	}

	private void initEvent() {
		mDoBack.setOnClickListener(this);
		mNextContent.setOnClickListener(this);
		mGetMessageCode.setOnClickListener(this);
	}

	private void initView() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mGetMessageCode = (TimeButton) findViewById(R.id.get_verification_code);
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mNextContent = (Button) findViewById(R.id.btn_next);
		mPhoneNoEditText = (EditTextWithDelete) findViewById(R.id.et_update_phone);
		mValidateCodeEditText = (EditTextWithDelete) findViewById(R.id.et_update_verificate_password);
		mHeadContent.setText("输入验证码");
		mDoBack.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.get_verification_code:
			mPhoneNo = mPhoneNoEditText.getText().toString().trim();
			if (Utilities.isMobileNO(mPhoneNo)) {
				Param param = new Param(Constant.PHONENO, mPhoneNo);
				Param flag = new Param(Constant.FLAG, "0");
				verifyPhoneNoAndGetSmsCode(ServicesConfig.COMMON_GET_SMS_CODE, param,flag);
			} else {
				Utilities.showToast("请输入正确的手机号", context);
			}
			break;
		case R.id.btn_next:
			mSMSCode = mValidateCodeEditText.getText().toString().trim();
			if (TextUtils.isEmpty(mSMSCode)) {
				Utilities.showToast("输入不能为空", context);
			} else {
				Param phoneParam = new Param(Constant.PHONENO, mPhoneNo);
		    	Param smsParam = new Param(Constant.SMSCODE, mSMSCode);
				submitVerificateSmsCode(ServicesConfig.COMMON_VARLICATE_SMS_CODE, phoneParam,smsParam);
			}
			break;
		case R.id.iv_headBack:
			finish();
			break;
		}
	}


	public void verifyPhoneNoAndGetSmsCode(String url, Param... params) {
		Request request = httpEngine.createRequest(url, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					String result = response.body().string();
					msg.what = StatusCode.SUBMIT_PHONENO_SUCCESS;
					msg.obj=result;
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
	
    private void submitVerificateSmsCode(String url, Param... params) {
    	startProgressDialog("正在验证...");
		Request request = httpEngine.createRequest(url, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					String result = response.body().string();
					msg.what = StatusCode.COMMOM_SUBMIT_SMS_CODE;
					msg.obj=result;
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
	
	private void UpdatePhoneNo() {
		String mLoginName = mSharedPreferences.getString(SharedPreferencesKeys.CURRENT_LOGIN_NAME, null);
		Param paramPhone = new Param(Constant.LOGINNAME,mLoginName);
		Param paramTaskNo = new Param(Constant.NEWPHONE, mPhoneNo);
		Request request = httpEngine.createRequest(ServicesConfig.CHANGE_PHONENO_UPDATE, paramPhone,paramTaskNo);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			
			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					String result = response.body().string();
					if (TextUtils.equals("true", result)) {
						msg.what = StatusCode.UPDATE_PHONENO_SUCCESS;
					} else {
						msg.what = StatusCode.UPDATE_PHONENO_FAILURE;
					}
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
