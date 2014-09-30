package cn.op.zdf.domain;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import cn.op.common.AppException;
import cn.op.common.domain.Entity;
import cn.op.common.domain.RspMsg;
import cn.op.common.util.StringUtils;

/**
 * 服务器对客户端的一些动态配置
 * 
 * @author lufei
 * 
 */
public class ServerConfig extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String startImg;
	public boolean isShowStartImg;
	public String startImgBeginTime;
	public String startImgEndTime;

	public String markerImg;
	public String markerSmallImg;
	public boolean isShowMarkerImg;
	public String markerImgBeginTime;
	public String markerImgEndTime;

	public static ServerConfig parse(String xml) throws AppException {
		ServerConfig u = new ServerConfig();
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
						u = new ServerConfig();
					} else if (u != null) {
						if (name.equals("startImg")) {
							u.startImg = parser.nextText();
						} else if (name.equals("isShowStartImg")) {
							u.isShowStartImg = StringUtils.toBool(parser
									.nextText());
						} else if (name.equals("startImgBeginTime")) {
							u.startImgBeginTime = parser.nextText();
						} else if (name.equals("startImgEndTime")) {
							u.startImgEndTime = parser.nextText();
						} else if (name.equals("markerImg")) {
							u.markerImg = parser.nextText();
						} else if (name.equals("markerSmallImg")) {
							u.markerSmallImg = parser.nextText();
						} else if (name.equals("isShowMarkerImg")) {
							u.isShowMarkerImg = StringUtils.toBool(parser
									.nextText());
						} else if (name.equals("markerImgBeginTime")) {
							u.markerImgBeginTime = parser.nextText();
						} else if (name.equals("markerImgEndTime")) {
							u.markerImgEndTime = parser.nextText();
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

//	public static ServerConfig parse(SoapObject result) {
//		ServerConfig u = new ServerConfig();
//		RspMsg msg = RspMsg.parse(result);
//		u.rspMsg = msg;
//
//		if (msg.OK()) {
//			u.startImg = result.getPrimitivePropertySafelyAsString("startImg");
//			u.isShowStartImg = StringUtils.toBool(result
//					.getPrimitivePropertySafelyAsString("isShowStartImg"));
//			u.startImgBeginTime = result
//					.getPrimitivePropertySafelyAsString("startImgBeginTime");
//			u.startImgEndTime = result
//					.getPrimitivePropertySafelyAsString("startImgEndTime");
//
//			u.markerImg = result
//					.getPrimitivePropertySafelyAsString("markerImg");
//			u.markerSmallImg = result
//					.getPrimitivePropertySafelyAsString("markerSmallImg");
//			u.isShowMarkerImg = StringUtils.toBool(result
//					.getPrimitivePropertySafelyAsString("isShowMarkerImg"));
//			u.markerImgBeginTime = result
//					.getPrimitivePropertySafelyAsString("markerImgBeginTime");
//			u.markerImgEndTime = result
//					.getPrimitivePropertySafelyAsString("markerImgEndTime");
//		}
//
//		return u;
//	}

}
