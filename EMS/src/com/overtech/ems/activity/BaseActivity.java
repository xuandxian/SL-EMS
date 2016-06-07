package com.overtech.ems.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.http.HttpEngine;
import com.overtech.ems.utils.StackManager;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.bitmap.ImageLoader;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;

/**
 * @author Tony
 * @description Activity基类(网络、百度地图、返回手势，短信验证码)
 * @date 2015-10-05
 */
public class BaseActivity extends FragmentActivity {

	public static final String ACTION_NETWORK_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	public static final String ACTION_NEW_VERSION = "apk.update.action";

	public MyApplication application;

	public ImageLoader imageLoader;

	public Activity activity;

	public Context context;

	public FragmentManager fragmentManager;

	public HttpEngine httpEngine;

	public CustomProgressDialog progressDialog;

	public NiftyDialogBuilder dialogBuilder;

	public SharedPreferences mSharedPreferences;

	public StackManager statckInstance;

	private InputMethodManager imm;

	public Gson gson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		application = (MyApplication) this.getApplication();
		activity = this;
		context = this;
		fragmentManager = getSupportFragmentManager();
		dialogBuilder = NiftyDialogBuilder.getInstance(this);
		// sharePreferencesUtils=SharePreferencesUtils.getInstance();
		httpEngine = HttpEngine.getInstance();
		httpEngine.initContext(context);
		imageLoader = ImageLoader.getInstance();
		imageLoader.initContext(context);
		progressDialog = CustomProgressDialog.createDialog(context);
		progressDialog.setMessage(context
				.getString(R.string.loading_public_default));
		progressDialog.setCanceledOnTouchOutside(false);
		mSharedPreferences = application.getSharePreference();
		statckInstance = StackManager.getStackManager();
		if (gson == null) {
			gson = new Gson();
		}
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

		ImageView imageView = (ImageView) progressDialog
				.findViewById(R.id.loadingImageView);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView
				.getBackground();
		animationDrawable.start();
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
