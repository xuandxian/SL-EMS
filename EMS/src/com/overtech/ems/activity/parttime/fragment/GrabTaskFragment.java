package com.overtech.ems.activity.parttime.fragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.adapter.GrabTaskAdapter;
import com.overtech.ems.activity.parttime.grabtask.GrabTaskDoFilterActivity;
import com.overtech.ems.activity.parttime.grabtask.KeyWordSerachActivity;
import com.overtech.ems.activity.parttime.grabtask.PackageDetailActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.StatusCodeBean;
import com.overtech.ems.entity.bean.TaskPackageBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.TaskPackage;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.AppUtils;
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
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

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
	private ArrayList<TaskPackage> list;
	private TextView mHeadTitle;
	private LinearLayout mNoResultPage;
	private LinearLayout mNoWifi;
	private Button mRetrySearch;
	/**
	 * 存放用于推送的标签
	 */
	private Set<String> tagSet;
	/**
	 * 抢单成功的任务单用做标签
	 */
	private String tagItem;
	private String TAG = "24梯";
	public LatLng myLocation;
	public LocationClient mLocationClient = null;
	private final static String REFRESH_TYPE_DEFAULT = "0";
	private final static String REFRESH_TYPE_LOADING = "1";
	private final static String REFRESH_TYPE_FILTER = "2";
	public BDLocationListener myListener = new MyLocationListener();

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Gson gson = new Gson();
			switch (msg.what) {
			case StatusCode.GRAB_GET_DATA_SUCCESS:
				if (null == myLocation) {
					Utilities.showToast("定位失败", context);
					return;
				}
				String json = (String) msg.obj;
				TaskPackageBean tasks = gson.fromJson(json,TaskPackageBean.class);
				list = (ArrayList<TaskPackage>) tasks.getModel();
				if (null == list || list.isEmpty()) {
					mNoWifi.setVisibility(View.GONE);
					mNoResultPage.setVisibility(View.VISIBLE);
				} else {
					mNoWifi.setVisibility(View.GONE);
					mNoResultPage.setVisibility(View.GONE);
					mAdapter = new GrabTaskAdapter(list, myLocation, mActivity);
					mSwipeListView.setAdapter(mAdapter);
				}
				break;
			case StatusCode.GRAG_RESPONSE_SUCCESS:
				String status = (String) msg.obj;
				StatusCodeBean bean = gson.fromJson(status,StatusCodeBean.class);
				String content = bean.getModel();
				if (TextUtils.equals(content, "0")) {
					Utilities.showToast("请不要重复抢单", context);
				} else if (TextUtils.equals(content, "1")) {
					Utilities.showToast("抢单成功，等待第二个人抢", context);

					// 推送业务代码
					tagItem = bean.getTaskNo();
					if (!AppUtils.isValidTagAndAlias(tagItem)) {
						Utilities.showToast("格式不对", context);
					} else {
						tagSet.add(tagItem);
						JPushInterface.setAliasAndTags(getActivity()
								.getApplicationContext(), null, tagSet,
								mTagsCallback);
					}

					onRefresh();
				} else if (TextUtils.equals(content, "2")) {
					Utilities.showToast("抢单成功，请到任务中查看", context);
					// 推送业务代码
					tagItem = bean.getTaskNo();
					if (!AppUtils.isValidTagAndAlias(tagItem)) {
						Utilities.showToast("格式不对", context);
					} else {
						tagSet.add(tagItem);
						JPushInterface.setAliasAndTags(getActivity()
								.getApplicationContext(), null, tagSet,
								mTagsCallback);
					}
					onRefresh();
				} else if (TextUtils.equals(content, "3")) {
					Utilities.showToast("差一点就抢到了", context);
				} else if (TextUtils.equals(content, "4")) {
					Utilities.showToast("维保日期的电梯数量已经超过10台，不能够再抢单。", context);
				}
				break;
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
		// 读取保存在本地的标签
		Set<String> tempSet = mSharedPreferences.getStringSet("tagSet", null);
		if (tempSet == null) {
			tagSet = new LinkedHashSet<String>();
		} else {
			tagSet = tempSet;
		}
		initBaiDuLocation();
		findViewById(view);
		init();
		
		Log.e("GrabTaskFragment", "onCretateView");
		
		return view;
	}
	private void initBaiDuLocation() {
		mLocationClient = new LocationClient(activity.getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPRS
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(0); // 设置发起定位请求的间隔时间为1000ms
		mLocationClient.setLocOption(option); // 设置定位参数
		mLocationClient.start();
	}

	private void findViewById(View view) {
		mSwipeListView = (PullToRefreshSwipeMenuListView) view
				.findViewById(R.id.sl_qiandan_listview);
		mHeadTitle = (TextView) view.findViewById(R.id.tv_headTitle);
		mNoResultPage=(LinearLayout)view.findViewById(R.id.page_no_result);
		mNoWifi=(LinearLayout)view.findViewById(R.id.page_no_wifi);
		mRetrySearch=(Button)view.findViewById(R.id.load_btn_retry);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (null == location) {
				Log.e("GrabTaskFragment", "定位失败");
				Utilities.showToast("定位失败", context);
			}
			myLocation = new LatLng(location.getLatitude(),
					location.getLongitude());
			Log.e("GrabTaskFragment", "location:" + "(" + myLocation.latitude
					+ "," + myLocation.longitude + ")");
			initData(ServicesConfig.GRABTASK, REFRESH_TYPE_DEFAULT);
		}
	}

	public void initData(String url, String flag, Param... params) {
		if (TextUtils.equals(REFRESH_TYPE_DEFAULT, flag)) {
			startProgressDialog("正在查询...");
		} else if (TextUtils.equals(REFRESH_TYPE_FILTER, flag)) {
			startProgressDialog("正在查询...");
			mSwipeListView.setPullRefreshEnable(false);
		}
		mSwipeListView.setFooterViewInvisible();
		Request request = httpEngine.createRequest(url, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.GRAB_GET_DATA_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request request, IOException e) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				handler.sendMessage(msg);
			}
		});

	}

	private void init() {
		mHeadTitle.setText("抢单");
		initListView();
		mSwipeListView.setRefreshTime(RefreshTime.getRefreshTime(mActivity));
		mSwipeListView.setMenuCreator(creator);
		mSwipeListView.setPullRefreshEnable(true);
		mSwipeListView.setPullLoadEnable(true);
		mSwipeListView.setXListViewListener(this);
		mHeadView = LayoutInflater.from(mActivity).inflate(R.layout.listview_header_filter, null);
		mHeadView.setOnClickListener(null);
		mSwipeListView.addHeaderView(mHeadView);
		mPartTimeDoFifter = (LinearLayout) mHeadView.findViewById(R.id.ll_grab_task);
		mKeyWordSearch = (TextView) mHeadView.findViewById(R.id.et_do_parttime_search);
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
				bundle.putString("CommunityName", data.getTaskPackageName());
				bundle.putString("TaskNo", data.getTaskNo());
				bundle.putString("Longitude", data.getLongitude());
				bundle.putString("Latitude", data.getLatitude());
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
				initData(ServicesConfig.GRABTASK, REFRESH_TYPE_DEFAULT);
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
			Param zoneParam = new Param(Constant.FILTERZONE, mZone);
			Param timeParam = new Param(Constant.FILTERTIME, mTime);
			if (list != null) {
				list.clear();
			}
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
			initData(ServicesConfig.DO_FILTER, REFRESH_TYPE_FILTER, zoneParam,
					timeParam);
		} else if (requestCode == StatusCode.RESULT_GRAB_DO_SEARCH
				&& resultCode == Activity.RESULT_OK) {
			String keyWord = data.getStringExtra("mKeyWord");
			Param keyWordParam = new Param(Constant.KEYWORD, keyWord);
			if (list != null) {
				list.clear();
			}
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
			initData(ServicesConfig.GRABTASK, REFRESH_TYPE_FILTER, keyWordParam);
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
		if (list != null) {
			list.clear();
		}
		initData(ServicesConfig.GRABTASK, REFRESH_TYPE_LOADING);
		mAdapter.notifyDataSetChanged();
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


	private void showDialog(final int position) {
		effect = Effectstype.Slideright;
		dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
				.withDividerColor("#11000000").withMessage("您是否要接此单？")
				.withMessageColor(R.color.main_primary)
				.withDialogColor("#FFFFFFFF").isCancelableOnTouchOutside(true)
				.withDuration(700).withEffect(effect)
				.withButtonDrawable(R.color.main_white).withButton1Text("否")
				.withButton1Color("#DD47BEE9").withButton2Text("是")
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
						String mLoginName = mSharedPreferences.getString(
								SharedPreferencesKeys.CURRENT_LOGIN_NAME, null);
						String mTaskNo = list.get(position).getTaskNo();
						Param paramPhone = new Param(Constant.LOGINNAME,
								mLoginName);
						Param paramTaskNo = new Param(Constant.TASKNO, mTaskNo);
						Request request = httpEngine.createRequest(
								ServicesConfig.Do_GRABTASK, paramPhone,
								paramTaskNo);
						Call call = httpEngine.createRequestCall(request);
						call.enqueue(new Callback() {

							@Override
							public void onFailure(Request request, IOException e) {
								Message msg = new Message();
								msg.what = StatusCode.RESPONSE_NET_FAILED;
								handler.sendMessage(msg);
							}

							@Override
							public void onResponse(Response response)
									throws IOException {
								Message msg = new Message();
								if (response.isSuccessful()) {
									msg.what = StatusCode.GRAG_RESPONSE_SUCCESS;
									msg.obj = response.body().string();
								} else {
									msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
								}
								handler.sendMessage(msg);
							}
						});
					}
				}).show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mLocationClient.isStarted()) {
			mLocationClient.unRegisterLocationListener(myListener);
			mLocationClient.stop();
		}
		Log.e("GrabTaskFragment", "onDestroy");
	}
}
