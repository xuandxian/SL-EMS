package com.overtech.ems.activity.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtech.ems.R;
import com.overtech.ems.picasso.Transformation;
import com.overtech.ems.utils.ImageUtils;
import com.overtech.ems.widget.bitmap.ImageLoader;

public class PersonalPartnersAdapter extends Adapter<ViewHolder> {
	private OnPartnersDeleteListener listener;
	private List<Map<String, Object>> datas;
	private Context ctx;
	private final int FOOTER = 0x001;
	private final int NORMAL = 0x002;

	public PersonalPartnersAdapter(Context ctx, List<Map<String, Object>> datas) {
		this.datas = datas;
		this.ctx = ctx;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return datas == null ? 1 : datas.size() + 1;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		if (holder instanceof NormalViewHolder) {
			NormalViewHolder nvh = (NormalViewHolder) holder;
			Map<String, Object> data = datas.get(position);
			ImageLoader.getInstance().displayImage(
					data.get("avator").toString(), nvh.ivAvator,
					R.drawable.icon_personal_my, R.drawable.icon_personal_my,
					Config.RGB_565, new Transformation() {

						@Override
						public Bitmap transform(Bitmap source) {
							// TODO Auto-generated method stub
							return ImageUtils.toRoundBitmap(source);
						}

						@Override
						public String key() {
							// TODO Auto-generated method stub
							return null;
						}
					});
			nvh.tvName.setText(data.get("name").toString());
			nvh.tvPhone.setText(data.get("phone").toString());
			nvh.ivDelete.setTag(position);
			nvh.ivDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (listener != null) {
						listener.delete(v);
					}
				}
			});
		} else {
			FooterViewHolder fvh = (FooterViewHolder) holder;
			fvh.tvMessage.setText("暂无更多搭档");
		}
	}

	public void setData(List<Map<String, Object>> datas) {
		this.datas = datas;
	}

	public Map<String, Object> getData(int position) {
		return datas.get(position);
	}

	public void deleteData(int position) {
		datas.remove(position);
		notifyItemRemoved(position);
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if (position == getItemCount() - 1) {
			return FOOTER;
		} else {
			return NORMAL;
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// TODO Auto-generated method stub
		if (viewType == FOOTER) {
			FooterViewHolder fvh = new FooterViewHolder(LayoutInflater
					.from(ctx).inflate(R.layout.item_footer_textview,
							parent, false));
			return fvh;
		} else {
			NormalViewHolder vh = null;
			vh = new NormalViewHolder(LayoutInflater.from(ctx)
					.inflate(R.layout.item_recyclerview_personal_partners,
							parent, false));
			return vh;

		}
	}

	public class NormalViewHolder extends ViewHolder {
		ImageView ivAvator;
		AppCompatTextView tvName;
		AppCompatTextView tvPhone;
		ImageView ivDelete;

		public NormalViewHolder(View v) {
			super(v);
			// TODO Auto-generated constructor stub
			ivAvator = (ImageView) v.findViewById(R.id.iv_avatar);
			tvName = (AppCompatTextView) v.findViewById(R.id.tv_name);
			tvPhone = (AppCompatTextView) v.findViewById(R.id.tv_phone);
			ivDelete = (ImageView) v.findViewById(R.id.iv_delete);
		}

	}

	public class FooterViewHolder extends ViewHolder {
		TextView tvMessage;

		public FooterViewHolder(View v) {
			super(v);
			// TODO Auto-generated constructor stub
			tvMessage = (TextView) v.findViewById(R.id.text);
		}

	}

	public void setOnPartnerDeleteListener(OnPartnersDeleteListener listener) {
		this.listener = listener;
	}

	public interface OnPartnersDeleteListener {
		void delete(View v);
	}
}
