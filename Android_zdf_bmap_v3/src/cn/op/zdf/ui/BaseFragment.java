package cn.op.zdf.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.op.zdf.R;

/**
 * 包含了自定义topBar的默认控制
 * @author lufei
 *
 */
public class BaseFragment extends Fragment {
	public ImageView btnLeft;
	public ImageView btnRight;
	public TextView tvTopBarTitle;
	public FragmentActivity activity;
	public View topBar;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		activity = (FragmentActivity) getActivity();

		btnLeft = (ImageView) view.findViewById(R.id.btnLeft);
		if (btnLeft == null) {
			return;
		}

		topBar = view.findViewById(R.id.topBar);
		btnRight = (ImageView) view.findViewById(R.id.btnRight);
		tvTopBarTitle = (TextView) view.findViewById(R.id.tvTitle);

		btnLeft.setVisibility(View.VISIBLE);
		btnRight.setVisibility(View.VISIBLE);

		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) activity).toggleDrawerMenu();
			}
		});
	}
}
