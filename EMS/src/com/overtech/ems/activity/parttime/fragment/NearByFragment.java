package com.overtech.ems.activity.parttime.fragment;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.activity.parttime.nearby.NearByListFragment;
import com.overtech.ems.activity.parttime.nearby.NearByMapFragment;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.TaskPackageBean;
import com.overtech.ems.entity.bean.TaskPackageBean.TaskPackage;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.utils.FragmentUtils;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class NearByFragment extends BaseFragment implements OnClickListener {

	private TextView mNearByMapTextView;
	private TextView mNearByListTextView;
	private View view;
	private Fragment currentFragment;
	private NearByMapFragment mNearByMap;
	private NearByListFragment mNearByList;
	private TextView mHeadTitle;
	private List<TaskPackage> list;
	private Bundle bundle;
	private double mLatitude;
	private double mLongitude;
	private String uid;
	private String certificate;
	private NearByMapCallback listener;
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.GET_DATA_BY_MYLOCATION_SUCCESS:
				String json = (String) msg.obj;
				TaskPackageBean tasks = gson.fromJson(json,
						TaskPackageBean.class);
				int st = tasks.st;
				if (st == -1 || st == -2) {
					Utilities.showToast(tasks.msg, activity);
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.UID, "");
					SharePreferencesUtils.put(activity,
							SharedPreferencesKeys.CERTIFICATED, "");
					Intent intent = new Intent(activity, LoginActivity.class);
					startActivity(intent);
					return;
				}
				list = tasks.body.data;
				if (listener != null) {
					listener.callback();
				}
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast("服务器异常", activity);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", activity);
				break;
			}
			stopProgressDialog();
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_nearby, container, false);
		uid = ((MainActivity) getActivity()).getUid();
		certificate = ((MainActivity) getActivity()).getCertificate();
		initBaiduMapView(view);
		init();
		return view;
	}

	private void initBaiduMapView(View v) {
		mLatitude = ((MyApplication) getActivity().getApplicationContext()).latitude;
		mLongitude = ((MyApplication) getActivity().getApplicationContext()).longitude;
		if (mLatitude == 0 || mLongitude == 0) {
			Utilities.showToast("定位失败！！！", activity);
			return;
		} else {
			Requester requester = new Requester();
			requester.cmd = 20030;
			requester.certificate = certificate;
			requester.uid = uid;
			requester.body.put("latitude", String.valueOf(mLatitude));
			requester.body.put("longitude", String.valueOf(mLongitude));
			getDataByLocation(SystemConfig.NEWIP, gson.toJson(requester));
		}
	}

	private void init() {
		mNearByMapTextView = (TextView) view.findViewById(R.id.tv_nearby_map);
		mNearByListTextView = (TextView) view.findViewById(R.id.tv_nearby_list);
		mHeadTitle = (TextView) view.findViewById(R.id.tv_headTitle);
		mHeadTitle.setText("附近");
		mNearByMapTextView.setOnClickListener(this);
		mNearByListTextView.setOnClickListener(this);
		currentFragment = FragmentUtils.switchFragment(
				getChildFragmentManager(), R.id.rl_nearby_content,
				currentFragment, NearByMapFragment.class, null);
		mNearByMap = (NearByMapFragment) currentFragment;
	}

	private void getDataByLocation(String url, String jsonData) {
		Request request = httpEngine.createRequest(url, jsonData);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.GET_DATA_BY_MYLOCATION_SUCCESS;
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

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_nearby_map:
			currentFragment = FragmentUtils.switchFragment(
					getChildFragmentManager(), R.id.rl_nearby_content,
					currentFragment, NearByMapFragment.class, null);
			mNearByMap = (NearByMapFragment) currentFragment;
			mNearByMapTextView
					.setBackgroundResource(R.drawable.horizontal_line);
			mNearByListTextView.setBackgroundResource(R.color.main_white);
			mNearByMapTextView.setTextColor(Color.rgb(0, 163, 233));
			mNearByListTextView.setTextColor(getResources().getColor(
					R.color.main_secondary));
			break;
		case R.id.tv_nearby_list:
			currentFragment = FragmentUtils.switchFragment(
					getChildFragmentManager(), R.id.rl_nearby_content,
					currentFragment, NearByListFragment.class, null);
			mNearByList = (NearByListFragment) currentFragment;

			mNearByMapTextView.setBackgroundResource(R.color.main_white);
			mNearByListTextView
					.setBackgroundResource(R.drawable.horizontal_line);
			mNearByMapTextView.setTextColor(getResources().getColor(
					R.color.main_secondary));
			mNearByListTextView.setTextColor(Color.rgb(0, 163, 233));
			break;
		}
	}

	public List<TaskPackage> getData() {
		return list;
	}

	public void setNearByMapCallback(NearByMapCallback listener) {
		this.listener = listener;
	}

	/**
	 * 数据加载完成后的回调
	 * 
	 * @author Overtech Will
	 * 
	 */
	public interface NearByMapCallback {
		void callback();
	}
}