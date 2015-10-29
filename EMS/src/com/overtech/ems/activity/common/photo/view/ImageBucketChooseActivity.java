package com.overtech.ems.activity.common.photo.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.common.photo.adapter.ImageBucketAdapter;
import com.overtech.ems.activity.common.photo.model.ImageBucket;
import com.overtech.ems.activity.common.photo.util.CustomConstants;
import com.overtech.ems.activity.common.photo.util.ImageFetcher;
import com.overtech.ems.activity.common.photo.util.IntentConstants;

/**
 * 选择相册
 * 
 */
public class ImageBucketChooseActivity extends Activity
{
	private ImageFetcher mHelper;
	private List<ImageBucket> mDataList = new ArrayList<ImageBucket>();
	private ListView mListView;
	private ImageBucketAdapter mAdapter;
	private int availableSize;
	private TextView mHeadContent;
	private ImageView mHeadBack;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_image_bucket_choose);
		mHelper = ImageFetcher.getInstance(getApplicationContext());
		initData();
		initView();
	}

	private void initData()
	{
		mDataList = mHelper.getImagesBucketList(false);
		availableSize = getIntent().getIntExtra(
				IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
				CustomConstants.MAX_IMAGE_SIZE);
	}

	private void initView()
	{
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mHeadContent.setText("相册");
		mHeadBack.setVisibility(View.VISIBLE);
		mListView = (ListView) findViewById(R.id.listview);
		mAdapter = new ImageBucketAdapter(this, mDataList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				selectOne(position);
				Intent intent = new Intent(ImageBucketChooseActivity.this,
						ImageChooseActivity.class);
				intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
						(Serializable) mDataList.get(position).imageList);
				intent.putExtra(IntentConstants.EXTRA_BUCKET_NAME,
						mDataList.get(position).bucketName);
				intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
						availableSize);
				startActivity(intent);
			}
		});

		mHeadBack.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(ImageBucketChooseActivity.this,
						PublishActivity.class);
				startActivity(intent);
			}
		});
	}

	private void selectOne(int position)
	{
		int size = mDataList.size();
		for (int i = 0; i != size; i++)
		{
			if (i == position) mDataList.get(i).selected = true;
			else
			{
				mDataList.get(i).selected = false;
			}
		}
		mAdapter.notifyDataSetChanged();
	}

}
