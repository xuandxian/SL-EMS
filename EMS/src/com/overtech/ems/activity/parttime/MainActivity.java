package com.overtech.ems.activity.parttime;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.parttime.fragment.GrabTaskFragment;
import com.overtech.ems.activity.parttime.fragment.NearByFragment;
import com.overtech.ems.activity.parttime.fragment.TaskListFragment;
import com.overtech.ems.activity.parttime.fragment.PersonalZoneFragment;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.dialogeffects.Effectstype;

import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * @author Tony
 * @description 主界面
 * @date 2015-10-05
 */
public class MainActivity extends BaseActivity {

	private RadioGroup mHomeRadioGroup;
	private RadioButton mHomeHomeRb;
	private RadioButton mHomeFindRb;
	private RadioButton mHomeSearchRb;
	private RadioButton mHomeProfileRb;
	private Fragment mGrabTaskFragment;
	private Fragment mTaskListFragment;
	private Fragment mPersonalZoneFragment;
	private Fragment mNearByFragment;
	private FragmentTransaction transaction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		findViewById();
		setDefaultView();
		initEvents();
	}

	protected void findViewById() {
		mHomeRadioGroup = (RadioGroup) findViewById(R.id.mHomeRadioGroup);
		mHomeHomeRb = (RadioButton) findViewById(R.id.mHomeHomeRb);
//		Drawable[] drawable=mHomeHomeRb.getCompoundDrawables();
//		if (null!=drawable[1]) {
//			Utilities.showToast("111", context);
//			drawable[1].setBounds(0,5,0,5);
//		}
		mHomeFindRb = (RadioButton) findViewById(R.id.mHomeFindRb);
		mHomeSearchRb = (RadioButton) findViewById(R.id.mHomeSearchRb);
		mHomeProfileRb = (RadioButton) findViewById(R.id.mHomeProfileRb);
	}

	private void setDefaultView() {
		transaction = fragmentManager.beginTransaction();
		mGrabTaskFragment = new GrabTaskFragment();
		transaction.replace(R.id.mHomeContent, mGrabTaskFragment);
		transaction.commit();
	}

	private void initEvents() {
		mHomeRadioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						FragmentTransaction transaction = fragmentManager
								.beginTransaction();
						switch (checkedId) {
						case R.id.mHomeHomeRb:
							mGrabTaskFragment = new GrabTaskFragment();
							transaction.replace(R.id.mHomeContent,
									mGrabTaskFragment);
							transaction.commit();
							break;
						case R.id.mHomeFindRb:
							mNearByFragment = new NearByFragment();
							transaction = fragmentManager.beginTransaction();
							transaction.replace(R.id.mHomeContent,
									mNearByFragment);
							transaction.commit();
							break;
						case R.id.mHomeSearchRb:
							mTaskListFragment = new TaskListFragment();
							transaction = fragmentManager.beginTransaction();
							transaction.replace(R.id.mHomeContent,
									mTaskListFragment);
							transaction.commit();
							break;
						case R.id.mHomeProfileRb:
							mPersonalZoneFragment = new PersonalZoneFragment();
							transaction = fragmentManager.beginTransaction();
							transaction.replace(R.id.mHomeContent,
									mPersonalZoneFragment);
							transaction.commit();
							break;
						}
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			Effectstype effect = Effectstype.Shake;
			dialogBuilder
					.withTitle("温馨提示")
					.withTitleColor("#FFFFFF")
					.withDividerColor("#11000000")
					.withMessage("您是否要退出?")
					.withMessageColor("#FFFFFFFF")
					.withDialogColor("#FF009BEE")
					.withIcon(getResources().getDrawable(R.drawable.icon_dialog))
					.isCancelableOnTouchOutside(true).withDuration(700)
					.withEffect(effect).withButtonDrawable(R.color.bg_title)
					.withButton1Text("确定").withButton1Color("#FFFFFFFF")
					.withButton2Text("取消").withButton2Color("#FFFFFFFF")
					.setButton1Click(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							finish();
							System.exit(0);
						}
					}).setButton2Click(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialogBuilder.dismiss();
						}
					}).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		hideSoftInput();
		return super.onTouchEvent(event);
	}
}
