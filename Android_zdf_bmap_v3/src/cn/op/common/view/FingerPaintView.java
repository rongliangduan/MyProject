package cn.op.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class FingerPaintView extends View {

	private static final float MINP = 0.25f;
	private static final float MAXP = 0.75f;

	private Paint mPaint;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;

	public FingerPaintView(Context c) {
		super(c);
		init();
	}

	public FingerPaintView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public FingerPaintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);

	}

	/**
	 * Clearing canvas
	 * </br>http://stackoverflow.com/questions/4650755/clearing-canvas
	 * -with-canvas-drawcolorf
	 */
	public void clear() {
		if (mCanvas != null) {
			Paint paint = new Paint();
			paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			mCanvas.drawPaint(paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.drawColor(0xFFAAAAAA);

		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

		canvas.drawPath(mPath, mPaint);
	}

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);
		// kill this so we don't double draw
		mPath.reset();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}
		return true;
	}
}
