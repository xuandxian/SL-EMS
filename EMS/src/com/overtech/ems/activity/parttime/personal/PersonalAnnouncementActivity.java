package com.overtech.ems.activity.parttime.personal;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.PersonalAnnounceAdapter;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.bean.AnnouncementBean;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.entity.parttime.Announcement;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
/**
 * 公告栏
 * 
 */
public class PersonalAnnouncementActivity extends BaseActivity {
	private ImageView mDoBack;
	private	TextView mHeadContent;
	private ListView mAnnouncement;
	private PersonalAnnounceAdapter adapter;
	private List<Announcement> list;
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.ANNOUNCEMENT_SUCCESS:
				String json= (String) msg.obj;
				Gson gson=new Gson();
				AnnouncementBean bean=gson.fromJson(json, AnnouncementBean.class);
				list=bean.getModel();
				if(null==list||list.size()==0){
					Utilities.showToast("无数据", activity);
				}else{
					adapter=new PersonalAnnounceAdapter(context, list);
					mAnnouncement.setAdapter(adapter);
				}
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast((String)msg.obj, context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast((String)msg.obj, context);
				break;

			default:
				break;
			}
			stopProgressDialog();
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_announcement);
		initView();
		initData();
	}


	private void initView() {
		mHeadContent=(TextView) findViewById(R.id.tv_headTitle);
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
		mAnnouncement=(ListView)findViewById(R.id.lv_announcement);
		mHeadContent.setText("公告");
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mAnnouncement.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Announcement data=(Announcement) parent.getAdapter().getItem(position);
				Bundle bundle=new Bundle();
				bundle.putString(Constant.ANNOUNCEMENTID, data.getId());
				Intent intent=new Intent(activity,PersonalAnnouncementDetailActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}
	
	private void initData() {
		startProgressDialog("正在加载中...");
		Request request = httpEngine.createRequest(ServicesConfig.PERSONAL_ANNOUNCEMENT);
		Call call=httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			
			@Override
			public void onResponse(Response response) throws IOException {
				Message msg=new Message();
				if(response.isSuccessful()){
					msg.what=StatusCode.ANNOUNCEMENT_SUCCESS;
					msg.obj=response.body().string();
				}else{
					msg.what=StatusCode.RESPONSE_SERVER_EXCEPTION;
					msg.obj="服务器异常";
				}
				handler.sendMessage(msg);
			}
			
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				Message msg=new Message();
				msg.what=StatusCode.RESPONSE_NET_FAILED;
				msg.obj="网络异常";
				handler.sendMessage(msg);
			}
		});
	}
}
