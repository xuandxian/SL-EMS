package com.overtech.ems.activity.common.register;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
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
import com.overtech.ems.utils.ImageUtils;
import com.overtech.ems.widget.popwindow.DimPopupWindow;

public class RegisterOtherCertificateFragment extends Fragment implements
		OnClickListener {
	private View view;
	private Context mContext;
	private ImageView mOtherCertificate;
	private DimPopupWindow mPopupWindow;
	private Button mCamera;
	private Button mPhoto;
	private Button mCancle;
	private ImageView mDoback;
	private TextView mHeadTitle;
	private Button mNext;
	private RegOthCerFrgListener listener;
	public String otherCertificatePath = null;
	public static final int SELECT_PICK = 0x1;
	public static final int SELECT_PICK_KITKAT = 0x3;
	private static final int PHOTO_CAPTURE = 0x2;
	private Uri otherCertificateUri = null;
	private static final int target = 400;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(
				R.layout.fragment_register_add_other_certificate, null);
		findViewById(view);
		return view;
	}

	private void findViewById(View v) {
		// TODO Auto-generated method stub
		mDoback = (ImageView) v.findViewById(R.id.iv_headBack);
		mHeadTitle = (TextView) v.findViewById(R.id.tv_headTitle);
		mNext = (Button) v.findViewById(R.id.btn_next_fragment);
		mOtherCertificate = (ImageView) view.findViewById(R.id.id_card_1);

		mDoback.setVisibility(View.VISIBLE);
		mDoback.setOnClickListener(this);
		mHeadTitle.setText("其他信息");
		mOtherCertificate.setOnClickListener(this);
		mNext.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_card_1:
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
			if (listener != null) {
				listener.onRegOthCerFrgClick();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 打开相册
	 */
	private void openPhoto() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/jpeg");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			startActivityForResult(intent, SELECT_PICK_KITKAT);
		} else {
			startActivityForResult(intent, SELECT_PICK);
		}
	}

	private File outFile;
	private Uri cameraUri;

	/**
	 * 打开相机
	 */
	private void openCamera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		outFile = new File(dir, "otherCertificate" + ".jpg");
		cameraUri = Uri.fromFile(outFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
		startActivityForResult(intent, PHOTO_CAPTURE);
	}

	private void showPopupWindow() {
		if (mPopupWindow == null) {
			mPopupWindow = new DimPopupWindow(mContext);
			View contentView = LayoutInflater.from(mContext).inflate(
					R.layout.layout_dim_pop_add_idcard, null);
			initView(contentView);
			mPopupWindow.setContentView(contentView);
			mPopupWindow.setInAnimation(R.anim.register_add_idcard_in);
			mPopupWindow.showAtLocation(((Activity) mContext).getWindow()
					.getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
		} else {
			mPopupWindow.showAtLocation(((Activity) mContext).getWindow()
					.getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
		}
	}

	private void initView(View contentView) {
		mCamera = (Button) contentView
				.findViewById(R.id.item_popupwindows_camera);
		mPhoto = (Button) contentView
				.findViewById(R.id.item_popupwindows_Photo);
		mCancle = (Button) contentView
				.findViewById(R.id.item_popupwindows_cancel);
		mCamera.setOnClickListener(this);
		mPhoto.setOnClickListener(this);
		mCancle.setOnClickListener(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			Log.i("内存卡错误", "请检查您的内存卡");
			return;
		}
		switch (requestCode) {
		case SELECT_PICK:
		case SELECT_PICK_KITKAT:
			if (resultCode == Activity.RESULT_OK) {
				// 如果用这个方法，Options为null时候，就是默认decode会出现oom哦.
				// Bitmap bm = ImageCacheUtil.decode(null, null,
				// ImageCacheDemoActivity.this, data.getData(), null);

				// 这里调用这个方法就不会oom.屌丝们就用这个方法吧.

				otherCertificateUri = data.getData();
				otherCertificatePath = ImageUtils.getPath(getActivity(),
						otherCertificateUri);
				mOtherCertificate.setImageBitmap(ImageUtils
						.getSmallBitmap(otherCertificatePath));
			}
			break;
		case PHOTO_CAPTURE:
			if (resultCode == Activity.RESULT_CANCELED) {
			}
			if (resultCode == Activity.RESULT_OK) {
				otherCertificateUri = cameraUri;
				otherCertificatePath = outFile.getAbsolutePath();
				mOtherCertificate.setImageBitmap(ImageUtils
						.getSmallBitmap(otherCertificatePath));
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/*
	 * public boolean isAllNotNull(){
	 * 
	 * if(otherCertificateUri!=null){ return true; }else{
	 * Utilities.showToast("你还没有选择证件", mContext); return false; } }
	 * 不需要这个，其他证书可有可无
	 */
	public void setRegOthFrgListener(RegOthCerFrgListener listener) {
		this.listener = listener;
	}

	public interface RegOthCerFrgListener {
		void onRegOthCerFrgClick();
	}
}
