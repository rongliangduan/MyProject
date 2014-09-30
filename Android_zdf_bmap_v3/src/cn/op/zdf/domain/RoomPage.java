package cn.op.zdf.domain;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import cn.op.common.AppException;
import cn.op.common.domain.RspMsg;
import cn.op.common.util.StringUtils;

public class RoomPage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RspMsg rspMsg = new RspMsg();
	public List<Room> list = new ArrayList<Room>();

	@SuppressWarnings("static-access")
	public static RoomPage parseDemo() throws AppException {
		try {
			new Thread().sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		RoomPage page = new RoomPage();
		try {
			ArrayList<Room> list = new ArrayList<Room>();
			// list.add(new Room("单人间", Room.SALE_TYPE_ZDF, Room.ROOM_TYPE_DRJ,
			// "特价时间段　10:00~17:00", "30", "/2h",
			// R.drawable.ic_hotel_type_zdf, true, false, false, false,
			// false, 6));
			// list.add(new Room("单人间", Room.SALE_TYPE_ZDF, Room.ROOM_TYPE_DRJ,
			// "特价时间段　10:00~17:00", "40", "/3h",
			// R.drawable.ic_hotel_type_zdf, false, true, false, false,
			// false, 4));
			// list.add(new Room("单人间", Room.SALE_TYPE_WYF, Room.ROOM_TYPE_DRJ,
			// "特价房　6:00~23:00", "98", "/天", R.drawable.ic_hotel_type_zdf,
			// true, false, false, false, false, 7));
			// list.add(new Room("标准间", Room.SALE_TYPE_WYF, Room.ROOM_TYPE_BZJ,
			// "特价房　6:00~23:00", "128", "/天",
			// R.drawable.ic_hotel_type_wyf, true, false, false, false,
			// true, 0));
			// list.add(new Room("大床房", Room.SALE_TYPE_ZDF, Room.ROOM_TYPE_DCJ,
			// "特价时间段　全天", "60", "/5h", R.drawable.ic_hotel_type_dcj,
			// true, true, true, false, true, 3));

			page.list = list;
			page.rspMsg = RspMsg.parseDemo();

			// PojoXml pojoXml = PojoXmlFactory.createPojoXml();
			// String filePath = new File(FileUtils.getDirOnExtStore("xml"),
			// "Room_Page").getAbsolutePath();
			// String xml = pojoXml.getXml(page);
			// pojoXml.saveXml(page, filePath);
			//
			// pojoXml.addCollectionClass("Room", Room.class);
			// page = (RoomPage) pojoXml.getPojoFromFile(filePath,
			// RoomPage.class);
		} catch (Exception e) {
			throw AppException.xml(e);
		}

		return page;
	}

	public RspMsg getRspMsg() {
		return rspMsg;
	}

	public void setRspMsg(RspMsg rspMsg) {
		this.rspMsg = rspMsg;
	}

	public List<Room> getList() {
		return list;
	}

	public void setList(List<Room> list) {
		this.list = list;
	}

	public static RoomPage parseRoomPage(String xml) throws AppException {
		RoomPage roomPage = new RoomPage();
		RspMsg msg = RspMsg.parse(xml);

		if (!msg.OK()) {
			roomPage.rspMsg = msg;
			return roomPage;
		}

		try {
			XmlPullParser parser = XmlPullParserFactory.newInstance()
					.newPullParser();
			StringReader stringReader = new StringReader(xml);
			parser.setInput(stringReader);

			int eventType = parser.getEventType();// 获取事件类型

			Room item = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = null;

				switch (eventType) {

				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();

					if (name.equals("return")) {
						roomPage = new RoomPage();

					} else if (roomPage != null) {

						if (name.equals("room")) {
							item = new Room();
						} else if (item != null) {
							if (name.equals("saleId")) {
								item.saleId = parser.nextText();
							} else if (name.equals("sellType")) {
								item.sellType = StringUtils.toInt(parser
										.nextText());
							} else if (name.equals("roomCount")) {
								item.roomCount = StringUtils.toInt(parser
										.nextText());
							} else if (name.equals("hourDuration")) {
								item.hourDuration = parser.nextText();
							} else if (name.equals("salePrice")) {
								item.salePrice = parser.nextText();
							} else if (name.equals("roomPrice")) {
								item.roomPrice = parser.nextText();
							} else if (name.equals("roomTypeName")) {
								item.roomTypeName = parser.nextText();
							} else if (name.equals("isPrepaid")) {
								item.isPrepaid = StringUtils.toInt(parser
										.nextText());
							} else if (name.equals("hourStartTime")) {
								item.setHourStartTime(parser.nextText());
							} else if (name.equals("hourEndTime")) {
								item.setHourEndTime(parser.nextText());
							}

						}
					}
					break;

				case XmlPullParser.END_TAG:
					name = parser.getName();

					if ("room".equals(name)) {
						roomPage.list.add(item);
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

		roomPage.rspMsg = msg;

		return roomPage;
	}

	// public static RoomPage parse(SoapObject result) throws AppException {
	// RoomPage RoomPage = new RoomPage();
	//
	// RspMsg msg = RspMsg.parse(result);
	//
	// RoomPage.rspMsg = msg;
	// if (msg.OK()) {
	// SoapObject objList = (SoapObject) result.getPropertySafely("list");
	// int size = objList.getPropertyCount();
	//
	// for (int i = 0; i < size; i++) {
	// SoapObject soapItem = (SoapObject) objList.getProperty(i);
	//
	// // String xml = soapItem.toString();
	// // xml = "<Item>" + xml + "</Item>";
	// //
	// // Room item;
	// //
	// // item = Room.parseRoom(xml);
	//
	// Room item = Room.parseRoom(soapItem);
	//
	// RoomPage.list.add(item);
	//
	// }
	// }
	//
	// return RoomPage;
	// }

}
