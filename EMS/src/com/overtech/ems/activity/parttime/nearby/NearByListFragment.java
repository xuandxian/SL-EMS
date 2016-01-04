package com.overtech.ems.activity.parttime.nearby;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import com.baidu.mapapi.model.LatLng;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.adapter.GrabTaskAdapter;
import com.overtech.ems.activity.parttime.common.PackageDetailActivity;
import com.overtech.ems.entity.parttime.TaskPackage;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView.IXListViewListener;
import com.overtech.ems.widget.swiperefreshlistview.PullToRefreshSwipeMenuListView.OnMenuItemClickListener;
import com.overtech.ems.widget.swiperefreshlistview.pulltorefresh.RefreshTime;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenu;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenuCreator;
import com.overtech.ems.widget.swiperefreshlistview.swipemenu.SwipeMenuItem;
import android.app.Activity;
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

@SuppressWarnings("unchecked")
public class NearByListFragment extends BaseFragment implements IXListViewListener {

	private PullToRefreshSwipeMenuListView mNearBySwipeListView;
	private SwipeMenuCreator creator;
	private Activity mActivity;
	private Effectstype effect;
	private Handler mHandler;
	private ArrayList<TaskPackage> list=new ArrayList<TaskPackage>();
	private LatLng myLocation;
	private GrabTaskAdapter mAdapter;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_nearby_list, container,false);
		getExtralData();
		initListView(view);
		return view;
	}
	
	private void getExtralData() {
		Bundle bundle=getArguments();
		if (null==bundle) {
			return;
		}
		list=(ArrayList<TaskPackage>) getArguments().getSerializable("taskPackage");
		myLocation=new LatLng(bundle.getDouble("latitude"), bundle.getDouble("longitude"));
	}

	private void initListView(View view) {
		mNearBySwipeListView = (PullToRefreshSwipeMenuListView) view.findViewById(R.id.sl_nearby_listview);
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
		mNearBySwipeListView.setMenuCreator(creator);
		mAdapter = new GrabTaskAdapter(list,myLocation,mActivity);
		mNearBySwipeListView.setAdapter(mAdapter);
		mNearBySwipeListView.setPullRefreshEnable(true);
		mNearBySwipeListView.setPullLoadEnable(true);
		mNearBySwipeListView.setXListViewListener(this);
		mHandler = new Handler();
		mNearBySwipeListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,int index) {
						showDialog();
					}
				});
		mNearBySwipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				TaskPackage data=(TaskPackage) parent.getItemAtPosition(position);
				Intent intent=new Intent(mActivity, PackageDetailActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("CommunityName", data.getTaskPackageName());
				bundle.putString("TaskNo", data.getTaskNo());
				bundle.putString("Longitude", data.getLongitude());
				bundle.putString("Latitude", data.getLatitude());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}
	
//	public void reflushAdapter(ArrayList<TaskPackage> data){
//		Log.e("NearByListFragment", "reflushAdapter");
//		if (!list.isEmpty()) {
//			list.clear();
//		}
//		list.addAll(data);
//		mAdapter.notifyDataSetChanged();
//	}
	
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
						startProgressDialog("正在抢单...");
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								stopProgressDialog();
							}
						}, 3000);
					}
				}).show();
	}
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm",Locale.getDefault());
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
		mNearBySwipeListView.setRefreshTime(RefreshTime.getRefreshTime(mActivity));
		mNearBySwipeListView.stopRefresh();
		mNearBySwipeListView.stopLoadMore();
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
