package com.overtech.ems.activity.parttime.fragment;

import com.overtech.ems.R;
import com.overtech.ems.activity.parttime.personal.PersonalAccountListActivity;
import com.overtech.ems.activity.parttime.personal.PersonalAnnouncementActivity;
import com.overtech.ems.activity.parttime.personal.PersonalBoundsActivity;
import com.overtech.ems.activity.parttime.personal.PersonalCancleListActivity;
import com.overtech.ems.activity.parttime.personal.PersonalDeatilsActivity;
import com.overtech.ems.activity.parttime.personal.PersonalHelpDocActivity;
import com.overtech.ems.utils.Utilities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class PersonalZoneFragment extends Fragment implements OnClickListener {
	private View view;
	private RelativeLayout mPersonalDetail;
	private RelativeLayout mPersonalAccountList;
	private RelativeLayout mPersonalBounds;
	private RelativeLayout mCompanyNotice;
	private RelativeLayout mCancleList;
	private RelativeLayout mHelpDoc;
	private Activity mActivity;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity=activity;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_personal_zone, container,
				false);
		initViews();
		initEvents();
		return view;
	}

	private void initViews() {
		mPersonalDetail = (RelativeLayout) view
				.findViewById(R.id.rl_personal_details);
		mPersonalAccountList = (RelativeLayout) view
				.findViewById(R.id.rl_personal_account_list);
		mPersonalBounds = (RelativeLayout) view
				.findViewById(R.id.rl_personal_bounds);
		mCompanyNotice = (RelativeLayout) view
				.findViewById(R.id.rl_personal_notice);
		mCancleList = (RelativeLayout) view
				.findViewById(R.id.rl_cancle_list);
		mHelpDoc = (RelativeLayout) view
				.findViewById(R.id.rl_help_doc);
	}

	private void initEvents() {
		mPersonalDetail.setOnClickListener(this);
		mPersonalAccountList.setOnClickListener(this);
		mPersonalBounds.setOnClickListener(this);
		mCompanyNotice.setOnClickListener(this);
		mCancleList.setOnClickListener(this);
		mHelpDoc.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.rl_personal_details:
			intent.setClass(mActivity, PersonalDeatilsActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_personal_account_list:
			intent.setClass(mActivity, PersonalAccountListActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_personal_bounds:
			intent.setClass(mActivity, PersonalBoundsActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_personal_notice:
//			Utilities.showToast("你点击了公告", mActivity);
			intent.setClass(mActivity, PersonalAnnouncementActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_cancle_list:
			intent.setClass(mActivity, PersonalCancleListActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_help_doc:
			intent.setClass(mActivity, PersonalHelpDocActivity.class);
			startActivity(intent);
			break;
		}
	}

}
