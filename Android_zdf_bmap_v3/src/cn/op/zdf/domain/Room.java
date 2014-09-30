package cn.op.zdf.domain;

import java.io.Serializable;
import java.util.Calendar;

import cn.op.common.domain.RspMsg;
import cn.op.common.util.DateUtil;
import cn.op.common.util.StringUtils;

public class Room implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int SALE_TYPE_ZDF = 1;
	public static final int SALE_TYPE_WYF = 2;
	public static final int SALE_TYPE_LSF = 3;

	// 支持的支付方式
	public static final int SUPPORT_PAY_WAY_ARRIVE = 42;
	public static final int SUPPORT_PAY_WAY_ONLINE = 43;
	public static final int SUPPORT_PAY_WAY_ALL = 323;

	// 标准销售时间段
	public static final String SALE_DATE_INCLUDE_ZDF = "0800-1800";
	public static final String SALE_DATE_INCLUDE_WYF = "1800-0000";
	public static final String SALE_DATE_INCLUDE_LSF = "0000-0800";

	// 显示时间
	public static final String SALE_DATE_INCLUDE_ZDF_SHOW = "08:00—18:00";
	public static final String SALE_DATE_INCLUDE_WYF_SHOW = "18:00—次日12:00";
	public static final String SALE_DATE_INCLUDE_LSF_SHOW = "00:00—08:00";

	// 查询销售时间段
	private static final int SALE_HOUR_BEGIN_ZDF = 8;
	public static final int SALE_HOUR_END_ZDF = 18;

	public static final int SALE_HOUR_BEGIN_WYF = 18;
	public static final int SALE_HOUR_END_WYF = 0;

	private static final int SALE_HOUR_BEGIN_LSF = 0;
	private static final int SALE_HOUR_END_LSF = 8;

	// 标准销售时间段
	// private static final int HOUR_BEGIN_ZDF = 8;
	// public static final int HOUR_END_ZDF = 18;
	//
	// public static final int HOUR_BEGIN_WYF = 18;
	// public static final int HOUR_END_WYF = 0;
	//
	// private static final int HOUR_BEGIN_LSF = 0;
	// private static final int HOUR_END_LSF = 8;

	public static final int ROOM_TYPE_DRJ = 15;
	public static final int ROOM_TYPE_BZJ = 2;
	public static final int ROOM_TYPE_SRJ = 18;
	public static final int ROOM_TYPE_DCJ = 14;
	public static final int ROOM_TYPE_HHJ = 19;

	/**
	 * 钟点房在线支付截止时间，下午4点
	 */
	public static final int HOUR_END_PAY_ONLINE_ZDF = 18;

	private static final int HOUR_BEGIN_PAY_ONLINE_ZDF = 8;

	/**
	 * 商务类型-团购
	 */
	public static final String BUSINESS_TYPE_TUAN = "1";

	/**
	 * 商务类型-其他
	 */
	public static final String BUSINESS_TYPE_OTHER = "0";

	public RspMsg rspMsg = new RspMsg();
	/**
	 * 房间销售id
	 */
	public String saleId;
	/**
	 * 携程id
	 */
	public String hotelCode;

	public String roomPlanId;
	/**
	 * 房间类型名称
	 */
	public String roomTypeName;
	/**
	 * 销售类型
	 */
	public int sellType;
	/**
	 * 房间类型
	 */
	public int roomType;
	/**
	 * 剩余数量
	 */
	public int roomCount;
	/**
	 * 时间段
	 */
	private String hotelsRegion;
	/**
	 * 房间现价
	 */
	public String roomPrice;
	/**
	 * 房间原价
	 */
	public String salePrice;
	/**
	 * 钟点时长
	 */
	public String hourDuration;
	public String priceYiLong;
	public String priceXiecheng;
	public String icRes;

	public boolean wifi;
	public boolean park = true;
	public boolean subway;
	public boolean sendFood;
	public boolean breakfest;
	public boolean safe;
	public boolean wash;
	// temp
	public int iconRes;

	/**
	 * 是否来自于团购，，团购支付只支持在线支付
	 */
	public String business_type;

	/**
	 * 是否属于担保支付，担保支付只支持在线支付
	 */
	// public boolean isGuarantee;

	/**
	 * 支持的支付方式
	 */
	public int isPrepaid;

	/**
	 * 钟点房最晚退房时间,格式：HHmm
	 */
	private String hourEndTime;
	/**
	 * 钟点房最早开始时间,格式：HHmm
	 */
	private String hourStartTime;

	public Room() {
		super();
	}

	public Room(String title, int type1, int type2, String time, String price,
			String unit, int icRes, boolean wifi, boolean sendFood,
			boolean breakfest, boolean safe, boolean wash, int remain) {
		super();
		this.roomTypeName = title;
		this.sellType = type1;
		this.roomType = type2;
		this.hotelsRegion = time;
		this.roomPrice = price;
		this.hourDuration = unit;
		this.iconRes = icRes;
		this.wifi = wifi;
		this.sendFood = sendFood;
		this.breakfest = breakfest;
		this.safe = safe;
		this.wash = wash;
		this.roomCount = remain;
	}

	/**
	 * 当前是否属于午夜房销售时段
	 * 
	 * @return
	 */
	public static boolean isSellWyf() {
		int hour = DateUtil.getHour(Calendar.getInstance());

		return hour >= Room.SALE_HOUR_BEGIN_WYF;
	}

	/**
	 * 当前时间是否属于销售钟点房时段
	 * 
	 * @return
	 */
	public static boolean isSellZdf() {
		int hour = DateUtil.getHour(Calendar.getInstance());

		return hour >= Room.SALE_HOUR_BEGIN_ZDF
				&& hour < Room.SALE_HOUR_END_ZDF;

	}

	public static boolean isSellLsf() {
		int hour = DateUtil.getHour(Calendar.getInstance());

		return hour >= Room.SALE_HOUR_BEGIN_LSF
				&& hour < Room.SALE_HOUR_END_LSF;
	}

	public static int getCurtSellType() {
		int sellType = SALE_TYPE_ZDF;

		if (isSellWyf()) {
			sellType = SALE_TYPE_WYF;
		} else if (isSellLsf()) {
			sellType = SALE_TYPE_LSF;
		} else if (isSellZdf()) {
			sellType = SALE_TYPE_ZDF;
		}

		return sellType;
	}

	/**
	 * 8~16 钟点房在线支付时限
	 * 
	 * @return
	 */
	public static boolean isTimePayOnlineZdf() {
		int hour = DateUtil.getHour(Calendar.getInstance());
		return hour >= Room.HOUR_BEGIN_PAY_ONLINE_ZDF
				&& hour < Room.HOUR_END_PAY_ONLINE_ZDF;
	}

	// public static Room parseRoom(SoapObject result) {
	// Room u = new Room();
	//
	// u.saleId = result.getPrimitivePropertySafelyAsString("saleId");
	// u.sellType = StringUtils.toInt(result
	// .getPrimitivePropertySafelyAsString("sellType"));
	// u.roomCount = StringUtils.toInt(result
	// .getPrimitivePropertySafelyAsString("roomCount"));
	// u.hourDuration = result
	// .getPrimitivePropertySafelyAsString("hourDuration");
	// u.salePrice = result.getPrimitivePropertySafelyAsString("salePrice");
	// u.roomPrice = result.getPrimitivePropertySafelyAsString("roomPrice");
	// u.roomTypeName = result
	// .getPrimitivePropertySafelyAsString("roomTypeName");
	// u.isPrepaid = StringUtils.toInt(result
	// .getPrimitivePropertySafelyAsString("isPrepaid"));
	// u.hourStartTime = result
	// .getPrimitivePropertySafelyAsString("hourStartTime");
	// u.hourEndTime = result
	// .getPrimitivePropertySafelyAsString("hourEndTime");
	//
	// return u;
	// }

	// public static Room parseRoom(String xml) throws AppException {
	// Room item = new Room();
	//
	// try {
	// XmlPullParser parser = XmlPullParserFactory.newInstance()
	// .newPullParser();
	// StringReader stringReader = new StringReader(xml);
	// parser.setInput(stringReader);
	// int eventType = parser.getEventType();// 获取事件类型
	//
	// while (eventType != XmlPullParser.END_DOCUMENT) {
	// String name = null;
	// switch (eventType) {
	//
	// case XmlPullParser.START_DOCUMENT:
	//
	// break;
	// case XmlPullParser.START_TAG:
	// name = parser.getName();
	//
	// if (name.equals("Item")) {
	// item = new Room();
	// } else if (item != null) {
	// if (name.equals("saleId")) {
	// item.saleId = parser.nextText();
	// } else if (name.equals("hotelsNum")) {
	// item.roomCount = StringUtils.toInt(parser
	// .nextText());
	// } else if (name.equals("hotelsRegion")) {
	// item.hotelsRegion = parser.nextText();
	// } else if (name.equals("hotelsDuration")) {
	// item.hourDuration = parser.nextText();
	// } else if (name.equals("roomType")) {
	// item.roomType = StringUtils
	// .toInt(parser.nextText());
	// } else if (name.equals("roomTypeName")) {
	// item.roomTypeName = parser.nextText();
	// } else if (name.equals("roomPrice")) {
	// item.roomPrice = parser.nextText();
	// if (StringUtils.isEmpty(item.roomPrice)) {
	// item.roomPrice = Item.DEFAULT_ZDF_PRICE;
	// }
	// } else if (name.equals("salePrice")) {
	// item.salePrice = parser.nextText();
	// } else if (name.equals("hotelCode")) {
	// item.hotelCode = parser.nextText();
	// } else if (name.equals("sellType")) {
	// item.sellType = StringUtils
	// .toInt(parser.nextText());
	// } else if (name.equals("roomPlanId")) {
	// item.roomPlanId = parser.nextText();
	// } else if (name.equals("isPrepaid")) {
	// item.isPrepaid = StringUtils.toInt(parser
	// .nextText());
	// } else if (name.equals("hourEndTime")) {
	// item.hourEndTime = parser.nextText();
	// }
	//
	// }
	// break;
	//
	// case XmlPullParser.END_TAG:
	// name = parser.getName();
	// break;
	// }
	// eventType = parser.next();
	// }
	// stringReader.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw AppException.xml(e);
	// }
	// return item;
	// }

	public String getHourEndTime() {
		if (!StringUtils.isEmpty(hourEndTime) && hourEndTime.length() == 4) {
			hourEndTime = hourEndTime.substring(0, 2) + ":"
					+ hourEndTime.substring(2);
		}

		return hourEndTime;
	}

	public void setHourStartTime(String hourStartTime) {
		this.hourStartTime = hourStartTime;
	}

	public void setHourEndTime(String hourEndTime) {
		this.hourEndTime = hourEndTime;

	}
}
