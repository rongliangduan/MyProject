package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;

public class PullToRefreshView extends PullToRefreshBase<RelativeLayout> {

	public PullToRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshView(Context context, Mode mode,
			AnimationStyle animStyle) {
		super(context, mode, animStyle);
	}

	public PullToRefreshView(Context context, Mode mode) {
		super(context, mode);
	}

	public PullToRefreshView(Context context) {
		super(context);
	}

	@Override
	public Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected RelativeLayout createRefreshableView(Context context,
			AttributeSet attrs) {
		return new RelativeLayout(context, attrs);
	}

	@Override
	protected boolean isReadyForPullEnd() {
		return mRefreshableView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullStart() {
		return true;
	}

}
