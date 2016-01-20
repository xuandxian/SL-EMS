package com.overtech.ems.activity.parttime.tasklist;

import java.io.IOException;
import java.util.List;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

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
	private String mTaskNo;
	private String loginName;
	private int mPosition;
	/**
	 * 访问任务包详情的请求码
	 */
	private final int REQUESTCODE = 0x11;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.TASKLIST_NONE_SUCCESS:
				String json = (String) msg.obj;
//				Log.e("==未完成任务单==", json);
				Gson gson = new Gson();
				TaskPackageBean bean = gson.fromJson(json,
						TaskPackageBean.class);
				list = bean.getModel();
				if (null == list || list.size() == 0) {
					Utilities.showToast("无数据", mActivity);
				} else {
					adapter = new TaskListAdapter(list, mActivity);
					mSwipeListView.setAdapter(adapter);
				}
				break;
			case StatusCode.VALIDATE_TIME_SUCCESS:
				String time = (String) msg.obj;
//				Log.e("==未完成时间什么值==", time);
				if(time.equals("-1")){
					Utilities.showToast("已超过退单时间！！！", context);
					break;
				}else if(time.equals("0")){
					Utilities.showToast("当天的任务不可以退单！！！", context);
					break;
				}else if (time.equals("1")) {
					dialogBuilder.withMessage("72小时内退单会影响星级评定，你是否要退单？");
				} else {
					dialogBuilder.withMessage("你是否要退单");
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
						.withButton1Text("否")
						.withButton1Color(R.color.main_primary)
						.withButton2Text("是")
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
								startProgressDialog("正在退单");

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
				stopProgressDialog();
				String state = (String) msg.obj;
				if (state.equals("true")) {
					Utilities.showToast("退单成功", context);
					list.remove(mPosition);
					adapter.notifyDataSetChanged();
				} else {
					Utilities.showToast("退单失败", context);
				}
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast((String) msg.obj, mActivity);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast((String) msg.obj, mActivity);
				break;
			default:
				break;
			}
			stopProgressDialog();
		};
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
		findViewById(view);
		init();
		return view;

	}

	private void findViewById(View view) {
		mSwipeListView = (SwipeMenuListView) view
				.findViewById(R.id.sl_task_list_listview);
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
						// TODO Auto-generated method stub
						switch (index) {
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
							mPosition=position;
							Param param = new Param(Constant.TASKNO, mTaskNo);
							startLoading(
									ServicesConfig.CHARGE_BACK_TASK_VALIDATE_TIME,
									new Callback() {

										@Override
										public void onResponse(Response response)
												throws IOException {
											// TODO Auto-generated method stub
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
											// TODO Auto-generated method stub
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
				// startActivity(intent);
				startActivityForResult(intent, REQUESTCODE);
			}
		});
		loginName = mSharedPreferences.getString(
				SharedPreferencesKeys.CURRENT_LOGIN_NAME, null);
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
				SwipeMenuItem navicateItem = new SwipeMenuItem(mActivity);
				navicateItem.setBackground(new ColorDrawable(Color.rgb(0xFF,
						0x9D, 0x00)));
				navicateItem.setWidth(dp2px(90));
				navicateItem.setTitle("导航");
				navicateItem.setTitleSize(18);
				navicateItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(navicateItem);
				SwipeMenuItem deleteItem = new SwipeMenuItem(mActivity);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xFF,
						0x3A, 0x30)));
				deleteItem.setWidth(dp2px(90));
				deleteItem.setTitle("退单");
				deleteItem.setTitleSize(18);
				deleteItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(deleteItem);
			}
		};
	}

	public void startNavicate(LatLng startPoint, LatLng endPoint, String endName) {
		// 构建 route搜索参数
		RouteParaOption para = new RouteParaOption().startName("我的位置")
				.startPoint(startPoint)
				// 路线检索起点
				.endPoint(endPoint).endName(endName)
				.busStrategyType(EBusStrategyType.bus_recommend_way);
		try {
			BaiduMapRoutePlan.setSupportWebRoute(true);
			BaiduMapRoutePlan.openBaiduMapTransitRoute(para, mActivity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
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
