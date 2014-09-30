package cn.op.common.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import cn.op.zdf.AppContext;

public class PhoneUtil {
	/**
	 * 判断是否可进行定位，GPS或者AGPS开启一个就认为是开启的
	 * 
	 * @param context
	 * @return true 表示开启
	 */
	public static boolean canLoc(final AppContext context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		// boolean networkLoc =
		// locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		// 发现既是AGPS，即设置里面的-位置服务-基于网络的位置服务是打开的，但网络未打开，是无法定位的，但如果只是网络打开却可以进行定位，所以这里应该以网络是否打开来判断是否可以通过网络定位
		boolean network = context.isNetworkConnected();

		if (gps || network) {
			return true;
		}

		return false;
	}

	/**
	 * 判断GPS是否开启
	 * 
	 * @param context
	 * @return
	 */
	public static final boolean isGpsOPen(final Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		return gps;
	}

	/**
	 * 强制帮用户打开GPS
	 * 
	 * @param context
	 */
	public static final void openGPS(Context context) {
		Intent GPSIntent = new Intent();
		GPSIntent.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
		GPSIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 震动一次
	 * 
	 * @param activity
	 * @param milliseconds
	 *            毫秒
	 */
	public static void vibrate(final Activity activity, long milliseconds) {
		Vibrator vib = (Vibrator) activity
				.getSystemService(Service.VIBRATOR_SERVICE);

		vib.vibrate(milliseconds);
	}

	/**
	 * 
	 * 
	 * @param activity
	 * @param pattern
	 *            自定义震动模式 。数组中数字的含义依次是静止的时长，震动时长，静止时长，震动时长。。。时长的单位是毫秒
	 * @param isRepeat
	 *            ture-反复震动，false-一次
	 */
	public static void vibrate(final Activity activity, long[] pattern,
			boolean isRepeat) {
		Vibrator vib = (Vibrator) activity
				.getSystemService(Service.VIBRATOR_SERVICE);

		vib.vibrate(pattern, isRepeat ? 1 : -1);
	}

	public static PackageInfo getPackageInfo(Context context) {
		PackageInfo packInfo = null;
		try {
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return packInfo;
	}

	public static int getVersionCode(Context context) {

		int versionCode = 0;
		PackageInfo packageInfo = getPackageInfo(context);
		if (packageInfo != null) {
			versionCode = packageInfo.versionCode;
		}

		return versionCode;

	}

	public static String getVersionName(Context context) {

		String versionName = "";
		PackageInfo packageInfo = getPackageInfo(context);
		if (packageInfo != null) {
			versionName = packageInfo.versionName;
		}

		return versionName;

	}

	public static ApplicationInfo ApplicationInfo(Context ac) {
		ApplicationInfo appInfo = null;
		try {
			appInfo = ac.getPackageManager().getApplicationInfo(
					ac.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return appInfo;
	}

	public static String getImei(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		return imei;
	}

	public static String getImsi(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		return imsi;
	}

	public static String getChannel(Context context) {
		ApplicationInfo appInfo = PhoneUtil.ApplicationInfo(context);
		String channelName = appInfo.metaData.getString("UMENG_CHANNEL");
		return channelName;
	}

	public static String getMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

}
