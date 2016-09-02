package com.overtech.ems.activity.parttime.tasklist;

import java.util.HashMap;

import android.content.Context;
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
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;

public class QuestionResponseActivity extends BaseActivity implements
		OnClickListener {
	private TextView tvHeadContent;
	private ImageView ivBack;
	private Button btConfirm;
	private EditText etFeedbackInfo;
	private String sFeedBackInfo;
	private String sTaskNo;
	private String sElevatorNo;
	private String uid;
	private String certificate;
	private QuestionResponseActivity activity;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_question_response;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		init();
		initEvent();
	}

	private void initEvent() {
		ivBack.setOnClickListener(this);
		btConfirm.setOnClickListener(this);

		startProgressDialog("加载中...");
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put(Constant.TASKNO, sTaskNo);
		body.put(Constant.ELEVATORNO, sElevatorNo);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20063, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				sFeedBackInfo = response.body.get("feedbackInfo").toString();
				if (!TextUtils.isEmpty(sFeedBackInfo)) {
					etFeedbackInfo.setText(sFeedBackInfo);
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
		activity = this;
		tvHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		ivBack = (ImageView) findViewById(R.id.iv_headBack);
		btConfirm = (Button) findViewById(R.id.bt_confirm);
		etFeedbackInfo = (EditText) findViewById(R.id.et_question_response);
		tvHeadContent.setText("问题反馈");
		ivBack.setVisibility(View.VISIBLE);
		sTaskNo = getIntent().getExtras().getString(Constant.TASKNO);
		sElevatorNo = getIntent().getExtras().getString(Constant.ELEVATORNO);
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
			sFeedBackInfo = etFeedbackInfo.getText().toString();
			if (TextUtils.isEmpty(sFeedBackInfo)) {
				// Intent intent = new Intent(QuestionResponseActivity.this,
				// TaskListPackageDetailActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putString(Constant.TASKNO, sTaskNo);
				// bundle.putString(Constant.ELEVATORNO, sElevatorNo);
				// intent.putExtras(bundle);
				// startActivity(intent);
				setResult(RESULT_OK);
				stackInstance.popActivity(activity);
			} else {
				startLoading();
			}
			break;

		default:
			break;
		}
	}

	private void startLoading() {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put(Constant.TASKNO, sTaskNo);
		body.put(Constant.FEEDBACKINFO, sFeedBackInfo);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20057, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent(QuestionResponseActivity.this,
				// TaskListPackageDetailActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putString(Constant.TASKNO, sTaskNo);
				// // bundle.putString(Constant.ELEVATORNO, sElevatorNo);任务列表不需要
				// intent.putExtras(bundle);
				// startActivity(intent);
				setResult(RESULT_OK);
				stackInstance.popActivity(activity);
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}

}
