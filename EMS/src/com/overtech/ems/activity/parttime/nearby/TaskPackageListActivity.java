package com.overtech.ems.activity.parttime.nearby;

import java.util.ArrayList;
import com.overtech.ems.R;
import com.overtech.ems.activity.adapter.GrabTaskAdapter;
import com.overtech.ems.activity.parttime.common.PackageDetailActivity;
import com.overtech.ems.entity.test.Data5;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView.OnMenuItemClickListener;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenu;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenuCreator;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenuItem;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Tony
 * @description 点击地图Marker（小区名称），显示该小区具体任务包
 * @date 2015-10-27
 */
public class TaskPackageListActivity extends Activity {
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private PullToRefreshSwipeMenuListView mTaskPackageList;
	private ArrayList<Data5> list;
	private Context context;
	private GrabTaskAdapter adapter;
	private String mCommunity;
	private SwipeMenuCreator creator;
	private NiftyDialogBuilder dialogBuilder;
	private Effectstype effect;
	private CustomProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		Bundle bundle = this.getIntent().getExtras();
		mCommunity = bundle.getString("name");
		setContentView(R.layout.activity_task_package_list);
		findViewById();
		getData(mCommunity);
		initListView();
		init();
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mTaskPackageList = (PullToRefreshSwipeMenuListView) findViewById(R.id.lv_task_package_list);
	}

	private void getData(String mCommunity) {
		list = new ArrayList<Data5>();
		Data5 data0 = new Data5("0", mCommunity + "0", "5", "1", "徐汇区广元西路",
				"13.5km", "2015/10/10");
		Data5 data1 = new Data5("0", mCommunity + "1", "5", "1", "徐汇区广元西路",
				"13.5km", "2015/10/10");
		Data5 data2 = new Data5("0", mCommunity + "2", "5", "0", "徐汇区广元西路",
				"13.5km", "2015/10/10");
		Data5 data3 = new Data5("0", mCommunity + "3", "5", "0", "徐汇区广元西路",
				"13.5km", "2015/10/10");
		Data5 data4 = new Data5("0", mCommunity + "4", "5", "1", "徐汇区广元西路",
				"13.5km", "2015/10/10");
		Data5 data5 = new Data5("0", mCommunity + "5", "5", "1", "徐汇区广元西路",
				"13.5km", "2015/10/10");
		Data5 data6 = new Data5("0", mCommunity + "6", "5", "1", "徐汇区广元西路",
				"13.5km", "2015/10/10");
		Data5 data7 = new Data5("0", mCommunity + "7", "5", "0", "徐汇区广元西路",
				"13.5km", "2015/10/10");
		Data5 data8 = new Data5("0", mCommunity + "8", "5", "0", "徐汇区广元西路",
				"13.5km", "2015/10/10");
		Data5 data9 = new Data5("0", mCommunity + "9", "5", "1", "徐汇区广元西路",
				"13.5km", "2015/10/10");
		list.add(data0);
		list.add(data1);
		list.add(data2);
		list.add(data3);
		list.add(data4);
		list.add(data5);
		list.add(data6);
		list.add(data7);
		list.add(data8);
		list.add(data9);
	}

	private void init() {
		context = TaskPackageListActivity.this;
		dialogBuilder = NiftyDialogBuilder.getInstance(context);
		progressDialog = CustomProgressDialog.createDialog(context);
		mHeadContent.setText(mCommunity);
		mHeadBack.setVisibility(View.VISIBLE);
		mTaskPackageList.setMenuCreator(creator);
//		adapter = new GrabTaskAdapter(list, context);
		mTaskPackageList.setAdapter(adapter);
		mTaskPackageList
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,
							int index) {
						showDialog();
					}
				});
		mTaskPackageList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Data5 data=(Data5) parent.getItemAtPosition(position);
				Intent intent = new Intent(TaskPackageListActivity.this,
						PackageDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("CommunityName",data.getName());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mHeadBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private void initListView() {
		creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem openItem = new SwipeMenuItem(context);
				openItem.setBackground(new ColorDrawable(Color.rgb(0x00, 0xff,
						0x00)));
				openItem.setWidth(dp2px(90));
				openItem.setTitle("抢");
				openItem.setTitleSize(18);
				openItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(openItem);
			}
		};
	}

	private void showDialog() {
		effect = Effectstype.Slideright;
		dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
		.withDividerColor(R.color.divider_color).withMessage("您是否要接此单？")
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
						progressDialog.setMessage("正在抢单...");
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

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
