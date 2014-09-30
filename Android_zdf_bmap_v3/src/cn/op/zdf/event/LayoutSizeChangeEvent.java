package cn.op.zdf.event;

public class LayoutSizeChangeEvent  extends Event{
	public boolean isSoftInputOpen = false;

	public LayoutSizeChangeEvent(boolean isSoftInputOpen) {
		super();
		this.isSoftInputOpen = isSoftInputOpen;
	}

}
