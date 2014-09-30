package cn.op.common.util;

import cn.op.zdf.R;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

public class ViewFlipperUtil {

	/**
	 * 添加布局到viewFliper，并进行显示
	 * 
	 * @param context
	 * @param viewFliper
	 * @param layoutAddToShow
	 */
	public static void showNext(Context context, ViewFlipper viewFliper,
			View layoutAddToShow) {
		viewFliper.addView(layoutAddToShow);

		Animation inAnimation = AnimationUtils.loadAnimation(context,
				R.anim.slide_in_from_right);
		Animation outAnimation = AnimationUtils.loadAnimation(context,
				R.anim.slide_out_to_left);

		viewFliper.setInAnimation(inAnimation);
		viewFliper.setOutAnimation(outAnimation);
		viewFliper.showNext();
	}

	/**
	 * 从viewFliper中移除当前显示的布局，显示上一个布局
	 * 
	 * @param context
	 * @param viewFliper
	 * @return 上一个布局的id
	 */
	public static int showPrevious(Context context, ViewFlipper viewFliper) {
		viewFliper.setInAnimation(context, R.anim.slide_in_from_left);
		viewFliper.setOutAnimation(context, R.anim.slide_out_to_right);

		View currentView = viewFliper.getCurrentView();
		viewFliper.removeView(currentView);
		viewFliper.showPrevious();

		int backShowViewId = viewFliper.getCurrentView().getId();

		return backShowViewId;
	}

}
