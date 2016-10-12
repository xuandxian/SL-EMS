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
import com.overtech.ems.utils.Utilities;

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
	private String workorderCode;// 维修单号
	private String siteTel;// 站点电话
	private String isMain;// 是否是主修
	private String hasReport;// 是否已经提交维修报告
	private String hasChooseComponent;// 是否已经选择了配件
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
			if(TextUtils.equals("1", hasReport)){
				initHasChooseFrag();//维修单详情页面配件列表进入
			}else{
				initReportFrag();
			}
		}
	}

	private void getExtraData() {
		// TODO Auto-generated method stub
		elevatorNo = getIntent().getStringExtra(Constant.ELEVATORNO);
		workorderCode = getIntent().getStringExtra(Constant.WORKORDERCODE);
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
		body.put("workorderCode", workorderCode);
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
				} else {//目前主修还没有提交维修报告  
					if(TextUtils.equals(isMain, "0")){
						initReportFrag();
					}else{
						Utilities.showToast("请等待主修完成问题选择", activity);//辅修
						stackInstance.popActivity(activity);
					}
				}
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal(Bean response) {
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
			ft.add(R.id.fl_container, reportFrag, "MaintenanceReport");
			ft.addToBackStack(reportFrag.getClass().getName());
			ft.commit();
			fragmentManager.executePendingTransactions();
			reportFrag.setOnNextClickListener(this);
		}
	}

	private void initHasChooseFrag() {
		if (hasChooseFrag == null) {
			hasChooseFrag = new MaintenanceHasChooseFragment();
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.add(R.id.fl_container, hasChooseFrag, "MaintenanceHasChoose");
			ft.addToBackStack(hasChooseFrag.getClass().getName());
			ft.commit();
			fragmentManager.executePendingTransactions();
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
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
//		super.onSaveInstanceState(outState);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Logr.e("MaintenanceTask=====getBackStackEntryCount=="
				+ fragmentManager.getBackStackEntryCount());
		if (fragmentManager.getBackStackEntryCount() == 1) {
			stackInstance.popActivity(activity);
		}
		super.onBackPressed();
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
			if (!hasChooseFrag.isAdded()) {
				ft.add(R.id.fl_container, hasChooseFrag);
			} else {
				ft.show(hasChooseFrag);
			}
			ft.addToBackStack(hasChooseFrag.getClass().getName());
			ft.commit();
			fragmentManager.executePendingTransactions();
		}else{
			Bundle args=new Bundle();
			args.putString("argument", json);
			hasChooseFrag.setArguments(args);
			FragmentTransaction ft = fragmentManager.beginTransaction();
			if (!hasChooseFrag.isAdded()) {
				ft.add(R.id.fl_container, hasChooseFrag);
			} else {
				ft.show(hasChooseFrag);
			}
			ft.addToBackStack(hasChooseFrag.getClass().getName());
			ft.commit();
			fragmentManager.executePendingTransactions();
		}
	}

}
