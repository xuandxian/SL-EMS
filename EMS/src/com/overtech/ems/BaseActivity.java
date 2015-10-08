package com.overtech.ems;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

/**
 * @author Tony
 * @description Activity基类(网络、百度地图)
 * @date 2015-10-05
 */
public class BaseActivity extends Activity {

	public static final String ACTION_NETWORK_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	public static final String ACTION_NEW_VERSION = "apk.update.action";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_NETWORK_CHANGE);
		filter.addAction(ACTION_NEW_VERSION);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 处理各种情况
			String action = intent.getAction();
			if (ACTION_NETWORK_CHANGE.equals(action)) { // 网络发生变化
				// 处理网络问题
			} else if (ACTION_NEW_VERSION.equals(action)) {// 检测到新版本

			}
		}
	};
}
