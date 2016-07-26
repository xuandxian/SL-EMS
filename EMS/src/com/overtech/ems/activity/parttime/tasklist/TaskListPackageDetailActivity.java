package com.overtech.ems.activity.parttime.tasklist;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.adapter.TaskListPackageDetailAdapter;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.parttime.common.ElevatorDetailActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.Bean;
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
import com.squareup.okhttp.Request;

/*
 *任务包详情(任务单模块)
 * 
 */
public class TaskListPackageDetailActivity extends BaseActivity implements
		OnRefreshListener, OnClickListener {
	private ImageView mDoBack;
	private ListView mTaskListView;
	private Button mDoChargeBack;
	private Button mDoResponse;
	private TextView mTaskPackageName;
	private TextView mTaskNo;
	private LinearLayout shareToFriends;
	private LinearLayout dialToPartner;
	private String sPhone;
	private String sTaskPackageName;
	private String latitude; // 纬度
	private String longitude; // 经度
	private String maintenanceDate; // 维保日期
	private String sZone;
	private String sPartnerName;
	private String sTaskNo;
	private String mLoginName;
	private Effectstype effect;
	private ImageView mDoMore;
	private PopupWindow popupWindow;
	private LatLng mStartPoint;
	private LatLng destination;
	// private String mDesName;
	private boolean isToday;
	private SwipeRefreshLayout mSwipeLayout;
	private TaskListPackageDetailAdapter adapter;
	private List<Map<String, Object>> list;
	private Set<String> tagSet;
	private String TAG = "24梯";
	protected final int LIST_PADDING = 10;// 列表弹窗的间隔
	private Rect mRect = new Rect();// 实例化一个矩形
	private final int[] mLocation = new int[2];// 坐标的位置（x、y）
	private int mScreenWidth;// 屏幕的宽度
	private int mScreenHeight;// 屏幕的高度
	private final String ALLCOMPLETE = "allComplete";
	private final String NOTCOMPLETE = "notComplete";
	private String uid;
	private String certificate;
	private TaskListPackageDetailActivity activity;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.MSG_SET_TAGS:
				Log.d("24梯", "Set tags in handler.");
				JPushInterface.setAliasAndTags(getApplicationContext(), null,
						(Set<String>) msg.obj, mTagsCallback);
				break;
			default:
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
				Log.d(TAG, logs);
				SharePreferencesUtils.put(activity,
						SharedPreferencesKeys.TAGSET, tags);// //
															// 成功保存标签后，将标签放到本地
				Utilities.showToast("退单成功", activity);
				stopProgressDialog();
				stackInstance.popActivity(activity);
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
		setContentView(R.layout.activity_tasklist_package_detail);
		initTag();
		initView();
		getExtraDataAndInit();
		initEvent();
	}

	private void initTag() {
		activity = TaskListPackageDetailActivity.this;
		stackInstance.pushActivity(activity);
		Set<String> tempSet = (Set<String>) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.TAGSET, new LinkedHashSet<String>());
	}

	private void initView() {
		mStartPoint = new LatLng(
				((MyApplication) getApplicationContext()).latitude,
				(((MyApplication) getApplicationContext())).longitude);
		mDoBack = (ImageView) findViewById(R.id.iv_grab_headBack);
		mTaskListView = (ListView) findViewById(R.id.lv_tasklist);
		mDoChargeBack = (Button) findViewById(R.id.bt_cancle_task);
		mDoResponse = (Button) findViewById(R.id.bt_next_response);
		mDoMore = (ImageView) findViewById(R.id.iv_navicate_right);
		mDoMore.setBackgroundResource(R.drawable.icon_common_more);
		mDoMore.setVisibility(View.VISIBLE);
		mTaskPackageName = (TextView) findViewById(R.id.tv_headTitle_community_name);
		mTaskNo = (TextView) findViewById(R.id.tv_headTitle_taskno);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.srl_container);
	}

	private void getExtraDataAndInit() {
		Intent intent = getIntent();
		sTaskNo = intent.getStringExtra(Constant.TASKNO);
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
		onRefresh();
	}

	private void initEvent() {
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.material_deep_teal_200,
				R.color.material_deep_teal_500);
		mDoBack.setOnClickListener(this);
		mDoChargeBack.setOnClickListener(this);
		mDoResponse.setOnClickListener(this);
		mDoMore.setOnClickListener(this);
		mTaskListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map<String, Object> detail = (Map<String, Object>) parent
						.getItemAtPosition(position);
				Intent intent = new Intent(activity,
						ElevatorDetailActivity.class);
				intent.putExtra(Constant.ELEVATORNO, detail.get("elevatorNo")
						.toString());
				startActivity(intent);
			}
		});
		initPopupWindow();
	}

	private void initPopupWindow() {
		if (popupWindow == null) {
			mScreenWidth = activity.getResources().getDisplayMetrics().widthPixels;
			mScreenHeight = activity.getResources().getDisplayMetrics().heightPixels;
			popupWindow = new PopupWindow(activity);
			popupWindow.setWidth(mScreenWidth / 2);
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			popupWindow.setFocusable(true);
			popupWindow.setTouchable(true);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setContentView(LayoutInflater.from(this).inflate(
					R.layout.layout_tasklist_pop, null));
			initUI();
		}
	}

	// 对popupWindow的ui进行初始化
	private void initUI() {
		popupWindow.getContentView().findViewById(R.id.ll_pop_1)
				.setOnClickListener(// 地图导航
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								startNavicate(mStartPoint, destination, "终点");
							}
						});
		shareToFriends = (LinearLayout) popupWindow.getContentView()
				.findViewById(R.id.ll_pop_2);
		shareToFriends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				shareToFriends();
			}
		});
		dialToPartner = (LinearLayout) popupWindow.getContentView()
				.findViewById(R.id.ll_pop_3);
		dialToPartner.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Effectstype effect = Effectstype.Shake;
				dialogBuilder.withTitle("温馨提示")
						.withTitleColor(R.color.main_primary)
						.withDividerColor("#11000000")
						.withMessage("您确认要拨打电话给您的搭档：" + sPartnerName)
						.withMessageColor(R.color.main_primary)
						.withDialogColor("#FFFFFFFF")
						.isCancelableOnTouchOutside(true).withDuration(700)
						.withEffect(effect)
						.withButtonDrawable(R.color.main_white)
						.withButton1Text("取消").withButton1Color("#DD47BEE9")
						.withButton2Text("确认").withButton2Color("#DD47BEE9")
						.setButton1Click(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogBuilder.dismiss();
							}
						}).setButton2Click(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(Intent.ACTION_CALL,
										Uri.parse("tel:" + sPhone));
								startActivity(intent);
							}
						}).show();
			}
		});
	}

	protected void shareToFriends() {// 分享给好友
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		oks.setText("我在24T中抢到"
				+ sZone
				+ "的一个维保单，单号为:"
				+ sTaskNo
				+ ",请速度去抢哦！App下载链接：http://www.wandoujia.com/apps/com.overtech.ems");
		oks.setUrl("http://www.wandoujia.com/apps/com.overtech.ems");
		oks.setVenueName("24T");
		oks.show(this);
	}

	public void startNavicate(LatLng startPoint, LatLng endPoint, String endName) {
		RouteParaOption para = new RouteParaOption().startName("我的位置")
				.startPoint(startPoint).endPoint(endPoint).endName("终点")
				.busStrategyType(EBusStrategyType.bus_recommend_way);
		try {
			BaiduMapRoutePlan.setSupportWebRoute(true);
			BaiduMapRoutePlan.openBaiduMapTransitRoute(para, activity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case StatusCode.RESULT_TASKLIST_PACKAGEDETAIL:
			onRefresh();
			break;
		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		Requester requester = new Requester();
		requester.cmd = 20051;
		requester.certificate = certificate;
		requester.uid = uid;
		requester.body.put(Constant.TASKNO, sTaskNo);
		ResultCallback<Bean> callback = new ResultCallback<Bean>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				if (mSwipeLayout.isRefreshing()) {
					mSwipeLayout.setRefreshing(false);
				}
				Logr.e(request.toString());
			}

			@Override
			public void onResponse(Bean response) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				if (mSwipeLayout.isRefreshing()) {
					mSwipeLayout.setRefreshing(false);
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
					return;
				} else if (st == 1) {
					Utilities.showToast(response.msg, activity);
					return;
				}
				list = (List<Map<String, Object>>) response.body.get("data");
				sPhone = response.body.get("partnerPhone").toString();
				sTaskPackageName = response.body.get("taskPackageName")
						.toString();
				latitude = response.body.get("latitude").toString();
				longitude = response.body.get("longitude").toString();
				destination = new LatLng(Double.valueOf(latitude),
						Double.valueOf(longitude));
				maintenanceDate = response.body.get("maintenanceDate")
						.toString();
				sPartnerName = response.body.get("partnerName").toString();
				sZone = response.body.get("zone").toString();
				mTaskNo.setText(sTaskNo);
				mTaskPackageName.setText(sTaskPackageName);
				if (TextUtils.isEmpty(sPartnerName)
						|| TextUtils.isEmpty(sPhone)) {
					shareToFriends.setVisibility(View.VISIBLE);
					dialToPartner.setVisibility(View.GONE);
				} else {
					shareToFriends.setVisibility(View.GONE);
					dialToPartner.setVisibility(View.VISIBLE);
				}
				if (null == list || list.size() == 0) {
					Utilities
							.showToast(
									getResources().getString(
											R.string.response_no_data),
									activity);
					mDoChargeBack.setVisibility(View.GONE);
					mDoResponse.setVisibility(View.GONE);
				} else {

					adapter = new TaskListPackageDetailAdapter(activity, list);
					mTaskListView.setAdapter(adapter);
					int count = 0;// 记录完成电梯的数量
					for (Map<String, Object> data : list) {
						if (data.get("isFinish").equals("2")) {
							count++;
						}
					}
					if (count == list.size()) {
						mDoResponse.setVisibility(View.VISIBLE);
						mDoResponse.setBackgroundResource(R.color.btn_visiable_bg);//绿色
						mDoResponse.setTag(ALLCOMPLETE);
						mDoChargeBack.setVisibility(View.GONE);
					} else {
						isToday = Utilities.isToday(maintenanceDate);
						Logr.e("==是不是当天==" + isToday);
						if (isToday) {
							mDoResponse.setVisibility(View.VISIBLE);
							mDoResponse.setBackgroundResource(R.color.btn_disable_bg);//灰色
							mDoResponse.setTag(NOTCOMPLETE);
							mDoChargeBack.setVisibility(View.GONE);
						} else {
							mDoResponse.setVisibility(View.GONE);
							mDoChargeBack.setVisibility(View.VISIBLE);
						}
					}

				}
			}
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));
	}

	private void validateChargebackTime() {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		Requester requestVali = new Requester();
		requestVali.cmd = 20052;
		requestVali.certificate = certificate;
		requestVali.uid = uid;
		requestVali.body.put(Constant.TASKNO, sTaskNo);
		ResultCallback<Bean> callback = new ResultCallback<Bean>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				if (mSwipeLayout.isRefreshing()) {
					mSwipeLayout.setRefreshing(false);
				}
				Logr.e(request.toString());
			}

			@Override
			public void onResponse(Bean response) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				if (mSwipeLayout.isRefreshing()) {
					mSwipeLayout.setRefreshing(false);
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
					return;
				} else if (st == 1) {
					Utilities.showToast(response.msg, activity);
					return;
				}
				String result = response.body.get("result").toString();
				if (result.equals("-1")) {
					Utilities.showToast(response.msg, activity);
					return;
				} else if (result.equals("0")) {
					Utilities.showToast(response.msg, activity);
					return;
				} else if (result.equals("1")) {
					dialogBuilder.withMessage("72小时内退单会影响星级评定，你确认要退单？");
				} else {
					dialogBuilder.withMessage("你确认要退单？");
				}
				effect = Effectstype.Slideright;
				dialogBuilder.withTitle("温馨提示")
						.withTitleColor(R.color.main_primary)
						.withDividerColor("#11000000")
						.withMessageColor(R.color.main_primary)
						.withDialogColor("#FFFFFFFF")
						.isCancelableOnTouchOutside(true).withDuration(700)
						.withEffect(effect)
						.withButtonDrawable(R.color.main_white)
						.withButton1Text("取消").withButton1Color("#DD47BEE9")
						.withButton2Text("确认").withButton2Color("#DD47BEE9")
						.setButton1Click(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogBuilder.dismiss();
							}
						}).setButton2Click(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogBuilder.dismiss();
								chargeback();
							}
						}).show();
			}
		};
		String jsonData = gson.toJson(requestVali);
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback, jsonData);
	}

	private void chargeback() {
		startProgressDialog("正在退单...");
		Requester requester = new Requester();
		requester.cmd = 20053;
		requester.certificate = certificate;
		requester.uid = uid;
		requester.body.put(Constant.TASKNO, sTaskNo);
		ResultCallback<Bean> callback = new ResultCallback<Bean>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				Logr.e(request.toString());
			}

			@Override
			public void onResponse(Bean response) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				int st = response.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(response.msg, activity);
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(activity, LoginActivity.class);
					startActivity(intent);
					return;
				} else if (st == 1) {
					Utilities.showToast(response.msg, activity);
					return;
				} else {
					Utilities.showToast(response.msg, activity);
					tagSet.remove(sTaskNo);
					JPushInterface.setAliasAndTags(getApplicationContext(),
							null, tagSet, mTagsCallback);
				}
			}
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_grab_headBack:
			stackInstance.popActivity(activity);
			break;
		case R.id.bt_cancle_task:
			Logr.e("退单验证时间==");
			validateChargebackTime();
			break;
		case R.id.bt_next_response:
			if (mDoResponse.getTag().equals(ALLCOMPLETE)) {
				Intent intent = new Intent(TaskListPackageDetailActivity.this,
						QuestionResponseActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(Constant.TASKNO, sTaskNo);
				intent.putExtras(bundle);
				startActivity(intent);
			} else if (mDoResponse.getTag().equals(NOTCOMPLETE)) {
				Utilities.showToast("尚有未完成的电梯", activity);
			}
			break;
		case R.id.iv_navicate_right:
			showPopupWindow(v);
			break;
		}
	}

	// 弹出popupWindow
	protected void showPopupWindow(View v) {
		v.getLocationOnScreen(mLocation);
		// 设置矩形的大小
		mRect.set(mLocation[0], mLocation[1], mLocation[0] + v.getWidth(),
				mLocation[1] + v.getHeight());
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, mScreenWidth
				- LIST_PADDING - (popupWindow.getWidth() / 2), mRect.bottom);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}
}
