package com.overtech.ems.activity.parttime.personal.notice;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.AnnouncementBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * 公告详情
 * 
 */
public class PersonalNoticeDetailActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private TextView mAnnouncementContent;
	private TextView mAnnouncementTheme;
	private TextView mAnnouncementDate;
	private TextView mAnnouncementSummary;
	private PersonalNoticeDetailActivity activity;
	private String uid;
	private String certificate;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.ANNOUNCEMENT_SUCCESS:
				String json = (String) msg.obj;
				AnnouncementBean bean = gson.fromJson(json,
						AnnouncementBean.class);
				int st = bean.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(bean.msg, activity);
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(activity, LoginActivity.class);
					startActivity(intent);
					return;
				}
				mAnnouncementContent.setText("\u3000\u3000"
						+ bean.body.get("content").toString());
				mAnnouncementDate.setText(bean.body.get("releaseDate")
						.toString());
				mAnnouncementSummary.setText(bean.body.get("summary")
						.toString());
				mAnnouncementTheme.setText(bean.body.get("theme").toString());
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast((String) msg.obj,
						PersonalNoticeDetailActivity.this);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast((String) msg.obj,
						PersonalNoticeDetailActivity.this);
				break;
			default:
				break;
			}
			stopProgressDialog();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_announcement_detail);
		activity = this;
		stackInstance.pushActivity(activity);
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
		initView();
		initData();
	}

	private void initData() {
		startProgressDialog("正在加载中...");
		String id = getIntent().getExtras().getString(Constant.ANNOUNCEMENTID);
		Requester requester = new Requester();
		requester.cmd = 20078;
		requester.uid = uid;
		requester.certificate = certificate;
		Request request = httpEngine.createRequest(SystemConfig.NEWIP,
				gson.toJson(requester));
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.ANNOUNCEMENT_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
					msg.obj = "服务器君歇菜了，请稍后";
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request arg0, IOException arg1) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				msg.obj = "网络异常，请检查";
				handler.sendMessage(msg);
			}
		});
	}

	private void initView() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mAnnouncementContent = (TextView) findViewById(R.id.tv_announcement_content);
		mAnnouncementTheme = (TextView) findViewById(R.id.tv_announcement_theme);
		mAnnouncementDate = (TextView) findViewById(R.id.tv_announcement_date);
		mAnnouncementSummary = (TextView) findViewById(R.id.tv_announcement_summary);
		mHeadContent.setText("公告详情");
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				stackInstance.popActivity(activity);
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}
}
