package cn.op.common.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;

public class PathMenuAnimUtil {

	// private static int xOffset = 15;
	private static int xOffset = 0;
	private static int yOffset = 10;

	// private static int yOffset = -13;

	public static void initOffset(Context context) {// 由布局文件
		xOffset = -(int) (xOffset * context.getResources().getDisplayMetrics().density);
		yOffset = (int) (yOffset * context.getResources().getDisplayMetrics().density);
	}

	/**
	 * 旋转动画
	 * 
	 * @param fromDegrees
	 * @param toDegrees
	 * @param durationMillis
	 * @return
	 */
	public static Animation getRotateAnimation(float fromDegrees,
			float toDegrees, int durationMillis) {
		RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotate.setDuration(durationMillis);
		rotate.setFillAfter(true);
		return rotate;
	}

	public static void startAnimationsOpen(ViewGroup viewgroup,
			int durationMillis) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
			ImageButton inoutimagebutton = (ImageButton) viewgroup
					.getChildAt(i);
			inoutimagebutton.setVisibility(View.VISIBLE);
			MarginLayoutParams mlp = (MarginLayoutParams) inoutimagebutton
					.getLayoutParams();
			Animation animation = new TranslateAnimation(
					-(mlp.leftMargin - xOffset), 0F,
					yOffset + mlp.bottomMargin, 0F);

			animation.setFillAfter(true);
			animation.setDuration(durationMillis);
			animation.setStartOffset((i * 100)
					/ (-1 + viewgroup.getChildCount()));
			animation.setInterpolator(new OvershootInterpolator(2F));
			inoutimagebutton.startAnimation(animation);

		}
	}

	public static void startAnimationsClose(ViewGroup viewgroup,
			int durationMillis) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
			final ImageButton inoutimagebutton = (ImageButton) viewgroup
					.getChildAt(i);
			MarginLayoutParams mlp = (MarginLayoutParams) inoutimagebutton
					.getLayoutParams();
			Animation animation = new TranslateAnimation(0F,
					-(mlp.leftMargin - xOffset), 0F, yOffset + mlp.bottomMargin);

			animation.setFillAfter(true);
			animation.setDuration(durationMillis);
			animation.setStartOffset(((viewgroup.getChildCount() - i) * 100)
					/ (-1 + viewgroup.getChildCount()));// 顺序倒一下比较舒服
			animation.setInterpolator(new AnticipateInterpolator(2F));
//			animation.setAnimationListener(new Animation.AnimationListener() {
//				@Override
//				public void onAnimationStart(Animation arg0) {
//				}
//
//				@Override
//				public void onAnimationRepeat(Animation arg0) {
//				}
//
//				@Override
//				public void onAnimationEnd(Animation arg0) {
//					inoutimagebutton.setVisibility(View.GONE);
//				}
//			});
			inoutimagebutton.startAnimation(animation);
			inoutimagebutton.setVisibility(View.GONE);
		}

	}

}