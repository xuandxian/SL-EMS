package com.overtech.ems.activity.parttime.fragment;

import java.io.IOException;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.parttime.MainActivity;
import com.overtech.ems.activity.parttime.nearby.NearByListFragment;
import com.overtech.ems.activity.parttime.nearby.NearByMapFragment;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.TaskPackageBean;
import com.overtech.ems.entity.bean.TaskPackageBean.TaskPackage;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class NearByFragment extends BaseFragment implements OnClickListener {

	private TextView mNearByMapTextView;
	private TextView mNearByListTextView;
	private View view;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private Fragment mNearByMap;
	private Fragment mNearByList;
	private TextView mHeadTitle;
	private List<TaskPackage> list;
	private Bundle bundle;
	private double mLatitude;
	private double mLongitude;
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			Gson gson = new Gson();
			switch (msg.what) {
			case StatusCode.GET_DATA_BY_MYLOCATION_SUCCESS:
				String json = (String) msg.obj;
				TaskPackageBean tasks = gson.fromJson(json,
						TaskPackageBean.class);
				list = tasks.body.data;
				setDefaultView(list);
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_nearby, container, false);
		initBaiduMapView(view);
		init();
		return view;
	}

	private void initBaiduMapView(View v) {
		mLatitude = ((MyApplication) getActivity().getApplicationContext()).latitude;
		mLongitude = ((MyApplication) getActivity().getApplicationContext()).longitude;
		if (mLatitude == 0 || mLongitude == 0) {
			Utilities.showToast("定位失败！！！", context);
		} else {
			Param latitude = new Param(Constant.LATITUDE,
					String.valueOf(mLatitude));
			Param longitude = new Param(Constant.LONGITUDE,
					String.valueOf(mLongitude));
			getDataByLocation(ServicesConfig.NEARBY, "0", latitude, longitude);
		}
	}

	private void init() {
		mNearByMapTextView = (TextView) view.findViewById(R.id.tv_nearby_map);
		mNearByListTextView = (TextView) view.findViewById(R.id.tv_nearby_list);
		mHeadTitle = (TextView) view.findViewById(R.id.tv_headTitle);
		mHeadTitle.setText("附近");
		mNearByMapTextView.setOnClickListener(this);
		mNearByListTextView.setOnClickListener(this);
		manager = getFragmentManager();
		mNearByMap = new NearByMapFragment();
		mNearByList = new NearByListFragment();
	}

	private void setDefaultView(List<TaskPackage> list) {
		setExtralData(mNearByMap);
		transaction = manager.beginTransaction();
		transaction.replace(R.id.rl_nearby_content, mNearByMap, "mNearByMap");
		transaction.commit();
	}

	public void switchContent(Fragment from, Fragment to) {
		transaction = manager.beginTransaction();
		if (!to.isAdded()) {
			setExtralData(to);
			transaction.hide(from).add(R.id.rl_nearby_content, to).commit();
		} else {
			transaction.hide(from).show(to).commit();
		}
	}

	private void setExtralData(Fragment fragment) {
		if (bundle == null) {
			bundle = new Bundle();
		}
		((MainActivity) getActivity()).getListCallback(list);// 数据已经改变，通知mainactivity更新
		bundle.putDouble(Constant.LONGITUDE, mLongitude);
		bundle.putDouble(Constant.LATITUDE, mLatitude);
		fragment.setArguments(bundle);
	}

	private void getDataByLocation(String url, String flag, Param... params) {
		if (TextUtils.equals(flag, "0")) {
			startProgressDialog("正在定位...");
		}
		Request request = httpEngine.createRequest(url, params);
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
			switchContent(mNearByList, mNearByMap);
			mNearByMapTextView
					.setBackgroundResource(R.drawable.horizontal_line);
			mNearByListTextView.setBackgroundResource(R.color.main_white);
			mNearByMapTextView.setTextColor(Color.rgb(0, 163, 233));
			mNearByListTextView.setTextColor(getResources().getColor(
					R.color.main_secondary));
			break;
		case R.id.tv_nearby_list:
			switchContent(mNearByMap, mNearByList);
			mNearByMapTextView.setBackgroundResource(R.color.main_white);
			mNearByListTextView
					.setBackgroundResource(R.drawable.horizontal_line);
			mNearByMapTextView.setTextColor(getResources().getColor(
					R.color.main_secondary));
			mNearByListTextView.setTextColor(Color.rgb(0, 163, 233));
			break;
		}
	}

	/**
	 * 告诉NearByFragmentList 数据已经更新
	 * 
	 * @author Overtech
	 * 
	 */
	public interface NearByMapCallBack {
		void getListCallback(List<TaskPackage> list);
	}
}