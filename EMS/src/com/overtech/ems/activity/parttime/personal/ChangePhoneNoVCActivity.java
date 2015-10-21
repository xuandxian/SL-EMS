package com.overtech.ems.activity.parttime.personal;

import android.os.Bundle;
import android.view.Window;

import com.overtech.ems.activity.BaseActivity;

public class ChangePhoneNoVCActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
	}
}
