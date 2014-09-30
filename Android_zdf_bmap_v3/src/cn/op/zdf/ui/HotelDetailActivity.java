package cn.op.zdf.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import cn.op.common.constant.Keys;
import cn.op.common.constant.Tags;
import cn.op.common.util.FragmentUtil;
import cn.op.common.util.Log;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.domain.Item;
import cn.op.zdf.event.HideReserveEvent;
import cn.op.zdf.event.SaveRecentBrowsHotelEvent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.meizu.smartbar.SmartBarUtils;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.UMSsoHandler;

import de.greenrobot.event.EventBus;

/**
 * 酒店详情
 * 
 * @author lufei
 * 
 */
public class HotelDetailActivity extends SherlockFragmentActivity {

	protected static final String TAG = Log
			.makeLogTag(HotelDetailActivity.class);
	private FragmentManager fm;
	public FragmentUtil fragmentUtil;
	private HotelDetailActivity activity;
	private AppContext ac;
	private Item itemHotel;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		if (SmartBarUtils.hasSmartBar()) {
			getSherlock().setUiOptions(
					ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);

			setTheme(R.style.Holo_Theme_CustomAbsOverlay);

			SmartBarUtils.setActionBarViewCollapsable(getActionBar(), true);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel);

		ac = AppContext.getAc();
		activity = this;
		fm = getSupportFragmentManager();
		fragmentUtil = new FragmentUtil(fm);

		addFragment();

		// overridePendingTransition
		// 使用的动画时间，在此时间后再加载fragment，否则在有些配置低的机子上，会有卡顿现象
		// int animTime = getResources().getInteger(
		// android.R.integer.config_mediumAnimTime);
		// new CountDownTimer(animTime, animTime) {
		//
		// public void onTick(long millisUntilFinished) {
		// Log.d(TAG, "======CountDownTimer onTick======"
		// + millisUntilFinished);
		// }
		//
		// public void onFinish() {
		// Log.d(TAG, "======CountDownTimer onFinish======");
		//
		// addFragment();
		//
		// }
		// }.start();

	}

	@Override
	protected void onResume() {
		Log.d(TAG, "======onResume======");
		super.onResume();
		TCAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "======onPause======");
		super.onPause();
		TCAgent.onPause(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	protected void addFragment() {
		Intent intent = getIntent();
		itemHotel = (Item) intent.getSerializableExtra(Keys.ITEM);

		Fragment fragment;

		// Fragment byTag = fm.findFragmentByTag(Tags.HOTEL_DETAIL);

		// if (Room.isSellZdf()) {
		// fragment = Fragment.instantiate(activity,
		// HotelDetailZdfFragment.class.getName());
		// } else {
		// fragment = Fragment.instantiate(activity,
		// HotelDetailFragment.class.getName());
		// }

		fragment = Fragment.instantiate(activity,
				HotelDetailFragment.class.getName());

		Bundle arg = new Bundle();
		arg.putSerializable(Keys.ITEM, itemHotel);
		fragment.setArguments(arg);

		fm.beginTransaction().add(R.id.root_hotel, fragment, Tags.HOTEL_DETAIL)
				.commit();
	}

	@Override
	public void finish() {

		// ac.saveRecentBrowseHotel(itemHotel);
		//
		// SaveRecentBrowsHotelEvent event = new SaveRecentBrowsHotelEvent();
		// event.save = true;
		// event.saveHotel = itemHotel;
		// EventBus.getDefault().post(event);

		super.finish();
		overridePendingTransition(R.anim.slide_in_from_left,
				R.anim.slide_out_to_right);
	}

	/**
	 * 用于activity中fragment的popBackStack
	 */
	public void popActivityFragBackStack() {
		if (this.getSupportFragmentManager().getBackStackEntryCount() > 0) {
			super.onBackPressed();
		}
	}

	@Override
	public void onBackPressed() {
		if (ac.isReserve) {
			HideReserveEvent hideReserveEvent = new HideReserveEvent();
			hideReserveEvent.hide = true;
			EventBus.getDefault().post(hideReserveEvent);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.d(TAG, "======onActivityResult====== requestCode=" + requestCode
				+ ",data=" + data);

		super.onActivityResult(requestCode, resultCode, data);

		// if (data != null) {
		// pb.setVisibility(View.VISIBLE);
		// }

		UMSocialService mController = UMServiceFactory.getUMSocialService(
				"com.umeng.login", RequestType.SOCIAL);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// // 用于activity中fragment的popBackStack
	// if (this.getSupportFragmentManager().getBackStackEntryCount() > 0
	// && keyCode == KeyEvent.KEYCODE_BACK) {
	//
	// if (ac.isReserve) {
	// HideReserveEvent hideReserveEvent = new HideReserveEvent();
	// hideReserveEvent.hide = true;
	// EventBus.getDefault().post(hideReserveEvent);
	//
	// return true;
	// }
	//
	// super.onBackPressed();
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

}
