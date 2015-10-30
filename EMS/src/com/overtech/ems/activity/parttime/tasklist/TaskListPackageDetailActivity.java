package com.overtech.ems.activity.parttime.tasklist;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.TaskListPackageDetailAdapter;
import com.overtech.ems.entity.test.Data2;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;

public class TaskListPackageDetailActivity extends BaseActivity {
	private ImageView mDoBack;
	private ListView mTask;
	private Button mCancle;
	private TaskListPackageDetailAdapter adapter;
	private ArrayList<Data2> list;
	private Context mActivity;
	private NiftyDialogBuilder dialogBuilder;
	private Effectstype effect;
	private CustomProgressDialog progressDialog;
	private LocationClient mLocClient;
	private LatLng mLocation;
	private TextView mDoNavicate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tasklist_package_detail);
		initView();
		initData();
		init();
	}

	private void init() {
		mActivity = TaskListPackageDetailActivity.this;
		dialogBuilder = NiftyDialogBuilder.getInstance(context);
		progressDialog = CustomProgressDialog.createDialog(context);
		adapter = new TaskListPackageDetailAdapter(context, list);
		mTask.setAdapter(adapter);
		mDoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTask.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent(context,
						TaskListPackageTaskDetailActivity.class);
				startActivity(intent);
			}

		});
		mCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				effect = Effectstype.Slideright;
				dialogBuilder
						.withTitle("温馨提示")
						.withTitleColor("#FFFFFF")
						.withDividerColor("#11000000")
						.withMessage("您是否要退掉此单？")
						.withMessageColor("#FF333333")
						.withDialogColor("#FFFFFFFF")
						.withIcon(
								getResources().getDrawable(
										R.drawable.icon_dialog))
						.isCancelableOnTouchOutside(true).withDuration(700)
						.withEffect(effect)
						.withButtonDrawable(R.color.main_white)
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
								progressDialog.setMessage("正在退单");
								progressDialog.show();
								new Handler().postDelayed(new Runnable() {

									@Override
									public void run() {
										finish();
									}
								}, 3000);
							}
						}).show();
			}
		});
		mDoNavicate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initBaiduMapLocation();
			}
		});
	}

	protected void initBaiduMapLocation() {
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

	private void initData() {
		list = new ArrayList<Data2>();
		Data2 data1 = new Data2("31号楼1号电梯(全包)", "上海三菱", "80032984590",
				"20层/20站");
		Data2 data2 = new Data2("31号楼2号电梯(半包)", "上海三菱", "80032984591",
				"20层/20站");
		Data2 data3 = new Data2("31号楼2号电梯(半包)", "上海三菱", "80032984592",
				"20层/20站");
		Data2 data4 = new Data2("31号楼3号电梯(半包)", "上海三菱", "80032984593",
				"20层/20站");
		Data2 data5 = new Data2("31号楼3号电梯(清包)", "上海三菱", "80032984594",
				"20层/20站");
		list.add(data1);
		list.add(data2);
		list.add(data3);
		list.add(data4);
		list.add(data5);
	}

	private void initView() {
		mDoBack = (ImageView) findViewById(R.id.iv_grab_headBack);
		mTask = (ListView) findViewById(R.id.lv_tasklist);
		mCancle = (Button) findViewById(R.id.bt_cancle_task);
		mDoNavicate = (TextView) findViewById(R.id.tv_navicate);
		mDoNavicate.setVisibility(View.VISIBLE);
	}
}
