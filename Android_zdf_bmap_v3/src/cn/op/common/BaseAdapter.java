package cn.op.common;

import java.util.List;

public abstract class BaseAdapter<R extends Object> extends
		android.widget.BaseAdapter {

	public List<R> data;

	public BaseAdapter(List<R> data) {
		super();
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public R getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
