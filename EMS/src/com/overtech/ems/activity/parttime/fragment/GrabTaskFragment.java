package com.overtech.ems.activity.parttime.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.adapter.GrabTaskAdapter;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.activity.parttime.common.PackageDetailActivity;
import com.overtech.ems.activity.parttime.grabtask.GrabTaskDoFilterActivity;
import com.overtech.ems.activity.parttime.grabtask.KeyWordSerachActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;
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

public class GrabTaskFragment extends BaseFragment implements
		IXListViewListener {

	private PullToRefreshSwipeMenuListView mSwipeListView;
	private SwipeMenuCreator creator;
	private Activity mActivity;
	private LinearLayout mPartTimeDoFifter;
	private TextView mKeyWordSearch;
	private View mHeadView;
	private GrabTaskAdapter mAdapter;
	private List<Map<String, Object>> list;
	private TextView mHeadTitle;
	private LinearLayout mNoResultPage;
	private LinearLayout mNoWifi;
	private Button mRetrySearch;
	private String uid;
	private String certificate;
	private final static String REFRESH_TYPE_DEFAULT = "0";
	private final static String REFRESH_TYPE_LOADING = "1";
	private final static String REFRESH_TYPE_FILTER = "2";

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.MSG_SET_TAGS:
				Log.d("24梯", "Set tags in handler.");
				JPushInterface.setAliasAndTags(getActivity()
						.getApplicationContext(), null, (Set<String>) msg.obj,
						mTagsCallback);
				break;
			}
			onLoad();
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
		View view = inflater.inflate(R.layout.fragment_grab_task, container,
				false);
		uid = ((MainActivity) getActivity()).getUid();
		certificate = ((MainActivity) getActivity()).getCertificate();
		initView(view);
		initEvent();
		startProgressDialog(getResources().getString(R.string.loading_public_default));
		onRefresh();
		return view;
	}

	private void initView(View view) {
		mSwipeListView = (PullToRefreshSwipeMenuListView) view
				.findViewById(R.id.sl_qiandan_listview);
		mHeadTitle = (TextView) view.findViewById(R.id.tv_headTitle);
		mNoResultPage = (LinearLayout) view.findViewById(R.id.page_no_result);
		mNoWifi = (LinearLayout) view.findViewById(R.id.page_no_wifi);
		mRetrySearch = (Button) view.findViewById(R.id.load_btn_retry);
	}

	private void initEvent() {
		mHeadTitle.setText("抢单");
		initListView();
		mSwipeListView
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,
							int index) {
						showDialog(position);
					}
				});
		mSwipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map<String, Object> data = (Map<String, Object>) parent
						.getItemAtPosition(position);
				Intent intent = new Intent(mActivity,
						PackageDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("CommunityName", data.get("taskPackageName")
						.toString());
				bundle.putString("TaskNo", data.get("taskNo").toString());
				bundle.putString("Longitude", data.get("longitude").toString());
				bundle.putString("Latitude", data.get("latitude").toString());
				intent.putExtras(bundle);
				startActivityForResult(intent, StatusCode.RESULT_GRAB_DO_GRAB);
			}
		});
		mPartTimeDoFifter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mActivity,
						GrabTaskDoFilterActivity.class);
				startActivityForResult(intent, StatusCode.RESULT_GRAB_DO_FILTER);
			}
		});

		mKeyWordSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mActivity,
						KeyWordSerachActivity.class);
				startActivityForResult(intent, StatusCode.RESULT_GRAB_DO_SEARCH);
			}
		});
		mRetrySearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				onRefresh();
			}
		});
	}

	private void initListView() {
		creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem openItem = new SwipeMenuItem(mActivity);
				openItem.setBackground(R.drawable.abc_popup_background_mtrl_mult);
				openItem.setWidth(Utilities.dp2px(mActivity, 120));
				openItem.setTitle("抢");
				openItem.setTitleSize(18);
				openItem.setTitleColor(getResources().getColor(
						R.color.colorPrimary));
				menu.addMenuItem(openItem);
			}
		};
		mSwipeListView.setRefreshTime(RefreshTime.getRefreshTime(mActivity));
		mSwipeListView.setMenuCreator(creator);
		mSwipeListView.setPullRefreshEnable(true);
		mSwipeListView.setPullLoadEnable(true);
		mSwipeListView.setXListViewListener(this);
		mSwipeListView.setFooterViewInvisible();
		mHeadView = LayoutInflater.from(mActivity).inflate(
				R.layout.listview_header_filter, null);
		mHeadView.setOnClickListener(null);
		mSwipeListView.addHeaderView(mHeadView);
		mPartTimeDoFifter = (LinearLayout) mHeadView
				.findViewById(R.id.ll_grab_task);
		mKeyWordSearch = (TextView) mHeadView
				.findViewById(R.id.et_do_parttime_search);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == StatusCode.RESULT_GRAB_DO_FILTER
				&& resultCode == Activity.RESULT_OK) {
			String mZone = data.getStringExtra("mZone");
			String mTime = data.getStringExtra("mTime");
			filter(mTime, mZone);
		} else if (requestCode == StatusCode.RESULT_GRAB_DO_SEARCH
				&& resultCode == Activity.RESULT_OK) {
			String keyWord = data.getStringExtra("mKeyWord");
			keyWord(keyWord);
		} else if (requestCode == StatusCode.RESULT_GRAB_DO_GRAB
				&& resultCode == Activity.RESULT_OK) {
			onRefresh();
		}
	}

	private void keyWord(String keyWord) {
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("mKeyWord", keyWord);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20020, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				mSwipeListView.stopRefresh();
				list = (List<Map<String, Object>>) response.body.get("data");
				if (null == list || list.isEmpty()) {
					mNoWifi.setVisibility(View.GONE);
					mNoResultPage.setVisibility(View.VISIBLE);
					if (mAdapter != null) {
						mAdapter.getData().clear();
						mAdapter.notifyDataSetChanged();
					}
				} else {
					mNoWifi.setVisibility(View.GONE);
					mNoResultPage.setVisibility(View.GONE);
					if (mAdapter == null) {
						mAdapter = new GrabTaskAdapter(list, mActivity);
						mSwipeListView.setAdapter(mAdapter);
					} else {
						mAdapter.setData(list);
						mAdapter.notifyDataSetChanged();
					}
				}
			}

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub
				mSwipeListView.stopRefresh();
			}

			@Override
			public void bizStIs1Deal(Bean response) {
				// TODO Auto-generated method stub
				mSwipeListView.stopRefresh();
				// new
				// AlertDialog.Builder(activity).setMessage(msg).create().show();
				if (mAdapter != null) {
					mAdapter.getData().clear();
					mAdapter.notifyDataSetChanged();
				}
				mNoResultPage.setVisibility(View.VISIBLE);
				mNoWifi.setVisibility(View.GONE);
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
			}
		};
		conn.sendRequest();
	}

	private void filter(String filterTime, String filterZone) {
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put(Constant.FILTERTIME, filterTime);
		body.put(Constant.FILTERZONE, filterZone);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20022, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				list = (List<Map<String, Object>>) response.body.get("data");
				if (null == list || list.isEmpty()) {
					mNoWifi.setVisibility(View.GONE);
					mNoResultPage.setVisibility(View.VISIBLE);
					if (mAdapter != null) {
						mAdapter.getData().clear();
						mAdapter.notifyDataSetChanged();
					}
				} else {
					mNoWifi.setVisibility(View.GONE);
					mNoResultPage.setVisibility(View.GONE);
					if (mAdapter == null) {
						mAdapter = new GrabTaskAdapter(list, mActivity);
						mSwipeListView.setAdapter(mAdapter);
					} else {
						mAdapter.setData(list);
						mAdapter.notifyDataSetChanged();
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
				// new
				// AlertDialog.Builder(activity).setMessage(msg).create().show();
				if (mAdapter != null) {
					mAdapter.getData().clear();
					mAdapter.notifyDataSetChanged();
				}
				mNoResultPage.setVisibility(View.VISIBLE);
				mNoWifi.setVisibility(View.GONE);
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

	private void grabTask(String taskNo) {
		startProgressDialog(getResources().getString(
				R.string.loading_public_grabing));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("taskNo", taskNo);
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20023, uid,
				certificate, body) {

			@Override
			public Context getContext() {
				// TODO Auto-generated method stub
				return activity;
			}

			@Override
			public void bizSuccess(Bean response) {
				// TODO Auto-generated method stub
				String status = response.body.get("status").toString();
				if (TextUtils.equals(status, "0")) {// 重复抢单
					Utilities.showToast(response.msg, activity);
					onRefresh();
				} else if (TextUtils.equals(status, "1")) {// 抢单成功，等待第二个人抢
					Utilities.showToast(response.msg, activity);
					onRefresh();
					loadNotDoneTask();
				} else if (TextUtils.equals(status, "2")) {// 抢单成功，到任务单中查看
					Utilities.showToast(response.msg, activity);
					onRefresh();
					loadNotDoneTask();
				} else if (TextUtils.equals(status, "3")) {// 差一点抢到
					Utilities.showToast(response.msg, activity);
					onRefresh();
				} else if (TextUtils.equals(status, "4")) {// 维保日期内的电梯已经超过10台
					Utilities.showToast(response.msg, activity);
					onRefresh();
				} else {
					Utilities.showToast(response.msg, activity);
					Intent intent = new Intent(getActivity(),
							LoginActivity.class);
					startActivity(intent);
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

	@Override
	public void onRefresh() {
		
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20020, uid,
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
				if (null == list || list.isEmpty()) {
					mNoWifi.setVisibility(View.GONE);
					mNoResultPage.setVisibility(View.VISIBLE);
					if (mAdapter != null) {
						mAdapter.getData().clear();
						mAdapter.notifyDataSetChanged();
					}
				} else {
					mNoWifi.setVisibility(View.GONE);
					mNoResultPage.setVisibility(View.GONE);
					if (mAdapter == null) {
						mAdapter = new GrabTaskAdapter(list, mActivity);
						mSwipeListView.setAdapter(mAdapter);
					} else {
						mAdapter.setData(list);
						mAdapter.notifyDataSetChanged();
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
				// new
				// AlertDialog.Builder(activity).setMessage(msg).create().show();
				if (mAdapter != null) {
					mAdapter.getData().clear();
					mAdapter.notifyDataSetChanged();
				}
				mNoResultPage.setVisibility(View.VISIBLE);
				mNoWifi.setVisibility(View.GONE);
			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub
				stopProgressDialog();
				mSwipeListView.stopRefresh();
			}
		};
		conn.sendRequest();
		// mSwipeListView.setFooterViewInvisible();
	}

	@Override
	public void onLoadMore() {
		// 此处暂时关闭，等待后台分页数据
		mSwipeListView.stopLoadMore();
	}

	public void onLoad() {
		SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm",
				Locale.getDefault());
		RefreshTime.setRefreshTime(mActivity, df.format(new Date()));
		mSwipeListView.setRefreshTime(RefreshTime.getRefreshTime(mActivity));
		mSwipeListView.stopRefresh();
		mSwipeListView.stopLoadMore();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {
			onRefresh();
		}
	}

	private void showDialog(final int position) {
		alertBuilder.setTitle("温馨提示").setMessage("您确认要接此单？")
				.setNegativeButton("取消", null)
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String mTaskNo = list.get(position).get("taskNo")
								.toString();
						grabTask(mTaskNo);
					}
				}).show();
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
