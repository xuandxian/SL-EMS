package com.overtech.ems.activity.common;

import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import cn.jpush.android.api.JPushInterface;

import com.overtech.ems.R;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.http.HttpEngine;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * @author Tony
 * @description 欢迎界面
 * @date 2015-10-05
 */
public class SplashActivity extends Activity {
	
	private final long timePeriod=2592000000L; //30天的毫秒数
//	private final long timePeriod=60000L;      //60秒（测试）
	private HttpEngine httpEngine;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);
		httpEngine=HttpEngine.getInstance();
		httpEngine.initContext(this);
		Request request = httpEngine.createRequest(ServicesConfig.RSA_INIT);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			
			@Override
			public void onResponse(Response response) throws IOException {
				Log.e("RSA:", response.body().string());
			}
			
			@Override
			public void onFailure(Request request, IOException exception) {
				Log.e("SplashActivity", "onFailure");
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
						Intent intent = new Intent(SplashActivity.this,MainActivity.class);
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
