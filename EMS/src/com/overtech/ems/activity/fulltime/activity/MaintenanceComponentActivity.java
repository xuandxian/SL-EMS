package com.overtech.ems.activity.fulltime.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceComponentAdapter;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;

public class MaintenanceComponentActivity extends BaseActivity implements
		OnClickListener {
	private Toolbar toolbar;
	private ActionBar actionBar;
	private AppCompatTextView tvTitle;
	private RecyclerView recyclerView;
	private AppCompatButton btComplete;
	private String uid;
	private String certificate;
	private String workorderCode;
	private String isMain;
	private MaintenanceComponentActivity activity;
	private MaintenanceComponentAdapter adapter;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_maintenance_component;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initView();
		initEvent();
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("workorderCode", workorderCode);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20011, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				List<Map<String, Object>> datas = (List<Map<String, Object>>) response.body
						.get("data");
				if (adapter == null) {
					adapter = new MaintenanceComponentAdapter(activity, datas);
					recyclerView.setAdapter(adapter);
				} else {
					adapter.setData(datas);
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

	private void initEvent() {
		// TODO Auto-generated method stub
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
		workorderCode = getIntent().getStringExtra("workorderCode");
		isMain = getIntent().getStringExtra("isMain");
		Logr.e("MaintenanceComponent==workorderCode=" + workorderCode
				+ "==isMain=" + isMain);
		toolbar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stackInstance.popActivity(activity);
			}
		});
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		tvTitle.setText("配件列表");
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		btComplete.setOnClickListener(this);

	}

	private void initView() {
		// TODO Auto-generated method stub
		activity = this;
		stackInstance.pushActivity(activity);
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		btComplete = (AppCompatButton) findViewById(R.id.bt_complete);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_complete:
			Intent i = new Intent(activity, MaintenanceResponseActivity.class);
			i.putExtra("workorderCode", workorderCode);
			i.putExtra("isMain", isMain);
			startActivity(i);
			break;

		default:
			break;
		}
	}

}
