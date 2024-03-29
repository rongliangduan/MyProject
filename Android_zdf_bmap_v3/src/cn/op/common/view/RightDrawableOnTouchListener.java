package cn.op.common.view;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public abstract class RightDrawableOnTouchListener implements OnTouchListener {
	Drawable drawable;
	private int fuzz = 10;

	/**
	 * @param keyword
	 */
	public RightDrawableOnTouchListener(TextView view) {
		super();
		final Drawable[] drawables = view.getCompoundDrawables();
		if (drawables != null && drawables.length == 4)
			this.drawable = drawables[2];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(final View v, final MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN && drawable != null) {
			final int x = (int) event.getX();
			final int y = (int) event.getY();
//			final int x = (int) v.getLeft();
//			final int y = (int) v.getTop();
			final Rect bounds = drawable.getBounds();
			if (x >= (v.getWidth() - bounds.width() - fuzz)
					&& x <= (v.getWidth() - v.getPaddingRight() + fuzz)
					&& y >= (v.getPaddingTop() - fuzz)
					&& y <= (v.getHeight() - v.getPaddingBottom()) + fuzz) {
				return onDrawableTouch(event);
			}
		}
		return false;
	}

	public abstract boolean onDrawableTouch(final MotionEvent event);

}
