package com.overtech.ems;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
/**
 * @author Tony 
 * @description 欢迎界面
 * @date 2015-10-05 
 */
public class SplashActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this,
						LoginActivity.class);
				startActivity(intent);
				finish();
			}
		}, 3000);
	}

}
