package com.overtech.ems.activity.parttime.fragment;

import com.overtech.ems.R;
import com.overtech.ems.activity.parttime.PersonalDeatilsActivity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PersonalZoneFragment extends Fragment implements OnClickListener {
	private View view;
	private LinearLayout personal_details;

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
		personal_details = (LinearLayout) view
				.findViewById(R.id.personal_details);
	}

	private void initEvents() {
		personal_details.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.personal_details:
			Intent intent = new Intent(getActivity(),
					PersonalDeatilsActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}
