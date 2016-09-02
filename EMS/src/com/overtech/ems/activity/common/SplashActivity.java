package com.overtech.ems.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import cn.jpush.android.api.JPushInterface;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;

/**
 * @author Will
 * @description 欢迎界面
 * @date 2016-6-14
 */
public class SplashActivity extends BaseActivity {
	private String uid;
	private String certificate;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.splash;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=getWindow().getDecorView();
		int option=View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		view.setSystemUiVisibility(option);
		
		uid = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.CERTIFICATED, "");
		if (!((MyApplication) getApplication()).locationService.isStarted()) {
			Logr.e("地图还没开始定位");
			((MyApplication) getApplication()).locationService.start();
		}
		((MyApplication) getApplication()).locationService.requestLocation();

		Logr.e("uid==" + uid + "==certificate==" + certificate);
		JPushInterface.resumePush(this);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(certificate)) {
					Logr.e("跳转Login");
					Intent intent = new Intent(SplashActivity.this,
							LoginActivity.class);
					startActivity(intent);
					finish();
				} else {
					Logr.e("跳转Main");
					Intent intent = new Intent(SplashActivity.this,
							MainActivity.class);
					startActivity(intent);
					finish();
				}
			}
		}, 3000);

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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
	}

}
