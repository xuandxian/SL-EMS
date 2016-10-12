package com.overtech.ems.activity.fulltime.activity;

import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;

public class ClosePeopleSolveActivity extends BaseActivity implements
		OnClickListener {
	private Toolbar toolbar;
	private ActionBar actionBar;
	private View dateTimeView;
	private DatePicker datePicker;
	private TimePicker timePicker;
	private AppCompatTextView tvRepairTime;
	private AppCompatTextView tvTitle;
	private AppCompatTextView tvAchieveTime;
	private AppCompatTextView tvProblemSolveTime;
	private AppCompatButton btComplete;
	private ClosePeopleSolveActivity activity;
	private String sRepairDate;
	private String sArrivalTime;
	private String sPeopleOutEmergencyTime;
	private String workOrderCode;
	private String isMain;
	private String hasReport;
	private String hasChooseComponent;
	private String uid;
	private String certificate;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_close_perple_solve;
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
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		workOrderCode = getIntent().getStringExtra("workorderCode");
		sRepairDate = getIntent().getStringExtra("faultTime");
		isMain = getIntent().getStringExtra("isMain");
		hasReport = getIntent().getStringExtra("hasReport");
		hasChooseComponent = getIntent().getStringExtra("hasChooseComponent");
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
		tvTitle.setText("解决");
		tvAchieveTime.setOnClickListener(this);
		tvProblemSolveTime.setOnClickListener(this);
		btComplete.setOnClickListener(this);

		tvRepairTime.setText("报修时间：" + sRepairDate);

		timePicker.setIs24HourView(true);
	}

	private void initView() {
		// TODO Auto-generated method stub
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
		tvRepairTime = (AppCompatTextView) findViewById(R.id.tv_repair_time);
		tvAchieveTime = (AppCompatTextView) findViewById(R.id.tv_achieve_time);
		tvProblemSolveTime = (AppCompatTextView) findViewById(R.id.tv_problem_solve_time);
		btComplete = (AppCompatButton) findViewById(R.id.bt_complete);

		dateTimeView = getLayoutInflater().inflate(
				R.layout.layout_date_time_picker, null);
		datePicker = (DatePicker) dateTimeView.findViewById(R.id.datePicker);
		timePicker = (TimePicker) dateTimeView.findViewById(R.id.timePicker);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_achieve_time:
			alertBuilder
					.setTitle("请选择到达时间")
					.setView(dateTimeView)
					.setNegativeButton("取消", null)
					.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									int year = datePicker.getYear();
									int month = datePicker.getMonth() + 1;
									int day = datePicker.getDayOfMonth();
									int hour = timePicker.getCurrentHour();
									int minute = timePicker.getCurrentMinute();
									sArrivalTime = year
											+ "-"
											+ (month < 10 ? "0" + month : month)
											+ "-"
											+ (day < 10 ? "0" + day : day)
											+ " "
											+ (hour < 10 ? "0" + hour : hour)
											+ ":"
											+ (minute < 10 ? "0" + minute
													: minute)+":00";
									tvAchieveTime.setText(sArrivalTime);
								}
							}).setOnDismissListener(new OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
							// TODO Auto-generated method stub
							((ViewGroup) dateTimeView.getParent())
									.removeView(dateTimeView);
						}
					}).show();
			break;
		case R.id.tv_problem_solve_time:
			alertBuilder
					.setTitle("请选择问题解决时间")
					.setView(dateTimeView)
					.setNegativeButton("取消", null)
					.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									int year = datePicker.getYear();
									int month = datePicker.getMonth() + 1;
									int day = datePicker.getDayOfMonth();
									int hour = timePicker.getCurrentHour();
									int minute = timePicker.getCurrentMinute();
									sPeopleOutEmergencyTime = year
											+ "-"
											+ (month < 10 ? "0" + month : month)
											+ "-"
											+ (day < 10 ? "0" + day : day)
											+ " "
											+ (hour < 10 ? "0" + hour : hour)
											+ ":"
											+ (minute < 10 ? "0" + minute
													: minute)+":00";
									tvProblemSolveTime
											.setText(sPeopleOutEmergencyTime);
								}
							}).setOnDismissListener(new OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
							// TODO Auto-generated method stub
							((ViewGroup) dateTimeView.getParent())
									.removeView(dateTimeView);
						}
					}).show();
			break;
		case R.id.bt_complete:
			if (judgeTime()) {
				alertBuilder
						.setTitle("温馨提示")
						.setMessage("您确认提交所选择的时间？")
						.setView(null)
						.setNegativeButton("取消", null)
						.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										confirm();
									}
								}).setOnDismissListener(null).show();
			} else {
				Utilities.showToast("时间先后不正确", activity);
			}

			break;
		default:
			break;
		}
	}

	/**
	 * 验证到达时间和放人时间的先后正确性
	 * 
	 * @return
	 */
	private boolean judgeTime() {
		String arriveTime = tvAchieveTime.getText().toString();
		String solveTime = tvProblemSolveTime.getText().toString();
		if (Integer.parseInt(arriveTime.substring(0, 4)) <= Integer
				.parseInt(solveTime.substring(0, 4))) {
			if (Integer.parseInt(arriveTime.substring(5, 7)) <= Integer
					.parseInt(solveTime.substring(5, 7))) {
				if (Integer.parseInt(arriveTime.substring(8, 10)) <= Integer
						.parseInt(solveTime.substring(8, 10))) {
					if (Integer.parseInt(arriveTime.substring(11, 13)) <= Integer
							.parseInt(solveTime.substring(11, 13))) {
						if (Integer.parseInt(arriveTime.substring(14, 16)) <= Integer
								.parseInt(solveTime.substring(14, 16))) {
							return true;
						} else {
							return false;
						}
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private void confirm() {
		// TODO Auto-generated method stubs
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("workorderCode", workOrderCode);
		body.put("arrivalTime", sArrivalTime);
		body.put("peopleOutEmergencyTime", sPeopleOutEmergencyTime);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20008, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				Intent i = new Intent(activity, MaintenanceTaskActivity.class);
				i.putExtra("workorderCode", workOrderCode);
				i.putExtra("isMain", isMain);
				i.putExtra("hasReport", hasReport);
				i.putExtra("hasChooseComponent", hasChooseComponent);
				startActivity(i);
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

}
