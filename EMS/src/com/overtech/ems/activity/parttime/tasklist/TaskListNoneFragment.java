package com.overtech.ems.activity.parttime.tasklist;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.adapter.TaskListAdapter;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.parttime.MainActivity;
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
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView.IXListViewListener;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView.OnMenuItemClickListener;
import com.overtech.ems.widget.swiperefreshlistview.pulltorefresh.RefreshTime;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenu;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenuCreator;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenuItem;
import com.squareup.okhttp.Request;

public class TaskListNoneFragment extends BaseFragment implements
		IXListViewListener {
	private PullToRefreshSwipeMenuListView mSwipeListView;
	private SwipeMenuCreator creator;
	private Activity mActivity;
	private Effectstype effect;
	private TaskListAdapter adapter;
	private List<Map<String, Object>> list;
	private LinearLayout mNoPage;
	private Button reLoad;
	private String mTaskNo;
	private String loginName;
	private int mPosition;
	private Set<String> tagSet;
	private String TAG = "24梯";
	private final int REQUESTCODE = 0x11; // 访问任务包详情的请求码
	private String uid;
	private String certificate;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.MSG_SET_TAGS:
				Log.d("24梯", "Set tags in handler.");
				JPushInterface.setAliasAndTags(getActivity()
						.getApplicationContext(), null, (Set<String>) msg.obj,
						mTagsCallback);
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
				Utilities.showToast("退单成功", mActivity);
				SharePreferencesUtils.put(mActivity,
						SharedPreferencesKeys.TAGSET, tags);
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
		initTag();
		findViewById(view);
		init();
		getDataFromServer();
		return view;
	}

	private void initTag() {
		Set<String> tempSet = (Set<String>) SharePreferencesUtils.get(
				mActivity, SharedPreferencesKeys.TAGSET,
				new LinkedHashSet<String>());
	}

	private void findViewById(View view) {
		mSwipeListView = (PullToRefreshSwipeMenuListView) view
				.findViewById(R.id.sl_task_list_listview);
		mNoPage = (LinearLayout) view.findViewById(R.id.page_no_result);
		reLoad = (Button) view.findViewById(R.id.load_btn_retry);
	}

	private void init() {
		uid = ((MainActivity) getActivity()).getUid();
		certificate = ((MainActivity) getActivity()).getCertificate();
		initListView();
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
							Map<String, Object> data = (Map<String, Object>) adapter
									.getItem(position);
							mTaskNo = data.get("taskNo").toString();
							mPosition = position;
							doChargeBackTaskValidateTime(mTaskNo);
							break;
						}
					}
				});
		mSwipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map<String, Object> data = (Map<String, Object>) parent
						.getAdapter().getItem(position);
				Intent intent = new Intent(mActivity,
						TaskListPackageDetailActivity.class);
				intent.putExtra(Constant.TASKNO, data.get("taskNo").toString());
				startActivityForResult(intent, REQUESTCODE);
			}
		});
		reLoad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getDataFromServer();
			}
		});
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
		mSwipeListView.setRefreshTime(RefreshTime.getRefreshTime(mActivity));
		mSwipeListView.setMenuCreator(creator);
		mSwipeListView.setPullRefreshEnable(true);
		mSwipeListView.setPullLoadEnable(true);
		mSwipeListView.setXListViewListener(this);
		mSwipeListView.setFooterViewInvisible();
	}

	// 侧滑退单，验证该维保单号的时间
	protected void doChargeBackTaskValidateTime(String taskNo) {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		Requester requester = new Requester();
		requester.uid = uid;
		requester.cmd = 20052;
		requester.certificate = certificate;
		requester.body.put(Constant.TASKNO, mTaskNo);
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
				if (response == null) {
					Utilities.showToast(R.string.response_no_object, activity);
					return;
				}
				int st = response.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(response.msg, mActivity);
					SharePreferencesUtils.put(mActivity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(mActivity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(mActivity, LoginActivity.class);
					startActivity(intent);
					return;
				} else if (st == 1) {
					Utilities.showToast(response.msg, mActivity);
					return;
				}
				String time = response.body.get("result").toString();
				if (time.equals("-1")) {
					Utilities.showToast(response.msg, mActivity);
				} else if (time.equals("0")) {
					Utilities.showToast(response.msg, mActivity);
				} else if (time.equals("1")) {
					dialogBuilder.withMessage(response.msg);
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
								dealChargeBackTask();
							}
						}).show();
			}
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));
	}

	// 处理退单事件
	private void dealChargeBackTask() {
		startProgressDialog("正在退单...");
		Requester requester = new Requester();
		requester.cmd = 20053;
		requester.certificate = certificate;
		requester.uid = uid;
		requester.body.put("taskNo", mTaskNo);
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
				if (response == null) {
					Utilities.showToast(R.string.response_no_object, activity);
					return;
				}
				int st = response.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(response.msg, mActivity);
					SharePreferencesUtils.put(mActivity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(mActivity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(mActivity, LoginActivity.class);
					startActivity(intent);
					return;
				}
				if (st == 0) {
					list.remove(mPosition);
					adapter.notifyDataSetChanged();
					tagSet.remove(mTaskNo);
					JPushInterface.setAliasAndTags(getActivity()
							.getApplicationContext(), null, tagSet,
							mTagsCallback);
				} else {
					Utilities.showToast(response.msg, activity);
				}

			}
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));
	}

	private void getDataFromServer() {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		Requester requester = new Requester();
		requester.certificate = certificate;
		requester.uid = uid;
		requester.cmd = 20050;
		ResultCallback<Bean> callback = new ResultCallback<Bean>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				mSwipeListView.stopRefresh();
				Logr.e(request.toString());
			}

			@Override
			public void onResponse(Bean response) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				mSwipeListView.stopRefresh();
				if (response == null) {
					Utilities.showToast(R.string.response_no_object, activity);
					mSwipeListView.setVisibility(View.GONE);
					mNoPage.setVisibility(View.VISIBLE);
					return;
				}
				int st = response.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(response.msg, mActivity);
					SharePreferencesUtils.put(mActivity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(mActivity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(mActivity, LoginActivity.class);
					startActivity(intent);
					return;
				} else if (st == 1) {
					Utilities.showToast(response.msg, activity);
					if (adapter != null) {
						adapter.getData().clear();
						adapter.notifyDataSetChanged();
					}
					mSwipeListView.setVisibility(View.GONE);
					mNoPage.setVisibility(View.VISIBLE);
					return;
				}
				list = (List<Map<String, Object>>) response.body.get("data");
				if (null == list || list.size() == 0) {
					Utilities
							.showToast(
									getResources().getString(
											R.string.response_no_data),
									mActivity);
					mSwipeListView.setVisibility(View.GONE);
					mNoPage.setVisibility(View.VISIBLE);
					if (adapter != null) {
						adapter.clearAdapter();
					}
				} else {
					mSwipeListView.setVisibility(View.VISIBLE);
					mNoPage.setVisibility(View.GONE);
					if (adapter == null) {
						adapter = new TaskListAdapter(list, mActivity,
								StatusCode.TASK_NO);
						mSwipeListView.setAdapter(adapter);
					} else {
						adapter.setData(list);
						adapter.notifyDataSetChanged();
					}
				}
			}
		};
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback,
				gson.toJson(requester));

	}

	public void startNavicate(LatLng startPoint, LatLng endPoint, String endName) {// endName暂时不使用
		// 构建 route搜索参数
		RouteParaOption para = new RouteParaOption().startName("起点")
				.startPoint(startPoint).endPoint(endPoint).endName("终点")
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
			getDataFromServer();
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		Logr.e("tasklistnone" + hidden);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		getDataFromServer();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mSwipeListView.stopLoadMore();
	}

}
