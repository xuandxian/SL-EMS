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
import com.overtech.ems.utils.Utilities;
import com.overtech.ems.widget.popwindow.DimPopupWindow;

public class RegisterAddIdCardFragment extends Fragment implements
		OnClickListener {
	private Context mContext;
	private View view;
	private TextView mHeadTitle;
	private ImageView mDoBack;
	private Button mNext;
	private ImageView mIdCardFront;
	private ImageView mIdCardOpposite;
	private DimPopupWindow mPopupWindow;
	private Button mCamera;
	private Button mPhoto;
	private Button mCancle;
	private RegAddIdCardFrgClickListener listener;
	public String idCardFrontPath = null;
	public String idCardOppositePath = null;
	/**
	 * 打开本地相册的requestcode.
	 */
	public final int SELECT_PICK = 0x3;
	/**
	 * android kitkat 版本打开图册的requestcode
	 */
	public final int SELECT_PICK_KITKAT = 0x4;
	/**
	 * 打开照相机的requestcode.
	 */
	private final int PHOTO_CAPTURE = 0x2;

	/**
	 * 打开相册或者图册返回的图片的地址
	 */
	private Uri frontUri = null;
	private Uri oppositeUri = null;
	private File outFile;
	private Uri cameraUri;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_register_add_id_card, null);
		findViewById(view);
		init();
		return view;
	}

	private void init() {
		mHeadTitle.setText("身份证确认");
		mDoBack.setVisibility(View.VISIBLE);
		mDoBack.setOnClickListener(this);
		mIdCardFront.setOnClickListener(this);
		mIdCardOpposite.setOnClickListener(this);
		mNext.setOnClickListener(this);
	}

	private void findViewById(View v) {
		mHeadTitle = (TextView) v.findViewById(R.id.tv_headTitle);
		mDoBack = (ImageView) v.findViewById(R.id.iv_headBack);
		mNext = (Button) v.findViewById(R.id.btn_next_fragment);
		mIdCardFront = (ImageView) v.findViewById(R.id.iv_idcard_front);
		mIdCardOpposite = (ImageView) v.findViewById(R.id.iv_idcard_opposite);
	}

	/**
	 * 记录身份证点击的状态
	 * */
	private int currentState;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_idcard_front:
			currentState = 0;
			showPopupWindow(v);
			break;
		case R.id.iv_idcard_opposite:
			currentState = 1;
			showPopupWindow(v);
			break;
		case R.id.item_popupwindows_camera:
			openCamera();
			mPopupWindow.dismiss();
			break;
		case R.id.item_popupwindows_Photo:
			openPhotos();
			mPopupWindow.dismiss();
			break;
		case R.id.item_popupwindows_cancel:
			mPopupWindow.dismiss();
			break;
		case R.id.iv_headBack:
			getActivity().onBackPressed();
			break;
		case R.id.btn_next_fragment:
			if (frontUri == null) {
				Utilities.showToast("您还没有选择照片", mContext);
				return;
			}
			if (oppositeUri == null) {
				Utilities.showToast("您还没有选择照片", mContext);
				return;
			}
			if (listener != null) {
				listener.onRegAddIdCardFrgClick();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 打开照相机
	 */
	private void openCamera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (currentState == 0) {
			outFile = new File(dir, "frontIdcard" + ".jpg");
		} else if (currentState == 1) {
			outFile = new File(dir, "oppositeIdcard" + ".jpg");
		}

		cameraUri = Uri.fromFile(outFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
		startActivityForResult(intent, PHOTO_CAPTURE);
	}

	/**
	 * 打开相册
	 */
	private void openPhotos() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/jpeg");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			startActivityForResult(intent, SELECT_PICK_KITKAT);
		} else {
			startActivityForResult(intent, SELECT_PICK);
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
		case SELECT_PICK_KITKAT:
		case SELECT_PICK:
			if (resultCode == Activity.RESULT_OK) {
				// 如果用这个方法，Options为null时候，就是默认decode会出现oom哦.
				// Bitmap bm = ImageCacheUtil.decode(null, null,
				// ImageCacheDemoActivity.this, data.getData(), null);

				// 这里调用这个方法就不会oom.屌丝们就用这个方法吧.
				if (currentState == 0) {

					// 记录打开相册获取的图片的uri，赋给
					frontUri = data.getData();
					idCardFrontPath = ImageUtils.getPath(getActivity(),
							frontUri);
					mIdCardFront.setImageBitmap(ImageUtils
							.getSmallBitmap(idCardFrontPath));
				} else if (currentState == 1) {
					oppositeUri = data.getData();
					idCardOppositePath = ImageUtils.getPath(getActivity(),
							oppositeUri);
					mIdCardOpposite.setImageBitmap(ImageUtils
							.getSmallBitmap(idCardOppositePath));
				}

			}
			break;
		case PHOTO_CAPTURE:
			if (resultCode == Activity.RESULT_CANCELED) {
				// 调用相机后，如果没有拍照就返回了，就把相应位置的uri置为null,原因：调用相机拍照后生成的照片我指定有位置，所以不能用那个uri来确定当前图片的uri;
				// 调用相机没有拍照返回后，清空
			}
			if (resultCode == Activity.RESULT_OK) {
				if (currentState == 0) {
					frontUri = cameraUri;
					idCardFrontPath = outFile.getAbsolutePath();
					mIdCardFront.setImageBitmap(ImageUtils
							.getSmallBitmap(idCardFrontPath));
				} else if (currentState == 1) {
					oppositeUri = cameraUri;
					idCardOppositePath = outFile.getAbsolutePath();
					mIdCardOpposite.setImageBitmap(ImageUtils
							.getSmallBitmap(idCardOppositePath));
				}
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showPopupWindow(View v) {
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

	public void setRegAddIdCardClickListener(
			RegAddIdCardFrgClickListener listener) {
		this.listener = listener;
	}

	public interface RegAddIdCardFrgClickListener {
		void onRegAddIdCardFrgClick();
	}
}
