package com.overtech.ems.activity.parttime.grabtask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.PackageDetailAdapter;
import com.overtech.ems.activity.parttime.common.ElevatorDetailActivity;
import com.overtech.ems.activity.parttime.common.ShowCommunityLocationActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.StatusCodeBean;
import com.overtech.ems.entity.bean.TaskPackageDetailBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.TaskPackageDetail;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.AppUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/*
 *任务包详情(抢单模块)
 * @author Tony
 * @date 2016-01-13
 * 
 */

public class PackageDetailActivity extends BaseActivity {
	private ListView mPackageDetailListView;
	private PackageDetailAdapter adapter;
	private ArrayList<TaskPackageDetail> list;
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
	private Set<String> tagSet;
	private String tagItem;
	private String TAG = "24梯";
	private int totalPrice;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Gson gson = new Gson();
			switch (msg.what) {
			case StatusCode.PACKAGE_DETAILS_SUCCESS:
				String json = (String) msg.obj;
				TaskPackageDetailBean tasks = gson.fromJson(json,
						TaskPackageDetailBean.class);
				list = (ArrayList<TaskPackageDetail>) tasks.getModel();
				if (null == list || list.size() == 0) {
					Utilities.showToast("无数据", context);
				} else {
					adapter = new PackageDetailAdapter(context, list);
					mPackageDetailListView.setAdapter(adapter);
				}
				for (int i = 0; i < list.size(); i++) {
					totalPrice += Integer.valueOf(list.get(i)
							.getMaintainPrice());
				}
				mGrabTaskBtn
						.setText("抢单(￥" + String.valueOf(totalPrice) + "元)");
				break;
			case StatusCode.GRAG_RESPONSE_SUCCESS:
				String status = (String) msg.obj;
				StatusCodeBean bean = gson.fromJson(status,
						StatusCodeBean.class);
				String content = bean.getModel();
				if (TextUtils.equals(content, "0")) {
					Utilities.showToast("请不要重复抢单", context);
				} else if (TextUtils.equals(content, "1")) {
					Utilities.showToast("抢单成功，等待第二个人抢", context);
					// TODO

					// 推送业务代码 tagItem = bean.getTaskNo();
					if (!AppUtils.isValidTagAndAlias(tagItem)) {
						Utilities.showToast("格式不对", context);
					} else {
						tagSet.add(tagItem);
						JPushInterface.setAliasAndTags(getApplicationContext(),
								null, tagSet, mTagsCallback);
					}

					onActivityForResult();
				} else if (TextUtils.equals(content, "2")) {
					Utilities.showToast("抢单成功，请到任务中查看", context);
					// TODO

					// 推送业务代码 tagItem = bean.getTaskNo();
					if (!AppUtils.isValidTagAndAlias(tagItem)) {
						Utilities.showToast("格式不对", context);
					} else {
						tagSet.add(tagItem);
						JPushInterface.setAliasAndTags(getApplicationContext(),
								null, tagSet, mTagsCallback);
					}

					onActivityForResult();
				} else if (TextUtils.equals(content, "3")) {
					Utilities.showToast("差一点就抢到了", context);
				} else if (TextUtils.equals(content, "4")) {
					Utilities.showToast("维保日期的电梯数量已经超过10台，不能够再抢单。", context);
				}
				break;
			case StatusCode.MSG_SET_TAGS:
				Log.d("24梯", "Set tags in handler.");
				// TODO

				JPushInterface.setAliasAndTags(getApplicationContext(), null,
						(Set<String>) msg.obj, mTagsCallback);

				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", context);
				mGrabTaskBtn
						.setText("抢单(￥" + String.valueOf(totalPrice) + "元)");
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast("服务端异常", context);
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
				Log.d(TAG, logs);
				mSharedPreferences.edit().putStringSet("tagSet", tags).commit();// 成功保存标签后，将标签放到本地
				break;

			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Log.d(TAG, logs);
				if (AppUtils.isConnected(getApplicationContext())) {
					handler.sendMessageDelayed(handler.obtainMessage(
							StatusCode.MSG_SET_TAGS, tags), 1000 * 60);
				} else {
					Log.i(TAG, "No network");
				}
				break;

			default:
				logs = "Failed with errorCode = " + code;
				Log.d(TAG, logs);
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_package_detail);
		Set<String> tempSet = mSharedPreferences.getStringSet("tagSet", null);
		if (tempSet == null) {
			tagSet = new LinkedHashSet<String>();
		} else {
			tagSet = tempSet;
		}
		getExtrasData();
		findViewById();
		getDataByTaskNo(ServicesConfig.TASK_PACKAGE_DETAIL);
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
		startProgressDialog("正在查询...");
		Param param = new Param(Constant.TASKNO, mTaskNo);
		Request request = httpEngine.createRequest(url, param);
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
						TaskPackageDetail data = (TaskPackageDetail) parent
								.getItemAtPosition(position);
						String elevatorNo = data.getElevatorNo();
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
				finish();
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
				.withDividerColor("#11000000").withMessage("您是否要接此单？")
				.withMessageColor(R.color.main_primary)
				.withDialogColor("#FFFFFFFF").isCancelableOnTouchOutside(true)
				.withDuration(700).withEffect(effect)
				.withButtonDrawable(R.color.main_white).withButton1Text("否")
				.withButton1Color("#DD47BEE9").withButton2Text("是")
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
						startProgressDialog("正在抢单...");
						String mLoginName = mSharedPreferences.getString(
								SharedPreferencesKeys.CURRENT_LOGIN_NAME, null);
						Param paramPhone = new Param(Constant.LOGINNAME,
								mLoginName);
						Param paramTaskNo = new Param(Constant.TASKNO, mTaskNo);
						Request request = httpEngine.createRequest(
								ServicesConfig.Do_GRABTASK, paramPhone,
								paramTaskNo);
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

	private void onActivityForResult() {
		Intent intent = new Intent();
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
}
