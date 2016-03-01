package com.overtech.ems.activity.parttime.personal;

import java.io.IOException;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.PersonalAccountListAdapter;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.BillBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class PersonalAccountListActivity extends BaseActivity implements
		OnClickListener {
	private ImageView mDoBack;
	private TextView mHeadContent;
	private ListView mPersonalAccountListView;
	private TextView mHasCount;
	private TextView mNoCount;
	private PersonalAccountListAdapter adapter;
	private static final String HASCOUNT = "1";
	private static final String NOCOUNT = "0";

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.ACCOUNT_LIST_SUCCESS:
				String json = (String) msg.obj;
//				Log.e("==w我的账单==", json);
				Gson gson = new Gson();
				BillBean datas = gson.fromJson(json, BillBean.class);
				adapter = new PersonalAccountListAdapter(context,
						datas.getModel());
				mPersonalAccountListView.setAdapter(adapter);
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast("服务端异常", context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", context);
				break;
			}
			stopProgressDialog();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_personal_account_list);
		findViewById();
		initData();
		startLoading(HASCOUNT);// 默认已结算
	}

	private void startLoading(String billState) {
		startProgressDialog("正在加载...");
		Param phoneParam = new Param(Constant.LOGINNAME,
				mSharedPreferences.getString(
						SharedPreferencesKeys.CURRENT_LOGIN_NAME, null));
		Param flagParam = new Param(Constant.CLOSINGSTATE, billState);
		Request requst = httpEngine.createRequest(ServicesConfig.PERSONAL_BILL,
				phoneParam, flagParam);
		Call call = httpEngine.createRequestCall(requst);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.ACCOUNT_LIST_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request response, IOException arg1) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				handler.sendMessage(msg);
			}
		});
	}

	private void findViewById() {
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mPersonalAccountListView = (ListView) findViewById(R.id.lv_personal_account_list);
		mHasCount = (TextView) findViewById(R.id.tv_account_donet);
		mNoCount = (TextView) findViewById(R.id.tv_account_none);
	}

	private void initData() {
		mDoBack.setVisibility(View.VISIBLE);
		mHeadContent.setText("我的账单");
		context = PersonalAccountListActivity.this;
		mDoBack.setOnClickListener(this);
		mHasCount.setOnClickListener(this);
		mNoCount.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_headBack:
			finish();
			break;
		case R.id.tv_account_donet:
			startLoading(HASCOUNT);
			mHasCount.setBackgroundResource(R.drawable.horizontal_line);
			mNoCount.setBackgroundResource(R.color.main_white);
			mHasCount.setTextColor(Color.rgb(0, 163, 233));
			mNoCount.setTextColor(getResources().getColor(
					R.color.main_secondary));
			break;
		case R.id.tv_account_none:
			startLoading(NOCOUNT);
			mHasCount.setBackgroundResource(R.color.main_white);
			mNoCount.setBackgroundResource(R.drawable.horizontal_line);
			mHasCount.setTextColor(getResources().getColor(
					R.color.main_secondary));
			mNoCount.setTextColor(Color.rgb(0, 163, 233));
			break;
		default:
			break;
		}
	}
}
