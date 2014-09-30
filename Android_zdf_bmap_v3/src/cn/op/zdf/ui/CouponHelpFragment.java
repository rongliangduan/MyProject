package cn.op.zdf.ui;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.op.common.UIHelper;
import cn.op.common.util.Constants;
import cn.op.zdf.R;

/**
 * 免责声明
 * 
 * @author lufei
 * 
 */
public class CouponHelpFragment extends Fragment {

	FragmentActivity activity;
	private ImageView btnLeft;
	private ImageView btnRight;
	private TextView tvTopBarTitle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.frag_coupon_help, container,
				false);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		return view;
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		btnLeft = (ImageView) view.findViewById(R.id.btnLeft);
		if (btnLeft == null) {
			return;
		}

		btnRight = (ImageView) view.findViewById(R.id.btnRight);
		tvTopBarTitle = (TextView) view.findViewById(R.id.tvTitle);

		activity = getActivity();

		tvTopBarTitle.setText("帮助");

		btnRight.setVisibility(View.INVISIBLE);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				activity.finish();
			}
		});

		TextView tvTel = (TextView) view.findViewById(R.id.tvTel);
		tvTel.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		tvTel.setText("有间房客户电话：4008-521-002");
		tvTel.setTextColor(getResources().getColor(R.color.red_text_click));

		tvTel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				UIHelper.call(activity, Constants.TEL_JUJIA);
			}
		});

	};
}
