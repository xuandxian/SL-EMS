package com.overtech.ems.activity.parttime.personal.others;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;

public class PersonalAboutAppActivity extends BaseActivity implements
		OnClickListener {
	private Toolbar toolbar;
	private ActionBar actionBar;
	private AppCompatTextView tvTitle;
	private LinearLayout llAboutUs;
	private LinearLayout llShareFriends;

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_personal_about_app;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		findViewById();
		init();
	}

	private void init() {
		tvTitle.setText("关于APP");
		toolbar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		llAboutUs.setOnClickListener(this);
		llShareFriends.setOnClickListener(this);
	}

	private void findViewById() {
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		tvTitle = (AppCompatTextView) findViewById(R.id.tvTitle);
		llAboutUs = (LinearLayout) findViewById(R.id.ll_about_us);
		llShareFriends = (LinearLayout) findViewById(R.id.lL_share_friends);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.ll_about_us:
			intent.setClass(this, PersonalAboutCompanyActivity.class);
			startActivity(intent);
			break;
		case R.id.lL_share_friends:
			showShare();
			break;
		}
	}

	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		oks.setTitle("上海乐配信息科技有限公司");
		// 暂时使用云端服务器上面的logo,豌豆荚审核通过后使用豌豆荚中的logo
		// oks.setImageUrl("http://120.55.162.181:8080/test/icon.png");//图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段
		// text是分享文本，所有平台都需要这个字段
		oks.setText("一款基于手机移动端的电梯维保App，欢迎下载。");
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://www.wandoujia.com/apps/com.overtech.ems");
		// 启动分享GUI
		oks.show(this);
	}

}
