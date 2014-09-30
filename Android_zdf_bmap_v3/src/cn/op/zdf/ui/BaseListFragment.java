package cn.op.zdf.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.op.zdf.R;

/**
 * 包含了自定义topBar的默认控制
 * 
 * @author lufei
 * 
 */
public class BaseListFragment extends ListFragment {
	public ImageView btnLeft;
	public ImageView btnRight;
	public TextView tvTopBarTitle;
	private Activity activity;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		activity = getActivity();

		btnLeft = (ImageView) view.findViewById(R.id.btnLeft);
		if (btnLeft == null) {
			return;
		}

		btnRight = (ImageView) view.findViewById(R.id.btnRight);
		tvTopBarTitle = (TextView) view.findViewById(R.id.tvTitle);
	}
}
