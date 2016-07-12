package com.overtech.ems.activity.parttime.nearby;

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
import android.text.TextUtils;
import android.util.Log;
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
import com.overtech.ems.activity.parttime.fragment.NearByFragment;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.StatusCodeBean;
import com.overtech.ems.entity.bean.TaskPackageBean.TaskPackage;
import com.overtech.ems.entity.common.Requester;
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
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

@SuppressWarnings("unchecked")
public class NearByListFragment extends BaseFragment {

	private SwipeMenuListView mNearBySwipeListView;
	private SwipeMenuCreator creator;
	private TextView tvNoData;
	private Activity mActivity;
	private Effectstype effect;
	private List<TaskPackage> list;
	private LatLng myLocation;
	private GrabTaskAdapter mAdapter;
	private String tagItem;
	private Set<String> tagSet;
	private String uid;
	private String certificate;
	private double latitude;
	private double longitude;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.GRAG_RESPONSE_SUCCESS:
				stopProgressDialog();
				String status = (String) msg.obj;
				StatusCodeBean bean = gson.fromJson(status,
						StatusCodeBean.class);
				int st = bean.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(bean.msg, activity);
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(activity, LoginActivity.class);
					startActivity(intent);
					return;
				}
				String content = bean.body.get("status").toString();
				if (TextUtils.equals(content, "0")) {
					Utilities.showToast(bean.msg, activity);
				} else if (TextUtils.equals(content, "1")) {
					Utilities.showToast(bean.msg, activity);
					tagItem = bean.body.get("taskNo").toString();
					if (!AppUtils.isValidTagAndAlias(tagItem)) {
						Utilities.showToast("格式不对", activity);
					} else {
						tagSet.add(tagItem);
						JPushInterface.setAliasAndTags(getActivity()
								.getApplicationContext(), null, tagSet,
								mTagsCallback);
					}
				} else if (TextUtils.equals(content, "2")) {
					Utilities.showToast(bean.msg, activity);
					// 推送业务代码
					tagItem = bean.body.get("taskNo").toString();
					if (!AppUtils.isValidTagAndAlias(tagItem)) {
						Utilities.showToast("格式不对", activity);
					} else {
						tagSet.add(tagItem);
						JPushInterface.setAliasAndTags(getActivity()
								.getApplicationContext(), null, tagSet,
								mTagsCallback);
					}
				} else if (TextUtils.equals(content, "3")) {
					Utilities.showToast(bean.msg, activity);
				} else if (TextUtils.equals(content, "4")) {
					Utilities.showToast(bean.msg, activity);
				} else {
					Utilities.showToast(bean.msg, activity);
					Intent intent = new Intent(getActivity(),
							LoginActivity.class);
					startActivity(intent);
				}
				break;
			case StatusCode.MSG_SET_TAGS:
				Log.d("24梯", "Set tags in handler.");
				JPushInterface.setAliasAndTags(getActivity()
						.getApplicationContext(), null, (Set<String>) msg.obj,
						mTagsCallback);
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast(R.string.response_failure_msg, activity);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast(R.string.request_error_msg, activity);
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
				SharePreferencesUtils.put(mActivity,
						SharedPreferencesKeys.TAGSET, tags);
				break;
			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				if (AppUtils.isConnected(getActivity().getApplicationContext())) {
					handler.sendMessageDelayed(handler.obtainMessage(
							StatusCode.MSG_SET_TAGS, tags), 1000 * 60);
				} else {
				}
				break;

			default:
				logs = "Failed with errorCode = " + code;
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

		Set<String> tempSet = (Set<String>) SharePreferencesUtils.get(
				mActivity, SharedPreferencesKeys.TAGSET,
				new LinkedHashSet<String>());

		mNearBySwipeListView = (SwipeMenuListView) view
				.findViewById(R.id.sl_nearby_listview);
		tvNoData = (TextView) view.findViewById(R.id.tv_no_data);
		getExtralData();
		initListView(view);
		return view;
	}

	private void getExtralData() {
		uid = ((MainActivity) getActivity()).getUid();
		certificate = ((MainActivity) getActivity()).getCertificate();
		latitude = ((MyApplication) getActivity().getApplicationContext()).latitude;
		longitude = ((MyApplication) getActivity().getApplicationContext()).longitude;
		list = ((NearByFragment) getParentFragment()).getData();
		myLocation = new LatLng(latitude, longitude);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		Logr.e("nearbylist fragment=="+hidden);
		if (!hidden) {
			if (list == null || list.size() == 0) {
				list = ((NearByFragment) getParentFragment()).getData();
				if (list == null || list.size() == 0) {
					mNearBySwipeListView.setVisibility(View.GONE);
					tvNoData.setVisibility(View.VISIBLE);
				} else {
					mNearBySwipeListView.setVisibility(View.VISIBLE);
					tvNoData.setVisibility(View.GONE);
				}
				if (mAdapter != null) {
					mAdapter.setData(list);
					mAdapter.notifyDataSetChanged();
				} else {
					mAdapter = new GrabTaskAdapter(list, mActivity);
					mNearBySwipeListView.setAdapter(mAdapter);
				}
			}
		}
	}

	private void initListView(View view) {
		if (list == null || list.size() == 0) {
			tvNoData.setVisibility(View.VISIBLE);
			mNearBySwipeListView.setVisibility(View.GONE);
		} else {
			tvNoData.setVisibility(View.GONE);
			mNearBySwipeListView.setVisibility(View.VISIBLE);
		}

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
		mAdapter = new GrabTaskAdapter(list, mActivity);
		mNearBySwipeListView.setAdapter(mAdapter);
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
						String mTaskNo = list.get(position).taskNo;
						startProgressDialog(getResources().getString(
								R.string.loading_public_default));
						Requester requester = new Requester();
						requester.cmd = 20023;
						requester.uid = uid;
						requester.certificate = certificate;
						requester.body.put(Constant.TASKNO, mTaskNo);
						Request request = httpEngine.createRequest(
								SystemConfig.NEWIP, gson.toJson(requester));
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
}
