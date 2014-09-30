package cn.op.common;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class BasePagerAdapter<R extends Object> extends PagerAdapter {

	public List<R> data;

	public BasePagerAdapter(List<R> data) {
		super();
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	public R getItem(int position) {
		return data.get(position);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

}
