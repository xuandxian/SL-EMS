package com.overtech.ems.activity.fulltime.activity;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceChildAdapter;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceChildAdapter.ChildViewHolder;
import com.overtech.ems.activity.fulltime.adapter.MaintenanceReportExpandAdapter;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.Bean;
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
	private ImageView ivBack;
	private ImageView ivRightMore;
	private ExpandableListView expand;
	private ListView lvMaintenanceDetail;
	private Button btChangeParts;
	private Button btSubmit;
	private PopupMenu popupMenu;
	private MaintenanceReportExpandAdapter mainAdapter;
	private MaintenanceChildAdapter childAdapter;
	private Activity activity;
	private String uid;
	private String certificate;
	private String elevatorNo;
	private String workorderCode;
	private String siteTel;
	private String isMain;
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
		requester.body.put("qrcode", elevatorNo);
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
					Utilities.showToast(R.string.response_no_object, activity);
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
					} else if (st == 1) {
						Utilities.showToast(msg, activity);
						stackInstance.popActivity(activity);
					} else {
						Utilities.showToast(msg, activity);
					}
				} else {
					workorderCode = response.body.workorderCode;
					Logr.e("workorderCode===" + workorderCode);
					siteTel = response.body.siteTel;
					isMain=response.body.isMain;
					if(isMain.equals("0")){//是主修人员
						btChangeParts.setAlpha(1);
						btChangeParts.setClickable(true);
					}else{
						btChangeParts.setAlpha((float) 0.5);
						btChangeParts.setClickable(false);
					}
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
		requester.body.put("workorderCode", workorderCode);
		requester.body.put("isMain", isMain);
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
					Utilities.showToast(R.string.response_no_object, activity);
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
					} else if (st == 1) {
						stackInstance.popActivity(activity);
						Utilities.showToast(msg, activity);
					} else {
						Utilities.showToast(msg, activity);
					}
				} else {
					if (mainAdapter == null) {
						List<Map<String, Object>> datas = (List<Map<String, Object>>) response.body
								.get("datas");
						mainAdapter = new MaintenanceReportExpandAdapter(
								activity, datas);
						expand.setAdapter(mainAdapter);
						if (childAdapter == null) {
							if (!mainAdapter.isHaveThreeLevel(0)) {
								childAdapter = new MaintenanceChildAdapter(
										activity,
										(List<Map<String, Object>>) datas
												.get(0).get("children"));
								lvMaintenanceDetail.setAdapter(childAdapter);
							} else {
								List<Map<String, Object>> childs = (List<Map<String, Object>>) datas
										.get(0).get("children");
								childAdapter = new MaintenanceChildAdapter(
										activity,
										(List<Map<String, Object>>) childs.get(
												0).get("children"));
								lvMaintenanceDetail.setAdapter(childAdapter);
							}
						}
					} else {
					}
				}
			}
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		title.setText("维修");
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stackInstance.popActivity(activity);
			}
		});
		expand.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// TODO Auto-generated method stub
				MaintenanceReportExpandAdapter adapter = (MaintenanceReportExpandAdapter) parent
						.getExpandableListAdapter();
				Map<String, Object> level1 = adapter.getParent(groupPosition);
				if (!mainAdapter.isHaveThreeLevel(groupPosition)) {
					childAdapter.setData((List<Map<String, Object>>) level1
							.get("children"));
					childAdapter.notifyDataSetChanged();
				}
				return false;
			}
		});
		expand.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				MaintenanceReportExpandAdapter adapter = (MaintenanceReportExpandAdapter) parent
						.getExpandableListAdapter();
				Map<String, Object> childrenLevel2 = adapter.getChildren(
						groupPosition, childPosition);
				childAdapter.setData((List<Map<String, Object>>) childrenLevel2
						.get("children"));
				childAdapter.notifyDataSetChanged();
				return false;
			}
		});
		lvMaintenanceDetail.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				MaintenanceChildAdapter adapter = (MaintenanceChildAdapter) parent
						.getAdapter();
				Map<String, Object> child = (Map<String, Object>) adapter
						.getItem(position);
				ChildViewHolder vh = (ChildViewHolder) view.getTag();
				vh.cbStatus.toggle();
				if (vh.cbStatus.isChecked()) {
					child.put("checked", "1");
				} else {
					child.put("child", "0");
				}
			}
		});
		btChangeParts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(activity)
						.setTitle("更换配件？")
						.setPositiveButton("更换配件",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										changeParts();
									}
								})
						.setNegativeButton("暂不需要",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub

									}
								}).show();

			}
		});
		btSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(activity)
						.setTitle("确定保存？")
						.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										uploadMaintenanceData();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub

									}
								}).show();
			}
		});
		ivRightMore.setVisibility(View.VISIBLE);
		ivRightMore.setImageResource(R.drawable.icon_common_more);
		ivRightMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initPopupMenu();
			}
		});
	}

	protected void changeParts() {// 更换组件
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
					Utilities.showToast(R.string.response_no_object, activity);
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
					} else if (st == 1) {
						Utilities.showToast(msg, activity);
						stackInstance.popActivity(activity);
					} else {
						Utilities.showToast(msg, activity);
					}
				} else {
					Utilities.showToast(msg, activity);
				}
			}
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));
	}

	protected void uploadMaintenanceData() {// 提交维修报告
		// TODO Auto-generated method stub
		Requester requester = new Requester();
		requester.cmd = 20005;
		requester.certificate = certificate;
		requester.uid = uid;
		Logr.e("workorderCode==" + workorderCode);
		requester.body.put("workorderCode", workorderCode);
		requester.body.put("isMain", isMain);
		requester.body.put("data", mainAdapter.getData());
		ResultCallback<Bean> callback = new ResultCallback<Bean>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				Logr.e(request.toString());
			}

			@Override
			public void onResponse(Bean response) {
				// TODO Auto-generated method stub
				if (response == null) {
					Utilities.showToast(R.string.response_no_object, activity);
					return;
				}
				int st = response.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(response.msg, activity);
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(activity, LoginActivity.class);
					startActivity(intent);
				} else if (st == 0) {
					Utilities.showToast(response.msg, activity);
					Intent intent = new Intent(activity,
							MaintenanceResponseActivity.class);
					intent.putExtra(Constant.WORKORDERCODE, workorderCode);
					intent.putExtra("isMain", isMain);
					startActivity(intent);
					stackInstance.popActivity(activity);
				} else if (st == 1) {
					Utilities.showToast(response.msg, activity);
					stackInstance.popActivity(activity);
				} else {
					Utilities.showToast(response.msg, activity);
				}
			}
		};
		Logr.e(gson.toJson(requester));
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));
	}

	private void initView() {
		// TODO Auto-generated method stub
		expand = (ExpandableListView) findViewById(R.id.elv_maintenance_report);
		lvMaintenanceDetail = (ListView) findViewById(R.id.lv_maintenance_detail);
		btChangeParts = (Button) findViewById(R.id.bt_change_parts);
		btSubmit = (Button) findViewById(R.id.bt_submit);
		title = (TextView) findViewById(R.id.tv_headTitle);
		ivBack = (ImageView) findViewById(R.id.iv_headBack);
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
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}
}
