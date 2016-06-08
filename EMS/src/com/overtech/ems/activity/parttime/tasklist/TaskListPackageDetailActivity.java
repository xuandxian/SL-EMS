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
import com.overtech.ems.activity.MyApplication;
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
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
/*
 *任务包详情(任务单模块)
 * 
 */
public class TaskListPackageDetailActivity extends BaseActivity implements OnRefreshListener,OnClickListener {
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
	private String latitude;           //纬度
	private String longitude;          //经度
	private String maintenanceDate;    //维保日期
	private String sZone;
	private String sPartnerName;
	private String sTaskNo;
	private String mLoginName;
	private Context mActivity;
	private Effectstype effect;
	private ImageView mDoMore;
	private PopupWindow popupWindow;
	private LatLng mStartPoint;
	private LatLng destination;
//	private String mDesName;
	private boolean isToday;
	private SwipeRefreshLayout mSwipeLayout;
	private TaskListPackageDetailAdapter adapter;
	private ArrayList<TaskPackageDetail> list;
	private Set<String> tagSet;
	private String TAG = "24梯";
	protected final int LIST_PADDING = 10;//列表弹窗的间隔
	private Rect mRect = new Rect();//实例化一个矩形
	private final int[] mLocation = new int[2];//坐标的位置（x、y）
	private int mScreenWidth;//屏幕的宽度
	private int mScreenHeight;//屏幕的高度

	
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.PACKAGE_DETAILS_SUCCESS:
				String json = (String) msg.obj;
				Gson gson = new Gson();
				TaskPackageDetailBean bean = gson.fromJson(json,TaskPackageDetailBean.class);
				list = (ArrayList<TaskPackageDetail>) bean.getModel();
				sPhone = bean.getPartnerPhone();
				sTaskPackageName = bean.getTaskPackageName();
				latitude=bean.getLatitude();
				longitude=bean.getLongitude();
				destination=new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
				maintenanceDate=bean.getMaintenanceDate();
				sPartnerName = bean.getPartnerName();
				sZone = bean.getZone();
				mTaskNo.setText(sTaskNo);
				mTaskPackageName.setText(sTaskPackageName);
				if (sPartnerName == null || sPhone == null) {
					shareToFriends.setVisibility(View.VISIBLE);
					dialToPartner.setVisibility(View.GONE);
				} else {
					shareToFriends.setVisibility(View.GONE);
					dialToPartner.setVisibility(View.VISIBLE);
				}
				if (null == list || list.size() == 0) {
					Utilities.showToast("无数据", mActivity);
					mDoChargeBack.setVisibility(View.GONE);
					mDoResponse.setVisibility(View.GONE);
				} else {
					int count=0;//记录完成电梯的数量
					for(TaskPackageDetail data:list){
						if(data.getIsFinish().equals("2")){
							count++;
						}
					}
					if(count==list.size()){
						mDoResponse.setVisibility(View.VISIBLE);
						mDoResponse.setBackgroundColor(0xff00b9ef);
						mDoResponse.setClickable(true);
						mDoChargeBack.setVisibility(View.GONE);
					}else{
						isToday=Utilities.isToday(maintenanceDate);
						if (true==isToday){
							mDoResponse.setVisibility(View.VISIBLE);
							mDoResponse.setBackgroundColor(0xffcccccc);
							mDoResponse.setClickable(false);
						    mDoChargeBack.setVisibility(View.GONE);
						}else {
							mDoResponse.setVisibility(View.GONE);
							mDoChargeBack.setVisibility(View.VISIBLE);
						}
					}
					adapter = new TaskListPackageDetailAdapter(context, list);
					mTaskListView.setAdapter(adapter);
				}
				stopProgressDialog();
				break;
			case StatusCode.VALIDATE_TIME_SUCCESS:
				String time = (String) msg.obj;
				if (time.equals("-1")) {
					Utilities.showToast("该维保单时间已经过期", context);
					break;
				} else if (time.equals("0")) {
					Utilities.showToast("当天的任务不可以被退单", context);
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
								Param param1 = new Param(Constant.TASKNO,sTaskNo);
								Param param2 = new Param(Constant.LOGINNAME,mSharedPreferences.getString(Constant.LOGINNAME, ""));
								Request request = httpEngine.createRequest(ServicesConfig.CHARGE_BACK_TASK, param1,param2);
								Call call = httpEngine.createRequestCall(request);
								call.enqueue(new com.squareup.okhttp.Callback(){

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
									public void onFailure(Request request,IOException e) {
										Message msg = new Message();
										msg.what = StatusCode.RESPONSE_NET_FAILED;
										msg.obj = "网络异常";
										handler.sendMessage(msg);
									}
								});
							}
						}).show();
				break;
			case StatusCode.CHARGEBACK_SUCCESS:
				String state = (String) msg.obj;
				if (state.equals("true")) {
					tagSet.remove(sTaskNo);
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
				break;
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
		initTag();
		initView();
		getExtraDataAndInit();
		initEvent();
	}

	private void initTag() {
		Set<String> tempSet = mSharedPreferences.getStringSet("tagSet", null);
		if (tempSet == null) {
			tagSet = new LinkedHashSet<String>();
		} else {
			tagSet = tempSet;
		}
	}
	
	private void initView() {
		mStartPoint=new LatLng(((MyApplication)getApplicationContext()).latitude,(((MyApplication)getApplicationContext())).longitude);
		mDoBack = (ImageView) findViewById(R.id.iv_grab_headBack);
		mTaskListView = (ListView) findViewById(R.id.lv_tasklist);
		mDoChargeBack = (Button) findViewById(R.id.bt_cancle_task);
		mDoResponse=(Button) findViewById(R.id.bt_next_response);
		mDoMore = (ImageView) findViewById(R.id.iv_navicate_right);
		mDoMore.setBackgroundResource(R.drawable.icon_common_more);
		mDoMore.setVisibility(View.VISIBLE);
		mTaskPackageName = (TextView) findViewById(R.id.tv_headTitle_community_name);
		mTaskNo = (TextView) findViewById(R.id.tv_headTitle_taskno);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.srl_container);
	}

	private void getExtraDataAndInit() {
		Intent intent=getIntent();
		sTaskNo = intent.getStringExtra(Constant.TASKNO);
		mLoginName = mSharedPreferences.getString(SharedPreferencesKeys.CURRENT_LOGIN_NAME, null);
		getDataFromServer();
	}
	
	private void initEvent() {
		mActivity = TaskListPackageDetailActivity.this;
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
		mDoBack.setOnClickListener(this);
		mDoChargeBack.setOnClickListener(this);
		mDoResponse.setOnClickListener(this);
		mDoMore.setOnClickListener(this);
		mTaskListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				TaskPackageDetail detail = (TaskPackageDetail) parent.getItemAtPosition(position);
				Intent intent = new Intent(context, ElevatorDetailActivity.class);
				intent.putExtra(Constant.ELEVATORNO, detail.getElevatorNo());
				startActivity(intent);
			}
		});
		initPopupWindow();
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
								Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + sPhone));
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
		oks.setText("我在24T中抢到" + sZone + "的一个维保单，单号为:" + sTaskNo + ",请速度去抢哦！App下载链接：http://www.wandoujia.com/apps/com.overtech.ems");
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
		RouteParaOption para = new RouteParaOption().startName("我的位置")
				.startPoint(startPoint).endPoint(endPoint).endName("终点")
				.busStrategyType(EBusStrategyType.bus_recommend_way);
		try {
			BaiduMapRoutePlan.setSupportWebRoute(true);
			BaiduMapRoutePlan.openBaiduMapTransitRoute(para, mActivity);
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
		getDataFromServer();
	}
	
	private void getDataFromServer() {
		startProgressDialog("正在加载...");
		Param param = new Param(Constant.TASKNO, sTaskNo);
		Param param2 = new Param(Constant.LOGINNAME, mLoginName);
		Request request = httpEngine.createRequest(ServicesConfig.TASK_PACKAGE_DETAIL, param,param2);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new com.squareup.okhttp.Callback(){

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

		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_grab_headBack:
			finish();
			break;
		case R.id.bt_cancle_task:
			Param param = new Param(Constant.TASKNO, sTaskNo);
			Request request = httpEngine.createRequest(ServicesConfig.CHARGE_BACK_TASK_VALIDATE_TIME, param);
			Call call = httpEngine.createRequestCall(request);
			call.enqueue(new com.squareup.okhttp.Callback(){

				@Override
				public void onFailure(Request request, IOException e) {
					Message msg = new Message();
					msg.what = StatusCode.RESPONSE_NET_FAILED;
					msg.obj = "网络异常";
					handler.sendMessage(msg);
				}

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
				}});
			break;
		case R.id.bt_next_response:
			Intent intent = new Intent(TaskListPackageDetailActivity.this,QuestionResponseActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(Constant.TASKNO, sTaskNo);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case R.id.iv_navicate_right:
			showPopupWindow(v);
			break;
	    }
	}
}
