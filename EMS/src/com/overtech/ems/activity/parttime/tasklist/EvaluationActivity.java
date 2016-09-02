package com.overtech.ems.activity.parttime.tasklist;

import java.util.HashMap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;

public class EvaluationActivity extends BaseActivity implements OnClickListener {
	private TextView tvHeadContent;
	private ImageView ivBack;
	private Button btConfirm;
	private RadioGroup rgChecked;
	private EditText etContent;
	private SwitchCompat swParteners;
	private String uid;
	private String certificate;
	private String sEvaluateLevel;
	private String sEvaluateInfo;
	private String taskNo;
	private String isPartner;// 是否是搭档
	private EvaluationActivity activity;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_evaluation;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		certificate = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.CERTIFICATED, "");
		uid = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.UID, "");
		init();
		initEvent();
	}

	private void initEvent() {
		ivBack.setOnClickListener(this);
		btConfirm.setOnClickListener(this);
		swParteners.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					alertBuilder
							.setTitle("温馨提示")
							.setMessage("您确定要取消收藏该搭档？")
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											swParteners.setChecked(true);
										}
									})
							.setPositiveButton("确认",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											updatePartnersChange("1");
										}
									}).show();
				} else {
					updatePartnersChange("0");
				}
			}
		});
		updatePartnersChange(null);

	}

	private void updatePartnersChange(String status) {
		startProgressDialog("加载中...");
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put(Constant.TASKNO, taskNo);
		body.put("isPartner", status);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20059, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				isPartner = response.body.get("isPartner").toString();
				if (TextUtils.equals(isPartner, "1")) {
					swParteners.setChecked(true);
				} else if (TextUtils.equals(isPartner, "0")) {
					swParteners.setChecked(false);
				}
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub
			}

			@Override
			public void bizStIs1Deal() {
				// TODO Auto-generated method stub
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
			}
		};
		conn.sendRequest();
	}

	private void init() {
		tvHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		ivBack = (ImageView) findViewById(R.id.iv_headBack);
		swParteners = (SwitchCompat) findViewById(R.id.swPartner);
		btConfirm = (Button) findViewById(R.id.bt_confirm);
		rgChecked = (RadioGroup) findViewById(R.id.rg_container);
		etContent = (EditText) findViewById(R.id.et_evaluation);
		tvHeadContent.setText("互相评价");
		ivBack.setVisibility(View.VISIBLE);
		taskNo = getIntent().getExtras().getString(Constant.TASKNO);
		activity = this;
		stackInstance.pushActivity(activity);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_headBack:
			stackInstance.popActivity(activity);
			break;
		case R.id.bt_confirm:
			upLoading();
			break;
		default:
			break;
		}
	}

	private void startLoading() {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put(Constant.TASKNO, taskNo);
		body.put(Constant.EVALUATELEVEL, sEvaluateLevel);
		body.put(Constant.EVALUATEINFO, sEvaluateInfo);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20058, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(activity, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("flag", "1");
				startActivity(intent);
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal() {
				// TODO Auto-generated method stub

			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
			}
		};
		conn.sendRequest();
	}

	private void upLoading() {
		sEvaluateInfo = etContent.getText().toString().trim();
		int rbId = rgChecked.getCheckedRadioButtonId();
		switch (rbId) {
		case R.id.rb_verygood:
			sEvaluateLevel = "3";
			break;
		case R.id.rb_sogood:
			sEvaluateLevel = "2";
			break;
		case R.id.rb_good:
			sEvaluateLevel = "1";
			break;
		case R.id.rb_notbad:
			sEvaluateLevel = "0";
			break;
		case R.id.rb_bad:
			if (TextUtils.isEmpty(sEvaluateInfo)) {
				Utilities.showToast("请告诉我们您选择差评的原因", this);
				return;
			} else if (sEvaluateInfo.length() < 15) {
				Utilities.showToast("输入的字符少于15个", this);
				return;
			}
			sEvaluateLevel = "-2";
			break;
		case RadioGroup.NO_ID:
			Utilities.showToast("您还没有选择分值", activity);
			return;
		}
		startLoading();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}

}
