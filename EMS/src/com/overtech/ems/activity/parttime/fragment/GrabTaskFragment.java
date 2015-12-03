package com.overtech.ems.activity.parttime.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import com.overtech.ems.R;
import com.overtech.ems.activity.adapter.GrabTaskAdapter;
import com.overtech.ems.activity.parttime.common.PackageDetailActivity;
import com.overtech.ems.activity.parttime.grabtask.GrabTaskDoFilterActivity;
import com.overtech.ems.entity.test.Data5;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView.IXListViewListener;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView.OnMenuItemClickListener;
import com.overtech.ems.widget.swiperefreshlistview.pulltorefresh.RefreshTime;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenu;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenuCreator;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenuItem;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class GrabTaskFragment extends Fragment implements IXListViewListener {

	private PullToRefreshSwipeMenuListView mSwipeListView;
	private SwipeMenuCreator creator;
	private Activity mActivity;
	private ImageView mPartTimeDoFifter;
	private NiftyDialogBuilder dialogBuilder;
	private Effectstype effect;
	private CustomProgressDialog progressDialog;
	private GrabTaskAdapter mAdapter;
	private ArrayList<Data5> list;
	private Handler mHandler;
	private TextView mHeadTitle;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_grab_task, container,
				false);
		findViewById(view);
		getData();
		init();
		return view;
	}

	private void findViewById(View view) {
		mSwipeListView = (PullToRefreshSwipeMenuListView) view
				.findViewById(R.id.sl_qiandan_listview);
		mHeadTitle=(TextView)view.findViewById(R.id.tv_headTitle);
	}

	private void getData() {
		list = new ArrayList<Data5>();
		Data5 data0 = new Data5("0", "徐家汇景园0", "5", "1", "徐汇区广元西路", "13.5km",
				"2015/10/10");
		Data5 data1 = new Data5("1", "徐家汇景园1", "5", "1", "徐汇区广元西路", "13.5km",
				"2015/10/10");
		Data5 data2 = new Data5("0", "徐家汇景园2", "5", "0", "徐汇区广元西路", "13.5km",
				"2015/10/10");
		Data5 data3 = new Data5("1", "虹桥小区0", "5", "0", "徐汇区广元西路", "13.5km",
				"2015/10/10");
		Data5 data4 = new Data5("0", "虹桥小区1", "5", "1", "徐汇区广元西路", "13.5km",
				"2015/10/10");
		Data5 data5 = new Data5("0", "虹桥小区2", "5", "1", "徐汇区广元西路", "13.5km",
				"2015/10/10");
		Data5 data6 = new Data5("0", "虹桥小区3", "5", "0", "徐汇区广元西路", "13.5km",
				"2015/10/10");
		Data5 data7 = new Data5("1", "丰业广元公寓0", "5", "0", "徐汇区广元西路", "13.5km",
				"2015/10/10");
		Data5 data8 = new Data5("0", "丰业广元公寓1", "5", "0", "徐汇区广元西路", "13.5km",
				"2015/10/10");
		Data5 data9 = new Data5("0", "丰业广元公寓2", "5", "0", "徐汇区广元西路", "13.5km",
				"2015/10/10");
		Data5 data10 = new Data5("1", "丰业广元公寓3", "5", "1", "徐汇区广元西路", "13.5km",
				"2015/10/10");
		Data5 data11 = new Data5("0", "乐山公寓0", "5", "0", "徐汇区广元西路", "13.5km",
				"2015/10/10");
		Data5 data12 = new Data5("0", "乐山公寓1", "5", "1", "徐汇区广元西路", "13.5km",
				"2015/10/10");
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
		list.add(data10);
		list.add(data11);
		list.add(data12);
	}

	private void init() {
		dialogBuilder = NiftyDialogBuilder.getInstance(mActivity);
		progressDialog = CustomProgressDialog.createDialog(mActivity);
		mHeadTitle.setText("抢单");
		initListView();
		mSwipeListView.setMenuCreator(creator);
		mAdapter = new GrabTaskAdapter(list, mActivity);
		mSwipeListView.setAdapter(mAdapter);
		mSwipeListView.setPullRefreshEnable(true);
		mSwipeListView.setPullLoadEnable(true);
		mSwipeListView.setXListViewListener(this);
		View mHeadView=LayoutInflater.from(mActivity).inflate(
				R.layout.listview_header_filter, null);
		mSwipeListView.addHeaderView(mHeadView);
		mPartTimeDoFifter=(ImageView) mHeadView.findViewById(R.id.iv_parttime_do_fifter);
		mHandler = new Handler();
		mSwipeListView
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,
							int index) {
						showDialog();
					}
				});
		mSwipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Data5 data = (Data5) parent.getItemAtPosition(position);
				Intent intent = new Intent(mActivity,
						PackageDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("CommunityName", data.getName());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mPartTimeDoFifter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mActivity,
						GrabTaskDoFilterActivity.class);
				startActivity(intent);

			}
		});
	}

	private void initListView() {
		creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem openItem = new SwipeMenuItem(mActivity);
				openItem.setBackground(new ColorDrawable(Color.rgb(0xFF, 0x3A,0x30)));
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
		dialogBuilder.withTitle("温馨提示").withTitleColor("#FFFFFF")
				.withDividerColor("#11000000").withMessage("您是否要接此单？")
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

	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm",
						Locale.getDefault());
				RefreshTime.setRefreshTime(mActivity, df.format(new Date()));
				onLoad();
			}
		}, 2000);
	}

	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				onLoad();
			}
		}, 2000);
	}

	private void onLoad() {
		mSwipeListView.setRefreshTime(RefreshTime.getRefreshTime(mActivity));
		mSwipeListView.stopRefresh();
		mSwipeListView.stopLoadMore();
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
