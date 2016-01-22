package com.overtech.ems.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import cn.smssdk.SMSSDK;
import com.baidu.mapapi.SDKInitializer;
import com.overtech.ems.R;
import com.overtech.ems.http.HttpEngine;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.bitmap.ImageLoader;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;

/**
 * @author Tony
 * @description Activity基类(网络、百度地图、返回手势，短信验证码)
 * @date 2015-10-05
 */
public class BaseActivity extends Activity {

	public static final String ACTION_NETWORK_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	public static final String ACTION_NEW_VERSION = "apk.update.action";
	
	public MyApplication application;
	
	public ImageLoader imageLoader;

	public Activity activity;

	public Context context;

	public FragmentManager fragmentManager;

	public OkHttpClientManager okHttpClientManager;

	public HttpEngine httpEngine;

	public CustomProgressDialog progressDialog;

	public NiftyDialogBuilder dialogBuilder;

	public SharedPreferences mSharedPreferences;

	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		SMSSDK.initSDK(this, "b731c30880f4", "1c3e262449b1c77498f37c78586b8cf1");
		SDKInitializer.initialize(getApplicationContext());
		application=(MyApplication)this.getApplication();
		activity = this;
		context = this;
		fragmentManager = getFragmentManager();
		dialogBuilder = NiftyDialogBuilder.getInstance(this);
		// sharePreferencesUtils=SharePreferencesUtils.getInstance();
		httpEngine = HttpEngine.getInstance();
		httpEngine.initContext(context);
		imageLoader = ImageLoader.getInstance();
		imageLoader.initContext(context);
		progressDialog = CustomProgressDialog.createDialog(context);
		progressDialog.setMessage(context.getString(R.string.loading_public_default));
		progressDialog.setCanceledOnTouchOutside(false);
		mSharedPreferences = application.getSharePreference();
	}
	
	/*
	 * 返回
	 */
	public void onBackPressed() {
		super.onBackPressed();
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public void startProgressDialog(String content) {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this);
		}
		progressDialog.setMessage(content);
		progressDialog.show();
	}

	public void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_NETWORK_CHANGE.equals(action)) {
				boolean isNetworkConnected = Utilities
						.isNetworkConnected(context);
				if (!isNetworkConnected) {
					Utilities.showToast("无网络连接，请检查网络！！", context);
				}
			} else if (ACTION_NEW_VERSION.equals(action)) {

			}
		}
	};

	/** 隐藏键盘 **/
	public void hideSoftInput() {
		if (imm == null) {
			imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		}
		if (imm != null) {
			imm.hideSoftInputFromWindow(getWindow().getDecorView()
					.getWindowToken(), 0);
		}
	}
}
