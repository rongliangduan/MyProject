package cn.op.common.domain;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import cn.op.common.AppException;

//@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * 响应消息
 * 
 * @author Frank
 * 
 */
public class RspMsg extends Base {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String code;

	public String message;

	/**
	 * 特殊注册，不需要密码和验证码，返回用户信息，服务器端先检查此账号是否已经注册过，如果没注册过则生成一个密码进行注册
	 * 
	 * 没注册过，进行注册后返回 code = 1， 已注册过返回 code =2001
	 */
	public static final String CODE_REGISTER_SPECIAL_USER_EXIST = "2001";

	/**
	 * OAuth用户绑定手机号,如果用户存在，则绑定手机号，返回用户信息。 code=013101 <br>
	 * 具体参看接口1.3.1
	 */
	public static final String CODE_OAUTH_BIND_PHONE = "013101";

	/**
	 * OAuth用户登录,如果手机号为空，仅返回响应消息。 code=01301 <br>
	 * 具体参看接口1.3
	 */
	public static final String CODE_OAUTH_LOGIN_NOT_BIND_PHONE = "01301";

	/**
	 * OAuth用户登录,如果用户不存在，仅返回响应消息。 code=01302 <br>
	 * 具体参看接口1.3
	 */
	public static final String CODE_OAUTH_LOGIN_NOT_EXIST = "01302";

	public static final String CODE_SUCCESS = "1";

	public static final String CODE_SUBMIT_ORDER_NO_ROOM = "2";
	public static final Object CODE_SUBMIT_ORDER_ONLY_FOR_PAY_ARRIVE = "3";

	public static final String CODE_GET_VERIFYCODE_USER_EXIST = "2";

	/**
	 * 余额充足，支付成功
	 */
	public static final String CODE_BALANCE_PAY_SUCCESS = "1001";

	/**
	 * 提交订单过于频繁
	 */
	public static final Object CODE_SUBMIT_ORDER_FREQUENT = "03002";

	public static final String CODE_UPPAY_REPEAT = "22";

	/**
	 * 用户未注册过
	 */
	public static final String CODE_CHECK_USER_NOT_EXIST = "0110101";

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return message;
	}

	public void setMsg(String msg) {
		this.message = msg;
	}

	public boolean OK() {
		return CODE_SUCCESS.equals(code);
	}

	@Override
	public String toString() {
		return "RspMsgItem [rspCode=" + code + ", info=" + message + "]";
	}

	public static RspMsg parse(String xml) throws AppException {
		RspMsg msg = new RspMsg();
		try {
			XmlPullParser parser = XmlPullParserFactory.newInstance()
					.newPullParser();
			StringReader stringReader = new StringReader(xml);
			parser.setInput(stringReader);

			int eventType = parser.getEventType();// 获取事件类型
			boolean isParse = true;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = null;
				switch (eventType) {

				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();

					if (name.equals("return")) {
						msg = new RspMsg();
					} else if (msg != null) {

						if (name.equals("code")) {
							msg.code = parser.nextText();
						} else if (name.equals("message")) {
							msg.message = parser.nextText();
						}
					}
					break;

				case XmlPullParser.END_TAG:
					name = parser.getName();
					if ("rspMsg".equals(name)) {
						// 退出解析xml文件
						eventType = XmlPullParser.END_DOCUMENT;
						isParse = false;
					}
					break;
				}

				if (isParse) {
					eventType = parser.next();
				}

			}
			stringReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.xml(e);
		}

		return msg;
	}

	public static RspMsg parseDemo() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		RspMsg msg = new RspMsg();
		msg.code = CODE_SUCCESS;
		return msg;
	}

	// public static RspMsg parse(SoapObject result) {
	// RspMsg msg = new RspMsg();
	// if (result.hasProperty("rspMsg")) {
	// SoapObject objMsg = (SoapObject) result.getPropertySafely("rspMsg");
	// msg.code = objMsg.getPrimitivePropertySafelyAsString("code");
	// msg.message = objMsg.getPrimitivePropertySafelyAsString("message");
	// } else {
	// msg.code = result.getPrimitivePropertySafelyAsString("code");
	// msg.message = result.getPrimitivePropertySafelyAsString("message");
	// }
	// return msg;
	// }

	// public static RspMsg parseDecrypt(SoapObject result) {
	// RspMsg msg = new RspMsg();
	// if (result.hasProperty("rspMsg")) {
	// SoapObject objMsg = (SoapObject) result.getPropertySafely("rspMsg");
	// msg.code = objMsg.getPrimitivePropertySafelyAsString("code");
	// msg.message = objMsg.getPrimitivePropertySafelyAsString("message");
	// } else {
	// msg.code = result.getPrimitivePropertySafelyAsString("code");
	// msg.message = result.getPrimitivePropertySafelyAsString("message");
	// }
	//
	// // 解密
	// if (URLs.IS_NEED_SECURITY) {
	// msg.code = DesUtils.decrypt(msg.code);
	// msg.message = DesUtils.decrypt(msg.message);
	// }
	//
	// return msg;
	// }

}
