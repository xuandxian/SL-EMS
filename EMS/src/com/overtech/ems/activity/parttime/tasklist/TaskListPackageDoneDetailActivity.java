package com.overtech.ems.activity.parttime.tasklist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.TaskListPackageDoneDetailAdapter;
import com.overtech.ems.activity.adapter.TaskListPackageDoneDetailAdapter.OnItemClickListener;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;

public class TaskListPackageDoneDetailActivity extends BaseActivity implements
		OnRefreshListener {
	private AppCompatTextView tvTitle;
	private Toolbar toolbar;
	private ActionBar actionBar;
	private SwipeRefreshLayout swipeRefresh;
	private RecyclerView recyclerView;
	private TaskListPackageDoneDetailActivity activity;
	private String sTaskNo;
	private String sTaskPackageName;
	private String uid;
	private String certificate;
	private TaskListPackageDoneDetailAdapter adapter;
	protected List<Map<String, Object>> data;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_tasklist_package_done_detail;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = this;
		stackInstance.pushActivity(activity);
		initView();
		initEvent();
	}

	private void initEvent() {
		// TODO Auto-generated method stub

		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
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
		tvTitle.setText(sTaskPackageName);
		swipeRefresh.setColorSchemeResources(R.color.colorPrimary,
				R.color.colorPrimary30);
		swipeRefresh.setOnRefreshListener(this);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		swipeRefresh.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				swipeRefresh.setRefreshing(true);
			}
		});
		onRefresh();
	}

	private void initView() {
		// TODO Auto-generated method stub
		sTaskNo = getIntent().getStringExtra(Constant.TASKNO);
		sTaskPackageName=getIntent().getStringExtra("taskPackageName");
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("taskNo", sTaskNo);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20066, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				data = (List<Map<String, Object>>) response.body.get("data");
				if (adapter == null) {
					adapter = new TaskListPackageDoneDetailAdapter(activity,
							data);
					adapter.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onButtonClick(View v, int position) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(activity,
									QuestionResponseActivity.class);
							intent.putExtra(Constant.TASKNO, sTaskNo);
							intent.putExtra(Constant.ELEVATORNO,
									data.get(position).get("elevatorNo").toString());
							startActivityForResult(intent, 123);
						}
					});
					recyclerView.setAdapter(adapter);

				} else {
					adapter.setData(data);
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
				if (swipeRefresh.isRefreshing()) {
					swipeRefresh.setRefreshing(false);
				}
			}
		};
		conn.sendRequest();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == Activity.RESULT_OK) {
			onRefresh();
		}
	}

}
