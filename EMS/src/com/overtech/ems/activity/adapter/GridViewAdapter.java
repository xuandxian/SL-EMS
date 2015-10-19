package com.overtech.ems.activity.adapter;

import com.overtech.ems.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridViewAdapter extends BaseAdapter {

	private int[] image;
	private boolean isChice[];
	private Context context;

	public GridViewAdapter(int[] im, Context context) {
		this.image = im;
		isChice = new boolean[im.length];
		for (int i = 0; i < im.length; i++) {
			isChice[i] = false;
		}
		this.context = context;
	}

	@Override
	public int getCount() {
		return image.length;
	}

	@Override
	public Object getItem(int position) {
		return image[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int arg0, View v, ViewGroup arg2) {
		View view = v;
		GetView getView = null;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.item_gridview_filter, null);
			getView = new GetView();
			getView.imageView = (ImageView) view.findViewById(R.id.image_item);
			view.setTag(getView);
		} else {
			getView = (GetView) view.getTag();
		}
		getView.imageView.setImageDrawable(getView(arg0));

		return view;
	}

	static class GetView {
		ImageView imageView;
	}

	@SuppressWarnings("deprecation")
	private LayerDrawable getView(int post) {

		Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(
				image[post])).getBitmap();
		Bitmap bitmap2 = null;
		LayerDrawable la = null;
		if (isChice[post] == true) {
			bitmap2 = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.icon_map_lable);
		}
		if (bitmap2 != null) {
			Drawable[] array = new Drawable[2];
			array[0] = new BitmapDrawable(bitmap);
			array[1] = new BitmapDrawable(bitmap2);
			la = new LayerDrawable(array);
			la.setLayerInset(0, 0, 0, 0, 0); // 第几张图离各边的间距
			la.setLayerInset(1, 0, 65, 65, 0);
		} else {
			Drawable[] array = new Drawable[1];
			array[0] = new BitmapDrawable(bitmap);
			la = new LayerDrawable(array);
			la.setLayerInset(0, 0, 0, 0, 0);
		}
		return la; // 返回叠加后的图
	}

	public void chiceState(int post) {
		isChice[post] = isChice[post] == true ? false : true;
		this.notifyDataSetChanged();
	}

}
