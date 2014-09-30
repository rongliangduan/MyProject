package cn.op.zdf;

import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import cn.op.common.AppConfig;
import cn.op.common.constant.Keys;
import cn.op.common.domain.UserInfo;
import cn.op.common.util.Log;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;

/**
 *清除 用户 浏览记录、注销用户
 * @author lufei
 *
 */
public class UserCenter {

	protected static final String TAG = Log.makeLogTag(UserCenter.class);

	/**
	 * 清除用户所有浏览记录，包括用户登录信息
	 * 
	 * @param context
	 */
	public static void clearUserRecord(Context context) {
		AppConfig config = AppConfig.getAppConfig(context);
		Properties prop = config.get();
		prop.remove(Keys.LAST_CHOOSE_CITY);
		prop.remove(Keys.LAST_FILE_LIVE_MAN);
		prop.remove(Keys.LAST_FILE_LIVE_MAN_PHONE);
		prop.remove(Keys.LAST_LATITUDE);
		prop.remove(Keys.LAST_LONGITUDE);

		prop.remove(Keys.LAST_LOGIN_NICKNAME);
		prop.remove(Keys.LAST_LOGIN_USER_TYPE);
		prop.remove(Keys.LAST_LOGIN_USERNAME);
		prop.remove(Keys.PSW);
		config.setProps(prop);

		AppContext ac = AppContext.getAc();
		ac.user = null;
		ac.myLocationBD = null;
		ac.removeRecentBrowsHotel();
	}

	/**
	 * 注销用户，普通用户删除密码，OAuth删除昵称，用户类型，以及删除授权
	 * 
	 * @param activity
	 */
	public static void loginOut(Activity activity) {
		AppContext ac = AppContext.getAc();
		String userType = ac.user.userType;
		ac.user = null;

		// 普通用户的退出
		AppConfig acf = AppConfig.getAppConfig(ac);
		acf.remove(Keys.PSW);

		// OAuth用户的退出
		acf.remove(Keys.LAST_LOGIN_NICKNAME);
		acf.remove(Keys.LAST_LOGIN_USER_TYPE);

//		TODO um 
		if (UserInfo.USER_TYPE_OAUTH_QZONE.equals(userType)) {
			deleteOauth(activity, SHARE_MEDIA.QZONE);
		} else if (UserInfo.USER_TYPE_OAUTH_SINA.equals(userType)) {
			deleteOauth(activity, SHARE_MEDIA.SINA);
		}

	}

	/**
	 * 取消指定平台的授权状态
	 * 
	 * @param context
	 * @param qzone
	 */
	private static void deleteOauth(Context context, SHARE_MEDIA platform) {
		UMSocialService mController = UMServiceFactory.getUMSocialService(
				"com.umeng.login", RequestType.SOCIAL);

		mController.deleteOauth(context, platform,
				new SocializeClientListener() {
					@Override
					public void onStart() {
						Log.d(TAG,
								"======UMSocialService deleteOauth======onStart()");
					}

					@Override
					public void onComplete(int arg0, SocializeEntity arg1) {
						Log.d(TAG,
								"======UMSocialService deleteOauth======onComplete()");
					}
				});

		// OAuth,注销登录状态 TODO 此方法未奇效
		// mController.loginout(activity, new SocializeClientListener() {
		// @Override
		// public void onStart() {
		// Log.d(TAG, "======UMSocialService loginout======onStart()");
		// }
		//
		// @Override
		// public void onComplete(int arg0, SocializeEntity arg1) {
		// Log.d(TAG, "======UMSocialService loginout======onComplete()");
		// }
		// });
	}

	/**
	 * 清除用户浏览记录，包括：上次位置、订单填写记录、浏览酒店记录
	 * 
	 * @param ac
	 */
	public static void clearBrowseRecord(AppContext ac) {
		AppConfig config = AppConfig.getAppConfig(ac);
		Properties prop = config.get();
		prop.remove(Keys.LAST_CHOOSE_CITY);
		prop.remove(Keys.LAST_FILE_LIVE_MAN);
		prop.remove(Keys.LAST_FILE_LIVE_MAN_PHONE);
		prop.remove(Keys.LAST_LATITUDE);
		prop.remove(Keys.LAST_LONGITUDE);
		config.setProps(prop);

		// 清除酒店浏览记录
		ac.removeRecentBrowsHotel();
	}

}
