package com.overtech.ems.activity.fulltime.activity;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.fulltime.activity.fragment.MaintenanceHasChooseFragment;
import com.overtech.ems.activity.fulltime.activity.fragment.MaintenanceReportFragment;
import com.overtech.ems.activity.fulltime.activity.fragment.MaintenanceReportFragment.OnNextClickListener;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;

/**
 * 维修报告单一级目录
 * 
 * @author Overtech Will
 * 
 */
public class MaintenanceTaskActivity extends BaseActivity implements
		OnNextClickListener {
	private Toolbar toolbar;
	private ActionBar actionBar;
	private AppCompatTextView tvTitle;
	private Activity activity;
	private MaintenanceReportFragment reportFrag;
	private MaintenanceHasChooseFragment hasChooseFrag;
	private String uid;
	private String certificate;
	private String elevatorNo;
	private String workorderCode;
	private String siteTel;
	private String isMain;
	private String hasReport;
	private String hasChooseComponent;
	public static final int REQUESTCODE = 0x001232;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_maintenancetask;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = this;
		stackInstance.pushActivity(activity);
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
		initView();
		initEvent();
		getExtraData();
		if (!TextUtils.isEmpty(elevatorNo)) {// 通过扫码进来
			initData();
		} else {
			initHasChooseFrag();
		}
	}

	private void getExtraData() {
		// TODO Auto-generated method stub
		elevatorNo = getIntent().getStringExtra(Constant.ELEVATORNO);
		workorderCode = getIntent().getStringExtra("workorderCode");
		siteTel = getIntent().getStringExtra("siteTel");
		hasReport = getIntent().getStringExtra("hasReport");
		hasChooseComponent = getIntent().getStringExtra("hasChooseComponent");
		isMain = getIntent().getStringExtra("isMain");

		Logr.e("MaintenanceTask==elevatorNo=" + elevatorNo + "=workorderCode="
				+ workorderCode + "=siteTel=" + siteTel + "=hasReport="
				+ hasReport + "=hasChoosereport=" + hasReport);
	}

	private void initData() {
		// TODO Auto-generated method stub
		startProgressDialog("加载中...");
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("qrcode", elevatorNo);
		HttpConnector<Bean> connector = new HttpConnector<Bean>(20004, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				isMain = response.body.get("isMain").toString();
				siteTel = response.body.get("siteTel").toString();
				hasReport = response.body.get("hasReport").toString();
				hasChooseComponent = response.body.get("hasChooseComponent")
						.toString();
				if (TextUtils.equals(hasReport, "1")) {
					initHasChooseFrag();
				} else {
					initReportFrag();
				}
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal() {
				// TODO Auto-generated method stub
				stackInstance.popActivity(activity);
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
			}
		};
		connector.sendRequest();
	}

	private void initReportFrag() {
		// TODO Auto-generated method stub
		if (reportFrag == null) {
			reportFrag = new MaintenanceReportFragment();
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.add(R.id.fl_container, reportFrag);
			ft.addToBackStack(reportFrag.getClass().getSimpleName());
			ft.commit();
			reportFrag.setOnNextClickListener(this);
		}
	}

	private void initHasChooseFrag() {
		if (hasChooseFrag == null) {
			hasChooseFrag = new MaintenanceHasChooseFragment();
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.add(R.id.fl_container, hasChooseFrag);
			ft.addToBackStack(hasChooseComponent.getClass().getSimpleName());
			ft.commit();
		}
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		tvTitle.setText("维修");
		toolbar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (fragmentManager.getBackStackEntryCount() == 1) {
					stackInstance.popActivity(activity);
				} else {
					onBackPressed();
				}
			}
		});
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
	}

	private void initView() {
		// TODO Auto-generated method stub
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.menu_maintenance_task, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_site_tel:
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ siteTel));
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Logr.e("MaintenanceTask=====getBackStackEntryCount=="
				+ fragmentManager.getBackStackEntryCount());
		if (fragmentManager.getBackStackEntryCount() == 1) {
			stackInstance.popActivity(activity);
		}
	}

	public String getUid() {
		return uid;
	}

	public String getCertificate() {
		return certificate;
	}

	public String getWorkorderCode() {
		return workorderCode;
	}

	public String getIsMain() {
		return isMain;
	}

	public String getHasReport() {
		return hasReport;
	}

	public String getHasChooseComponent() {
		return hasChooseComponent;
	}

	@Override
	public void onNextClick(String json) {
		// TODO Auto-generated method stub
		if (hasChooseFrag == null) {
			hasChooseFrag = MaintenanceHasChooseFragment.newInstance(json);
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.add(R.id.fl_container, hasChooseFrag);
			ft.commit();
		}
	}

}
