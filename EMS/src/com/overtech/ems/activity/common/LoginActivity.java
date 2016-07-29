package com.overtech.ems.activity.common;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.password.LostPasswordActivity;
import com.overtech.ems.activity.common.register.RegisterActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.entity.bean.LoginBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.http.OkHttpClientManager.ResultCallback;
import com.overtech.ems.security.MD5Util;
import com.overtech.ems.utils.AppUtils;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;
import com.squareup.okhttp.Request;

/**
 * @author Overtech Will
 * @description 登录界面
 * @date 2016-6-14
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
	private EditTextWithDelete mUserName, mPassword;
	private String sUserName, sPassword;
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private TextView mLostPassword;
	private Button mLogin;
	private TextView mRegister;
	private ToggleButton mChangePasswordState;
	private String encryptPassword;
	private Context ctx;
	protected String certificate;
	protected String uid;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.MSG_SET_TAGS:
				Log.d("24梯", "Set tags in handler.");
				JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj,
						mTagsCallback);
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Logr.e("Loginactivity====oncreate===执行了");
		stackInstance.popAllActivitys();
		ctx = this;
		initView();
		initEvent();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		Logr.e("loginactivity===onNewIntent===执行了");
		stackInstance.popAllActivitys();
	}

	private void initView() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mUserName = (EditTextWithDelete) findViewById(R.id.et_login_username);
		mPassword = (EditTextWithDelete) findViewById(R.id.et_login_password);
		mLostPassword = (TextView) findViewById(R.id.tv_lost_password);
		mRegister = (TextView) findViewById(R.id.tv_login_by_message);
		mLogin = (Button) findViewById(R.id.btn_login);
		mChangePasswordState = (ToggleButton) findViewById(R.id.tb_change_password);
	}

	private void initEvent() {
		mHeadContent.setText("登 录");
		mHeadBack.setVisibility(View.VISIBLE);
		mLostPassword.setOnClickListener(this);
		mRegister.setOnClickListener(this);
		mHeadBack.setOnClickListener(this);
		mChangePasswordState
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							mPassword
									.setTransformationMethod(HideReturnsTransformationMethod
											.getInstance());// 设置密码为可见的
						} else {
							mPassword
									.setTransformationMethod(PasswordTransformationMethod
											.getInstance());
						}
					}
				});
		mLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sUserName = mUserName.getText().toString().trim();
				sPassword = mPassword.getText().toString().trim();
				if (TextUtils.isEmpty(sUserName)
						|| TextUtils.isEmpty(sPassword)) {
					Utilities.showToast("输入不能为空", ctx);
				} else {
					doLogin(sUserName, sPassword);
				}
			}
		});
	}

	private void doLogin(String username, String password) {
		try {
			encryptPassword = MD5Util.md5Encode(password);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		startProgressDialog(getResources().getString(R.string.loading_public_login));
		Requester requester = new Requester();
		requester.cmd = 1;
		requester.pwd = encryptPassword;
		requester.body.put("loginName", username);
		ResultCallback<LoginBean> callback = new ResultCallback<LoginBean>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				Utilities.showToast(R.string.request_error_msg, ctx);
				Logr.e(request.toString(), ctx);
			}

			@Override
			public void onResponse(LoginBean response) {
				// TODO Auto-generated method stub
				if (response == null) {
					Utilities.showToast(R.string.response_no_object, ctx);
					return;
				}
				int st = response.st;
				if (st != 0) {
					stopProgressDialog();
					Utilities.showToast(response.msg, ctx);
				} else {
					 certificate = response.body.certificate;
					String employeeType = response.body.employeeType;
					 uid = response.body.uid;
					SharePreferencesUtils.put(ctx,
							SharedPreferencesKeys.CERTIFICATED, certificate);
					SharePreferencesUtils.put(ctx, SharedPreferencesKeys.UID,
							uid);
					SharePreferencesUtils.put(ctx,
							SharedPreferencesKeys.EMPLOYEETYPE, employeeType);

					loadNotDoneTask();
				}
			}

		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_lost_password:
			Intent intent = new Intent(LoginActivity.this,
					LostPasswordActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_login_by_message:
			Intent intent2 = new Intent(LoginActivity.this,
					RegisterActivity.class);
			startActivity(intent2);
			break;
		case R.id.iv_headBack:
			onBackPressed();
			break;
		}
	}
	private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			stopProgressDialog();
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				Logr.e(logs);
				//将信息添加成功后即可成功
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
				break;
			case 6002:
				Utilities.showToast("登录失败，请检查网络重新尝试", LoginActivity.this);
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Logr.e(logs);
				if (AppUtils.isConnected(getApplicationContext())) {
					handler.sendMessageDelayed(handler.obtainMessage(
							StatusCode.MSG_SET_TAGS, tags), 1000 * 60);
				} else {
					Logr.e("No network");
				}
				break;

			default:
				logs = "Failed with errorCode = " + code;
				Logr.e(logs);
			}

		}

	};
	private ResultCallback<Bean> loadNotDoneCallback = new ResultCallback<Bean>() {

		@Override
		public void onError(Request request, Exception e) {
			// TODO Auto-generated method stub
			Logr.e(request.toString());
		}

		@Override
		public void onResponse(Bean response) {
			// TODO Auto-generated method stub
			int st = response.st;
			if (st == 0) {
				List<Map<String, Object>> datas = (List<Map<String, Object>>) response.body
						.get("data");
				Set<String> tempSet = new HashSet<String>();
				for (Map<String, Object> data : datas) {
					String sTaskNo = (String) data.get("taskNo");
					tempSet.add(sTaskNo);
				}
				JPushInterface.setAliasAndTags(LoginActivity.this, "", tempSet,
						mTagsCallback);
			}
		}
	};

	/**
	 * 加载未完成的任务单
	 */
	private void loadNotDoneTask() {
		Requester requester = new Requester();
		requester.cmd = 20050;
		requester.uid = uid;
		requester.certificate = certificate;
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, loadNotDoneCallback,
				gson.toJson(requester));
	}
}
