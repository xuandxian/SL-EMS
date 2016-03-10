package com.overtech.ems.activity.parttime.tasklist;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.adapter.TaskListAdapter;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.TaskPackageBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.TaskPackage;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.AppUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.overtech.ems.widget.swipemenu.SwipeMenu;
import com.overtech.ems.widget.swipemenu.SwipeMenuCreator;
import com.overtech.ems.widget.swipemenu.SwipeMenuItem;
import com.overtech.ems.widget.swipemenu.SwipeMenuListView;
import com.overtech.ems.widget.swipemenu.SwipeMenuListView.OnMenuItemClickListener;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class TaskListNoneFragment extends BaseFragment {
	private SwipeMenuListView mSwipeListView;
	private SwipeMenuCreator creator;
	private Activity mActivity;
	private Effectstype effect;
	private LocationClient mLocationClient;
	private TaskListAdapter adapter;
	private List<TaskPackage> list;
	private LinearLayout mNoPage;
	private LinearLayout mNoWifi;
	private Button reLoad;
	private String mTaskNo;
	private String loginName;
	private int mPosition;

	private Set<String> tagSet;
	private String TAG = "24梯";
	/**
	 * 访问任务包详情的请求码
	 */
	private final int REQUESTCODE = 0x11;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.TASKLIST_NONE_SUCCESS:
				String json = (String) msg.obj;
				Log.e("==未完成任务单==", json);
				Gson gson = new Gson();
				TaskPackageBean bean = gson.fromJson(json,
						TaskPackageBean.class);
				list = bean.getModel();
				if (null == list || list.size() == 0) {
					Utilities.showToast("无数据", mActivity);
					mNoPage.setVisibility(View.VISIBLE);
					mNoWifi.setVisibility(View.GONE);
					if(adapter!=null){
						adapter.clearAdapter();
					}
				} else {
					mNoPage.setVisibility(View.GONE);
					mNoWifi.setVisibility(View.GONE);
					adapter = new TaskListAdapter(list, mActivity,StatusCode.TASK_NO);
					mSwipeListView.setAdapter(adapter);
				}
				break;
			case StatusCode.VALIDATE_TIME_SUCCESS:
				String time = (String) msg.obj;
				// Log.e("==未完成时间什么值==", time);
				if (time.equals("-1")) {
					Utilities.showToast("已超过退单时间！！！", context);
					break;
				} else if (time.equals("0")) {
					Utilities.showToast("当天的任务不可以退单！！！", context);
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
						.withButton1Color(R.color.main_primary)
						.withButton2Text("确认")
						.withButton2Color(R.color.main_primary)
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

								Param param1 = new Param(Constant.TASKNO,
										mTaskNo);
								Param param2 = new Param(Constant.LOGINNAME,
										loginName);
								startLoading(ServicesConfig.CHARGE_BACK_TASK,
										new com.squareup.okhttp.Callback() {

											@Override
											public void onResponse(
													Response response)
													throws IOException {
												Message msg = new Message();
												if (response.isSuccessful()) {
													msg.what = StatusCode.CHARGEBACK_SUCCESS;
													msg.obj = response.body()
															.string();
												} else {
													msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
													msg.obj = "服务器异常";
												}
												handler.sendMessage(msg);
											}

											@Override
											public void onFailure(Request arg0,
													IOException arg1) {
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

					list.remove(mPosition);
					adapter.notifyDataSetChanged();

					tagSet.remove(mTaskNo);
					JPushInterface.setAliasAndTags(getActivity()
							.getApplicationContext(), null, tagSet,
							mTagsCallback);

				} else {
					Utilities.showToast("退单失败", context);
				}
				break;
			case StatusCode.MSG_SET_TAGS:
				Log.d("24梯", "Set tags in handler.");
				JPushInterface.setAliasAndTags(getActivity()
						.getApplicationContext(), null, (Set<String>) msg.obj,
						mTagsCallback);
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast((String) msg.obj, mActivity);
				mNoPage.setVisibility(View.VISIBLE);
				mNoWifi.setVisibility(View.GONE);
				if(adapter!=null){
					adapter.clearAdapter();
				}
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast((String) msg.obj, mActivity);
				mNoPage.setVisibility(View.GONE);
				mNoWifi.setVisibility(View.VISIBLE);
				if(adapter!=null){
					adapter.clearAdapter();
				}
				break;
			default:
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
				Utilities.showToast("退单成功", context);
				mSharedPreferences.edit().putStringSet("tagSet", tags).commit();// 成功保存标签后，将标签放到本地
				stopProgressDialog();
				break;

			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Log.d(TAG, logs);
				if (AppUtils.isConnected(getActivity().getApplicationContext())) {
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_task_list_none,
				container, false);
		Set<String> tempSet = mSharedPreferences.getStringSet("tagSet", null);
		if (tempSet == null) {
			tagSet = new LinkedHashSet<String>();
		} else {
			tagSet = tempSet;
		}
		findViewById(view);
		init();
		return view;

	}

	private void findViewById(View view) {
		mSwipeListView = (SwipeMenuListView) view
				.findViewById(R.id.sl_task_list_listview);
		mNoPage=(LinearLayout) view.findViewById(R.id.page_no_result);
		mNoWifi=(LinearLayout) view.findViewById(R.id.page_no_wifi);
		reLoad=(Button) view.findViewById(R.id.load_btn_retry);
	}

	private void init() {
		mLocationClient = ((MyApplication) getActivity().getApplication()).mLocationClient;
		mLocationClient.requestLocation();
		mLocationClient.start();
		initListView();
		mSwipeListView.setMenuCreator(creator);
		mSwipeListView
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,
							int index) {
						switch (index) {
//						case 0:// 分享
//							Utilities.showToast("点击分享", context);
//							showShare();
//							break;
						case 0:// 导航
							LatLng startPoint = adapter.getCurrentLocation();
							LatLng endPoint = adapter.getDestination(position);
							String endName = adapter.getDesName(position);
							startNavicate(startPoint, endPoint, endName);
							break;
						case 1:// t退单
							TaskPackage data = (TaskPackage) adapter
									.getItem(position);
							mTaskNo = data.getTaskNo();
							mPosition = position;
							Param param = new Param(Constant.TASKNO, mTaskNo);
							startLoading(
									ServicesConfig.CHARGE_BACK_TASK_VALIDATE_TIME,
									new Callback() {

										@Override
										public void onResponse(Response response)
												throws IOException {
											Message msg = new Message();
											if (response.isSuccessful()) {
												msg.what = StatusCode.VALIDATE_TIME_SUCCESS;
												msg.obj = response.body()
														.string();
											} else {
												msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
												msg.obj = "服务器异常";
											}
											handler.sendMessage(msg);
										}

										@Override
										public void onFailure(Request arg0,
												IOException arg1) {
											Message msg = new Message();
											msg.what = StatusCode.RESPONSE_NET_FAILED;
											msg.obj = "网络异常";
											handler.sendMessage(msg);
										}
									}, param);
							break;
						}
					}

				});
		mSwipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskPackage data = (TaskPackage) parent.getAdapter().getItem(
						position);
				Intent intent = new Intent(mActivity,
						TaskListPackageDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(Constant.TASKNO, data.getTaskNo());
				bundle.putString(Constant.TASKPACKAGENAME,
						data.getTaskPackageName());
				bundle.putString(Constant.DESNAME, data.getTaskPackageName());
				bundle.putParcelable(Constant.CURLOCATION,
						adapter.getCurrentLocation());
				bundle.putParcelable(Constant.DESTINATION,
						adapter.getDestination(position));
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE);
			}
		});
		reLoad.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startLoading();
			}
		});
		loginName = mSharedPreferences.getString(SharedPreferencesKeys.CURRENT_LOGIN_NAME, null);
		startLoading();
	}

	private void startLoading() {
		startProgressDialog("正在加载");
		Param param = new Param(Constant.LOGINNAME, loginName);
		startLoading(ServicesConfig.TASK_LIST_NONE, new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.TASKLIST_NONE_SUCCESS;
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

	private void initListView() {
		creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
//				SwipeMenuItem shareItem = new SwipeMenuItem(mActivity);
//				shareItem.setBackground(new ColorDrawable(Color.rgb(0x00,0xCD, 0x00)));
//				shareItem.setWidth(dp2px(90));
//				shareItem.setTitle("分享");
//				shareItem.setTitleSize(18);
//				shareItem.setTitleColor(Color.WHITE);
//				menu.addMenuItem(shareItem);
				SwipeMenuItem navicateItem = new SwipeMenuItem(mActivity);
				navicateItem.setBackground(new ColorDrawable(Color.rgb(0xFF,0x9D, 0x00)));
				navicateItem.setWidth(dp2px(90));
				navicateItem.setTitle("导航");
				navicateItem.setTitleSize(18);
				navicateItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(navicateItem);
				SwipeMenuItem deleteItem = new SwipeMenuItem(mActivity);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xFF,0x3A, 0x30)));
				deleteItem.setWidth(dp2px(90));
				deleteItem.setTitle("退单");
				deleteItem.setTitleSize(18);
				deleteItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(deleteItem);
			}
		};
	}

	public void startNavicate(LatLng startPoint, LatLng endPoint, String endName) {//endName暂时不使用
		// 构建 route搜索参数
		RouteParaOption para = new RouteParaOption().startName("起点").startPoint(startPoint)
				.endPoint(endPoint).endName("终点")
				.busStrategyType(EBusStrategyType.bus_time_first);
		try {
			BaiduMapRoutePlan.setSupportWebRoute(true);
			BaiduMapRoutePlan.openBaiduMapTransitRoute(para, mActivity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUESTCODE) {
			startLoading();
		}
	}

	protected void startLoading(String url, Callback callback, Param... params) {
		Request request = httpEngine.createRequest(url, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(callback);
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocationClient.stop();
	}
}
