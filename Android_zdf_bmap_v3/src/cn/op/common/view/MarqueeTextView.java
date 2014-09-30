package cn.op.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 走马灯TextView：可在ListView中使用
 * 或者直接用普通TextView，然后在adapter中调用tv.setSelected(true);
 * @author lufei
 *
 */
public class MarqueeTextView extends TextView {

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		rotate();
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		rotate();
	}

	public MarqueeTextView(Context context) {
		super(context);
		init();
		rotate();
	}

	private void rotate() {
		setSelected(true);
	}

	private void init() {
		if (!isInEditMode()) {

		}
	}

}