package com.overtech.ems.activity.parttime.tasklist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
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
import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.TaskListPackageDetailAdapter;
import com.overtech.ems.activity.parttime.common.ElevatorDetailActivity;
import com.overtech.ems.config.StatusCode;
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

public class TaskListPackageDetailActivity extends BaseActivity implements
		OnRefreshListener {
	private ImageView mDoBack;
	private ListView mTask;
	private Button mCancle;
	private Button mToResponse;
	private TextView mTaskPackageName;
	private TextView mTaskNo;
	private LinearLayout shareToFriends;
	private LinearLayout dialToPartner;
	private String mPhone;
	private String mZonePhone;
	private String mZone;
	private String mPartnerName;
	private String taskNo;
	private String mLoginName;
	private Context mActivity;
	private Effectstype effect;
	private ImageView mDoMore;
	private PopupWindow popupWindow;
	private LatLng mStartPoint;
	private LatLng destination;
	private String mDesName;
	private boolean isToday;
	private SwipeRefreshLayout mSwipeLayout;
	private TaskListPackageDetailAdapter adapter;
	private ArrayList<TaskPackageDetail> list;

	private Set<String> tagSet;
	private String TAG = "24梯";
	/**
	 * 列表弹窗的间隔
	 */
	protected final int LIST_PADDING = 10;
	/**
	 * 实例化一个矩形
	 */
	private Rect mRect = new Rect();

	/**
	 * 坐标的位置（x、y）
	 */
	private final int[] mLocation = new int[2];

	/**
	 * 屏幕的宽度
	 */
	private int mScreenWidth;
	/**
	 * 屏幕的高度
	 */
	private int mScreenHeight;
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.PACKAGE_DETAILS_SUCCESS:
				String json = (String) msg.obj;
//				Log.e("==24t任务详情==", json);
				Gson gson = new Gson();
				TaskPackageDetailBean bean = gson.fromJson(json,
						TaskPackageDetailBean.class);
				list = (ArrayList<TaskPackageDetail>) bean.getModel();
				mPhone = bean.getPartnerPhone();
				mZonePhone = bean.getZonePhone();
				mPartnerName = bean.getPartnerName();
				mZone = bean.getZone();
				if (mPartnerName == null || mPhone == null) {
					shareToFriends.setVisibility(View.VISIBLE);
					dialToPartner.setVisibility(View.GONE);
				} else {
					shareToFriends.setVisibility(View.GONE);
					dialToPartner.setVisibility(View.VISIBLE);
				}
				if (null == list || list.size() == 0) {
					Utilities.showToast("无数据", mActivity);
					mCancle.setVisibility(View.GONE);
					mToResponse.setVisibility(View.GONE);
				} else {
					int count=0;//记录完成电梯的数量
					for(TaskPackageDetail data:list){
						if(data.getIsFinish().equals("2")){
							count++;
						}
					}
					if(count==list.size()){
						mToResponse.setVisibility(View.VISIBLE);
						mToResponse.setBackgroundColor(0xff00b9ef);
						mToResponse.setClickable(true);
						mCancle.setVisibility(View.GONE);
					}else{
						if (true==isToday){
							mToResponse.setVisibility(View.VISIBLE);
							mToResponse.setBackgroundColor(0xffcccccc);
							mToResponse.setClickable(false);
						    mCancle.setVisibility(View.GONE);
						}else {
							mToResponse.setVisibility(View.GONE);
							mCancle.setVisibility(View.VISIBLE);
						}

					}
					adapter = new TaskListPackageDetailAdapter(context, list);
					mTask.setAdapter(adapter);
				}
				break;
			case StatusCode.VALIDATE_TIME_SUCCESS:
				String time = (String) msg.obj;
				if (time.equals("-1")) {
					Utilities.showToast("已超过退单时间！！！", context);
					break;
				} else if (time.equals("0")) {
					Utilities.showToast("当天的任务不可以被退单！！！", context);
					break;
				} else if (time.equals("1")) {
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
						.withButton1Text("取消")
						.withButton1Color("#DD47BEE9")
						.withButton2Text("确认")
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
								startProgressDialog("正在退单...");
								Param param1 = new Param(Constant.TASKNO,taskNo);
								Param param2 = new Param(Constant.LOGINNAME,mSharedPreferences.getString(Constant.LOGINNAME, ""));
								startLoading(ServicesConfig.CHARGE_BACK_TASK,
										new com.squareup.okhttp.Callback() {

											@Override
											public void onResponse(Response response)throws IOException {
												Message msg = new Message();
												if (response.isSuccessful()) {
													msg.what = StatusCode.CHARGEBACK_SUCCESS;
													msg.obj = response.body().string();
												} else {
													msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
													msg.obj = "服务器异常";
												}
												handler.sendMessage(msg);
											}

											@Override
											public void onFailure(Request arg0,IOException arg1) {
												Message msg = new Message();
												msg.what = StatusCode.RESPONSE_NET_FAILED;
												msg.obj = "网络异常";
												handler.sendMessage(msg);
											}
										}, param1, param2);
							}
						}).show();
				break;
			case StatusCode.CHARGEBACK_SUCCESS:
				String state = (String) msg.obj;
				if (state.equals("true")) {
					tagSet.remove(taskNo);
					JPushInterface.setAliasAndTags(getApplicationContext(),null, tagSet, mTagsCallback);
				} else {
					Utilities.showToast("退单失败", context);
				}
				break;
			case StatusCode.MSG_SET_TAGS:
				Log.d("24梯", "Set tags in handler.");
				JPushInterface.setAliasAndTags(getApplicationContext(), null,(Set<String>) msg.obj, mTagsCallback);
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast((String) msg.obj, context);
				stopProgressDialog();
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast((String) msg.obj, context);
				stopProgressDialog();
			default:
				break;
			}
			mSwipeLayout.setRefreshing(false);
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
				Utilities.showToast("退单成功", context);
				stopProgressDialog();
				finish();
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
		setContentView(R.layout.activity_tasklist_package_detail);
		Set<String> tempSet = mSharedPreferences.getStringSet("tagSet", null);
		if (tempSet == null) {
			tagSet = new LinkedHashSet<String>();
		} else {
			tagSet = tempSet;
		}
		initView();
		initData();
		initEvent();
	}

	private void initEvent() {
		mActivity = TaskListPackageDetailActivity.this;
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
		mDoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTask.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				TaskPackageDetail detail = (TaskPackageDetail) parent.getItemAtPosition(position);
				if (detail.getIsFinish().equals("2")) {
					Utilities.showToast("你好，该电梯已经完成", mActivity);
				} else if (detail.getIsFinish().equals("1")) {
					Utilities.showToast("仍有人未完成该电梯的维保工作", context);
				} else {
					Utilities.showToast("请通过扫描二维码开启工作或者完成工作", context);
				}
				Intent intent = new Intent(context, ElevatorDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(Constant.ELEVATORNO, detail.getElevatorNo());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Param param = new Param(Constant.TASKNO, taskNo);
				startLoading(ServicesConfig.CHARGE_BACK_TASK_VALIDATE_TIME,
						new Callback() {

							@Override
							public void onResponse(Response response) throws IOException {
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
		});
		mToResponse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TaskListPackageDetailActivity.this,
						QuestionResponseActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(Constant.TASKNO, taskNo);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		initPopupWindow();
		mDoMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopupWindow(v);
			}
		});
	}

	private void initPopupWindow() {
		mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
		mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
		popupWindow = new PopupWindow(mActivity);
		popupWindow.setWidth(mScreenWidth / 2);
		popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setContentView(LayoutInflater.from(activity).inflate(R.layout.layout_tasklist_pop, null));
		initUI();
	}

	// 对popupWindow的ui进行初始化
	private void initUI() {
		popupWindow.getContentView().findViewById(R.id.ll_pop_1).setOnClickListener(// 地图导航
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						startNavicate(mStartPoint, destination, "终点");
					}
				});
		shareToFriends = (LinearLayout) popupWindow.getContentView().findViewById(R.id.ll_pop_2);
		shareToFriends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				shareToFriends();
			}
		});
		dialToPartner = (LinearLayout) popupWindow.getContentView().findViewById(R.id.ll_pop_3);
		dialToPartner.setOnClickListener(// 拨打搭档电话
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Effectstype effect = Effectstype.Shake;
						dialogBuilder.withTitle("温馨提示")
								.withTitleColor(R.color.main_primary)
								.withDividerColor("#11000000")
								.withMessage("您确认要拨打电话给您的搭档：" + mPartnerName)
								.withMessageColor(R.color.main_primary)
								.withDialogColor("#FFFFFFFF")
								.isCancelableOnTouchOutside(true)
								.withDuration(700).withEffect(effect)
								.withButtonDrawable(R.color.main_white)
								.withButton1Text("取消")
								.withButton1Color("#DD47BEE9")
								.withButton2Text("确认")
								.withButton2Color("#DD47BEE9")
								.setButton1Click(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										dialogBuilder.dismiss();
									}
								}).setButton2Click(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhone));
								startActivity(intent);
							}
						}).show();
					}
				});
	}

	protected void shareToFriends() {// 分享给好友
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		oks.setTitleUrl("http://www.wandoujia.com/apps/com.overtech.ems");
		oks.setText("我在24T中抢到" + mZone + "的一个维保单，单号为:" + taskNo + ",请速度去抢哦！App下载链接：http://www.wandoujia.com/apps/com.overtech.ems");
		oks.setVenueName("24T");
		oks.show(this);
	}
	// 弹出popupWindow
	protected void showPopupWindow(View v) {
		v.getLocationOnScreen(mLocation);
		// 设置矩形的大小
		mRect.set(mLocation[0], mLocation[1], mLocation[0] + v.getWidth(),mLocation[1] + v.getHeight());
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, mScreenWidth- LIST_PADDING - (popupWindow.getWidth() / 2), mRect.bottom);
	}

	public void startNavicate(LatLng startPoint, LatLng endPoint, String endName) {
		// 构建 route搜索参数
		RouteParaOption para = new RouteParaOption().startName("我的位置")
				.startPoint(startPoint)
				// 路线检索起点
				.endPoint(endPoint)
				// 路线检索终点
				// .endName(endName)
				.endName("终点")
				.busStrategyType(EBusStrategyType.bus_recommend_way);
		try {
			BaiduMapRoutePlan.setSupportWebRoute(true);
			BaiduMapRoutePlan.openBaiduMapTransitRoute(para, mActivity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initData() {
		Bundle bundle = getIntent().getExtras();
		mStartPoint = bundle.getParcelable(Constant.CURLOCATION);
		destination = bundle.getParcelable(Constant.DESTINATION);
		mDesName = bundle.getString(Constant.DESNAME);
		isToday=bundle.getBoolean(Constant.ISTODAY);
		String taskPackage = bundle.getString(Constant.TASKPACKAGENAME);
		taskNo = bundle.getString(Constant.TASKNO);
		mTaskPackageName.setText(taskPackage);
		mTaskNo.setText(taskNo);
		mLoginName = mSharedPreferences.getString(SharedPreferencesKeys.CURRENT_LOGIN_NAME, null);
		Param param = new Param(Constant.TASKNO, taskNo);
		Param param2 = new Param(Constant.LOGINNAME, mLoginName);
		startLoading(ServicesConfig.TASK_PACKAGE_DETAIL, new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.PACKAGE_DETAILS_SUCCESS;
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
		}, param, param2);
	}

	private void initView() {
		mDoBack = (ImageView) findViewById(R.id.iv_grab_headBack);
		mTask = (ListView) findViewById(R.id.lv_tasklist);
		mCancle = (Button) findViewById(R.id.bt_cancle_task);
		mToResponse=(Button) findViewById(R.id.bt_next_response);
		mDoMore = (ImageView) findViewById(R.id.iv_navicate_right);
		mDoMore.setBackgroundResource(R.drawable.icon_common_more);
		mDoMore.setVisibility(View.VISIBLE);
		mTaskPackageName = (TextView) findViewById(R.id.tv_headTitle_community_name);
		mTaskNo = (TextView) findViewById(R.id.tv_headTitle_taskno);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.srl_container);
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
		Param param = new Param(Constant.TASKNO, taskNo);
		Param param2 = new Param(Constant.LOGINNAME, mLoginName);
		startLoading(ServicesConfig.TASK_PACKAGE_DETAIL, new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.PACKAGE_DETAILS_SUCCESS;
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
		}, param, param2);
	}

	protected void startLoading(String url, Callback callback, Param... params) {
		Request request = httpEngine.createRequest(url, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(callback);
	}
}
