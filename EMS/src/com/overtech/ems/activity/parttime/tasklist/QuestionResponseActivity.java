package com.overtech.ems.activity.parttime.tasklist;

import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.AppUtils;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;

public class QuestionResponseActivity extends BaseActivity implements
		OnClickListener {
	private Toolbar toolbar;
	private ActionBar actionBar;
	private AppCompatTextView tvTitle;
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
		tvTitle.setText("问题反馈");
		toolbar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stackInstance.popActivity(activity);
			}
		});
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
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
			public void bizStIs1Deal(Bean response) {
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
		stackInstance.pushActivity(activity);
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
		btConfirm = (Button) findViewById(R.id.bt_confirm);
		etFeedbackInfo = (EditText) findViewById(R.id.et_question_response);
		sTaskNo = getIntent().getExtras().getString(Constant.TASKNO);
		sElevatorNo = getIntent().getExtras().getString(Constant.ELEVATORNO);
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
				if(AppUtils.isValidTagAndAlias(sFeedBackInfo)){
					startLoading();
				}else{
					Utilities.showToast("输入数据不合法", activity);
				}
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
		body.put(Constant.ELEVATORNO, sElevatorNo);
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
				Utilities.showToast("提交成功", activity);
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
			public void bizStIs1Deal(Bean response) {
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
