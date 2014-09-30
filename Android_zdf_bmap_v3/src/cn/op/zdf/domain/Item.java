package cn.op.zdf.domain;

import java.io.Serializable;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import cn.op.common.AppException;
import cn.op.common.domain.RspMsg;
import cn.op.common.domain.URLs;
import cn.op.common.util.DesUtils;
import cn.op.common.util.LatLngUtil;
import cn.op.common.util.Log;
import cn.op.common.util.StringUtils;

public class Item implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int ORDER_BY_PRICE = 0;
	public static final int ORDER_BY_DISTANCE = 1;
	public static final int ORDER_BY_COMMENT = 2;

	public RspMsg rspMsg = new RspMsg();
	public String hotelsId;
	public String brandName;
	public String hotelsName;
	public String hotelsTel;

	/**
	 * 是否选中,用于界面中ListView的选中状态
	 */
	public boolean isSelected;

	/**
	 * 酒店地址
	 */
	public String hotelsAddr;
	public String dist;
	/**
	 * 酒店logo
	 */
	public String logopath;
	public String brief;
	/**
	 * 酒店提供设施服务id,以英文逗号相连, 1,5,3
	 */
	public String facilitysIds;
	public String[] hotelsImgs = new String[] {};
	public double hotelsLatitude;
	public double hotelsLongitude;
	public int logoRes;
	public String hotelsStarlevel;

	public String STAR_LEVEL_1 = "27";
	public String STAR_LEVEL_2 = "29";
	public String STAR_LEVEL_3 = "31";
	public String STAR_LEVEL_4 = "33";
	public String STAR_LEVEL_5 = "35";

	public String brandId;
	public String brandMinLogo;
	public String brandMaxLogo;

	public String booksId;
	public int booksStatus;
	/**
	 * 入住时间
	 */
	public String bookStartTime;
	/**
	 * 离店时间
	 */
	public String bookEndDate;

	private String nightDate;
	private String dayDate;

	/**
	 * 是否有钟点房
	 */
	public boolean isHour;
	/**
	 * 是否有午夜房
	 */
	public boolean isSpecial;
	/**
	 * 是否有零时房
	 */
	public boolean isMidNight;

	/**
	 * 原价-钟点房
	 */
	public String hourSalePrice;
	/**
	 * 现价、折后价-钟点房
	 */
	public String hourroomPrice;
	public String hourpriceOriginal;

	/**
	 * 钟点房时长
	 */
	public String hours;

	/**
	 * 原价-午夜房
	 */
	public String daySalePrice;
	/**
	 * 现价、折后价-午夜房
	 */
	public String dayroomPrice;
	public String daypriceOriginal;

	/**
	 * 零时原价
	 */
	public String nightSalePrice;
	/**
	 * 零时现价
	 */
	public String nightroomPrice;
	public String nightpriceOriginal;

	public String imagesPath;

	/**
	 * 钟点房最晚退房时间,格式：HHmm
	 */
	private String hourEndTime;
	/**
	 * 钟点房最早开始预定时间最早,格式：HHmm
	 */
	private String hourStartTime;

	// public final static int ORDER_STATE_COMMIT = 1;

	/**
	 * 预订成功，等待到店支付入住
	 */
	public static final int ORDER_STATE_WAIT_ARRIVE_PAY_LIVE = 44;

	/**
	 * 预订并支付成功，等待入住
	 */
	public static final int ORDER_STATE_PAY_ONLINE_SUCCESS_WAIT_LIVE = 45;

	/**
	 * 预订并支付成功，等待确认
	 */
	public static final int ORDER_STATE_PAY_ONLINE_SUCCESS_WAIT_RESPONSE = 90;

	/**
	 * 订单完成,（成功入住）
	 */
	public final static int ORDER_STATE_DONE = 91;

	/**
	 * 订单过期, （客户端15分钟过期，后台20分钟过期）
	 */
	public static final int ORDER_STATE_PAY_ONLINE_TIME_OVER = 92;

	/**
	 * 取消订单
	 */
	public static final int ORDER_STATE_CANCEL = 93;

	/**
	 * 等待支付
	 */
	public static final int ORDER_STATE_WAIT_PAY_ONLINE = 126;
	/**
	 * 提交成功，等待确认
	 */
	public final static int ORDER_STATE_WAIT_RESPONSE = 133;
	/**
	 * 取消中,待审核
	 */
	public final static int ORDER_STATE_CANCEL_WAIT_RESPONSE = 328;

	/**
	 * 未入住
	 */
	public final static int ORDER_STATE_NOT_ARRIVE = 324;
	/**
	 * 已入住
	 */
	public final static int ORDER_STATE_ARRIVED = 330;
	/**
	 * 已离店
	 */
	public final static int ORDER_STATE_LEAVED = 331;

	public final static int ORDER_STATE_FAIL = 46;

	public static final int PAY_WAY_ONLINE = 43;
	public static final int PAY_WAY_ARRIVE = 42;

	public static final String HOTEL_UPDATE_TYPE_ADD = "112";
	public static final String HOTEL_UPDATE_TYPE_UPDATE = "113";
	public static final String HOTEL_UPDATE_TYPE_DELETE = "114";
	public static final String HOTEL_UPDATE_TYPE_CHECK = "136";
	public static final String HOTEL_UPDATE_TYPE_UNCHECK = "139";
	public static final String HOTEL_UPDATE_TYPE_USE = "137";
	public static final String HOTEL_UPDATE_TYPE_UNUSE = "140";

	private static final String TAG = Log.makeLogTag(Item.class);

	private static final String BRAND_GLHT = "109";
	private static final String BRAND_RJ = "101";

	public static final String DEFAULT_ZDF_PRICE = "80";

	public static final String DEFAULT_ZDF_HOUR = "4";

	/**
	 * 优惠券使用状态：未使用(可以使用)
	 */
	public static final int COUPON_USE_STATE_NOT_USE = 326;
	/**
	 * 优惠券使用状态：已使用
	 */
	public static final int COUPON_USE_STATE_USED = 327;

	/**
	 * 优惠券使用状态：已过期
	 */
	public static final int COUPON_USE_STATE_EXPIRE = 325;

	/**
	 * 优惠券类型：立减
	 */
	public static final int COUPON_TYPE_REDUCE_MONEY = 1;
	/**
	 * 优惠券类型：返现
	 */
	public static final int COUPON_TYPE_RETURN_MONEY = 2;

	public String commentTitle;
	public String comment;
	public String date;

	public boolean wifi;
	public boolean park;
	public boolean subway;

	/**
	 * @return yyyy-MM-dd
	 */
	public String getCouponEndData() {
		// TODO try cache 处理

		String date = "";
		if (!StringUtils.isEmpty(couponEndTime)) {
			couponEndTime = couponEndTime.trim();

			if (couponEndTime.contains("/")) {
				// 2013/10/12 21:06:00
				date = couponEndTime.replace("/", "-").split(" ")[0];

			} else {
				// 20131012210600
				date = couponEndTime.substring(0, 4) + "-"
						+ couponEndTime.substring(4, 6) + "-"
						+ couponEndTime.substring(6, 8);
			}

			// 2013-10-12
		}

		return date;
	}

	/**
	 * @return null or格式 2013-10-12 21:06
	 */
	public String getRoomUseDate() {
		String date = null;
		if (!StringUtils.isEmpty(bookStartTime)) {
			bookStartTime = bookStartTime.trim();

			if (bookStartTime.contains("/")) {
				// 2013-10-12 21:06:00
				date = bookStartTime.replace("/", "-");

			} else {
				// 201310122106
				date = bookStartTime.substring(0, 4) + "-"
						+ bookStartTime.substring(4, 6) + "-"
						+ bookStartTime.substring(6, 8) + " "
						+ bookStartTime.substring(8, 10) + ":"
						+ bookStartTime.substring(10, 12);
			}

			// 2013-10-12 21:06
		}

		return date;
	}

	public String getBookEndDate() {
		String date = "";
		if (!StringUtils.isEmpty(bookEndDate)) {
			bookEndDate = bookEndDate.trim();
			if (bookEndDate.contains("/")) {
				// 2013-10-12 21:06:00
				date = bookEndDate.replace("/", "-");

			} else {
				// 201310122106
				date = bookEndDate.substring(0, 4) + "-"
						+ bookEndDate.substring(4, 6) + "-"
						+ bookEndDate.substring(6, 8) + " "
						+ bookEndDate.substring(8, 10) + ":"
						+ bookEndDate.substring(10, 12);
			}
			// 2013-10-12 21:06
		}

		return date;
	}

	/**
	 * @return yyyy-MM-dd HH:mm
	 */
	public String getCommitBookTime() {
		String date = null;
		if (!StringUtils.isEmpty(getBookCommitTime())) {
			setBookCommitTime(getBookCommitTime().trim());
			// 201310122106
			date = getBookCommitTime().substring(0, 4) + "-"
					+ getBookCommitTime().substring(4, 6) + "-"
					+ getBookCommitTime().substring(6, 8) + " "
					+ getBookCommitTime().substring(8, 10) + ":"
					+ getBookCommitTime().substring(10, 12);
			// 2013-10-12 21:06
		}

		return date;
	}

	/**
	 * 入住订单价格
	 */
	public String priceOrder;
	/**
	 * 已优惠价格
	 */
	public String priceDiscount;
	/**
	 * 订单原价格
	 */
	public String price;

	// public String orderNum;
	/**
	 * 预订人
	 */
	public String bookPeople;
	/**
	 * 预订人手机号
	 */
	public String bookMobile;
	public String roomCount;

	// public String hotelName;

	// private String hotelPhone;

	public int payWay;

	public int roomType;

	public boolean collection;

	public int sellType;

	/**
	 * 入住人
	 */
	public String roomUserName;

	/**
	 * 入住人手机号
	 */
	public String roomUserMobile;

	public String operateType;

	/**
	 * 订单提交时间
	 */
	private String bookCommitTime;

	public String roomTypeName;

	/**
	 * 活动id
	 */
	public String eventId;

	/**
	 * 活动标题
	 */
	public String eventTitle;

	/**
	 * 活动开始时间
	 */
	public int startDate;

	/**
	 * 活动结束时间
	 */
	public String endDate;

	/**
	 * 活动备注
	 */
	public String remark;

	/**
	 * 活动图片路径
	 */
	public String filePath;

	/**
	 * 活动内容
	 */
	public String content;

	public String cityId;

	/**
	 * 是否有团购
	 */
	public boolean hasTuan;

	/**
	 * 酒店包含的钟点房时长类型,（4小时、6小时、3小时）以英文逗号相连 4,6,3
	 */
	public String zdfDurationType;

	/**
	 * 优惠金额（针对钟点房在线支付），0-表示无优惠
	 */
	public int discountOnline;

	/**
	 * 订单备注
	 */
	public String orderRemark;

	/**
	 * 优惠券Id
	 */
	public String couponId;
	/**
	 * 优惠券激活码
	 */
	public String couponKey;
	/**
	 * 优惠券使用状态
	 */
	public int couponUseState;
	/**
	 * 优惠券名称
	 */
	public String couponName;
	/**
	 * 优惠券说明
	 */
	public String couponBrief;
	/**
	 * 优惠券金额
	 */
	public int couponPrice;
	/**
	 * 优惠券开始时间
	 */
	public String couponStarTime;
	/**
	 * 优惠券结束时间
	 */
	private String couponEndTime;

	/**
	 * 仅限某品牌使用
	 */
	public String onlyBrandUse;
	/**
	 * 仅限某酒店使用
	 */
	public String onlyHotelUse;
	/**
	 * 仅限某销售类型使用
	 */
	public String onlySaleTypeUse;
	/**
	 * 优惠券类型：立减、返现
	 */
	public int couponType;

	/**
	 * 优惠券logo
	 */
	public String couponLogo;

	public String booksNum;

	/**
	 * 优惠券Id
	 */

	public Item(String commentTitle, String comment, String date) {
		super();
		this.commentTitle = commentTitle;
		this.comment = comment;
		this.date = date;
	}

	public Item(String brand, String title, String addr, String dist,
			String price, String priceOriginal, String tel, int logoRes,
			double latitude, double longitude, int orderState) {
		super();
		this.brandName = brand;
		this.hotelsName = title;
		this.hotelsAddr = addr;
		this.dist = dist;
		this.hourSalePrice = price;
		this.hourroomPrice = priceOriginal;
		this.hotelsTel = tel;
		this.logoRes = logoRes;
		this.hotelsLatitude = latitude;
		this.hotelsLongitude = longitude;
		this.booksStatus = orderState;
	}

	public Item() {
	}

	public Item(String brandName, String brandId) {
		this.brandName = brandName;
		this.brandId = brandId;
	}

	// public static Item parseCoupon(SoapObject result) {
	// Item u = new Item();
	//
	// u.couponId = result.getPrimitivePropertySafelyAsString("couponId");
	// u.couponKey = result.getPrimitivePropertySafelyAsString("couponKey");
	// u.couponUseState = StringUtils.toInt(result
	// .getPrimitivePropertySafelyAsString("useState"));
	// u.couponName = result.getPrimitivePropertySafelyAsString("couponName");
	// u.couponBrief = result
	// .getPrimitivePropertySafelyAsString("couponBrief");
	// u.couponPrice = StringUtils.toInt(result
	// .getPrimitivePropertySafelyAsString("couponPrice"));
	// u.couponStarTime = result
	// .getPrimitivePropertySafelyAsString("couponStarTime");
	// u.couponEndTime = result
	// .getPrimitivePropertySafelyAsString("couponEndTime");
	// u.onlyBrandUse = result
	// .getPrimitivePropertySafelyAsString("onlyBrandUse");
	// u.onlyHotelUse = result
	// .getPrimitivePropertySafelyAsString("onlyHotelUse");
	// u.onlySaleTypeUse = result
	// .getPrimitivePropertySafelyAsString("onlySaleTypeUse");
	// u.couponType = StringUtils.toInt(result
	// .getPrimitivePropertySafelyAsString("couponType"));
	// u.couponLogo = result.getPrimitivePropertySafelyAsString("couponLogo");
	//
	// return u;
	// }

	// public static Item parseOrder(SoapObject result) {
	// Item u = new Item();
	//
	// u.booksId = result.getPrimitivePropertySafelyAsString("booksId");
	// u.booksStatus = StringUtils.toIntAfterDecrypt(result
	// .getPrimitivePropertySafelyAsString("booksStatus"));
	// u.hotelsId = result.getPrimitivePropertySafelyAsString("hotelsId");
	// u.priceOrder = result.getPrimitivePropertySafelyAsString("priceOrder");
	// u.hotelsName = result.getPrimitivePropertySafelyAsString("hotelsName");
	// u.logopath = result.getPrimitivePropertySafelyAsString("logopath");
	// u.sellType = StringUtils.toIntAfterDecrypt(result
	// .getPrimitivePropertySafelyAsString("sellType"));
	// u.roomTypeName = result
	// .getPrimitivePropertySafelyAsString("roomTypeName");
	// u.hours = result.getPrimitivePropertySafelyAsString("hours");
	// u.setBookCommitTime(result
	// .getPrimitivePropertySafelyAsString("commitBookTime"));
	// u.bookStartTime = result
	// .getPrimitivePropertySafelyAsString("roomUseDate");
	// u.booksNum = result.getPrimitivePropertySafelyAsString("booksNum");
	//
	// u = decryptOrder(u);
	//
	// double[] toBaidu = LatLngUtil.fromGcjToBaidu(u.hotelsLatitude,
	// u.hotelsLongitude);
	// u.hotelsLatitude = toBaidu[0];
	// u.hotelsLongitude = toBaidu[1];
	//
	// return u;
	// }

	// public static Item parseOrderDetail(SoapObject result) {
	// Item u = new Item();
	// RspMsg msg = RspMsg.parse(result);
	// u.rspMsg = msg;
	//
	// u.booksId = result.getPrimitivePropertySafelyAsString("booksId");
	// u.booksStatus = StringUtils.toIntAfterDecrypt(result
	// .getPrimitivePropertySafelyAsString("bookStatus"));
	// u.payWay = StringUtils.toIntAfterDecrypt(result
	// .getPrimitivePropertySafelyAsString("payWay"));
	// u.setBookCommitTime(result
	// .getPrimitivePropertySafelyAsString("bookCommitTime"));
	// u.bookStartTime = result
	// .getPrimitivePropertySafelyAsString("bookStartTime");
	// u.bookEndDate = result
	// .getPrimitivePropertySafelyAsString("bookEndDate");
	// u.price = result.getPrimitivePropertySafelyAsString("price");
	// u.priceOrder = result.getPrimitivePropertySafelyAsString("priceOrder");
	// u.priceDiscount = result
	// .getPrimitivePropertySafelyAsString("priceDiscount");
	// u.hotelsId = result.getPrimitivePropertySafelyAsString("hotelsId");
	// u.hotelsTel = result
	// .getPrimitivePropertySafelyAsString("hotelsPhoneno");
	// u.hotelsName = result.getPrimitivePropertySafelyAsString("hotelsName");
	// u.brandName = result.getPrimitivePropertySafelyAsString("brandName");
	// u.logopath = result.getPrimitivePropertySafelyAsString("hotelsLogo");
	// u.hotelsLatitude = StringUtils.toDoubleAfterDecrypt(result
	// .getPrimitivePropertySafelyAsString("hotelsLatitude"));
	// u.hotelsLongitude = StringUtils.toDoubleAfterDecrypt(result
	// .getPrimitivePropertySafelyAsString("hotelsLongitude"));
	// u.hotelsAddr = result
	// .getPrimitivePropertySafelyAsString("hotelsPhyaddress");
	// u.sellType = StringUtils.toIntAfterDecrypt(result
	// .getPrimitivePropertySafelyAsString("sellType"));
	// u.roomTypeName = result
	// .getPrimitivePropertySafelyAsString("roomTypeName");
	// u.hours = result.getPrimitivePropertySafelyAsString("hours");
	// u.roomUserName = result
	// .getPrimitivePropertySafelyAsString("roomUserName");
	// u.roomUserMobile = result
	// .getPrimitivePropertySafelyAsString("userMobile");
	// u.bookPeople = result.getPrimitivePropertySafelyAsString("bookPeople");
	// u.bookMobile = result.getPrimitivePropertySafelyAsString("bookMobile");
	// u.roomCount = result.getPrimitivePropertySafelyAsString("roomCount");
	// u.orderRemark = result
	// .getPrimitivePropertySafelyAsString("specialDemands");
	// u.hourStartTime = result
	// .getPrimitivePropertySafelyAsString("hourStartTime");
	// u.hourEndTime = result
	// .getPrimitivePropertySafelyAsString("hourEndTime");
	// u.booksNum = result.getPrimitivePropertySafelyAsString("booksNum");
	//
	// u = decryptOrder(u);
	//
	// double[] toBaidu = LatLngUtil.fromGcjToBaidu(u.hotelsLatitude,
	// u.hotelsLongitude);
	// u.hotelsLatitude = toBaidu[0];
	// u.hotelsLongitude = toBaidu[1];
	//
	// return u;
	// }

	static Item decryptOrder(Item u) {
		// 解密
		if (URLs.IS_NEED_SECURITY) {
			u.hotelsAddr = DesUtils.decrypt(u.hotelsAddr);
			u.booksId = DesUtils.decrypt(u.booksId);
			u.hotelsId = DesUtils.decrypt(u.hotelsId);
			u.booksStatus = u.booksStatus;
			// u.orderNum = DesUtils.decrypt(u.orderNum);
			u.payWay = u.payWay;
			u.sellType = u.sellType;
			u.roomType = u.roomType;
			u.roomTypeName = DesUtils.decrypt(u.roomTypeName);
			u.hotelsName = DesUtils.decrypt(u.hotelsName);
			u.priceOrder = DesUtils.decrypt(u.priceOrder);
			u.priceDiscount = DesUtils.decrypt(u.priceDiscount);
			u.price = DesUtils.decrypt(u.price);
			u.hotelsTel = DesUtils.decrypt(u.hotelsTel);
			u.hotelsLatitude = u.hotelsLatitude;
			u.hotelsLongitude = u.hotelsLongitude;
			u.roomUserName = DesUtils.decrypt(u.roomUserName);
			u.roomUserMobile = DesUtils.decrypt(u.roomUserMobile);
			u.bookPeople = DesUtils.decrypt(u.bookPeople);
			u.bookMobile = DesUtils.decrypt(u.bookMobile);
			u.roomCount = DesUtils.decrypt(u.roomCount);
			u.bookStartTime = DesUtils.decrypt(u.bookStartTime);
			u.setBookCommitTime(DesUtils.decrypt(u.getBookCommitTime()));
			u.logopath = DesUtils.decrypt(u.logopath);
			u.bookEndDate = DesUtils.decrypt(u.bookEndDate);
			u.brandId = DesUtils.decrypt(u.brandId);
			u.brandName = DesUtils.decrypt(u.brandName);
			u.hours = DesUtils.decrypt(u.hours);
			u.hourEndTime = DesUtils.decrypt(u.hourEndTime);
			u.booksNum = DesUtils.decrypt(u.booksNum);
		}

		return u;
	}

	public static Item parseHotelDetail(String xml) throws AppException {
		Item u = new Item();
		RspMsg msg = RspMsg.parse(xml);

		if (!msg.OK()) {
			u.rspMsg = msg;
			return u;
		}

		try {
			XmlPullParser parser = XmlPullParserFactory.newInstance()
					.newPullParser();
			StringReader stringReader = new StringReader(xml);
			parser.setInput(stringReader);

			int eventType = parser.getEventType();// 获取事件类型

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = null;
				switch (eventType) {

				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();

					if (name.equals("return")) {
						u = new Item();
					} else if (u != null) {
						if (name.equals("brief")) {
							u.brief = parser.nextText();
						}

						else if (name.equals("facilitysIds")) {
							u.facilitysIds = parser.nextText();
						} else if (name.equals("discountOnline")) {
							u.discountOnline = StringUtils.toInt(parser
									.nextText());
						} else if (name.equals("hourStartTime")) {
							u.hourStartTime = parser.nextText();
						} else if (name.equals("hourEndTime")) {
							u.hourEndTime = parser.nextText();
						} else if (name.equals("hotelsImgs")) {
							String img = parser.nextText();

							if (!StringUtils.isEmpty(img)) {
								img = img.replaceAll("@2x", "");
								u.hotelsImgs = img.split(",");
							}
						}

					}
					break;

				case XmlPullParser.END_TAG:
					name = parser.getName();
					break;
				}
				eventType = parser.next();
			}
			stringReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.xml(e);
		}

		u.rspMsg = msg;

		return u;
	}

	// public static Item parseHotelDetail(SoapObject result) {
	// Item u = new Item();
	// RspMsg msg = RspMsg.parse(result);
	// u.rspMsg = msg;
	//
	// if (msg.OK()) {
	// u.brief = result.getPrimitivePropertySafelyAsString("brief");
	// u.facilitysIds = result
	// .getPrimitivePropertySafelyAsString("facilitysIds");
	// u.discountOnline = StringUtils.toInt(result
	// .getPrimitivePropertySafelyAsString("discountOnline"));
	// u.hourStartTime = result
	// .getPrimitivePropertySafelyAsString("hourStartTime");
	// u.hourEndTime = result
	// .getPrimitivePropertySafelyAsString("hourEndTime");
	//
	// String img = result
	// .getPrimitivePropertySafelyAsString("hotelsImgs");
	//
	// if (!StringUtils.isEmpty(img)) {
	// img = img.replaceAll("@2x", "");
	// u.hotelsImgs = img.split(",");
	// }
	//
	// // 临时：去掉无效图片地址，C:\Users\lanying1\Desktop\17.jpg
	// ArrayList<String> list = new ArrayList<String>(
	// Arrays.asList(u.hotelsImgs));
	// for (int i = 0; i < list.size(); i++) {
	// String imgUrl = list.get(i);
	// if (imgUrl.contains(":\\")) {
	// list.remove(i);
	// }
	// }
	// String[] imgArray = list.toArray(new String[] {});
	//
	// u.hotelsImgs = imgArray;
	//
	// }
	//
	// return u;
	// }

	// public static Item parseHotel(SoapObject result) {
	// Item u = new Item();
	//
	// u.hotelsId = result.getPrimitivePropertySafelyAsString("hotelsId");
	// u.hotelsName = result.getPrimitivePropertySafelyAsString("hotelsName");
	// u.roomType = StringUtils.toInt(result
	// .getPrimitivePropertySafelyAsString("roomType"));
	// u.brandId = result.getPrimitivePropertySafelyAsString("brandId");
	// u.brandName = result.getPrimitivePropertySafelyAsString("brandName");
	// u.dist = result.getPrimitivePropertySafelyAsString("dist");
	// u.hotelsLatitude = StringUtils.toDouble(result
	// .getPrimitivePropertySafelyAsString("hotelsLatitude"));
	// u.hotelsLongitude = StringUtils.toDouble(result
	// .getPrimitivePropertySafelyAsString("hotelsLongitude"));
	// u.facilitysIds = result
	// .getPrimitivePropertySafelyAsString("facilitysIds");
	// u.logopath = result.getPrimitivePropertySafelyAsString("logopath");
	// u.brief = result.getPrimitivePropertySafelyAsString("brief");
	// u.hotelsTel = result
	// .getPrimitivePropertySafelyAsString("hotelsPhoneno");
	// u.collection = StringUtils.toBool(result
	// .getPrimitivePropertySafelyAsString("collection"));
	// u.isHour = StringUtils.toBool(result
	// .getPrimitivePropertySafelyAsString("isHour"));
	// u.isSpecial = StringUtils.toBool(result
	// .getPrimitivePropertySafelyAsString("isSpecial"));
	// u.isMidNight = StringUtils.toBool(result
	// .getPrimitivePropertySafelyAsString("isMidNight"));
	// u.hotelsAddr = result
	// .getPrimitivePropertySafelyAsString("hotelsPhyaddress");
	// u.hourroomPrice = result
	// .getPrimitivePropertySafelyAsString("hourroomPrice");
	// u.hourSalePrice = result
	// .getPrimitivePropertySafelyAsString("hourSalePrice");
	// u.hours = result.getPrimitivePropertySafelyAsString("hours");
	// u.dayroomPrice = result
	// .getPrimitivePropertySafelyAsString("dayroomPrice");
	// u.daySalePrice = result
	// .getPrimitivePropertySafelyAsString("daySalePrice");
	// u.nightroomPrice = result
	// .getPrimitivePropertySafelyAsString("nightroomPrice");
	// u.nightSalePrice = result
	// .getPrimitivePropertySafelyAsString("nightSalePrice");
	// u.hotelsStarlevel = result
	// .getPrimitivePropertySafelyAsString("hotelsStarlevel");
	// u.operateType = result
	// .getPrimitivePropertySafelyAsString("operateType");
	// u.cityId = result.getPrimitivePropertySafelyAsString("cityId");
	// u.hasTuan = StringUtils.toBool(result
	// .getPrimitivePropertySafelyAsString("isCustomers"));
	// u.zdfDurationType = result
	// .getPrimitivePropertySafelyAsString("zdfDurationType");
	//
	// if (!StringUtils.isEmpty(u.logopath)) {
	// u.logopath = u.logopath.replaceAll("@2x", "");
	// }
	//
	// double[] toBaidu = LatLngUtil.fromGcjToBaidu(u.hotelsLatitude,
	// u.hotelsLongitude);
	// u.hotelsLatitude = toBaidu[0];
	// u.hotelsLongitude = toBaidu[1];
	//
	// return u;
	// }

	// public static Item parseHotel(String xml) throws AppException {
	// Item item = new Item();
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
	// item = new Item();
	// } else if (item != null) {
	// if (name.equals("hotelsId")) {
	// item.hotelsId = parser.nextText();
	// } else if (name.equals("hotelsName")) {
	// item.hotelsName = parser.nextText();
	// } else if (name.equals("roomType")) {
	// item.roomType = StringUtils
	// .toInt(parser.nextText());
	// } else if (name.equals("brandId")) {
	// item.brandId = parser.nextText();
	// } else if (name.equals("brandName")) {
	// item.brandName = parser.nextText();
	// } else if (name.equals("dist")) {
	// item.dist = parser.nextText();
	// } else if (name.equals("hotelsLatitude")) {
	// item.hotelsLatitude = StringUtils.toDouble(parser
	// .nextText());
	// } else if (name.equals("hotelsLongitude")) {
	// item.hotelsLongitude = StringUtils.toDouble(parser
	// .nextText());
	// } else if (name.equals("facilitysIds")) {
	// item.facilitysIds = parser.nextText();
	// } else if (name.equals("logopath")) {
	// item.logopath = parser.nextText();
	// } else if (name.equals("brief")) {
	// item.brief = parser.nextText();
	// } else if (name.equals("collection")) {
	// item.collection = StringUtils.toBool(parser
	// .nextText());
	// } else if (name.equals("hotelsPhoneno")) {
	// item.hotelsTel = parser.nextText();
	// } else if (name.equals("isHour")) {
	// item.isHour = StringUtils.toBool(parser.nextText());
	// } else if (name.equals("isSpecial")) {
	// item.isSpecial = StringUtils.toBool(parser
	// .nextText());
	// } else if (name.equals("isMidNight")) {
	// item.isMidNight = StringUtils.toBool(parser
	// .nextText());
	// } else if (name.equals("hotelsPhyaddress")) {
	// item.hotelsAddr = parser.nextText();
	// } else if (name.equals("hotelsStarlevel")) {
	// item.hotelsStarlevel = parser.nextText();
	// } else if (name.equals("hourroomPrice")) {
	// item.hourroomPrice = parser.nextText();
	// if (StringUtils.isEmpty(item.hourroomPrice)) {
	// item.hourroomPrice = DEFAULT_ZDF_PRICE;
	// }
	// } else if (name.equals("hourSalePrice")) {
	// item.hourSalePrice = parser.nextText();
	// } else if (name.equals("hours")) {
	// item.hours = parser.nextText();
	// if (StringUtils.isEmpty(item.hours)) {
	// item.hours = DEFAULT_ZDF_HOUR;
	// }
	//
	// } else if (name.equals("dayroomPrice")) {
	// item.dayroomPrice = parser.nextText();
	// } else if (name.equals("daySalePrice")) {
	// item.daySalePrice = parser.nextText();
	// } else if (name.equals("nightroomPrice")) {
	// item.nightroomPrice = parser.nextText();
	// } else if (name.equals("nightSalePrice")) {
	// item.nightSalePrice = parser.nextText();
	// } else if (name.equals("operateType")) {
	// item.operateType = parser.nextText();
	// } else if (name.equals("cityId")) {
	// item.cityId = parser.nextText();
	// } else if (name.equals("isCustomers")) {
	// item.hasTuan = StringUtils
	// .toBool(parser.nextText());
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
	//
	// double[] toBaidu = LatLngUtil.fromGcjToBaidu(item.hotelsLatitude,
	// item.hotelsLongitude);
	// item.hotelsLatitude = toBaidu[0];
	// item.hotelsLongitude = toBaidu[1];
	//
	// return item;
	// }

	// public static Item parseOrder(String xml) throws AppException {
	// Item item = new Item();
	//
	// try {
	//
	// Log.d(TAG, "======parseOrder====== " + xml);
	//
	// XmlPullParser parser = XmlPullParserFactory.newInstance()
	// .newPullParser();
	// StringReader stringReader = new StringReader(xml);
	// parser.setInput(stringReader);
	//
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
	// item = new Item();
	// } else if (item != null) {
	// if (name.equals("booksId")) {
	// item.booksId = parser.nextText();
	// } else if (name.equals("hotelsName")) {
	// item.hotelsName = parser.nextText();
	// } else if (name.equals("hotelsId")) {
	// item.hotelsId = parser.nextText();
	// } else if (name.equals("hotelsPhyaddress")) {
	// item.hotelsAddr = parser.nextText();
	// } else if (name.equals("booksStatus")) {
	// item.booksStatus = StringUtils
	// .toIntAfterDecrypt(parser.nextText());
	// }
	// // else if (name.equals("booksNo")) {
	// // item.orderNum = parser.nextText();
	// // }
	// else if (name.equals("booksStyle")) {
	// item.payWay = StringUtils.toIntAfterDecrypt(parser
	// .nextText());
	// } else if (name.equals("sellType")) {
	// item.sellType = StringUtils
	// .toIntAfterDecrypt(parser.nextText());
	// } else if (name.equals("roomType")) {
	// item.roomType = StringUtils
	// .toIntAfterDecrypt(parser.nextText());
	// } else if (name.equals("roomTypeName")) {
	// item.roomTypeName = parser.nextText();
	// } else if (name.equals("priceOrder")) {
	// item.priceOrder = parser.nextText();
	// } else if (name.equals("priceDiscount")) {
	// item.priceDiscount = parser.nextText();
	// } else if (name.equals("price")) {
	// item.price = parser.nextText();
	// } else if (name.equals("hotelsPhoneno")) {
	// item.hotelsTel = parser.nextText();
	// } else if (name.equals("hotelsLatitude")) {
	// item.hotelsLatitude = StringUtils
	// .toDoubleAfterDecrypt(parser.nextText());
	// } else if (name.equals("hotelsLongitude")) {
	// item.hotelsLongitude = StringUtils
	// .toDoubleAfterDecrypt(parser.nextText());
	// } else if (name.equals("roomUserName")) {
	// item.roomUserName = parser.nextText();
	// } else if (name.equals("userMobile")) {
	// item.roomUserMobile = parser.nextText();
	// } else if (name.equals("bookPeople")) {
	// item.bookPeople = parser.nextText();
	// } else if (name.equals("bookMobile")) {
	// item.bookMobile = parser.nextText();
	// } else if (name.equals("roomCount")) {
	// item.roomCount = parser.nextText();
	// } else if (name.equals("roomUseDate")) {
	// item.bookStartTime = parser.nextText();
	// } else if (name.equals("commitBookTime")) {
	// item.setBookCommitTime(parser.nextText());
	// } else if (name.equals("logopath")) {
	// item.logopath = parser.nextText();
	// } else if (name.equals("bookEndDate")) {
	// item.bookEndDate = parser.nextText();
	// } else if (name.equals("brandId")) {
	// item.brandId = parser.nextText();
	// } else if (name.equals("hours")) {
	// item.hours = parser.nextText();
	// } else if (name.equals("hourEndTime")) {
	// item.hourEndTime = parser.nextText();
	// }
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
	//
	// item = decryptOrder(item);
	//
	// double[] toBaidu = LatLngUtil.fromGcjToBaidu(item.hotelsLatitude,
	// item.hotelsLongitude);
	// item.hotelsLatitude = toBaidu[0];
	// item.hotelsLongitude = toBaidu[1];
	//
	// return item;
	// }

	/**
	 * 是否是桔家合作品牌
	 * 
	 * @param brandId
	 * @return
	 */
	public static boolean isJujiaLinkBrand(String brandId) {
		if (StringUtils.isEmpty(brandId)) {
			return false;
		}

		if (brandId.equals(Item.BRAND_RJ)) {
			return true;
		}

		// if (brandId.equals(Item.BRAND_GLHT)||brandId.equals(Item.BRAND_RJ)) {
		// return true;
		// }

		return false;
	}

	public static Item parseGift(String xml) throws AppException {
		Item item = new Item();

		try {
			XmlPullParser parser = XmlPullParserFactory.newInstance()
					.newPullParser();
			StringReader stringReader = new StringReader(xml);
			parser.setInput(stringReader);
			int eventType = parser.getEventType();// 获取事件类型

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = null;
				switch (eventType) {

				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();

					if (name.equals("Item")) {
						item = new Item();
					} else if (item != null) {
						if (name.equals("eventId")) {
							item.eventId = parser.nextText();
						} else if (name.equals("eventTitle")) {
							item.eventTitle = parser.nextText();
						} else if (name.equals("startDate")) {
							item.startDate = StringUtils.toInt(parser
									.nextText());
						} else if (name.equals("endDate")) {
							item.endDate = parser.nextText();
						} else if (name.equals("remark")) {
							item.remark = parser.nextText();
						} else if (name.equals("filePath")) {
							item.filePath = parser.nextText();
						} else if (name.equals("content")) {

							item.content = parser.nextText();
							// TODO
							String text = parser.getText();

						}

					}
					break;

				case XmlPullParser.END_TAG:
					name = parser.getName();
					break;
				}
				eventType = parser.next();
			}
			stringReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.xml(e);
		}
		return item;
	}

//	public static Item parseGift(SoapObject result) {
//		Item u = new Item();
//
//		u.eventId = result.getPrimitivePropertySafelyAsString("eventId");
//		u.eventTitle = result.getPrimitivePropertySafelyAsString("eventTitle");
//		u.startDate = StringUtils.toInt(result
//				.getPrimitivePropertySafelyAsString("startDate"));
//		u.endDate = result.getPrimitivePropertySafelyAsString("endDate");
//		u.remark = result.getPrimitivePropertySafelyAsString("remark");
//		u.filePath = result.getPrimitivePropertySafelyAsString("filePath");
//		u.content = result.getPrimitivePropertySafelyAsString("content");
//
//		return u;
//	}

//	public static Item parseGiftDetail(SoapObject result) {
//		Item u = new Item();
//		RspMsg msg = RspMsg.parse(result);
//		u.rspMsg = msg;
//
//		if (msg.OK()) {
//			u.eventId = result.getPrimitivePropertySafelyAsString("eventId");
//			u.eventTitle = result
//					.getPrimitivePropertySafelyAsString("eventTitle");
//			u.startDate = StringUtils.toInt(result
//					.getPrimitivePropertySafelyAsString("startDate"));
//			u.endDate = result.getPrimitivePropertySafelyAsString("endDate");
//			u.remark = result.getPrimitivePropertySafelyAsString("remark");
//			u.filePath = result.getPrimitivePropertySafelyAsString("filePath");
//			u.content = result.getPrimitivePropertySafelyAsString("content");
//		}
//
//		return u;
//	}

	public String getHourEndTime() {
		if (!StringUtils.isEmpty(hourEndTime) && hourEndTime.length() == 4) {
			hourEndTime = hourEndTime.substring(0, 2) + ":"
					+ hourEndTime.substring(2);
		}

		return hourEndTime;
	}

	public String getHourStartTime() {
		if (!StringUtils.isEmpty(hourStartTime) && hourStartTime.length() == 4) {
			hourStartTime = hourStartTime.substring(0, 2) + ":"
					+ hourStartTime.substring(2);
		}

		return hourStartTime;
	}

	public static Item parseOrderDetail(String xml) throws AppException {
		Item u = null;
		RspMsg msg = RspMsg.parse(xml);

		try {
			XmlPullParser parser = XmlPullParserFactory.newInstance()
					.newPullParser();
			StringReader stringReader = new StringReader(xml);
			parser.setInput(stringReader);

			int eventType = parser.getEventType();// 获取事件类型

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = null;
				switch (eventType) {

				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();

					if (name.equals("return")) {
						u = new Item();
					} else if (u != null) {
						if (name.equals("booksId")) {
							u.booksId = parser.nextText();
						} else if (name.equals("bookStatus")) {
							u.booksStatus = StringUtils
									.toIntAfterDecrypt(parser.nextText());
						} else if (name.equals("payWay")) {
							u.payWay = StringUtils.toIntAfterDecrypt(parser
									.nextText());
						} else if (name.equals("bookCommitTime")) {
							u.setBookCommitTime(parser.nextText());
						} else if (name.equals("bookStartTime")) {
							u.bookStartTime = parser.nextText();
						} else if (name.equals("bookEndDate")) {
							u.bookEndDate = parser.nextText();
						} else if (name.equals("price")) {
							u.price = parser.nextText();
						} else if (name.equals("priceOrder")) {
							u.priceOrder = parser.nextText();
						} else if (name.equals("priceDiscount")) {
							u.priceDiscount = parser.nextText();
						} else if (name.equals("hotelsId")) {
							u.hotelsId = parser.nextText();
						} else if (name.equals("hotelsPhoneno")) {
							u.hotelsTel = parser.nextText();
						} else if (name.equals("hotelsName")) {
							u.hotelsName = parser.nextText();
						} else if (name.equals("brandName")) {
							u.brandName = parser.nextText();
						} else if (name.equals("hotelsLogo")) {
							u.logopath = parser.nextText();
						} else if (name.equals("hotelsLatitude")) {
							u.hotelsLatitude = StringUtils
									.toDoubleAfterDecrypt(parser.nextText());
						} else if (name.equals("hotelsLongitude")) {
							u.hotelsLongitude = StringUtils
									.toDoubleAfterDecrypt(parser.nextText());
						} else if (name.equals("hotelsPhyaddress")) {
							u.hotelsAddr = parser.nextText();
						} else if (name.equals("sellType")) {
							u.sellType = StringUtils.toIntAfterDecrypt(parser
									.nextText());
						} else if (name.equals("roomTypeName")) {
							u.roomTypeName = parser.nextText();
						} else if (name.equals("hours")) {
							u.hours = parser.nextText();
						} else if (name.equals("roomUserName")) {
							u.roomUserName = parser.nextText();
						} else if (name.equals("userMobile")) {
							u.roomUserMobile = parser.nextText();
						} else if (name.equals("bookPeople")) {
							u.bookPeople = parser.nextText();
						} else if (name.equals("bookMobile")) {
							u.bookMobile = parser.nextText();
						} else if (name.equals("roomCount")) {
							u.roomCount = parser.nextText();
						} else if (name.equals("specialDemands")) {
							u.orderRemark = parser.nextText();
						} else if (name.equals("hourStartTime")) {
							u.hourStartTime = parser.nextText();
						} else if (name.equals("hourEndTime")) {
							u.hourEndTime = parser.nextText();
						} else if (name.equals("booksNum")) {
							u.booksNum = parser.nextText();
						}
					}
					break;

				case XmlPullParser.END_TAG:
					name = parser.getName();
					break;
				}
				eventType = parser.next();
			}
			stringReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.xml(e);
		}

		if (u == null) {
			u = new Item();
		}

		u.rspMsg = msg;

		u = decryptOrder(u);

		double[] toBaidu = LatLngUtil.fromGcjToBaidu(u.hotelsLatitude,
				u.hotelsLongitude);
		u.hotelsLatitude = toBaidu[0];
		u.hotelsLongitude = toBaidu[1];

		return u;
	}

	public String getBookCommitTime() {
		return bookCommitTime;
	}

	public void setBookCommitTime(String bookCommitTime) {
		this.bookCommitTime = bookCommitTime;
	}

	public void setCouponEndTime(String couponEndTime) {
		this.couponEndTime = couponEndTime;
	}

}
