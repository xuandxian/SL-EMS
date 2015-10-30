package com.overtech.ems.activity.parttime.personal;

import com.overtech.ems.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class PersonalCompanyNoticesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_company_notice);
	}
}
