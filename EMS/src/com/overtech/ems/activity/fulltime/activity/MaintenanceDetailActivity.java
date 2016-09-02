package com.overtech.ems.activity.fulltime.activity;

import java.util.HashMap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.parttime.common.ElevatorDetailActivity;
import com.overtech.ems.activity.parttime.tasklist.ScanCodeActivity;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;

public class MaintenanceDetailActivity extends BaseActivity implements
		OnClickListener {
	private Toolbar toolbar;
	private ActionBar actionBar;
	private AppCompatTextView tvTitle;
	private AppCompatTextView tvNavigation;
	private AppCompatTextView tvQrcode;
	private AppCompatTextView tvPartners;
	private LinearLayout elevatorDetail;
	private AppCompatTextView tvAddress;
	private AppCompatTextView tvElevatorNo;
	private AppCompatTextView tvFaultFrom;
	private AppCompatTextView tvRepairContent;
	private AppCompatButton btClosePeople;
	private AppCompatButton btComponentLists;
	private String isMain;// 是否主修
	private String peopleInEmergency;// 是否是关人状态
	private String hasChooseComponent;// 主修是否已经进行了配件操作
	private String hasReport;// 主修是否已经提交了维修报告
	private String siteTel;// 站点电话
	private String uid;
	private String certificate;
	private String workorderCode;
	private String partnerTel;
	private String elevatorNo;
	private String faultTime;
	private LatLng curLatLng;
	private LatLng desLatLng;
	private MaintenanceDetailActivity activity;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_maintenance_detail;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = this;
		stackInstance.pushActivity(activity);
		uid = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.CERTIFICATED, "");
		workorderCode = getIntent().getStringExtra(Constant.WORKORDERCODE);
		Logr.e(workorderCode);
		initView();
		initEvent();
		loadingData();
	}

	private void loadingData() {
		// TODO Auto-generated method stub
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("workorderCode", workorderCode);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20002, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				tvAddress.setText("地址："
						+ response.body.get("repairAddress").toString());
				tvElevatorNo.setText("梯号："
						+ response.body.get("elevatorBrand").toString());
				tvFaultFrom.setText("故障来源："
						+ response.body.get("storeySite").toString());
				tvRepairContent.setText("报修内容："
						+ response.body.get("faultCause").toString());
				tvFaultFrom.setText(response.body.get("faultFrom").toString());
				// faultComponent.setText(response.body.faultComponent);
				partnerTel = response.body.get("partnerTel").toString();
				elevatorNo = response.body.get("elevatorNo").toString();
				desLatLng = new LatLng(Double.parseDouble(response.body.get(
						"latitude").toString()),
						Double.parseDouble(response.body.get("longitude")
								.toString()));
				isMain = response.body.get("isMain").toString();
				peopleInEmergency = response.body.get("peopleInEmergency")
						.toString();
				hasChooseComponent = response.body.get("hasChooseComponent")
						.toString();
				faultTime = response.body.get("faultTime").toString();
				hasReport = response.body.get("hasReport").toString();
				siteTel = response.body.get("siteTel").toString();
				if (TextUtils.equals(peopleInEmergency, "1")) {
					btClosePeople.setVisibility(View.VISIBLE);
				} else {
					btClosePeople.setVisibility(View.GONE);
				}
				if (TextUtils.equals(hasChooseComponent, "1")) {
					btComponentLists.setVisibility(View.VISIBLE);
				} else {
					btComponentLists.setVisibility(View.GONE);
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

	private void initEvent() {
		// TODO Auto-generated method stub
		toolbar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stackInstance.popActivity(activity);
			}
		});
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		tvNavigation.setOnClickListener(this);
		tvQrcode.setOnClickListener(this);
		tvPartners.setOnClickListener(this);
		elevatorDetail.setOnClickListener(this);
		double latitude = ((MyApplication) getApplicationContext()).latitude;
		double longitude = ((MyApplication) getApplicationContext()).longitude;
		curLatLng = new LatLng(latitude, longitude);
	}

	private void initView() {
		// TODO Auto-generated method stub
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
		tvNavigation = (AppCompatTextView) findViewById(R.id.tv_navigation);
		tvQrcode = (AppCompatTextView) findViewById(R.id.tv_qrcode);
		tvPartners = (AppCompatTextView) findViewById(R.id.tv_partner);
		elevatorDetail = (LinearLayout) findViewById(R.id.rl_elevator_detail);
		tvAddress = (AppCompatTextView) findViewById(R.id.tv_address);
		tvElevatorNo = (AppCompatTextView) findViewById(R.id.tv_elevator_no);
		tvFaultFrom = (AppCompatTextView) findViewById(R.id.tv_fault_from);
		tvRepairContent = (AppCompatTextView) findViewById(R.id.tv_repair_content);
		btClosePeople = (AppCompatButton) findViewById(R.id.bt_close_people);
		btComponentLists = (AppCompatButton) findViewById(R.id.bt_component_list);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_navigation:
			startNavicate(curLatLng, desLatLng, "终点");
			break;
		case R.id.tv_partner:
			Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ partnerTel));
			startActivity(dial);
			break;
		case R.id.tv_qrcode:
			Intent i = new Intent(activity, ScanCodeActivity.class);
			startActivity(i);
			break;
		case R.id.rl_elevator_detail:
			Intent elevatorDetail = new Intent(this,
					ElevatorDetailActivity.class);
			elevatorDetail.putExtra(Constant.ELEVATORNO, elevatorNo);
			startActivity(elevatorDetail);
			break;
		case R.id.bt_close_people:
			alertBuilder
					.setTitle("关人确认")
					.setMessage("是否关人？")
					.setNegativeButton("否", null)
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Intent i = new Intent(activity,
											ClosePeopleSolveActivity.class);
									i.putExtra("workorderCode", workorderCode);
									i.putExtra("faultTime", faultTime);
									startActivity(i);
								}
							}).show();
			break;
		case R.id.bt_component_list:
			Intent i2 = new Intent(activity, MaintenanceTaskActivity.class);
			i2.putExtra("workorderCode", workorderCode);
			i2.putExtra("isMain", isMain);
			i2.putExtra("siteTel", siteTel);
			i2.putExtra("hasReport", hasReport);
			startActivity(i2);
			break;
		default:
			break;
		}
	}

	private void startNavicate(LatLng startPoint, LatLng endPoint,
			String endName) {
		// TODO Auto-generated method stub
		RouteParaOption para = new RouteParaOption().startName("我的位置")
				.startPoint(startPoint).endPoint(endPoint).endName(endName)
				.busStrategyType(EBusStrategyType.bus_recommend_way);
		try {
			BaiduMapRoutePlan.setSupportWebRoute(true);
			BaiduMapRoutePlan.openBaiduMapTransitRoute(para, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}

}
