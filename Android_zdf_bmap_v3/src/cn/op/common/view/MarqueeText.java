package cn.op.common.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 走马灯TextView ,需要在xml中设置以下属性
 * </br>android:ellipsize="marquee" </br>android:focusable="true"
 * </br>android:focusableInTouchMode="true"
 * </br>android:marqueeRepeatLimit="marquee_forever"
 * </br>android:scrollHorizontally="true" android:singleLine="true"
 * 
 * @author lufei
 * 
 */
public class MarqueeText extends TextView {
	public MarqueeText(Context con) {
		super(con);
	}

	public MarqueeText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MarqueeText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFocused() {
		return true;
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
	}
}
