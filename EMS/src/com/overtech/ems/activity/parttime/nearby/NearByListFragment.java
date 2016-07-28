package com.overtech.ems.activity.parttime.nearby;

import java.util.HashSet;
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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.baidu.mapapi.model.LatLng;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.adapter.GrabTaskAdapter;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.activity.parttime.common.PackageDetailActivity;
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
import com.overtech.ems.widget.swipemenu.SwipeMenu;
import com.overtech.ems.widget.swipemenu.SwipeMenuCreator;
import com.overtech.ems.widget.swipemenu.SwipeMenuItem;
import com.overtech.ems.widget.swipemenu.SwipeMenuListView;
import com.overtech.ems.widget.swipemenu.SwipeMenuListView.OnMenuItemClickListener;
import com.squareup.okhttp.Request;

@SuppressWarnings("unchecked")
public class NearByListFragment extends BaseFragment {

	private SwipeMenuListView mNearBySwipeListView;
	private SwipeMenuCreator creator;
	private TextView tvNoData;
	private Activity mActivity;
	private Effectstype effect;
	private List<Map<String, Object>> list;
	private LatLng myLocation;
	private GrabTaskAdapter mAdapter;
	private String uid;
	private String certificate;
	private double latitude;
	private double longitude;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.MSG_SET_TAGS:
				Logr.d("24梯", "Set tags in handler.");
				JPushInterface.setAliasAndTags(getActivity()
						.getApplicationContext(), null, (Set<String>) msg.obj,
						mTagsCallback);
				break;
			}
		};
	};

	private ResultCallback<Bean> grabCallback = new ResultCallback<Bean>() {

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
			int st = response.st;
			if (st == -1 || st == -2) {
				Utilities.showToast(response.msg, activity);
				SharePreferencesUtils.put(activity, SharedPreferencesKeys.UID,
						"");
				SharePreferencesUtils.put(activity,
						SharedPreferencesKeys.CERTIFICATED, "");
				Intent intent = new Intent(activity, LoginActivity.class);
				startActivity(intent);
				return;
			} else if (st == 0) {
				String content = response.body.get("status").toString();
				if (TextUtils.equals(content, "0")) {
					Utilities.showToast(response.msg, activity);
				} else if (TextUtils.equals(content, "1")) {// 抢单成功，等待第二个人抢
					Utilities.showToast(response.msg, activity);
					// 待刷新页面
					onRefresh();
					loadNotDoneTask();
					// 推送业务
				} else if (TextUtils.equals(content, "2")) {// 抢单成功，到任务单中查看
					Utilities.showToast(response.msg, activity);
					onRefresh();
					// 推送业务代码
					loadNotDoneTask();
				} else if (TextUtils.equals(content, "3")) {
					Utilities.showToast(response.msg, activity);
				} else if (TextUtils.equals(content, "4")) {
					Utilities.showToast(response.msg, activity);
				} else {
					Utilities.showToast(response.msg, activity);
					Intent intent = new Intent(getActivity(),
							LoginActivity.class);
					startActivity(intent);
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

	private ResultCallback<Bean> nearCallback = new ResultCallback<Bean>() {

		@Override
		public void onError(Request request, Exception e) {
			// TODO Auto-generated method stub
			Logr.e(request.toString());
		}

		@Override
		public void onResponse(Bean response) {
			// TODO Auto-generated method stub
			int st = response.st;
			if (st == -1 || st == -2) {
				Utilities.showToast(response.msg, activity);
				SharePreferencesUtils.put(activity, SharedPreferencesKeys.UID,
						"");
				SharePreferencesUtils.put(activity,
						SharedPreferencesKeys.CERTIFICATED, "");
				Intent intent = new Intent(activity, LoginActivity.class);
				startActivity(intent);
				return;
			} else if (st == 1) {// 上岗证相关
				Utilities.showToast(response.msg, activity);
			}
			list = (List<Map<String, Object>>) response.body.get("data");
			if (list == null || list.size() == 0) {
				tvNoData.setVisibility(View.VISIBLE);
				mNearBySwipeListView.setVisibility(View.GONE);
			} else {
				tvNoData.setVisibility(View.GONE);
				mNearBySwipeListView.setVisibility(View.VISIBLE);
				if (mAdapter == null) {
					mAdapter = new GrabTaskAdapter(list, mActivity);
					mNearBySwipeListView.setAdapter(mAdapter);
				} else {
					mAdapter.setData(list);
					mAdapter.notifyDataSetChanged();
				}
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
		View view = inflater.inflate(R.layout.fragment_nearby_list, container,
				false);
		mNearBySwipeListView = (SwipeMenuListView) view
				.findViewById(R.id.sl_nearby_listview);
		tvNoData = (TextView) view.findViewById(R.id.tv_no_data);
		initListView(view);
		getExtralData();
		onRefresh();
		return view;
	}

	public void onRefresh() {
		// TODO Auto-generated method stub
		Requester requester = new Requester();
		requester.cmd = 20030;
		requester.certificate = certificate;
		requester.uid = uid;
		requester.body.put("latitude", String.valueOf(latitude));
		requester.body.put("longitude", String.valueOf(longitude));

		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, nearCallback,
				gson.toJson(requester));
	}

	private void getExtralData() {
		uid = ((MainActivity) getActivity()).getUid();
		certificate = ((MainActivity) getActivity()).getCertificate();
		latitude = ((MyApplication) getActivity().getApplicationContext()).latitude;
		longitude = ((MyApplication) getActivity().getApplicationContext()).longitude;
		myLocation = new LatLng(latitude, longitude);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		Logr.e("nearbylist fragment==" + hidden);
		if (!hidden) {
			Logr.e("附近数据刷新了");
			onRefresh();
		}
	}

	private void initListView(View view) {
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
		mNearBySwipeListView.setMenuCreator(creator);
		mNearBySwipeListView
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,
							int index) {
						showDialog(position);
					}
				});
		mNearBySwipeListView.setOnItemClickListener(new OnItemClickListener() {

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
				startActivity(intent);
			}
		});
	}

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
						String mTaskNo = list.get(position).get("taskNo")
								.toString();
						startProgressDialog(getResources().getString(
								R.string.loading_public_default));
						Requester requester = new Requester();
						requester.cmd = 20023;
						requester.uid = uid;
						requester.certificate = certificate;
						requester.body.put(Constant.TASKNO, mTaskNo);
						OkHttpClientManager.postAsyn(SystemConfig.NEWIP,
								grabCallback, gson.toJson(requester));
					}
				}).show();
	}

	private ResultCallback<Bean> loadNotDoneCallback = new ResultCallback<Bean>() {

		@Override
		public void onError(Request request, Exception e) {
			// TODO Auto-generated method stub
			Logr.e(request.toString());
		}

		@Override
		public void onResponse(Bean response) {
			// TODO Auto-generated method stub
			int st = response.st;
			if (st == 0) {
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
		}
	};

	/**
	 * 加载未完成的任务单
	 */
	private void loadNotDoneTask() {
		Requester requester = new Requester();
		requester.cmd = 20050;
		requester.uid = uid;
		requester.certificate = certificate;
		OkHttpClientManager.postAsyn(SystemConfig.NEWIP, loadNotDoneCallback,
				gson.toJson(requester));
	}
}
