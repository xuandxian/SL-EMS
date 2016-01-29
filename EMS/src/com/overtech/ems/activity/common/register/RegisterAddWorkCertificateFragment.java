package com.overtech.ems.activity.common.register;

import java.io.File;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.activity.MyApplication;
import com.overtech.ems.http.constant.Constant;
import com.overtech.ems.utils.ImageCacheUtils;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.popwindow.DimPopupWindow;

public class RegisterAddWorkCertificateFragment extends Fragment implements OnClickListener {
	private View view;
	private Context mContext;
	private ImageView mWorkCertificate;
	private ImageView mDoBack;
	private TextView mHeadTitle;
	private Button mNext;
	private DimPopupWindow mPopupWindow;
	private Button mCamera;
	private Button mPhoto;
	private Button mCancle;
	private RegAddWorkCerFrgClickListener listener;
	public String workCertificatePath=null;
	
    public static final int OPEN_PHOTO_REQUESTCODE =  0x1;  
    private static final int PHOTO_CAPTURE = 0x2;
    /** 
     * 图片的target大小. 
     */
    private static final int target = 400;  
    private Uri certificateUri=null;
    @Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext=activity;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.fragment_register_add_work_certificate, null);
		findViewById(view);
		return view;
	}
	private void findViewById(View v) {
		// TODO Auto-generated method stub
		mWorkCertificate=(ImageView) v.findViewById(R.id.id_work_certificate);
		mDoBack=(ImageView)v.findViewById(R.id.iv_headBack);
		mHeadTitle=(TextView)v.findViewById(R.id.tv_headTitle);
		mNext=(Button)v.findViewById(R.id.btn_next_fragment);
		
		mDoBack.setVisibility(View.VISIBLE);
		mHeadTitle.setText("上岗证确认");
		mDoBack.setOnClickListener(this);
		mWorkCertificate.setOnClickListener(this);
		mNext.setOnClickListener(this);
	}
	protected void showPopupWindow() {
		mPopupWindow=new DimPopupWindow(mContext);
		View contentView=LayoutInflater.from(mContext).inflate(R.layout.layout_dim_pop_add_idcard, null);
		initView(contentView);
		mPopupWindow.setContentView(contentView);
		mPopupWindow.setInAnimation(R.anim.register_add_idcard_in);
		mPopupWindow.showAtLocation(((Activity) mContext).getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
	}
	private void initView(View contentView) {
		mCamera=(Button) contentView.findViewById(R.id.item_popupwindows_camera);
		mPhoto=(Button) contentView.findViewById(R.id.item_popupwindows_Photo);
		mCancle=(Button) contentView.findViewById(R.id.item_popupwindows_cancel);
		mCamera.setOnClickListener(this);
		mPhoto.setOnClickListener(this);
		mCancle.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_work_certificate:
			showPopupWindow();
			break;
		case R.id.item_popupwindows_camera:
			mPopupWindow.dismiss();
			openCamera();
			break;
		case R.id.item_popupwindows_Photo:
			mPopupWindow.dismiss();
			openPhoto();
			break;
		case R.id.item_popupwindows_cancel:
			mPopupWindow.dismiss();
			break;
		case R.id.iv_headBack:
			getActivity().onBackPressed();
			break;
		case R.id.btn_next_fragment:
			if(listener!=null){
				listener.onRegAddWorkCerFrgClick();
			}
			break;
		default:
			break;
		}
	}
	private void openPhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);  
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  
                "image/*");  
        startActivityForResult(intent, OPEN_PHOTO_REQUESTCODE);
	}
	private File outFile;
	private Uri cameraUri;
	
	/**
	 * 打开相机
	 */
	private void openCamera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		File dir=mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		if(!dir.exists()){
			dir.mkdirs();
		}
		outFile=new File(dir,"workcitificate"+".jpg");
		cameraUri=Uri.fromFile(outFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,cameraUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
		startActivityForResult(intent, PHOTO_CAPTURE);
	}
	private String getPhotoPath(Uri imageUri){
		ContentResolver resolver=mContext.getContentResolver();
		String[] proj={MediaStore.Images.Media.DATA};
		Cursor cursor=resolver.query(imageUri, proj, null, null, null);
		int columIndex=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		if(cursor.moveToNext()){
			return cursor.getString(columIndex);
		}
		return null;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		String sdStatus = Environment.getExternalStorageState();
		if(!sdStatus.equals(Environment.MEDIA_MOUNTED)){
			Log.i("内存卡错误", "请检查您的内存卡");
			return ;
		}
		switch (requestCode) {
		case OPEN_PHOTO_REQUESTCODE:
			if(resultCode == Activity.RESULT_OK){  
                //如果用这个方法，Options为null时候，就是默认decode会出现oom哦.  
                //Bitmap bm = ImageCacheUtil.decode(null, null,   
                //      ImageCacheDemoActivity.this, data.getData(), null);  
                  
                //这里调用这个方法就不会oom.屌丝们就用这个方法吧.  
                Bitmap bm = ImageCacheUtils.getResizedBitmap(null, null,   
                        mContext, data.getData(), target, false);  
                mWorkCertificate.setImageBitmap(bm);
                certificateUri=data.getData();
                workCertificatePath =getPhotoPath(certificateUri);
            }
			break;
		case PHOTO_CAPTURE:
			if(resultCode == Activity.RESULT_CANCELED){
				
			}
			if(resultCode == Activity.RESULT_OK){
				BitmapFactory.Options op = new BitmapFactory.Options();
				Bitmap bmp=BitmapFactory.decodeFile(outFile.getAbsolutePath());
				int width = bmp.getWidth();
				int height = bmp.getHeight();
				// 设置想要的大小
				int newWidth = 480;
				int newHeight = 640;
				// 计算缩放比例
				float scaleWidth = ((float) newWidth) / width;
				float scaleHeight = ((float) newHeight) / height;
				// 取得想要缩放的matrix参数
				Matrix matrix = new Matrix();
				matrix.postScale(scaleWidth, scaleHeight);
				// 得到新的图片
				bmp = Bitmap.createBitmap(bmp, 0, 0, width, height,
						matrix, true);
				// canvas.drawBitmap(bitMap, 0, 0, paint)
				// 防止内存溢出
				op.inSampleSize = 4; // 这个数字越大,图片大小越小.
				Bitmap pic = null;
				pic = BitmapFactory.decodeFile(outFile.getAbsolutePath(), op);
				mWorkCertificate.setImageBitmap(pic);
				workCertificatePath=outFile.getAbsolutePath();
				certificateUri = cameraUri;
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	public boolean isAllNotNull(){
		if(certificateUri!=null){
			return true;
		}else{
			Utilities.showToast("你还没有选择证件", mContext);
			return false;
		}
	}
	public void setRegAddWorkCerFrgClickListener(RegAddWorkCerFrgClickListener listener){
		this.listener=listener;
	}
	public interface RegAddWorkCerFrgClickListener{
		void onRegAddWorkCerFrgClick();
	}
}