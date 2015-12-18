package com.overtech.ems.activity.parttime.fragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.overtech.ems.R;
import com.overtech.ems.activity.adapter.GrabTaskAdapter;
import com.overtech.ems.activity.parttime.common.PackageDetailActivity;
import com.overtech.ems.activity.parttime.grabtask.GrabTaskDoFilterActivity;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.TaskPackage;
import com.overtech.ems.entity.parttime.TaskPackageBean;
import com.overtech.ems.entity.test.Data5;
import com.overtech.ems.http.OkHttpClientManager;
import com.overtech.ems.utils.Utilities;
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
import com.squareup.okhttp.Response;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	private ArrayList<TaskPackage> list;
	private Handler mHandler;
	private TextView mHeadTitle;
	private OkHttpClientManager httpManager;
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			String json=(String) msg.obj;
			Gson gson=new Gson();
			
			TaskPackageBean tasks=gson.fromJson(json, TaskPackageBean.class);
			list=(ArrayList<TaskPackage>) tasks.getModel();
			mAdapter = new GrabTaskAdapter(list, mActivity);
			mSwipeListView.setAdapter(mAdapter);
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
		View view = inflater.inflate(R.layout.fragment_grab_task, container,
				false);
		findViewById(view);
		getData();
		init();
		return view;
	}
	
	private void findViewById(View view) {
		httpManager=OkHttpClientManager.getInstance();
		mSwipeListView = (PullToRefreshSwipeMenuListView) view
				.findViewById(R.id.sl_qiandan_listview);
		mHeadTitle=(TextView)view.findViewById(R.id.tv_headTitle);
	}

	private void getData() {

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Response response=httpManager.post(ServicesConfig.GRABTASK, null);
					Message msg=new Message();
					msg.obj=response.body().string();
					handler.sendMessage(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void init() {
		dialogBuilder = NiftyDialogBuilder.getInstance(mActivity);
		progressDialog = CustomProgressDialog.createDialog(mActivity);
		mHeadTitle.setText("抢单");
		initListView();
		mSwipeListView.setMenuCreator(creator);
		
		mSwipeListView.setPullRefreshEnable(true);
		mSwipeListView.setPullLoadEnable(true);
		mSwipeListView.setXListViewListener(this);
		View mHeadView=LayoutInflater.from(mActivity).inflate(R.layout.listview_header_filter, null);
		mSwipeListView.addHeaderView(mHeadView);
		mPartTimeDoFifter=(ImageView) mHeadView.findViewById(R.id.iv_parttime_do_fifter);
		mHandler = new Handler();
		mSwipeListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {

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
				TaskPackage data = (TaskPackage) parent.getItemAtPosition(position);
				Intent intent = new Intent(mActivity,
						PackageDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("CommunityName", data.getProjectName());
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

	public void onRefresh() {
		Utilities.showToast("下拉刷新",mActivity);
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
		Utilities.showToast("上拉加载",mActivity);
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

	private void showDialog() {
		effect = Effectstype.Slideright;
		dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
				.withDividerColor("#11000000").withMessage("您是否要接此单？")
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
