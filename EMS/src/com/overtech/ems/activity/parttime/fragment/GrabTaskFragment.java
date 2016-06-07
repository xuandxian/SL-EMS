package com.overtech.ems.activity.parttime.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
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
import com.overtech.ems.activity.parttime.common.PackageDetailActivity;
import com.overtech.ems.activity.parttime.grabtask.GrabTaskDoFilterActivity;
import com.overtech.ems.activity.parttime.grabtask.KeyWordSerachActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.TaskPackageBean;
import com.overtech.ems.entity.bean.TaskPackageBean.TaskPackage;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.entity.common.ServicesConfig;
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

public class GrabTaskFragment extends BaseFragment implements
		IXListViewListener {

	private PullToRefreshSwipeMenuListView mSwipeListView;
	private SwipeMenuCreator creator;
	private Activity mActivity;
	private LinearLayout mPartTimeDoFifter;
	private TextView mKeyWordSearch;
	private View mHeadView;
	private Effectstype effect;
	private GrabTaskAdapter mAdapter;
	private List<TaskPackage> list;
	private TextView mHeadTitle;
	private LinearLayout mNoResultPage;
	private LinearLayout mNoWifi;
	private Button mRetrySearch;
	private String uid;
	private String certificate;
	/**
	 * 存放用于推送的标签
	 */
	private Set<String> tagSet;
	/**
	 * 抢单成功的任务单用做标签
	 */
	private String tagItem;
	private String TAG = "24梯";
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
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				mNoWifi.setVisibility(View.VISIBLE);
				mNoResultPage.setVisibility(View.GONE);
				Utilities.showToast("服务器异常", context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				mNoWifi.setVisibility(View.VISIBLE);
				mNoResultPage.setVisibility(View.GONE);
				Utilities.showToast("网络异常", context);
				break;
			}
			onLoad();
			stopProgressDialog();
		};
	};
	private ResultCallback<TaskPackageBean> mCallBack = new ResultCallback<TaskPackageBean>() {

		@Override
		public void onError(Request request, Exception e) {
			// TODO Auto-generated method stub
			Logr.e(request.toString());
		}

		@Override
		public void onResponse(TaskPackageBean response) {
			// TODO Auto-generated method stub
			if (response == null) {
				Utilities.showToast("暂时没有数据", mActivity);
				return;
			}
			int st = response.st;
			String msg = response.msg;
			if (st != 0) {
				if (st == -1 || st == -2) {
					Intent intent = new Intent(mActivity, LoginActivity.class);
					startActivity(intent);
					mActivity.finish();
				} else {
					Utilities.showToast(msg, mActivity);
				}
			} else {
				list = response.body.data;
				if (null == list || list.isEmpty()) {
					mNoWifi.setVisibility(View.GONE);
					mNoResultPage.setVisibility(View.VISIBLE);
				} else {
					mNoWifi.setVisibility(View.GONE);
					mNoResultPage.setVisibility(View.GONE);
					if (mAdapter == null) {
						mAdapter = new GrabTaskAdapter(list, mActivity);
						mSwipeListView.setAdapter(mAdapter);
					} else {
						mAdapter.notifyDataSetChanged();
					}
				}
			}
		}
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
		View view = inflater.inflate(R.layout.fragment_grab_task, container,
				false);
		uid = (String) SharePreferencesUtils.get(mActivity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(mActivity,
				SharedPreferencesKeys.CERTIFICATED, "");
		// 读取保存在本地的标签
		readTagSet();
		findViewById(view);
		init();
		loadingData();
		Log.e("GrabTaskFragment", "onCretateView");

		return view;
	}

	private void readTagSet() {
		// TODO Auto-generated method stub
		Set<String> tempSet = mSharedPreferences.getStringSet("tagSet", null);
		if (tempSet == null) {
			tagSet = new LinkedHashSet<String>();
		} else {
			tagSet = tempSet;
		}
	}

	private void loadingData() {
		// TODO Auto-generated method stub
		Requester requester = new Requester();
		requester.cmd = 20020;
		requester.certificate = certificate;
		requester.uid = uid;
		initData(ServicesConfig.GRABTASK, REFRESH_TYPE_DEFAULT,
				gson.toJson(requester), mCallBack);
	}

	private void findViewById(View view) {
		mSwipeListView = (PullToRefreshSwipeMenuListView) view
				.findViewById(R.id.sl_qiandan_listview);
		mHeadTitle = (TextView) view.findViewById(R.id.tv_headTitle);
		mNoResultPage = (LinearLayout) view.findViewById(R.id.page_no_result);
		mNoWifi = (LinearLayout) view.findViewById(R.id.page_no_wifi);
		mRetrySearch = (Button) view.findViewById(R.id.load_btn_retry);
	}

	public <T> void initData(String url, String flag, String jsonData,
			ResultCallback<T> callback) {
		if (TextUtils.equals(REFRESH_TYPE_DEFAULT, flag)) {
			startProgressDialog("正在查询...");
		} else if (TextUtils.equals(REFRESH_TYPE_FILTER, flag)) {
			startProgressDialog("正在查询...");
			// mSwipeListView.setPullRefreshEnable(false);
		}
		mSwipeListView.setFooterViewInvisible();
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, callback, jsonData);
	}

	private void init() {
		mHeadTitle.setText("抢单");
		initListView();
		mSwipeListView.setRefreshTime(RefreshTime.getRefreshTime(mActivity));
		mSwipeListView.setMenuCreator(creator);
		mSwipeListView.setPullRefreshEnable(true);
		mSwipeListView.setPullLoadEnable(true);
		mSwipeListView.setXListViewListener(this);
		mHeadView = LayoutInflater.from(mActivity).inflate(
				R.layout.listview_header_filter, null);
		mHeadView.setOnClickListener(null);
		mSwipeListView.addHeaderView(mHeadView);
		mPartTimeDoFifter = (LinearLayout) mHeadView
				.findViewById(R.id.ll_grab_task);
		mKeyWordSearch = (TextView) mHeadView
				.findViewById(R.id.et_do_parttime_search);
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
				TaskPackage data = (TaskPackage) parent
						.getItemAtPosition(position);
				Intent intent = new Intent(mActivity,
						PackageDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("CommunityName", data.taskPackageName);
				bundle.putString("TaskNo", data.taskNo);
				bundle.putString("Longitude", data.longitude);
				bundle.putString("Latitude", data.latitude);
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
				loadingData();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == StatusCode.RESULT_GRAB_DO_FILTER
				&& resultCode == Activity.RESULT_OK) {
			String mZone = data.getStringExtra("mZone");
			String mTime = data.getStringExtra("mTime");

			Requester requester = new Requester();
			requester.cmd = 20022;
			requester.certificate = certificate;
			requester.uid = uid;
			requester.body.put(Constant.FILTERTIME, mTime);
			requester.body.put(Constant.FILTERZONE, mZone);
			initData(SystemConfig.NEWIP, REFRESH_TYPE_FILTER,
					gson.toJson(requester), mCallBack);
		} else if (requestCode == StatusCode.RESULT_GRAB_DO_SEARCH
				&& resultCode == Activity.RESULT_OK) {
			String keyWord = data.getStringExtra("mKeyWord");

			Requester requester = new Requester();
			requester.cmd = 20021;
			requester.certificate = certificate;
			requester.uid = uid;
			requester.body.put(Constant.KEYWORD, keyWord);
			initData(SystemConfig.NEWIP, REFRESH_TYPE_FILTER,
					gson.toJson(requester), mCallBack);
		} else if (requestCode == StatusCode.RESULT_GRAB_DO_GRAB
				&& resultCode == Activity.RESULT_OK) {
			onRefresh();
		}
	}

	private void initListView() {
		creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem openItem = new SwipeMenuItem(mActivity);
				openItem.setBackground(new ColorDrawable(Color.rgb(0xFF, 0x3A,
						0x30)));
				openItem.setWidth(dp2px(90));
				openItem.setTitle("抢");
				openItem.setTitleSize(18);
				openItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(openItem);
			}
		};
	}

	public void onRefresh() {
		loadingData();
	}

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

	private ResultCallback<TaskPackageBean> grabCallback = new ResultCallback<TaskPackageBean>() {

		@Override
		public void onError(Request request, Exception e) {
			// TODO Auto-generated method stub
			Logr.e(request.toString());
		}

		@Override
		public void onResponse(TaskPackageBean response) {
			// TODO Auto-generated method stub
			int st = response.st;
			String msg = response.msg;
			if (st != 0) {
				if (st == -1 || st == -2) {
					Intent intent = new Intent(mActivity, LoginActivity.class);
					startActivity(intent);
					mActivity.finish();
				} else {
					Utilities.showToast(msg, mActivity);
				}
			} else {
				String status = response.body.status;
				if (TextUtils.equals(status, "0")) {
					Utilities.showToast("请不要重复抢单", context);
				} else if (TextUtils.equals(status, "1")) {
					Utilities.showToast("抢单成功，等待第二个人抢", context);

					// 推送业务代码
					// tagItem = response.body.taskNo;
					// if (!AppUtils.isValidTagAndAlias(tagItem)) {
					// Utilities.showToast("格式不对", context);
					// } else {
					// tagSet.add(tagItem);
					// JPushInterface.setAliasAndTags(getActivity()
					// .getApplicationContext(), null, tagSet,
					// mTagsCallback);
					// }
					onRefresh();
				} else if (TextUtils.equals(status, "2")) {
					Utilities.showToast("抢单成功，请到任务中查看", context);
					// 推送业务代码
					// tagItem = response.body.taskNo;
					// if (!AppUtils.isValidTagAndAlias(tagItem)) {
					// Utilities.showToast("格式不对", context);
					// } else {
					// tagSet.add(tagItem);
					// JPushInterface.setAliasAndTags(getActivity()
					// .getApplicationContext(), null, tagSet,
					// mTagsCallback);
					// }
					onRefresh();
				} else if (TextUtils.equals(status, "3")) {
					Utilities.showToast("差一点就抢到了", context);
				} else if (TextUtils.equals(status, "4")) {
					Utilities.showToast("维保日期的电梯数量已经超过10台，不能够再抢单。", context);
				} else {
					Utilities.showToast("用户账户异常", context);
					Intent intent = new Intent(getActivity(),
							LoginActivity.class);
					startActivity(intent);
					getActivity().finish();
				}
			}
		}

	};

	private void showDialog(final int position) {
		effect = Effectstype.Slideright;
		dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
				.withDividerColor("#11000000").withMessage("您确认要接此单？")
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
						startProgressDialog("正在抢单...");
						String mTaskNo = list.get(position).taskNo;
						Requester requester = new Requester();
						requester.cmd = 20021;
						requester.uid = uid;
						requester.certificate = certificate;
						requester.body.put(Constant.TASKNO, mTaskNo);
						OkHttpClientManager.postAsyn(SystemConfig.NEWIP,
								grabCallback, gson.toJson(requester));
					}
				}).show();
	}

}
