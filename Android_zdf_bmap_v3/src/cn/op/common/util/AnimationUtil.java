package cn.op.common.util;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import cn.op.zdf.R;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class AnimationUtil {

	private static final String TAG = Log.makeLogTag(AnimationUtil.class);

	/**
	 * 在Y轴移动values距离后返回原来的位置
	 * 
	 * @param target
	 * @param values
	 * @return
	 */
	public static AnimatorSet getBackAfterTranslationY(Object target,
			float... values) {

		AnimatorSet animatorDownUp = new AnimatorSet();

		ObjectAnimator animTransY = getTranslationY(target, values);
		ObjectAnimator animTransBack = getTranslationY(target, 0f);
		animTransBack.setInterpolator(new DecelerateInterpolator());

		animatorDownUp.play(animTransY).before(animTransBack);
		return animatorDownUp;
	}

	/**
	 * 在Y轴移动values距离
	 * 
	 * @param target
	 * @param values
	 * @return
	 */
	private static ObjectAnimator getTranslationY(Object target,
			float... values) {
		ObjectAnimator animTransY = ObjectAnimator.ofFloat(target,
				"translationY", values).setDuration(500);
		animTransY.setInterpolator(new AccelerateInterpolator());

		return animTransY;
	}

	public static Animation animationShowUpHideDown(Context context,
			boolean show, View v) {

		TranslateAnimation ta;
		if (show) {
			v.setVisibility(View.VISIBLE);

			// Log.d(TAG, "v.getHeight()=" + v.getHeight());
			ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, 0);

			ta.setInterpolator(new DecelerateInterpolator());
		} else {
			v.setVisibility(View.GONE);

			// Log.d(TAG, "v.getHeight()=" + v.getHeight());
			ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, v.getHeight());
		}

		ta.setDuration(1000);
		v.startAnimation(ta);
		return ta;
	}

	/**
	 * 
	 * @param context
	 * @param show
	 *            显示-true，隐藏-false
	 * @param v
	 * @return
	 */
	public static Animation animationShowDownHideUp(Context context,
			boolean show, View v) {
		int i = 0;
		if (show) {
			i = R.anim.slide_in_from_top;
			v.setVisibility(View.VISIBLE);
		} else {
			i = R.anim.slide_out_to_top;
			v.setVisibility(View.GONE);
		}
		Animation ta = AnimationUtils.loadAnimation(context, i);
		ta.setDuration(300);
		v.startAnimation(ta);
		return ta;
	}

	/**
	 * 显示slide_in_from_bottom, 隐藏slide_out_to_bottom
	 * 
	 * @param context
	 * @param show
	 *            显示-true，隐藏-false
	 * @param v
	 * @return
	 */
	public static Animation animationShowSifbHideSotb(Context context,
			boolean show, View v) {
		int i = 0;
		if (show) {
			i = R.anim.slide_in_from_bottom;
			v.setVisibility(View.VISIBLE);
		} else {
			i = R.anim.slide_out_to_bottom;
			v.setVisibility(View.GONE);
		}
		Animation ta = AnimationUtils.loadAnimation(context, i);
		v.startAnimation(ta);
		return ta;
	}

	/**
	 * 有些设备，当view的visible默认是GONE时，这时改变其为Visible，但view却仍是不可见的；
	 * 但是使用INVISIBLE代替GONE却没问题
	 * 
	 * @param context
	 * @param show
	 * @param v
	 * @return
	 */
	public static Animation animationShowHideAlphaSpecial(Context context,
			boolean show, View v) {
		int i = 0;
		if (show) {
			i = R.anim.toshow;
			v.setVisibility(View.VISIBLE);
		} else {
			i = R.anim.todispear;
			v.setVisibility(View.INVISIBLE);
		}
		Animation ta = AnimationUtils.loadAnimation(context, i);
		ta.setDuration(500);
		v.startAnimation(ta);
		return ta;
	}

	/**
	 * 有些设备，当view的visible默认是GONE时，这时改变其为Visible，但view却仍是不可见的；
	 * 但是使用INVISIBLE代替GONE却没问题
	 * 
	 * @param context
	 * @param show
	 *            显示-true，隐藏-false
	 * @param v
	 * @return
	 */
	public static Animation animationShowDownHideUpSpecial(Context context,
			boolean show, View v) {
		int i = 0;
		if (show) {
			i = R.anim.slide_in_from_top;
			v.setVisibility(View.VISIBLE);
		} else {
			i = R.anim.slide_out_to_top;
			v.setVisibility(View.INVISIBLE);
		}
		Animation ta = AnimationUtils.loadAnimation(context, i);
		ta.setDuration(300);
		v.startAnimation(ta);
		return ta;
	}

	public static Animation animationShowHideAlpha(Context context,
			boolean show, View v) {
		int i = 0;
		if (show) {
//			i = R.anim.toshow;
			i = R.anim.umeng_socialize_fade_in;
//			i = R.anim.translucent_zoom_in;
			v.setVisibility(View.VISIBLE);
		} else {
//			i = R.anim.todispear;
			i = R.anim.umeng_socialize_fade_out;
//			i = R.anim.translucent_zoom_out;
			v.setVisibility(View.GONE);
		}
		Animation ta = AnimationUtils.loadAnimation(context, i);
//		ta.setDuration(500);
		v.startAnimation(ta);
		return ta;
	}

	/**
	 * 显示slide_in_from_bottom, 隐藏slide_out_to_bottom
	 * 
	 * 有些设备，当view的visible默认是GONE时，这时改变其为Visible，但view却仍是不可见的；
	 * 但是使用INVISIBLE代替GONE却没问题
	 * 
	 * @param context
	 * @param isToShow
	 * @param v
	 * @return
	 */
	public static Animation animationShowSifbHideSotbSpecial(Context context,
			boolean isToShow, View v) {
		int i = 0;
		if (isToShow) {
			i = R.anim.slide_in_from_bottom;
			v.setVisibility(View.VISIBLE);
		} else {
			i = R.anim.slide_out_to_bottom;
			v.setVisibility(View.INVISIBLE);
		}
		Animation ta = AnimationUtils.loadAnimation(context, i);
		v.startAnimation(ta);
		return ta;
	}

}
