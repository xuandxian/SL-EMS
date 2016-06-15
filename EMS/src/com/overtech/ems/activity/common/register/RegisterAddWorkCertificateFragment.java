package com.overtech.ems.activity.common.register;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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

public class RegisterAddWorkCertificateFragment extends Fragment implements
		OnClickListener {
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
	public String workCertificatePath = null;

	/**
	 * 打开本地相册的requestcode.
	 */
	public final int SELECT_PICK = 0x3;
	/**
	 * android kitkat 版本打开图册的requestcode
	 */
	public final int SELECT_PICK_KITKAT = 0x4;
	private static final int PHOTO_CAPTURE = 0x2;
	private Uri certificateUri = null;

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
				R.layout.fragment_register_add_work_certificate, null);
		findViewById(view);
		return view;
	}

	private void findViewById(View v) {
		// TODO Auto-generated method stub
		mWorkCertificate = (ImageView) v.findViewById(R.id.id_work_certificate);
		mDoBack = (ImageView) v.findViewById(R.id.iv_headBack);
		mHeadTitle = (TextView) v.findViewById(R.id.tv_headTitle);
		mNext = (Button) v.findViewById(R.id.btn_next_fragment);

		mDoBack.setVisibility(View.VISIBLE);
		mHeadTitle.setText("上岗证确认");
		mDoBack.setOnClickListener(this);
		mWorkCertificate.setOnClickListener(this);
		mNext.setOnClickListener(this);
	}

	protected void showPopupWindow() {
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
			/*
			 * if(certificateUri==null){上岗证证书不必非得上传
			 * Utilities.showToast("您还没有选择证件", mContext); return; }
			 */
			if (listener != null) {
				listener.onRegAddWorkCerFrgClick();
			}
			break;
		default:
			break;
		}
	}

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
		outFile = new File(dir, "workcitificate" + ".jpg");
		cameraUri = Uri.fromFile(outFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
		startActivityForResult(intent, PHOTO_CAPTURE);
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
				certificateUri = data.getData();
				workCertificatePath = ImageUtils.getPath(getActivity(),
						data.getData());
				mWorkCertificate.setImageBitmap(ImageUtils
						.getSmallBitmap(workCertificatePath));
			}
			break;

		case PHOTO_CAPTURE:
			if (resultCode == Activity.RESULT_CANCELED) {

			}
			if (resultCode == Activity.RESULT_OK) {
				workCertificatePath = outFile.getAbsolutePath();
				mWorkCertificate.setImageBitmap(ImageUtils
						.getSmallBitmap(workCertificatePath));
				certificateUri = cameraUri;
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void setRegAddWorkCerFrgClickListener(
			RegAddWorkCerFrgClickListener listener) {
		this.listener = listener;
	}

	public interface RegAddWorkCerFrgClickListener {
		void onRegAddWorkCerFrgClick();
	}
}
