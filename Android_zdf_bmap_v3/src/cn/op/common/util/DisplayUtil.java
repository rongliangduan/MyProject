package cn.op.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;

public class DisplayUtil {
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static float spToPixels(Context context, float sp) {
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
				r.getDisplayMetrics());
		return px;
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static DisplayMetrics getDisplayMetrics(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);// 获取分辨率
		return dm;
	}

	public static int getScreenWidth(Activity context) {
		return getDisplayMetrics(context).widthPixels;
	}

	public static int getScreenHight(Activity context) {
		return getDisplayMetrics(context).heightPixels;
	}

	/**
	 * 此方法需要在view.post()方法中,或者Activity.onWindowFocusChanged()中才能正确获取高度
	 * 
	 * @param activity
	 * @return
	 */
	public static int getStatusBarHeight(Activity activity) {
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		return statusBarHeight;
	}

	/**
	 * 
	 * @param activity
	 * @return 手机屏幕dpi
	 */
	public static int getScreenDpi(Activity activity) {
		Display localDisplay = activity.getWindowManager().getDefaultDisplay();

		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		localDisplay.getMetrics(localDisplayMetrics);
		int dpi = Math.round(localDisplayMetrics.xdpi);
		return dpi;
	}

	/**
	 * @param activity
	 * @return 是否是比较低的dpi，一般为dpi<260,分辨率480*800左右
	 */
	public static boolean isLowDpi(Activity activity) {
		return getScreenDpi(activity) < 360;
	}
}
