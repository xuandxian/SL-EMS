package com.overtech.ems.activity.fulltime.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.parttime.common.ElevatorDetailActivity;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.entity.fulltime.MaintenanceBean;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.http.OkHttpClientManager.ResultCallback;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Request;

public class MaintenanceDetailActivity extends BaseActivity implements
		OnClickListener {
	private TextView title;
	private ImageView ivBack;
	private ImageView more;
	private LinearLayout elevatorDetail;
	private TextView address;
	private TextView elevatorBrand;
	private TextView elevatorStoreySite;
	private TextView faultCause;
	private TextView faultFrom;
	private TextView faultComponent;
	private PopupWindow morePopupWindow;
	private LinearLayout llPop1;
	private LinearLayout llPop2;
	private LinearLayout llPop3;
	private String uid;
	private String certificate;
	private String workorderCode;
	private String partnerTel;
	private String elevatorNo;
	private LatLng curLatLng;
	private LatLng desLatLng;
	private MaintenanceDetailActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maintenance_detail);
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
		Requester requester = new Requester();
		requester.cmd = 20002;
		requester.uid = uid;
		requester.certificate = certificate;
		requester.body.put("workorderCode", workorderCode);
		ResultCallback<MaintenanceBean> callback = new ResultCallback<MaintenanceBean>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				Logr.e(request.toString());
			}

			@Override
			public void onResponse(MaintenanceBean response) {
				// TODO Auto-generated method stub
				if (response == null) {
					Utilities.showToast("暂时没有数据", activity);
					stopProgressDialog();
					return;
				}
				int st = response.st;
				String msg = response.msg;
				if (st != 0) {
					if (st == -1 || st == -2) {
						if (activity != null) {
							Utilities.showToast(msg, activity);
							SharePreferencesUtils.put(activity,
									SharedPreferencesKeys.UID, "");
							SharePreferencesUtils.put(activity,
									SharedPreferencesKeys.CERTIFICATED, "");
							Intent intent = new Intent(activity,
									LoginActivity.class);
							startActivity(intent);
						}
					} else {
						Utilities.showToast(msg, activity);
					}
				} else {
					address.setText(response.body.repairAddress);
					elevatorBrand.setText(response.body.elevatorBrand);
					elevatorStoreySite.setText(response.body.storeySite);
					faultCause.setText(response.body.faultCause);
					faultFrom.setText(response.body.faultFrom);
					faultComponent.setText(response.body.faultComponent);
					partnerTel = response.body.partnerTel;
					elevatorNo = response.body.elevatorNo;
					desLatLng = new LatLng(
							Double.parseDouble(response.body.latitude),
							Double.parseDouble(response.body.longitude));
				}
				stopProgressDialog();
			}
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		title.setText(workorderCode);
		ivBack.setVisibility(View.VISIBLE);
		more.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(this);
		more.setOnClickListener(this);
		elevatorDetail.setOnClickListener(this);
		double latitude = ((MyApplication) getApplicationContext()).latitude;
		double longitude = ((MyApplication) getApplicationContext()).longitude;
		curLatLng = new LatLng(latitude, longitude);
	}

	private void initView() {
		// TODO Auto-generated method stub
		title = (TextView) findViewById(R.id.tv_headTitle);
		ivBack = (ImageView) findViewById(R.id.iv_headBack);
		more = (ImageView) findViewById(R.id.iv_headTitleRight);
		elevatorDetail = (LinearLayout) findViewById(R.id.rl_elevator_detail);
		address = (TextView) findViewById(R.id.tv_adderss);
		elevatorBrand = (TextView) findViewById(R.id.tv_elevator_brand);
		elevatorStoreySite = (TextView) findViewById(R.id.tv_elevator_storey_site);
		faultCause = (TextView) findViewById(R.id.tv_fault_cause);
		faultFrom = (TextView) findViewById(R.id.tv_fault_from);
		faultComponent = (TextView) findViewById(R.id.tv_fault_component);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_headBack:
			stackInstance.popActivity(activity);
			break;
		case R.id.iv_headTitleRight:
			if (morePopupWindow == null) {
				View contentView = getLayoutInflater().inflate(
						R.layout.layout_tasklist_pop, null);
				llPop1 = (LinearLayout) contentView.findViewById(R.id.ll_pop_1);
				llPop2 = (LinearLayout) contentView.findViewById(R.id.ll_pop_2);
				llPop3 = (LinearLayout) contentView.findViewById(R.id.ll_pop_3);
				llPop1.setVisibility(View.VISIBLE);
				llPop2.setVisibility(View.GONE);
				llPop3.setVisibility(View.VISIBLE);
				llPop1.setOnClickListener(this);
				llPop3.setOnClickListener(this);
				morePopupWindow = new PopupWindow(contentView, getResources()
						.getDisplayMetrics().widthPixels / 2,
						LayoutParams.WRAP_CONTENT);
				morePopupWindow.setFocusable(true);
				morePopupWindow.setOutsideTouchable(true);
				morePopupWindow.setBackgroundDrawable(new ColorDrawable(
						0x00000000));

				morePopupWindow.showAsDropDown(more);
			} else {
				morePopupWindow.showAsDropDown(more);
			}
			break;
		case R.id.ll_pop_1:
			startNavicate(curLatLng, desLatLng, "终点");
			break;
		case R.id.ll_pop_3:
			Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ partnerTel));
			startActivity(dial);
			break;
		case R.id.rl_elevator_detail:
			Intent elevatorDetail = new Intent(this,
					ElevatorDetailActivity.class);
			elevatorDetail.putExtra(Constant.ELEVATORNO, elevatorNo);
			startActivity(elevatorDetail);
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
