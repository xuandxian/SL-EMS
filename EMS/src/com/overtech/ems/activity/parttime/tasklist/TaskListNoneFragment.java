package com.overtech.ems.activity.parttime.tasklist;

import java.util.ArrayList;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.overtech.ems.R;
import com.overtech.ems.activity.adapter.TaskListAdapter;
import com.overtech.ems.entity.test.Data4;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView.OnMenuItemClickListener;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenu;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenuCreator;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenuItem;

public class TaskListNoneFragment extends Fragment {
	private PullToRefreshSwipeMenuListView mSwipeListView;
	private SwipeMenuCreator creator;
	private Activity mActivity;
	private LocationClient mLocClient;
	private LatLng mLocation;
	private NiftyDialogBuilder dialogBuilder;
	private Effectstype effect;
	private ArrayList<Data4> list;
	private CustomProgressDialog progressDialog;

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
		getData();
		init();
		return view;

	}

	private void findViewById(View view) {
		mSwipeListView = (PullToRefreshSwipeMenuListView) view
				.findViewById(R.id.sl_task_list_listview);
	}

	private void getData() {
		Data4 data = new Data4("南虹小区", "5", "徐汇区广元西路", "13.5km", "2015-10-10");
		Data4 data1 = new Data4("徐家汇景园", "5", "徐汇区广元西路", "13.5km", "2015-10-10");
		Data4 data2 = new Data4("丰业广元公寓", "5", "徐汇区广元西路", "13.5km",
				"2015-10-10");
		Data4 data3 = new Data4("虹桥小区", "5", "徐汇区广元西路", "13.5km", "2015-10-10");
		Data4 data4 = new Data4("徐家汇景园", "5", "徐汇区广元西路", "13.5km", "2015-10-10");
		Data4 data5 = new Data4("虹桥小区", "5", "徐汇区广元西路", "13.5km", "2015-10-10");
		Data4 data6 = new Data4("虹桥小区", "5", "徐汇区广元西路", "13.5km", "2015-10-10");
		Data4 data7 = new Data4("丰业广元公寓", "5", "徐汇区广元西路", "13.5km",
				"2015-10-10");
		Data4 data8 = new Data4("丰业广元公寓", "5", "徐汇区广元西路", "13.5km",
				"2015-10-10");
		Data4 data9 = new Data4("南虹小区0", "5", "徐汇区广元西路", "13.5km", "2015-10-10");
		Data4 data10 = new Data4("南虹小区1", "5", "徐汇区广元西路", "13.5km",
				"2015-10-10");
		Data4 data11 = new Data4("南虹小区2", "5", "徐汇区广元西路", "13.5km",
				"2015-10-10");
		Data4 data12 = new Data4("虹桥小区", "5", "徐汇区广元西路", "13.5km", "2015-10-10");
		Data4 data13 = new Data4("南虹小区4", "5", "徐汇区广元西路", "13.5km",
				"2015-10-10");
		Data4 data14 = new Data4("虹桥小区", "5", "徐汇区广元西路", "13.5km", "2015-10-10");
		Data4 data15 = new Data4("丰业广元公寓", "5", "徐汇区广元西路", "13.5km",
				"2015-10-10");
		Data4 data16 = new Data4("南虹小区7", "5", "徐汇区广元西路", "13.5km",
				"2015-10-10");
		Data4 data17 = new Data4("丰业广元公寓", "5", "徐汇区广元西路", "13.5km",
				"2015-10-10");
		list = new ArrayList<Data4>();
		list.add(data);
		list.add(data1);
		list.add(data2);
		list.add(data3);
		list.add(data4);
		list.add(data5);
		list.add(data6);
		list.add(data7);
		list.add(data8);
		list.add(data9);
		list.add(data10);
		list.add(data11);
		list.add(data12);
		list.add(data13);
		list.add(data14);
		list.add(data15);
		list.add(data16);
		list.add(data17);
	}

	private void initBaiduMapLocation() {
		// 实例化定位服务，LocationClient类必须在主线程中声明
		mLocClient = new LocationClient(mActivity);
		mLocClient.registerLocationListener(new BDLocationListenerImpl());// 注册定位监听接口
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPRS
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
		mLocClient.setLocOption(option); // 设置定位参数
		mLocClient.start();
	}

	private void init() {
		dialogBuilder = NiftyDialogBuilder.getInstance(mActivity);
		progressDialog=CustomProgressDialog.createDialog(mActivity);
		initListView();
		mSwipeListView.setMenuCreator(creator);
		TaskListAdapter mAdapter = new TaskListAdapter(list, mActivity);
		mSwipeListView.setAdapter(mAdapter);
		mSwipeListView
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,
							int index) {
						switch (index) {
						case 0:
							initBaiduMapLocation();
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
	}

	protected void showDialog() {
		effect = Effectstype.Slideright;
		dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
		.withDividerColor(R.color.divider_color).withMessage("您是否要退掉此单？")
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

	public class BDLocationListenerImpl implements BDLocationListener {

		/**
		 * 接收异步返回的定位结果，参数是BDLocation类型参数
		 */
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			double mLatitude = location.getLatitude();
			double mLongitude = location.getLongitude();
			mLocation = new LatLng(mLatitude, mLongitude);
			startNavicate();
		}
	}

	public void startNavicate() {
		// 构建 route搜索参数
		RouteParaOption para = new RouteParaOption().startName("我的位置")
				.startPoint(mLocation)// 路线检索起点
				.endName("东方明珠")// 路线检索终点名称
				.cityName("上海")// 城市名称
				.busStrategyType(EBusStrategyType.bus_recommend_way);
		try {
			BaiduMapRoutePlan.openBaiduMapTransitRoute(para, mActivity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
