package cn.op.zdf.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import cn.op.common.UIHelper;
import cn.op.common.constant.Keys;
import cn.op.zdf.R;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.meizu.smartbar.SmartBarUtils;

/**
 * 通用FragmentActivity ，通过Intent传入fragName，即可加载到当前Activity中
 * 
 * @author lufei
 * 
 */
public class SimpleFragActivity extends SherlockFragmentActivity {

	private SimpleFragActivity activity;
	private FragmentManager fm;
	private String fragName;
	private View view;

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
		setContentView(R.layout.activity_simple_frag);

		view = findViewById(R.id.root_simple_frag);

		activity = this;
		fm = getSupportFragmentManager();

		addFragment(getIntent());
	}

	protected void addFragment(Intent intentData) {
		fragName = intentData.getStringExtra(Keys.FRAG_NAME);

		Fragment fragment = Fragment.instantiate(activity, fragName,
				intentData.getExtras());
		fm.beginTransaction().add(R.id.root_simple_frag, fragment, fragName)
				.commit();
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.slide_in_from_left,
				R.anim.slide_out_to_right);
	}

	@Override
	public void finish() {
		UIHelper.hideSoftInput(getApplicationContext(), view);
		super.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater supportMenuInflater = getSupportMenuInflater();

		if (SmartBarUtils.hasSmartBar()) {
			UIHelper.makeEmptyMenu(supportMenuInflater, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

}
