package cn.op.common.domain;

import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 接口URL实体类
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class URLs implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 统计主机
	 */
	// public final static String HOST_DATA = "192.168.8.200:81";
	public final static String HOST_DATA = "42.96.189.25:81";

	// public final static String HOST = "ws.jujiasoft.com:7001";
	public final static String HOST = "42.96.189.25:7001";
//	 public final static String HOST = "192.168.8.153:7001";
	// public final static String HOST = "192.168.8.200:7001";
	// public final static String HOST = "sxy522514.xicp.net:7001";
	// public final static String HOST = "113.140.7.178:7001";
	// public final static String HOST = "192.168.8.188:7001";

	public static final boolean IS_NEED_SECURITY = true;

	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";

	private final static String URL_SPLITTER = "/";
	private final static String URL_UNDERLINE = "_";

	public final static String URL_API_HOST = HTTP + HOST + URL_SPLITTER;
	public final static String URL_API_HOST_DATA = HTTP + HOST_DATA
			+ URL_SPLITTER;

	private final static String URL_HOST = "cn.op";
	private final static String URL_WWW_HOST = "www." + URL_HOST;
	private final static String URL_MY_HOST = "my." + URL_HOST;

	public static final String URL_ZDF_API = URL_API_HOST + "hotels_app"
			+ URL_SPLITTER;
	// public static final String URL_ZDF_API = URL_API_HOST + "Hotels"
	// + URL_SPLITTER;

	private static final String URL_ZDF_API_DATA = URL_API_HOST_DATA
			+ "Statistics" + URL_SPLITTER;

	public static final String CITY_LIST = "http://rq.xasoft.org/popularity/public!getCityList.action";

	public static final String DOWNLOAD_UPPAY_PLUGIN = URL_API_HOST
			+ "Hotels/download/UPPayPluginExPro.apk";

	public static final String SOAP_ACTION = null;

	// 统计接口
	public static final String URL_DATA_PALTFORM_BASE = URL_ZDF_API_DATA
			+ "YJFWebService.asmx";
	public static final String NAMESPACE_DATA_PALTFORM_BASE = "http://tempuri.org/";
	public static final String METHOD_DATA_PALTFORM_BASE = "YJFStockIn";
	public static final String ACTION_DATA_PALTFORM_BASE = NAMESPACE_DATA_PALTFORM_BASE
			+ METHOD_DATA_PALTFORM_BASE;

	// 有间房Api
	public static final String URL_PAY_NOTIFY_URL = URL_ZDF_API
			+ "notify_url.jsp";
	public static final String URL_GET_HOTEL_LIST_BY_LAT_LNG = URL_ZDF_API
			+ "FindCoordinatesService";
	public static final String URL_GET_HOTEL_LIST_BY_KEYWORD = URL_ZDF_API
			+ "HotelsFindService";
	public static final String URL_LOGIN = URL_ZDF_API + "UserLoginService";
	public static final String URL_LOGIN_OAUTH = URL_ZDF_API
			+ "UserOauthLoginService";
	public static final String URL_REGISTER = URL_ZDF_API + "UserAddService";
	public static final String URL_REGISTER_SPECIAL = URL_ZDF_API
			+ "UserSmsCallAddService";
	public static final String URL_REGISTER_OAUTH = URL_ZDF_API
			+ "UserQQAddService";
	public static final String URL_UPDATE_USER = URL_ZDF_API
			+ "UserEditService";
	public static final String URL_UPDATE_PSW = URL_ZDF_API
			+ "PasswordEditService";
	public static final String URL_FIND_PSW = URL_ZDF_API
			+ "PasswordFindService";
	public static final String URL_SEARCH_HOTEL_LIST = URL_ZDF_API
			+ "HotelSearchService";
	public static final String URL_HOTEL_DETAIL = URL_ZDF_API
			+ "HotelOtherFindService";
	public static final String URL_PUT_COLLECT = URL_ZDF_API
			+ "UnsubscribeService";
	public static final String URL_GET_COLLECTION = URL_ZDF_API
			+ "MyFavoritesListService";
	public static final String URL_GET_BRAND_PAGE = URL_ZDF_API
			+ "BrandListService";
	public static final String URL_GET_CITY_PAGE = URL_ZDF_API
			+ "CityListService";
	public static final String URL_GET_LOC_PAGE = URL_ZDF_API
			+ "GetSownTownListService";
	public static final String URL_DELETE_ORDER = URL_ZDF_API
			+ "BooksDelService";
	public static final String URL_ORDER_DONE = URL_ZDF_API
			+ "BookCheckService";
	public static final String URL_ORDER_PAY_SUCCESS = URL_ZDF_API
			+ "BookSuccessService";
	public static final String URL_BATCH_DELETE_COLLECT = URL_ZDF_API
			+ "MyFavoritesDelsService";
	public static final String URL_BATCH_DELETE_ORDER = URL_ZDF_API
			+ "BooksDelsService";
	public static final String URL_DELETE_USER = URL_ZDF_API + "UserDelService";
	public static final String URL_GET_HOTEL_LIST_BY_LAT_LNG_LARGE_DATA = URL_ZDF_API
			+ "SaerchCoordinatesService";
	public static final String URL_FEED_BACK = URL_ZDF_API + "FeedbackService";
	public static final String URL_UPDATE_USERNAME = URL_ZDF_API
			+ "UserUpdateService";
	public static final String URL_UPDATE_PHONE_OAUTH = URL_ZDF_API
			+ "PhoneUpdateService";
	public static final String URL_GET_SERVER_CONFIG = URL_ZDF_API
			+ "ServiceConfigurationService";
	public static final String URL_GET_VERIFY_CODE = URL_ZDF_API
			+ "PhoneRegistrationVerifiedService";
	public static final String URL_GET_HOTEL_LIST_BY_LAT_LNG_EL = URL_ZDF_API
			+ "FindCoordinatesEService";
	public static final String URL_GET_ROOM_PAGE = URL_ZDF_API
			+ "HotelsRoomService";
	public static final String URL_GET_HOTEL_LIST_BY_KEYWORD_EL = URL_ZDF_API
			+ "HotelsFindEService";
	public static final String URL_GET_HOTEL_LIST_BY_LAT_LNG_LARGE_DATA_EL = URL_ZDF_API
			+ "SaerchCoordinatesEService";
	public static final String URL_GET_ORDER_PAGE = URL_ZDF_API
			+ "HotelsBooksListService";
	public static final String URL_ORDER_DETAIL = URL_ZDF_API
			+ "HotelsBooksViewService";
	public static final String URL_GET_HOTEL_UPDATA_DATA = URL_ZDF_API
			+ "HotelsChangeDataService";
	public static final String URL_GET_GIFT_PAGE = URL_ZDF_API
			+ "EventInforListService";
	public static final String URL_GET_GIFT_DETAIL = URL_ZDF_API
			+ "EventInforViewService";
	public static final String URL_UPDATE_USER_2 = URL_ZDF_API
			+ "UserEditTwoService";
	public static final String URL_RESET_PSW = URL_ZDF_API
			+ "PasswordUpdateService";
	public static final String URL_VERIFY_CODE_4_REGISTER = URL_ZDF_API
			+ "PhoneRegistrationVerifiedService";
	public static final String URL_SUBMIT_RECHARGE = URL_ZDF_API
			+ "RecordsAddService";
	public static final String URL_QUERY_RECHARGE_RESULT = URL_ZDF_API
			+ "RecordsViewService";
	public static final String URL_BALANCE_PAY = URL_ZDF_API
			+ "BalanceOutLayService";
	public static final String URL_CHECK_ROOM_IS_TUAN = URL_ZDF_API
			+ "HotelsRoomTypeService";
	public static final String URL_SUBMIT_ORDER = URL_ZDF_API
			+ "BooksAddService";
	public static final String URL_GET_HOTELS_SALE_STATE = URL_ZDF_API
			+ "HotelSaleStateService";
	public static final String URL_GET_UPPAY_TN = URL_ZDF_API
			+ "PurchaseExampleService";

	public static final String URL_CHECK_USER_EXIST = URL_ZDF_API
			+ "CheckPhoneService";
	public static final String NAMESPACE_CHECK_USER_EXIST = "http://checkPhone.users.app.hotel.com/";
	public static final String METHOD_CHECK_USER_EXIST = "checkPhone";

	public static final String NAMESPACE_GET_HOTEL_LIST_BY_LAT_LNG = "http://findCoordinates.hotels.web.hotel.com/";
	public static final String NAMESPACE_GET_HOTEL_LIST_BY_LAT_LNG_LARGE_DATA = "http://findCoordinates.hotels.web.hotel.com/";
	public static final String NAMESPACE_GET_HOTEL_LIST_BY_KEYWORD = "http://hotelsFind.hotels.web.hotel.com/";
	public static final String NAMESPACE_LOGIN = "http://userLogin.users.app.hotel.com/";
	public static final String NAMESPACE_LOGIN_OAUTH = "http://userOauthLogin.users.app.hotel.com/";
	public static final String NAMESPACE_REGISTER = "http://userAdd.users.app.hotel.com/";
	public static final String NAMESPACE_REGISTER_SPECIAL = "http://userSmsCallAdd.users.app.hotel.com/";
	public static final String NAMESPACE_REGISTER_OAUTH = "http://userQQAdd.users.app.hotel.com/";
	public static final String NAMESPACE_GET_ROOM_PAGE = "http://roomList.hotelsRoom.app.hotel.com/";
	public static final String NAMESPACE_UPDATE_USER = "http://userEdit.users.app.hotel.com/";
	public static final String NAMESPACE_UPDATE_PSW = "http://passwordEdit.users.app.hotel.com/";
	public static final String NAMESPACE_FIND_PSW = "http://passwordFind.userPassword.users.app.hotel.com/";
	public static final String NAMESPACE_SEARCH_HOTEL_LIST = "http://hotelSearch.hotels.web.hotel.com/";
	public static final String NAMESPACE_HOTEL_DETAIL = "http://hotelOther.hotelsRoom.app.hotel.com/";
	public static final String NAMESPACE_PUT_COLLECT = "http://myFavoritesDel.myFavorites.web.hotel.com/";
	public static final String NAMESPACE_GET_COLLECTION = "http://myFavoritesList.myFavorites.web.hotel.com/";
	public static final String NAMESPACE_GET_BRAND_PAGE = "http://brand.web.hotel.com/";
	public static final String NAMESPACE_GET_CITY_PAGE = "http://city.web.hotel.com/";
	public static final String NAMESPACE_GET_LOC_PAGE = "http://getsowntownlist.endpoint.hotel.com/";
	public static final String NAMESPACE_DELETE_ORDER = "http://booksDel.hotelsBooks.app.hotel.com/";
	public static final String NAMESPACE_ORDER_DONE = "http://bookCheck.roomBook.endpoint.hotel.com/";
	public static final String NAMESPACE_ORDER_PAY_SUCCESS = "http://bookSuccess.hotelsBooks.web.hotel.com/";
	public static final String NAMESPACE_BATCH_DELETE_COLLECT = "http://myFavoritesDels.myFavorites.web.hotel.com/";
	public static final String NAMESPACE_BATCH_DELETE_ORDER = "http://booksDels.hotelsBooks.web.hotel.com/";
	public static final String NAMESPACE_DELETE_USER = "http://userDel.users.app.hotel.com/";
	public static final String NAMESPACE_FEED_BACK = "http://feedback.services.app.hotel.com/";
	public static final String NAMESPACE_UPDATE_USERNAME = "http://userUpdate.users.app.hotel.com/";
	public static final String NAMESPACE_UPDATE_PHONE_OAUTH = "http://phoneUpdate.users.app.hotel.com/";
	public static final String NAMESPACE_GET_SERVER_CONFIG = "http://serviceConfiguration.services.app.hotel.com/";
	public static final String NAMESPACE_GET_VERIFY_CODE = "http://phoneRegistrationVerified.users.app.hotel.com/";
	public static final String NAMESPACE_GET_HOTEL_LIST_BY_LAT_LNG_EL = "http://findCoordinatesE.hotels.elong.hotel.com/";
	public static final String NAMESPACE_GET_HOTEL_LIST_BY_KEYWORD_EL = "http://hotelsFindE.hotels.elong.hotel.com/";
	public static final String NAMESPACE_GET_HOTEL_LIST_BY_LAT_LNG_LARGE_DATA_EL = "http://findCoordinatesE.hotels.elong.hotel.com/";
	public static final String NAMESPACE_GET_ORDER_PAGE = "http://hotelsBooksList.hotelsBooks.app.hotel.com/";
	public static final String NAMESPACE_ORDER_DETAIL = "http://hotelsBooksView.hotelsBooks.app.hotel.com/";
	public static final String NAMESPACE_GET_HOTEL_UPDATA_DATA = "http://hotelsChangeData.hotelsRoom.app.hotel.com/";
	public static final String NAMESPACE_GET_GIFT_PAGE = "http://eventInfor.web.hotel.com/";
	public static final String NAMESPACE_GET_GIFT_DETAIL = "http://eventInfor.web.hotel.com/";
	public static final String NAMESPACE_UPDATE_USER_2 = "http://userEdit.users.app.hotel.com/";
	public static final String NAMESPACE_RESET_PSW = "http://passwordUpdate.users.app.hotel.com/";
	public static final String NAMESPACE_VERIFY_CODE_4_REGISTER = "http://phoneRegistrationVerified.users.app.hotel.com/";
	public static final String NAMESPACE_SUBMIT_RECHARGE = "http://records.account.app.hotel.com/";
	public static final String NAMESPACE_QUERY_RECHARGE_RESULT = "http://records.account.app.hotel.com/";
	public static final String NAMESPACE_BALANCE_PAY = "http://balance.account.app.hotel.com/";
	public static final String NAMESPACE_CHECK_ROOM_IS_TUAN = "http://hotelsRoomE.elong.hotel.com/";
	public static final String NAMESPACE_SUBMIT_ORDER = "http://booksAdd.hotelsBooks.app.hotel.com/";
	public static final String NAMESPACE_GET_HOTELS_SALE_STATE = "http://hotelSaleState.hotelsRoom.app.hotel.com/";
	public static final String NAMESPACE_GET_UPPAY_TN = "http://examples.unionPay.account.app.hotel.com/";

	public static final String METHOD_GET_HOTEL_LIST_BY_LAT_LNG = "findCoordinates";
	public static final String METHOD_GET_HOTEL_LIST_BY_LAT_LNG_LARGE_DATA = "saerchCoordinates";
	public static final String METHOD_GET_HOTEL_LIST_BY_KEYWORD = "hotelsFind";
	public static final String METHOD_LOGIN = "userlogin";
	public static final String METHOD_LOGIN_OAUTH = "userOauthLogin";
	public static final String METHOD_REGISTER = "userAdd";
	public static final String METHOD_REGISTER_SPECIAL = "userSmsCallAdd";
	public static final String METHOD_REGISTER_OAUTH = "userQQAdd";
	public static final String METHOD_UPDATE_USER = "userEdit";
	public static final String METHOD_UPDATE_PSW = "passwordEdit";
	public static final String METHOD_FIND_PSW = "passwordFind";
	public static final String METHOD_SEARCH_HOTEL_LIST = "hotelSearch";
	public static final String METHOD_GET_COLLECTION = "myFavoritesList";
	public static final String METHOD_PUT_COLLECT = "unsubscribe";
	public static final String METHOD_DELETE_ORDER = "booksDel";
	public static final String METHOD_GET_BRAND_PAGE = "brandList";
	public static final String METHOD_GET_LOC_PAGE = "getsowntownList";
	public static final String METHOD_GET_CITY_PAGE = "cityList";
	public static final String METHOD_HOTEL_DETAIL = "hotelOtherFind";
	public static final String METHOD_ORDER_DONE = "bookCheck";
	public static final String METHOD_ORDER_PAY_SUCCESS = "bookSuccess";
	public static final String METHOD_BATCH_DELETE_COLLECT = "delsMyFavorites";
	public static final String METHOD_BATCH_DELETE_ORDER = "booksDels";
	public static final String METHOD_DELETE_USER = "userDel";
	public static final String METHOD_FEED_BACK = "feedback";
	public static final String METHOD_UPDATE_USERNAME = "userUpdate";
	public static final String METHOD_UPDATE_PHONE_OAUTH = "phoneUpdate";
	public static final String METHOD_GET_SERVER_CONFIG = "serviceConfiguration";
	public static final String METHOD_GET_VERIFY_CODE = "phoneRegistrationVerified";
	public static final String METHOD_GET_HOTEL_LIST_BY_LAT_LNG_EL = "findCoordinatesE";
	public static final String METHOD_GET_ROOM_PAGE = "hotelRoom";
	public static final String METHOD_GET_HOTEL_LIST_BY_KEYWORD_EL = "hotelsFindE";
	public static final String METHOD_GET_HOTEL_LIST_BY_LAT_LNG_LARGE_DATA_EL = "saerchCoordinatesE";
	public static final String METHOD_GET_ORDER_PAGE = "booksList";
	public static final String METHOD_ORDER_DETAIL = "booksView";
	public static final String METHOD_GET_HOTEL_UPDATA_DATA = "hotelsChangeData";
	public static final String METHOD_GET_GIFT_PAGE = "eventInforList";
	public static final String METHOD_GET_GIFT_DETAIL = "eventInforView";
	public static final String METHOD_UPDATE_USER_2 = "userEditTwo";
	public static final String METHOD_RESET_PSW = "passwordUpdate";
	public static final String METHOD_VERIFY_CODE_4_REGISTER = "phoneRegistrationVerified";
	public static final String METHOD_SUBMIT_RECHARGE = "recordsAdd";
	public static final String METHOD_QUERY_RECHARGE_RESULT = "recordsView";
	public static final String METHOD_BALANCE_PAY = "balanceOutlay";
	public static final String METHOD_CHECK_ROOM_IS_TUAN = "hotelsRoomtype";
	public static final String METHOD_SUBMIT_ORDER_FULL_ROOM = "booksAdd";
	public static final String METHOD_GET_HOTELS_SALE_STATE = "hotelSaleState";
	public static final String METHOD_GET_UPPAY_TN = "purchaseExample";

	public static final String URL_UPDATE_EMAIL = URL_ZDF_API
			+ "EmailUpdateService";
	public static final String NAMESPACE_UPDATE_EMAIL = "http://emailupdate.users.app.hotel.com/";
	public static final String METHOD_UPDATE_EMAIL = "emailUpdate";

	public static final String URL_UPDATE_REAL_NAME = URL_ZDF_API
			+ "NameUpdateService";
	public static final String NAMESPACE_UPDATE_REAL_NAME = "http://nameUpdate.users.app.hotel.com/";
	public static final String METHOD_UPDATE_REAL_NAME = "nameUpdate";

	public static final String URL_GET_CITY_UPDATE_DATA = URL_ZDF_API
			+ "CityLogUpdateService";
	public static final String NAMESPACE_GET_CITY_UPDATE_DATA = "http://cityLog.services.app.hotel.com/";
	public static final String METHOD_GET_CITY_UPDATE_DATA = "cityLogUpdate";

	public static final String URL_CANCEL_ORDER = URL_ZDF_API
			+ "BooksCancelService";
	public static final String NAMESPACE_CANCEL_ORDER = "http://booksCancel.hotelsBooks.app.hotel.com/";
	public static final String METHOD_CANCEL_ORDER = "booksCancel";

	public static final String URL_GET_COUPON_PAGE = URL_ZDF_API
			+ "CouponFindService";
	public static final String NAMESPACE_GET_COUPON_PAGE = "http://couponFind.coupon.app.hotel.com/";
	public static final String METHOD_GET_COUPON_PAGE = "couponFind";

	public static final String URL_APP_COUPON = URL_ZDF_API
			+ "CouponAddService";
	public static final String NAMESPACE_APP_COUPON = "http://couponAdd.coupon.app.hotel.com/";
	public static final String METHOD_APP_COUPON = "couponAdd";

	public static final String URL_OAUTH_BIND_PHONE = URL_ZDF_API
			+ "PhoneOauthConService";
	public static final String NAMESPACE_OAUTH_BIND_PHONE = "http://phoneOauthcon.users.app.hotel.com/";
	public static final String METHOD_OAUTH_BIND_PHONE = "phoneOauthCon";

	public static final String URL_CHECK_COUPON_4_ORDER = URL_ZDF_API
			+ "CouponCheckService";;
	public static final String NAMESPACE_CHECK_COUPON_4_ORDER = "http://couponCheck.coupon.app.hotel.com/";
	public static final String METHOD_CHECK_COUPON_4_ORDER = "couponCheck";

	public static final String URL_QUERY_BALANCE = URL_ZDF_API
			+ "CheckAccountService";
	public static final String NAMESPACE_QUERY_BALANCE = "http://checkaccount.users.app.hotel.com/";
	public static final String METHOD_QUERY_BALANCE = "checkaccount";

	//
	// public static final String URL_PAY_NOTIFY_URL = URL_ZDF_API
	// + "notify_url.jsp";
	// public static final String URL_GET_HOTEL_LIST_BY_LAT_LNG = URL_ZDF_API
	// + "FindCoordinatesService";
	// public static final String URL_GET_HOTEL_LIST_BY_KEYWORD = URL_ZDF_API
	// + "HotelsFindService";
	// public static final String URL_LOGIN = URL_ZDF_API + "UserLoginService";
	// public static final String URL_LOGIN_OAUTH = URL_ZDF_API
	// + "UserOauthLoginService";
	// public static final String URL_REGISTER = URL_ZDF_API + "UserAddService";
	// public static final String URL_REGISTER_SPECIAL = URL_ZDF_API
	// + "UserRegistrationService";
	// public static final String URL_REGISTER_SPECIAL_SMS = URL_ZDF_API
	// + "UserSmsCallAddService";
	// public static final String URL_REGISTER_OAUTH = URL_ZDF_API
	// + "UserQQAddService";
	// public static final String URL_GET_ROOM_PAGE1 = URL_ZDF_API
	// + "HotelRoom1Service";
	// public static final String URL_GET_ROOM_PAGE = URL_ZDF_API
	// + "HotelRoomService";
	// public static final String URL_GET_ROOM_PAGE_XC = URL_ZDF_API
	// + "HotelRoomXieChengService";
	// public static final String URL_UPDATE_USER = URL_ZDF_API
	// + "UserEditService";
	// public static final String URL_UPDATE_PSW = URL_ZDF_API
	// + "PasswordEditService";
	// public static final String URL_FIND_PSW = URL_ZDF_API
	// + "PasswordFindService";
	// public static final String URL_SEARCH_HOTEL_LIST = URL_ZDF_API
	// + "HotelSearchService";
	// public static final String URL_HOTEL_DETAIL = URL_ZDF_API
	// + "HotelsViewService";
	// public static final String URL_PUT_COLLECT = URL_ZDF_API
	// + "UnsubscribeService";
	// public static final String URL_GET_COLLECTION = URL_ZDF_API
	// + "MyFavoritesListService";
	// public static final String URL_GET_ORDER_PAGE = URL_ZDF_API
	// + "BooksListService";
	// public static final String URL_GET_ORDER_PAGE_XC = URL_ZDF_API
	// + "BooksListWithXCService";
	// public static final String URL_GET_BRAND_PAGE = URL_ZDF_API
	// + "BrandListService";
	// public static final String URL_GET_CITY_PAGE = URL_ZDF_API
	// + "CityListService";
	// public static final String URL_GET_LOC_PAGE = URL_ZDF_API
	// + "GetSownTownListService";
	// public static final String URL_DELETE_ORDER = URL_ZDF_API
	// + "BooksDelService";
	// public static final String URL_ORDER_DETAIL = URL_ZDF_API
	// + "BooksViewService";
	// public static final String URL_ORDER_DETAIL_XC = URL_ZDF_API
	// + "BooksViewWithXCService";
	// public static final String URL_SUBMIT_ORDER = URL_ZDF_API
	// + "BooksAddService";
	// public static final String URL_SUBMIT_ORDER_CONTAIN_XC = URL_ZDF_API
	// + "BooksAddWithXCService";
	// public static final String URL_ORDER_DONE = URL_ZDF_API
	// + "BookCheckService";
	// public static final String URL_ORDER_PAY_SUCCESS = URL_ZDF_API
	// + "BookSuccessService";
	// public static final String URL_BATCH_DELETE_COLLECT = URL_ZDF_API
	// + "MyFavoritesDelsService";
	// public static final String URL_BATCH_DELETE_ORDER = URL_ZDF_API
	// + "BooksDelsService";
	// public static final String URL_VERIFY_CODE = URL_ZDF_API
	// + "PhoneVerifiedService";
	// public static final String URL_DELETE_USER = URL_ZDF_API +
	// "UserDelService";
	// public static final String URL_GET_HOTEL_LIST_BY_LAT_LNG_LARGE_DATA =
	// URL_ZDF_API
	// + "SaerchCoordinatesService";
	// public static final String URL_GET_HOTELS_PRICE = URL_ZDF_API
	// + "HotelsUpdateDateService";
	// public static final String URL_FEED_BACK = URL_ZDF_API +
	// "FeedbackService";
	// public static final String URL_UPDATE_USERNAME = URL_ZDF_API
	// + "UserUpdateService";
	// public static final String URL_UPDATE_USER_PHONE = URL_ZDF_API
	// + "PhoneUpdateService";
	// public static final String URL_GET_SERVER_CONFIG = URL_ZDF_API
	// + "ServiceConfigurationService";
	// public static final String URL_VERIFY_CODE_4_FIND_PSW = URL_ZDF_API
	// + "UserPhoneFindService";
	// public static final String URL_GET_HOTEL_LIST_BY_LAT_LNG_EL = URL_ZDF_API
	// + "FindCoordinatesEService";
	// public static final String URL_GET_ROOM_PAGE_EL = URL_ZDF_API
	// + "HotelRoomEService";
	// public static final String URL_GET_HOTEL_LIST_BY_KEYWORD_EL = URL_ZDF_API
	// + "HotelsFindEService";
	// public static final String URL_GET_HOTELS_PRICE_EL = URL_ZDF_API
	// + "HotelsUpdateDateEService";
	// public static final String URL_GET_HOTEL_LIST_BY_LAT_LNG_LARGE_DATA_EL =
	// URL_ZDF_API
	// + "SaerchCoordinatesEService";
	// public static final String URL_SUBMIT_ORDER_ZL = URL_ZDF_API
	// + "BooksAddEService";
	// public static final String URL_GET_ORDER_PAGE_EL = URL_ZDF_API
	// + "BooksListEService";
	// public static final String URL_ORDER_DETAIL_EL = URL_ZDF_API
	// + "BooksViewEService";
	// public static final String URL_GET_HOTEL_LIST_ALL_EL = URL_ZDF_API
	// + "SaerchCoordinatesAllEService";
	// public static final String URL_GET_GIFT_PAGE = URL_ZDF_API
	// + "EventInforListService";
	// public static final String URL_GET_GIFT_DETAIL = URL_ZDF_API
	// + "EventInforViewService";
	// public static final String URL_UPDATE_USER_2 = URL_ZDF_API
	// + "UserEditTwoService";
	// public static final String URL_RESET_PSW = URL_ZDF_API
	// + "PasswordUpdateService";
	// public static final String URL_VERIFY_CODE_4_REGISTER = URL_ZDF_API
	// + "PhoneRegistrationVerifiedService";
	// public static final String URL_SUBMIT_RECHARGE = URL_ZDF_API
	// + "RecordsAddService";
	// public static final String URL_QUERY_RECHARGE_OR_BALANCE = URL_ZDF_API
	// + "RecordsViewService";
	// public static final String URL_BALANCE_PAY = URL_ZDF_API
	// + "BalanceOutLayService";
	// public static final String URL_CHECK_ROOM_IS_TUAN = URL_ZDF_API
	// + "HotelsRoomTypeService";
	// public static final String URL_SUBMIT_ORDER_FULL_ROOM = URL_ZDF_API
	// + "BooksAddCService";
	// public static final String URL_GET_HOTELS_PRICE_HAS_TUAN = URL_ZDF_API
	// + "HotelsUpdateDateCService";
	// public static final String URL_GET_UPPAY_TN = URL_ZDF_API
	// + "PurchaseExampleService";
	//
	// public static final String NAMESPACE_GET_HOTEL_LIST_BY_LAT_LNG =
	// "http://findCoordinates.hotels.web.hotel.com/";
	// public static final String NAMESPACE_GET_HOTEL_LIST_BY_LAT_LNG_LARGE_DATA
	// = "http://findCoordinates.hotels.web.hotel.com/";
	// public static final String NAMESPACE_GET_HOTEL_LIST_BY_KEYWORD =
	// "http://hotelsFind.hotels.web.hotel.com/";
	// public static final String NAMESPACE_LOGIN =
	// "http://userLogin.user.web.hotel.com/";
	// public static final String NAMESPACE_LOGIN_OAUTH =
	// "http://userOauthLogin.user.web.hotel.com/";
	// public static final String NAMESPACE_REGISTER =
	// "http://userAdd.user.web.hotel.com/";
	// public static final String NAMESPACE_REGISTER_SPECIAL =
	// "http://userRegistration.user.web.hotel.com/";
	// public static final String NAMESPACE_REGISTER_SPECIAL_SMS =
	// "http://userSmsCallAdd.user.web.hotel.com/";
	// public static final String NAMESPACE_REGISTER_OAUTH =
	// "http://userQQAdd.user.web.hotel.com/";
	// public static final String NAMESPACE_GET_ROOM_PAGE1 =
	// "http://hotelRoom1.hotels.web.hotel.com/";
	// public static final String NAMESPACE_GET_ROOM_PAGE =
	// "http://hotelRoom.hotels.web.hotel.com/";
	// public static final String NAMESPACE_GET_ROOM_PAGE_XC =
	// "http://hotelRoomXieCheng.hotels.web.hotel.com/";
	// public static final String NAMESPACE_UPDATE_USER =
	// "http://userEdit.user.web.hotel.com/";
	// public static final String NAMESPACE_UPDATE_PSW =
	// "http://passwordEdit.userPassword.user.web.hotel.com/";
	// public static final String NAMESPACE_FIND_PSW =
	// "http://passwordFind.userPassword.user.web.hotel.com/";
	// public static final String NAMESPACE_SEARCH_HOTEL_LIST =
	// "http://hotelSearch.hotels.web.hotel.com/";
	// public static final String NAMESPACE_HOTEL_DETAIL =
	// "http://hotelsView.hotels.web.hotel.com/";
	// public static final String NAMESPACE_PUT_COLLECT =
	// "http://myFavoritesDel.myFavorites.web.hotel.com/";
	// public static final String NAMESPACE_GET_COLLECTION =
	// "http://myFavoritesList.myFavorites.web.hotel.com/";
	// public static final String NAMESPACE_GET_ORDER_PAGE =
	// "http://booksList.hotelsBooks.web.hotel.com/";
	// public static final String NAMESPACE_GET_ORDER_PAGE_XC =
	// "http://booksListWithXC.hotelsBooks.web.hotel.com/";
	// public static final String NAMESPACE_GET_BRAND_PAGE =
	// "http://brand.web.hotel.com/";
	// public static final String NAMESPACE_GET_CITY_PAGE =
	// "http://city.web.hotel.com/";
	// public static final String NAMESPACE_GET_LOC_PAGE =
	// "http://getsowntownlist.endpoint.hotel.com/";
	// public static final String NAMESPACE_DELETE_ORDER =
	// "http://booksDel.hotelsBooks.web.hotel.com/";
	// public static final String NAMESPACE_ORDER_DETAIL =
	// "http://booksView.hotelsBooks.web.hotel.com/";
	// public static final String NAMESPACE_ORDER_DETAIL_XC =
	// "http://booksViewWithXC.hotelsBooks.web.hotel.com/";
	// public static final String NAMESPACE_SUBMIT_ORDER =
	// "http://booksAdd.hotelsBooks.web.hotel.com/";
	// public static final String NAMESPACE_SUBMIT_ORDER_CONTAIN_XC =
	// "http://booksAddWithXC.hotelsBooks.web.hotel.com/";
	// public static final String NAMESPACE_ORDER_DONE =
	// "http://bookCheck.roomBook.endpoint.hotel.com/";
	// public static final String NAMESPACE_ORDER_PAY_SUCCESS =
	// "http://bookSuccess.hotelsBooks.web.hotel.com/";
	// public static final String NAMESPACE_BATCH_DELETE_COLLECT =
	// "http://myFavoritesDels.myFavorites.web.hotel.com/";
	// public static final String NAMESPACE_BATCH_DELETE_ORDER =
	// "http://booksDels.hotelsBooks.web.hotel.com/";
	// public static final String NAMESPACE_VERIFY_CODE =
	// "http://phoneVerified.user.web.hotel.com/";
	// public static final String NAMESPACE_DELETE_USER =
	// "http://userDel.user.web.hotel.com/";
	// public static final String NAMESPACE_GET_HOTELS_PRICE =
	// "http://hotelsPrice.hotels.web.hotel.com/";
	// public static final String NAMESPACE_FEED_BACK =
	// "http://feedback.web.hotel.com/";
	// public static final String NAMESPACE_UPDATE_USERNAME =
	// "http://userUpdate.user.web.hotel.com/";
	// public static final String NAMESPACE_UPDATE_USER_PHONE =
	// "http://phoneUpdate.user.web.hotel.com/";
	// public static final String NAMESPACE_GET_SERVER_CONFIG =
	// "http://serviceConfiguration.services.app.hotel.com/";
	// public static final String NAMESPACE_VERIFY_CODE_4_FIND_PSW =
	// "http://userPhoneFind.user.web.hotel.com/";
	// public static final String NAMESPACE_GET_HOTEL_LIST_BY_LAT_LNG_EL =
	// "http://findCoordinatesE.hotels.elong.hotel.com/";
	// public static final String NAMESPACE_GET_ROOM_PAGE_EL =
	// "http://hotelsRoomE.elong.hotel.com/";
	// public static final String NAMESPACE_GET_HOTEL_LIST_BY_KEYWORD_EL =
	// "http://hotelsFindE.hotels.elong.hotel.com/";
	// public static final String NAMESPACE_GET_HOTELS_PRICE_EL =
	// "http://hotelsPriceE.hotels.elong.hotel.com/";
	// public static final String
	// NAMESPACE_GET_HOTEL_LIST_BY_LAT_LNG_LARGE_DATA_EL =
	// "http://findCoordinatesE.hotels.elong.hotel.com/";
	// public static final String NAMESPACE_SUBMIT_ORDER_ZL =
	// "http://booksAddE.hotelsBooksE.elong.hotel.com/";
	// public static final String NAMESPACE_GET_ORDER_PAGE_EL =
	// "http://booksListE.hotelsBooksE.elong.hotel.com/";
	// public static final String NAMESPACE_ORDER_DETAIL_EL =
	// "http://booksViewE.hotelsBooksE.elong.hotel.com/";
	// public static final String NAMESPACE_GET_HOTEL_LIST_ALL_EL =
	// "http://saerchCoordinatesAllE.hotels.elong.hotel.com/";
	// public static final String NAMESPACE_GET_GIFT_PAGE =
	// "http://eventInfor.web.hotel.com/";
	// public static final String NAMESPACE_GET_GIFT_DETAIL =
	// "http://eventInfor.web.hotel.com/";
	// public static final String NAMESPACE_UPDATE_USER_2 =
	// "http://userEdit.user.web.hotel.com/";
	// public static final String NAMESPACE_RESET_PSW =
	// "http://passwordUpdate.userPassword.user.web.hotel.com/";
	// public static final String NAMESPACE_VERIFY_CODE_4_REGISTER =
	// "http://phoneRegistrationVerified.user.web.hotel.com/";
	// public static final String NAMESPACE_SUBMIT_RECHARGE =
	// "http://records.account.hotel.com/";
	// public static final String NAMESPACE_QUERY_RECHARGE_OR_BALANCE =
	// "http://records.account.hotel.com/";
	// public static final String NAMESPACE_BALANCE_PAY =
	// "http://outlay.account.hotel.com/";
	// public static final String NAMESPACE_CHECK_ROOM_IS_TUAN =
	// "http://hotelsRoomE.elong.hotel.com/";
	// public static final String NAMESPACE_SUBMIT_ORDER_FULL_ROOM =
	// "http://hotelsBooksC.webC.hotel.com/";
	// public static final String NAMESPACE_GET_HOTELS_PRICE_HAS_TUAN =
	// "http://hotelsPriceC.webC.hotel.com/";
	// public static final String NAMESPACE_GET_UPPAY_TN =
	// "http://examples.unionPay.hotel.com/";
	//
	// public static final String METHOD_GET_HOTEL_LIST_BY_LAT_LNG =
	// "findCoordinates";
	// public static final String METHOD_GET_HOTEL_LIST_BY_LAT_LNG_LARGE_DATA =
	// "saerchCoordinates";
	// public static final String METHOD_GET_HOTEL_LIST_BY_KEYWORD =
	// "hotelsFind";
	// public static final String METHOD_LOGIN = "userlogin";
	// public static final String METHOD_LOGIN_OAUTH = "userOauthLogin";
	// public static final String METHOD_REGISTER = "userAdd";
	// public static final String METHOD_REGISTER_SPECIAL = "userRegistration";
	// public static final String METHOD_REGISTER_SPECIAL_SMS =
	// "userSmsCallAdd";
	// public static final String METHOD_REGISTER_OAUTH = "userQQAdd";
	// public static final String METHOD_UPDATE_USER = "userEdit";
	// public static final String METHOD_UPDATE_PSW = "passwordEdit";
	// public static final String METHOD_FIND_PSW = "passwordFind";
	// public static final String METHOD_SEARCH_HOTEL_LIST = "hotelSearch";
	// public static final String METHOD_GET_ROOM_PAGE1 = "hotelRoom1";
	// public static final String METHOD_GET_ROOM_PAGE = "hotelRoom";
	// public static final String METHOD_GET_ROOM_PAGE_XC = "hotelRoomXieCheng";
	// public static final String METHOD_GET_COLLECTION = "myFavoritesList";
	// public static final String METHOD_PUT_COLLECT = "unsubscribe";
	// public static final String METHOD_GET_ORDER_PAGE = "booksList";
	// public static final String METHOD_GET_ORDER_PAGE_XC = "booksList";
	// public static final String METHOD_DELETE_ORDER = "booksDel";
	// public static final String METHOD_GET_BRAND_PAGE = "brandList";
	// public static final String METHOD_GET_LOC_PAGE = "getsowntownList";
	// public static final String METHOD_GET_CITY_PAGE = "cityList";
	// public static final String METHOD_HOTEL_DETAIL = "hotelsView";
	// public static final String METHOD_ORDER_DETAIL = "booksView";
	// public static final String METHOD_ORDER_DETAIL_XC = "booksViewWithXC";
	// public static final String METHOD_SUBMIT_ORDER = "bookAdd";
	// public static final String METHOD_SUBMIT_ORDER_CONTAIN_XC =
	// "booksAddWithXC";
	// public static final String METHOD_ORDER_DONE = "bookCheck";
	// public static final String METHOD_ORDER_PAY_SUCCESS = "bookSuccess";
	// public static final String METHOD_BATCH_DELETE_COLLECT =
	// "delsMyFavorites";
	// public static final String METHOD_BATCH_DELETE_ORDER = "booksDels";
	// public static final String METHOD_VERIFY_CODE = "phoneVerified";
	// public static final String METHOD_DELETE_USER = "userDel";
	// public static final String METHOD_GET_HOTELS_PRICE = "hotelsUpdateDate";
	// public static final String METHOD_FEED_BACK = "feedback";
	// public static final String METHOD_UPDATE_USERNAME = "userUpdate";
	// public static final String METHOD_UPDATE_USER_PHONE = "phoneUpdate";
	// public static final String METHOD_GET_SERVER_CONFIG =
	// "serviceConfiguration";
	// public static final String METHOD_VERIFY_CODE_4_FIND_PSW =
	// "userPhoneFind";
	// public static final String METHOD_GET_HOTEL_LIST_BY_LAT_LNG_EL =
	// "findCoordinatesE";
	// public static final String METHOD_GET_ROOM_PAGE_EL = "hotelRoomE";
	// public static final String METHOD_GET_HOTEL_LIST_BY_KEYWORD_EL =
	// "hotelsFindE";
	// public static final String METHOD_GET_HOTELS_PRICE_EL =
	// "hotelsUpdateDateE";
	// public static final String METHOD_GET_HOTEL_LIST_BY_LAT_LNG_LARGE_DATA_EL
	// = "saerchCoordinatesE";
	// public static final String METHOD_SUBMIT_ORDER_ZL = "booksAddE";
	// public static final String METHOD_GET_ORDER_PAGE_EL = "booksList";
	// public static final String METHOD_ORDER_DETAIL_EL = "booksView";
	// public static final String METHOD_GET_HOTEL_LIST_ALL_EL =
	// "saerchCoordinatesAllE";
	// public static final String METHOD_GET_GIFT_PAGE = "eventInforList";
	// public static final String METHOD_GET_GIFT_DETAIL = "eventInforView";
	// public static final String METHOD_UPDATE_USER_2 = "userEditTwo";
	// public static final String METHOD_RESET_PSW = "passwordUpdate";
	// public static final String METHOD_VERIFY_CODE_4_REGISTER =
	// "phoneRegistrationVerified";
	// public static final String METHOD_SUBMIT_RECHARGE = "recordsAdd";
	// public static final String METHOD_QUERY_RECHARGE_OR_BALANCE =
	// "recordsView";
	// public static final String METHOD_BALANCE_PAY = "balanceOutlay";
	// public static final String METHOD_CHECK_ROOM_IS_TUAN = "hotelsRoomtype";
	// public static final String METHOD_SUBMIT_ORDER_FULL_ROOM = "booksAddC";
	// public static final String METHOD_GET_HOTELS_PRICE_HAS_TUAN =
	// "hotelsUpdateDateC";
	// public static final String METHOD_GET_UPPAY_TN = "purchaseExample";

	private int objId;
	private String objKey = "";
	private int objType;

	public int getObjId() {
		return objId;
	}

	public void setObjId(int objId) {
		this.objId = objId;
	}

	public String getObjKey() {
		return objKey;
	}

	public void setObjKey(String objKey) {
		this.objKey = objKey;
	}

	public int getObjType() {
		return objType;
	}

	public void setObjType(int objType) {
		this.objType = objType;
	}

	/**
	 * 解析url获得objId
	 * 
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjId(String path, String url_type) {
		String objId = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if (str.contains(URL_SPLITTER)) {
			tmp = str.split(URL_SPLITTER);
			objId = tmp[0];
		} else {
			objId = str;
		}
		return objId;
	}

	/**
	 * 解析url获得objKey
	 * 
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjKey(String path, String url_type) {
		path = URLDecoder.decode(path);
		String objKey = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if (str.contains("?")) {
			tmp = str.split("?");
			objKey = tmp[0];
		} else {
			objKey = str;
		}

		return objKey;
	}

	/**
	 * 对URL进行格式处理
	 * 
	 * @param path
	 * @return
	 */
	private final static String formatURL(String path) {
		if (path.startsWith("http://") || path.startsWith("https://"))
			return path;
		return "http://" + URLEncoder.encode(path);
	}
}
