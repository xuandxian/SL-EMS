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
		rl_share.setOnClickListener(this);
	}

	private void findViewById() {
		mHead=(TextView) findViewById(R.id.tv_headTitle);
		mDoBack=(ImageView) findViewById(R.id.iv_headBack);
		rl_company_info=(RelativeLayout) findViewById(R.id.rl_company_info);
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
		case R.id.rl_share:
			showShare();
			break;
		}
	}
	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
//		oks.disableSSOWhenAuthorize();
		oks.setTitle("上海乐配信息科技有限公司");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://www.wandoujia.com/apps/com.overtech.ems");
		//暂时使用云端服务器上面的logo,豌豆荚审核通过后使用豌豆荚中的logo
//		oks.setImageUrl("http://120.55.162.181:8080/test/icon.png");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("一款基于手机移动端的电梯维保App，欢迎下载。");
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://www.wandoujia.com/apps/com.overtech.ems");
		oks.setVenueName("24T");
		// 启动分享GUI
		oks.show(this);
	}
}
