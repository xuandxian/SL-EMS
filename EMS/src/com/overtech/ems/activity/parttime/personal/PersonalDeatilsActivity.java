package com.overtech.ems.activity.parttime.personal;

import com.overtech.ems.R;
import com.overtech.ems.activity.common.LoginActivity;
import com.overtech.ems.widget.dialogeffects.Effectstype;
import com.overtech.ems.widget.dialogeffects.NiftyDialogBuilder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PersonalDeatilsActivity extends Activity implements
		OnClickListener {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private RelativeLayout mChangePhoneNo;
	private Button mDoExit;
	private Effectstype effect;
	private NiftyDialogBuilder dialogBuilder;
	private TextView mPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_personal_details);
		initViews();
		initEvents();
	}

	private void initViews() {
		dialogBuilder = NiftyDialogBuilder.getInstance(this);
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mChangePhoneNo = (RelativeLayout) findViewById(R.id.rl_change_phoneNo);
		mDoExit = (Button) findViewById(R.id.btn_exit);
		mPhone=(TextView) findViewById(R.id.textView3);
		mHeadContent.setText("账号信息");
	}

	private void initEvents() {
		// 返回键
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(this);
		mChangePhoneNo.setOnClickListener(this);
		mDoExit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_headBack:
			this.finish();
			break;
		case R.id.rl_change_phoneNo:
//			Intent intent = new Intent(PersonalDeatilsActivity.this,
//					PersonalChangePhoneNoActivity.class);
			Intent intent = new Intent(PersonalDeatilsActivity.this,
					ChangePhoneNoVCActivity.class);
			String phone=mPhone.getText().toString();
			intent.putExtra("phone", phone);
			startActivity(intent);
			break;
		case R.id.btn_exit:
			showExitDialog();
			break;
		}
	}

	private void showExitDialog() {
		effect = Effectstype.SlideBottom;
		dialogBuilder.withTitle("温馨提示").withTitleColor("#FFFFFF")
				.withDividerColor("#11000000").withMessage("您是否要退出?")
				.withMessageColor("#FFFFFFFF").withDialogColor("#FF009BEE")
				.withIcon(getResources().getDrawable(R.drawable.icon_dialog))
				.isCancelableOnTouchOutside(true).withDuration(700)
				.withEffect(effect).withButton1Text("确定").withButton2Text("取消")
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(
								PersonalDeatilsActivity.this,
								LoginActivity.class);
						startActivity(intent);
						finish();
					}
				}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).show();
	}
}
