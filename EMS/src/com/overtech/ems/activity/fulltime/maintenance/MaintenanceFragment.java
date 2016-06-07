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

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseFragment;
import com.overtech.ems.activity.parttime.tasklist.ScanCodeActivity;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.FragmentUtils;

public class MaintenanceFragment extends BaseFragment implements
		OnClickListener {
	private TextView title;
	private ImageView qrCode;
	private Button btMaintenanceNone;
	private Button btMaintenanceDone;
	private Fragment currentFragment;
	private MaintenanceNoneFragment mMaintenanceNoneFragment;
	private MaintenanceDoneFragment mMaintenanceDoneFragment;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_maintenance, null);
		title = (TextView) view.findViewById(R.id.tv_tasklist_title);
		qrCode = (ImageView) view.findViewById(R.id.iv_common_qrcode);
		btMaintenanceNone = (Button) view
				.findViewById(R.id.bt_maintenance_none);
		btMaintenanceDone = (Button) view
				.findViewById(R.id.bt_maintenance_done);

		title.setText("维修单");
		currentFragment = FragmentUtils.switchFragment(fragmentManager,
				R.id.fl_maintenance_content, currentFragment,
				MaintenanceNoneFragment.class, null);
		mMaintenanceNoneFragment = (MaintenanceNoneFragment) currentFragment;
		qrCode.setOnClickListener(this);
		btMaintenanceNone.setOnClickListener(this);
		btMaintenanceDone.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_maintenance_none:
			currentFragment = FragmentUtils.switchFragment(fragmentManager,
					R.id.fl_maintenance_content, currentFragment,
					MaintenanceNoneFragment.class, null);
			mMaintenanceNoneFragment = (MaintenanceNoneFragment) currentFragment;
			break;
		case R.id.bt_maintenance_done:
			currentFragment = FragmentUtils.switchFragment(fragmentManager,
					R.id.fl_maintenance_content, currentFragment,
					MaintenanceDoneFragment.class, null);
			mMaintenanceDoneFragment = (MaintenanceDoneFragment) currentFragment;
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
