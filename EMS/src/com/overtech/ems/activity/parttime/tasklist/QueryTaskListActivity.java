package com.overtech.ems.activity.parttime.tasklist;

import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.CustomProgressDialog;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;

public class QueryTaskListActivity extends Activity {
	private MainFrameTask mMainFrameTask = null;
	private CustomProgressDialog progressDialog = null;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = QueryTaskListActivity.this;
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String result = bundle.getString("result");
		if (TextUtils.isEmpty(result) || result == null) {
			return;
		} else {
			Utilities.showToast(result, context);
			mMainFrameTask = new MainFrameTask(this);
			mMainFrameTask.execute();
		}
	}

	private void startProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage("正在查询中...");
		}
		progressDialog.show();
	}

	private void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public class MainFrameTask extends AsyncTask<Integer, String, Integer> {
		private QueryTaskListActivity mainFrame = null;

		public MainFrameTask(QueryTaskListActivity mainFrame) {
			this.mainFrame = mainFrame;
		}

		@Override
		protected void onCancelled() {
			stopProgressDialog();
			super.onCancelled();
		}

		@Override
		protected Integer doInBackground(Integer... params) {

			try {
				Thread.sleep(100 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			startProgressDialog();
		}

		@Override
		protected void onPostExecute(Integer result) {
			stopProgressDialog();
		}
	}

	@Override
	protected void onDestroy() {
		stopProgressDialog();
		if (mMainFrameTask != null && !mMainFrameTask.isCancelled()) {
			mMainFrameTask.cancel(true);
		}
		super.onDestroy();
	}
}
