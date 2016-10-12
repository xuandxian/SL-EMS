package com.overtech.ems.activity.parttime.tasklist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.mapdialog.InstallerMapDialog;
import com.overtech.ems.utils.AppUtils;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView.IXListViewListener;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView.OnMenuItemClickListener;
import com.overtech.ems.widget.swiperefreshlistview.pulltorefresh.RefreshTime;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenu;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenuCreator;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenuItem;

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
	private InstallerMapDialog mapDialog;
	/**
	 * 记录年检提醒状态，如果出现之后点击确认后就不再提醒了
	 */
	private boolean hasShowAsNotification;
	private String mTaskNo;
	private String loginName;
	private int mPosition;
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
				if (AppUtils.isConnected(getActivity().getApplicationContext())) {
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
		onRefresh();
		return view;
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
					public void onMenuItemClick(final int position,
							SwipeMenu menu, int index) {
						// TODO Auto-generated method stub
						switch (index) {
						case 0:// 导航
							startNavicate(adapter.getLatitude(position),
									adapter.getLongitude(position));
							break;
						case 1:// t退单
							alertBuilder
									.setTitle("温馨提示")
									.setMessage("您确定要退单")
									.setNegativeButton("取消", null)
									.setPositiveButton(
											"确认",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// TODO Auto-generated
													// method stub
													Map<String, Object> data = (Map<String, Object>) adapter
															.getItem(position);
													mTaskNo = data
															.get("taskNo")
															.toString();
													mPosition = position;
													doChargeBackTaskValidateTime(mTaskNo);
												}
											}).show();

							// Map<String, Object> data = (Map<String, Object>)
							// adapter
							// .getItem(position);
							// mTaskNo = data.get("taskNo").toString();
							// mPosition = position;
							// doChargeBackTaskValidateTime(mTaskNo);
							break;
						}
					}
				});
		mSwipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				@SuppressWarnings("unchecked")
				Map<String, Object> data = (Map<String, Object>) parent
						.getAdapter().getItem(position);
				Intent intent = new Intent(mActivity,
						TaskListPackageDetailActivity.class);
				intent.putExtra(Constant.TASKNO, data.get("taskNo").toString());
				if (data.get("isAs") != null) {
					intent.putExtra("isAs", data.get("isAs").toString());
				}
				startActivityForResult(intent, REQUESTCODE);
			}
		});
		reLoad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onRefresh();
			}
		});
	}

	private void initListView() {
		creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem navicateItem = new SwipeMenuItem(mActivity);
				navicateItem
						.setBackground(R.drawable.abc_popup_background_mtrl_mult);
				navicateItem.setWidth(Utilities.dp2px(mActivity, 120));
				navicateItem.setTitle("导航");
				navicateItem.setTitleSize(18);
				navicateItem.setTitleColor(getResources().getColor(
						R.color.colorPrimary));
				menu.addMenuItem(navicateItem);
				SwipeMenuItem deleteItem = new SwipeMenuItem(mActivity);
				deleteItem
						.setBackground(R.drawable.abc_popup_background_mtrl_mult);
				deleteItem.setWidth(Utilities.dp2px(mActivity, 120));
				deleteItem.setTitle("退单");
				deleteItem.setTitleSize(18);
				deleteItem.setTitleColor(getResources().getColor(
						R.color.colorPrimary30));
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
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put(Constant.TASKNO, mTaskNo);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20052, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				String time = response.body.get("result").toString();
				if (time.equals("-1")) {// 维保单维保时间已经过期
					Utilities.showToast(response.msg, mActivity);
					return;
				} else if (time.equals("0")) {// 维保单维保时间是当天，不允许退单
					Utilities.showToast(response.msg, mActivity);
					return;
				} else if (time.equals("1")) {// 退单时间距离维保时间在72小时内，退单会影响星级评定
					alertBuilder
							.setTitle("温馨提示")
							.setMessage(response.msg)
							.setNegativeButton("取消", null)
							.setPositiveButton("确认",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											dealChargeBackTask();
										}
									}).show();

				} else {
					// 退单时间距离维保时间72小时外
					dealChargeBackTask();
				}

			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub
			}

			@Override
			public void bizStIs1Deal(Bean response) {
				// TODO Auto-generated method stub
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
			}
		};
		conn.sendRequest();
	}

	// 处理退单事件
	private void dealChargeBackTask() {
		startProgressDialog("正在退单...");
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("taskNo", mTaskNo);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20053, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				stopProgressDialog();
				list.remove(mPosition);
				if (list.isEmpty()) {
					mSwipeListView.setVisibility(View.GONE);
					mNoPage.setVisibility(View.VISIBLE);
				} else {
					adapter.notifyDataSetChanged();
				}
				// 推送业务
				loadNotDoneTask();
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub
			}

			@Override
			public void bizStIs1Deal(Bean response) {
				// TODO Auto-generated method stub
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
			}
		};
		conn.sendRequest();
	}

	private void startNavicate(double latitude, double longitude) {// endName暂时不使用
		// 构建 route搜索参数
		if (mapDialog == null) {
			mapDialog = new InstallerMapDialog(mActivity);
			mapDialog.showDialog(latitude, longitude);
		} else {
			mapDialog.showDialog(latitude, longitude);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUESTCODE) {
			onRefresh();
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {
			onRefresh();
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20050, uid,
				certificate, null) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				list = (List<Map<String, Object>>) response.body.get("data");
				if (null == list || list.size() == 0) {
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

					for (Map<String, Object> map : list) {
						String isAs = map.get("isAs").toString();
						if (TextUtils.equals(isAs, "1")
								&& !hasShowAsNotification) {// 年检包
							alertBuilder
									.setTitle("温馨提示")
									.setMessage("发现你有需要年检的任务单，请按时年检")
									.setNegativeButton("取消", null)
									.setPositiveButton(
											"确认",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// TODO Auto-generated
													// method stub
													hasShowAsNotification = true;
												}
											}).show();
						} else {// 维保包

						}
					}

				}
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal(Bean response) {
				// TODO Auto-generated method stub
				if (adapter != null) {
					adapter.getData().clear();
					adapter.notifyDataSetChanged();
				}
				mSwipeListView.setVisibility(View.GONE);
				mNoPage.setVisibility(View.VISIBLE);
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
				mSwipeListView.stopRefresh();
			}
		};
		conn.sendRequest();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mSwipeListView.stopLoadMore();
	}

	/**
	 * 加载未完成的任务单
	 */
	private void loadNotDoneTask() {
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20050, uid,
				certificate, null) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
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

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal(Bean response) {
				// TODO Auto-generated method stub

			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub

			}
		};
		conn.sendRequest();
	}
}
