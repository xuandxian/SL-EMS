package com.overtech.ems.activity.parttime.grabtask;

import java.io.IOException;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.SearchHistoryAdapter;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.entity.common.ServicesConfig;
import com.overtech.ems.http.HttpEngine.Param;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import android.view.inputmethod.EditorInfo;

public class KeyWordSerachActivity extends BaseActivity {

	private EditTextWithDelete mDoSearch;
	private TextView mDoCancel;
	private ListView listView;
	private String mKeyWord;
	private SearchHistoryAdapter adapter;
	private ArrayList<String> list=new ArrayList<String>();
	
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.KEYWORDS_SUCCESS:
				list=(ArrayList<String>) msg.obj;
				for (int i = 0; i < list.size(); i++) {
					Utilities.showToast("data:"+list.get(i), context);
				}
//				adapter=new SearchHistoryAdapter(context,list);
				break;
            case StatusCode.KEYWORDS_FAILED:
				
				break;
            case StatusCode.RESPONSE_NET_FAILED:
            	break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grab_task_keyword);
		findViewById();
		init();
	}
	private void findViewById(){
		mDoSearch=(EditTextWithDelete)findViewById(R.id.et_do_parttime_search);
		mDoCancel=(TextView)findViewById(R.id.tv_parttime_do_cancel);
		listView=(ListView)findViewById(R.id.lv_search_history);
	}
	private void init(){
		
		mDoSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
				if (!"".equals(s.toString().trim())) {
					listView.setVisibility(View.VISIBLE);
					GetAutoCompleteResult(s.toString().trim());
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		mDoSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
		mDoCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	private void GetAutoCompleteResult(String keyWord) {
		Param params=new Param("mAutoKeyWord", keyWord);
		Request request = httpEngine.createRequest(ServicesConfig.KEY_WORD_COMPLETE, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.KEYWORDS_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.KEYWORDS_FAILED;
				}
				handler.sendMessage(msg);
			}
			
			@Override
			public void onFailure(Request request, IOException e) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				handler.sendMessage(msg);
			}
		});
	}
}
