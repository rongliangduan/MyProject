package cn.op.zdf.event;

import cn.op.zdf.domain.City;

public class LocChooseEvent  extends Event{
	public City loc;

	public LocChooseEvent(City city) {
		super();
		this.loc = city;
	}
	
}
