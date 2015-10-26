package com.overtech.ems.activity.parttime.fragment;

import com.overtech.ems.R;
import com.overtech.ems.activity.adapter.GrabTaskAdapter;
import com.overtech.ems.activity.parttime.grabtask.GrabTaskDoFilterActivity;
import com.overtech.ems.activity.parttime.grabtask.PackageDetailActivity;
import com.overtech.ems.widget.CustomProgressDialog;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;
import com.overtech.ems.widget.swipemenu.SwipeMenu;
import com.overtech.ems.widget.swipemenu.SwipeMenuCreator;
import com.overtech.ems.widget.swipemenu.SwipeMenuItem;
import com.overtech.ems.widget.swipemenu.SwipeMenuListView;
import com.overtech.ems.widget.swipemenu.SwipeMenuListView.OnMenuItemClickListener;
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

public class GrabTaskFragment extends Fragment {

	private SwipeMenuListView mSwipeListView;
	private SwipeMenuCreator creator;
	private Activity mActivity;
	private ImageView mPartTimeDoFifter;
	private NiftyDialogBuilder dialogBuilder;
	private Effectstype effect;
	private CustomProgressDialog progressDialog;
	private GrabTaskAdapter mAdapter;

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
		init();
		return view;
	}

	private void findViewById(View view) {
		mSwipeListView = (SwipeMenuListView) view
				.findViewById(R.id.sl_qiandan_listview);
		mPartTimeDoFifter = (ImageView) view
				.findViewById(R.id.iv_parttime_do_fifter);
	}

	private void init() {
		dialogBuilder = NiftyDialogBuilder.getInstance(mActivity);
		progressDialog = CustomProgressDialog.createDialog(mActivity); 
		initListView();
		mSwipeListView.setMenuCreator(creator);
		mAdapter = new GrabTaskAdapter(mActivity);
		mSwipeListView.setAdapter(mAdapter);
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
				Intent intent = new Intent(mActivity,
						PackageDetailActivity.class);
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

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
