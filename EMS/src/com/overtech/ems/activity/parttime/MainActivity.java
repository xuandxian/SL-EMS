package com.overtech.ems.activity.parttime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.activity.fulltime.maintenance.MaintenanceFragment;
import com.overtech.ems.activity.parttime.common.PackageDetailActivity;
import com.overtech.ems.activity.parttime.fragment.GrabTaskFragment;
import com.overtech.ems.activity.parttime.fragment.NearByFragment;
import com.overtech.ems.activity.parttime.fragment.PersonalZoneFragment;
import com.overtech.ems.activity.parttime.fragment.TaskListFragment;
import com.overtech.ems.utils.FragmentUtils;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;

/**
 * @author Will
 * @description 主界面
 * @date 2016-06-14
 */
public class MainActivity extends BaseActivity {

	private RadioGroup mHomeRadioGroup;
	private RadioButton mHomeHomeRb;
	private RadioButton mHomeFindRb;
	private RadioButton mHomeSearchRb;
	private RadioButton mHomeProfileRb;
	private MaintenanceFragment mMaintenanceFragment;
	private GrabTaskFragment mGrabTaskFragment;
	private TaskListFragment mTaskListFragment;
	private PersonalZoneFragment mPersonalZoneFragment;
	private NearByFragment mNearByFragment;
	private Fragment currentFragment;
	private String employeeType;
	private String uid;
	private String certificate;
	public static String JZ = "兼职";
	public static String QZ = "全职";

	@Override
	protected int getLayoutResIds() {
		// TODO Auto-generated method stub
		return R.layout.activity_main;
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		employeeType = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.EMPLOYEETYPE, "");
		uid = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(this,
				SharedPreferencesKeys.CERTIFICATED, "");
		if (!stackInstance.isExistActivity(this)) {
			stackInstance.pushActivity(this);
		}
		findViewById();
		setDefaultView();
		initEvents();
		initJpushIfExists();
	}

	private void initJpushIfExists() {
		// TODO Auto-generated method stub
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String orderNo = bundle.getString("orderNo");
			String action = bundle.getString("action");
			Logr.e("=orderNo==" + orderNo + "==action==" + action);
			if (TextUtils.equals(action, "1") && orderNo != null) {// 打开抢单页面
				if (employeeType.equals(JZ)) {
					Intent i = new Intent(this, PackageDetailActivity.class);
					i.putExtra("TaskNo", orderNo);
					startActivity(i);
				} else {
					Utilities.showToast("您是全职人员不用参与抢单！！！", this);
				}
			} else {

			}
		}
	}

	protected void findViewById() {
		mHomeRadioGroup = (RadioGroup) findViewById(R.id.mHomeRadioGroup);
		mHomeHomeRb = (RadioButton) findViewById(R.id.mHomeHomeRb);
		mHomeFindRb = (RadioButton) findViewById(R.id.mHomeFindRb);
		mHomeSearchRb = (RadioButton) findViewById(R.id.mHomeSearchRb);
		mHomeProfileRb = (RadioButton) findViewById(R.id.mHomeProfileRb);
	}

	private void setDefaultView() {
		if (TextUtils.equals(QZ, employeeType)) {
			mHomeHomeRb.setText("维修单");
			String flag = getIntent().getStringExtra("flag");
			if (TextUtils.equals(flag, "1")) {
				currentFragment = FragmentUtils.switchFragment(fragmentManager,
						R.id.mHomeContent, currentFragment,
						TaskListFragment.class, null);
				mTaskListFragment = (TaskListFragment) currentFragment;

				mHomeSearchRb.setChecked(true);
			} else {
				currentFragment = FragmentUtils.switchFragment(fragmentManager,
						R.id.mHomeContent, currentFragment,
						MaintenanceFragment.class, null);
				mMaintenanceFragment = (MaintenanceFragment) currentFragment;
			}
		} else {
			String flag = getIntent().getStringExtra("flag");
			mHomeHomeRb.setText("抢单");
			if (TextUtils.equals("1", flag)) {
				currentFragment = FragmentUtils.switchFragment(fragmentManager,
						R.id.mHomeContent, currentFragment,
						TaskListFragment.class, null);
				mTaskListFragment = (TaskListFragment) currentFragment;

				mHomeSearchRb.setChecked(true);
			} else {
				currentFragment = FragmentUtils.switchFragment(fragmentManager,
						R.id.mHomeContent, currentFragment,
						GrabTaskFragment.class, null);
				mGrabTaskFragment = (GrabTaskFragment) currentFragment;
			}
		}

	}

	private void initEvents() {
		mHomeRadioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.mHomeHomeRb:
							if (TextUtils.equals(employeeType, JZ)) {
								currentFragment = FragmentUtils.switchFragment(
										fragmentManager, R.id.mHomeContent,
										currentFragment,
										GrabTaskFragment.class, null);
								mGrabTaskFragment = (GrabTaskFragment) currentFragment;
							} else if (TextUtils.equals(employeeType, QZ)) {
								currentFragment = FragmentUtils.switchFragment(
										fragmentManager, R.id.mHomeContent,
										currentFragment,
										MaintenanceFragment.class, null);
								mMaintenanceFragment = (MaintenanceFragment) currentFragment;
							}
							// mGrabTaskFragment = new GrabTaskFragment();
							// switchFragment(mGrabTaskFragment);
							break;
						case R.id.mHomeFindRb:
							currentFragment = FragmentUtils
									.switchFragment(fragmentManager,
											R.id.mHomeContent, currentFragment,
											NearByFragment.class, null);
							mNearByFragment = (NearByFragment) currentFragment;

							// mNearByFragment = new NearByFragment();
							// switchFragment(mNearByFragment);
							break;
						case R.id.mHomeSearchRb:
							currentFragment = FragmentUtils.switchFragment(
									fragmentManager, R.id.mHomeContent,
									currentFragment, TaskListFragment.class,
									null);
							mTaskListFragment = (TaskListFragment) currentFragment;
							// mTaskListFragment = new TaskListFragment();
							// switchFragment(mTaskListFragment);
							break;
						case R.id.mHomeProfileRb:
							currentFragment = FragmentUtils.switchFragment(
									fragmentManager, R.id.mHomeContent,
									currentFragment,
									PersonalZoneFragment.class, null);
							mPersonalZoneFragment = (PersonalZoneFragment) currentFragment;
							// mPersonalZoneFragment = new
							// PersonalZoneFragment();
							// switchFragment(mPersonalZoneFragment);
							break;
						}
					}
				});
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			// Effectstype effect = Effectstype.Shake;
			// dialogBuilder.withTitle("温馨提示")
			// .withTitleColor(R.color.main_primary)
			// .withDividerColor("#11000000").withMessage("您确认要退出？")
			// .withMessageColor(R.color.main_primary)
			// .withDialogColor("#FFFFFFFF")
			// .isCancelableOnTouchOutside(true).withDuration(700)
			// .withEffect(effect).withButtonDrawable(R.color.main_white)
			// .withButton1Text("取消").withButton1Color("#DD47BEE9")
			// .withButton2Text("确认").withButton2Color("#DD47BEE9")
			// .setButton1Click(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// dialogBuilder.dismiss();
			// }
			// }).setButton2Click(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// statckInstance.popAllActivitys();
			// }
			// }).show();
			if (System.currentTimeMillis() - exitTime >= 2000) {
				Utilities.showToast("再按一次退出24梯", this);
				exitTime = System.currentTimeMillis();
			} else {
				if (((MyApplication) getApplication()).locationService != null) {
					((MyApplication) getApplication()).locationService.stop();
				}
				stackInstance.popAllActivitys();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		hideSoftInput();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public String getUid() {
		return uid;
	}

	public String getCertificate() {
		return certificate;
	}

	public String getEmployeeType() {
		return employeeType;
	}

}
