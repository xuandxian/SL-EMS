package com.overtech.ems.activity.parttime.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.PackageDetailAdapter;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.AppUtils;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;

/*
 *任务包详情(抢单模块/附近模块)
 * @author Will
 * @date 2016-06-15
 * 
 */

public class PackageDetailActivity extends BaseActivity {
	private Toolbar toolbar;
	private ActionBar actionBar;
	private AppCompatTextView tvTitle;
	private AppCompatTextView tvSubTitle;
	private ListView mPackageDetailListView;
	private PackageDetailAdapter adapter;
	private List<Map<String, Object>> list;
	private Button mGrabTaskBtn;
	private String mCommunityName;
	private String mTaskNo;
	private String mLongitude; // 经度
	private String mLatitude; // 纬度
	private int totalPrice;
	private String uid;
	private String certificate;
	private PackageDetailActivity activity;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.MSG_SET_TAGS:
				Log.d("24梯", "Set tags in handler.");
				JPushInterface.setAliasAndTags(getApplicationContext(), null,
						(Set<String>) msg.obj, mTagsCallback);
				break;
			}
		};
	};

	private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				Logr.e(logs);
				break;
			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Logr.e(logs);
				if (AppUtils.isConnected(getApplicationContext())) {
					handler.sendMessageDelayed(handler.obtainMessage(
							StatusCode.MSG_SET_TAGS, tags), 1000 * 60);
				} else {
					Logr.e("No network");
				}
				break;

			default:
				logs = "Failed with errorCode = " + code;
				Logr.e(logs);
			}

		}

	};

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_package_detail;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = this;
		stackInstance.pushActivity(activity);
		getExtrasData();
		findViewById();
		getDataByTaskNo();
		init();
	}

	private void getExtrasData() {
		Bundle bundle = getIntent().getExtras();
		if (null == bundle) {
			return;
		}
		mTaskNo = bundle.getString("TaskNo");
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
	}

	private void findViewById() {
		toolbar = (Toolbar) findViewById(R.id.toolBar2);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
		tvSubTitle = (AppCompatTextView) findViewById(R.id.tvSubTitle);

		mPackageDetailListView = (ListView) findViewById(R.id.grab_task_package_listview);
		mGrabTaskBtn = (Button) findViewById(R.id.btn_grab_task_package);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.menu_map, menu);
		return true;
	}

	private void getDataByTaskNo() {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("taskNo", mTaskNo);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20051, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				mCommunityName = response.body.get("taskPackageName")
						.toString();
				tvTitle.setText(mCommunityName);
				mLongitude = response.body.get("longitude").toString();
				mLatitude = response.body.get("latitude").toString();
				list = (List<Map<String, Object>>) response.body.get("data");
				if (null == list || list.size() == 0) {
					stopProgressDialog();
					Utilities.showToast(response.msg, activity);
					return;
				} else {
					if (adapter == null) {
						adapter = new PackageDetailAdapter(activity, list);
						mPackageDetailListView.setAdapter(adapter);
					} else {
						adapter.setData(list);
						adapter.notifyDataSetChanged();
					}
				}
				for (int i = 0; i < list.size(); i++) {
					totalPrice += Integer.valueOf(list.get(i)
							.get("maintainPrice").toString());
				}
				mGrabTaskBtn
						.setText("抢单(￥" + String.valueOf(totalPrice) + "元)");
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

	private void init() {
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
		tvSubTitle.setText(mTaskNo);
		toolbar.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				// TODO Auto-generated method stub
				switch (menuItem.getItemId()) {
				case R.id.menu_map:
					Intent intent = new Intent(PackageDetailActivity.this,
							ShowCommunityLocationActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("CommunityName", mCommunityName);
					bundle.putString("Longitude", mLongitude);
					bundle.putString("Latitude", mLatitude);
					intent.putExtras(bundle);
					startActivity(intent);
					break;

				default:
					break;
				}
				return true;
			}
		});
		mPackageDetailListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Map<String, Object> data = (Map<String, Object>) parent
								.getItemAtPosition(position);
						String elevatorNo = data.get("elevatorNo").toString();
						Intent intent = new Intent(PackageDetailActivity.this,
								ElevatorDetailActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString(Constant.ELEVATORNO, elevatorNo);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
		mGrabTaskBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDialog();
			}
		});

	}

	private void showDialog() {
		alertBuilder.setTitle("温馨提示")
				.setMessage("您确认要接此单？")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				})
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						startProgressDialog(getResources().getString(
								R.string.loading_public_grabing));
						HashMap<String, Object> body = new HashMap<String, Object>();
						body.put(Constant.TASKNO, mTaskNo);
						HttpConnector<Bean> conn = new HttpConnector<Bean>(
								20023, uid, certificate, body) {

							@Override
							public Context getContext() {
								// TODO Auto-generated method stub
								return activity;
							}

							@Override
							public void bizSuccess(Bean response) {
								// TODO Auto-generated method stub
								String content = response.body.get("status")
										.toString();
								if (TextUtils.equals(content, "0")) {
									Utilities.showToast(response.msg, activity);
								} else if (TextUtils.equals(content, "1")) {// 抢单成功，等待第二个人抢
									Utilities.showToast(response.msg, activity);
									// 推送业务代码
									// TODO
									loadNotDoneTask();
									onActivityForResult();
								} else if (TextUtils.equals(content, "2")) {// 抢单成功，到任务单中查看
									Utilities.showToast(response.msg, activity);
									// 推送业务代码
									loadNotDoneTask();
									onActivityForResult();
								} else if (TextUtils.equals(content, "3")) {
									Utilities.showToast(response.msg, activity);
								} else if (TextUtils.equals(content, "4")) {
									Utilities.showToast(response.msg, activity);
								} else {
									Utilities.showToast(response.msg, activity);
									stackInstance
											.popTopActivitys(PackageDetailActivity.class);
									Intent intent = new Intent(
											PackageDetailActivity.this,
											LoginActivity.class);
									startActivity(intent);
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
				}).show();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}

	private void onActivityForResult() {
		Intent intent = new Intent();
		setResult(Activity.RESULT_OK, intent);
		stackInstance.popActivity(activity);
	}

	/**
	 * 加载未完成的任务单
	 */
	private void loadNotDoneTask() {
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20050, uid,
				certificate, null) {

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
				Set<String> tempSet = new HashSet<String>();
				for (Map<String, Object> data : datas) {
					String sTaskNo = (String) data.get("taskNo");
					tempSet.add(sTaskNo);
				}
				JPushInterface.setAliasAndTags(activity, "", tempSet,
						mTagsCallback);
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

			}

		};
		conn.sendRequest();
	}

}
