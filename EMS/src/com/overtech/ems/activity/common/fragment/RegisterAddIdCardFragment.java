package com.overtech.ems.activity.common.fragment;

import java.io.File;
import java.net.URI;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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

import com.overtech.ems.R;
import com.overtech.ems.utils.ImageCacheUtils;
import com.overtech.ems.utils.SharePreferencesUtils;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.popwindow.DimPopupWindow;

public class RegisterAddIdCardFragment extends Fragment implements OnClickListener {
	private Context mContext;
	private View view;
	private ImageView mIdCardFront;
	private ImageView mIdCardOpposite;
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
	 * 打开相册或者图册返回的图片的地址
	 */
	private Uri frontUri=null;
	private Uri oppositeUri=null;
	/** 
     * 图片的target大小. 
     */
    private static final int target = 400;  
    
    private SharePreferencesUtils sp;
    private Editor editor;
    private final String IDCardFront="frontIdcard";
    private final String IDCardOpposite="oppositeIdCard";
    @Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext=activity;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.fragment_register_add_id_card, null);
		findViewById(view);
		init();
		sp=SharePreferencesUtils.getInstance();
		editor=sp.edit();
		return view;
	}
	private void init() {
		mIdCardFront.setOnClickListener(this);
		mIdCardOpposite.setOnClickListener(this);
		
	}
	private void findViewById(View v) {
		mIdCardFront=(ImageView)v. findViewById(R.id.iv_idcard_front);
		mIdCardOpposite=(ImageView) v.findViewById(R.id.iv_idcard_opposite);
	}
	/**
	 * 记录身份证点击的状态
	 * */
	private int currentState;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_idcard_front:
			currentState=0;
			showPopupWindow(v);
			break;
		case R.id.iv_idcard_opposite:
			currentState=1;
			showPopupWindow(v);
			break;
		case R.id.item_popupwindows_camera:
			Utilities.showToast("你点击了照相机", mContext);
			openCamera();
			mPopupWindow.dismiss();
			break;
		case R.id.item_popupwindows_Photo:
			Utilities.showToast("你点击了相册", mContext);
			openPhotos();
			mPopupWindow.dismiss();
			break;
		case R.id.item_popupwindows_cancel:
			Utilities.showToast("你点击了取消", mContext);
			mPopupWindow.dismiss();
			break;
		
		
		default:
			break;
		}
	}
	/**
	 * 获取拍照后的图片的路径
	 */
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
	private File outFile;
	private Uri cameraUri;
	/**
	 * 打开照相机
	 */
	private void openCamera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		File dir=mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		if(!dir.exists()){
			dir.mkdirs();
		}
		if(currentState==0){
			outFile=new File(dir,"frontIdcard"+".jpg");
		}else if(currentState==1){
			outFile=new File(dir,"oppositeIdcard"+".jpg");
		}
		
		cameraUri=Uri.fromFile(outFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
		startActivityForResult(intent, PHOTO_CAPTURE);
	}

	private void openPhotos() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);  
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  
                "image/*");  
        startActivityForResult(intent, OPEN_PHOTO_REQUESTCODE);
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
                if(currentState==0){
                	mIdCardFront.setImageBitmap(bm);  
                	//记录打开相册获取的图片的uri，赋给
                	frontUri=data.getData();
                	String path=getPhotoPath(frontUri);
                	editor.putString(IDCardFront, path);
                }else if(currentState==1){
                	mIdCardOpposite.setImageBitmap(bm);
                	oppositeUri=data.getData();
                	String path=getPhotoPath(oppositeUri);
                	editor.putString(IDCardOpposite, path);
                }
                editor.commit();
                
            }
			break;
		case PHOTO_CAPTURE:
			if(resultCode==Activity.RESULT_CANCELED){
				//调用相机后，如果没有拍照就返回了，就把相应位置的uri置为null,原因：调用相机拍照后生成的照片我指定有位置，所以不能用那个uri来确定当前图片的uri;
				//调用相机没有拍照返回后，清空
				
			}
			if(resultCode == Activity.RESULT_OK){
				
				Log.e("==这里是用来判断intent值===", data.toString());
				
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
				if(currentState==0){
					frontUri=cameraUri;
	            	mIdCardFront.setImageBitmap(pic);  
	            	editor.putString(IDCardFront, outFile.getAbsolutePath());
	            }else if(currentState==1){
	            	mIdCardOpposite.setImageBitmap(pic);
	            	oppositeUri=cameraUri;
	            	editor.putString(IDCardOpposite, outFile.getAbsolutePath());
	            }
				editor.commit();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void showPopupWindow(View v) {
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
	public boolean isAllNotNull(){
		if(frontUri!=null&&oppositeUri!=null){
			return true;
		}else{
			Utilities.showToast("您还没有选择证件", mContext);
			return false;
		}
	}
}
