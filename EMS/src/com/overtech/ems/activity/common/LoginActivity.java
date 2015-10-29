package com.overtech.ems.activity.common;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.photo.view.PublishActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.EditTextWithDelete;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * @author Tony
 * @description 登录界面
 * @date 2015-10-05
 */
public class LoginActivity extends BaseActivity {
	private EditTextWithDelete mUserName, mPassword;
	private String sUserName, sPassword;
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private TextView mLostPassword;
	private Button mLogin;
	private TextView mRegister;
	private ToggleButton mChangePasswordState;
	private MainFrameTask mMainFrameTask = null;
	private CustomProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setNeedBackGesture(false);// 设置需要手势监听
		findViewById();
		init();
		mLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sUserName = mUserName.getText().toString().trim();
				sPassword = mPassword.getText().toString().trim();
				if (TextUtils.isEmpty(sUserName)
						|| TextUtils.isEmpty(sPassword)) {
					Utilities.showToast("输入不能为空", context);
				} else {
					mMainFrameTask = new MainFrameTask(context);
					mMainFrameTask.execute();
				}
			}
		});
		mLostPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this,
						LostPasswordActivity.class);
				startActivity(intent);
			}
		});
		mRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
//				Intent intent = new Intent(LoginActivity.this,
//						PublishActivity.class);
				startActivity(intent);
			}
		});

		mHeadBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		mChangePasswordState
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// 设置密码为可见的
							mPassword
									.setTransformationMethod(HideReturnsTransformationMethod
											.getInstance());
						} else {
							mPassword
									.setTransformationMethod(PasswordTransformationMethod
											.getInstance());
						}
					}
				});
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mUserName = (EditTextWithDelete) findViewById(R.id.et_login_username);
		mPassword = (EditTextWithDelete) findViewById(R.id.et_login_password);
		mLostPassword = (TextView) findViewById(R.id.tv_lost_password);
		mRegister = (TextView) findViewById(R.id.tv_login_by_message);
		mLogin = (Button) findViewById(R.id.btn_login);
		mChangePasswordState = (ToggleButton) findViewById(R.id.tb_change_password);
	}

	private void init() {
		mHeadContent.setText("登 录");
		mHeadBack.setVisibility(View.VISIBLE);
	}

	public class MainFrameTask extends AsyncTask<Integer, String, Integer> {
		private Context mainFrame = null;

		public MainFrameTask(Context mainFrame) {
			this.mainFrame = mainFrame;
		}

		@Override
		protected void onCancelled() {
			stopProgressDialog();
			super.onCancelled();
		}

		@Override
		protected Integer doInBackground(Integer... params) {

			try {
				Thread.sleep(3 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			startProgressDialog();
		}

		@Override
		protected void onPostExecute(Integer result) {
			stopProgressDialog();
//			if ("15012345678".equals(sUserName)&&"123456".equals(sPassword)) {
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
//			}else {
//				Utilities.showToast("用户名或者密码错误", context);
//			}
		}
	}

	private void startProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage("正在登录...");
		}
		progressDialog.show();
	}

	private void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	protected void onDestroy() {
		stopProgressDialog();
		if (mMainFrameTask != null && !mMainFrameTask.isCancelled()) {
			mMainFrameTask.cancel(true);
		}
		super.onDestroy();
	}
}
