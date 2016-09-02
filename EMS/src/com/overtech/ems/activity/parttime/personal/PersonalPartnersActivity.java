package com.overtech.ems.activity.parttime.personal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.PersonalPartnersAdapter;
import com.overtech.ems.activity.adapter.PersonalPartnersAdapter.OnPartnersDeleteListener;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;

public class PersonalPartnersActivity extends BaseActivity implements
		OnRefreshListener {
	private Toolbar toolBar;
	private ActionBar actionBar;
	private AppCompatTextView tvHead;
	private SwipeRefreshLayout swipeRefresh;
	private RecyclerView recyclerView;
	private PersonalPartnersActivity activity;
	private PersonalPartnersAdapter adapter;
	private List<Map<String, Object>> datas;
	private String uid;
	private String certificate;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_personal_partners;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = this;
		stackInstance.pushActivity(activity);
		init();
		initEvent();
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		setSupportActionBar(toolBar);
		toolBar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stackInstance.popActivity(activity);
			}
		});
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDefaultDisplayHomeAsUpEnabled(true);
		tvHead.setText("搭档收藏");
		swipeRefresh.setColorSchemeColors(R.color.material_deep_teal_200,
				R.color.material_deep_teal_500);
		swipeRefresh.setOnRefreshListener(this);
		swipeRefresh.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				swipeRefresh.setRefreshing(true);
			}
		});
		onRefresh();
		LinearLayoutManager layout = new LinearLayoutManager(activity,
				LinearLayoutManager.VERTICAL, false);
		recyclerView.setLayoutManager(layout);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
	}

	private void init() {
		// TODO Auto-generated method stub
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");

		toolBar = (Toolbar) findViewById(R.id.toolBar);
		tvHead = (AppCompatTextView) findViewById(R.id.tvTitle);
		swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20079, uid,
				certificate, null) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub

				datas = (List<Map<String, Object>>) response.body.get("data");
				if (adapter == null) {
					adapter = new PersonalPartnersAdapter(activity, datas);
					adapter.setOnPartnerDeleteListener(new OnPartnersDeleteListener() {

						@Override
						public void delete(View v) {
							// TODO Auto-generated method stub
							final int position = (Integer) v.getTag();
							alertBuilder
									.setTitle("温馨提示")
									.setMessage("您确认要删除搭档吗？")
									.setNegativeButton(
											"取消",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// TODO Auto-generated
													// method stub
													deletePartener(position);
												}
											})
									.setPositiveButton(
											"确认",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// TODO Auto-generated
													// method stub

												}
											}).show();
						}
					});
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
			public void bizStIs1Deal() {
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

	public void deletePartener(final int position) {
		startProgressDialog("删除中...");
		Map<String, Object> p = adapter.getData(position);
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("uid", body.get("uid").toString());
		body.put("isPartner", "1");
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
				adapter.deleteData(position);
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

}
