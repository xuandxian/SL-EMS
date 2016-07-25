package com.overtech.ems.activity.parttime.tasklist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.http.OkHttpClientManager.ResultCallback;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Request;

public class QuestionResponseActivity extends BaseActivity implements
		OnClickListener {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private Button mConfirm;
	private EditText mFeedbackInfo;
	private String feedBackInfo;
	private String taskNo;
	private String elevatorNo;
	private String uid;
	private String certificate;
	private QuestionResponseActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_response);
		init();
		initEvent();
	}

	private void initEvent() {
		mDoBack.setOnClickListener(this);
		mConfirm.setOnClickListener(this);
	}

	private void init() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mConfirm = (Button) findViewById(R.id.bt_confirm);
		mFeedbackInfo = (EditText) findViewById(R.id.et_question_response);
		mHeadContent.setText("问题反馈");
		mDoBack.setVisibility(View.VISIBLE);
		taskNo = getIntent().getExtras().getString(Constant.TASKNO, "");
		elevatorNo=getIntent().getExtras().getString(Constant.ELEVATORNO);
		activity = this;
		stackInstance.pushActivity(activity);
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_headBack:
			stackInstance.popActivity(activity);
			break;
		case R.id.bt_confirm:
			feedBackInfo = mFeedbackInfo.getText().toString();
//			if (TextUtils.isEmpty(feedBackInfo)) {
//				Intent intent = new Intent(QuestionResponseActivity.this,
//						TaskListPackageDetailActivity.class);
//				Bundle bundle = new Bundle();
//				bundle.putString(Constant.TASKNO, taskNo);
//				bundle.putString(Constant.ELEVATORNO, elevatorNo);
//				intent.putExtras(bundle);
//				startActivity(intent);
//				stackInstance.popActivity(activity);
//			} else {
			startLoading();
//			}
			break;

		default:
			break;
		}
	}

	private void startLoading() {
		startProgressDialog(getResources().getString(R.string.loading_public_default));
		Requester requester = new Requester();
		requester.uid = uid;
		requester.certificate = certificate;
		requester.cmd = 20057;
		requester.body.put(Constant.TASKNO, taskNo);
		requester.body.put(Constant.FEEDBACKINFO, feedBackInfo);
		ResultCallback<Bean> callback=new ResultCallback<Bean>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				stopProgressDialog();
			}

			@Override
			public void onResponse(Bean response) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				if (response == null) {
					Utilities.showToast(R.string.response_no_object, activity);
					return;
				}
				int st = response.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(response.msg, activity);
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(activity, LoginActivity.class);
					startActivity(intent);
					return;
				} else if (st == 1) {
					Utilities.showToast(response.msg, activity);
					return;
				} else {
					Intent intent = new Intent(QuestionResponseActivity.this,
							TaskListPackageDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString(Constant.TASKNO, taskNo);
					bundle.putString(Constant.ELEVATORNO, elevatorNo);
					intent.putExtras(bundle);
					startActivity(intent);
					stackInstance.popActivity(activity);
				}
			}
			
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback, gson.toJson(requester));
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}
}
