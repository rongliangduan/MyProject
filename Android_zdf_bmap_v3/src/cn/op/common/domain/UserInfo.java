package cn.op.common.domain;

import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import cn.op.common.AppException;
import cn.op.common.util.DesUtils;
import cn.op.common.util.RoundTool;
import cn.op.common.util.StringUtils;

/**
 * 个人信息
 * 
 * @author Frank
 * 
 */
public class UserInfo implements Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String USER_TYPE_NORMAL = "25";
	public static final String USER_TYPE_OAUTH_QZONE = "103";
	public static final String USER_TYPE_OAUTH_SINA = "104";
	private String userId;

	public String username;
	/**
	 * 昵称
	 */
	public String nickname;
	public String realname;
	public String user_gender;
	public String userPhone;
	public String login_pwd;
	public String email;
	/**
	 * 账户余额
	 */
	public float balance;

	public RspMsg rspMsg = new RspMsg();
	public String userType;

	@Override
	public UserInfo clone() {
		UserInfo u = null;
		try {
			u = (UserInfo) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return u;
	}

	public static UserInfo parseDemo() throws AppException {
		UserInfo u = new UserInfo();
		u.username = "18602990487";
		u.login_pwd = "aaa";
		u.userId = "11";

		u.rspMsg = RspMsg.parseDemo();

		return u;
	}

	// public static UserInfo parse(SoapObject result) {
	// UserInfo u = new UserInfo();
	// RspMsg msg = RspMsg.parse(result);
	// u.rspMsg = msg;
	//
	// if (msg.OK()) {
	// u.username = result
	// .getPrimitivePropertySafelyAsString("userAccount");
	// u.userType = result.getPrimitivePropertySafelyAsString("userType");
	// u.userId = result.getPrimitivePropertySafelyAsString("userId");
	// u.email = result.getPrimitivePropertySafelyAsString("userEmail");
	// u.userPhone = result
	// .getPrimitivePropertySafelyAsString("userPhone");
	// u.realname = result.getPrimitivePropertySafelyAsString("userName");
	// u.login_pwd = result.getPrimitivePropertySafelyAsString("userPwd");
	// String balanceStr = result
	// .getPrimitivePropertySafelyAsString("balance");
	//
	// // 解密
	// if (URLs.IS_NEED_SECURITY) {
	// u.username = DesUtils.decrypt(u.username);
	// u.userType = DesUtils.decrypt(u.userType);
	// u.userId = DesUtils.decrypt(u.userId);
	// u.email = DesUtils.decrypt(u.email);
	// u.userPhone = DesUtils.decrypt(u.userPhone);
	// u.realname = DesUtils.decrypt(u.realname);
	// u.login_pwd = DesUtils.decrypt(u.login_pwd);
	// u.balance = StringUtils.toFloat(DesUtils.decrypt(balanceStr));
	// u.balance = RoundTool.round(u.balance, 2,
	// BigDecimal.ROUND_HALF_UP);
	// }
	//
	// }
	//
	// return u;
	// }

	// public static UserInfo parseOAuth(SoapObject result) {
	// UserInfo u = new UserInfo();
	// RspMsg msg = RspMsg.parse(result);
	// u.rspMsg = msg;
	//
	// if (msg.OK() || RspMsg.CODE_OAUTH_BIND_PHONE.equals(msg.code)) {
	// u.userId = result.getPrimitivePropertySafelyAsString("userId");
	// u.userType = result.getPrimitivePropertySafelyAsString("userType");
	// u.username = result
	// .getPrimitivePropertySafelyAsString("userAccount");
	// u.email = result.getPrimitivePropertySafelyAsString("userEmail");
	// u.userPhone = result
	// .getPrimitivePropertySafelyAsString("userPhone");
	// u.realname = result.getPrimitivePropertySafelyAsString("userName");
	// u.login_pwd = result.getPrimitivePropertySafelyAsString("userPwd");
	// // 解析获取
	// u.nickname = result.getPrimitivePropertySafelyAsString("nickname");
	//
	// String balanceStr = result
	// .getPrimitivePropertySafelyAsString("balance");
	//
	// // 解密
	// if (URLs.IS_NEED_SECURITY) {
	// u.userId = DesUtils.decrypt(u.userId);
	// u.userType = DesUtils.decrypt(u.userType);
	// u.username = DesUtils.decrypt(u.username);
	// u.email = DesUtils.decrypt(u.email);
	// u.userPhone = DesUtils.decrypt(u.userPhone);
	// u.realname = DesUtils.decrypt(u.realname);
	// u.login_pwd = DesUtils.decrypt(u.login_pwd);
	// u.nickname = DesUtils.decrypt(u.nickname);
	// u.balance = StringUtils.toFloat(DesUtils.decrypt(balanceStr));
	// u.balance = RoundTool.round(u.balance, 2,
	// BigDecimal.ROUND_HALF_UP);
	// }
	// }
	//
	// return u;
	// }

	public String getUserId() {
		return userId;
	}

	public static UserInfo parse(String xml) throws AppException {
		UserInfo u = new UserInfo();
		RspMsg msg = RspMsg.parse(xml);

		if (!(msg.OK() || RspMsg.CODE_OAUTH_BIND_PHONE.equals(msg.code))) {
			u.rspMsg = msg;
			return u;
		}

		String balanceStr = null;
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
						u = new UserInfo();
					} else if (u != null) {
						if (name.equals("userId")) {
							u.userId = parser.nextText();
						} else if (name.equals("userType")) {
							u.userType = parser.nextText();
						} else if (name.equals("userAccount")) {
							u.username = parser.nextText();
						} else if (name.equals("userEmail")) {
							u.email = parser.nextText();
						} else if (name.equals("userPhone")) {
							u.userPhone = parser.nextText();
						} else if (name.equals("userName")) {
							u.realname = parser.nextText();
						} else if (name.equals("userPwd")) {
							u.login_pwd = parser.nextText();
						} else if (name.equals("nickname")) {
							u.nickname = parser.nextText();
						} else if (name.equals("balance")) {
							balanceStr = parser.nextText();
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
			u.userId = DesUtils.decrypt(u.userId);
			u.userType = DesUtils.decrypt(u.userType);
			u.username = DesUtils.decrypt(u.username);
			u.email = DesUtils.decrypt(u.email);
			u.userPhone = DesUtils.decrypt(u.userPhone);
			u.realname = DesUtils.decrypt(u.realname);
			u.login_pwd = DesUtils.decrypt(u.login_pwd);
			u.nickname = DesUtils.decrypt(u.nickname);
			u.balance = StringUtils.toFloat(DesUtils.decrypt(balanceStr));
			u.balance = RoundTool.round(u.balance, 2, BigDecimal.ROUND_HALF_UP);
		}

		return u;
	}
}