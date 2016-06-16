package com.overtech.ems.activity.parttime.grabtask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.BaseActivity;
import com.overtech.ems.activity.adapter.SearchHistoryAdapter;
import com.overtech.ems.activity.adapter.SearchResultAdapter;
import com.overtech.ems.config.StatusCode;
import com.overtech.ems.config.SystemConfig;
import com.overtech.ems.entity.bean.StatusCodeBean;
import com.overtech.ems.entity.common.Requester;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.SharedPreferencesKeys;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.EditTextWithDelete;
import com.overtech.ems.widget.swipemenu.SwipeMenu;
import com.overtech.ems.widget.swipemenu.SwipeMenuCreator;
import com.overtech.ems.widget.swipemenu.SwipeMenuItem;
import com.overtech.ems.widget.swipemenu.SwipeMenuListView;
import com.overtech.ems.widget.swipemenu.SwipeMenuListView.OnMenuItemClickListener;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class KeyWordSerachActivity extends BaseActivity {

	private EditTextWithDelete mDoSearch;
	private TextView mDoCancel;
	private TextView mHistoryTextView;
	private ListView mSearchListView;
	private SwipeMenuListView mHistoryListView;
	private SwipeMenuCreator creator;
	private String mKeyWord;
	private SearchResultAdapter mResultAdapter;
	private SearchHistoryAdapter mHistoryAdapter;
	private int zoneCount;
	private SharedPreferences sharedPreferences;
	private ArrayList<String> searchList = new ArrayList<String>();
	private ArrayList<String> historyList = new ArrayList<String>();
	private KeyWordSerachActivity activity;
	private String uid;
	private String certificate;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StatusCode.KEYWORDS_SUCCESS:
				if (!searchList.isEmpty()) {
					searchList.clear();
				}
				String json = (String) msg.obj;
				StatusCodeBean bean = gson.fromJson(json, StatusCodeBean.class);
				List<String> data = (List<String>) bean.body.get("data");
				Logr.e("后台传过来的数据" + json);
				String[] array = (String[]) data.toArray();
				int length = array.length;
				if (length == 1) {
					Utilities.showToast("无结果", activity);
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
					mResultAdapter = new SearchResultAdapter(activity,
							searchList, zoneCount);
					mSearchListView.setAdapter(mResultAdapter);
				}
				break;
			case StatusCode.RESPONSE_SERVER_EXCEPTION:
				Utilities.showToast("服务器异常", activity);
				break;
			case StatusCode.RESPONSE_NET_FAILED:
				Utilities.showToast("网络异常", activity);
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
		mHistoryListView = (SwipeMenuListView) findViewById(R.id.lv_search_history);
	}

	private void init() {
		activity = this;
		uid = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.UID, "");
		certificate = (String) SharePreferencesUtils.get(activity,
				SharedPreferencesKeys.CERTIFICATED, "");
		sharedPreferences = getSharedPreferences("search", MODE_PRIVATE);
		readHistory();
		if (null == historyList || historyList.isEmpty()) {
			Utilities.showToast("无历史记录", activity);
		} else {
			initHistoryItem();
			mHistoryTextView.setVisibility(View.VISIBLE);
			mHistoryAdapter = new SearchHistoryAdapter(activity, historyList);
			mHistoryListView.setAdapter(mHistoryAdapter);
		}
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
				mKeyWord = ((String) parent.getItemAtPosition(position))
						.replace("\"", "");
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
				mKeyWord = ((String) parent.getItemAtPosition(position))
						.replace("\"", "");
				writeHistory(mKeyWord);
				Intent intent = new Intent();
				intent.putExtra("mKeyWord", mKeyWord);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
		mHistoryListView
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,
							int index) {
						String data = historyList.get(position);
						historyList.remove(position);
						deleteHistory(data);
						mHistoryAdapter.notifyDataSetChanged();
					}
				});
	}

	private void GetAutoCompleteResult(String keyWord) {
		Requester requester = new Requester();
		requester.cmd = 20024;
		requester.certificate = certificate;
		requester.uid = uid;
		requester.body.put("mAutoKeyWord", keyWord);
		Request request = httpEngine.createRequest(SystemConfig.NEWIP,
				gson.toJson(requester));
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onResponse(Response response) throws IOException {
				Message msg = new Message();
				if (response.isSuccessful()) {
					msg.what = StatusCode.KEYWORDS_SUCCESS;
					msg.obj = response.body().string();
				} else {
					msg.what = StatusCode.RESPONSE_SERVER_EXCEPTION;
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

	private void writeHistory(String value) {
		Editor editor = sharedPreferences.edit();
		String longHistory = sharedPreferences.getString("history", "");
		String realValue = value + ",";
		StringBuilder sb = new StringBuilder(longHistory);
		if (!longHistory.contains(realValue)) {
			sb.insert(0, realValue);
		} else {
			int index = sb.indexOf(realValue);
			sb.delete(index, index + realValue.length());
			sb.insert(0, realValue);
		}
		editor.putString("history", sb.toString());
		editor.commit();
	}

	private void deleteHistory(String value) {
		Editor editor = sharedPreferences.edit();
		String longHistory = sharedPreferences.getString("history", "");
		String realValue = value + ",";
		StringBuilder sb = new StringBuilder(longHistory);
		int index = sb.indexOf(realValue);
		sb.delete(index, index + realValue.length());
		editor.putString("history", sb.toString());
		editor.commit();
	}

	private List<String> readHistory() {
		String longHistory = sharedPreferences.getString("history", "");
		if ("" != longHistory) {
			String[] historyArray = longHistory.split(",");
			for (int i = 0; i < historyArray.length; i++) {
				if (historyList.size() < 10) {
					historyList.add(historyArray[i]);
				}
			}
		}
		return historyList;
	}

	private void initHistoryItem() {
		creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem deleteItem = new SwipeMenuItem(activity);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				deleteItem.setWidth(dp2px(60));
				deleteItem.setTitle("删除");
				deleteItem.setTitleSize(18);
				deleteItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(deleteItem);
			}
		};
		mHistoryListView.setMenuCreator(creator);
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
