package com.overtech.ems.activity.parttime.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;

public class PersonalAboutAppActivity extends BaseActivity implements OnClickListener {
	private TextView mHead;
	private ImageView mDoBack;
	private RelativeLayout rl_company_info;
	private RelativeLayout rl_app_developers;
	private RelativeLayout rl_share;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_about_app);
		findViewById();
		init();
	}

	private void init() {
		mHead.setText("关于APP");
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		rl_company_info.setOnClickListener(this);
		rl_app_developers.setOnClickListener(this);
		rl_share.setOnClickListener(this);
	}

	private void findViewById() {
		mHead=(TextView) findViewById(R.id.tv_headTitle);
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
		rl_company_info=(RelativeLayout) findViewById(R.id.rl_company_info);
		rl_app_developers=(RelativeLayout) findViewById(R.id.rl_app_developers);
		rl_share=(RelativeLayout) findViewById(R.id.rl_share);
	}

	@Override
	public void onClick(View v) {
		Intent intent =new Intent();
		switch (v.getId()) {
		case R.id.rl_company_info:
			intent.setClass(this, PersonalAboutCompanyActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_app_developers:
			intent.setClass(this, PersonalAboutDeveloperActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_share:
			showShare();
			break;
		}
	}
	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
//		oks.disableSSOWhenAuthorize();
		oks.setTitle("上海润利电梯配件技术开发有限公司");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://www.wandoujia.com/apps/com.overtech.ems");
		oks.setImageUrl("http://img.wdjimg.com/mms/icon/v1/c/66/b6e84a3e811e6e84f64496d7d438866c_256_256.png");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("一款基于手机移动端的电梯维保App，欢迎下载。");
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://www.wandoujia.com/apps/com.overtech.ems");
		oks.setVenueName("24梯");
		// 启动分享GUI
		oks.show(this);
	}
}
