package com.overtech.ems.activity.parttime.personal;

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
import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.Employee;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


/*
 *修改手机号功能（验证登录密码）
 *@author Tony
 *@date change on 2016-01-13
 *
 */
public class ChangePhoneNoValidatePasswordActivity extends BaseActivity implements OnClickListener {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private Button mNextContent;
	private String phone;
	private EditTextWithDelete mPhone;
	private EditTextWithDelete mPassword;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.RESPONSE_VALICATE_PASSWORD_SUCCESS:
				Intent intent = new Intent(ChangePhoneNoValidatePasswordActivity.this,ChangePhoneNoValicateSmsCodeActivity.class);
				startActivity(intent);
				break;
			case StatusCode.RESPONSE_VALICATE_PASSWORD_FAILURE:
				Utilities.showToast("密码错误", context);
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
		setContentView(R.layout.activity_change_phoneno_vc);
		phone = getIntent().getStringExtra("phone");
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
		mHeadContent.setText("验证密码");
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mNextContent = (Button) findViewById(R.id.btn_next);
		mPhone = (EditTextWithDelete) findViewById(R.id.et_phone);
		mPhone.setHint(phone);
		mPassword = (EditTextWithDelete) findViewById(R.id.et_verificate_password);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_headBack:
			finish();
			break;
		case R.id.btn_next:
			String phoneNo = mPhone.getHint().toString().trim();
			String password = mPassword.getText().toString().trim();
			ValicatePassword(phoneNo, password);
			break;
		default:
			break;
		}
	}

	private void ValicatePassword(String phoneNo, String password) {
		startProgressDialog("正在验证...");
		Employee employee = new Employee();
		employee.setPhoneNo(phoneNo);
		employee.setPassword(password);
		Gson gson = new Gson();
		String person = gson.toJson(employee);
		Request request = httpEngine.createRequest(ServicesConfig.CHANGE_PHONENO_PASSWORD, person);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					String result = response.body().string();
					if (TextUtils.equals("true", result)) {
						msg.what = StatusCode.RESPONSE_VALICATE_PASSWORD_SUCCESS;
					}else {
						msg.what = StatusCode.RESPONSE_VALICATE_PASSWORD_FAILURE;
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
			}
		});
	}
}
