package com.overtech.ems.activity.parttime.tasklist;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.media.effect.Effect;
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
	private Context context;
	private NiftyDialogBuilder dialogBuilder;
	private Effectstype effect;
	private CustomProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tasklist_package_detail);
		initView();
		initData();
		init();
	}

	private void init() {
		context=TaskListPackageDetailActivity.this;
		dialogBuilder=NiftyDialogBuilder.getInstance(context);
		progressDialog=CustomProgressDialog.createDialog(context);
		TaskListPackageDetailAdapter adapter=new TaskListPackageDetailAdapter(context, list);
		mTask.setAdapter(adapter);
		mDoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTask.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent=new Intent(context,TaskListPackageTaskDetailActivity.class);
				startActivity(intent);
			}
			
		});
		mCancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				effect=Effectstype.Slideright;
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
	}


	private void initData() {
		list=new ArrayList<Data2>();
		Data2 data1=new Data2("31号楼1号电梯(全包)","上海三菱","80032984590","20层/20站");
		Data2 data2=new Data2("31号楼2号电梯(半包)","上海三菱","80032984591","20层/20站");
		Data2 data3=new Data2("31号楼2号电梯(半包)","上海三菱","80032984592","20层/20站");
		Data2 data4=new Data2("31号楼3号电梯(半包)","上海三菱","80032984593","20层/20站");
		Data2 data5=new Data2("31号楼3号电梯(清包)","上海三菱","80032984594","20层/20站");
		list.add(data1);
		list.add(data2);
		list.add(data3);
		list.add(data4);
		list.add(data5);
	}

	private void initView() {
		mDoBack=(ImageView) findViewById(R.id.iv_grab_headBack);
		mTask=(ListView) findViewById(R.id.lv_tasklist);
		mCancle=(Button) findViewById(R.id.bt_cancle_task);
	}
}
