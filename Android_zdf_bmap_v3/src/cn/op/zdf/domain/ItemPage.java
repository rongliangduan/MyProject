package cn.op.zdf.domain;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import cn.op.common.AppException;
import cn.op.common.constant.Constant;
import cn.op.common.domain.Entity;
import cn.op.common.domain.RspMsg;
import cn.op.common.util.LatLngUtil;
import cn.op.common.util.StringUtils;

public class ItemPage extends Entity implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String TAG = ItemPage.class.getSimpleName();

	public RspMsg rspMsg = new RspMsg();
	public List<Item> list = new ArrayList<Item>();

	public double reqLatitude;
	public double reqLongitude;

	public List<Item> getList() {
		return list;
	}

	public void setList(List<Item> list) {
		this.list = list;
	}

	public RspMsg getRspMsg() {
		return rspMsg;
	}

	public void setRspMsg(RspMsg rspMsg) {
		this.rspMsg = rspMsg;
	}

	public static ItemPage parseDemoByLatLng(double latitude, double longitude)
			throws AppException {
		ItemPage page = new ItemPage();

		ArrayList<Item> list = new ArrayList<Item>();
		// list.add(new Item("7天", "7天连锁酒店", "西安东大街122号", "0.3", "158", "235",
		// "029-8536489", R.drawable.pic_hotel_logo_7day, latitude + 0.01,
		// longitude + 0.01, 1));
		// list.add(new Item("布丁", "布丁酒店", "西安长安南路52号", "0.3", "118", "135",
		// "029-8536489", R.drawable.pic_hotel_logo_rj, latitude - 0.02,
		// longitude + 0.02, 2));
		// list.add(new Item("锦江之星", "锦江之星西安大雁塔店", "西安雁塔西路", "0.4", "148",
		// "255",
		// "029-8536489", R.drawable.pic_hotel_logo_7day, latitude + 0.02,
		// longitude - 0.03, 3));
		// list.add(new Item("速8", "速8酒店钟楼北店", "西安北大街159号", "0.6", "188", "336",
		// "029-8536489", R.drawable.pic_hotel_logo_rj, latitude + 0.03,
		// longitude + 0.01, 1));
		// list.add(new Item("汉庭", "汉庭酒店西安南大街店", "西安南大街22号", "0.7", "258",
		// "285",
		// "029-8536489", R.drawable.pic_hotel_logo_7day, latitude - 0.04,
		// longitude + 0.01, 3));
		// list.add(new Item("如家", "如家酒店和平门店", "西安和平门东南角", "1.3", "155", "435",
		// "029-8536489", R.drawable.pic_hotel_logo_rj, latitude + 0.02,
		// longitude + 0.02, 4));
		// list.add(new Item("喜来登", "西安喜来登大酒店", "西二环122号", "1.3", "148", "235",
		// "029-8536489", R.drawable.pic_hotel_logo_7day, latitude + 0.04,
		// longitude - 0.02, 4));
		// list.add(new Item("速8", "速8酒店西安大雁塔店", "西安大雁塔南广场", "1.4", "158",
		// "295",
		// "029-8536489", R.drawable.pic_hotel_logo_7day, latitude + 0.04,
		// longitude + 0.03, 2));
		// list.add(new Item("美伦", "西安美伦酒店", "西安莲湖区莲湖路30号", "1.5", "128", "335",
		// "029-8536489", R.drawable.pic_hotel_logo_rj, latitude - 0.03,
		// longitude + 0.03, 2));
		// list.add(new Item("凯宾斯基", "西安凯宾斯基酒店", "西安浐灞生态区欧亚大道西段6号", "2.1",
		// "198",
		// "435", "029-8536489", R.drawable.pic_hotel_logo_7day,
		// latitude + 0.02, longitude + 0.04, 3));
		// list.add(new Item("达兆瑞", "西安华美达兆瑞酒店", "西安莲湖区北大街79号", "2.3", "168",
		// "295", "029-8536489", R.drawable.pic_hotel_logo_7day,
		// latitude - 0.04, longitude + 0.01, 1));
		page.list = list;
		page.rspMsg = RspMsg.parseDemo();

		// PojoXml pojoXml = PojoXmlFactory.createPojoXml();
		// pojoXml.addCollectionClass("Item", Item.class);
		// String filePath = new File(FileUtils.getDirOnExtStore("xml"),
		// "hotel-by-latLng").getAbsolutePath();
		// pojoXml.saveXml(page, filePath);
		// // String xml = pojoXml.getXml(page);
		// page = (ItemPage) pojoXml.getPojoFromFile(filePath, ItemPage.class);

		return page;
	}

	public static ItemPage parseDemo() throws AppException {
		return parseDemoByLatLng(Constant.lat, Constant.lng);
	}

	public static ItemPage parseDemoComment() throws AppException {
		ItemPage page = new ItemPage();
		ArrayList<Item> list = new ArrayList<Item>();
		list.add(new Item("服务不错", "服务很热情，环境也很优雅，下次一定要再来", "2013-07-21"));
		list.add(new Item(
				"环境不错，妹子漂亮",
				"环境还不错，不过不大。很多汤池都没有开放，让人有点儿失望。小茶点其实就是几样水果和橙汁类的饮品，很单调。值得一提的是服务真的不错，和价位相匹配！听服务员说，那里的洗澡水也是温泉水，嗯，这个真不错！有时间了还会再去。还是比较享受的。",
				"2013-05-11"));
		list.add(new Item(
				"挺好的，下次再来",
				"来这泡温泉那是相当舒服，价钱也是相当贵的，环境还不错，服务员态度一般有待提高啊。木屋别苑不错我喜欢，可好贵噢。一般有人请才来，自己掏腰包舍不得了。哈！",
				"2013-04-21"));
		list.add(new Item(
				"酒店很不错，服务员很热情",
				"世园期间开的，到现在也有三年了，比邻灞河环境很不错，就是距市区远喜欢清静的可以住这边。服务很好，西餐厅的自助餐品种不多，不过品质都算上乘朋友在这边办过婚礼，宴会大厅华丽丽~~",
				"2013-07-21"));
		list.add(new Item("服务不错",
				"经常去浐灞的这家凯宾斯基去给新娘子跟妆，一家非常不错的超五星酒店，环境也是非常优美，唯一就是离市区稍微有点远。 ",
				"2013-06-16"));
		list.add(new Item(
				"服务不错",
				"给这里高分其实是一个吃货的私信，和其他五星比较这家其实位置比较偏，但是谁让我爱他家的美食廊呢~ 蛋糕和面包虽然比不上北京三元桥那边的那个凯宾斯基 但是也还可以呀。。",
				"2013-07-21"));
		page.list = list;
		page.rspMsg = RspMsg.parseDemo();

		// PojoXml pojoXml = PojoXmlFactory.createPojoXml();
		// pojoXml.addCollectionClass("Item", Item.class);
		// String filePath = new File(FileUtils.getDirOnExtStore("xml"),
		// "comment-by-hotelid").getAbsolutePath();
		// pojoXml.saveXml(page, filePath);
		// String xml = pojoXml.getXml(page);
		// page = (ItemPage) pojoXml.getPojoFromFile(filePath, ItemPage.class);
		return page;
	}

	public static ItemPage parseHotelPage(String xml) throws AppException {

		long millis1 = System.currentTimeMillis();

		ItemPage hotelPage = new ItemPage();
		RspMsg msg = RspMsg.parse(xml);

		if (!msg.OK()) {
			hotelPage = new ItemPage();
			hotelPage.rspMsg = msg;
			return hotelPage;
		}

		try {
			XmlPullParser parser = XmlPullParserFactory.newInstance()
					.newPullParser();
			StringReader stringReader = new StringReader(xml);
			parser.setInput(stringReader);

			int eventType = parser.getEventType();// 获取事件类型

			Item item = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = null;

				switch (eventType) {

				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();

					if (name.equals("return")) {
						hotelPage = new ItemPage();

					} else if (hotelPage != null) {

						if (name.equals("item")) {
							item = new Item();
						} else if (item != null) {
							if (name.equals("hotelsId")) {
								item.hotelsId = parser.nextText();
							} else if (name.equals("hotelsName")) {
								item.hotelsName = parser.nextText();
							} else if (name.equals("roomType")) {
								item.roomType = StringUtils.toInt(parser
										.nextText());
							} else if (name.equals("brandId")) {
								item.brandId = parser.nextText();
							} else if (name.equals("brandName")) {
								item.brandName = parser.nextText();
							} else if (name.equals("dist")) {
								item.dist = parser.nextText();
							} else if (name.equals("hotelsLatitude")) {
								item.hotelsLatitude = StringUtils
										.toDouble(parser.nextText());
							} else if (name.equals("hotelsLongitude")) {
								item.hotelsLongitude = StringUtils
										.toDouble(parser.nextText());
							} else if (name.equals("facilitysIds")) {
								item.facilitysIds = parser.nextText();
							} else if (name.equals("logopath")) {
								item.logopath = parser.nextText();
							} else if (name.equals("brief")) {
								item.brief = parser.nextText();
							} else if (name.equals("hotelsPhoneno")) {
								item.hotelsTel = parser.nextText();
							} else if (name.equals("collection")) {
								item.collection = StringUtils.toBool(parser
										.nextText());
							} else if (name.equals("isHour")) {
								item.isHour = StringUtils.toBool(parser
										.nextText());
							} else if (name.equals("isSpecial")) {
								item.isSpecial = StringUtils.toBool(parser
										.nextText());
							} else if (name.equals("isMidNight")) {
								item.isMidNight = StringUtils.toBool(parser
										.nextText());
							} else if (name.equals("hotelsPhyaddress")) {
								item.hotelsAddr = parser.nextText();
							} else if (name.equals("hourroomPrice")) {
								item.hourroomPrice = parser.nextText();
							} else if (name.equals("hourSalePrice")) {
								item.hourSalePrice = parser.nextText();
							} else if (name.equals("hours")) {
								item.hours = parser.nextText();
							} else if (name.equals("dayroomPrice")) {
								item.dayroomPrice = parser.nextText();
							} else if (name.equals("daySalePrice")) {
								item.daySalePrice = parser.nextText();
							} else if (name.equals("nightroomPrice")) {
								item.nightroomPrice = parser.nextText();
							} else if (name.equals("nightSalePrice")) {
								item.nightSalePrice = parser.nextText();
							} else if (name.equals("hotelsStarlevel")) {
								item.hotelsStarlevel = parser.nextText();
							} else if (name.equals("operateType")) {
								item.operateType = parser.nextText();
							} else if (name.equals("cityId")) {
								item.cityId = parser.nextText();
							} else if (name.equals("isCustomers")) {
								item.hasTuan = StringUtils.toBool(parser
										.nextText());
							} else if (name.equals("zdfDurationType")) {
								item.zdfDurationType = parser.nextText();
							}
						}
					}
					break;

				case XmlPullParser.END_TAG:
					name = parser.getName();

					if ("item".equals(name)) {

						if (!StringUtils.isEmpty(item.logopath)) {
							item.logopath = item.logopath.replaceAll("@2x", "");
						}
						double[] toBaidu = LatLngUtil.fromGcjToBaidu(
								item.hotelsLatitude, item.hotelsLongitude);
						item.hotelsLatitude = toBaidu[0];
						item.hotelsLongitude = toBaidu[1];

						hotelPage.list.add(item);
						item = null;
					}

					break;
				}
				eventType = parser.next();
			}
			stringReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.xml(e);
		}

		hotelPage.rspMsg = msg;

		long millis2 = System.currentTimeMillis();
		Log.d(TAG, "======parseHotelPage====== time=" + (millis2 - millis1));

		return hotelPage;
	}

	// public static ItemPage parseHotelPage(SoapObject result)
	// throws AppException {
	// ItemPage itemPage = new ItemPage();
	//
	// RspMsg msg = RspMsg.parse(result);
	//
	// itemPage.rspMsg = msg;
	// if (msg.OK()) {
	// Object safely = result.getPropertySafely("list");
	//
	// SoapObject objList = null;
	// int size = 0;
	// if (safely instanceof SoapObject) {
	// objList = (SoapObject) safely;
	// size = objList.getPropertyCount();
	// }
	//
	// for (int i = 0; i < size; i++) {
	// Item item = null;
	//
	// Object property = objList.getProperty(i);
	// if (property instanceof SoapPrimitive) {
	// SoapPrimitive soapItem = (SoapPrimitive) property;
	//
	// String xml = soapItem.toString();
	// xml = "<Item>" + xml + "</Item>";
	//
	// xml = xml.replaceAll("null", "");
	// // 默认大图：pic@2x.jpg，小图为pic.jpg
	// xml = xml.replaceAll("@2x", "");
	//
	// // item = XStreamUtil.xml2Bean(xml, "Item", Item.class);
	// item = Item.parseHotel(xml);
	//
	// } else if (property instanceof SoapObject) {
	// SoapObject soapItem = (SoapObject) property;
	//
	// item = Item.parseHotel(soapItem);
	// }
	//
	// if (!StringUtils.isEmpty(item.dist)) {
	// if (item.dist.length() > 4) {
	// item.dist = item.dist.substring(0, 4);
	// }
	// }
	// itemPage.list.add(item);
	// }
	// }
	//
	// return itemPage;
	// }

	@SuppressWarnings("unused")
	public static ItemPage parseOrderPage(String xml) throws AppException {
		ItemPage orderPage = new ItemPage();
		RspMsg msg = RspMsg.parse(xml);

		if (!msg.OK()) {
			orderPage = new ItemPage();
			orderPage.rspMsg = msg;
			return orderPage;
		}

		try {
			XmlPullParser parser = XmlPullParserFactory.newInstance()
					.newPullParser();
			StringReader stringReader = new StringReader(xml);
			parser.setInput(stringReader);

			int eventType = parser.getEventType();// 获取事件类型

			Item order = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = null;

				switch (eventType) {

				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();

					if (name.equals("return")) {
						orderPage = new ItemPage();

					} else if (orderPage != null) {

						if (name.equals("item")) {
							order = new Item();
						} else if (order != null) {

							if (name.equals("booksId")) {
								order.booksId = parser.nextText();
							} else if (name.equals("booksStatus")) {
								order.booksStatus = StringUtils
										.toIntAfterDecrypt(parser.nextText());
							} else if (name.equals("hotelsId")) {
								order.hotelsId = parser.nextText();
							} else if (name.equals("priceOrder")) {
								order.priceOrder = parser.nextText();
							} else if (name.equals("hotelsName")) {
								order.hotelsName = parser.nextText();
							} else if (name.equals("logopath")) {
								order.logopath = parser.nextText();
							} else if (name.equals("sellType")) {
								order.sellType = StringUtils
										.toIntAfterDecrypt(parser.nextText());
							} else if (name.equals("roomTypeName")) {
								order.roomTypeName = parser.nextText();
							} else if (name.equals("hours")) {
								order.hours = parser.nextText();
							} else if (name.equals("commitBookTime")) {
								order.setBookCommitTime(parser.nextText());
							} else if (name.equals("roomUseDate")) {
								order.bookStartTime = parser.nextText();
							} else if (name.equals("booksNum")) {
								order.booksNum = parser.nextText();
							}

						}

					}
					break;

				case XmlPullParser.END_TAG:
					name = parser.getName();

					if ("item".equals(name)) {
						order = Item.decryptOrder(order);
						if (!StringUtils.isEmpty(order.logopath)) {
							order.logopath = order.logopath.replaceAll("@2x",
									"");
						}

						orderPage.list.add(order);
						order = null;
					}

					break;
				}
				eventType = parser.next();
			}
			stringReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.xml(e);
		}

		if (orderPage == null) {
			orderPage = new ItemPage();
		}

		orderPage.rspMsg = msg;

		return orderPage;
	}

	// public static ItemPage parseOrderPage(SoapObject result)
	// throws AppException {
	// ItemPage itemPage = new ItemPage();
	//
	// RspMsg msg = RspMsg.parse(result);
	//
	// itemPage.rspMsg = msg;
	// if (msg.OK()) {
	// Object safely = result.getPropertySafely("list");
	//
	// SoapObject objList = null;
	// int size = 0;
	// if (safely instanceof SoapObject) {
	// objList = (SoapObject) safely;
	// size = objList.getPropertyCount();
	// }
	//
	// for (int i = 0; i < size; i++) {
	// Item item = null;
	//
	// Object property = objList.getProperty(i);
	//
	// SoapObject soapItem = (SoapObject) property;
	// item = Item.parseOrder(soapItem);
	//
	// // if (property instanceof SoapPrimitive) {
	// // SoapPrimitive soapItem = (SoapPrimitive) property;
	// //
	// // String xml = soapItem.toString();
	// // xml = "<Item>" + xml + "</Item>";
	// //
	// // item = Item.parseOrder(xml);
	// //
	// // } else if (property instanceof SoapObject) {
	// // SoapObject soapItem = (SoapObject) property;
	// //
	// // item = Item.parseOrder(soapItem);
	// // }
	//
	// if (!StringUtils.isEmpty(item.dist)) {
	// if (item.dist.length() > 4) {
	// item.dist = item.dist.substring(0, 4);
	// }
	// }
	//
	// if (!StringUtils.isEmpty(item.logopath)) {
	// item.logopath = item.logopath.replaceAll("@2x", "");
	// }
	//
	// itemPage.list.add(item);
	// }
	// }
	//
	// return itemPage;
	// }

	// public static ItemPage parseGiftPage(SoapObject result) throws
	// AppException {
	// ItemPage itemPage = new ItemPage();
	//
	// RspMsg msg = RspMsg.parse(result);
	//
	// itemPage.rspMsg = msg;
	// if (msg.OK()) {
	// Object safely = result.getPropertySafely("list");
	//
	// SoapObject objList = null;
	// int size = 0;
	// if (safely instanceof SoapObject) {
	// objList = (SoapObject) safely;
	// size = objList.getPropertyCount();
	// }
	//
	// for (int i = 0; i < size; i++) {
	// Item item = null;
	//
	// Object property = objList.getProperty(i);
	// if (property instanceof SoapPrimitive) {
	// SoapPrimitive soapItem = (SoapPrimitive) property;
	//
	// String xml = soapItem.toString();
	// xml = "<Item>" + xml + "</Item>";
	//
	// item = Item.parseGift(xml);
	//
	// } else if (property instanceof SoapObject) {
	// SoapObject soapItem = (SoapObject) property;
	//
	// item = Item.parseGift(soapItem);
	// }
	//
	// itemPage.list.add(item);
	// }
	// }
	//
	// return itemPage;
	// }

	public static ItemPage parseCouponDemo() {
		ItemPage itemPage = new ItemPage();
		itemPage.rspMsg = RspMsg.parseDemo();

		for (int i = 0; i < 16; i++) {
			Item c = new Item();
			if (i % 2 == 0) {
				c.couponId = "123456";
				c.couponBrief = "这是一张使用之后能立马减少金额消耗的优惠券，仅限锦江品牌使用，你将能够获得一定金额的免费使用效果";
				// c.couponEndTime = "2014-12-8 12:30";
				c.couponKey = "key12345";
				c.couponName = "立减优惠券";
				c.couponPrice = 6;
				c.couponStarTime = "2014-8-10 12:30";
				c.couponUseState = Item.COUPON_USE_STATE_NOT_USE;
				c.couponType = Item.COUPON_TYPE_REDUCE_MONEY;
			} else {
				c.couponId = "456789";
				c.couponBrief = "这是一张使用之后能不能立马减少金额消耗的优惠券，任何酒店都可使用，入住成功后你将能够获得一定金额的补偿效果";
				// c.couponEndTime = "2014-09-8 11:30";
				c.couponKey = "key12345";
				c.couponName = "反现优惠券";
				c.couponPrice = 10;
				c.couponStarTime = "2014-8-10 11:30";
				c.couponUseState = Item.COUPON_USE_STATE_USED;
				c.couponType = Item.COUPON_TYPE_RETURN_MONEY;
			}

			itemPage.list.add(c);
		}

		return itemPage;

	}

	public static ItemPage parseCouponPage(String xml) throws AppException {
		ItemPage couponPage = new ItemPage();
		RspMsg msg = RspMsg.parse(xml);

		if (!msg.OK()) {
			couponPage = new ItemPage();
			couponPage.rspMsg = msg;
			return couponPage;
		}

		try {
			XmlPullParser parser = XmlPullParserFactory.newInstance()
					.newPullParser();
			StringReader stringReader = new StringReader(xml);
			parser.setInput(stringReader);

			int eventType = parser.getEventType();// 获取事件类型

			Item item = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = null;

				switch (eventType) {

				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();

					if (name.equals("return")) {
						couponPage = new ItemPage();

					} else if (couponPage != null) {

						if (name.equals("coupon")) {
							item = new Item();
						} else if (item != null) {
							if (name.equals("couponId")) {
								item.couponId = parser.nextText();
							} else if (name.equals("couponKey")) {
								item.couponKey = parser.nextText();
							} else if (name.equals("useState")) {
								item.couponUseState = StringUtils.toInt(parser
										.nextText());
							} else if (name.equals("couponName")) {
								item.couponName = parser.nextText();
							} else if (name.equals("couponBrief")) {
								item.couponBrief = parser.nextText();
							} else if (name.equals("couponPrice")) {
								item.couponPrice = StringUtils.toInt(parser
										.nextText());
							} else if (name.equals("couponStarTime")) {
								item.couponStarTime = parser.nextText();
							} else if (name.equals("couponEndTime")) {
								item.setCouponEndTime(parser.nextText());
							} else if (name.equals("onlyBrandUse")) {
								item.onlyBrandUse = parser.nextText();
							} else if (name.equals("onlyHotelUse")) {
								item.onlyHotelUse = parser.nextText();
							} else if (name.equals("onlySaleTypeUse")) {
								item.onlySaleTypeUse = parser.nextText();
							} else if (name.equals("couponType")) {
								item.couponType = StringUtils.toInt(parser
										.nextText());
							} else if (name.equals("couponLogo")) {
								item.couponLogo = parser.nextText();
							}
						}
					}
					break;

				case XmlPullParser.END_TAG:
					name = parser.getName();

					if ("coupon".equals(name)) {
						couponPage.list.add(item);
						item = null;
					}

					break;
				}
				eventType = parser.next();
			}
			stringReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.xml(e);
		}

		couponPage.rspMsg = msg;

		return couponPage;
	}

	// public static ItemPage parseCouponPage(SoapObject result) {
	// ItemPage itemPage = new ItemPage();
	//
	// RspMsg msg = RspMsg.parse(result);
	//
	// itemPage.rspMsg = msg;
	// if (msg.OK()) {
	// Object safely = result.getPropertySafely("list");
	//
	// SoapObject objList = null;
	// int size = 0;
	// if (safely instanceof SoapObject) {
	// objList = (SoapObject) safely;
	// size = objList.getPropertyCount();
	// }
	//
	// for (int i = 0; i < size; i++) {
	// Item item = null;
	//
	// Object property = objList.getProperty(i);
	//
	// SoapObject soapItem = (SoapObject) property;
	// item = Item.parseCoupon(soapItem);
	//
	// itemPage.list.add(item);
	// }
	// }
	//
	// return itemPage;
	// }

}
