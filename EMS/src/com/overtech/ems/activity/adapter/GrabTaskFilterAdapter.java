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
import java.util.ArrayList;

public class GrabTaskFilterAdapter extends BaseAdapter {

	private int[] image;
	private boolean isChoice[];
	private Context context;

	public GrabTaskFilterAdapter(int[] im, Context context) {
		this.image = im;
		isChoice = new boolean[im.length];
		for (int i = 0; i < im.length; i++) {
			isChoice[i] = false;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_gridview_filter, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.image_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.imageView.setImageDrawable(getView(position));
		return convertView;
	}

	private LayerDrawable getView(int position) {

		Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(image[position])).getBitmap();
		Bitmap bitmap2 = null;
		LayerDrawable la = null;
		if (isChoice[position] == true) {
			bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_map_lable);
		}
		if (bitmap2 != null) {
			Drawable[] array = new Drawable[2];
			array[0] = new BitmapDrawable(bitmap);
			array[1] = new BitmapDrawable(bitmap2);
			la = new LayerDrawable(array);
			la.setLayerInset(0, 0, 0, 0, 0); // 第几张图离各边的间距
			la.setLayerInset(1, 0, 10, 10, 0);
		} else {
			Drawable[] array = new Drawable[1];
			array[0] = new BitmapDrawable(bitmap);
			la = new LayerDrawable(array);
			la.setLayerInset(0, 0, 0, 0, 0);
		}
		return la; // 返回叠加后的图
	}

	public void choiceStatus(int position) {
		isChoice[position] = isChoice[position] == true ? false : true;
		this.notifyDataSetChanged();
	}
	public ArrayList<String> getTypePositon(int[] im){
		ArrayList<String> list=new ArrayList<String>();
		for (int i = 0; i < im.length; i++){
			if (isChoice[i]==true){
				list.add(String.valueOf(i));
			}
		}
		return list;
	}
	static class ViewHolder {
		ImageView imageView;
	}
}
