package cn.op.zdf.event;

import cn.op.common.AppException;
import cn.op.zdf.domain.ItemPage;

public class RspHotelPageEvent extends Event {

	public static final int CODE_EXCEPTION = -1;
	public static final int CODE_SUCCESS = 1;
	public ItemPage hotelPage;
	public AppException exception;
	public int code;

}
