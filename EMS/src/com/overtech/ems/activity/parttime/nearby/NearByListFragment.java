package com.overtech.ems.activity.parttime.nearby;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.overtech.ems.entity.bean.Bean;
import com.overtech.ems.http.HttpConnector;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.AppUtils;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.swipemenu.SwipeMenu;
import com.overtech.ems.widget.swipemenu.SwipeMenuCreator;
import com.overtech.ems.widget.swipemenu.SwipeMenuItem;
import com.overtech.ems.widget.swipemenu.SwipeMenuListView;
import com.overtech.ems.widget.swipemenu.SwipeMenuListView.OnMenuItemClickListener;

@SuppressWarnings("unchecked")
public class NearByListFragment extends BaseFragment {

	private SwipeMenuListView mNearBySwipeListView;
	private SwipeMenuCreator creator;
	private TextView tvNoData;
	private Activity mActivity;
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
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put("latitude", String.valueOf(latitude));
		body.put("longitude", String.valueOf(longitude));
		HttpConnector<Bean> conn = new HttpConnector<Bean>(20030, uid,
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

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bizStIs1Deal() {
				// TODO Auto-generated method stub

			}

			@Override
			public void stopDialog() {
				// TODO Auto-generated method stub

			}
		};
		conn.sendRequest();
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
				openItem.setBackground(R.drawable.abc_popup_background_mtrl_mult);
				openItem.setWidth(Utilities.dp2px(mActivity, 120));
				openItem.setTitle("抢");
				openItem.setTitleSize(18);
				openItem.setTitleColor(getResources().getColor(
						R.color.colorPrimary));
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

	private void grabTask(String taskNo) {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		HashMap<String, Object> body = new HashMap<String, Object>();
		body.put(Constant.TASKNO, taskNo);
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

			@Override
			public void bizFailed() {
				// TODO Auto-generated method stub
			}

			@Override
			public void bizStIs1Deal() {
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
			public void bizStIs1Deal() {
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
