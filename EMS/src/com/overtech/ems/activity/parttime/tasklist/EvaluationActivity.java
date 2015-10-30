package com.overtech.ems.activity.parttime.tasklist;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.CustomProgressDialog;

public class EvaluationActivity extends BaseActivity implements OnClickListener {
	private TextView mHeadContent;
	private ImageView mDoBack;
	private Button mConfirm;
	private CustomProgressDialog progressDialog;
	private RadioGroup mChecked;
	private EditText mContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_evaluation);
		init();
		initEvent();
	}

	private void initEvent() {
		mDoBack.setOnClickListener(this);
		mConfirm.setOnClickListener(this);
	}

	private void init() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mDoBack = (ImageView) findViewById(R.id.iv_headBack);
		mConfirm=(Button) findViewById(R.id.bt_confirm);
		mChecked=(RadioGroup) findViewById(R.id.rg_container);
		mContent=(EditText) findViewById(R.id.et_evaluation);
		mHeadContent.setText("互相评价");
		mDoBack.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_headBack:
			finish();
			break;
		case R.id.bt_confirm:
			dealContent();
			
			break;
		default:
			break;
		}
	}

	private void dealContent() {
		String content=mContent.getText().toString().trim();
		int rbId=mChecked.getCheckedRadioButtonId();
		switch (rbId) {
		case R.id.rb_verygood:
			Utilities.showToast("3分", this);
			break;
		case R.id.rb_sogood:
			Utilities.showToast("2分", this);
			break;
		case R.id.rb_good:
			Utilities.showToast("1分", this);
			break;
		case R.id.rb_notbad:
			Utilities.showToast("0分", this);
			break;
		case R.id.rb_bad:
			if(TextUtils.isEmpty(content)){
				Utilities.showToast("请告诉我们您选择差评的原因", this);
				return;
			}else if(content.length()<15){
				Utilities.showToast("输入的字符少于15个", this);
				return;
			}
			break;
		}
		showDialog();
	}

	private void showDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage("正在提交...");
		}
		progressDialog.show();
	}
}
