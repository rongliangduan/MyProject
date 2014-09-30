package cn.op.zdf.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import cn.op.common.UIHelper;
import cn.op.common.constant.Tags;
import cn.op.common.util.Log;
import cn.op.zdf.R;

import com.tendcloud.tenddata.TCAgent;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.UMSsoHandler;

/**
 * 登录
 * 
 * @author lufei
 * 
 */
public class LoginActivity extends FragmentActivity {

	private static final String TAG = Log.makeLogTag(LoginActivity.class);
	private LoginActivity activity;
	private FragmentManager fm;
	public View pb;
	private LoginFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		activity = this;
		fm = getSupportFragmentManager();
		pb = findViewById(R.id.pb);

		addFragment();
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

	protected void addFragment() {

		fragment = (LoginFragment) Fragment.instantiate(activity,
				LoginFragment.class.getName());

		fm.beginTransaction().add(R.id.root_login, fragment, Tags.LOGIN)
				.commit();
	}

	@Override
	public void finish() {
		UIHelper.hideSoftInput(activity, pb);
		super.finish();
		overridePendingTransition(R.anim.slide_in_from_left,
				R.anim.slide_out_to_right);
	}

	@Override
	public void onBackPressed() {
		fragment.back();

		// if (fragment.layoutFindPsw != null
		// && fragment.layoutFindPsw.getVisibility() == View.VISIBLE) {
		// fragment.hideFindPsw();
		// } else {
		// super.onBackPressed();
		// }
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "======onActivityResult====== requestCode=" + requestCode
				+ ",data=" + data);

		super.onActivityResult(requestCode, resultCode, data);

		if (data != null) {
			pb.setVisibility(View.VISIBLE);
		}

		UMSocialService mController = UMServiceFactory.getUMSocialService(
				"com.umeng.login", RequestType.SOCIAL);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

	}

}
