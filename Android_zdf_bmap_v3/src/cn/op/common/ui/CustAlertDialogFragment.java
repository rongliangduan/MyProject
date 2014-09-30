package cn.op.common.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;

public class CustAlertDialogFragment extends Fragment {

	private String title;
	private String msg;

	public static CustAlertDialogFragment newInstance(
			OnDialogClickListener onDialogClickListener, int resLayout,
			String title, String msg) {
		CustAlertDialogFragment f = new CustAlertDialogFragment();
		f.resLayout = resLayout;
		f.title = title;
		f.msg = msg;
		f.onDialogClickListener = onDialogClickListener;
		return f;
	}

	private int resLayout = R.layout.frag_cust_alert_dialog_default;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(resLayout, container, false);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		return view;
	}
	
	

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final View viewBg = view.findViewById(R.id.viewBg);
		View layoutDialog = view.findViewById(R.id.layoutDialog);
		TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		TextView tvMsg = (TextView) view.findViewById(R.id.tvMsg);
		Button btn1 = (Button) view.findViewById(R.id.button1);
		Button btn2 = (Button) view.findViewById(R.id.button2);

		btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				animation1(false, viewBg);
				onDialogClickListener.onBtn1Click(v);
			}
		});
		btn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				animation1(false, viewBg);
				onDialogClickListener.onBtn2Click(v);
			}
		});

		viewBg.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				onDialogClickListener.onDialogBgClick(v);
				animation1(false, viewBg);
				return true;
			}
		});

		layoutDialog.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		
		tvTitle.setText(title);
		tvMsg.setText(msg);
		
		animation1(true, viewBg);
	}
	
	
	private void animation1(final boolean isChecked, View v) {
		int i = 0;
		if (isChecked) {
			i = R.anim.toshow;
			v.setVisibility(View.VISIBLE);
		} else {
			i = R.anim.todispear;
			v.setVisibility(View.GONE);
		}
		AnimationSet ta = (AnimationSet) AnimationUtils.loadAnimation(AppContext.getAc(),
				i);
		ta.setDuration(500);
		v.startAnimation(ta);

	}

	private OnDialogClickListener onDialogClickListener = new OnDialogClickListener() {

		@Override
		public void onDialogBgClick(View v) {
		}

		@Override
		public void onBtn2Click(View v) {
		}

		@Override
		public void onBtn1Click(View v) {
		}
	};

	public interface OnDialogClickListener {
		void onBtn1Click(View v);

		void onBtn2Click(View v);

		void onDialogBgClick(View v);
	}
}