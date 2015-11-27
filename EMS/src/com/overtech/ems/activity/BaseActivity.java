package com.overtech.ems.activity;

import cn.smssdk.SMSSDK;

import com.baidu.mapapi.SDKInitializer;
import com.overtech.ems.R;
import com.overtech.ems.http.HttpEngine;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.listener.BackGestureListener;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.bitmap.ImageLoader;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

/**
 * @author Tony
 * @description Activity基类(网络、百度地图、返回手势，短信验证码)
 * @date 2015-10-05
 */
public class BaseActivity extends Activity {

	public static final String ACTION_NETWORK_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	public static final String ACTION_NEW_VERSION = "apk.update.action";
	/** 手势监听 */
	GestureDetector mGestureDetector;
	/** 是否需要监听手势关闭功能 */
	private boolean mNeedBackGesture = false;
	/** 图片加载 */
	public ImageLoader imageLoader;

	public Activity activity;

	public Context context;
    //
	public FragmentManager fragmentManager;

	public SharePreferencesUtils sharePreferencesUtils;

	public OkHttpClientManager okHttpClientManager;

	public HttpEngine httpEngine;

	public CustomProgressDialog progressDialog;
	
	public NiftyDialogBuilder dialogBuilder;

	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		initGestureDetector();
		setNeedBackGesture(false);
		SMSSDK.initSDK(this, "b731c30880f4", "1c3e262449b1c77498f37c78586b8cf1");
		SDKInitializer.initialize(getApplicationContext());
		activity=this;
		context = this;
		fragmentManager=getFragmentManager();
		dialogBuilder = NiftyDialogBuilder.getInstance(this);
		//sharePreferencesUtils=SharePreferencesUtils.getInstance();
		//okHttpClientManager=OkHttpClientManager.getInstance();
		httpEngine=HttpEngine.getInstance();
		httpEngine.initContext(context);
		imageLoader = ImageLoader.getInstance();
		imageLoader.initContext(context);
		progressDialog=CustomProgressDialog.createDialog(context);
		progressDialog.setMessage(context.getString(R.string.loading_public_default));
	}


	private void initGestureDetector() {
		if (mGestureDetector == null) {
			mGestureDetector = new GestureDetector(getApplicationContext(),
					new BackGestureListener(this));
		}
	}

	/*
	 * 设置是否进行手势监听
	 */
	public void setNeedBackGesture(boolean mNeedBackGesture) {
		this.mNeedBackGesture = mNeedBackGesture;
	}

	/*
	 * 返回
	 */
	public void onBackPressed(){
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
