package cn.op.zdf.domain;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import cn.op.common.AppException;
import cn.op.common.domain.RspMsg;
import cn.op.common.domain.URLs;
import cn.op.common.util.DesUtils;
import cn.op.common.util.StringUtils;

public class Recharge {
	public RspMsg rspMsg = new RspMsg();
	public String rechargeId;
	/**
	 * 充值状态
	 */
	public boolean status;
	/**
	 * 账户余额
	 */
	public String balance;
	/**
	 * 充值金额
	 */
	public String money;

	public static Recharge parseRechorge(String xml) throws AppException {
		Recharge u = new Recharge();
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
						u = new Recharge();
					} else if (u != null) {
						if ("rechargeId".equals(name)) {
							u.rechargeId = parser.nextText();
						} else if (name.equals("balance")) {
							u.balance = parser.nextText();
						} else if (name.equals("money")) {
							u.money = parser.nextText();
						} else if (name.equals("status")) {
							u.status = StringUtils.toBool(parser.nextText());
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

	// public static Recharge parseRechorge(SoapObject result) {
	// Recharge u = new Recharge();
	// RspMsg msg = RspMsg.parse(result);
	// u.rspMsg = msg;
	//
	// if (msg.OK()) {
	// u.rechargeId = result
	// .getPrimitivePropertySafelyAsString("rechargeId");
	// u.balance = result.getPrimitivePropertySafelyAsString("balance");
	// u.money = result.getPrimitivePropertySafelyAsString("money");
	// u.status = StringUtils.toBool(result
	// .getPrimitivePropertySafelyAsString("status"));
	// }
	//
	// return u;
	// }

	public static Recharge parseBalance(String xml) throws AppException {
		Recharge u = new Recharge();
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
						u = new Recharge();
					} else if (u != null) {
						if (name.equals("balance")) {
							u.balance = parser.nextText();
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

		// 解密
		if (URLs.IS_NEED_SECURITY) {
			u.balance = DesUtils.decrypt(u.balance);
		}

		return u;
	}

	// public static Recharge parseBalance(SoapObject result) {
	// Recharge u = new Recharge();
	// RspMsg msg = RspMsg.parse(result);
	// u.rspMsg = msg;
	//
	// if (msg.OK()) {
	// u.balance = result.getPrimitivePropertySafelyAsString("balance");
	//
	// u.balance = DesUtils.decrypt(u.balance);
	// }
	//
	// return u;
	// }

}
