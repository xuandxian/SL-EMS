package com.overtech.ems.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

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
public abstract class BaseActivity extends AppCompatActivity {

	public static final String ACTION_NETWORK_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	public static final String ACTION_NEW_VERSION = "apk.update.action";

	public ImageLoader imageLoader;

	public FragmentManager fragmentManager;

	public HttpEngine httpEngine;

	public CustomProgressDialog progressDialog;

	public ProgressDialog dialog;

	public NiftyDialogBuilder dialogBuilder;

	public StackManager stackInstance;

	private InputMethodManager imm;

	public Gson gson;

	public AlertDialog.Builder alertBuilder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setStatusBar();
		setContentView(getLayoutResIds());

		ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
		View parentView = contentFrameLayout.getChildAt(0);
		if (parentView != null
				&& Build.VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			parentView.setFitsSystemWindows(true);
		}
		fragmentManager = getSupportFragmentManager();
		dialogBuilder = NiftyDialogBuilder.getInstance(this);
		// sharePreferencesUtils=SharePreferencesUtils.getInstance();
		httpEngine = HttpEngine.getInstance();
		imageLoader = ImageLoader.getInstance();
		imageLoader.initContext(getApplicationContext());
		if (dialog == null
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			dialog = new ProgressDialog(this);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setIndeterminate(true);
		} else {
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage(this
					.getString(R.string.loading_public_default));
			progressDialog.setCanceledOnTouchOutside(false);
		}
		stackInstance = StackManager.getStackManager();
		if (gson == null) {
			gson = new Gson();
		}
		if (alertBuilder == null) {
			alertBuilder = new AlertDialog.Builder(this).setCancelable(false);
		}
		afterCreate(savedInstanceState);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void setStatusBar() {
		// TODO Auto-generated method stub
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
		}
	}

	protected abstract int getLayoutResIds();

	protected abstract void afterCreate(Bundle savedInstanceState);

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
		if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
			dialog.setMessage(content);
			dialog.show();
		} else {
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
	}

	public void stopProgressDialog() {
		if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
			if (dialog != null) {
				dialog.dismiss();
			}
		} else {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
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
