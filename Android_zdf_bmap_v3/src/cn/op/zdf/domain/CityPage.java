package cn.op.zdf.domain;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import cn.op.common.AppException;
import cn.op.common.domain.Entity;
import cn.op.common.domain.RspMsg;
import cn.op.common.util.StringUtils;

/**
 * 城市列表
 * 
 * @author Frank
 * 
 */
public class CityPage extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	public int pageSize;

	/**
	 * 城市
	 */
	public List<City> list = new ArrayList<City>();

	/**
	 * 一级商圈
	 */
	public List<City> listParent = new ArrayList<City>();
	/**
	 * 二级商圈
	 */
	public List<City> listChild = new ArrayList<City>();

	public HashMap<String, City> cityMap = new HashMap<String, City>();

	public static CityPage parseDemo(String http_get_string) {
		CityPage roadList = new CityPage();

		City rt = new City();

		return null;
	}

//	public static CityPage parseCity(SoapObject result) throws AppException {
//		CityPage itemPage = new CityPage();
//
//		RspMsg msg = RspMsg.parse(result);
//
//		itemPage.rspMsg = msg;
//		if (msg.OK()) {
//			SoapObject objList = (SoapObject) result.getPropertySafely("list");
//			int size = objList.getPropertyCount();
//
//			for (int i = 0; i < size; i++) {
//				SoapObject soapItem = (SoapObject) objList.getProperty(i);
//				City item = City.parseCity(soapItem);
//				itemPage.list.add(item);
//			}
//		}
//
//		return itemPage;
//	}

	public static CityPage parseCityPage(String xml) throws AppException {
		CityPage roomPage = new CityPage();
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

			City item = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = null;

				switch (eventType) {

				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();

					if (name.equals("return")) {
						roomPage = new CityPage();

					} else if (roomPage != null) {

						if (name.equals("city")) {
							item = new City();
						} else if (item != null) {
							if (name.equals("cityId")) {
								item.cityId = parser.nextText();
							} else if (name.equals("updateType")) {
								item.updateType = parser.nextText();
							} else if (name.equals("cityName")) {
								item.cityName = parser.nextText();
							} else if (name.equals("cityNamePy")) {
								item.cityNamePy = parser.nextText();
							} else if (name.equals("cityLat")) {
								item.cityLat = StringUtils.toDouble(parser
										.nextText());
							} else if (name.equals("cityLng")) {
								item.cityLng = StringUtils.toDouble(parser
										.nextText());
							}

						}
					}
					break;

				case XmlPullParser.END_TAG:
					name = parser.getName();

					if ("city".equals(name)) {
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

	// public static CityPage parseLoc(SoapObject result) throws AppException {
	// CityPage itemPage = new CityPage();
	//
	// RspMsg msg = RspMsg.parse(result);
	//
	// itemPage.rspMsg = msg;
	// if (msg.OK()) {
	// SoapObject objList = (SoapObject) result
	// .getPropertySafely("listParent");
	// int size = objList.getPropertyCount();
	// // 一级商圈
	// for (int i = 0; i < size; i++) {
	// SoapPrimitive soapItem = (SoapPrimitive) objList.getProperty(i);
	//
	// String xml = soapItem.toString();
	// xml = "<Item>" + xml + "</Item>";
	//
	// City item = City.parseLoc(xml);
	// // City item = XStreamUtil.xml2Bean(xml, "Item", City.class);
	// itemPage.listParent.add(item);
	// }
	//
	// SoapObject objListChild = (SoapObject) result
	// .getPropertySafely("listChild");
	// int sizeChild = objListChild.getPropertyCount();
	// // 二级商圈
	// for (int i = 0; i < sizeChild; i++) {
	// SoapPrimitive soapItem = (SoapPrimitive) objListChild
	// .getProperty(i);
	//
	// String xml = soapItem.toString();
	// xml = "<Item>" + xml + "</Item>";
	//
	// City item = City.parseLoc(xml);
	// // City item = XStreamUtil.xml2Bean(xml, "Item", City.class);
	// // if (StringUtils.isEmpty(item.cityNamePy)) {
	// // item.cityNamePy = PinyinUtil.hanziToPinyin(item.cityName);
	// // }
	//
	// itemPage.listChild.add(item);
	// }
	// }
	//
	// return itemPage;
	// }
}