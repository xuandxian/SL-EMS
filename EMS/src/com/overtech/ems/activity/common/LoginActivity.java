package com.overtech.ems.activity.common;

import java.io.IOException;

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
import com.overtech.ems.activity.parttime.MainActivity2;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.EditTextWithDelete;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

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
	private CustomProgressDialog progressDialog = null;
	/**
	 * 模拟地址
	 * */ 
	private String url="http://192.168.1.103/relevator/basicinfo/user/login.action";
	
	public final MediaType JSON=MediaType.parse("application/json;charset=utf-8");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setNeedBackGesture(false);// 设置需要手势监听
		initView();
		initData();
		
	}

	private void initData() {
		mHeadContent.setText("登 录");
		mHeadBack.setVisibility(View.VISIBLE);
		
		mLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sUserName = mUserName.getText().toString().trim();
				sPassword = mPassword.getText().toString().trim();
				if (TextUtils.isEmpty(sUserName)
						|| TextUtils.isEmpty(sPassword)) {
					Utilities.showToast("输入不能为空", context);
					Log.e("========", "hahahahhahahah");
				} else {
					Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
					startActivity(intent);
					finish();
				}
			}
		});
		mLostPassword.setOnClickListener(this);
		mRegister.setOnClickListener(this); 
		mHeadBack.setOnClickListener(this);
		mChangePasswordState
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// 设置密码为可见的
							mPassword
									.setTransformationMethod(HideReturnsTransformationMethod
											.getInstance());
						} else {
							mPassword
									.setTransformationMethod(PasswordTransformationMethod
											.getInstance());
						}
					}
				});
	}

	protected String post(String url,String json) {
		OkHttpClient client=new OkHttpClient();
		
//		RequestBody body=RequestBody.create(JSON, json);
//		
//		Request request = new Request.Builder()
//							.url(url)
//							.post(body)
//							.build();
//		try {
//			Response response = client.newCall(request).execute();
//			if(response.isSuccessful()){
//				return response.body().toString();
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return null;
//		
		//post提交键值对
//		RequestBody formBody=new FormEncodingBuilder()
//								.add("userPhone", sUserName)
//								.add("userPassWord",sPassword)
//								.build();
//		Request request=new Request.Builder()
//							.url(url)
//							.post(formBody)
//							.build();
//		Response response=null;
//		
//		try {
//			response=client.newCall(request).execute();
//			return response.body().string();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
	}

	protected String get(String url) {
		OkHttpClient client=new OkHttpClient();
		Request request= new Request.Builder()
							.url(url)
							.build();
		Response response=null;
		
		try {
			response=client.newCall(request).execute();
			return response.body().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
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

	private void startProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage("正在登录...");
		}
		progressDialog.show();
	}

	private void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	protected void onDestroy() {
		stopProgressDialog();
		
		super.onDestroy();
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
