package com.overtech.ems.activity.parttime.personal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;

public class ChangePhoneNoSuccessActivity extends BaseActivity implements OnClickListener {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private Button mNextContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_phoneno_success);
		initView();
		initEvent();
		showDialog();
	}

	private void showDialog() {
		ProgressDialog mDialog=new ProgressDialog(this);
		mDialog.setMessage("信息更新中...");
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.show();
	}

	private void initEvent() {
		mDoBack.setOnClickListener(this);
		mNextContent.setOnClickListener(this);
	}

	private void initView() {
		mHeadContent=(TextView) findViewById(R.id.tv_headTitle);
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
		mNextContent=(Button) findViewById(R.id.btn_next);
		mHeadContent.setText("更换成功");
		mDoBack.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_headBack:
			finish();
			break;
		case R.id.btn_next:
			break;
		default:
			break;
		}
	}
}
