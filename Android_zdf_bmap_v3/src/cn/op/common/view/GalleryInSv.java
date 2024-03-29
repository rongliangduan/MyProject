package cn.op.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

/**
 * 解决在ScrollView中左右滑动冲突问题
 * @author lufei
 *
 */
public class GalleryInSv extends Gallery {
	private static final String TAG = GalleryInSv.class.getSimpleName();
	private float gTouchStartX;
	private float gTouchStartY;

	public GalleryInSv(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GalleryInSv(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GalleryInSv(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			gTouchStartX = ev.getX();
			gTouchStartY = ev.getY();
			super.onTouchEvent(ev);
			break;
		case MotionEvent.ACTION_MOVE:
			final float touchDistancesX = Math.abs(ev.getX() - gTouchStartX);
			final float touchDistancesY = Math.abs(ev.getY() - gTouchStartY);
			if (touchDistancesY * 2 >= touchDistancesX) {
				
				Log.d(TAG, "======ACTION_MOVE====== false");
				return false;
			} else {
				Log.d(TAG, "======ACTION_MOVE====== true");
				return true;
			}
		case MotionEvent.ACTION_CANCEL:
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e2.getX() > e1.getX()) {
			onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
		} else {
			onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
		}
		
		Log.d(TAG, "======onFling======");
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, "======onTouchEvent======");
		return super.onTouchEvent(event);
	}

}
