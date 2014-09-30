package cn.op.zdf.event;

import java.util.ArrayList;
import java.util.List;

import cn.op.zdf.domain.Item;

public class ToggleHotelMapEvent extends Event {
	public List<Item> list = new ArrayList<Item>();

	public ToggleHotelMapEvent(List<Item> mHotelList) {
		super();
		this.list = mHotelList;
	}

	public ToggleHotelMapEvent() {
	}
}
