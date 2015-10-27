package com.overtech.ems.activity.common;

import com.overtech.ems.R;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ResetPasswordActivity extends Activity {
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private Button mResetPassword;
	private EditTextWithDelete mPasswordNew;
	private EditTextWithDelete mPasswordConfirm;
	private String sPasswordNew;
	private String sPasswordConfirm;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reset_password);
		findViewById();
		init();
	}

	private void findViewById() {
		context=ResetPasswordActivity.this;
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mResetPassword = (Button) findViewById(R.id.btn_reset_password);
		mPasswordNew=(EditTextWithDelete)findViewById(R.id.et_reset_password_new);
		mPasswordConfirm=(EditTextWithDelete)findViewById(R.id.et_reset_password_confirm);
	}

	private void init() {
		mHeadContent.setText("密码重置");
		mHeadBack.setVisibility(View.VISIBLE);
		mResetPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sPasswordNew=mPasswordNew.getText().toString().trim();
				sPasswordConfirm=mPasswordConfirm.getText().toString().trim();
				if (TextUtils.isEmpty(sPasswordNew)||TextUtils.isEmpty(sPasswordConfirm)) {
					Utilities.showToast("输入不能为空", context);
				}else {
					if (sPasswordNew.length()>=6&&sPasswordNew.length()<=18) {
						if (TextUtils.equals(sPasswordNew, sPasswordConfirm)) {
							Intent intent = new Intent(ResetPasswordActivity.this,
									ResetPasswordSuccessActivity.class);
							startActivity(intent);
						}else {
							Utilities.showToast("两次输入不相同", context);
						}
					}else {
						Utilities.showToast("密码长度为6—18位", context);
					}
				}
			}
		});
		mHeadBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
}
