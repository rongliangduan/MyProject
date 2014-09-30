package cn.op.zdf.event;

public class MenuSelectEvent extends Event{
	public int menuSelectIndex;

	public MenuSelectEvent(int indexSelected) {
		super();
		this.menuSelectIndex = indexSelected;
	}

}
