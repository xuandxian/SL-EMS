package com.overtech.ems.activity.parttime.fragment;

import com.overtech.ems.R;
import com.overtech.ems.activity.parttime.personal.PersonalDeatilsActivity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class PersonalZoneFragment extends Fragment implements OnClickListener {
	private View view;
	private LinearLayout mPersonalDetail;
	private LinearLayout mPersonalAccountList;
	private LinearLayout mPersonalBounds;
	private LinearLayout mCompanyNotice;

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
		mPersonalDetail = (LinearLayout) view
				.findViewById(R.id.ll_personal_details);
		mPersonalAccountList = (LinearLayout) view
				.findViewById(R.id.ll_personal_account_list);
		mPersonalBounds = (LinearLayout) view
				.findViewById(R.id.ll_personal_bounds);
		mCompanyNotice = (LinearLayout) view
				.findViewById(R.id.ll_personal_notice);

	}

	private void initEvents() {
		mPersonalDetail.setOnClickListener(this);
		mPersonalAccountList.setOnClickListener(this);
		mPersonalBounds.setOnClickListener(this);
		mCompanyNotice.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.ll_personal_details:
			intent.setClass(getActivity(), PersonalDeatilsActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_personal_account_list:
			intent.setClass(getActivity(), PersonalDeatilsActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_personal_bounds:
			intent.setClass(getActivity(), PersonalDeatilsActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_personal_notice:
			intent.setClass(getActivity(), PersonalDeatilsActivity.class);
			startActivity(intent);
			break;
		}
	}

}
