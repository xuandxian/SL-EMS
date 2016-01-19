package com.overtech.ems.activity.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import cn.jpush.android.api.JPushInterface;

import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.PublicKeyBean;
import com.overtech.ems.entity.bean.TaskPackageBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.http.HttpEngine;
import com.overtech.ems.http.constant.Constant;
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
	private final String AUTO_LOGIN="true";
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Gson gson = new Gson();
			switch (msg.what) {
			case StatusCode.INIT_RSA_SUCCESS:
				String json = (String) msg.obj;
				PublicKeyBean tasks = gson.fromJson(json,PublicKeyBean.class);
				String publicKey=tasks.getModel();
				Utilities.showToast("publicKey:"+publicKey, context);
				mSharedPreferences.edit().putString(SharedPreferencesKeys.PUBLIC_KEY,publicKey).commit();// 保存publicKey
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast("服务端异常", context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", context);
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
		Request request = httpEngine.createRequest(ServicesConfig.RSA_INIT);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			
			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.INIT_RSA_SUCCESS;
					msg.obj=response.body().string();
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
				}
				handler.sendMessage(msg);
			}
			
			@Override
			public void onFailure(Request request, IOException exception) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				handler.sendMessage(msg);
			}
		});
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				long lastDate=((MyApplication)getApplication()).getSharePreference().getLong(SharedPreferencesKeys.CURRENT_DATE, 0);
				if(lastDate==0){
					Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
					startActivity(intent);
					finish();
				}else {
					long currentDate=new Date().getTime();
					long period=currentDate-lastDate;
					if (period<timePeriod) {
						Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
//						Intent intent = new Intent(SplashActivity.this,TestActivity.class);
//						Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
						Bundle bundle=new Bundle();
						bundle.putString(Constant.AUTO_LOGIN,AUTO_LOGIN);
						startActivity(intent);
						finish();
					}else {
						Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
						startActivity(intent);
						finish();
					}
				}
			}
		}, 5000);
		JPushInterface.resumePush(this);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);
	}
	
}
