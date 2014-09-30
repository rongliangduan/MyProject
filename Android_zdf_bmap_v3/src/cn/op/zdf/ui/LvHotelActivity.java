package cn.op.zdf.ui;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import cn.op.common.UIHelper;
import cn.op.common.util.Constants;
import cn.op.common.util.Log;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.R.anim;
import cn.op.zdf.R.layout;
import cn.op.zdf.event.Event;
import cn.op.zdf.event.ExitSearchEvent;
import cn.op.zdf.event.SearchResultEvent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.meizu.smartbar.SmartBarUtils;

import de.greenrobot.event.EventBus;

public class LvHotelActivity extends SherlockFragmentActivity {

	private static final String TAG = LvHotelActivity.class.getSimpleName();
	private LvHotelActivity activity;
	private AppContext ac;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
	protected void onCreate(Bundle savedInstanceState) {
		
		if (SmartBarUtils.hasSmartBar()) {
			getSherlock().setUiOptions(
					ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);

			setTheme(R.style.Holo_Theme_CustomAbsOverlay);

			SmartBarUtils.setActionBarViewCollapsable(getActionBar(), true);
		}
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lv_hotel);

		activity = this;
		ac = AppContext.getAc();
		
//		View topBar = findViewById(R.id.topBarMain);
//		ImageView btnLeft = (ImageView) topBar.findViewById(R.id.btnLeft);
//		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
//
//		if (ac.lastChooseCity != null) {
//			tvTitle.setText(ac.lastChooseCity.cityName);
//		} else if (ac.lastLocCity != null) {
//			tvTitle.setText(ac.lastLocCity.cityName);
//		} else {
//			tvTitle.setText("选择城市");
//		}
//
//		tvTitle.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (UIHelper.isFastDoubleClick(v)) {
//					return;
//				}
//
//				UIHelper.showCityChoose(activity);
//			}
//		});
//
//		btnLeft.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				finish();
//			}
//		});

	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "======onDestroyView======");
		super.onDestroy();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_from_left,
				R.anim.slide_out_to_right);
	}

}
