package com.overtech.ems.activity.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.common.password.LostPasswordActivity;
import com.overtech.ems.activity.common.register.RegisterActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.security.MD5Util;
import com.overtech.ems.utils.AppUtils;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;

/**
 * @author Overtech Will
 * @description 登录界面
 * @date 2016-6-14
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
	private Toolbar toolbar;
	private ActionBar actionBar;
	private AppCompatTextView tvTitle;
	private EditTextWithDelete mUserName, mPassword;
	private SwitchCompat switchPassword;
	private String sUserName, sPassword;
	private Button mLogin;
	private TextView mLostPassword;
	private TextView mRegister;

	private String encryptPassword;
	private Context ctx;
	protected String certificate;
	protected String uid;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.MSG_SET_TAGS:
				Log.d("24梯", "Set tags in handler.");
				JPushInterface.setAliasAndTags(getApplicationContext(), null,
						(Set<String>) msg.obj, mTagsCallback);
				break;
			}
		};
	};

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_login;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Logr.e("Loginactivity====oncreate===执行了");
		ctx = this;
		stackInstance.popAllActivitys();
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
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);

		actionBar = getSupportActionBar();
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
		mUserName = (EditTextWithDelete) findViewById(R.id.et_login_username);
		mPassword = (EditTextWithDelete) findViewById(R.id.et_login_password);
		mLostPassword = (TextView) findViewById(R.id.tv_lost_password);
		mRegister = (TextView) findViewById(R.id.tv_login_by_message);
		mLogin = (Button) findViewById(R.id.btn_login);
		switchPassword = (SwitchCompat) findViewById(R.id.switch_password);
	}

	private void initEvent() {
		toolbar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(true);
		tvTitle.setText("登录");
		mLostPassword.setOnClickListener(this);
		mRegister.setOnClickListener(this);
		switchPassword
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
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
		startProgressDialog(getResources().getString(
				R.string.loading_public_login));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("loginName", username);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(1, encryptPassword,
				body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return ctx;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				certificate = response.body.get("certificate").toString();
				String employeeType = response.body.get("employeeType")
						.toString();
				uid = response.body.get("uid").toString();
				SharePreferencesUtils.put(ctx,
						SharedPreferencesKeys.CERTIFICATED, certificate);
				SharePreferencesUtils.put(ctx, SharedPreferencesKeys.UID, uid);
				SharePreferencesUtils.put(ctx,
						SharedPreferencesKeys.EMPLOYEETYPE, employeeType);

				loadNotDoneTask();
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub
			}

			@Override
			public void bizStIs1Deal() {
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
				// 将信息添加成功后即可成功
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

	/**
	 * 加载未完成的任务单
	 */
	private void loadNotDoneTask() {
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20050, uid,
				certificate, null) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return ctx;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				List<Map<String, Object>> datas = (List<Map<String, Object>>) response.body
						.get("data");
				Set<String> tempSet = new HashSet<String>();
				for (Map<String, Object> data : datas) {
					String sTaskNo = (String) data.get("taskNo");
					tempSet.add(sTaskNo);
				}
				// 别名用will测试一下
				JPushInterface.setAliasAndTags(LoginActivity.this, uid,
						tempSet, mTagsCallback);
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub

			}
		};
		conn.sendRequest();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		((MyApplication) getApplication()).locationService.stop();
		super.onBackPressed();
	}

}
