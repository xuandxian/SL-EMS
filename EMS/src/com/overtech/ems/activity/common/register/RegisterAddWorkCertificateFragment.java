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
import android.support.annotation.Nullable;
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
import com.overtech.ems.utils.Logr;
import com.overtech.ems.widget.popwindow.DimPopupWindow;

public class RegisterAddWorkCertificateFragment extends Fragment implements
		OnClickListener {
	private View view;
	private Context mContext;
	private ImageView workCertificate1;
	private ImageView workCertificate2;
	private ImageView mDoBack;
	private TextView mHeadTitle;
	private Button mNext;
	private DimPopupWindow mPopupWindow;
	private Button mCamera;
	private Button mPhoto;
	private Button mCancle;
	private RegAddWorkCerFrgClickListener listener;
	public String workCertificatePath1 = null;
	public String workCertificatePath2 = null;
	public static final int CONE = 0x0010;
	public static final int CTWO = 0x0020;
	/**
	 * 选择的是照片
	 */
	private int curSel;

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
	private Uri certificateUri2 = null;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			String fileStr = savedInstanceState.getString("outFile");
			String file2Str = savedInstanceState.getString("outFile2");
			if (fileStr != null) {
				outFile = new File(fileStr);
				certificateUri = Uri.fromFile(outFile);
			}
			if (file2Str != null) {
				outFile2 = new File(file2Str);
				certificateUri2 = Uri.fromFile(outFile2);
			}
			workCertificatePath1 = savedInstanceState
					.getString("workCertificatePath");
			workCertificatePath2 = savedInstanceState
					.getString("workCertificatePath2");
			curSel = savedInstanceState.getInt("curSel");
		}
		view = inflater.inflate(
				R.layout.fragment_register_add_work_certificate, null);
		findViewById(view);
		return view;
	}

	private void findViewById(View v) {
		// TODO Auto-generated method stub
		workCertificate1 = (ImageView) v.findViewById(R.id.id_work_certificate);
		workCertificate2 = (ImageView) v
				.findViewById(R.id.id_work_certificate2);
		mDoBack = (ImageView) v.findViewById(R.id.iv_headBack);
		mHeadTitle = (TextView) v.findViewById(R.id.tv_headTitle);
		mNext = (Button) v.findViewById(R.id.btn_next_fragment);

		mDoBack.setVisibility(View.VISIBLE);
		mHeadTitle.setText("上岗证确认");
		mDoBack.setOnClickListener(this);
		workCertificate1.setOnClickListener(this);
		workCertificate2.setOnClickListener(this);
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
			Logr.e("==registerAddWorkCertificate=="+"one");
			curSel = CONE;
			showPopupWindow();
			break;
		case R.id.id_work_certificate2:
			Logr.e("==registerAddWorkCertificate=="+"two");
			curSel = CTWO;
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
	private File outFile2;
	private Uri cameraUri;
	private Uri cameraUri2;

	/**
	 * 打开相机
	 */
	private void openCamera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (curSel == CONE) {
			outFile = new File(dir, "workcitificate1" + ".jpg");
			cameraUri = Uri.fromFile(outFile);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
			startActivityForResult(intent, PHOTO_CAPTURE);
		} else if (curSel == CTWO) {
			outFile2 = new File(dir, "workcitificate2" + ".jpg");
			cameraUri2 = Uri.fromFile(outFile2);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri2); // 这样就将文件的存储方式和uri指定到了Camera应用中
			startActivityForResult(intent, PHOTO_CAPTURE);
		}
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
				if (curSel == CONE) {
					certificateUri = data.getData();
					workCertificatePath1 = ImageUtils.getPath(getActivity(),
							data.getData());
					workCertificate1.setImageBitmap(ImageUtils
							.getSmallBitmap(workCertificatePath1));
				} else if (curSel == CTWO) {
					certificateUri2 = data.getData();
					workCertificatePath2 = ImageUtils.getPath(getActivity(),
							data.getData());
					workCertificate2.setImageBitmap(ImageUtils
							.getSmallBitmap(workCertificatePath2));
				}
			}
			break;

		case PHOTO_CAPTURE:
			if (resultCode == Activity.RESULT_CANCELED) {

			}
			if (resultCode == Activity.RESULT_OK) {
				if (curSel == CONE) {
					workCertificatePath1 = outFile.getAbsolutePath();
					workCertificate1.setImageBitmap(ImageUtils
							.getSmallBitmap(workCertificatePath1));
					certificateUri = cameraUri;
				} else if (curSel == CTWO) {
					workCertificatePath2 = outFile2.getAbsolutePath();
					workCertificate2.setImageBitmap(ImageUtils
							.getSmallBitmap(workCertificatePath2));
					certificateUri2 = cameraUri2;
				}
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("outFile",
				outFile == null ? null : outFile.getAbsolutePath());
		outState.putString("workCertificatePath", workCertificatePath1);
		outState.putString("outFile2",
				outFile2 == null ? null : outFile2.getAbsolutePath());
		outState.putString("workCertificatePath2", workCertificatePath2);
		outState.putInt("curSel", curSel);
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewStateRestored(savedInstanceState);
		if (certificateUri != null) {
			workCertificate1.setImageBitmap(ImageUtils
					.getSmallBitmap(ImageUtils.getPath(getActivity(),
							certificateUri)));
		}
		if (certificateUri2 != null) {
			workCertificate2.setImageBitmap(ImageUtils
					.getSmallBitmap(ImageUtils.getPath(getActivity(),
							certificateUri2)));
		}
	}

	public void setRegAddWorkCerFrgClickListener(
			RegAddWorkCerFrgClickListener listener) {
		this.listener = listener;
	}

	public interface RegAddWorkCerFrgClickListener {
		void onRegAddWorkCerFrgClick();
	}
}
