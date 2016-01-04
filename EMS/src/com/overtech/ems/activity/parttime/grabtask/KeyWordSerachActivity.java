package com.overtech.ems.activity.parttime.grabtask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.SearchHistoryAdapter;
import com.overtech.ems.activity.adapter.SearchResultAdapter;
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
	private TextView mHistoryTextView;
	private ListView mSearchListView;
	private ListView mHistoryListView;
	private String mKeyWord;
	private SearchResultAdapter mResultAdapter;
	private SearchHistoryAdapter mHistoryAdapter;
	private int zoneCount;
	private SharedPreferences sharedPreferences;
	private ArrayList<String> searchList = new ArrayList<String>();
	private ArrayList<String> historyList = new ArrayList<String>();

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.KEYWORDS_SUCCESS:
				if (!searchList.isEmpty()) {
					searchList.clear();
				}
				String data = (String) msg.obj;
				String[] array = data.split(",");
				int length = array.length;
				if (length == 1) {
					Utilities.showToast("无结果", context);
				} else {
					for (int i = 0; i < array.length; i++) {
						if (i == 0) {
							zoneCount = Integer.valueOf(array[i].substring(1)
									.replace("\"", ""));
						} else if (i == array.length - 1) {
							searchList.add(array[i].substring(0,
									array[i].length() - 1));
						} else {
							searchList.add(array[i]);
						}
					}
					mResultAdapter = new SearchResultAdapter(context, searchList, zoneCount);
					mSearchListView.setAdapter(mResultAdapter);
				}
				break;
			case StatusCode.KEYWORDS_FAILED:
				Utilities.showToast("服务器异常", context);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", context);
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

	private void findViewById() {
		mDoSearch = (EditTextWithDelete) findViewById(R.id.et_do_parttime_search);
		mDoCancel = (TextView) findViewById(R.id.tv_parttime_do_cancel);
		mHistoryTextView = (TextView) findViewById(R.id.tv_search_history);
		mSearchListView = (ListView) findViewById(R.id.lv_search_result);
		mHistoryListView = (ListView) findViewById(R.id.lv_search_history);
	}

	private void init() {
		sharedPreferences= getSharedPreferences("search", MODE_PRIVATE);
		readHistory();
		mHistoryTextView.setVisibility(View.VISIBLE);
		mHistoryAdapter=new SearchHistoryAdapter(context, historyList);
		mHistoryListView.setAdapter(mHistoryAdapter);
		mDoSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (!"".equals(s.toString().trim())) {
					mHistoryTextView.setVisibility(View.GONE);
					mHistoryListView.setVisibility(View.GONE);
					mSearchListView.setVisibility(View.VISIBLE);
					GetAutoCompleteResult(s.toString().trim());
				} else {
					if (!searchList.isEmpty()) {
						searchList.clear();
						mResultAdapter.notifyDataSetChanged();
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		mDoSearch
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView view, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_DONE) {
							mKeyWord = view.getText().toString().trim();
							writeHistory(mKeyWord);
							Intent intent = new Intent();
							intent.putExtra("mKeyWord", mKeyWord);
							setResult(Activity.RESULT_OK, intent);
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
		mSearchListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mKeyWord = ((String) parent.getItemAtPosition(position)).replace("\"", "");
				writeHistory(mKeyWord);
				Intent intent = new Intent();
				intent.putExtra("mKeyWord", mKeyWord);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
		mHistoryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mKeyWord = ((String) parent.getItemAtPosition(position)).replace("\"", "");
				writeHistory(mKeyWord);
				Intent intent = new Intent();
				intent.putExtra("mKeyWord", mKeyWord);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	}

	private void GetAutoCompleteResult(String keyWord) {
		Param params = new Param("mAutoKeyWord", keyWord);
		Request request = httpEngine.createRequest(
				ServicesConfig.KEY_WORD_COMPLETE, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onResponse(final Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.KEYWORDS_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.KEYWORDS_FAILED;
				}
				handler.sendMessage(msg);
				// Log.e("KeyWordSerachActivity", response.body().string());
			}

			@Override
			public void onFailure(Request request, IOException e) {
				Message msg = new Message();
				msg.what = StatusCode.RESPONSE_NET_FAILED;
				handler.sendMessage(msg);
			}
		});
	}

	private void writeHistory(String value) {
		Editor editor = sharedPreferences.edit();
		String longHistory=sharedPreferences.getString("history", "");
		String realValue=value+",";
		StringBuilder sb=new StringBuilder(longHistory);
		if (!longHistory.contains(realValue)) {
			sb.insert(0, realValue);
		}else {
			int index=sb.indexOf(realValue);
			sb.delete(index, index+realValue.length());
			sb.insert(0, realValue);
		}
		editor.putString("history", sb.toString());
		editor.commit();
	}

	private List<String> readHistory() {
		String longHistory=sharedPreferences.getString("history", "");
		String[] historyArray=longHistory.split(",");
		for (int i = 0; i < historyArray.length; i++) {
			historyList.add(historyArray[i]);
		}
		return historyList;
	}
}
