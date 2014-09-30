package com.cyrilmottier.android.translucentactionbar;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ScrollView;
import cn.op.common.util.DisplayUtil;
import cn.op.zdf.R;

import com.nineoldandroids.animation.ObjectAnimator;

/**
 * @author Cyril Mottier with modifications from Manuel Peinado
 */
public class HotelDetailNotifyingScrollView extends ScrollView {
	// Edge-effects don't mix well with the translucent action bar in Android
	// 2.X
	private boolean mDisableEdgeEffects = true;

	// private GestureDetector mGestureDetector;
	// View.OnTouchListener mGestureListener;

	/**
	 * @author Cyril Mottier
	 */
	public interface OnScrollChangedListener {
		void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt);
	}

	private OnScrollChangedListener mOnScrollChangedListener;

	public HotelDetailNotifyingScrollView(Context context) {
		super(context);
	}

	public HotelDetailNotifyingScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// mGestureDetector = new GestureDetector(new YScrollDetector());
	}

	public HotelDetailNotifyingScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	// Return false if we're scrolling in the x direction
	class YScrollDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// if (Math.abs(distanceY) > Math.abs(distanceX)
			// ) {
			if (Math.abs(distanceY) > Math.abs(distanceX)
					|| Math.abs(distanceX) > 10) {

				// Log.d(TAG, "======YScrollDetector====== true");
				return true;
			}
			// Log.d(TAG, "======YScrollDetector====== false");
			return false;
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mOnScrollChangedListener != null) {
			mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
		}

//		int tChange = t - oldt;
		// Log.d(TAG, "======onScrollChanged====== l=" + l + ", t=" + t
		// + ", oldl=" + oldl + ", oldt=" + oldt + ", tChange=" + tChange);

//		boolean isSlide2Down = tChange < 0 ? true : false;

		// if(!isSlide2Down){
		// int scrollY = mSvContent.getScrollY();
		// if (scrollY != 0) {
		//
		// mSvContent.scrollBy(0, (int) (deltaY * SCROLL_RATIO));
		//
		// // animation();
		// }
		// }

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
	private float moveYLast;
	private int scrollY = 0;
	private boolean handleStop = false;
	private int eachStep = 0;
	public static final int MAX_SCROLL_HEIGHT = 500;// 最大滑动距离 private static
	private static final String TAG = HotelDetailNotifyingScrollView.class
			.getSimpleName();
	final float SCROLL_RATIO = 0.9f;// 阻尼系数,越小阻力就越大
	private OnScrollPullOffsetListener onScrollPullOffsetListener;
	private ViewGroup mSvChildView;
	private View mSvContent;
	private View mSvHeader;
	protected int mSvHeaderHeight;
	protected int mSvHeight;
	private float downY;
	private int padingTop;
	protected int top2Center4SvHeader;
	private float upY;
	private boolean isSlide2Down;
	private boolean expend;
	private int scrollYHeader;
	private int eachStepHeader;

	private float scale = 1f;

	private float scaleLast = 1f;

	private float SCALE_MAX = 2f;

	private float SCALE_MIN = 1f;

	private boolean isPull2Down;

	private int scrollYLast;
	protected int top4SvContent;

	// private ImageView mIv;

	public interface OnScrollPullOffsetListener {
		/**
		 * @param offset
		 *            pull to down if offset<0, else is pull to up。max
		 *            offset(abs) is NotifyingScrollView.MAX_SCROLL_HEIGHT
		 * @param mView
		 *            ScrollView的唯一子view
		 */
		void pullOffset(int offset, View mView);
	}

	public void setOnScrollPullOffsetListener(
			OnScrollPullOffsetListener listener) {
		this.onScrollPullOffsetListener = listener;
	}

	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			this.mSvChildView = (ViewGroup) getChildAt(0);
			this.mSvContent = mSvChildView.findViewById(R.id.svContent);
			this.mSvHeader = mSvChildView.findViewById(R.id.svHeader1);

			// mIv = (ImageView) mSvHeader;

			mSvChildView.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {
						private boolean mFirstGlobalLayoutPerformed;

						@Override
						public void onGlobalLayout() {
							int headerHeight = mSvHeader.getHeight();
							if (!mFirstGlobalLayoutPerformed
									&& headerHeight != 0) {

								mFirstGlobalLayoutPerformed = true;
								mSvHeaderHeight = headerHeight;
								mSvHeight = getHeight();

								top4SvContent = mSvHeight
										- mSvHeaderHeight
										- getResources()
												.getDimensionPixelOffset(
														R.dimen.height_logo_brand);

								top4SvContent += DisplayUtil.dip2px(
										getContext(), 20);
								top2Center4SvHeader = (mSvHeight - mSvHeaderHeight) / 2;

								// Log.d(TAG,
								// "======onGlobalLayout====== mHeaderHeight="
								// + mSvHeaderHeight);
							}
						}
					});
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {

		if (arg0.getAction() == MotionEvent.ACTION_DOWN) {
			moveYLast = arg0.getY();
		}

		// return mGestureDetector.onTouchEvent(arg0)
		// && super.onInterceptTouchEvent(arg0);

		return super.onInterceptTouchEvent(arg0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// if (mSvChildView == null) {
		if (mSvContent == null) {
			return super.onTouchEvent(ev);
		} else {
			commonOnTouchEvent(ev);
		}

		// super.onTouchEvent(ev);
		// return false;

		return super.onTouchEvent(ev);
	}

	private void commonOnTouchEvent(MotionEvent ev) {
		int action = ev.getAction();

		switch (action) {
		case MotionEvent.ACTION_UP:

			if (mSvContent.getScrollY() != 0) {
				handleStop = true;
				animation();
			}

			upY = ev.getY();
			// Log.d(TAG, "======ACTION_UP====== mSvContent.getScrollY()="
			// + mSvContent.getScrollY());
			// Log.d(TAG, "======ACTION_UP====== mSvChildView.getScrollY()=" +
			// mSvChildView.getScrollY());
			// Log.d(TAG, "======ACTION_UP====== mSvHeader.getScrollY()=" +
			// mSvHeader.getScrollY());
			// if (mSvChildView.getScrollY() != 0) {

			// if (mSvContent.getScrollY() != 0) {
			// handleStop = true;
			//
			// if (isSlide2Down) {
			// mSvChildView.setPadding(0, top2Center4SvHeader, 0, 0);
			// }
			//
			// // mSvChildView.setPadding(0, top2Center4SvHeader, 0, 0);
			//
			// // animation();
			// }
			break;
		case MotionEvent.ACTION_DOWN:
			downY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			float moveY = ev.getY();
			// 反映了滑动速度，负值向下滑
			int deltaY = (int) (moveYLast - moveY);
			moveYLast = moveY;
			// Log.d(TAG, "======ACTION_MOVE====== deltaY=" + deltaY);
			// if (deltaY > 0 && expend) {
			// expend = false;
			//
			// ObjectAnimator.ofFloat(mSvContent, "translationY", 800, 0)
			// .setDuration(500).start();
			//
			// ObjectAnimator
			// .ofFloat(mSvHeader, "translationY",
			// top2Center4SvHeader, 0).setDuration(500)
			// .start();
			// }

			if (isNeedMove()) {
				int offset = mSvContent.getScrollY();
				if (offset < MAX_SCROLL_HEIGHT && offset > -MAX_SCROLL_HEIGHT) {

					if (deltaY < 0) {
						mSvContent.scrollBy(0, (int) (deltaY * SCROLL_RATIO));
						// mSvHeader.scrollBy(0, (int) (deltaY * 0.6));
						handleStop = false;

						// Log.d(TAG, "======ACTION_MOVE====== scrollBy="
						// + ((int) deltaY * SCROLL_RATIO));
					}

					if (onScrollPullOffsetListener != null) {
						onScrollPullOffsetListener.pullOffset(offset,
								mSvChildView);
					}

					// Log.d(TAG, "======commonOnTouchEvent ACTION_MOVE offset="
					// + offset);

					float setp = HotelDetailNotifyingScrollView.MAX_SCROLL_HEIGHT / 10;
					float per = Math.abs(offset / setp);
					scale = 1f + (per / 10f);

					if (scale >= SCALE_MAX) {
						scale = SCALE_MAX;
					}

					if (offset < 0) {
						isPull2Down = true;
						if (scale >= scaleLast) {
							// Log.d(TAG, "======pullOffset====== setp=" + setp
							// + " ,offset=" + offset + " ,per=" + per
							// + " ,scale=" + scale + " ,scaleLast="
							// + scaleLast);

							long duration = 5;

							// com.nineoldandroids.view.ViewPropertyAnimator
							// .animate(mSvHeader).setDuration(duration)
							// .scaleX(scale).scaleY(scale);

							ObjectAnimator
									.ofFloat(mSvHeader, "scaleX", scaleLast,
											scale).setDuration(duration)
									.start();
							ObjectAnimator
									.ofFloat(mSvHeader, "scaleY", scaleLast,
											scale).setDuration(duration)
									.start();

							scaleLast = scale;
						}
					} else {
						isPull2Down = false;
					}

					long duration = 700;
					if (deltaY < -10 && !expend) {
						expend = true;

						// Log.d(TAG, "======expend====== true");

						ObjectAnimator
								.ofFloat(mSvContent, "translationY", 0,
										top4SvContent).setDuration(duration)
								.start();

						// ObjectAnimator
						// .ofFloat(mSvHeader, "translationY", 0,
						// top2Center4SvHeader)
						// .setDuration(duration).start();

						com.nineoldandroids.view.ViewPropertyAnimator
								.animate(mSvHeader).setDuration(duration)
								.translationY(top2Center4SvHeader);

						// Log.d(TAG, "======pullOffset====== setp=" + setp
						// + " ,offset=" + offset + " ,per=" + per
						// + " ,scale2=" + scale + " ,scaleLast="
						// + scaleLast);

						// if (scale >= scaleLast) {

						// ObjectAnimator
						// .ofFloat(mSvHeader, "scaleX", scaleLast,
						// SCALE_MAX).setDuration(duration)
						// .start();
						// ObjectAnimator
						// .ofFloat(mSvHeader, "scaleY", scaleLast,
						// SCALE_MAX).setDuration(duration)
						// .start();

						com.nineoldandroids.view.ViewPropertyAnimator
								.animate(mSvHeader).setDuration(duration)
								.scaleX(SCALE_MAX).scaleY(SCALE_MAX);

						scaleLast = SCALE_MAX;
						// }

					} else if (deltaY > 0 && expend) {
						expend = false;

						// Log.d(TAG, "======expend====== false");

						ObjectAnimator
								.ofFloat(mSvContent, "translationY",
										top4SvContent, 0).setDuration(duration)
								.start();

						ObjectAnimator
								.ofFloat(mSvHeader, "translationY",
										top2Center4SvHeader, 0)
								.setDuration(duration).start();

						// Log.d(TAG, "======pullOffset====== setp=" + setp
						// + " ,offset=" + offset + " ,per=" + per
						// + " ,scale4=" + scale + " ,scaleLast="
						// + scaleLast);

						// com.nineoldandroids.view.ViewPropertyAnimator
						// .animate(mSvHeader).setDuration(duration)
						// .scaleX(SCALE_MIN).scaleY(SCALE_MIN);

						ObjectAnimator
								.ofFloat(mSvHeader, "scaleX", scaleLast,
										SCALE_MIN).setDuration(duration)
								.start();
						ObjectAnimator
								.ofFloat(mSvHeader, "scaleY", scaleLast,
										SCALE_MIN).setDuration(duration)
								.start();

						scaleLast = SCALE_MIN;
					}
				}
			}

			// // 上划
			// if ((nowY - downY) < 0) {
			// int scrollY = mSvContent.getScrollY();
			// if (scrollY != 0) {
			//
			// mSvContent.scrollBy(0, (int) (deltaY * SCROLL_RATIO));
			// int pading = (int) (Math.abs(scrollY) - (downY - nowY));
			//
			// Log.d(TAG, "======commonOnTouchEvent====== padding="
			// + pading + " ,downY=" + downY + " ,nowY=" + nowY
			// + " ,scrollY=" + scrollY);
			//
			// if (pading <= top2Center4SvHeader) {
			// mSvChildView.setPadding(0, pading, 0, 0);
			// }
			//
			// // animation();
			// }
			// }
			//
			// int offset = mSvContent.getScrollY();
			//
			// if (isNeedMove()) {
			// // int offset = mSvChildView.getScrollY();
			//
			// Log.d(TAG, "======commonOnTouchEvent ACTION_MOVE offset="
			// + offset);
			//
			// if (offset < 0) {
			// isSlide2Down = true;
			// }
			//
			// if (offset < MAX_SCROLL_HEIGHT && offset > -MAX_SCROLL_HEIGHT) {
			// // mSvChildView.scrollBy(0, (int) (deltaY * SCROLL_RATIO));
			// mSvContent.scrollBy(0, (int) (deltaY * SCROLL_RATIO));
			// handleStop = false;
			//
			// int paddingTop = mSvChildView.getPaddingTop();
			// if (offset < 0 && paddingTop < top2Center4SvHeader) {
			//
			// // int per = top2Center4SvHeader / offset;
			// int per = top2Center4SvHeader / MAX_SCROLL_HEIGHT;
			//
			// padingTop = Math.abs(offset*per);
			//
			// mSvChildView.setPadding(0, padingTop, 0, 0);
			// }
			//
			// if (onScrollPullOffsetListener != null) {
			// onScrollPullOffsetListener.pullOffset(offset,
			// mSvChildView);
			// // onScrollPullOffsetListener.pullOffset(offset,mSvChildView);
			// }
			// }
			// }
			break;
		default:
			break;
		}
	}

	private boolean isNeedMove() {
		// int viewHight = mSvChildView.getMeasuredHeight();
		int viewHight = mSvContent.getMeasuredHeight();
		int srollHight = getHeight();
		int offset = viewHight - srollHight;
		int scrollY = getScrollY();

		// Log.d(TAG, "======isNeedMove====== scrollY=" + scrollY
		// + " ,srollHight=" + srollHight + " ,offset=" + offset);

		if (scrollY > scrollYLast) {
			// Log.d(TAG, "======isNeedMove====== scrollYLast=" + scrollYLast);
			scrollYLast = scrollY;
		}

		if (scrollY == 0 || scrollY == offset) {
			scrollYLast = scrollY;

			return true;
		}

		// if(expend){
		// return true;
		// }

		return false;
	}

	private void animation() {
		// scrollY = mSvChildView.getScrollY();
		scrollY = mSvContent.getScrollY();
		scrollYHeader = mSvHeader.getScrollY();

		eachStep = scrollY / 10;
		eachStepHeader = scrollYHeader / 10;
		resetPositionHandler.sendEmptyMessage(0);
	}

	Handler resetPositionHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (scrollY != 0 && handleStop) {
				scrollY -= eachStep;
				scrollYHeader -= eachStepHeader;

				if ((eachStep < 0 && scrollY > 0)
						|| (eachStep > 0 && scrollY < 0)) {
					scrollY = 0;
				}

				if ((eachStepHeader < 0 && scrollYHeader > 0)
						|| (eachStepHeader > 0 && scrollYHeader < 0)) {
					scrollYHeader = 0;
				}

				// Log.d(TAG, "======resetPositionHandler====== scrollY="
				// + scrollY);

				float setp = HotelDetailNotifyingScrollView.MAX_SCROLL_HEIGHT / 10;
				float per = Math.abs(scrollY / setp);
				scale = 1f + (per / 10f);

				if (scale <= scaleLast && !expend && isPull2Down) {
					ObjectAnimator
							.ofFloat(mSvHeader, "scaleX", scaleLast, scale)
							.setDuration(10).start();
					ObjectAnimator
							.ofFloat(mSvHeader, "scaleY", scaleLast, scale)
							.setDuration(10).start();

					scaleLast = scale;

					// Log.d(TAG, "======pullOffset====== setp=" + setp +
					// " ,per="
					// + per + " ,scale3=" + scale + " ,scaleLast="
					// + scaleLast);
				}

				// mSvChildView.scrollTo(0, scrollY);
				mSvContent.scrollTo(0, scrollY);
				mSvHeader.scrollTo(0, scrollYHeader);
				// this.sendEmptyMessageDelayed(0, 5);
				this.sendEmptyMessageDelayed(0, 10);
			}
		};
	};
}