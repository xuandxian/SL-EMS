package com.overtech.ems.activity.fulltime.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceReportExpandAdapter;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.entity.fulltime.MaintenanceBean;
import com.overtech.ems.entity.fulltime.MaintenanceReportBean;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.http.OkHttpClientManager.ResultCallback;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Request;

/**
 * 维修报告单一级目录
 * 
 * @author Overtech Will
 * 
 */
public class MaintenanceTaskActivity extends BaseActivity {
	private TextView title;
	private ImageView ivRightMore;
	private ExpandableListView expand;
	private Button btChangeParts;
	private Button btSubmit;
	private PopupMenu popupMenu;
	private MaintenanceReportExpandAdapter adapter;
	private Activity activity;
	private String uid;
	private String certificate;
	private String elevatorNo;
	private String workorderCode;
	private String siteTel;
	public static final int REQUESTCODE = 0x001232;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maintenancetask);
		activity = this;
		stackInstance.pushActivity(activity);
		elevatorNo = getIntent().getStringExtra(Constant.ELEVATORNO);
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
		initView();
		initEvent();
		initElevatorData();
	}

	private void initElevatorData() {
		// TODO Auto-generated method stub
		Requester requester = new Requester();
		requester.certificate = certificate;
		requester.uid = uid;
		requester.cmd = 20004;
		ResultCallback<MaintenanceBean> callback = new ResultCallback<MaintenanceBean>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				Logr.e(request.toString());
			}

			@Override
			public void onResponse(MaintenanceBean response) {
				// TODO Auto-generated method stub
				if (response == null) {
					Utilities.showToast("暂时没有数据", activity);
					return;
				}
				int st = response.st;
				String msg = response.msg;
				if (st != 0) {
					if (st == -1 || st == -2) {
						Utilities.showToast(msg, activity);
						SharePreferencesUtils.put(activity,
								SharedPreferencesKeys.UID, "");
						SharePreferencesUtils.put(activity,
								SharedPreferencesKeys.CERTIFICATED, "");
						Intent intent = new Intent(activity,
								LoginActivity.class);
						startActivity(intent);
					} else {
						Utilities.showToast(msg, activity);
					}
				} else {
					workorderCode = response.body.workorderCode;
					siteTel = response.body.siteTel;
					initData();
				}
			}
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));
	}

	private void initData() {
		// TODO Auto-generated method stub
		Requester requester = new Requester();
		requester.cmd = 20010;
		requester.uid = uid;
		requester.certificate = certificate;
		ResultCallback<MaintenanceReportBean> callback = new ResultCallback<MaintenanceReportBean>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				Logr.e(request.toString());
			}

			@Override
			public void onResponse(MaintenanceReportBean response) {
				// TODO Auto-generated method stub
				if (response == null) {
					Utilities.showToast("暂时没有数据", activity);
					return;
				}
				int st = response.st;
				String msg = response.msg;
				if (st != 0) {
					if (st == -1 || st == -2) {
						Utilities.showToast(msg, activity);
						SharePreferencesUtils.put(activity,
								SharedPreferencesKeys.UID, "");
						SharePreferencesUtils.put(activity,
								SharedPreferencesKeys.CERTIFICATED, "");
						Intent intent = new Intent(activity,
								LoginActivity.class);
						startActivity(intent);
					} else {
						Utilities.showToast(msg, activity);
					}
				} else {
					if (adapter == null) {
						adapter = new MaintenanceReportExpandAdapter(activity,
								response.body.datas);
						expand.setAdapter(adapter);
					}
				}
			}
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		expand.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// TODO Auto-generated method stub
				MaintenanceReportExpandAdapter mainAdapter = (MaintenanceReportExpandAdapter) parent
						.getExpandableListAdapter();
				if (!mainAdapter.isHaveThreeLevel(groupPosition)) {
					Intent intent = new Intent(activity,
							MaintenanceTaskDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt(Constant.TYPE, 100);
					bundle.putString(Constant.MAINTENANCENAME,
							(String) mainAdapter.getGroup(groupPosition));
					bundle.putSerializable(Constant.MAINTENANCEDETAIL,
							mainAdapter.getParent(groupPosition));

					intent.putExtras(bundle);
					startActivityForResult(intent, REQUESTCODE);
					return true;
				}
				return false;
			}
		});
		expand.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				MaintenanceReportExpandAdapter mainAdapter = (MaintenanceReportExpandAdapter) parent
						.getExpandableListAdapter();
				Intent intent = new Intent(activity,
						MaintenanceTaskDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(Constant.TYPE, 101);
				bundle.putSerializable(Constant.MAINTENANCEDETAIL,
						mainAdapter.getChildren(groupPosition, childPosition));
				bundle.putString(Constant.MAINTENANCENAME, (String) mainAdapter
						.getChild(groupPosition, childPosition));
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE);
				return false;
			}
		});
		btChangeParts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Requester requester = new Requester();
				requester.cmd = 20007;
				requester.uid = uid;
				requester.certificate = certificate;
				requester.body.put("workorderCode", workorderCode);
				ResultCallback<MaintenanceBean> callback = new ResultCallback<MaintenanceBean>() {

					@Override
					public void onError(Request request, Exception e) {
						// TODO Auto-generated method stub
						Logr.e(request.toString());
					}

					@Override
					public void onResponse(MaintenanceBean response) {
						// TODO Auto-generated method stub
						if (response == null) {
							Utilities.showToast("暂时没有数据", activity);
							return;
						}
						int st = response.st;
						String msg = response.msg;
						if (st != 0) {
							if (st == -1 || st == -2) {
								Utilities.showToast(msg, activity);
								SharePreferencesUtils.put(activity,
										SharedPreferencesKeys.UID, "");
								SharePreferencesUtils.put(activity,
										SharedPreferencesKeys.CERTIFICATED, "");
								Intent intent = new Intent(activity,
										LoginActivity.class);
								startActivity(intent);
							} else {
								Utilities.showToast(msg, activity);
							}
						} else {
							Utilities.showToast("更换配件申请成功，请线下完成采购更换", activity);
						}
					}
				};
			}
		});
		btSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		ivRightMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initPopupMenu();
			}
		});
	}

	private void initView() {
		// TODO Auto-generated method stub
		expand = (ExpandableListView) findViewById(R.id.elv_maintenance_report);
		btChangeParts = (Button) findViewById(R.id.bt_change_parts);
		btSubmit = (Button) findViewById(R.id.bt_submit);
		title = (TextView) findViewById(R.id.tv_headTitle);
		ivRightMore = (ImageView) findViewById(R.id.iv_headTitleRight);
	}

	private void initPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new PopupMenu(activity, ivRightMore);
			popupMenu.inflate(R.menu.menu_maintenance_task);
			popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem arg0) {
					// TODO Auto-generated method stub
					switch (arg0.getItemId()) {
					case R.id.menu_site_tel:
						Intent intent = new Intent(Intent.ACTION_DIAL, Uri
								.parse("tel:" + siteTel));
						startActivity(intent);
						break;

					default:
						break;
					}
					return false;
				}
			});
			popupMenu.show();
		} else {
			popupMenu.show();
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == Activity.RESULT_CANCELED) {
			Utilities.showToast("cancle", activity);
		} else if (arg1 == Activity.RESULT_OK) {
			Utilities.showToast("confirm", activity);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}
}
