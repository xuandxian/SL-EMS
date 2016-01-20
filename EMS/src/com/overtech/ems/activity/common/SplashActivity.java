package com.overtech.ems.activity.common;

import java.util.Date;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import cn.jpush.android.api.JPushInterface;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.utils.SharedPreferencesKeys;

/**
 * @author Tony
 * @description 欢迎界面
 * @date 2015-10-05
 */
public class SplashActivity extends BaseActivity {
	
//	private final long timePeriod=2592000000L; //30天的毫秒数
	private final long timePeriod=60000L;      //60秒（测试）
	
	
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
		super.onResume();
		JPushInterface.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}
	
}
