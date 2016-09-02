package com.overtech.ems.activity.common.password;

import java.io.IOException;

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

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.CommonBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.security.MD5Util;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

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
	private ResetPasswordActivity activity;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.RESET_PASSWORD_SUCCESS:
				String json = (String) msg.obj;
				Logr.e(json);
				CommonBean bean = gson.fromJson(json, CommonBean.class);
				int st = bean.st;
				String beanMsg = bean.msg;
				if (st == 0) {
					Utilities.showToast(beanMsg, context);
					Intent intent = new Intent(activity,
							ResetPasswordSuccessActivity.class);
					startActivity(intent);
				} else {
					Utilities.showToast(beanMsg, activity);
				}
				break;
			case StatusCode.RESET_PASSWORD_FAILED:
				Utilities.showToast(R.string.response_failure_msg, context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast(R.string.request_error_msg, context);
				break;
			default:
				break;
			}
			stopProgressDialog();
		};
	};

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_reset_password;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		stackInstance.pushActivity(this);
		findViewById();
		init();
	}

	private void findViewById() {
		context = ResetPasswordActivity.this;
		mPhoneNo = getIntent().getStringExtra("mPhoneNo");
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mResetPassword = (Button) findViewById(R.id.btn_reset_password);
		mPasswordNew = (EditTextWithDelete) findViewById(R.id.et_reset_password_new);
		mPasswordConfirm = (EditTextWithDelete) findViewById(R.id.et_reset_password_confirm);
	}

	private void init() {
		activity = ResetPasswordActivity.this;
		mHeadContent.setText("密码重置");
		mHeadBack.setVisibility(View.VISIBLE);
		mResetPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sPasswordNew = mPasswordNew.getText().toString().trim();
				sPasswordConfirm = mPasswordConfirm.getText().toString().trim();
				if (TextUtils.isEmpty(sPasswordNew)
						|| TextUtils.isEmpty(sPasswordConfirm)) {
					Utilities.showToast("输入不能为空", context);
				} else {
					if (sPasswordNew.length() >= 6
							&& sPasswordNew.length() <= 18) {
						if (TextUtils.equals(sPasswordNew, sPasswordConfirm)) {
							startProgressDialog(getResources().getString(
									R.string.loading_public_default));
							Requester requester = new Requester();
							requester.cmd = 20043;
							requester.body.put(Constant.PHONENO, mPhoneNo);
							try {
								requester.body.put("password",
										MD5Util.md5Encode(sPasswordNew));
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							Request request = httpEngine.createRequest(
									SystemConfig.NEWIP, gson.toJson(requester));
							Call call = httpEngine.createRequestCall(request);
							call.enqueue(new Callback() {
								@Override
								public void onFailure(Request request,
										IOException e) {
									Message msg = new Message();
									msg.what = StatusCode.RESPONSE_NET_FAILED;
									msg.obj = "网络异常";
									handler.sendMessage(msg);
								}

								@Override
								public void onResponse(Response response)
										throws IOException {
									Message msg = new Message();
									if (response.isSuccessful()) {
										msg.what = StatusCode.RESET_PASSWORD_SUCCESS;
										msg.obj = response.body().string();
									} else {
										msg.what = StatusCode.RESET_PASSWORD_FAILED;
										msg.obj = "服务器正在维护...";
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
				stackInstance.popActivity(activity);
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
