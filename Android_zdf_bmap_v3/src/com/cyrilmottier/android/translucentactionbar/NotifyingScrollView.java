package com.cyrilmottier.android.translucentactionbar;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * @author Cyril Mottier with modifications from Manuel Peinado
 */
public class NotifyingScrollView extends ScrollView {
	// Edge-effects don't mix well with the translucent action bar in Android
	// 2.X
	private boolean mDisableEdgeEffects = true;

	/**
	 * @author Cyril Mottier
	 */
	public interface OnScrollChangedListener {
		void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt);
	}

	private OnScrollChangedListener mOnScrollChangedListener;

	public NotifyingScrollView(Context context) {
		super(context);
	}

	public NotifyingScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NotifyingScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mOnScrollChangedListener != null) {
			mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
		}
	}

	public void setOnScrollChangedListener(OnScrollChangedListener listener) {
		mOnScrollChangedListener = listener;
	}

	@Override
	protected float getTopFadingEdgeStrength() {
		// http://stackoverflow.com/a/6894270/244576
		if (mDisableEdgeEffects
				&& Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return 0.0f;
		}
		return super.getTopFadingEdgeStrength();
	}

	@Override
	protected float getBottomFadingEdgeStrength() {
		// http://stackoverflow.com/a/6894270/244576
		if (mDisableEdgeEffects
				&& Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return 0.0f;
		}
		return super.getBottomFadingEdgeStrength();
	}

	// ====================== 阻尼效果
	private View mView;
	private float touchY;
	private int scrollY = 0;
	private boolean handleStop = false;
	private int eachStep = 0;
	public static final int MAX_SCROLL_HEIGHT = 300;// 最大滑动距离 private static
	private static final String TAG = NotifyingScrollView.class.getSimpleName();
	final float SCROLL_RATIO = 0.9f;// 阻尼系数,越小阻力就越大
	private OnScrollPullOffsetListener onScrollPullOffsetListener;

	public interface OnScrollPullOffsetListener {
		/**
		 * @param offset
		 *            pull to down if offset<0, else is pull to up
		 */
		void pullOffset(int offset);
	}

	public void setOnScrollPullOffsetListener(
			OnScrollPullOffsetListener listener) {
		this.onScrollPullOffsetListener = listener;
	}

	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			this.mView = getChildAt(0);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (arg0.getAction() == MotionEvent.ACTION_DOWN) {
			touchY = arg0.getY();
		}
		return super.onInterceptTouchEvent(arg0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mView == null) {
			return super.onTouchEvent(ev);
		} else {
			commonOnTouchEvent(ev);
		}
		return super.onTouchEvent(ev);
	}

	private void commonOnTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_UP:
			if (mView.getScrollY() != 0) {
				handleStop = true;
				animation();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			float nowY = ev.getY();
			int deltaY = (int) (touchY - nowY);
			touchY = nowY;
			if (isNeedMove()) {
				int offset = mView.getScrollY();
				if (offset < MAX_SCROLL_HEIGHT && offset > -MAX_SCROLL_HEIGHT) {
					mView.scrollBy(0, (int) (deltaY * SCROLL_RATIO));
					handleStop = false;
					
//					Log.d(TAG, "======commonOnTouchEvent ACTION_MOVE offset="
//							+ offset);

					if (onScrollPullOffsetListener != null) {
						onScrollPullOffsetListener.pullOffset(offset);
					}
				}
			}
			break;
		default:
			break;
		}
	}

	private boolean isNeedMove() {
		int viewHight = mView.getMeasuredHeight();
		int srollHight = getHeight();
		int offset = viewHight - srollHight;
		int scrollY = getScrollY();
		
		Log.d(TAG, "======isNeedMove====== scrollY=" + scrollY
				+ " ,srollHight=" + srollHight + " ,offset=" + offset);
		
		if (scrollY == 0 || scrollY == offset) {
			return true;
		}
		return false;
	}

	private void animation() {
		scrollY = mView.getScrollY();
		eachStep = scrollY / 10;
		resetPositionHandler.sendEmptyMessage(0);
	}

	Handler resetPositionHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (scrollY != 0 && handleStop) {
				scrollY -= eachStep;
				if ((eachStep < 0 && scrollY > 0)
						|| (eachStep > 0 && scrollY < 0)) {
					scrollY = 0;
				}
				mView.scrollTo(0, scrollY);
				this.sendEmptyMessageDelayed(0, 5);
			}
		};
	};
}