package com.overtech.ems.activity.parttime.tasklist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.TaskListDetailsAdapter;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.MaintenanceCompleteBean;
import com.overtech.ems.entity.bean.WorkTypeBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.entity.parttime.MaintenanceType;
import com.overtech.ems.entity.parttime.ScanResultBean;
import com.overtech.ems.entity.parttime.ScanResultBean.BeginWorkResult;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.AppUtils;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/*
 * 维保清单列表
 *
 * 维保人员通过扫描二维码开启工作，将用户名和电梯编号发送到后台进行校验，如果该电梯是当天的维保任务，并且也有维保搭档，则可以开始工作，
 * 然后为防止维保人员没有到维保地点就扫描开始工作，再加上维保地点经纬度的判断，500米范围之内 需要做的事情：和搭档之间是不是第一次开始该电梯的维保工作
 * 点击的时候该电梯是否已经完成
 * 
 * @author Overtech Will
 * 
 */

public class QueryTaskListActivity extends BaseActivity implements
		OnClickListener {
	private TextView mHeadContent;
	private ImageView mHeadBack, mCallPhone;
	private double mLatitude;
	private double mLongitude;
	private LatLng mCurrentLocation;
	private String mWorktype;
	private String mZonePhone;
	private String mTaskNo;
	private String mElevatorNo;
	private boolean currentElevatorIsFinish;// 当前电梯的完成状态
	private ListView mTaskListData;
	private View mListFooterView;
	private Button mDone;
	private TaskListDetailsAdapter adapter;
	private TextView mTaskDetailsTitle;
	private final String TYPE1 = "CALL_PHONE";
	private final String TYPE2 = "CONFIRM";
	private Set<String> tagSet;
	private String TAG = "24梯";
	private QueryTaskListActivity activity;
	private String uid;
	private String certificate;
	ArrayList<MaintenanceType> list = new ArrayList<MaintenanceType>();
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.QUERY_TASK_PACKAGE_ELEVATOR_SUCCESS:
				String json = (String) msg.obj;
				ScanResultBean bean = gson.fromJson(json, ScanResultBean.class);
				BeginWorkResult currentElevator = null;
				String isMeetRequire = bean.body.isMeetRequire;// 是否满足维保要求
				if (TextUtils.equals(isMeetRequire, "1")) {// 满足
					List<BeginWorkResult> result = bean.body.data;
					// int count = 0;// 记录包中完成电梯的数量
					// for (int i = 0; i < result.size(); i++) {
					// if (result.get(i).getIsFinish().equals("2")) {
					// count++;
					// }
					// }
					for (int i = 0; i < result.size(); i++) {// 遍历电梯编号，得到当前电梯正在维保的电梯
						if (mElevatorNo.equals(result.get(i).elevatorNo)) {
							currentElevator = result.get(i);
						}
					}
					double latitude = Double
							.parseDouble(currentElevator.latitude);
					double longitude = Double
							.parseDouble(currentElevator.longitude);
					LatLng latlng = new LatLng(latitude, longitude);
					double distance = DistanceUtil.getDistance(
							mCurrentLocation, latlng);
					if (distance > 500.0) {
						Utilities.showToast("您距离维保电梯的距离超出范围", context);
						finish();
					} else {
						if (currentElevator.isFinish.equals("2")) {
							Utilities.showToast("你已经完成了该电梯", context);
							finish();
						} else {
							stopProgressDialog();
							mTaskNo = currentElevator.taskNo;
							mWorktype = currentElevator.workType;
							mZonePhone = currentElevator.zonePhone;
							showNiffyDialog(mTaskNo, mWorktype, mZonePhone);
						}
					}
				} else {
					Utilities.showToast("您尚未满足维保要求", context);// 维保要求包括，维保时间正确，有维保搭档，维保电梯正确
					finish();
				}
				break;
			case StatusCode.WORK_DETAILS_SUCCESS:
				String jsonWorkType = (String) msg.obj;
				WorkTypeBean beanWorkBean = gson.fromJson(jsonWorkType,
						WorkTypeBean.class);
				List<String> tempList = beanWorkBean.body.data;
				list.add(new MaintenanceType("0", "Title", "content"));
				for (int i = 0; i < tempList.size(); i++) {
					String allString = tempList.get(i);
					String[] data = allString.split("\\|");
					MaintenanceType type = new MaintenanceType(
							String.valueOf(i + 1), data[0], data[1]);
					list.add(type);
				}
				mTaskDetailsTitle.setVisibility(View.VISIBLE);
				adapter = new TaskListDetailsAdapter(context, list);
				mTaskListData.setAdapter(adapter);
				break;
			case StatusCode.MAINTENANCE_COMPLETE_SUCCESS:
				String maintenanceJson = (String) msg.obj;// 提交电梯完成状态后，后台返回的信息
				MaintenanceCompleteBean mComBean = gson.fromJson(
						maintenanceJson, MaintenanceCompleteBean.class);
				String updateMsg = mComBean.body.updateStatus;// 该电梯完成状态是否已经更新，0，表示更新失败，1表示更新成功
				String isAllCompleted = mComBean.body.isAllCompleted;// 对于维保的单台电梯，true代表该电梯两人都完成，false代表尚未完成或者有一人完成
				String taskStatus = mComBean.body.taskStatus;
				if (updateMsg.equals("1")) {
					currentElevatorIsFinish = true;
				} else {
					currentElevatorIsFinish = false;
				}
				if (TextUtils.equals(isAllCompleted, "1")) {
					tagSet.remove(mTaskNo);
					JPushInterface.setAliasAndTags(getApplicationContext(),
							null, tagSet, mTagsCallback);
					if (TextUtils.equals("0", taskStatus)) {
						// 任务包中还有未完成的
						Utilities.showToast("您还有未完成的电梯", context);
						Intent intent = new Intent(QueryTaskListActivity.this,
								TaskListPackageDetailActivity.class);
						intent.putExtra(Constant.TASKNO, mTaskNo);
						startActivity(intent);
						finish();
					} else {
						// 任务包中全部都完成了
						Intent intent = new Intent(QueryTaskListActivity.this,
								TaskListPackageDetailActivity.class);
						intent.putExtra(Constant.TASKNO, mTaskNo);
						startActivity(intent);
						finish();
					}
				} else {
					Utilities.showToast("请和搭档确认电梯的完成状态", context);
					Intent intent = new Intent(QueryTaskListActivity.this,
							TaskListPackageDetailActivity.class);
					intent.putExtra(Constant.TASKNO, mTaskNo);
					startActivity(intent);
					finish();
				}
				break;
			case StatusCode.MSG_SET_TAGS:
				JPushInterface.setAliasAndTags(getApplicationContext(), null,
						(Set<String>) msg.obj, mTagsCallback);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", context);
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast("服务器异常", context);
				break;
			}
			stopProgressDialog();
		};
	};

	private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			switch (code) {
			case 0:
				Log.d(TAG, "Set tag and alias success");
				SharePreferencesUtils.put(context,
						SharedPreferencesKeys.TAGSET, tags);// //
															// 成功保存标签后，将标签放到本地
				break;
			case 6002:
				Log.d(TAG,
						"Failed to set alias and tags due to timeout. Try again after 60s.");
				if (AppUtils.isConnected(getApplicationContext())) {
					handler.sendMessageDelayed(handler.obtainMessage(
							StatusCode.MSG_SET_TAGS, tags), 1000 * 60);
				} else {
					Log.i(TAG, "No network");
				}
				break;
			default:
				Log.d(TAG, "Failed with errorCode = " + code);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_details);
		activity = this;
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
		initTag();
		init();
		getExtraDataAndValidate();
	}

	private void initTag() {
		SharePreferencesUtils.get(activity, SharedPreferencesKeys.TAGSET,
				new LinkedHashSet<String>());
		Set<String> tempSet = (Set<String>) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.TAGSET, new LinkedHashSet<String>());
	}

	private void init() {
		context = QueryTaskListActivity.this;
		mLatitude = application.latitude;
		mLongitude = application.longitude;
		mCurrentLocation = new LatLng(mLatitude, mLongitude);
		mListFooterView = LayoutInflater.from(context).inflate(
				R.layout.listview_footer_done, null);
		mDone = (Button) mListFooterView.findViewById(R.id.btn_tasklist_done);
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mCallPhone = (ImageView) findViewById(R.id.iv_headTitleRight);
		mTaskDetailsTitle = (TextView) findViewById(R.id.tv_task_detail_title);
		mHeadContent.setText("维保清单");
		mHeadBack.setVisibility(View.VISIBLE);
		mCallPhone.setVisibility(View.VISIBLE);
		mTaskListData = (ListView) findViewById(R.id.lv_task_details);
		mTaskListData.addFooterView(mListFooterView);
		mHeadBack.setOnClickListener(this);
		mCallPhone.setOnClickListener(this);
		mDone.setOnClickListener(this);
	}

	private void getExtraDataAndValidate() {
		startProgressDialog("正在查询...");
		Intent intent = getIntent();
		mElevatorNo = intent.getStringExtra(Constant.ELEVATORNO);
		Requester requester = new Requester();
		requester.cmd = 20055;
		requester.uid = uid;
		requester.certificate = certificate;
		requester.body.put(Constant.ELEVATORNO, mElevatorNo);
		Request request = httpEngine.createRequest(SystemConfig.NEWIP,
				gson.toJson(requester));
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.obj = response.body().string();
					msg.what = StatusCode.QUERY_TASK_PACKAGE_ELEVATOR_SUCCESS;
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request request, IOException exception) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				handler.sendMessage(msg);
			}
		});
	}

	// 根据维保类型，获取维保任务列表
	private void getMaintenanceTaskListData() {
		startProgressDialog("正在加载...");
		Requester requester = new Requester();
		requester.uid = uid;
		requester.certificate = certificate;
		requester.cmd = 20060;
		requester.body.put(Constant.WORKTYPE, mWorktype);
		Request request = httpEngine.createRequest(SystemConfig.NEWIP,
				gson.toJson(requester));
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.WORK_DETAILS_SUCCESS;
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

	private void showNiffyDialog(String taskNo, String workType,
			String zonePhone) {
		Effectstype effect = Effectstype.Slideright;
		dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
				.withDividerColor("#11000000")
				.withMessage("请将电梯监测设备按钮调至维保状态后开始进行维保工作")
				.withMessageColor(R.color.main_primary)
				.withDialogColor("#FFFFFFFF").isCancelableOnTouchOutside(false)
				.withDuration(700).withEffect(effect)
				.withButtonDrawable(R.color.main_white).withButton1Text("取消")
				.withButton1Color("#DD47BEE9").withButton2Text("确认")
				.withButton2Color("#DD47BEE9")
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
						finish();
					}
				}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
						getMaintenanceTaskListData();
					}
				}).show();
	}

	private void showDialog(final String type, String message) {
		Effectstype effect = Effectstype.Slideright;
		dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
				.withDividerColor("#11000000").withMessage(message)
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
						if (TextUtils.equals(type, TYPE1)) {
							Intent intent = new Intent(Intent.ACTION_CALL, Uri
									.parse("tel:" + mZonePhone));
							startActivity(intent);
						} else {
							if (!currentElevatorIsFinish) {
								Requester requester = new Requester();
								requester.cmd = 20056;
								requester.uid = uid;
								requester.certificate = certificate;
								requester.body.put(Constant.ELEVATORNO,
										mElevatorNo);
								requester.body.put(Constant.TASKNO, mTaskNo);
								Request request = httpEngine.createRequest(
										SystemConfig.NEWIP,
										gson.toJson(requester));
								Call call = httpEngine
										.createRequestCall(request);
								call.enqueue(new Callback() {

									@Override
									public void onResponse(Response response)
											throws IOException {
										Message msg = new Message();
										if (response.isSuccessful()) {
											msg.what = StatusCode.MAINTENANCE_COMPLETE_SUCCESS;
											msg.obj = response.body().string();
										} else {
											msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
										}
										handler.sendMessage(msg);
									}

									@Override
									public void onFailure(Request request,
											IOException e) {
										Message msg = new Message();
										msg.what = StatusCode.RESPONSE_NET_FAILED;
										handler.sendMessage(msg);
									}
								});
							} else {
								Utilities.showToast("该电梯已经完成", context);
							}
						}
					}
				}).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_headBack:
			finish();
			break;
		case R.id.btn_tasklist_done:
			showDialog(TYPE2, "请确认维保工作已完成，并将电梯监测设备按钮调至正常状态!!!");
			break;
		case R.id.iv_headTitleRight:
			showDialog(TYPE1, "您确认要拨打技术支持电话？");
			break;
		default:
			break;
		}
	}
}
