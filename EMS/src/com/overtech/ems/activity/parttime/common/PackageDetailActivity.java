package com.overtech.ems.activity.parttime.common;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.PackageDetailAdapter;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.entity.bean.StatusCodeBean;
import com.overtech.ems.entity.bean.TaskPackageDetailBean;
import com.overtech.ems.entity.bean.TaskPackageDetailBean.TaskPackage;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.http.OkHttpClientManager.ResultCallback;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.AppUtils;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/*
 *任务包详情(抢单模块/附近模块)
 * @author Will
 * @date 2016-06-15
 * 
 */

public class PackageDetailActivity extends BaseActivity {
	private ListView mPackageDetailListView;
	private PackageDetailAdapter adapter;
	private List<TaskPackage> list;
	private Button mGrabTaskBtn;
	private Effectstype effect;
	private ImageView mDoBack;
	private String mCommunityName;
	private TextView mHeadTitleCommunity;
	private ImageView mRightContent;
	private String mTaskNo;
	private String mLongitude; // 经度
	private String mLatitude; // 纬度
	private TextView mHeadTitleTaskNo;
	private int totalPrice;
	private String uid;
	private String certificate;
	private PackageDetailActivity activity;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.PACKAGE_DETAILS_SUCCESS:
				stopProgressDialog();
				String json = (String) msg.obj;
				Logr.e(json);
				TaskPackageDetailBean tasks = gson.fromJson(json,
						TaskPackageDetailBean.class);
				int st = tasks.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(tasks.msg, activity);
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(activity, LoginActivity.class);
					startActivity(intent);
					return;
				}else if(st==1){
					Utilities.showToast(tasks.msg, activity);
					return;
				}
				list = tasks.body.data;
				if (null == list || list.size() == 0) {
					stopProgressDialog();
					Utilities.showToast(tasks.msg, activity);
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
					totalPrice += Integer.valueOf(list.get(i).maintainPrice);
				}
				mGrabTaskBtn
						.setText("抢单(￥" + String.valueOf(totalPrice) + "元)");
				break;
			case StatusCode.GRAG_RESPONSE_SUCCESS:
				stopProgressDialog();
				String status = (String) msg.obj;
				Logr.e(status);
				StatusCodeBean bean = gson.fromJson(status,
						StatusCodeBean.class);
				int st2 = bean.st;
				if (st2 == -1 || st2 == -2) {
					Utilities.showToast(bean.msg, activity);
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(activity, LoginActivity.class);
					startActivity(intent);
					return;
				}else if(st2==1){
					Utilities.showToast(bean.msg, activity);
					return;
				}
				String content = bean.body.get("status").toString();
				if (TextUtils.equals(content, "0")) {
					Utilities.showToast(bean.msg, activity);
				} else if (TextUtils.equals(content, "1")) {//抢单成功，等待第二个人抢
					Utilities.showToast(bean.msg, activity);
					// 推送业务代码
					//TODO
					loadNotDoneTask();
					onActivityForResult();
				} else if (TextUtils.equals(content, "2")) {//抢单成功，到任务单中查看
					Utilities.showToast(bean.msg, activity);
					// 推送业务代码
					loadNotDoneTask();
					onActivityForResult();
				} else if (TextUtils.equals(content, "3")) {
					Utilities.showToast(bean.msg, activity);
				} else if (TextUtils.equals(content, "4")) {
					Utilities.showToast(bean.msg, activity);
				} else {
					Utilities.showToast(bean.msg, activity);
					stackInstance.popTopActivitys(PackageDetailActivity.class);
					Intent intent = new Intent(PackageDetailActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}
				break;
			case StatusCode.MSG_SET_TAGS:
				Log.d("24梯", "Set tags in handler.");
				JPushInterface.setAliasAndTags(getApplicationContext(), null,
						(Set<String>) msg.obj, mTagsCallback);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast(R.string.request_error_msg, activity);
				mGrabTaskBtn
						.setText("抢单(￥" + String.valueOf(totalPrice) + "元)");
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast(R.string.response_failure_msg, activity);
				break;
			}
			stopProgressDialog();
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_package_detail);
		activity = this;
		stackInstance.pushActivity(activity);
		getExtrasData();
		findViewById();
		getDataByTaskNo(SystemConfig.NEWIP);
		init();
	}

	private void getExtrasData() {
		Bundle bundle = getIntent().getExtras();
		if (null == bundle) {
			return;
		}
		mCommunityName = bundle.getString("CommunityName");
		mTaskNo = bundle.getString("TaskNo");
		mLongitude = bundle.getString("Longitude");
		mLatitude = bundle.getString("Latitude");
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
	}

	private void findViewById() {
		mPackageDetailListView = (ListView) findViewById(R.id.grab_task_package_listview);
		mGrabTaskBtn = (Button) findViewById(R.id.btn_grab_task_package);
		mHeadTitleCommunity = (TextView) findViewById(R.id.tv_headTitle_community_name);
		mHeadTitleTaskNo = (TextView) findViewById(R.id.tv_headTitle_taskno);
		mDoBack = (ImageView) findViewById(R.id.iv_grab_headBack);
		mRightContent = (ImageView) findViewById(R.id.iv_navicate_right);
		mRightContent.setBackgroundResource(R.drawable.icon_map);
		mRightContent.setVisibility(View.VISIBLE);
	}

	private void getDataByTaskNo(String url) {
		startProgressDialog(getResources().getString(R.string.loading_public_default));
		Requester requester = new Requester();
		requester.cmd = 20051;
		requester.uid = uid;
		requester.certificate = certificate;
		requester.body.put("taskNo", mTaskNo);

		Request request = httpEngine.createRequest(url, gson.toJson(requester));
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.PACKAGE_DETAILS_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request request, IOException e) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				handler.sendMessage(msg);
			}
		});
	}

	private void init() {
		mHeadTitleCommunity.setText(mCommunityName);
		mHeadTitleTaskNo.setText(mTaskNo);
		mPackageDetailListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						TaskPackage data = (TaskPackage) parent
								.getItemAtPosition(position);
						String elevatorNo = data.elevatorNo;
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
		mDoBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				stackInstance.popActivity(activity);
			}
		});
		mRightContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(PackageDetailActivity.this,
						ShowCommunityLocationActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("CommunityName", mCommunityName);
				bundle.putString("Longitude", mLongitude);
				bundle.putString("Latitude", mLatitude);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void showDialog() {
		effect = Effectstype.Slideright;
		dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
				.withDividerColor("#11000000").withMessage("您确认要接此单？")
				.withMessageColor(R.color.main_primary)
				.withDialogColor("#FFFFFFFF").isCancelableOnTouchOutside(true)
				.withDuration(700).withEffect(effect)
				.withButtonDrawable(R.color.main_white).withButton1Text("取消")
				.withButton1Color("#DD47BEE9").withButton2Text("确认")
				.withButton2Color("#DD47BEE9")
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
						startProgressDialog(getResources().getString(R.string.loading_public_grabing));
						Requester requester = new Requester();
						requester.certificate = certificate;
						requester.uid = uid;
						requester.cmd = 20023;
						requester.body.put(Constant.TASKNO, mTaskNo);
						Request request = httpEngine.createRequest(
								SystemConfig.NEWIP,
								gson.toJson(requester));
						Call call = httpEngine.createRequestCall(request);
						call.enqueue(new Callback() {

							@Override
							public void onFailure(Request request, IOException e) {
								Message msg = new Message();
								msg.what = StatusCode.RESPONSE_NET_FAILED;
								handler.sendMessage(msg);
							}

							@Override
							public void onResponse(Response response)
									throws IOException {
								Message msg = new Message();
								if (response.isSuccessful()) {
									msg.what = StatusCode.GRAG_RESPONSE_SUCCESS;
									msg.obj = response.body().string();
								} else {
									msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
								}
								handler.sendMessage(msg);
							}
						});
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
	
	private ResultCallback<Bean> loadNotDoneCallback = new ResultCallback<Bean>() {

		@Override
		public void onError(Request request, Exception e) {
			// TODO Auto-generated method stub
			Logr.e(request.toString());
		}

		@Override
		public void onResponse(Bean response) {
			// TODO Auto-generated method stub
			int st = response.st;
			if (st == 0) {
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
		}
	};

	/**
	 * 加载未完成的任务单
	 */
	private void loadNotDoneTask() {
		Requester requester = new Requester();
		requester.cmd = 20050;
		requester.uid = uid;
		requester.certificate = certificate;
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, loadNotDoneCallback,
				gson.toJson(requester));
	}
}
