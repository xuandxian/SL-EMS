package com.overtech.ems.activity.parttime.personal;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.Utilities;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
/**
 * 公告详情
 * 
 */
public class PersonalAnnouncementDetailActivity extends BaseActivity {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private TextView mAnnouncementContent;
	private TextView mAnnouncementTheme;
	private TextView mAnnouncementDate;
	private TextView mAnnouncementSummary;
	
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StatusCode.ANNOUNCEMENT_SUCCESS:
				String json=(String) msg.obj;
				try {
					JSONObject jsonObj=new JSONObject(json);
					JSONObject model=(JSONObject) jsonObj.get("model");
					mAnnouncementContent.setText("\u3000\u3000"+model.getString("content"));
					mAnnouncementDate.setText(model.getString("releaseDate"));
					mAnnouncementSummary.setText("摘要："+model.getString("summary"));
					mAnnouncementTheme.setText(model.getString("theme"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		setContentView(R.layout.activity_personal_announcement_detail);
		initView();
		initData();
	}

	private void initData() {
		startProgressDialog("正在加载中...");
		String id=getIntent().getExtras().getString(Constant.ANNOUNCEMENTID);
		Param param=new Param(Constant.ANNOUNCEMENTID,id);
		Request request=httpEngine.createRequest(ServicesConfig.PERSONAL_ANNOUNCEMENT_DETAIL, param);
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
					msg.obj="服务器君歇菜了，请稍后";
				}
				handler.sendMessage(msg);
			}
			
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				Message msg=new Message();
				msg.what=StatusCode.RESPONSE_NET_FAILED;
				msg.obj="网络异常，请检查";
				handler.sendMessage(msg);
			}
		});
	}
	private void initView() {
		mHeadContent=(TextView) findViewById(R.id.tv_headTitle);
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
		mAnnouncementContent=(TextView) findViewById(R.id.tv_announcement_content);
		mAnnouncementTheme=(TextView) findViewById(R.id.tv_announcement_theme);
		mAnnouncementDate=(TextView) findViewById(R.id.tv_announcement_date);
		mAnnouncementSummary=(TextView) findViewById(R.id.tv_announcement_summary);
		mHeadContent.setText("详情");
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
}
