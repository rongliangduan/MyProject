package cn.op.zdf;

import cn.op.common.AppException;
import cn.op.common.BaseApiClient;
import cn.op.common.BaseApplication;
import cn.op.common.domain.RspMsg;
import cn.op.common.domain.URLs;
import cn.op.common.domain.UserInfo;
import cn.op.common.util.DesUtils;
import cn.op.common.util.Log;
import cn.op.zdf.domain.CityPage;
import cn.op.zdf.domain.Item;
import cn.op.zdf.domain.ItemPage;
import cn.op.zdf.domain.Recharge;
import cn.op.zdf.domain.RoomPage;
import cn.op.zdf.domain.ServerConfig;

/**
 * 负责ws接口的网络调用
 * 
 * @author lufei
 * 
 */
public class ApiClient extends BaseApiClient {

	private static final String TAG = Log.makeLogTag(ApiClient.class);

	public static UserInfo login(BaseApplication BaseApplication,
			String username, String psw) throws AppException {

		try {

			Log.d(TAG, "======login====== args: " + "arg0=" + username + ","
					+ "arg1=" + psw);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				username = DesUtils.encrypt(username);
				psw = DesUtils.encrypt(psw);
			}

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_LOGIN,
			// URLs.METHOD_LOGIN);
			// rpc.addProperty("arg0", username);
			// rpc.addProperty("arg1", psw);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_LOGIN);
			//
			// return UserInfo.parse(result);

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(username).append("</arg0>")
					.append("<arg1>").append(psw).append("</arg1>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_LOGIN,
					URLs.METHOD_LOGIN, sb.toString());

			String xml = http_post_string(URLs.URL_LOGIN, reqXml);

			return UserInfo.parse(xml);

			// return UserInfo.parseDemo();
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.e(e);
		}
	}

	/**
	 * 生成Soap协议请求XML
	 * 
	 * @param namespace
	 *            命名空间
	 * @param method
	 *            方法
	 * @param args
	 *            输入参数
	 * @return
	 */
	private static String makeSoapRequestXml(String namespace, String method,
			String args) {

		StringBuilder sb = new StringBuilder();
		sb.append(
				"<v:Envelope xmlns:v=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:n0=\"")
				.append(namespace).append("\"><v:Header /><v:Body><n0:")
				.append(method).append(">");

		sb.append(args);

		sb.append("</n0:").append(method).append("></v:Body></v:Envelope>");

		return sb.toString();
	}

	private static String makeSoapRequestXmlDotNet(String namespace,
			String method, String action, String args) {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"")
				.append(namespace)
				.append("\"><soapenv:Header/><soapenv:Body><tem:")
				.append(method).append(">");

		sb.append(args);

		sb.append("</tem:").append(method)
				.append("></soapenv:Body></soapenv:Envelope>");

		return sb.toString();
	}

	/**
	 * @param uid
	 *            OAuth认证提供的
	 * @param userType
	 * @param nickname
	 * @return
	 * @throws AppException
	 */
	public static UserInfo loginOAuth(String uid, String userType,
			String nickname) throws AppException {

		try {

			Log.d(TAG, "======loginOAuth====== args: " + "arg0=" + uid + ","
					+ "arg1=" + userType + "," + "arg2=" + nickname);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				uid = DesUtils.encrypt(uid);
				userType = DesUtils.encrypt(userType);
				nickname = DesUtils.encrypt(nickname);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(uid).append("</arg0>").append("<arg1>")
					.append(userType).append("</arg1>").append("<arg2>")
					.append(nickname).append("</arg2>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_LOGIN_OAUTH,
					URLs.METHOD_LOGIN_OAUTH, sb.toString());

			String xml = http_post_string(URLs.URL_LOGIN_OAUTH, reqXml);

			return UserInfo.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_LOGIN_OAUTH,
			// URLs.METHOD_LOGIN_OAUTH);
			//
			// rpc.addProperty("arg0", uid);
			// rpc.addProperty("arg1", userType);
			// rpc.addProperty("arg2", nickname);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_LOGIN_OAUTH);
			// // String result = soapCallGetXml(rpc, URLs.URL_LOGIN);
			//
			// return UserInfo.parseOAuth(result);

		} catch (Exception e) {
			Log.e(TAG, "loginOAuth==", e);
			throw AppException.e(e);
		}
	}

	// public static ItemPage getHotelByKeyword(double latitude, double
	// longitude,
	// String keyword, String cityId) throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(
	// URLs.NAMESPACE_GET_HOTEL_LIST_BY_KEYWORD_EL,
	// URLs.METHOD_GET_HOTEL_LIST_BY_KEYWORD_EL);
	//
	// Log.d(TAG, "======getHotelByKeyword====== args: " + "arg0="
	// + latitude + "," + "arg1=" + longitude + "," + "arg2="
	// + keyword + "arg3=" + cityId);
	//
	// // 传参
	// rpc.addProperty("arg0", "" + latitude);
	// rpc.addProperty("arg1", "" + longitude);
	// rpc.addProperty("arg2", keyword);
	// rpc.addProperty("arg3", cityId);
	//
	// SoapObject result = soapCall(rpc,
	// URLs.URL_GET_HOTEL_LIST_BY_KEYWORD_EL);
	//
	// // String xml = soapCallGetXml(rpc,
	// // URLs.URL_GET_HOTEL_LIST_BY_LAT_LNG);
	//
	// return ItemPage.parseHotelPage(result);
	// // return ItemPage.parse(xml,
	// // URLs.METHOD_GET_HOTEL_LIST_BY_LAT_LNG);
	// // return ItemPage.parseDemoByLatLng(latitude, longitude);
	// } catch (Exception e) {
	// Log.e(TAG, "getHotelByKeyword==", e);
	// throw AppException.e(e);
	// }
	// }

	// public static ItemPage getHotelByLatLng(double latitude, double
	// longitude,
	// int type) throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(
	// URLs.NAMESPACE_GET_HOTEL_LIST_BY_LAT_LNG_EL,
	// URLs.METHOD_GET_HOTEL_LIST_BY_LAT_LNG_EL);
	//
	// Log.d(TAG, "======getHotelByLatLng====== args: " + "arg0="
	// + latitude + "," + "arg1=" + longitude);
	//
	// // 传参
	// rpc.addProperty("arg0", "" + latitude);
	// rpc.addProperty("arg1", "" + longitude);
	// // rpc.addProperty("arg2", type);
	//
	// long millis1 = System.currentTimeMillis();
	// SoapObject result = soapCall(rpc,
	// URLs.URL_GET_HOTEL_LIST_BY_LAT_LNG_EL);
	// long millis2 = System.currentTimeMillis();
	//
	// Log.d(TAG, "======getHotelByLatLng====== time net :"
	// + (millis2 - millis1));
	//
	// // String xml = soapCallGetXml(rpc,
	// // URLs.URL_GET_HOTEL_LIST_BY_LAT_LNG);
	//
	// long millis3 = System.currentTimeMillis();
	// ItemPage itemPage = ItemPage.parseHotelPage(result);
	// long millis4 = System.currentTimeMillis();
	//
	// Log.d(TAG, "======getHotelByLatLng====== time parse :"
	// + (millis4 - millis3));
	//
	// return itemPage;
	// // return ItemPage.parse(xml,
	// // URLs.METHOD_GET_HOTEL_LIST_BY_LAT_LNG);
	// // return ItemPage.parseDemoByLatLng(latitude, longitude);
	// } catch (Exception e) {
	// Log.e(TAG, "getHotelByLatLng==", e);
	// throw AppException.e(e);
	// }
	//
	// }

	// public static ItemPage getHotelByLatLngLageData(double latitude,
	// double longitude, String lastUpdateTime) throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(
	// URLs.NAMESPACE_GET_HOTEL_LIST_BY_LAT_LNG_LARGE_DATA_EL,
	// URLs.METHOD_GET_HOTEL_LIST_BY_LAT_LNG_LARGE_DATA_EL);
	//
	// Log.d(TAG, "======getHotelByLatLngLageData====== args: " + "arg0="
	// + latitude + "," + "arg1=" + longitude + "," + "arg2="
	// + lastUpdateTime);
	//
	// // 传参
	// rpc.addProperty("arg0", "" + latitude);
	// rpc.addProperty("arg1", "" + longitude);
	// rpc.addProperty("arg2", "" + lastUpdateTime);
	//
	// long millis1 = System.currentTimeMillis();
	// SoapObject result = soapCall(rpc,
	// URLs.URL_GET_HOTEL_LIST_BY_LAT_LNG_LARGE_DATA_EL);
	// long millis2 = System.currentTimeMillis();
	//
	// Log.d(TAG, "======getHotelByLatLngLageData====== time net :"
	// + (millis2 - millis1));
	//
	// // String xml = soapCallGetXml(rpc,
	// // URLs.URL_GET_HOTEL_LIST_BY_LAT_LNG);
	//
	// long millis3 = System.currentTimeMillis();
	// ItemPage itemPage = ItemPage.parseHotelPage(result);
	// long millis4 = System.currentTimeMillis();
	//
	// Log.d(TAG, "======getHotelByLatLngLageData====== time parse :"
	// + (millis4 - millis3));
	//
	// return itemPage;
	// // return ItemPage.parse(xml,
	// // URLs.METHOD_GET_HOTEL_LIST_BY_LAT_LNG);
	// // return ItemPage.parseDemoByLatLng(latitude, longitude);
	// } catch (Exception e) {
	// Log.e(TAG, "getHotelByLatLngLageData==", e);
	// throw AppException.e(e);
	// }
	// }

	/**
	 * 获取指定城市下酒店更新数据
	 * 
	 * @param cityId
	 * @param lastUpdateTime
	 * @return
	 * @throws AppException
	 */
	public static ItemPage getHotelUpdateData(String cityId,
			String lastUpdateTime) throws AppException {
		try {

			Log.d(TAG, "======getHotelUpdateData====== args: " + "arg0="
					+ lastUpdateTime + "arg1=" + cityId);

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(lastUpdateTime).append("</arg0>")
					.append("<arg1>").append(cityId).append("</arg1>");

			String reqXml = makeSoapRequestXml(
					URLs.NAMESPACE_GET_HOTEL_UPDATA_DATA,
					URLs.METHOD_GET_HOTEL_UPDATA_DATA, sb.toString());

			String xml = http_post_string(URLs.URL_GET_HOTEL_UPDATA_DATA,
					reqXml);

			return ItemPage.parseHotelPage(xml);

			// SoapObject rpc = new SoapObject(
			// URLs.NAMESPACE_GET_HOTEL_UPDATA_DATA,
			// URLs.METHOD_GET_HOTEL_UPDATA_DATA);
			//
			// // 传参
			// rpc.addProperty("arg0", lastUpdateTime);
			// rpc.addProperty("arg1", cityId);
			//
			// long millis1 = System.currentTimeMillis();
			// SoapObject result = soapCall(rpc,
			// URLs.URL_GET_HOTEL_UPDATA_DATA);
			// long millis2 = System.currentTimeMillis();
			//
			// Log.d(TAG, "======getHotelUpdateData====== time net :"
			// + (millis2 - millis1));
			//
			// long millis3 = System.currentTimeMillis();
			// ItemPage itemPage = ItemPage.parseHotelPage(result);
			// long millis4 = System.currentTimeMillis();
			//
			// Log.d(TAG, "======getHotelUpdateData====== time parse :"
			// + (millis4 - millis3));
			//
			// return itemPage;
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.e(e);
		}
	}

	// public static ItemPage getHotelPage(double latitude, double longitude,
	// String cityId) throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(URLs.NAMESPACE_SEARCH_HOTEL_LIST,
	// URLs.METHOD_SEARCH_HOTEL_LIST);
	//
	// Log.d(TAG, "======getHotelPage====== args: " + "arg0=" + latitude
	// + "," + "arg1=" + longitude + "," + "arg2=" + cityId);
	//
	// // 传参
	// rpc.addProperty("arg0", "" + latitude);
	// rpc.addProperty("arg1", "" + longitude);
	// rpc.addProperty("arg2", cityId);
	//
	// long millis1 = System.currentTimeMillis();
	// SoapObject result = soapCall(rpc, URLs.URL_SEARCH_HOTEL_LIST);
	// long millis2 = System.currentTimeMillis();
	//
	// Log.d(TAG, "======getHotelPage====== time net :"
	// + (millis2 - millis1));
	//
	// // String xml = soapCallGetXml(rpc,
	// // URLs.URL_GET_HOTEL_LIST_BY_LAT_LNG);
	//
	// long millis3 = System.currentTimeMillis();
	// ItemPage itemPage = ItemPage.parseHotelPage(result);
	// long millis4 = System.currentTimeMillis();
	//
	// Log.d(TAG, "======getHotelPage====== time parse :"
	// + (millis4 - millis3));
	//
	// return itemPage;
	// // return ItemPage.parseDemo();
	//
	// } catch (Exception e) {
	// Log.e(TAG, "getHotelPage==", e);
	// throw AppException.e(e);
	// }
	// }

	public static RspMsg findPsw(String phone) throws AppException {
		try {

			Log.d(TAG, "======findPsw====== args: " + "arg0=" + phone);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				phone = DesUtils.encrypt(phone);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(phone).append("</arg0>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_FIND_PSW,
					URLs.METHOD_FIND_PSW, sb.toString());

			String xml = http_post_string(URLs.URL_FIND_PSW, reqXml);

			return RspMsg.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_FIND_PSW,
			// URLs.METHOD_FIND_PSW);
			//
			// rpc.addProperty("arg0", phone);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_FIND_PSW);
			//
			// return RspMsg.parse(result);
			// return RspMsg.parseDemo();
		} catch (Exception e) {
			Log.e(TAG, "findPsw==", e);
			throw AppException.e(e);
		}
	}

	public static RspMsg updatePsw(String username, String oldPsw, String newPsw)
			throws AppException {
		try {

			Log.d(TAG, "======updatePsw====== args: " + "arg0=" + username
					+ "," + "arg1=" + oldPsw + "," + "arg2=" + newPsw);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				username = DesUtils.encrypt(username);
				oldPsw = DesUtils.encrypt(oldPsw);
				newPsw = DesUtils.encrypt(newPsw);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(username).append("</arg0>")
					.append("<arg1>").append(oldPsw).append("</arg1>")
					.append("<arg2>").append(newPsw).append("</arg2>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_UPDATE_PSW,
					URLs.METHOD_UPDATE_PSW, sb.toString());

			String xml = http_post_string(URLs.URL_UPDATE_PSW, reqXml);

			return RspMsg.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_UPDATE_PSW,
			// URLs.METHOD_UPDATE_PSW);
			//
			// rpc.addProperty("arg0", username);
			// rpc.addProperty("arg1", oldPsw);
			// rpc.addProperty("arg2", newPsw);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_UPDATE_PSW);
			//
			// return RspMsg.parse(result);
			// return RspMsg.parseDemo();
		} catch (Exception e) {
			Log.e(TAG, "updatePsw==", e);
			throw AppException.e(e);
		}
	}

	public static RoomPage getRoomPage(String hotelId, String brandId)
			throws AppException {
		try {

			Log.d(TAG, "======getRoomPage====== args: " + "arg0=" + hotelId
					+ "," + "arg1=" + brandId);

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(hotelId).append("</arg0>")
					.append("<arg1>").append(brandId).append("</arg1>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_GET_ROOM_PAGE,
					URLs.METHOD_GET_ROOM_PAGE, sb.toString());

			String xml = http_post_string(URLs.URL_GET_ROOM_PAGE, reqXml);

			return RoomPage.parseRoomPage(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_GET_ROOM_PAGE,
			// URLs.METHOD_GET_ROOM_PAGE);
			//
			// // 传参
			// rpc.addProperty("arg0", "" + hotelId);
			// rpc.addProperty("arg1", "" + brandId);
			//
			// long millis1 = System.currentTimeMillis();
			// SoapObject result = soapCallRetry(rpc, URLs.URL_GET_ROOM_PAGE);
			// long millis2 = System.currentTimeMillis();
			//
			// Log.d(TAG, "======getRoomPage====== time net :"
			// + (millis2 - millis1));
			//
			// // String xml = soapCallGetXml(rpc,
			// // URLs.URL_GET_HOTEL_LIST_BY_LAT_LNG);
			//
			// long millis3 = System.currentTimeMillis();
			// RoomPage roomPage = RoomPage.parse(result);
			// long millis4 = System.currentTimeMillis();
			//
			// Log.d(TAG, "======getRoomPage====== time parse :"
			// + (millis4 - millis3));
			//
			// return roomPage;

			// return RoomPage.parseDemo();
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.e(e);
		}
	}

	public static ItemPage getOrderPage(String userId, int pageNum, int pageSize)
			throws AppException {
		try {

			Log.d(TAG, "======getOrderPage====== args: " + "arg0=" + userId
					+ ",arg1=" + pageNum + ",arg2=" + pageSize);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				userId = DesUtils.encrypt(userId);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(userId).append("</arg0>")
					.append("<arg1>").append(pageNum).append("</arg1>")
					.append("<arg2>").append(pageSize).append("</arg2>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_GET_ORDER_PAGE,
					URLs.METHOD_GET_ORDER_PAGE, sb.toString());

			String xml = http_post_string(URLs.URL_GET_ORDER_PAGE, reqXml);

			return ItemPage.parseOrderPage(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_GET_ORDER_PAGE,
			// URLs.METHOD_GET_ORDER_PAGE);
			//
			// // 传参
			// rpc.addProperty("arg0", userId);
			// rpc.addProperty("arg1", "" + pageNum);
			// rpc.addProperty("arg2", "" + pageSize);
			//
			// SoapObject result = soapCallRetry(rpc, URLs.URL_GET_ORDER_PAGE);
			//
			// return ItemPage.parseOrderPage(result);
		} catch (Exception e) {
			Log.e(TAG, "getOrderPage==", e);
			throw AppException.e(e);
		}
	}

	// public static ItemPage getCollectPage(String userId, double latitude,
	// double longitude) throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(URLs.NAMESPACE_GET_COLLECTION,
	// URLs.METHOD_GET_COLLECTION);
	//
	// Log.d(TAG, "======getCollectPage====== args: " + "arg0=" + userId
	// + "," + "arg1=" + latitude + "," + "arg2=" + longitude);
	//
	// // 传参
	// rpc.addProperty("arg0", userId);
	// rpc.addProperty("arg1", "" + latitude);
	// rpc.addProperty("arg2", "" + longitude);
	//
	// SoapObject result = soapCall(rpc, URLs.URL_GET_COLLECTION);
	//
	// return ItemPage.parseHotelPage(result);
	// } catch (Exception e) {
	// Log.e(TAG, "getCollectPage==", e);
	// throw AppException.e(e);
	// }
	// }

	// public static ItemPage getBrandPage() throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(URLs.NAMESPACE_GET_BRAND_PAGE,
	// URLs.METHOD_GET_BRAND_PAGE);
	//
	// Log.d(TAG, "======getBrandPage====== args: ");
	//
	// // 传参
	//
	// SoapObject result = soapCall(rpc, URLs.URL_GET_BRAND_PAGE);
	//
	// return ItemPage.parseHotelPage(result);
	// } catch (Exception e) {
	// Log.e(TAG, "getBrandPage==", e);
	// throw AppException.e(e);
	// }
	// }

	public static ItemPage getCommentPage(String hotelId) throws AppException {
		try {
			return ItemPage.parseDemoComment();
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	public static UserInfo register(String username, String psw,
			String verifyCode) throws AppException {
		try {

			Log.d(TAG, "======register====== args: " + "arg0=" + username + ","
					+ "arg1=" + psw + "," + "arg2=" + verifyCode);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				username = DesUtils.encrypt(username);
				psw = DesUtils.encrypt(psw);
				verifyCode = DesUtils.encrypt(verifyCode);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(username).append("</arg0>")
					.append("<arg1>").append(psw).append("</arg1>")
					.append("<arg2>").append(verifyCode).append("</arg2>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_REGISTER,
					URLs.METHOD_REGISTER, sb.toString());

			String xml = http_post_string(URLs.URL_REGISTER, reqXml);

			return UserInfo.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_REGISTER,
			// URLs.METHOD_REGISTER);
			//
			// rpc.addProperty("arg0", username);
			// rpc.addProperty("arg1", psw);
			// rpc.addProperty("arg2", verifyCode);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_REGISTER);
			// // String result = soapCallGetXml(rpc, URLs.URL_LOGIN);
			//
			// return UserInfo.parse(result);

			// return UserInfo.parseDemo();
		} catch (Exception e) {
			Log.e(TAG, "register==", e);
			throw AppException.e(e);
		}
	}

	public static UserInfo registerOAuth(String uid, String platformName,
			String nickname) throws AppException {
		try {

			Log.d(TAG, "======registerOAuth====== args: " + "arg0=" + uid + ","
					+ "arg1=" + platformName + "," + "arg2=" + nickname);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				uid = DesUtils.encrypt(uid);
				platformName = DesUtils.encrypt(platformName);
				nickname = DesUtils.encrypt(nickname);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(uid).append("</arg0>").append("<arg1>")
					.append(platformName).append("</arg1>").append("<arg2>")
					.append(nickname).append("</arg2>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_REGISTER_OAUTH,
					URLs.METHOD_REGISTER_OAUTH, sb.toString());

			String xml = http_post_string(URLs.URL_REGISTER_OAUTH, reqXml);

			return UserInfo.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_REGISTER_OAUTH,
			// URLs.METHOD_REGISTER_OAUTH);
			//
			// rpc.addProperty("arg0", uid);
			// rpc.addProperty("arg1", platformName);
			// rpc.addProperty("arg2", nickname);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_REGISTER_OAUTH);
			// // String result = soapCallGetXml(rpc, URLs.URL_LOGIN);
			//
			// return UserInfo.parseOAuth(result);

		} catch (Exception e) {
			Log.e(TAG, "registerOAuth==", e);
			throw AppException.e(e);
		}
	}

	public static UserInfo registerSpecial(String username, String realname)
			throws AppException {
		try {

			Log.d(TAG, "======registerSpecial====== args: " + "arg0="
					+ username + "," + "arg1=" + realname);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				username = DesUtils.encrypt(username);
				realname = DesUtils.encrypt(realname);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(username).append("</arg0>")
					.append("<arg1>").append(realname).append("</arg1>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_REGISTER_SPECIAL,
					URLs.METHOD_REGISTER_SPECIAL, sb.toString());

			String xml = http_post_string(URLs.URL_REGISTER_SPECIAL, reqXml);

			return UserInfo.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_REGISTER_SPECIAL,
			// URLs.METHOD_REGISTER_SPECIAL);
			//
			// rpc.addProperty("arg0", username);
			// rpc.addProperty("arg1", realname);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_REGISTER_SPECIAL);
			// // String result = soapCallGetXml(rpc, URLs.URL_LOGIN);
			//
			// return UserInfo.parse(result);

			// return UserInfo.parseDemo();
		} catch (Exception e) {
			Log.e(TAG, "registerSpecial==", e);
			throw AppException.e(e);
		}
	}

	public static Item getHotel(String hotelId) throws AppException {
		try {

			Log.d(TAG, "======getHotel====== args: " + "arg0=" + hotelId);

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(hotelId).append("</arg0>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_HOTEL_DETAIL,
					URLs.METHOD_HOTEL_DETAIL, sb.toString());

			String xml = http_post_string(URLs.URL_HOTEL_DETAIL, reqXml);

			return Item.parseHotelDetail(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_HOTEL_DETAIL,
			// URLs.METHOD_HOTEL_DETAIL);
			//
			// rpc.addProperty("arg0", hotelId);
			//
			// SoapObject result = soapCallRetry(rpc, URLs.URL_HOTEL_DETAIL);
			//
			// return Item.parseHotelDetail(result);
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.e(e);
		}
	}

	// public static RspMsg putCollect(String hotelId, String userId,
	// boolean collect, int saleType) throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(URLs.NAMESPACE_PUT_COLLECT,
	// URLs.METHOD_PUT_COLLECT);
	//
	// Log.d(TAG, "======putCollect====== args: " + "arg0=" + hotelId
	// + "," + "arg1=" + userId + "," + "arg2=" + collect + ","
	// + "arg3=" + saleType);
	//
	// rpc.addProperty("arg0", hotelId);
	// rpc.addProperty("arg1", userId);
	// rpc.addProperty("arg2", collect);
	// rpc.addProperty("arg3", saleType);
	//
	// SoapObject result = soapCall(rpc, URLs.URL_PUT_COLLECT);
	//
	// return RspMsg.parse(result);
	// } catch (Exception e) {
	// Log.e(TAG, "putCollect==", e);
	// throw AppException.e(e);
	// }
	// }

	// public static RspMsg batchDeleteCollect(String batchDelIds, String
	// userId,
	// boolean delete) throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(
	// URLs.NAMESPACE_BATCH_DELETE_COLLECT,
	// URLs.METHOD_BATCH_DELETE_COLLECT);
	//
	// Log.d(TAG, "======batchDeleteCollect====== args: " + "arg0="
	// + batchDelIds + "," + "arg1=" + userId + "," + "arg2="
	// + delete);
	//
	// rpc.addProperty("arg0", batchDelIds);
	// rpc.addProperty("arg1", userId);
	// rpc.addProperty("arg2", delete);
	//
	// SoapObject result = soapCall(rpc, URLs.URL_BATCH_DELETE_COLLECT);
	//
	// return RspMsg.parse(result);
	// } catch (Exception e) {
	// Log.e(TAG, "deleteCollect==", e);
	// throw AppException.e(e);
	// }
	// }

	// public static CityPage getCityList(final String imei) throws AppException
	// {
	// try {
	// SoapObject rpc = new SoapObject(URLs.NAMESPACE_GET_CITY_PAGE,
	// URLs.METHOD_GET_CITY_PAGE);
	//
	// Log.d(TAG, "======getCityList====== args: ");
	//
	// // 传参
	//
	// SoapObject result = soapCall(rpc, URLs.URL_GET_CITY_PAGE);
	//
	// // String xml = soapCallGetXml(rpc,
	// // URLs.URL_GET_HOTEL_LIST_BY_LAT_LNG);
	//
	// return CityPage.parseCity(result);
	// // return ItemPage.parse(xml,
	// // URLs.METHOD_GET_HOTEL_LIST_BY_LAT_LNG);
	// // return ItemPage.parseDemoByLatLng(latitude, longitude);
	// } catch (Exception e) {
	// Log.e(TAG, "getCityList==", e);
	// throw AppException.e(e);
	// }
	//
	// // String newUrl = _MakeURL(URLs.CITY_LIST, new HashMap<String,
	// // Object>() {
	// // {
	// // put("devKey", imei);
	// // }
	// // });
	// //
	// // try {
	// // return CityList.parse(http_get_string(newUrl));
	// // } catch (Exception e) {
	// // if (e instanceof AppException)
	// // throw (AppException) e;
	// // throw AppException.network(e);
	// // }
	//
	// }

	// /**
	// * 获取商圈（位置）列表
	// *
	// * @param isRefresh
	// * @param cityId
	// * @return
	// * @throws AppException
	// */
	// public static CityPage getLocList(boolean isRefresh, String cityId)
	// throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(URLs.NAMESPACE_GET_LOC_PAGE,
	// URLs.METHOD_GET_LOC_PAGE);
	//
	// Log.d(TAG, "======getHotelsPrice====== args: " + "arg0=" + cityId);
	//
	// // 传参
	// rpc.addProperty("arg0", cityId);
	//
	// SoapObject result = soapCall(rpc, URLs.URL_GET_LOC_PAGE);
	//
	// return CityPage.parseLoc(result);
	// } catch (Exception e) {
	// Log.e(TAG, "getLocList==", e);
	// throw AppException.e(e);
	// }
	// }

	/**
	 * @param orderIds
	 *            以半角逗号相连的订单id
	 * @param userId
	 * @return
	 * @throws AppException
	 */
	public static RspMsg deleteOrder(String orderIds, String userId)
			throws AppException {
		try {

			Log.d(TAG, "======deleteOrder====== args: " + "arg0=" + orderIds
					+ "," + "arg1=" + userId);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				userId = DesUtils.encrypt(userId);

				if (orderIds.contains(",")) {
					// 订单单个ID依次加密后以“,” 相连
					StringBuilder sb = new StringBuilder();
					String[] split = orderIds.split(",");
					for (int i = 0; i < split.length; i++) {
						sb.append(DesUtils.encrypt(split[i])).append(",");
					}

					orderIds = sb.deleteCharAt(sb.length() - 1).toString();

				} else {
					orderIds = DesUtils.encrypt(orderIds);
				}

			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(userId).append("</arg0>")
					.append("<arg1>").append(orderIds).append("</arg1>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_DELETE_ORDER,
					URLs.METHOD_DELETE_ORDER, sb.toString());

			String xml = http_post_string(URLs.URL_DELETE_ORDER, reqXml);

			return RspMsg.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_DELETE_ORDER,
			// URLs.METHOD_DELETE_ORDER);
			//
			// rpc.addProperty("arg0", userId);
			// rpc.addProperty("arg1", orderIds);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_DELETE_ORDER);
			//
			// return RspMsg.parse(result);

		} catch (Exception e) {
			Log.e(TAG, "deleteOrder==", e);
			throw AppException.e(e);
		}
	}

	// public static RspMsg batchDeleteOrder(String batchDelIds, String userId)
	// throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(URLs.NAMESPACE_BATCH_DELETE_ORDER,
	// URLs.METHOD_BATCH_DELETE_ORDER);
	//
	// Log.d(TAG, "======batchDeleteOrder====== args: " + "arg0="
	// + batchDelIds + "," + "arg1=" + userId);
	//
	// // 加密
	// if (URLs.IS_NEED_SECURITY) {
	// batchDelIds = DesUtils.encrypt(batchDelIds);
	// userId = DesUtils.encrypt(userId);
	// }
	//
	// rpc.addProperty("arg0", batchDelIds);
	// rpc.addProperty("arg1", userId);
	//
	// SoapObject result = soapCall(rpc, URLs.URL_BATCH_DELETE_ORDER);
	//
	// return RspMsg.parse(result);
	// } catch (Exception e) {
	// Log.e(TAG, "batchDeleteOrder==", e);
	// throw AppException.e(e);
	// }
	// }

	public static Item submitOrder(String hotelId, String userId,
			String roomSaleId, int roomType, int saleType, String dateArrive,
			String liveMan, String liveManPhone, String contact,
			String contactPhone, String count, int payWay, String hotelCode,
			String roomPlanId, String roomPrice, String brandId,
			String hotelName) throws AppException {
		try {

			Log.d(TAG, "======submitOrder====== args: " + "arg0=" + hotelId
					+ "," + "arg1=" + userId + "," + "arg2=" + roomSaleId + ","
					+ "arg3=" + saleType + "," + "arg4=" + liveMan + ","
					+ "arg5=" + liveManPhone + "," + "arg6=" + count + ","
					+ "arg7=" + payWay + "," + "arg8=" + contact + ","
					+ "arg9=" + contactPhone);

			String saleTypeStr;
			String payWayStr;

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				hotelId = DesUtils.encrypt(hotelId);
				roomSaleId = DesUtils.encrypt(roomSaleId);
				userId = DesUtils.encrypt(userId);
				saleTypeStr = DesUtils.encrypt("" + saleType);
				liveMan = DesUtils.encrypt(liveMan);
				liveManPhone = DesUtils.encrypt(liveManPhone);
				// count = count;
				payWayStr = DesUtils.encrypt("" + payWay);
				contact = DesUtils.encrypt(contact);
				contactPhone = DesUtils.encrypt(contactPhone);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(hotelId).append("</arg0>")
					.append("<arg1>").append(userId).append("</arg1>")
					.append("<arg2>").append(roomSaleId).append("</arg2>")
					.append("<arg3>").append(saleTypeStr).append("</arg3>")
					.append("<arg4>").append(liveMan).append("</arg4>")
					.append("<arg5>").append(liveManPhone).append("</arg5>")
					.append("<arg6>").append(contact).append("</arg6>")
					.append("<arg7>").append(contactPhone).append("</arg7>")
					.append("<arg8>").append(count).append("</arg8>")
					.append("<arg9>").append(payWayStr).append("</arg9>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_SUBMIT_ORDER,
					URLs.METHOD_SUBMIT_ORDER_FULL_ROOM, sb.toString());

			String xml = http_post_string(URLs.URL_SUBMIT_ORDER, reqXml);

			return Item.parseOrderDetail(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_SUBMIT_ORDER,
			// URLs.METHOD_SUBMIT_ORDER_FULL_ROOM);
			//
			// rpc.addProperty("arg0", hotelId);
			// rpc.addProperty("arg1", userId);
			// rpc.addProperty("arg2", roomSaleId);
			// rpc.addProperty("arg3", saleTypeStr);
			// rpc.addProperty("arg4", liveMan);
			// rpc.addProperty("arg5", liveManPhone);
			// rpc.addProperty("arg6", contact);
			// rpc.addProperty("arg7", contactPhone);
			// rpc.addProperty("arg8", count);
			// rpc.addProperty("arg9", payWayStr);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_SUBMIT_ORDER);
			//
			// return Item.parseOrderDetail(result);
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.e(e);
		}
	}

	public static Item getOrderDetail(String orderId, int sellType)
			throws AppException {
		try {

			Log.d(TAG, "======getOrderDetail====== args: " + "arg0=" + orderId
					+ "," + "arg1=" + sellType);

			String sellTypeStr;
			// 加密
			if (URLs.IS_NEED_SECURITY) {
				orderId = DesUtils.encrypt(orderId);
				sellTypeStr = DesUtils.encrypt("" + sellType);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(orderId).append("</arg0>")
					.append("<arg1>").append(sellTypeStr).append("</arg1>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_ORDER_DETAIL,
					URLs.METHOD_ORDER_DETAIL, sb.toString());

			String xml = http_post_string(URLs.URL_ORDER_DETAIL, reqXml);

			return Item.parseOrderDetail(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_ORDER_DETAIL,
			// URLs.METHOD_ORDER_DETAIL);
			//
			// rpc.addProperty("arg0", orderId);
			// rpc.addProperty("arg1", sellTypeStr);
			//
			// SoapObject result = soapCallRetry(rpc, URLs.URL_ORDER_DETAIL);
			//
			// return Item.parseOrderDetail(result);
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.e(e);
		}
	}

	// public static RspMsg orderDone(String orderId) throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(URLs.NAMESPACE_ORDER_DONE,
	// URLs.METHOD_ORDER_DONE);
	//
	// Log.d(TAG, "======orderDone====== args: " + "arg0=" + orderId);
	//
	// rpc.addProperty("arg0", orderId);
	//
	// SoapObject result = soapCall(rpc, URLs.URL_ORDER_DONE);
	//
	// return RspMsg.parse(result);
	// } catch (Exception e) {
	// Log.e(TAG, "orderDone==", e);
	// throw AppException.e(e);
	// }
	// }

	public static RspMsg deleteUser(String userId) throws AppException {
		try {

			Log.d(TAG, "======deleteUser====== args: " + "arg0=" + userId);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				userId = DesUtils.encrypt(userId);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(userId).append("</arg0>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_DELETE_USER,
					URLs.METHOD_DELETE_USER, sb.toString());

			String xml = http_post_string(URLs.URL_DELETE_USER, reqXml);

			return RspMsg.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_DELETE_USER,
			// URLs.METHOD_DELETE_USER);
			// rpc.addProperty("arg0", userId);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_DELETE_USER);
			//
			// return RspMsg.parse(result);
		} catch (Exception e) {
			Log.e(TAG, "deleteUser==", e);
			throw AppException.e(e);
		}
	}

	/**
	 * 获取酒店价格更新
	 * 
	 * @param hotelIds
	 * @param saleType
	 * @return
	 * @throws AppException
	 */
	public static ItemPage getHotelsPrice(String hotelIds) throws AppException {
		try {
			Log.d(TAG, "======getHotelsPrice====== args: " + "arg0=" + hotelIds);

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(hotelIds).append("</arg0>");

			String reqXml = makeSoapRequestXml(
					URLs.NAMESPACE_GET_HOTELS_SALE_STATE,
					URLs.METHOD_GET_HOTELS_SALE_STATE, sb.toString());

			String xml = http_post_string(URLs.URL_GET_HOTELS_SALE_STATE,
					reqXml);

			return ItemPage.parseHotelPage(xml);

			// SoapObject rpc = new SoapObject(
			// URLs.NAMESPACE_GET_HOTELS_SALE_STATE,
			// URLs.METHOD_GET_HOTELS_SALE_STATE);
			//
			// rpc.addProperty("arg0", hotelIds);
			//
			// SoapObject result = soapCall(rpc,
			// URLs.URL_GET_HOTELS_SALE_STATE);
			//
			// return ItemPage.parseHotelPage(result);
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.e(e);
		}
	}

	// public static RspMsg orderPaySuccess(String hotelsId, String booksId,
	// String paySucDate, String priceOrder) throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(URLs.NAMESPACE_ORDER_PAY_SUCCESS,
	// URLs.METHOD_ORDER_PAY_SUCCESS);
	//
	// Log.d(TAG, "======orderPaySuccess====== args: " + "arg0="
	// + hotelsId + "," + "arg1=" + booksId + "," + "arg2="
	// + paySucDate + "," + "arg3=" + priceOrder);
	//
	// // 加密
	// if (URLs.IS_NEED_SECURITY) {
	// hotelsId = DesUtils.encrypt(hotelsId);
	// booksId = DesUtils.encrypt(booksId);
	// paySucDate = DesUtils.encrypt(paySucDate);
	// priceOrder = DesUtils.encrypt(priceOrder);
	// }
	//
	// rpc.addProperty("arg0", hotelsId);
	// rpc.addProperty("arg1", booksId);
	// rpc.addProperty("arg2", paySucDate);
	// rpc.addProperty("arg3", priceOrder);
	//
	// SoapObject result = soapCall(rpc, URLs.URL_ORDER_PAY_SUCCESS);
	//
	// return RspMsg.parse(result);
	// } catch (Exception e) {
	// Log.e(TAG, "orderPaySuccess==", e);
	// throw AppException.e(e);
	// }
	// }

	// public static RspMsg feedBack(String userId, String versionId,
	// String userPhon, String feedbackStatus, String phonimei,
	// String feedbackContent, String phonePlatform) throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(URLs.NAMESPACE_FEED_BACK,
	// URLs.METHOD_FEED_BACK);
	//
	// Log.d(TAG, "======feedBack====== args: " + "arg0=" + userId + ","
	// + "arg1=" + versionId + "," + "arg2=" + userPhon + ","
	// + "arg3=" + feedbackStatus + "," + "arg4=" + phonimei + ","
	// + "arg5=" + feedbackContent + "," + "arg6=" + phonePlatform);
	//
	// rpc.addProperty("arg0", userId);
	// rpc.addProperty("arg1", versionId);
	// rpc.addProperty("arg2", userPhon);
	// rpc.addProperty("arg3", feedbackStatus);
	// rpc.addProperty("arg4", phonimei);
	// rpc.addProperty("arg5", feedbackContent);
	// rpc.addProperty("arg6", phonePlatform);
	//
	// SoapObject result = soapCall(rpc, URLs.URL_FEED_BACK);
	//
	// return RspMsg.parse(result);
	// } catch (Exception e) {
	// Log.e(TAG, "feedBack==", e);
	// throw AppException.e(e);
	// }
	// }

	public static RspMsg updateUsername(String userId, String username)
			throws AppException {
		try {

			Log.d(TAG, "======updateUsername====== args: " + "arg0=" + userId
					+ "," + "arg1=" + username);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				userId = DesUtils.encrypt(userId);
				username = DesUtils.encrypt(username);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(userId).append("</arg0>")
					.append("<arg1>").append(username).append("</arg1>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_UPDATE_USERNAME,
					URLs.METHOD_UPDATE_USERNAME, sb.toString());

			String xml = http_post_string(URLs.URL_UPDATE_USERNAME, reqXml);

			return RspMsg.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_UPDATE_USERNAME,
			// URLs.METHOD_UPDATE_USERNAME);
			// rpc.addProperty("arg0", userId);
			// rpc.addProperty("arg1", username);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_UPDATE_USERNAME);
			//
			// return RspMsg.parse(result);
		} catch (Exception e) {
			Log.e(TAG, "updateUsername==", e);
			throw AppException.e(e);
		}
	}

	public static RspMsg updateUserPhone(String userId, String username)
			throws AppException {
		try {

			Log.d(TAG, "======updateUserPhone====== args: " + "arg0=" + userId
					+ "," + "arg1=" + username);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				userId = DesUtils.encrypt(userId);
				username = DesUtils.encrypt(username);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(userId).append("</arg0>")
					.append("<arg1>").append(username).append("</arg1>");

			String reqXml = makeSoapRequestXml(
					URLs.NAMESPACE_UPDATE_PHONE_OAUTH,
					URLs.METHOD_UPDATE_PHONE_OAUTH, sb.toString());

			String xml = http_post_string(URLs.URL_UPDATE_PHONE_OAUTH, reqXml);

			return RspMsg.parse(xml);

			// SoapObject rpc = new
			// SoapObject(URLs.NAMESPACE_UPDATE_PHONE_OAUTH,
			// URLs.METHOD_UPDATE_PHONE_OAUTH);
			// rpc.addProperty("arg0", userId);
			// rpc.addProperty("arg1", username);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_UPDATE_PHONE_OAUTH);
			//
			// return RspMsg.parse(result);
		} catch (Exception e) {
			Log.e(TAG, "updateUserPhone==", e);
			throw AppException.e(e);
		}
	}

	public static ServerConfig getServerConfig(String phonePlatform)
			throws AppException {
		try {

			Log.d(TAG, "======getServerConfig====== args: " + "arg0="
					+ phonePlatform);

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(phonePlatform).append("</arg0>");

			String reqXml = makeSoapRequestXml(
					URLs.NAMESPACE_GET_SERVER_CONFIG,
					URLs.METHOD_GET_SERVER_CONFIG, sb.toString());

			String xml = http_post_string(URLs.URL_GET_SERVER_CONFIG, reqXml);

			return ServerConfig.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_GET_SERVER_CONFIG,
			// URLs.METHOD_GET_SERVER_CONFIG);
			//
			// rpc.addProperty("arg0", phonePlatform);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_GET_SERVER_CONFIG);
			//
			// return ServerConfig.parse(result);
		} catch (Exception e) {
			Log.e(TAG, "getServerConfig==", e);
			throw AppException.e(e);
		}
	}

	public static RspMsg getVerifyCode(String phone, String type)
			throws AppException {
		try {

			Log.d(TAG, "======getVerifyCode====== args: " + "arg0=" + phone
					+ "arg1=" + type);

			if (URLs.IS_NEED_SECURITY) {
				phone = DesUtils.encrypt(phone);
				type = DesUtils.encrypt(type);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(phone).append("</arg0>")
					.append("<arg1>").append(type).append("</arg1>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_GET_VERIFY_CODE,
					URLs.METHOD_GET_VERIFY_CODE, sb.toString());

			String xml = http_post_string(URLs.URL_GET_VERIFY_CODE, reqXml);

			return RspMsg.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_GET_VERIFY_CODE,
			// URLs.METHOD_GET_VERIFY_CODE);
			//
			// rpc.addProperty("arg0", phone);
			// rpc.addProperty("arg1", type);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_GET_VERIFY_CODE);
			//
			// return RspMsg.parse(result);
		} catch (Exception e) {
			Log.e(TAG, "getVerifyCode==", e);
			throw AppException.e(e);
		}
	}

	// public static RspMsg getVerifycode4Register(String phone)
	// throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(
	// URLs.NAMESPACE_VERIFY_CODE_4_REGISTER,
	// URLs.METHOD_VERIFY_CODE_4_REGISTER);
	//
	// Log.d(TAG, "======getVerifycode4Register====== args: " + "arg0="
	// + phone);
	//
	// if (URLs.IS_NEED_SECURITY) {
	// phone = DesUtils.encrypt(phone);
	// }
	//
	// rpc.addProperty("arg0", phone);
	//
	// SoapObject result = soapCall(rpc, URLs.URL_VERIFY_CODE_4_REGISTER);
	//
	// return RspMsg.parse(result);
	// } catch (Exception e) {
	// Log.e(TAG, "getVerifycode4Register==", e);
	// throw AppException.e(e);
	// }
	// }

	// public static ItemPage getGiftPage() throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(URLs.NAMESPACE_GET_GIFT_PAGE,
	// URLs.METHOD_GET_GIFT_PAGE);
	//
	// Log.d(TAG, "======getGiftPage======");
	//
	// SoapObject result = soapCallRetry(rpc, URLs.URL_GET_GIFT_PAGE);
	//
	// return ItemPage.parseGiftPage(result);
	// } catch (Exception e) {
	// Log.e(TAG, "getGiftPage==", e);
	// throw AppException.e(e);
	// }
	// }

	// public static Item getGiftDetail(String giftId) throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(URLs.NAMESPACE_GET_GIFT_DETAIL,
	// URLs.METHOD_GET_GIFT_DETAIL);
	//
	// Log.d(TAG, "======getGiftDetail======args: " + "arg0=" + giftId);
	//
	// rpc.addProperty("arg0", giftId);
	//
	// SoapObject result = soapCallRetry(rpc, URLs.URL_GET_GIFT_DETAIL);
	//
	// return Item.parseGiftDetail(result);
	// } catch (Exception e) {
	// Log.e(TAG, "getGiftDetail==", e);
	// throw AppException.e(e);
	// }
	// }

	// /**
	// * 普通用户信息修改
	// *
	// * @param userId
	// * @param realName
	// * @param email
	// * @return
	// * @throws AppException
	// */
	// public static RspMsg updateUser(String userId, String realName, String
	// email)
	// throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(URLs.NAMESPACE_UPDATE_USER,
	// URLs.METHOD_UPDATE_USER);
	//
	// Log.d(TAG, "======updateUser====== args: " + "arg0=" + userId + ","
	// + "arg1=" + realName + "," + "arg2=" + email);
	//
	// // 加密
	// if (URLs.IS_NEED_SECURITY) {
	// userId = DesUtils.encrypt(userId);
	// realName = DesUtils.encrypt(realName);
	// email = DesUtils.encrypt(email);
	// }
	//
	// rpc.addProperty("arg0", userId);
	// rpc.addProperty("arg1", realName);
	// rpc.addProperty("arg2", email);
	//
	// SoapObject result = soapCall(rpc, URLs.URL_UPDATE_USER);
	//
	// return RspMsg.parse(result);
	// // return RspMsg.parseDemo();
	// } catch (Exception e) {
	// Log.e(TAG, "updateUser==", e);
	// throw AppException.e(e);
	// }
	// }

	public static RspMsg updateEmail(String userId, String email)
			throws AppException {
		try {

			Log.d(TAG, "======updateEmail====== args: " + "arg0=" + userId
					+ "," + "arg1=" + email);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				userId = DesUtils.encrypt(userId);
				email = DesUtils.encrypt(email);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(userId).append("</arg0>")
					.append("<arg1>").append(email).append("</arg1>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_UPDATE_EMAIL,
					URLs.METHOD_UPDATE_EMAIL, sb.toString());

			String xml = http_post_string(URLs.URL_UPDATE_EMAIL, reqXml);

			return RspMsg.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_UPDATE_EMAIL,
			// URLs.METHOD_UPDATE_EMAIL);
			// rpc.addProperty("arg0", userId);
			// rpc.addProperty("arg1", email);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_UPDATE_EMAIL);
			//
			// return RspMsg.parse(result);
			// return RspMsg.parseDemo();
		} catch (Exception e) {
			Log.e(TAG, "updateEmail==", e);
			throw AppException.e(e);
		}
	}

	public static RspMsg updateRealName(String userId, String realName)
			throws AppException {
		try {

			Log.d(TAG, "======updateRealName====== args: " + "arg0=" + userId
					+ "," + "arg1=" + realName);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				userId = DesUtils.encrypt(userId);
				realName = DesUtils.encrypt(realName);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(userId).append("</arg0>")
					.append("<arg1>").append(realName).append("</arg1>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_UPDATE_REAL_NAME,
					URLs.METHOD_UPDATE_REAL_NAME, sb.toString());

			String xml = http_post_string(URLs.URL_UPDATE_REAL_NAME, reqXml);

			return RspMsg.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_UPDATE_REAL_NAME,
			// URLs.METHOD_UPDATE_REAL_NAME);
			// rpc.addProperty("arg0", userId);
			// rpc.addProperty("arg1", realName);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_UPDATE_REAL_NAME);
			//
			// return RspMsg.parse(result);
			// return RspMsg.parseDemo();
		} catch (Exception e) {
			Log.e(TAG, "updateRealName==", e);
			throw AppException.e(e);
		}
	}

	// /**
	// * OAuth用户信息修改
	// *
	// * @param userId
	// * @param realName
	// * @param email
	// * @param phone
	// * @return
	// * @throws AppException
	// */
	// public static RspMsg updateUser(String userId, String realName,
	// String email, String phone) throws AppException {
	// try {
	//
	//
	// Log.d(TAG, "======updateUser====== args: " + "arg0=" + userId
	// + "，arg1=" + realName + "，arg2=" + email + "，arg3=" + phone);
	//
	// // 加密
	// if (URLs.IS_NEED_SECURITY) {
	// userId = DesUtils.encrypt(userId);
	// realName = DesUtils.encrypt(realName);
	// email = DesUtils.encrypt(email);
	// phone = DesUtils.encrypt(phone);
	// }
	//
	//
	// StringBuilder sb = new StringBuilder();
	// sb.append("<arg0>").append(userId).append("</arg0>")
	// .append("<arg1>").append(realName).append("</arg1>")
	// .append("<arg2>").append(email).append("</arg2>")
	// .append("<arg3>").append(phone).append("</arg3>")
	// ;
	//
	// String reqXml = makeSoapRequestXml(URLs.NAMESPACE_UPDATE_REAL_NAME,
	// URLs.METHOD_UPDATE_REAL_NAME, sb.toString());
	//
	// String xml = http_post_string(URLs.URL_UPDATE_REAL_NAME, reqXml);
	//
	// return RspMsg.parse(xml);
	//
	// SoapObject rpc = new SoapObject(URLs.NAMESPACE_UPDATE_USER_2,
	// URLs.METHOD_UPDATE_USER_2);
	// rpc.addProperty("arg0", userId);
	// rpc.addProperty("arg1", realName);
	// rpc.addProperty("arg2", email);
	// rpc.addProperty("arg3", phone);
	//
	// SoapObject result = soapCall(rpc, URLs.URL_UPDATE_USER_2);
	//
	// return RspMsg.parse(result);
	// // return RspMsg.parseDemo();
	// } catch (Exception e) {
	// Log.e(TAG, "updateUser==", e);
	// throw AppException.e(e);
	// }
	// }

	public static RspMsg resetPsw(String phone, String psw) throws AppException {
		try {

			Log.d(TAG, "======resetPsw====== args: " + "arg0=" + phone
					+ "，arg1=" + psw);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				phone = DesUtils.encrypt(phone);
				psw = DesUtils.encrypt(psw);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(phone).append("</arg0>")
					.append("<arg1>").append(psw).append("</arg1>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_RESET_PSW,
					URLs.METHOD_RESET_PSW, sb.toString());

			String xml = http_post_string(URLs.URL_RESET_PSW, reqXml);

			return RspMsg.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_RESET_PSW,
			// URLs.METHOD_RESET_PSW);
			// rpc.addProperty("arg0", phone);
			// rpc.addProperty("arg1", psw);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_RESET_PSW);
			//
			// return RspMsg.parse(result);
			// return RspMsg.parseDemo();
		} catch (Exception e) {
			Log.e(TAG, "resetPsw==", e);
			throw AppException.e(e);
		}
	}

	/**
	 * 平台基础数据统计
	 * 
	 * @param imei
	 * @param model
	 * @param brand
	 * @param phonePlatform
	 * @param versionCode
	 * @param channelName
	 * @param macAddress
	 * @param imsi
	 * @return
	 * @throws AppException
	 */
	public static RspMsg baseDataCollect(String channelName,
			String versionCode, String versionName, String phonePlatform,
			String brand, String model, String imei, String imsi,
			String macAddress) throws AppException {
		try {

			Log.d(TAG, "======baseDataCollect====== args: " + "Mac="
					+ macAddress + "," + "AndroidImei=" + imei + ","
					+ "AndroidImsi=" + imsi + "," + "AndroidChannel="
					+ channelName + "," + "IOSIdfa=" + null + ","
					+ "IOSCallback=" + null + "," + "platform=" + phonePlatform
					+ "," + "versionCode=" + versionCode + ",phoneCompany="
					+ brand + ",phoneModel=" + model);

			StringBuilder sb = new StringBuilder();
			sb.append("<Mac>").append(macAddress).append("</Mac>")
					.append("<AndroidImei>").append(imei)
					.append("</AndroidImei>").append("<AndroidImsi>")
					.append(imsi).append("</AndroidImsi>")
					.append("<AndroidChannel>").append(channelName)
					.append("</AndroidChannel>").append("<IOSIdfa>").append("")
					.append("</IOSIdfa>").append("<IOSCallback>").append("")
					.append("</IOSCallback>").append("<platform>")
					.append(phonePlatform).append("</platform>")
					.append("<versionCode>").append(versionCode)
					.append("</versionCode>").append("<versionName>")
					.append(versionName).append("</versionName>")
					.append("<phoneCompany>").append(brand)
					.append("</phoneCompany>").append("<phoneModel>")
					.append(model).append("</phoneModel>");

			String reqXml = makeSoapRequestXmlDotNet(
					URLs.NAMESPACE_DATA_PALTFORM_BASE,
					URLs.METHOD_DATA_PALTFORM_BASE,
					URLs.ACTION_DATA_PALTFORM_BASE, sb.toString());

			String xml = http_post_string(URLs.URL_DATA_PALTFORM_BASE, reqXml);

			return RspMsg.parse(xml);

			// SoapObject rpc = new
			// SoapObject(URLs.NAMESPACE_DATA_PALTFORM_BASE,
			// URLs.METHOD_DATA_PALTFORM_BASE);
			//
			// rpc.addProperty("Mac", macAddress);
			// rpc.addProperty("AndroidImei", imei);
			// rpc.addProperty("AndroidImsi", imsi);
			// rpc.addProperty("AndroidChannel", channelName);
			// rpc.addProperty("IOSIdfa", null);
			// rpc.addProperty("IOSCallback", null);
			//
			// rpc.addProperty("platform", phonePlatform);
			// rpc.addProperty("versionCode", versionCode);
			// rpc.addProperty("versionName", versionName);
			// rpc.addProperty("phoneCompany", brand);
			// rpc.addProperty("phoneModel", model);
			//
			// SoapObject result = soapCallDotNet(rpc,
			// URLs.URL_DATA_PALTFORM_BASE, URLs.ACTION_DATA_PALTFORM_BASE);
			//
			// return RspMsg.parse(result);
			// return RspMsg.parseDemo();
		} catch (Exception e) {
			Log.e(TAG, "baseDataCollect==", e);
			throw AppException.e(e);
		}

	}

	public static Item balancePay(String userId, String booksId,
			float useBalance, String payPsw, String hotelsId, int sellType,
			String couponId) throws AppException {
		try {

			Log.d(TAG, "======balancePay====== args: " + "arg0=" + userId
					+ ",arg1=" + booksId + ",arg2=" + useBalance + ",arg3="
					+ hotelsId + ",arg4=" + sellType + ",arg5=" + couponId);

			String useBalanceStr;
			String saleTypeStr;

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				userId = DesUtils.encrypt(userId);
				booksId = DesUtils.encrypt(booksId);
				useBalanceStr = DesUtils.encrypt("" + useBalance);
				// payPsw = DesUtils.encrypt(payPsw);
				hotelsId = DesUtils.encrypt(hotelsId);
				saleTypeStr = DesUtils.encrypt("" + sellType);
				couponId = DesUtils.encrypt(couponId);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(userId).append("</arg0>")
					.append("<arg1>").append(booksId).append("</arg1>")
					.append("<arg2>").append(useBalanceStr).append("</arg2>")
					.append("<arg3>").append(hotelsId).append("</arg3>")
					.append("<arg4>").append(saleTypeStr).append("</arg4>")
					.append("<arg5>").append(couponId).append("</arg5>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_BALANCE_PAY,
					URLs.METHOD_BALANCE_PAY, sb.toString());

			String xml = http_post_string(URLs.URL_BALANCE_PAY, reqXml);

			return Item.parseOrderDetail(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_BALANCE_PAY,
			// URLs.METHOD_BALANCE_PAY);
			//
			// rpc.addProperty("arg0", userId);
			// rpc.addProperty("arg1", booksId);
			// rpc.addProperty("arg2", useBalanceStr);
			// rpc.addProperty("arg3", hotelsId);
			// rpc.addProperty("arg4", saleTypeStr);
			// rpc.addProperty("arg5", couponId);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_BALANCE_PAY);
			//
			// return Item.parseOrderDetail(result);

			// return Item.parseOrderDetail(null);
		} catch (Exception e) {
			Log.e(TAG, "balancePay==", e);
			throw AppException.e(e);
		}
	}

	public static Recharge submitRecharge(String rechargeMoney, String userId,
			String payPlatform) throws AppException {
		try {

			Log.d(TAG, "======submitRecharge====== args: " + "arg0="
					+ rechargeMoney + "," + "arg1=" + userId + "," + "arg2="
					+ payPlatform);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				rechargeMoney = DesUtils.encrypt(rechargeMoney);
				userId = DesUtils.encrypt(userId);
				payPlatform = DesUtils.encrypt(payPlatform);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(rechargeMoney).append("</arg0>")
					.append("<arg1>").append(userId).append("</arg1>")
					.append("<arg2>").append(payPlatform).append("</arg2>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_SUBMIT_RECHARGE,
					URLs.METHOD_SUBMIT_RECHARGE, sb.toString());

			String xml = http_post_string(URLs.URL_SUBMIT_RECHARGE, reqXml);

			return Recharge.parseRechorge(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_SUBMIT_RECHARGE,
			// URLs.METHOD_SUBMIT_RECHARGE);
			//
			// rpc.addProperty("arg0", rechargeMoney);
			// rpc.addProperty("arg1", userId);
			// rpc.addProperty("arg2", payPlatform);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_SUBMIT_RECHARGE);
			//
			// return Recharge.parseRechorge(result);
		} catch (Exception e) {
			Log.e(TAG, "submitRecharge==", e);
			throw AppException.e(e);
		}
	}

	/**
	 * 查询充值结果、账户余额
	 * 
	 * @param userId
	 *            用户id
	 * @param rechargeId
	 *            充值id
	 * @return
	 * @throws AppException
	 */
	public static Recharge queryRechargeBalance(String userId, String rechargeId)
			throws AppException {
		try {

			Log.d(TAG, "======queryRechargeBalance====== args: " + "arg0="
					+ userId + "," + "arg1=" + rechargeId);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				userId = DesUtils.encrypt(userId);
				rechargeId = DesUtils.encrypt(rechargeId);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(userId).append("</arg0>")
					.append("<arg1>").append(rechargeId).append("</arg1>");

			String reqXml = makeSoapRequestXml(
					URLs.NAMESPACE_QUERY_RECHARGE_RESULT,
					URLs.METHOD_QUERY_RECHARGE_RESULT, sb.toString());

			String xml = http_post_string(URLs.URL_QUERY_RECHARGE_RESULT,
					reqXml);

			return Recharge.parseRechorge(xml);

			// SoapObject rpc = new SoapObject(
			// URLs.NAMESPACE_QUERY_RECHARGE_RESULT,
			// URLs.METHOD_QUERY_RECHARGE_RESULT);
			//
			// rpc.addProperty("arg0", userId);
			// rpc.addProperty("arg1", rechargeId);
			//
			// SoapObject result = soapCallRetry(rpc,
			// URLs.URL_QUERY_RECHARGE_RESULT);
			//
			// return Recharge.parseRechorge(result);
		} catch (Exception e) {
			Log.e(TAG, "queryRechargeBalance==", e);
			throw AppException.e(e);
		}
	}

	// /**
	// * 检查此销售房间是否属于团购
	// *
	// * @param saleId
	// * @param saleType
	// * @return
	// * @throws AppException
	// */
	// public static RspMsg checkeIsTuan(String saleId, int saleType)
	// throws AppException {
	// try {
	// SoapObject rpc = new SoapObject(URLs.NAMESPACE_CHECK_ROOM_IS_TUAN,
	// URLs.METHOD_CHECK_ROOM_IS_TUAN);
	//
	// Log.d(TAG, "======checkeIsTuan====== args: " + "arg0=" + saleId
	// + "," + "arg1=" + saleType);
	//
	// rpc.addProperty("arg0", saleId);
	// rpc.addProperty("arg1", saleType);
	//
	// SoapObject result = soapCallRetry(rpc, URLs.URL_CHECK_ROOM_IS_TUAN);
	//
	// return RspMsg.parse(result);
	// } catch (Exception e) {
	// Log.e(TAG, "checkeIsTuan==", e);
	// throw AppException.e(e);
	// }
	// }

	public static RspMsg getUppayTn(String booksId, String mPayType,
			String payMoney, String couponId) throws AppException {
		try {

			Log.d(TAG, "======getUppayTn====== args: " + "arg0=" + booksId
					+ " ,arg1=" + mPayType + " ,arg2=" + payMoney + " ,arg3="
					+ couponId);

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(booksId).append("</arg0>")
					.append("<arg1>").append(mPayType).append("</arg1>")
					.append("<arg2>").append(payMoney).append("</arg2>")
					.append("<arg3>").append(couponId).append("</arg3>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_GET_UPPAY_TN,
					URLs.METHOD_GET_UPPAY_TN, sb.toString());

			String xml = http_post_string(URLs.URL_GET_UPPAY_TN, reqXml);

			return RspMsg.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_GET_UPPAY_TN,
			// URLs.METHOD_GET_UPPAY_TN);
			//
			// rpc.addProperty("arg0", booksId);
			// rpc.addProperty("arg1", mPayType);
			// rpc.addProperty("arg2", payMoney);
			// rpc.addProperty("arg3", couponId);
			//
			// SoapObject result = soapCallRetry(rpc, URLs.URL_GET_UPPAY_TN);
			//
			// return RspMsg.parse(result);
		} catch (Exception e) {
			Log.e(TAG, "getUppayTn==", e);
			throw AppException.e(e);
		}
	}

	public static RspMsg checkUserExist(String username) throws AppException {
		try {

			Log.d(TAG, "======checkUserExist====== args: " + "arg0=" + username);

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(username).append("</arg0>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_CHECK_USER_EXIST,
					URLs.METHOD_CHECK_USER_EXIST, sb.toString());

			String xml = http_post_string(URLs.URL_CHECK_USER_EXIST, reqXml);

			return RspMsg.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_CHECK_USER_EXIST,
			// URLs.METHOD_CHECK_USER_EXIST);
			//
			// rpc.addProperty("arg0", username);
			//
			// SoapObject result = soapCallRetry(rpc,
			// URLs.URL_CHECK_USER_EXIST);
			//
			// return RspMsg.parse(result);

			// RspMsg rspMsg = RspMsg.parseDemo();
			// rspMsg.code = RspMsg.CODE_CHECK_USER_NOT_EXIST;
			// return rspMsg;
		} catch (Exception e) {
			Log.e(TAG, "checkUserExist==", e);
			throw AppException.e(e);
		}
	}

	public static CityPage getCityUpdateData(String lastUpdateTime)
			throws AppException {
		try {

			Log.d(TAG, "======getCityUpdateData====== args: " + "arg0="
					+ lastUpdateTime);

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(lastUpdateTime).append("</arg0>");

			String reqXml = makeSoapRequestXml(
					URLs.NAMESPACE_GET_CITY_UPDATE_DATA,
					URLs.METHOD_GET_CITY_UPDATE_DATA, sb.toString());

			String xml = http_post_string(URLs.URL_GET_CITY_UPDATE_DATA, reqXml);

			return CityPage.parseCityPage(xml);

			// SoapObject rpc = new SoapObject(
			// URLs.NAMESPACE_GET_CITY_UPDATE_DATA,
			// URLs.METHOD_GET_CITY_UPDATE_DATA);
			//
			// rpc.addProperty("arg0", lastUpdateTime);
			//
			// SoapObject result = soapCallRetry(rpc,
			// URLs.URL_GET_CITY_UPDATE_DATA);
			//
			// return CityPage.parseCity(result);

		} catch (Exception e) {
			Log.e(TAG, "getCityUpdateData==", e);
			throw AppException.e(e);
		}
	}

	public static Item cancelOrder(String userId, String orderId)
			throws AppException {
		try {

			Log.d(TAG, "======cancelOrder====== args: " + "arg0=" + userId
					+ "," + "arg1=" + orderId);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				userId = DesUtils.encrypt(userId);
				orderId = DesUtils.encrypt(orderId);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(userId).append("</arg0>")
					.append("<arg1>").append(orderId).append("</arg1>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_CANCEL_ORDER,
					URLs.METHOD_CANCEL_ORDER, sb.toString());

			String xml = http_post_string(URLs.URL_CANCEL_ORDER, reqXml);

			return Item.parseOrderDetail(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_CANCEL_ORDER,
			// URLs.METHOD_CANCEL_ORDER);
			//
			//
			// rpc.addProperty("arg0", userId);
			// rpc.addProperty("arg1", orderId);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_CANCEL_ORDER);
			//
			// return Item.parseOrderDetail(result);
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.e(e);
		}
	}

	public static ItemPage getCouponPage(String userId, int pageNum,
			int pageSize, int couponUseState) throws AppException {
		try {

			Log.d(TAG, "======getCouponPage====== args: " + "arg0=" + userId
					+ ",arg1=" + pageNum + ",arg2=" + pageSize + ",arg3="
					+ couponUseState);

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(userId).append("</arg0>")
					.append("<arg1>").append(pageNum).append("</arg1>")
					.append("<arg2>").append(pageSize).append("</arg2>")
					.append("<arg3>").append(couponUseState).append("</arg3>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_GET_COUPON_PAGE,
					URLs.METHOD_GET_COUPON_PAGE, sb.toString());

			String xml = http_post_string(URLs.URL_GET_COUPON_PAGE, reqXml);

			return ItemPage.parseCouponPage(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_GET_COUPON_PAGE,
			// URLs.METHOD_GET_COUPON_PAGE);
			//
			// rpc.addProperty("arg0", userId);
			// rpc.addProperty("arg1", pageNum);
			// rpc.addProperty("arg2", "" + pageSize);
			// rpc.addProperty("arg3", "" + couponUseState);
			//
			// Log.d(TAG, URLs.URL_GET_COUPON_PAGE);
			// SoapObject result = soapCall(rpc, URLs.URL_GET_COUPON_PAGE);
			//
			// return ItemPage.parseCouponPage(result);
			// return ItemPage.parseCouponDemo();
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.e(e);
		}
	}

	public static RspMsg addCoupon(String userId, String couponKey)
			throws AppException {
		try {

			Log.d(TAG, "======addCoupon====== args: " + "arg0=" + userId + ","
					+ "arg1=" + couponKey);

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(userId).append("</arg0>")
					.append("<arg1>").append(couponKey).append("</arg1>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_APP_COUPON,
					URLs.METHOD_APP_COUPON, sb.toString());

			String xml = http_post_string(URLs.URL_APP_COUPON, reqXml);

			return RspMsg.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_APP_COUPON,
			// URLs.METHOD_APP_COUPON);
			//
			// rpc.addProperty("arg0", userId);
			// rpc.addProperty("arg1", couponKey);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_APP_COUPON);
			//
			// return RspMsg.parse(result);
		} catch (Exception e) {
			Log.e(TAG, "addCoupon==", e);
			throw AppException.e(e);
		}
	}

	public static UserInfo oauthBindPhone(String uid, String userType,
			String nickname, String phone) throws AppException {
		try {

			Log.d(TAG, "======oauthBindPhone====== args: " + "arg0=" + uid
					+ "," + "arg1=" + userType + "," + "arg2=" + nickname + ","
					+ "arg3=" + phone);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				uid = DesUtils.encrypt(uid);
				userType = DesUtils.encrypt(userType);
				nickname = DesUtils.encrypt(nickname);
				phone = DesUtils.encrypt(phone);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(uid).append("</arg0>").append("<arg1>")
					.append(userType).append("</arg1>").append("<arg2>")
					.append(nickname).append("</arg2>").append("<arg3>")
					.append(phone).append("</arg3>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_OAUTH_BIND_PHONE,
					URLs.METHOD_OAUTH_BIND_PHONE, sb.toString());

			String xml = http_post_string(URLs.URL_OAUTH_BIND_PHONE, reqXml);

			return UserInfo.parse(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_OAUTH_BIND_PHONE,
			// URLs.METHOD_OAUTH_BIND_PHONE);
			//
			// rpc.addProperty("arg0", uid);
			// rpc.addProperty("arg1", userType);
			// rpc.addProperty("arg2", nickname);
			// rpc.addProperty("arg3", phone);
			//
			// SoapObject result = soapCall(rpc, URLs.URL_OAUTH_BIND_PHONE);
			//
			// return UserInfo.parseOAuth(result);
		} catch (Exception e) {
			Log.e(TAG, "oauthBindPhone==", e);
			throw AppException.e(e);
		}
	}

	public static RspMsg checkCoupon4Order(String couponId, String booksId)
			throws AppException {
		try {
			Log.d(TAG, "======checkCoupon4Order====== args: " + "arg0="
					+ couponId + ",arg1=" + booksId);

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(couponId).append("</arg0>")
					.append("<arg1>").append(booksId).append("</arg1>");

			String reqXml = makeSoapRequestXml(
					URLs.NAMESPACE_CHECK_COUPON_4_ORDER,
					URLs.METHOD_CHECK_COUPON_4_ORDER, sb.toString());

			String xml = http_post_string(URLs.URL_CHECK_COUPON_4_ORDER, reqXml);

			return RspMsg.parse(xml);

			// SoapObject rpc = new SoapObject(
			// URLs.NAMESPACE_CHECK_COUPON_4_ORDER,
			// URLs.METHOD_CHECK_COUPON_4_ORDER);
			//
			// rpc.addProperty("arg0", couponId);
			// rpc.addProperty("arg1", booksId);
			//
			// // SoapObject result = soapCall(rpc,
			// URLs.URL_CHECK_COUPON_4_ORDER);
			// // return RspMsg.parse(result);
			//
			// return RspMsg.parseDemo();
		} catch (Exception e) {
			Log.e(TAG, "checkCoupon4Order==", e);
			throw AppException.e(e);
		}
	}

	public static Recharge queryBalance(String userId) throws AppException {
		try {

			Log.d(TAG, "======queryBalance====== args: " + "arg0=" + userId);

			// 加密
			if (URLs.IS_NEED_SECURITY) {
				userId = DesUtils.encrypt(userId);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<arg0>").append(userId).append("</arg0>");

			String reqXml = makeSoapRequestXml(URLs.NAMESPACE_QUERY_BALANCE,
					URLs.METHOD_QUERY_BALANCE, sb.toString());

			String xml = http_post_string(URLs.URL_QUERY_BALANCE, reqXml);

			return Recharge.parseBalance(xml);

			// SoapObject rpc = new SoapObject(URLs.NAMESPACE_QUERY_BALANCE,
			// URLs.METHOD_QUERY_BALANCE);
			//
			// rpc.addProperty("arg0", userId);
			// SoapObject result = soapCall(rpc, URLs.URL_QUERY_BALANCE);
			//
			// return Recharge.parseBalance(result);
		} catch (Exception e) {
			Log.e(TAG, "queryBalance==", e);
			throw AppException.e(e);
		}
	}

}
