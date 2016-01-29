package com.overtech.ems.activity.common.password;

import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.Employee;
import com.overtech.ems.security.MD5Util;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import android.content.Context;
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
import java.io.IOException;

public class ResetPasswordActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private Button mResetPassword;
	private EditTextWithDelete mPasswordNew;
	private EditTextWithDelete mPasswordConfirm;
	private String sPasswordNew;
	private String sPasswordConfirm;
	private Context context;
	private String mPhoneNo;
	
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.RESET_PASSWORD_SUCCESS:
				Intent intent = new Intent(ResetPasswordActivity.this,ResetPasswordSuccessActivity.class);
				startActivity(intent);
				finish();
				break;
			case StatusCode.RESET_PASSWORD_FAILED:
				Utilities.showToast("重置密码失败", context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", context);
				break;
			default:
				break;
			}
			stopProgressDialog();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);
		findViewById();
		init();
	}

	private void findViewById() {
		context = ResetPasswordActivity.this;
		mPhoneNo=getIntent().getStringExtra("mPhoneNo");
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mResetPassword = (Button) findViewById(R.id.btn_reset_password);
		mPasswordNew = (EditTextWithDelete) findViewById(R.id.et_reset_password_new);
		mPasswordConfirm = (EditTextWithDelete) findViewById(R.id.et_reset_password_confirm);
	}

	private void init() {
		mHeadContent.setText("密码重置");
		mHeadBack.setVisibility(View.VISIBLE);
		mResetPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sPasswordNew = mPasswordNew.getText().toString().trim();
				sPasswordConfirm = mPasswordConfirm.getText().toString().trim();
				if (TextUtils.isEmpty(sPasswordNew) || TextUtils.isEmpty(sPasswordConfirm)) {
					Utilities.showToast("输入不能为空", context);
				} else {
					if (sPasswordNew.length() >= 6 && sPasswordNew.length() <= 18) {
						if (TextUtils.equals(sPasswordNew, sPasswordConfirm)) {
							startProgressDialog("正在更新...");
							Employee employee = new Employee();
							employee.setPhoneNo(mPhoneNo);
							try {
								employee.setPassword(MD5Util.md5Encode(sPasswordNew));
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							Gson gson = new Gson();
							String person = gson.toJson(employee);
							Request request = httpEngine.createRequest(ServicesConfig.UPDATE_PASSWORD, person);
							Call call = httpEngine.createRequestCall(request);
							call.enqueue(new Callback() {
								@Override
								public void onFailure(Request request,IOException e) {
									Message msg = new Message();
									msg.what = StatusCode.RESPONSE_NET_FAILED;
									handler.sendMessage(msg);
								}

								@Override
								public void onResponse(Response response)throws IOException {
									Message msg = new Message();
									if (response.isSuccessful()) {
										String result = response.body().string();
										if (TextUtils.equals("true", result)) {
											msg.what = StatusCode.RESET_PASSWORD_SUCCESS;
										} else {
											msg.what = StatusCode.RESET_PASSWORD_FAILED;
										}
									} else {
										msg.what = StatusCode.RESPONSE_NET_FAILED;
									}
									handler.sendMessage(msg);
								}
							});
						} else {
							Utilities.showToast("两次输入不相同", context);
						}
					} else {
						Utilities.showToast("密码长度为6—18位", context);
					}
				}
			}
		});
		mHeadBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
}
