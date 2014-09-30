package cn.op.zdf.domain;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import cn.op.common.AppException;
import cn.op.common.domain.Base;
import cn.op.common.util.StringUtils;

/**
 * 城市，商圈
 * 
 * @author lufei
 * 
 */
public class City extends Base {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String cityId;
	public String cityName;
	/**
	 * 城市名称拼音
	 */
	public String cityNamePy;
	public double cityLat;
	public double cityLng;

	public String parentid;

	/**
	 * 更新类型
	 */
	public String updateType;

	public City() {
		super();
	}

	public City(String name, String loc1Id) {
		super();
		this.cityName = name;
		this.cityId = loc1Id;
	}

	public City(String cityName, double mlat, double mLon) {
		this.cityName = cityName;
		this.cityLat = mlat;
		this.cityLng = mLon;
	}

	public City(String cityId, String cityName, String cityNamePy,
			double latitude, double longitude) {
		this.cityId = cityId;
		this.cityName = cityName;
		this.cityNamePy = cityNamePy;
		this.cityLat = latitude;
		this.cityLng = longitude;
	}

	public static City parseCity(String xml) throws AppException {
		City item = new City();

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
						item = new City();
					} else if (item != null) {
						if (name.equals("cityId")) {
							item.cityId = parser.nextText();
						} else if (name.equals("cityName")) {
							item.cityName = parser.nextText();
						} else if (name.equals("cityNamePy")) {
							item.cityNamePy = parser.nextText();
						} else if (name.equals("cityLongitude")) {
							item.cityLng = StringUtils.toDouble(parser
									.nextText());
						} else if (name.equals("cityLatitude")) {
							item.cityLat = StringUtils.toDouble(parser
									.nextText());
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

//	public static City parseLoc(String xml) throws AppException {
//		City item = new City();
//
//		try {
//			XmlPullParser parser = XmlPullParserFactory.newInstance()
//					.newPullParser();
//			StringReader stringReader = new StringReader(xml);
//			parser.setInput(stringReader);
//			int eventType = parser.getEventType();// 获取事件类型
//
//			while (eventType != XmlPullParser.END_DOCUMENT) {
//				String name = null;
//				switch (eventType) {
//
//				case XmlPullParser.START_DOCUMENT:
//
//					break;
//				case XmlPullParser.START_TAG:
//					name = parser.getName();
//
//					if (name.equals("Item")) {
//						item = new City();
//					} else if (item != null) {
//						if (name.equals("cityId")) {
//							item.cityId = parser.nextText();
//						} else if (name.equals("updateType")) {
//							item.updateType = parser.nextText();
//						} else if (name.equals("cityName")) {
//							item.cityName = parser.nextText();
//						} else if (name.equals("cityNamePy")) {
//							item.cityNamePy = parser.nextText();
//						} else if (name.equals("cityLng")) {
//							item.cityLng = StringUtils.toDouble(parser
//									.nextText());
//						} else if (name.equals("cityLat")) {
//							item.cityLat = StringUtils.toDouble(parser
//									.nextText());
//						} else if (name.equals("parentid")) {
//							item.parentid = parser.nextText();
//						}
//					}
//					break;
//
//				case XmlPullParser.END_TAG:
//					name = parser.getName();
//					break;
//				}
//				eventType = parser.next();
//			}
//			stringReader.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw AppException.xml(e);
//		}
//		return item;
//	}

//	/**
//	 * @param result
//	 * @return
//	 */
//	public static City parseCity(SoapObject result) {
//		City c = new City();
//
//		c.cityId = result.getPrimitivePropertySafelyAsString("cityId");
//		c.updateType = result.getPrimitivePropertySafelyAsString("updateType");
//		c.cityName = result.getPrimitivePropertySafelyAsString("cityName");
//		c.cityNamePy = result.getPrimitivePropertySafelyAsString("cityNamePy");
//		c.cityLat = StringUtils.toDouble(result
//				.getPrimitivePropertySafelyAsString("cityLat"));
//		c.cityLng = StringUtils.toDouble(result
//				.getPrimitivePropertySafelyAsString("cityLng"));
//
//		return c;
//	}

}
