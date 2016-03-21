package com.overtech.ems.activity.common;

import java.io.IOException;
import java.util.Date;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.WindowManager;
import cn.jpush.android.api.JPushInterface;

import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.Employee;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * @author Tony
 * @description 欢迎界面
 * @date 2015-10-05
 */
public class SplashActivity extends BaseActivity {
	
	private final long timePeriod=2592000000L; //30天的毫秒数
//	private final long timePeriod=60000L;      //60秒（测试）
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.LOGIN_SUCCESS:
				Intent intent = new Intent(SplashActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast("服务端异常", context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", context);
				break;
			case StatusCode.LOGIN_NOT_EXIST:
				Utilities.showToast("用户或者密码错误", context);
				break;
			}
			stopProgressDialog();
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				long lastDate=application.getSharePreference().getLong(SharedPreferencesKeys.CURRENT_DATE, 0);
				if(lastDate==0){
					Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
					startActivity(intent);
					finish();
				}else {
					long currentDate=new Date().getTime();
					long period=currentDate-lastDate;
					if (period<timePeriod) {
						Intent intent = new Intent(SplashActivity.this,MainActivity.class);
						startActivity(intent);
						finish();
					}else {
						String username=mSharedPreferences.getString(SharedPreferencesKeys.CURRENT_LOGIN_NAME,null);
						String password=mSharedPreferences.getString(SharedPreferencesKeys.CURRENT_LOGIN_PASSWORD,null);
						doLogin(username,password);
					}
				}
			}
		}, 5000);
		JPushInterface.resumePush(this);
	}
	
	private void doLogin(String username,String password){
		startProgressDialog("正在登录...");
		Employee employee = new Employee();
		employee.setLoginName(username);
		employee.setPassword(password);
		Gson gson = new Gson();
		String person = gson.toJson(employee);
		Request request = httpEngine.createRequest(ServicesConfig.LOGIN, person);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
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
						msg.what = StatusCode.LOGIN_SUCCESS;
					} else {
						msg.what = StatusCode.LOGIN_NOT_EXIST;
					}
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
				}
				handler.sendMessage(msg);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}
	
}
