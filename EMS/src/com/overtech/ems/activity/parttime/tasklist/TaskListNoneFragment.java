package com.overtech.ems.activity.parttime.tasklist;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem.OnMenuItemClickListener;
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
import com.overtech.ems.activity.adapter.GrabTaskAdapter;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;
import com.overtech.ems.widget.swipemenu.SwipeMenu;
import com.overtech.ems.widget.swipemenu.SwipeMenuCreator;
import com.overtech.ems.widget.swipemenu.SwipeMenuItem;
import com.overtech.ems.widget.swipemenu.SwipeMenuListView;

public class TaskListNoneFragment extends Fragment {
	private SwipeMenuListView mSwipeListView;
	private SwipeMenuCreator creator;
	private Activity mActivity;
	private LocationClient mLocClient;
	private LatLng mLocation;
	private NiftyDialogBuilder dialogBuilder;
	private Effectstype effect;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_task_list_none, container, false);
		findViewById(view);
		init();
		return view;
		
	}

	private void findViewById(View view) {
		mSwipeListView = (SwipeMenuListView) view
				.findViewById(R.id.sl_task_list_listview);
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
		initListView();
		mSwipeListView.setMenuCreator(creator);
		GrabTaskAdapter mAdapter = new GrabTaskAdapter(mActivity);
		mSwipeListView.setAdapter(mAdapter);
		mSwipeListView
				.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
					
					@Override
					public void onMenuItemClick(int position, SwipeMenu menu, int index) {
						switch (index) {
						case 0:
							initBaiduMapLocation();
							break;
						case 1:
//							Utilities.showToast("退单", mActivity);
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
			}
		});
	}

	protected void showDialog() {
		effect = Effectstype.Slideright;
		dialogBuilder.withTitle("温馨提示").withTitleColor("#FFFFFF")
				.withDividerColor("#11000000").withMessage("您是否要退掉此单？")
				.withMessageColor("#FF333333").withDialogColor("#FFFFFFFF")
				.withIcon(getResources().getDrawable(R.drawable.icon_dialog))
				.isCancelableOnTouchOutside(true).withDuration(700)
				.withEffect(effect).withButtonDrawable(R.color.main_white)
				.withButton1Text("否").withButton1Color("#FF333333")
				.withButton2Text("是").withButton2Color("#FF333333")
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).show();
	}

	private void initListView() {
		creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem navicateItem = new SwipeMenuItem(mActivity);
				navicateItem.setBackground(new ColorDrawable(Color.rgb(0x00,
						0xff, 0x00)));
				navicateItem.setWidth(dp2px(90));
				navicateItem.setTitle("导航");
				navicateItem.setTitleSize(18);
				navicateItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(navicateItem);
				SwipeMenuItem deleteItem = new SwipeMenuItem(mActivity);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xff,
						0x00, 0x00)));
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
