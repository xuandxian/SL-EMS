package com.overtech.ems.activity.parttime.personal.notice;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.PersonalAnnounceAdapter;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.AnnouncementBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * 公告栏
 * 
 */
public class PersonalNoticeActivity extends BaseActivity {
	private ImageView mDoBack;
	private TextView mHeadContent;
	private ListView mAnnouncement;
	private PersonalNoticeActivity activity;
	private PersonalAnnounceAdapter adapter;
	private List<Map<String, Object>> list;
	private int announceSize;
	private HashSet<String> announceItemPosition;
	private final String ANNOUNCEITEMPOSITION = "announce_item_position";
	private final String ANNOUNCE_SIZE = "announces_size";
	private String uid;
	private String certificate;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.ANNOUNCEMENT_SUCCESS:
				String json = (String) msg.obj;
				AnnouncementBean bean = gson.fromJson(json,
						AnnouncementBean.class);
				int st = bean.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(bean.msg, activity);
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(activity, LoginActivity.class);
					startActivity(intent);
				}
				list = (List<Map<String, Object>>) bean.body.get("data");
				if (null == list || list.size() == 0) {
					Utilities.showToast(getString(R.string.response_no_data),
							activity);
				} else {
					int newItems = list.size() - announceSize;// 计算最新的数据集合的大小与之前的数据集合大小
					if (newItems > 0) {
						HashSet<String> tempSet = new HashSet<String>();// 初始化一个tempSet,用于保存更新后的数据的已经点击的公告条目的位置
						Iterator<String> iterator = announceItemPosition
								.iterator();
						while (iterator.hasNext()) {
							int a = Integer.parseInt(iterator.next())
									+ newItems;// 如果原集合中有数据，则将保存的角标全部加上新的计算的差值
							tempSet.add(String.valueOf(a));
						}
						announceItemPosition = tempSet;// 使用最新的集合
						SharePreferencesUtils.put(activity,
								ANNOUNCEITEMPOSITION, announceItemPosition);// 将位置保存
						SharePreferencesUtils.put(activity, ANNOUNCE_SIZE,
								list.size());// 将集合大小保存
					}
					adapter = new PersonalAnnounceAdapter(activity, list,
							announceItemPosition);
					mAnnouncement.setAdapter(adapter);
				}
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast(R.string.response_failure_msg, activity);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast(R.string.request_error_msg, activity);
				break;

			default:
				break;
			}
			stopProgressDialog();
		};
	};

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_personal_announcement;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = this;
		stackInstance.pushActivity(activity);
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");

		initView();
		initData();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e("onResume", "onresume");
		announceSize = (Integer) SharePreferencesUtils.get(activity,
				ANNOUNCE_SIZE, 0);// 获取保存的公告条目的长度
		announceItemPosition = (HashSet<String>) SharePreferencesUtils.get(
				activity, ANNOUNCEITEMPOSITION, new HashSet<String>());// 获取已经点击过的公告条目的position集合
		if (adapter != null) {
			adapter.setHashSet(announceItemPosition);
			adapter.notifyDataSetChanged();
		}
	}

	private void initView() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mAnnouncement = (ListView) findViewById(R.id.lv_announcement);
		mHeadContent.setText("公告");
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stackInstance.popActivity(activity);
			}
		});
		mAnnouncement.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				announceItemPosition.add(String.valueOf(position));// 将点击的位置保存到集合中
				SharePreferencesUtils.put(activity, ANNOUNCEITEMPOSITION,
						announceItemPosition);
				/*
				 * Iterator<String> it = announceItemPosition.iterator();//测试使用
				 * while (it.hasNext()) { Log.e("==dianji==", it.next()); } Map
				 * map=mSharedPreferences.getAll(); Set set=map.keySet();
				 * Iterator iterator=set.iterator(); while(iterator.hasNext()){
				 * Log.e("=====", iterator.next()+""); }
				 */
				Map<String, Object> data = (Map<String, Object>) parent
						.getAdapter().getItem(position);
				Bundle bundle = new Bundle();
				bundle.putString(Constant.ANNOUNCEMENTID, data.get("id")
						.toString());
				Intent intent = new Intent(activity,
						PersonalNoticeDetailActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void initData() {
		startProgressDialog(getResources().getString(
				R.string.loading_public_default));
		Requester requester = new Requester();
		requester.cmd = 20077;
		requester.uid = uid;
		requester.certificate = certificate;
		Request request = httpEngine.createRequest(SystemConfig.NEWIP,
				gson.toJson(requester));
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.ANNOUNCEMENT_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
					msg.obj = "服务器异常";
				}
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(Request arg0, IOException arg1) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				msg.obj = "网络异常";
				handler.sendMessage(msg);
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		stackInstance.popActivity(activity);
	}

}
