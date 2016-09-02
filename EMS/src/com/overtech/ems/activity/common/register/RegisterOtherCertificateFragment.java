package com.overtech.ems.activity.common.register;

import java.io.File;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.utils.AppUtils;
import com.overtech.ems.utils.ImageUtils;
import com.overtech.ems.utils.Logr;
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.popwindow.DimPopupWindow;

public class RegisterOtherCertificateFragment extends Fragment implements
		OnClickListener {
	private View view;
	private Context mContext;
	private GridLayout gridLayout;
	private Button btAddImg;
	private DimPopupWindow mPopupWindow;
	private Button mCamera;
	private Button mPhoto;
	private Button mCancle;
	private ImageView mDoback;
	private TextView mHeadTitle;
	private Button mNext;
	private RegOthCerFrgListener listener;
	/**
	 * 记录已经选择过的图片路径
	 */
	public String[] paths = new String[5];
	public static final int SELECT_PICK = 0x1;
	public static final int SELECT_PICK_KITKAT = 0x3;
	private static final int PHOTO_CAPTURE = 0x2;

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
			// String fileStr=savedInstanceState.getString("outFile");
			// if(fileStr!=null){
			// outFile=new File(fileStr);
			// otherCertificateUri=Uri.fromFile(outFile);
			// }
			// otherCertificatePath=savedInstanceState.getString("otherCertificatePath");
		}
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
		gridLayout = (GridLayout) view.findViewById(R.id.gridLayout);
		btAddImg = (Button) view.findViewById(R.id.bt_add_img);

		mDoback.setVisibility(View.VISIBLE);
		mDoback.setOnClickListener(this);
		mHeadTitle.setText("其他信息");
		btAddImg.setOnClickListener(this);
		mNext.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_add_img:
			if (gridLayout.getChildCount() >= 5) {
				Utilities.showToast("其他证书最多选择5张", getActivity());
			} else {
				showPopupWindow();
			}
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
		outFile = new File(dir, "otherCertificate" + (int)(Math.random() * 100)
				+ ".jpg");
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

				// otherCertificateUri = data.getData();
				// otherCertificatePath = ImageUtils.getPath(getActivity(),
				// otherCertificateUri);
				// mOtherCertificate.setImageBitmap(ImageUtils
				// .getSmallBitmap(otherCertificatePath));

				ImageView newImg = new ImageView(getActivity());
				paths[gridLayout.getChildCount()] = ImageUtils.getPath(
						getActivity(), data.getData());
				newImg.setTag(gridLayout.getChildCount());// 用于标记图片的path，
				newImg.setScaleType(ScaleType.CENTER_CROP);
				newImg.setImageBitmap(ImageUtils.getSmallBitmap(ImageUtils
						.getPath(getActivity(), data.getData())));
				newImg.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int index = (Integer) v.getTag();
						for (int i = index; i < paths.length; i++) {
							if (i != paths.length - 1) {// 防止角标越界
								paths[i] = paths[i + 1];// 当删除了gridlayout中的某一个item
														// 就将后面的路径全部补上一个位置
							} else {
								paths[i] = null;
							}
						}
						gridLayout.removeView(v);
						for (int i = 0; i < gridLayout.getChildCount(); i++) {
							gridLayout.getChildAt(i).setTag(i);// 将gridLayout中现存的child的tag更新，方便删除图片时不会弄错路径
						}
					}
				});
				gridLayout.addView(newImg, Utilities.dp2px(getActivity(), 110),
						Utilities.dp2px(getActivity(), 110));

			}
			break;
		case PHOTO_CAPTURE:
			if (resultCode == Activity.RESULT_CANCELED) {
			}
			if (resultCode == Activity.RESULT_OK) {
				// otherCertificateUri = cameraUri;
				// otherCertificatePath = outFile.getAbsolutePath();
				// mOtherCertificate.setImageBitmap(ImageUtils
				// .getSmallBitmap(otherCertificatePath));
				ImageView newImg = new ImageView(getActivity());
				paths[gridLayout.getChildCount()] = ImageUtils.getPath(
						getActivity(), cameraUri);
				newImg.setScaleType(ScaleType.CENTER_CROP);
				newImg.setTag(gridLayout.getChildCount());
				newImg.setImageBitmap(ImageUtils.getSmallBitmap(ImageUtils
						.getPath(getActivity(), cameraUri)));
				newImg.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int index = (Integer) v.getTag();
						File delF = new File(paths[index]);
						boolean del = delF.delete();
						Logr.e("文件删除状态==" + del);
						for (int i = index; i < paths.length; i++) {
							if (i != paths.length - 1) {// 防止角标越界
								paths[i] = paths[i + 1];// 当删除了gridlayout中的某一个item
														// 就将后面的路径全部补上一个位置
							} else {
								paths[i] = null;
							}
						}
						gridLayout.removeView(v);
						for (int i = 0; i < gridLayout.getChildCount(); i++) {
							gridLayout.getChildAt(i).setTag(i);// 将gridLayout中现存的child的tag更新，方便删除图片时不会弄错路径
						}
					}
				});
				gridLayout.addView(newImg, Utilities.dp2px(getActivity(), 110),
						Utilities.dp2px(getActivity(), 110));
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
		// outState.putString("outFile",
		// outFile==null?null:outFile.getAbsolutePath());
		// outState.putString("otherCertificatePath", otherCertificatePath);
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewStateRestored(savedInstanceState);
		// if(otherCertificateUri!=null){
		// mOtherCertificate.setImageBitmap(ImageUtils.getSmallBitmap(ImageUtils.getPath(getActivity(),
		// otherCertificateUri)));
		// }
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		for (String path : paths) {
			Logr.e("已选择的图片的路径==" + path);
		}
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
