package com.overtech.ems.activity.parttime;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.parttime.fragment.GrabTaskFragment;
import com.overtech.ems.activity.parttime.fragment.NearByFragment;
import com.overtech.ems.activity.parttime.fragment.TaskListFragment;
import com.overtech.ems.activity.parttime.fragment.PersonalZoneFragment;
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
	private GrabTaskFragment mGrabTaskFragment;
	private TaskListFragment mTaskListFragment;
	private PersonalZoneFragment mPersonalZoneFragment;
	private NearByFragment mNearByFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		statckInstance.pushActivity(this);
		setContentView(R.layout.activity_main);
		findViewById();
		setDefaultView();
		initEvents();
	}

	protected void findViewById() {
		mHomeRadioGroup = (RadioGroup) findViewById(R.id.mHomeRadioGroup);
		mHomeHomeRb = (RadioButton) findViewById(R.id.mHomeHomeRb);
		mHomeFindRb = (RadioButton) findViewById(R.id.mHomeFindRb);
		mHomeSearchRb = (RadioButton) findViewById(R.id.mHomeSearchRb);
		mHomeProfileRb = (RadioButton) findViewById(R.id.mHomeProfileRb);
	}

	private void setDefaultView() {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		mGrabTaskFragment = new GrabTaskFragment();
		transaction.replace(R.id.mHomeContent, mGrabTaskFragment);
		transaction.commit();
	}

	private void initEvents() {
		mHomeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.mHomeHomeRb:
						mGrabTaskFragment = new GrabTaskFragment();
						switchFragment(mGrabTaskFragment);
						break;
					case R.id.mHomeFindRb:
						mNearByFragment = new NearByFragment();
						switchFragment(mNearByFragment);
						break;
					case R.id.mHomeSearchRb:
						mTaskListFragment = new TaskListFragment();
						switchFragment(mTaskListFragment);
						break;
					case R.id.mHomeProfileRb:
						mPersonalZoneFragment = new PersonalZoneFragment();
						switchFragment(mPersonalZoneFragment);
						break;
				}
			}
		});
	}
	
	public void switchFragment(Fragment mFragment) {
	    if (mFragment == null){
	    	return;
	    }
	    if (fragmentManager == null){
			fragmentManager=getFragmentManager();
	    }
	    FragmentTransaction transaction = fragmentManager.beginTransaction();
	    if (!mFragment.isAdded()) {
	        transaction.replace(R.id.mHomeContent, mFragment);
	        transaction.addToBackStack(null);
	        transaction.commit();
	      } else {
	        transaction.show(mFragment);
	      }
	  }
	private long exitTime=0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK&& event.getAction() == KeyEvent.ACTION_DOWN) {
			Effectstype effect = Effectstype.Shake;
			dialogBuilder.withTitle("温馨提示").withTitleColor(R.color.main_primary)
			.withDividerColor("#11000000").withMessage("您是否要退出？")
			.withMessageColor(R.color.main_primary).withDialogColor("#FFFFFFFF")
			.isCancelableOnTouchOutside(true).withDuration(700)
			.withEffect(effect).withButtonDrawable(R.color.main_white)
			.withButton1Text("否").withButton1Color("#DD47BEE9")
			.withButton2Text("是").withButton2Color("#DD47BEE9")
			.setButton1Click(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialogBuilder.dismiss();
						}
					}).setButton2Click(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							statckInstance.popAllActivitys();
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
