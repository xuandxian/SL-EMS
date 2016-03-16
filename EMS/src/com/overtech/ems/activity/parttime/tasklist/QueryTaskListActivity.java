package com.overtech.ems.activity.parttime.tasklist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
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
import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.entity.parttime.ScanResultBean;
import com.overtech.ems.entity.bean.BeginWorkResult;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.TaskListDetailsAdapter;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.WorkTypeBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.MaintenanceType;
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
 *维保清单列表
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
	// 当前电梯的完成状态
	private boolean currentElevatorIsFinish;
	private ListView mTaskListData;
	private View mListFooterView;
	private Button mDone;
	private TaskListDetailsAdapter adapter;
	private TextView mTaskDetailsTitle;
	private final String TYPE1 = "CALL_PHONE";
	private final String TYPE2 = "CONFIRM";
	private Set<String> tagSet;
	private String TAG = "24梯";
	/**
	 * 是否可以完成工作，true代表是当天的任务可以完成，false代表不是维保时间的任务，不可以完成
	 */
	private boolean isCanConfirmDone;

	ArrayList<MaintenanceType> list = new ArrayList<MaintenanceType>();

	private Handler handler = new Handler() {
		Gson gson = new Gson();

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case StatusCode.QUERY_TASK_PACKAGE_ELEVATOR_SUCCESS:
					String json = (String) msg.obj;
					ScanResultBean bean = gson.fromJson(json, ScanResultBean.class);
					BeginWorkResult currentElevator = null;
					boolean isTrue = bean.isSuccess();//是否满足维保要求
					if (isTrue) {
						List<BeginWorkResult> result = bean.getModel();
//						int count = 0;// 记录包中完成电梯的数量
//						for (int i = 0; i < result.size(); i++) {
//							if (result.get(i).getIsFinish().equals("2")) {
//								count++;
//							}
//						}
						for (int i = 0; i < result.size(); i++) {//遍历电梯编号，得到当前电梯正在维保的电梯
							if (mElevatorNo.equals(result.get(i).getElevatorNo())) {
								currentElevator = result.get(i);
							}
						}
						double latitude = Double.parseDouble(currentElevator.getLatitude());
						double longitude = Double.parseDouble(currentElevator.getLongitude());
						String taskNo = currentElevator.getTaskNo();
						String workType = currentElevator.getWorkType();
						String zonePhone = currentElevator.getZonePhone();
						LatLng latlng = new LatLng(latitude, longitude);
						double distance = DistanceUtil.getDistance(mCurrentLocation, latlng);
						if (distance > 500.0) {
							Utilities.showToast("您距离维保电梯的距离超出范围", context);
							finish();
						}else {
							if(currentElevator.getIsFinish().equals("2")){
								Utilities.showToast("你已经为完成了该电梯", context);
								finish();
							}else {
								stopProgressDialog();
								showNiffyDialog(taskNo, workType, zonePhone);
							}
						}
					} else {
						Utilities.showToast("您尚未满足维保要求", context);// 维保要求包括，维保时间正确，有维保搭档，维保电梯正确
						finish();
					}
					break;
			case StatusCode.WORK_DETAILS_SUCCESS:
				String jsonWorkType = (String) msg.obj;
				WorkTypeBean beanWorkBean = gson.fromJson(jsonWorkType, WorkTypeBean.class);
				ArrayList<String> tempList = beanWorkBean.getModel();
				list.add(new MaintenanceType("0", "Title", "content"));
				for (int i = 0; i < tempList.size(); i++) {
					String allString = tempList.get(i);
					String[] data = allString.split("\\|");
					MaintenanceType type = new MaintenanceType(String.valueOf(i + 1), data[0], data[1]);
					list.add(type);
				}
				mTaskDetailsTitle.setVisibility(View.VISIBLE);
				adapter = new TaskListDetailsAdapter(context, list);
				mTaskListData.setAdapter(adapter);
				break;
			case StatusCode.MAINTENANCE_COMPLETE_SUCCESS:
				String maintenanceJson = (String) msg.obj;// 提交电梯完成状态后，后台返回的信息
				try {
					JSONObject jsonObject = new JSONObject(maintenanceJson);
					String updateMsg = jsonObject.getString("msg");// 该电梯完成状态是否已经更新，0，表示更新失败，1表示更新成功
					boolean completeProgress = jsonObject.getBoolean("success");// 对于维保的单台电梯，true代表该电梯两人都完成，false代表尚未完成或者有一人完成
					if (updateMsg.equals("1")) {
						currentElevatorIsFinish = true;
					} else {
						currentElevatorIsFinish = false;
					}
					if (completeProgress) {
						tagSet.remove(mTaskNo);
						JPushInterface.setAliasAndTags(getApplicationContext(),null, tagSet, mTagsCallback);
						if (!jsonObject.isNull("taskState")) {
							String taskState = jsonObject.getString("taskState");
							if (taskState.equals("0")) {
								// 任务包中还有未完成的
								Utilities.showToast("您还有未完成的电梯", context);
								finish();
							} else {
								// 任务包中全部都完成了
								Intent intent = new Intent(QueryTaskListActivity.this,QuestionResponseActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString(Constant.TASKNO, mTaskNo);
								intent.putExtras(bundle);
								startActivity(intent);
								finish();
							}
						} else {
							Utilities.showToast("查询其他电梯状态失败", context);
						}
					} else {
						Utilities.showToast("请和搭档确认电梯的完成状态", context);
						finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case StatusCode.VALIDATE_TIME_SUCCESS:
				String time = (String) msg.obj;
				if (time.equals("0")) {
					isCanConfirmDone = true;
				} else {
					isCanConfirmDone = false;
				}
				break;
			case StatusCode.MSG_SET_TAGS:
				JPushInterface.setAliasAndTags(getApplicationContext(), null,(Set<String>) msg.obj, mTagsCallback);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast((String) msg.obj, context);
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast((String) msg.obj, context);
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
				/*
				 * // 当该电梯中所有的电梯都完成后，并且标签也在jpush后台注册成功后，开始问题反馈； Intent intent =
				 * new Intent(QueryTaskListActivity.this,
				 * QuestionResponseActivity.class); Bundle bundle = new
				 * Bundle(); bundle.putString(Constant.TASKNO, mTaskNo);
				 * intent.putExtras(bundle); startActivity(intent); finish();
				 */

				break;

			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Log.d(TAG, logs);
				if (AppUtils.isConnected(getApplicationContext())) {
					handler.sendMessageDelayed(handler.obtainMessage(StatusCode.MSG_SET_TAGS, tags), 1000 * 60);
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
		setContentView(R.layout.activity_task_details);
		Set<String> tempSet = mSharedPreferences.getStringSet("tagSet", null);
		if (tempSet == null) {
			tagSet = new LinkedHashSet<String>();
		} else {
			tagSet = tempSet;
		}
		mLatitude = application.latitude;
		mLongitude = application.longitude;
		mCurrentLocation = new LatLng(mLatitude, mLongitude);
		getExtraDataAndValidate();
		init();
		initEvent();
		getData();
	}

	private void getExtraDataAndValidate() {
		Intent intent = getIntent();
		mElevatorNo=intent.getStringExtra(Constant.ELEVATORNO);
		Param param = new Param(Constant.ELEVATORNO, mElevatorNo);
		Param param2 = new Param(Constant.LOGINNAME, mSharedPreferences.getString(SharedPreferencesKeys.CURRENT_LOGIN_NAME, ""));
		Request request = httpEngine.createRequest(ServicesConfig.QUERY_TASK_PACKAGE_ELEVATOR, param, param2);
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

	private void init() {
		context = QueryTaskListActivity.this;
		mListFooterView = LayoutInflater.from(context).inflate(R.layout.listview_footer_done, null);
		mDone = (Button) mListFooterView.findViewById(R.id.btn_login);
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mCallPhone = (ImageView) findViewById(R.id.iv_headTitleRight);
		mTaskDetailsTitle = (TextView) findViewById(R.id.tv_task_detail_title);
		mHeadContent.setText("维保清单");
		mHeadBack.setVisibility(View.VISIBLE);
		mCallPhone.setVisibility(View.VISIBLE);
		mTaskListData = (ListView) findViewById(R.id.lv_task_details);
		mTaskListData.addFooterView(mListFooterView);
	}

	private void initEvent() {
		mHeadBack.setOnClickListener(this);
		mCallPhone.setOnClickListener(this);
		mDone.setOnClickListener(this);
	}

	private void getData() {
		startProgressDialog("正在加载...");
		validateDate();
		Param param = new Param(Constant.WORKTYPE, mWorktype);
		startLoading(ServicesConfig.WORK_TYPE, new Callback() {
			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.WORK_DETAILS_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
					msg.obj = "服务器异常";
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request request, IOException e) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				msg.obj = "网络异常";
				handler.sendMessage(msg);
			}
		}, param);
		
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
		case R.id.btn_login:
			// 将对应的电梯的完成状态更新到服务器
			if (isCanConfirmDone) {
				showDialog(TYPE2, "请确认维保工作已完成，并将电梯监测设备按钮调至正常状态!!!");
			} else {
				Utilities.showToast("维保任务必须在当天完成", context);
			}
			break;
		case R.id.iv_headTitleRight:
			showDialog(TYPE1, "您确认要拨打技术支持电话？");
			break;
		default:
			break;
		}
	}

	/**
	 * 请求服务器对任务单的维保时间进行验证
	 */
	private void validateDate() {
		Param param = new Param(Constant.TASKNO, mTaskNo);
		startLoading(ServicesConfig.CHARGE_BACK_TASK_VALIDATE_TIME,
				new Callback() {

					@Override
					public void onResponse(Response response)
							throws IOException {
						Message msg = new Message();
						if (response.isSuccessful()) {
							msg.what = StatusCode.VALIDATE_TIME_SUCCESS;
							msg.obj = response.body().string();
						} else {
							msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
							msg.obj = "服务器异常";
						}
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(Request arg0, IOException arg1) {
						Message msg = new Message();
						msg.what = StatusCode.RESPONSE_NET_FAILED;
						msg.obj = "网络异常";
						handler.sendMessage(msg);
					}
				}, param);
	}

	protected void startLoading(String url, Callback callback, Param... params) {
		Request request = httpEngine.createRequest(url, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(callback);
	}
	private void showNiffyDialog(String taskNo, String workType,String zonePhone){
		Effectstype effect = Effectstype.Slideright;
		dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
				.withDividerColor("#11000000").withMessage("请将电梯监测设备按钮调至维保状态后开始进行维保工作")
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
							Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mZonePhone));
							startActivity(intent);
						} else {
							if (!currentElevatorIsFinish) {
								Param taskNoParam = new Param(Constant.TASKNO,mTaskNo);
								Param elevatorNoParam = new Param(Constant.ELEVATORNO, mElevatorNo);
<<<<<<< HEAD
								Param loginNameParam = new Param(Constant.LOGINNAME, mSharedPreferences.getString(SharedPreferencesKeys.CURRENT_LOGIN_NAME, ""));
								startLoading(ServicesConfig.MAINTENCE_LIST_COMPLETE, new Callback() {
											@Override
											public void onResponse(Response response) throws IOException {
=======
								Param loginNameParam = new Param(Constant.LOGINNAME,mSharedPreferences.getString(SharedPreferencesKeys.CURRENT_LOGIN_NAME,""));
								startLoading(ServicesConfig.MAINTENCE_LIST_COMPLETE,new Callback() {
											@Override
											public void onResponse(Response response)throws IOException {
>>>>>>> origin/master
												Message msg = new Message();
												if (response.isSuccessful()) {
													msg.what = StatusCode.MAINTENANCE_COMPLETE_SUCCESS;
													msg.obj = response.body().string();
												} else {
													msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
													msg.obj = "服务器异常";
												}
												handler.sendMessage(msg);
											}

											@Override
<<<<<<< HEAD
											public void onFailure(Request arg0, IOException arg1) {
=======
											public void onFailure(Request arg0,IOException arg1) {
>>>>>>> origin/master
												Message msg = new Message();
												msg.what = StatusCode.RESPONSE_NET_FAILED;
												msg.obj = "网络异常";
												handler.sendMessage(msg);
											}
<<<<<<< HEAD
										}, taskNoParam, elevatorNoParam, loginNameParam);
=======
										}, taskNoParam, elevatorNoParam,loginNameParam);
>>>>>>> origin/master
							} else {
								Utilities.showToast("该电梯已经完成", context);
							}
						}
					}
<<<<<<< HEAD
		}).show();
=======
				}).show();
	}

	/**
	 * 请求服务器对任务单的维保时间进行验证
	 */
	private void validateDate() {
		Param param = new Param(Constant.TASKNO, mTaskNo);
		startLoading(ServicesConfig.CHARGE_BACK_TASK_VALIDATE_TIME,
				new Callback() {

					@Override
					public void onResponse(Response response)throws IOException {
						Message msg = new Message();
						if (response.isSuccessful()) {
							msg.what = StatusCode.VALIDATE_TIME_SUCCESS;
							msg.obj = response.body().string();
						} else {
							msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
							msg.obj = "服务器异常";
						}
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(Request arg0, IOException arg1) {
						Message msg = new Message();
						msg.what = StatusCode.RESPONSE_NET_FAILED;
						msg.obj = "网络异常";
						handler.sendMessage(msg);
					}
				}, param);
	}

	protected void startLoading(String url, Callback callback, Param... params) {
		Request request = httpEngine.createRequest(url, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(callback);
>>>>>>> origin/master
	}
}
