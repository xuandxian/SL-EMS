package com.overtech.ems.activity.common;

import android.content.Intent;
import android.os.Bundle;
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
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.Employee;
import com.overtech.ems.utils.Logr;
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
		mChangePasswordState.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
						if (isChecked) {
							// 设置密码为可见的
							mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
						} else {
							mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
						}
					}
				});
		mLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sUserName = mUserName.getText().toString().trim();
				sPassword = mPassword.getText().toString().trim();
				if (TextUtils.isEmpty(sUserName) || TextUtils.isEmpty(sPassword)) {
					Utilities.showToast("输入不能为空", context);
				} else {
					startProgressDialog("正在登录...");
					Employee employee = new Employee();
					employee.setLoginName(sUserName);
					employee.setPassword(sPassword);
					Gson gson = new Gson();
					String person = gson.toJson(employee);
					Request request = httpEngine.createRequest(ServicesConfig.LOGIN, person);
					Call call = httpEngine.createRequestCall(request);
					call.enqueue(new Callback() {
						@Override
						public void onFailure(Request request, IOException e) {
							stopProgressDialog();
							Intent intent = new Intent(LoginActivity.this,MainActivity.class);
							startActivity(intent);
							finish();
							Log.e("Login","onFailure");
//							Utilities.showToast("登录失败",context);
						}
						@Override
						public void onResponse(Response response)throws IOException {
							String result=response.body().string();
							if (TextUtils.equals("true",result)){
								stopProgressDialog();
								Intent intent = new Intent(LoginActivity.this,MainActivity.class);
								startActivity(intent);
								finish();
							}else {
								stopProgressDialog();
								Intent intent = new Intent(LoginActivity.this,MainActivity.class);
								startActivity(intent);
								finish();
								Log.e("Login", "onFailure2");
//								Utilities.showToast("登录失败",context);
							}
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
			Intent intent = new Intent(LoginActivity.this,LostPasswordActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_login_by_message:
			Intent intent2 = new Intent(LoginActivity.this,RegisterActivity.class);
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
