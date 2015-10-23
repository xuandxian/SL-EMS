package com.overtech.ems.activity.common;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.EditTextWithDelete;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ResetPasswordActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private Button mResetPassword;
	private EditTextWithDelete mResetNewPassword;
	private EditTextWithDelete mResetConfirmPassword;
	private String sNewPassword;
	private String sConfirmPasword;
	private MainFrameTask mMainFrameTask = null;
	private CustomProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);
		findViewById();
		init();
		mResetPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sNewPassword=mResetNewPassword.getText().toString().trim();
				sConfirmPasword=mResetConfirmPassword.getText().toString().trim();
				if (TextUtils.isEmpty(sNewPassword)||TextUtils.isEmpty(sConfirmPasword)) {
					Utilities.showToast("输入不能为空", context);
				}else {
					if (sNewPassword.length()<6) {
						Utilities.showToast("密码长度小于6位，请重新输入", context);
					}else {
						if (TextUtils.equals(sNewPassword, sConfirmPasword)) {
							mMainFrameTask = new MainFrameTask(context);
							mMainFrameTask.execute();
						}else {
							Utilities.showToast("两次密码输入不正确", context);
						}
					}
				}
			}
		});
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mResetPassword = (Button) findViewById(R.id.btn_reset_password);
		mResetNewPassword=(EditTextWithDelete)findViewById(R.id.et_reset_password_new);
		mResetConfirmPassword=(EditTextWithDelete)findViewById(R.id.et_reset_password_confirm);
	}

	private void init() {
		mHeadContent.setText("密码重置");
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
			Intent intent = new Intent(ResetPasswordActivity.this,
					ResetPasswordSuccessActivity.class);
			startActivity(intent);
		}
	}
	private void startProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage("正在更新...");
		}
		progressDialog.show();
	}

	private void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}
