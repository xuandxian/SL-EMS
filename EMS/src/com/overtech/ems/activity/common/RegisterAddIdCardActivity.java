package com.overtech.ems.activity.common;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.utils.ImageCacheUtils;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.popwindow.DimPopupWindow;

public class RegisterAddIdCardActivity extends Activity implements OnClickListener {
	private TextView mHeadContent;
	private ImageView mHeadBack;
	private Button mNext;
	private ImageView mIdCardFront;
	private ImageView mIdCardOpposite;
	private Context mActivity;
	private DimPopupWindow mPopupWindow;
	private Button mCamera;
	private Button mPhoto;
	private Button mCancle;
	
	/** 
     * 打开本地相册的requestcode. 
     */  
    public static final int OPEN_PHOTO_REQUESTCODE =  0x1;  
    /**
     * 打开照相机的requestcode.
     */
    private static final int PHOTO_CAPTURE = 0x2;
    /** 
     * 图片的target大小. 
     */  
    private static String photoPath = "/sdcard/EMS/";
	private static String photoName = photoPath + "IDCard.jpg";
	Uri imageUri = Uri.fromFile(new File(Environment
			.getExternalStorageDirectory(), "image.jpg"));
	
    private static final int target = 400;  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_add_id_card);
		mActivity=this;
		findViewById();
		init();
	}

	private void init() {
		mHeadContent.setText("身份证确认");
		mHeadBack.setVisibility(View.VISIBLE);
		mNext.setOnClickListener(this);
		mHeadBack.setOnClickListener(this);
		mIdCardFront.setOnClickListener(this);
		mIdCardOpposite.setOnClickListener(this);
	}

	private void findViewById() {
		mHeadContent = (TextView) findViewById(R.id.tv_headTitle);
		mHeadBack = (ImageView) findViewById(R.id.iv_headBack);
		mNext=(Button) findViewById(R.id.btn_register_next_work);
		mIdCardFront=(ImageView) findViewById(R.id.iv_idcard_front);
		mIdCardOpposite=(ImageView) findViewById(R.id.iv_idcard_opposite);
	}
	/**
	 * 记录身份证点击的状态
	 * */
	private int currentState;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_headBack:
			finish();
			break;
		case R.id.btn_register_next_work:
			Intent intent = new Intent(RegisterAddIdCardActivity.this,RegisterAddWorkCertificateActivity.class);
			startActivity(intent);
			break;
		case R.id.iv_idcard_front:
			currentState=0;
			showPopupWindow(v);
			break;
		case R.id.iv_idcard_opposite:
			currentState=1;
			showPopupWindow(v);
			break;
		case R.id.item_popupwindows_camera:
			Utilities.showToast("你点击了照相机", mActivity);
			openCamera();
			mPopupWindow.dismiss();
			break;
		case R.id.item_popupwindows_Photo:
			Utilities.showToast("你点击了相册", mActivity);
			openPhotos();
			mPopupWindow.dismiss();
			break;
		case R.id.item_popupwindows_cancel:
			Utilities.showToast("你点击了取消", mActivity);
			mPopupWindow.dismiss();
			break;
		default:
			break;
		}
	}

	private void openCamera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		File file = new File(photoPath);
		if (!file.exists()) { // 检查图片存放的文件夹是否存在
			file.mkdir(); // 不存在的话 创建文件夹
		}
		File photo = new File(photoName);
		imageUri = Uri.fromFile(photo);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
		startActivityForResult(intent, PHOTO_CAPTURE);
	}

	private void openPhotos() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);  
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  
                "image/*");  
  
        startActivityForResult(intent, OPEN_PHOTO_REQUESTCODE);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String sdStatus = Environment.getExternalStorageState();
		if(!sdStatus.equals(Environment.MEDIA_MOUNTED)){
			Log.i("内存卡错误", "请检查您的内存卡");
			return ;
		}
		switch (requestCode) {
		case OPEN_PHOTO_REQUESTCODE:
			if(resultCode == RESULT_OK){  
                //如果用这个方法，Options为null时候，就是默认decode会出现oom哦.  
                //Bitmap bm = ImageCacheUtil.decode(null, null,   
                //      ImageCacheDemoActivity.this, data.getData(), null);  
                  
                //这里调用这个方法就不会oom.屌丝们就用这个方法吧.  
                Bitmap bm = ImageCacheUtils.getResizedBitmap(null, null,   
                        mActivity, data.getData(), target, false);  
                if(currentState==0){
                	mIdCardFront.setImageBitmap(bm);  
                }else if(currentState==1){
                	mIdCardOpposite.setImageBitmap(bm);
                }
            }
			break;
		case PHOTO_CAPTURE:
			Bitmap pic=ImageCacheUtils.getBitmap(photoName);
			if(currentState==0){
            	mIdCardFront.setImageBitmap(pic);  
            }else if(currentState==1){
            	mIdCardOpposite.setImageBitmap(pic);
            }
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void showPopupWindow(View v) {
		mPopupWindow=new DimPopupWindow(mActivity);
		View contentView=LayoutInflater.from(mActivity).inflate(R.layout.layout_dim_pop_add_idcard, null);
		findViewById(contentView);
		mPopupWindow.setContentView(contentView);
		mPopupWindow.setInAnimation(R.anim.register_add_idcard_in);
		mPopupWindow.showAtLocation(getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
	}

	private void findViewById(View contentView) {
		mCamera=(Button) contentView.findViewById(R.id.item_popupwindows_camera);
		mPhoto=(Button) contentView.findViewById(R.id.item_popupwindows_Photo);
		mCancle=(Button) contentView.findViewById(R.id.item_popupwindows_cancel);
		mCamera.setOnClickListener(this);
		mPhoto.setOnClickListener(this);
		mCancle.setOnClickListener(this);
	}
}
