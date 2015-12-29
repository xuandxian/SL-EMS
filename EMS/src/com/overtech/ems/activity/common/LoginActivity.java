package com.overtech.ems.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.Employee;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;
import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;

/**
 * @author Tony
 * @description 登录界面
 * @date 2015-10-05
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
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.LOGIN_SUCCESS:
				Utilities.showToast("登录成功", context);
				mSharedPreferences.edit().putString(SharedPreferencesKeys.CURRENT_LOGIN_NAME, sUserName).commit();// 将登陆的用户名保存
				Intent intent = new Intent(LoginActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
				break;
			case StatusCode.LOGIN_FAILED:
				Utilities.showToast("登录失败", context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", context);
				break;
			case StatusCode.LOGIN_NOT_EXIST:
				Utilities.showToast("用户不存在", context);
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
		setContentView(R.layout.activity_login);
		initView();
		initData();
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

	private void initData() {
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
					Utilities.showToast("输入不能为空", context);
				} else {
					startProgressDialog("正在登录...");
					Employee employee = new Employee();
					employee.setLoginName(sUserName);
					employee.setPassword(sPassword);
					Gson gson = new Gson();
					String person = gson.toJson(employee);
					Request request = httpEngine.createRequest(
							ServicesConfig.LOGIN, person);
					Call call = httpEngine.createRequestCall(request);
					call.enqueue(new Callback() {
						@Override
						public void onFailure(Request request, IOException e) {
							Message msg=new Message();
							msg.what=StatusCode.LOGIN_FAILED;
							handler.sendMessage(msg);
						}

						@Override
						public void onResponse(Response response)
								throws IOException {
							Message msg = new Message();
							if (response.isSuccessful()) {
								String result = response.body().string();
								if (TextUtils.equals("true", result)) {
									msg.what = StatusCode.LOGIN_SUCCESS;
								} else {
									msg.what = StatusCode.LOGIN_NOT_EXIST;
								}
							} else {
								msg.what = StatusCode.RESPONSE_NET_FAILED;
							}
							handler.sendMessage(msg);
						}
					});
				}
			}
		});
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
		default:
			break;
		}
	}
}
