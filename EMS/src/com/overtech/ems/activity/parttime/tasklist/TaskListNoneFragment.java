package com.overtech.ems.activity.parttime.tasklist;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.adapter.TaskListAdapter;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.TaskPackageBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView.OnMenuItemClickListener;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenu;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenuCreator;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenuItem;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class TaskListNoneFragment extends BaseFragment {
	private PullToRefreshSwipeMenuListView mSwipeListView;
	private SwipeMenuCreator creator;
	private Activity mActivity;
	private Effectstype effect;
	private LocationClient mLocationClient;
	private TaskListAdapter adapter;
	private double latitude;
	private double longitude;
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.TASKLIST_NONE_SUCCESS:
				String json=(String) msg.obj;
				Gson gson=new Gson();
				TaskPackageBean bean=gson.fromJson(json, TaskPackageBean.class);
				adapter=new TaskListAdapter(bean.getModel(), mActivity);
				mSwipeListView.setAdapter(adapter);
				break;
			case StatusCode.TASKLIST_NONE_FAILED:
				Utilities.showToast((String)msg.obj, mActivity);
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
		View view = inflater.inflate(R.layout.fragment_task_list_none,
				container, false);
		findViewById(view);
		init();
		return view;

	}
	

	private void findViewById(View view) {
		mSwipeListView = (PullToRefreshSwipeMenuListView) view.findViewById(R.id.sl_task_list_listview);
	}

	private void init() {
		mLocationClient=((MyApplication)getActivity().getApplication()).mLocationClient;
		mLocationClient.requestLocation();
		mLocationClient.start();
		initListView();
		mSwipeListView.setMenuCreator(creator);
		mSwipeListView
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,
							int index) {
						switch (index) {
						case 0:
							
							break;
						case 1:
							// Utilities.showToast("退单", mActivity);
							showDialog();
							break;
						}
					}
				});
		mSwipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Utilities.showToast("你点击了" + position + "位置", mActivity);
				Intent intent = new Intent(mActivity,
						TaskListPackageDetailActivity.class);
				startActivity(intent);
			}
		});
		startLoading();
	}

	private void startLoading() {
		startProgressDialog("正在加载");
		String loginName=mSharedPreferences.getString(SharedPreferencesKeys.CURRENT_LOGIN_NAME, null);
		Param param=new Param(Constant.LOGINNAME,loginName);
		Request request=httpEngine.createRequest(ServicesConfig.TASK_LIST_NONE,param);
		Call call=httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			
			@Override
			public void onResponse(Response response) throws IOException {
				Message msg=new Message();
				if(response.isSuccessful()){
					msg.what=StatusCode.TASKLIST_NONE_SUCCESS;
					msg.obj=response.body().string();
				}else{
					msg.what=StatusCode.TASKLIST_NONE_FAILED;
					msg.obj="数据异常，请稍后再试";
				}
				handler.sendMessage(msg);
			}
			
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				Message msg=new Message();
				msg.what=StatusCode.TASKLIST_NONE_FAILED;
				msg.obj="网络链接错误";
				handler.sendMessage(msg);
			}
		});
	}

	protected void showDialog() {
		effect = Effectstype.Slideright;
		dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
		.withDividerColor("#11000000").withMessage("您是否要退掉此单？")
		.withMessageColor(R.color.main_primary).withDialogColor("#FFFFFFFF")
		.isCancelableOnTouchOutside(true).withDuration(700)
		.withEffect(effect).withButtonDrawable(R.color.main_white)
		.withButton1Text("否").withButton1Color(R.color.main_primary)
		.withButton2Text("是").withButton2Color(R.color.main_primary)
		.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
						progressDialog.setMessage("正在退单");
						progressDialog.show();
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								progressDialog.dismiss();
							}
						}, 3000);
					}
				}).show();
	}

	private void initListView() {
		creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem navicateItem = new SwipeMenuItem(mActivity);
				navicateItem.setBackground(new ColorDrawable(Color.rgb(0xFF,0x9D, 0x00)));
				navicateItem.setWidth(dp2px(90));
				navicateItem.setTitle("导航");
				navicateItem.setTitleSize(18);
				navicateItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(navicateItem);
				SwipeMenuItem deleteItem = new SwipeMenuItem(mActivity);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xFF,0x3A, 0x30)));
				deleteItem.setWidth(dp2px(90));
				deleteItem.setTitle("退单");
				deleteItem.setTitleSize(18);
				deleteItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(deleteItem);
			}
		};
	}

	

	/*public void startNavicate() {
		// 构建 route搜索参数
		RouteParaOption para = new RouteParaOption().startName("我的位置")
				.startPoint(mLocation)// 路线检索起点
				.busStrategyType(EBusStrategyType.bus_recommend_way);
		try {
			BaiduMapRoutePlan.setSupportWebRoute(true);
			BaiduMapRoutePlan.openBaiduMapTransitRoute(para, mActivity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mLocationClient.stop();
	}
}
