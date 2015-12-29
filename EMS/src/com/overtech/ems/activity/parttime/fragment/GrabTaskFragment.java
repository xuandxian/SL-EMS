package com.overtech.ems.activity.parttime.fragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.adapter.GrabTaskAdapter;
import com.overtech.ems.activity.parttime.common.PackageDetailActivity;
import com.overtech.ems.activity.parttime.grabtask.GrabTaskDoFilterActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.StatusCodeBean;
import com.overtech.ems.entity.bean.TaskPackageBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.TaskPackage;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.EditTextWithDelete;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;
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
	private ImageView mPartTimeDoFifter;
	private EditTextWithDelete mKeyWordSearch;
	private Effectstype effect;
	private GrabTaskAdapter mAdapter;
	private ArrayList<TaskPackage> list;
	private Handler mHandler;
	private TextView mHeadTitle;

	public LatLng myLocation;

	public LocationClient mLocationClient = null;

	public BDLocationListener myListener = new MyLocationListener();

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Gson gson = new Gson();
			switch (msg.what) {
			case StatusCode.GRAB_SUCCESS:
				String json = (String) msg.obj;
				TaskPackageBean tasks = gson.fromJson(json,
						TaskPackageBean.class);
				list = (ArrayList<TaskPackage>) tasks.getModel();
				if (null == myLocation) {
					Utilities.showToast("定位失败", context);
					return;
				}
				mAdapter = new GrabTaskAdapter(list, myLocation, mActivity);
				mSwipeListView.setAdapter(mAdapter);
				break;
			case StatusCode.GRAB_FAILED:
				Utilities.showToast("请求失败", context);
				break;
			case StatusCode.GRAG_RESPONSE_SUCCESS:
				String status = (String) msg.obj;
				StatusCodeBean bean = gson.fromJson(status,
						StatusCodeBean.class);
				String content = bean.getModel();
				if (TextUtils.equals(content, "0")) {
					Utilities.showToast("请不要重复抢单", context);
				} else if (TextUtils.equals(content, "1")) {
					Utilities.showToast("抢单成功，等待第二个人抢", context);
				} else if (TextUtils.equals(content, "2")) {
					Utilities.showToast("抢单成功，请到任务中查看", context);
				} else if (TextUtils.equals(content, "3")) {
					Utilities.showToast("差一点就抢到了", context);
				}
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", context);
				break;
			case StatusCode.GRAG_RESPONSE_OTHER_FAILED:
				Utilities.showToast("网络异常", context);
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
		View view = inflater.inflate(R.layout.fragment_grab_task, container,
				false);
		initBaiDuLocation();
		findViewById(view);
		init();
		return view;
	}

	private void initBaiDuLocation() {
		mLocationClient = new LocationClient(activity.getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPRS
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(1000); // 设置发起定位请求的间隔时间为1000ms
		mLocationClient.setLocOption(option); // 设置定位参数
		mLocationClient.start();
	}

	private void findViewById(View view) {
		mSwipeListView = (PullToRefreshSwipeMenuListView) view
				.findViewById(R.id.sl_qiandan_listview);
		mHeadTitle = (TextView) view.findViewById(R.id.tv_headTitle);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (null == location) {
				Log.e("GrabTaskFragment", "定位失败");
				return;
			}
			Log.e("GrabTaskFragment", "定位成功");
			myLocation = new LatLng(location.getLatitude(),
					location.getLongitude());
			Log.e("GrabTaskFragment", "location:" + "(" + myLocation.latitude
					+ "," + myLocation.longitude + ")");
			initData(ServicesConfig.GRABTASK);
		}
	}

	public void initData(String url, Param... params) {
		startProgressDialog("正在查询...");
		Request request = httpEngine.createRequest(url, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.GRAB_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.GRAB_FAILED;
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request request, IOException e) {
				Message msg = new Message();
				msg.what = StatusCode.GRAB_FAILED;
				handler.sendMessage(msg);
			}
		});

	}

	private void init() {
		dialogBuilder = NiftyDialogBuilder.getInstance(mActivity);
		progressDialog = CustomProgressDialog.createDialog(mActivity);
		mHeadTitle.setText("抢单");
		initListView();
		mSwipeListView.setMenuCreator(creator);
		mSwipeListView.setPullRefreshEnable(true);
		mSwipeListView.setPullLoadEnable(true);
		mSwipeListView.setXListViewListener(this);
		View mHeadView = LayoutInflater.from(mActivity).inflate(
				R.layout.listview_header_filter, null);
		mSwipeListView.addHeaderView(mHeadView);
		mPartTimeDoFifter = (ImageView) mHeadView
				.findViewById(R.id.iv_parttime_do_fifter);
		mKeyWordSearch = (EditTextWithDelete) mHeadView
				.findViewById(R.id.et_do_parttime_search);
		mHandler = new Handler();
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
				bundle.putString("CommunityName", data.getProjectName());
				bundle.putString("TaskNo", data.getTaskNo());
				bundle.putString("Longitude", data.getLongitude());
				bundle.putString("Latitude", data.getLatitude());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mPartTimeDoFifter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mActivity,
						GrabTaskDoFilterActivity.class);
				startActivityForResult(intent, 0x1);
			}
		});
		mKeyWordSearch.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					String keyWord = view.getText().toString().trim();
					Param param = new Param("mKeyWord", keyWord);
					initData(ServicesConfig.GRABTASK, param);
				}
				return true;
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0x1 && resultCode == Activity.RESULT_OK) {
			String mZone = data.getStringExtra("mZone");
			String mTime = data.getStringExtra("mTime");
			Param zoneParam = new Param("mFilterZone", mZone);
			Param timeParam = new Param("mFilterTime", mTime);
			if (list != null) {
				list.clear();
			}
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
			initData(ServicesConfig.DO_FILTER, zoneParam, timeParam);
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
		Utilities.showToast("下拉刷新", mActivity);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm",
						Locale.getDefault());
				RefreshTime.setRefreshTime(mActivity, df.format(new Date()));
				onLoad();
			}
		}, 2000);
	}

	public void onLoadMore() {
		Utilities.showToast("上拉加载", mActivity);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				onLoad();
			}
		}, 2000);
	}

	private void onLoad() {
		mSwipeListView.setRefreshTime(RefreshTime.getRefreshTime(mActivity));
		mSwipeListView.stopRefresh();
		mSwipeListView.stopLoadMore();
	}

	public class BDLocationListenerImpl implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			double mLatitude = location.getLatitude();
			double mLongitude = location.getLongitude();
			Log.e("经纬度", "经纬度：" + "(" + mLongitude + "," + mLatitude + ")");
			myLocation = new LatLng(location.getLatitude(),
					location.getLongitude());
		}
	}

	private void showDialog(final int position) {
		effect = Effectstype.Slideright;
		dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
				.withDividerColor("#11000000").withMessage("您是否要接此单？")
				.withMessageColor(R.color.main_primary)
				.withDialogColor("#FFFFFFFF").isCancelableOnTouchOutside(true)
				.withDuration(700).withEffect(effect)
				.withButtonDrawable(R.color.main_white).withButton1Text("否")
				.withButton1Color(R.color.main_primary).withButton2Text("是")
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
						startProgressDialog("正在抢单...");
						String mLoginName = mSharedPreferences.getString(
								SharedPreferencesKeys.LOGIN_NAME, null);
						String mTaskNo = list.get(position).getTaskNo();
						Param paramPhone = new Param("loginName", mLoginName);
						Param paramTaskNo = new Param("taskNo", mTaskNo);
						Request request = httpEngine.createRequest(
								ServicesConfig.Do_GRABTASK, paramPhone,
								paramTaskNo);
						Call call = httpEngine.createRequestCall(request);
						call.enqueue(new Callback() {

							@Override
							public void onFailure(Request request, IOException e) {
								Message msg = new Message();
								msg.what = StatusCode.GRAG_RESPONSE_OTHER_FAILED;
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
									msg.what = StatusCode.RESPONSE_NET_FAILED;
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
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
