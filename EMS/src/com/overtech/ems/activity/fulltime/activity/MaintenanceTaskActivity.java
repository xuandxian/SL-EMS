package com.overtech.ems.activity.fulltime.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;

public class MaintenanceTaskActivity extends BaseActivity implements
		OnClickListener {
	private LinearLayout llFaultComponent;
	private LinearLayout llFaultCause;
	private LinearLayout llFaultSubCause;
	private LinearLayout llFaultCauseElectric;
	private LinearLayout llFaultCauseMachine;
	private LinearLayout llFaultCauseOther;
	private LinearLayout llFaultSolve;
	private LinearLayout llFaultProperty;
	private LinearLayout llFaultDuty;
	private LinearLayout llFaultAttention;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maintenancetask);
		initView();
		initEvent();
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		llFaultComponent.setOnClickListener(this);
		llFaultCause.setOnClickListener(this);
		llFaultCauseElectric.setOnClickListener(this);
		llFaultCauseMachine.setOnClickListener(this);
		llFaultCauseOther.setOnClickListener(this);
		llFaultSolve.setOnClickListener(this);
		llFaultProperty.setOnClickListener(this);
		llFaultDuty.setOnClickListener(this);
		llFaultAttention.setOnClickListener(this);
	}

	private void initView() {
		// TODO Auto-generated method stub
		llFaultComponent = (LinearLayout) findViewById(R.id.ll_fault_component);
		llFaultCause = (LinearLayout) findViewById(R.id.ll_fault_cause);
		llFaultSubCause = (LinearLayout) findViewById(R.id.ll_fault_sub_cause);
		llFaultCauseElectric = (LinearLayout) findViewById(R.id.ll_fault_cause_electric);
		llFaultCauseMachine = (LinearLayout) findViewById(R.id.ll_fault_cause_machine);
		llFaultCauseOther = (LinearLayout) findViewById(R.id.ll_fault_cause_other);
		llFaultSolve = (LinearLayout) findViewById(R.id.ll_fault_solve);
		llFaultProperty = (LinearLayout) findViewById(R.id.ll_fault_property);
		llFaultDuty = (LinearLayout) findViewById(R.id.ll_fault_duty);
		llFaultAttention = (LinearLayout) findViewById(R.id.ll_fault_attention);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ll_fault_component:
			Intent component=new Intent(this,)
			break;
		case R.id.ll_fault_cause:
			if(llFaultSubCause.getVisibility()==ViewGroup.VISIBLE){
				llFaultSubCause.setVisibility(ViewGroup.GONE);
			}else{
				llFaultSubCause.setVisibility(ViewGroup.VISIBLE);
			}
			break;
		case R.id.ll_fault_cause_electric:

			break;
		case R.id.ll_fault_cause_machine:

			break;
		case R.id.ll_fault_cause_other:

			break;
		case R.id.ll_fault_solve:

			break;
		case R.id.ll_fault_property:

			break;
		case R.id.ll_fault_duty:

			break;
		case R.id.ll_fault_attention:

			break;

		default:
			break;
		}
	}
}
