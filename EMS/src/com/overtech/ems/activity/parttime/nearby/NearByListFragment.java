package com.overtech.ems.activity.parttime.nearby;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.adapter.GrabTaskAdapter;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.parttime.common.PackageDetailActivity;
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
import com.overtech.ems.widget.swipemenu.SwipeMenu;
import com.overtech.ems.widget.swipemenu.SwipeMenuCreator;
import com.overtech.ems.widget.swipemenu.SwipeMenuItem;
import com.overtech.ems.widget.swipemenu.SwipeMenuListView;
import com.overtech.ems.widget.swipemenu.SwipeMenuListView.OnMenuItemClickListener;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import android.R.integer;
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

@SuppressWarnings("unchecked")
public class NearByListFragment extends BaseFragment {

	private SwipeMenuListView mNearBySwipeListView;
	private SwipeMenuCreator creator;
	private Activity mActivity;
	private Effectstype effect;
	private ArrayList<TaskPackage> list;
	private LatLng myLocation;
	private GrabTaskAdapter mAdapter;
	private String tagItem;
	private Set<String> tagSet;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Gson gson = new Gson();
			switch (msg.what) {
			case StatusCode.GRAG_RESPONSE_SUCCESS:
				String status = (String) msg.obj;
				StatusCodeBean bean = gson.fromJson(status,StatusCodeBean.class);
				String content = bean.getModel();
				if (TextUtils.equals(content, "0")) {
					Utilities.showToast("请不要重复抢单", context);
				} else if (TextUtils.equals(content, "1")) {
					Utilities.showToast("抢单成功，等待第二个人抢", context);
					tagItem = bean.getTaskNo();
					if (!AppUtils.isValidTagAndAlias(tagItem)) {
						Utilities.showToast("格式不对", context);
					} else {
						tagSet.add(tagItem);
						JPushInterface.setAliasAndTags(getActivity().getApplicationContext(), null, tagSet,mTagsCallback);
					}
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
				} else if (TextUtils.equals(content, "3")) {
					Utilities.showToast("差一点就抢到了", context);
				} else if (TextUtils.equals(content, "4")) {
					Utilities.showToast("维保日期的电梯数量已经超过10台，不能够再抢单。", context);
				}else {
					Utilities.showToast("用户账户异常", context);
					Intent intent=new Intent(getActivity(),LoginActivity.class);
					startActivity(intent);
					getActivity().finish();
				}
				break;
			case StatusCode.MSG_SET_TAGS:
				Log.d("24梯", "Set tags in handler.");
				JPushInterface.setAliasAndTags(getActivity()
						.getApplicationContext(), null, (Set<String>) msg.obj,
						mTagsCallback);
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast("服务器异常", context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", context);
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
				mSharedPreferences.edit().putStringSet("tagSet", tags).commit();// 成功保存标签后，将标签放到本地
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
		View view = inflater.inflate(R.layout.fragment_nearby_list, container,false);
		Set<String> tempSet = mSharedPreferences.getStringSet("tagSet", null);
		if (tempSet == null) {
			tagSet = new LinkedHashSet<String>();
		} else {
			tagSet = tempSet;
		}
		getExtralData();
		initListView(view);
		return view;
	}

	private void getExtralData() {
		Bundle bundle = getArguments();
		if (null == bundle) {
			return;
		}
		list = (ArrayList<TaskPackage>) getArguments().getSerializable(
				"taskPackage");
		myLocation = new LatLng(bundle.getDouble("latitude"),
				bundle.getDouble("longitude"));
	}

	private void initListView(View view) {
		mNearBySwipeListView = (SwipeMenuListView) view
				.findViewById(R.id.sl_nearby_listview);
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
		mAdapter = new GrabTaskAdapter(list, myLocation, mActivity);
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
				bundle.putString("CommunityName", data.getTaskPackageName());
				bundle.putString("TaskNo", data.getTaskNo());
				bundle.putString("Longitude", data.getLongitude());
				bundle.putString("Latitude", data.getLatitude());
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
}
