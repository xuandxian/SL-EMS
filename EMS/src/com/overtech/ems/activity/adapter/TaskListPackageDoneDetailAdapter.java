package com.overtech.ems.activity.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.overtech.ems.R;

public class TaskListPackageDoneDetailAdapter extends Adapter<ViewHolder> {
	private Context ctx;
	private List<Map<String, Object>> datas;
	private final int NORMAL = 0x001;
	private final int MORE = 0x002;
	private int state;
	private OnItemClickListener listener;

	public TaskListPackageDoneDetailAdapter(Context ctx,
			List<Map<String, Object>> datas) {
		this.ctx = ctx;
		this.datas = datas;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return datas == null ? 0 : datas.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		// TODO Auto-generated method stub
		if (holder instanceof NormalViewHolder) {
			NormalViewHolder vh = (NormalViewHolder) holder;
			Map<String, Object> data = datas.get(position);
			vh.tvElevatorAddress.setText(data.get("elevatorName").toString());
			if (data.get("workType").equals("0")) {
				vh.tvElevatorNo.setText(data.get("elevatorNo").toString()
						+ "（半月保）");
			} else if (data.get("workType").equals("1")) {
				vh.tvElevatorNo.setText(data.get("elevatorNo").toString()
						+ "（季度保）");
			} else if (data.get("workType").equals("2")) {
				vh.tvElevatorNo.setText(data.get("elevatorNo").toString()
						+ "（半年保）");
			} else {
				vh.tvElevatorNo.setText(data.get("elevatorNo").toString()
						+ "（年保）");
			}
			vh.tvElevatorBrand.setText(data.get("elevatorBrand").toString());
			vh.tvElevatorStorey.setText(data.get("storeySite").toString());
			if (data.get("canEvaluate").equals("1")) {
				vh.btEvaluate.setEnabled(true);
				vh.btEvaluate.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (listener != null) {
							listener.onButtonClick(v, position);
						}
					}
				});

			} else {
				vh.btEvaluate.setEnabled(false);
			}
		} else {
			MoreViewHolder vh = (MoreViewHolder) holder;
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
		// TODO Auto-generated method stub
		if (type == MORE) {
			MoreViewHolder moreVH = new MoreViewHolder(
					LayoutInflater.from(ctx).inflate(
							android.R.layout.simple_list_item_1, parent, false));
			return moreVH;
		} else {
			NormalViewHolder normalVH = new NormalViewHolder(LayoutInflater
					.from(ctx).inflate(
							R.layout.item_recyclerview_tasklist_done_detail,
							parent, false));
			return normalVH;
		}
	}

	public void setData(List<Map<String, Object>> datas) {
		this.datas = datas;
		notifyDataSetChanged();
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	public interface OnItemClickListener {
		void onButtonClick(View v, int position);
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return NORMAL;
		// if (position == getItemCount() - 1) {
		// return MORE;
		// } else {
		// return NORMAL;
		// }
	}

	class NormalViewHolder extends ViewHolder {
		AppCompatTextView tvElevatorAddress;
		AppCompatTextView tvElevatorNo;
		AppCompatTextView tvElevatorBrand;
		AppCompatTextView tvElevatorStorey;
		AppCompatButton btEvaluate;

		public NormalViewHolder(View v) {
			super(v);
			// TODO Auto-generated constructor stub
			tvElevatorAddress = (AppCompatTextView) v
					.findViewById(R.id.tv_address);
			tvElevatorNo = (AppCompatTextView) v
					.findViewById(R.id.tv_elevator_no);
			tvElevatorBrand = (AppCompatTextView) v
					.findViewById(R.id.tv_elevator_brand);
			tvElevatorStorey = (AppCompatTextView) v
					.findViewById(R.id.tv_elevator_storey);
			btEvaluate = (AppCompatButton) v.findViewById(R.id.bt_evaluate);
		}

	}

	class MoreViewHolder extends ViewHolder {
		TextView textView1;

		public MoreViewHolder(View v) {
			super(v);
			// TODO Auto-generated constructor stub
			textView1 = (TextView) v.findViewById(R.id.textView1);
		}

	}
}
