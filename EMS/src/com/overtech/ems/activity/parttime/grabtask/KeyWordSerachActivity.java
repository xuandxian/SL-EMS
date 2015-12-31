package com.overtech.ems.activity.parttime.grabtask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.widget.EditTextWithDelete;
import android.view.inputmethod.EditorInfo;

public class KeyWordSerachActivity extends BaseActivity {

	private EditTextWithDelete et_do_parttime_search;
	private TextView tv_parttime_do_cancel;
	private ListView lv_search_history;
	private String mKeyWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grab_task_keyword);
		findViewById();
		init();
	}
	private void findViewById(){
		et_do_parttime_search=(EditTextWithDelete)findViewById(R.id.et_do_parttime_search);
		tv_parttime_do_cancel=(TextView)findViewById(R.id.tv_parttime_do_cancel);
		lv_search_history=(ListView)findViewById(R.id.lv_search_history);
	}
	private void init(){
		et_do_parttime_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					mKeyWord = view.getText().toString().trim();
					Intent intent =new Intent();
					intent.putExtra("mKeyWord",mKeyWord);
					setResult(Activity.RESULT_OK,intent);
					finish();
				}
				return true;
			}
		});
		tv_parttime_do_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
