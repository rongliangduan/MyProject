package cn.op.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import cn.op.zdf.R;

public class SideBar extends View {
	private String TAG = "SideBar";

	private char[] letter;
	private SectionIndexer sectionIndexter = null;
	private ListView list;
	private int m_nItemHeight = 16;

	public SideBar(Context context) {
		super(context);
		init();
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		letter = new char[] { '热', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
				'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
				'V', 'W', 'X', 'Y', 'Z' };
		setBackgroundColor(getResources().getColor(R.color.transparent));
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void setListView(ListView _list) {
		list = _list;
		ListAdapter adapter = _list.getAdapter();

		sectionIndexter = (SectionIndexer) _list.getAdapter();

		// if(adapter instanceof ListAdapterWrapper){
		// ListAdapterWrapper listAdapterWrapper =
		// (org.holoeverywhere.widget.ListAdapterWrapper) adapter;
		// sectionIndexter= (SectionIndexer)
		// listAdapterWrapper.getWrappedAdapter();
		// }else{
		// sectionIndexter = (SectionIndexer) _list.getAdapter();
		// }
	}

	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int i = (int) event.getY();
		int idx = i / m_nItemHeight;
		if (idx >= letter.length) {
			idx = letter.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			if (sectionIndexter == null) {
				sectionIndexter = (SectionIndexer) list.getAdapter();
			}
			int position = sectionIndexter.getPositionForSection(letter[idx]);
			if (position == -1) {
				return true;
			}
			list.setSelection(position);
		}
		return true;
	}

	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();

		paint.setColor(getResources().getColor(R.color.tv_slide_bar));
		paint.setTextSize(m_nItemHeight / 1.7f);
		paint.setTextAlign(Paint.Align.CENTER);
		float widthCenter = getMeasuredWidth() / 2;
		for (int i = 0; i < letter.length; i++) {
			canvas.drawText(String.valueOf(letter[i]), widthCenter,
					m_nItemHeight + (i * m_nItemHeight), paint);
		}
		super.onDraw(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		m_nItemHeight = h / letter.length;
	}
}