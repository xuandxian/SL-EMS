package com.overtech.ems.activity.parttime;

import com.overtech.ems.R;
import com.overtech.ems.activity.ScanCodeActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TaskListScanActivity extends Activity {

	private TextView mTextView;
	private ImageView mImageView;
	private Button mTaskListScanBtn;
	private final static int SCANNIN_GREQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_list_scan);
		findViewById();
		initScanEvents();
	}

	private void findViewById() {
		mTextView = (TextView) findViewById(R.id.result);
		mImageView = (ImageView) findViewById(R.id.qrcode_bitmap);
		mTaskListScanBtn = (Button) findViewById(R.id.task_list_scan_btn);
	}

	private void initScanEvents() {
		mTaskListScanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(TaskListScanActivity.this,
						ScanCodeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				// 显示扫描到的内容
				mTextView.setText(bundle.getString("result"));
				showDialog(bundle.getString("result"));
				// 显示
				mImageView.setImageBitmap((Bitmap) data
						.getParcelableExtra("bitmap"));
			}
			break;
		}
	}

	private void showDialog(final String url) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("温馨提示");
		builder.setIcon(R.drawable.ic_launcher);
		builder.setMessage("是否打开:" + url);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				Uri uri = Uri.parse(url);
				intent.setData(uri);
				startActivity(intent);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		Dialog dialog = builder.create();
		dialog.show();
	}
}
