package com.overtech.ems.activity.common.fragment;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.overtech.ems.R;
import com.overtech.ems.utils.ImageCacheUtils;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.popwindow.DimPopupWindow;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class RegisterAddIdCardFragment extends Fragment implements OnClickListener {
	private Context mContext;
	private View view;
	private ImageView mIdCardFront;
	private ImageView mIdCardOpposite;
	private DimPopupWindow mPopupWindow;
	private Button mCamera;
	private Button mPhoto;
	private Button mCancle;
	private Button mStartUpload1;
	private Button mStartUpload2;
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
		return view;
	}
	private void init() {
		mIdCardFront.setOnClickListener(this);
		mIdCardOpposite.setOnClickListener(this);
		mStartUpload1.setOnClickListener(this);
		mStartUpload2.setOnClickListener(this);
	}
	private void findViewById(View v) {
		mIdCardFront=(ImageView)v. findViewById(R.id.iv_idcard_front);
		mIdCardOpposite=(ImageView) v.findViewById(R.id.iv_idcard_opposite);
		mStartUpload1=(Button)v.findViewById(R.id.bt_front_start);
		mStartUpload2=(Button)v.findViewById(R.id.bt_opposite_start);
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
		case R.id.bt_front_start:
//			Utilities.showToast("开始上传1", mContext);
			upLoading();
			break;
		case R.id.bt_opposite_start:
			Utilities.showToast("开始上传2", mContext);
			break;
		default:
			break;
		}
	}
	/**
	 * 上传图片
	 */
	private void upLoading(){
		ContentResolver resolver=mContext.getContentResolver();
		String[] proj={MediaStore.Images.Media.DATA};
		Cursor cursor=resolver.query(frontUri, proj, null, null, null);
		int columIndex=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		if(cursor.moveToNext()){
			String path=cursor.getString(columIndex);
			OkHttpClient mOkHttpClient=new OkHttpClient();
			File file=new File(path);
			Log.e("====文件的大小====", file.length()+"");
			RequestBody fileBody=RequestBody.create(MediaType.parse("image/jpeg"), file);
			RequestBody requestBody=new MultipartBuilder()
			.type(MultipartBuilder.FORM)
			.addPart(Headers.of("Content-Disposition","form-data;name=\"mFile\";filename=\"beautiful.jpg\""),fileBody)
			.build();
			Request request=new Request.Builder().url("http://192.168.1.112/slems/api/system/employee/login.action").post(requestBody).build();
			Call call=mOkHttpClient.newCall(request);
			call.enqueue(new Callback() {
				
				@Override
				public void onResponse(Response arg0) throws IOException {
					// TODO Auto-generated method stub
					System.out.println("+++++++++++++++++++++++");
				}
				
				@Override
				public void onFailure(Request arg0, IOException arg1) {
					System.out.println("--------------------------");
				}
			});
			Log.e("==我是图片的路径==", path);
		}
	}
	private void openCamera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, frontUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
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
                	mStartUpload1.setVisibility(View.VISIBLE);
                }else if(currentState==1){
                	mIdCardOpposite.setImageBitmap(bm);
                	oppositeUri=data.getData();
                	mStartUpload2.setVisibility(View.VISIBLE);
                }
                
            }else{
            	if(currentState==0){
            		mStartUpload1.setVisibility(View.GONE);
            	}else if(currentState==1){
            		mStartUpload2.setVisibility(View.GONE);
            	}
            }
			break;
		case PHOTO_CAPTURE:
			if(resultCode == Activity.RESULT_OK){
				Bitmap pic = ImageCacheUtils.getResizedBitmap(null, null,   
	                    mContext, data.getData(), target, false);
				if(currentState==0){
					frontUri=data.getData();
	            	mIdCardFront.setImageBitmap(pic);  
	            }else if(currentState==1){
	            	mIdCardOpposite.setImageBitmap(pic);
	            	oppositeUri=data.getData();
	            }
				mStartUpload2.setVisibility(View.VISIBLE);
			}else{
				mStartUpload2.setVisibility(View.GONE);
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
