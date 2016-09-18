package com.overtech.ems.activity.fulltime.maintenance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.ArcOptions;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.parttime.tasklist.ScanCodeActivity;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.FragmentUtils;

public class MaintenanceFragment extends BaseFragment implements
		OnClickListener {
	private TextView title;
	private Button btMaintenanceNone;
	private Button btMaintenanceDone;
	private Fragment currentFragment;
	private MaintenanceNoneFragment mMaintenanceNoneFragment;
	private MaintenanceDoneFragment mMaintenanceDoneFragment;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_maintenance, container,
				false);
		initView(view);
		initEvent();
		return view;
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		title.setText("维修单");
		currentFragment = FragmentUtils.switchFragment(
				getChildFragmentManager(), R.id.fl_maintenance_content,
				currentFragment, MaintenanceNoneFragment.class, null);
		mMaintenanceNoneFragment = (MaintenanceNoneFragment) currentFragment;
		btMaintenanceNone.setOnClickListener(this);
		btMaintenanceDone.setOnClickListener(this);
	}

	private void initView(View view) {
		// TODO Auto-generated method stub
		title = (TextView) view.findViewById(R.id.tv_tasklist_title);
		btMaintenanceNone = (Button) view
				.findViewById(R.id.bt_maintenance_none);
		btMaintenanceDone = (Button) view
				.findViewById(R.id.bt_maintenance_done);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_maintenance_none:
			currentFragment = FragmentUtils.switchFragment(
					getChildFragmentManager(), R.id.fl_maintenance_content,
					currentFragment, MaintenanceNoneFragment.class, null);
			mMaintenanceNoneFragment = (MaintenanceNoneFragment) currentFragment;
			btMaintenanceNone.setBackgroundResource(R.drawable.horizontal_line);
			btMaintenanceDone.setBackgroundResource(R.color.main_white);
			btMaintenanceNone.setTextColor(getResources().getColor(R.color.colorPrimary));
			btMaintenanceDone.setTextColor(getResources().getColor(R.color.primary_text_default_material_light));
			break;
		case R.id.bt_maintenance_done:
			currentFragment = FragmentUtils.switchFragment(
					getChildFragmentManager(), R.id.fl_maintenance_content,
					currentFragment, MaintenanceDoneFragment.class, null);
			mMaintenanceDoneFragment = (MaintenanceDoneFragment) currentFragment;
			btMaintenanceNone.setBackgroundResource(R.color.main_white);
			btMaintenanceDone.setBackgroundResource(R.drawable.horizontal_line);
			btMaintenanceNone.setTextColor(getResources().getColor(R.color.primary_text_default_material_light));
			btMaintenanceDone.setTextColor(getResources().getColor(R.color.colorPrimary));
			break;
		case R.id.iv_common_qrcode:
			Intent intent = new Intent(getActivity(), ScanCodeActivity.class);
			intent.putExtra(Constant.EMPLOYEETYPE, "全职");
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
