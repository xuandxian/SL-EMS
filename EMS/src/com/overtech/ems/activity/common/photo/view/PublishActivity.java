package com.overtech.ems.activity.common.photo.view;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.overtech.ems.R;
import com.overtech.ems.activity.common.photo.adapter.ImagePublishAdapter;
import com.overtech.ems.activity.common.photo.model.ImageItem;
import com.overtech.ems.activity.common.photo.util.CustomConstants;
import com.overtech.ems.activity.common.photo.util.IntentConstants;

public class PublishActivity extends Activity {
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private GridView mGridView;
	private ImagePublishAdapter mAdapter;
	// private TextView sendTv;
	public static List<ImageItem> mDataList = new ArrayList<ImageItem>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_publish);
		initData();
		initView();
	}

	protected void onPause() {
		super.onPause();
		saveTempToPref();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveTempToPref();
	}

	private void saveTempToPref() {
		SharedPreferences sp = getSharedPreferences(
				CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
		String prefStr = JSON.toJSONString(mDataList);
		sp.edit().putString(CustomConstants.PREF_TEMP_IMAGES, prefStr).commit();

	}

	private void getTempFromPref() {
		SharedPreferences sp = getSharedPreferences(
				CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
		String prefStr = sp.getString(CustomConstants.PREF_TEMP_IMAGES, null);
		if (!TextUtils.isEmpty(prefStr)) {
			List<ImageItem> tempImages = JSON.parseArray(prefStr,
					ImageItem.class);
			mDataList = tempImages;
		}
	}

	private void removeTempFromPref() {
		SharedPreferences sp = getSharedPreferences(
				CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
		sp.edit().remove(CustomConstants.PREF_TEMP_IMAGES).commit();
	}

	@SuppressWarnings("unchecked")
	private void initData() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mHeadContent.setText("身份证确认");
		mHeadBack.setVisibility(View.VISIBLE);
		getTempFromPref();
		List<ImageItem> incomingDataList = (List<ImageItem>) getIntent()
				.getSerializableExtra(IntentConstants.EXTRA_IMAGE_LIST);
		if (incomingDataList != null) {
			mDataList.addAll(incomingDataList);
		}
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		notifyDataChanged(); // 当在ImageZoomActivity中删除图片时，返回这里需要刷新
	}

	public void initView() {
		mGridView = (GridView) findViewById(R.id.gridview_photo);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mAdapter = new ImagePublishAdapter(this, mDataList);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == getDataSize()) {
					new PopupWindows(PublishActivity.this, mGridView);
				} else {
					Intent intent = new Intent(PublishActivity.this,
							ImageZoomActivity.class);
					intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
							(Serializable) mDataList);
					intent.putExtra(IntentConstants.EXTRA_CURRENT_IMG_POSITION,
							position);
					startActivity(intent);
				}
			}
		});
		// sendTv = (TextView) findViewById(R.id.action);
		// sendTv.setText("发送");
		// sendTv.setOnClickListener(new OnClickListener()
		// {
		//
		// public void onClick(View v)
		// {
		// removeTempFromPref();
		// System.exit(0);
		// //TODO 这边以mDataList为来源做上传的动作
		// }
		// });
	}

	private int getDataSize() {
		return mDataList == null ? 0 : mDataList.size();
	}

	private int getAvailableSize() {
		int availSize = CustomConstants.MAX_IMAGE_SIZE - mDataList.size();
		if (availSize >= 0) {
			return availSize;
		}
		return 0;
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View.inflate(mContext, R.layout.item_popupwindow, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));

			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					takePhoto();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(PublishActivity.this,
							ImageBucketChooseActivity.class);
					intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
							getAvailableSize());
					startActivity(intent);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}

	private static final int TAKE_PICTURE = 0x000000;
	private String path = "";

	public void takePhoto() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		File vFile = new File(Environment.getExternalStorageDirectory()
				+ "/myimage/", String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		if (!vFile.exists()) {
			File vDirPath = vFile.getParentFile();
			vDirPath.mkdirs();
		} else {
			if (vFile.exists()) {
				vFile.delete();
			}
		}
		path = vFile.getPath();
		Uri cameraUri = Uri.fromFile(vFile);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (mDataList.size() < CustomConstants.MAX_IMAGE_SIZE
					&& resultCode == -1 && !TextUtils.isEmpty(path)) {
				ImageItem item = new ImageItem();
				item.sourcePath = path;
				mDataList.add(item);
			}
			break;
		}
	}

	private void notifyDataChanged() {
		mAdapter.notifyDataSetChanged();
	}

}