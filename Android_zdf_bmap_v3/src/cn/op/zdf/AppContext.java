package cn.op.zdf;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Build;
import android.os.Message;
import cn.op.common.AppConfig;
import cn.op.common.AppConfigOnSdcard;
import cn.op.common.AppException;
import cn.op.common.BaseApplication;
import cn.op.common.constant.Constant;
import cn.op.common.constant.Keys;
import cn.op.common.domain.RspMsg;
import cn.op.common.domain.UserInfo;
import cn.op.common.util.Constants;
import cn.op.common.util.DateUtil;
import cn.op.common.util.LatLngUtil;
import cn.op.common.util.Log;
import cn.op.common.util.PhoneUtil;
import cn.op.common.util.RoundTool;
import cn.op.common.util.StringUtils;
import cn.op.zdf.dao.MyDbHelper;
import cn.op.zdf.domain.City;
import cn.op.zdf.domain.CityPage;
import cn.op.zdf.domain.Item;
import cn.op.zdf.domain.ItemPage;
import cn.op.zdf.domain.Recharge;
import cn.op.zdf.domain.Room;
import cn.op.zdf.domain.RoomPage;
import cn.op.zdf.domain.ServerConfig;
import cn.op.zdf.event.DeleteRecentBrowseHotelEvent;
import cn.op.zdf.event.LoginEvent;
import cn.op.zdf.event.ReqHotelPageEvent;
import cn.op.zdf.event.RspHotelPageEvent;

import com.baidu.mapapi.model.LatLng;
import com.tendcloud.tenddata.TCAgent;

import de.greenrobot.event.EventBus;

/**
 * Application对象，用作全局容器、应用的初始化控制、以及直接调用ApiClient作为业务处理层
 * 
 * @author lufei
 * 
 */
public class AppContext extends BaseApplication {
	private static final String TAG = Log.makeLogTag(AppContext.class);
	private static AppContext ac;
	/**
	 * 是否正在填写订单
	 */
	public boolean isReserve;

	private String imei;

	// public boolean need2Exit;
	// private UEHandler ueHandler;
	/**
	 * 是否正在更新酒店缓存
	 */
	protected boolean isCacheingHotelData;

	public double lastReqLatitude;
	public double lastReqLongitude;

	/**
	 * 是否处于搜索状态，未取消搜索
	 */
	public boolean isSearch;
	/**
	 * 数据加载前，是否刚刚进行过搜索
	 */
	public boolean isJustSearch;
	/**
	 * 当前搜索的关键字
	 */
	public String searchKeyword;
	/**
	 * 从关键字匹配中选择了一家酒店
	 */
	public boolean isSelectOneHotel;

	public boolean mHotelListShowChange = false;
	public int saleType = Room.SALE_TYPE_ZDF;
	public static int curtOrderBy = Item.ORDER_BY_DISTANCE;

	/**
	 * 上次小人位置
	 */
	public LatLng lastChooseLoc;

	/**
	 * 数据加载前，是否刚刚切换过城市
	 */
	public boolean isJustCityChange = false;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "======onCreate======");
		ac = this;

		// imei = getImei();
		// Log.d(TAG, "======imei======" + imei);

		initBMapManager(ac);

		// app启动即请求定位
		startLocBD();

		// 设置异常处理实例
		// need2Exit = false;
		// ueHandler = new UEHandler(this);
		// Thread.setDefaultUncaughtExceptionHandler(ueHandler);

		initApp();
		initImageLoader(getApplicationContext());
		// initJpush();
	}

	/**
	 * 设置首次定位回调，第一次获取位置后就开始更新酒店缓存
	 */
	// private void preInitHotelCache() {
	// Log.d(TAG, "======preInitHotelCache======");
	// // 安装数据库， 调用一次数据库查询，即可提前安装数据库
	// MyDbHelper.getInstance(ac).queryHotelPageCacheByLatLng(0, 0);
	//
	// firstLoc = new FirstLoc() {
	// @Override
	// public void locationFirst(double latitude, double longitude) {
	// Log.d(TAG, "======preInitHotelCache======: locationFirst");
	// ac.getHotelPageInsertDb(MyDbHelper.getInstance(ac), latitude,
	// longitude, false);
	// firstLoc = null;
	// }
	// };
	// }

	/**
	 * 极光推送
	 */
	private void initJpush() {
		// JPushInterface.setDebugMode(true);
		// JPushInterface.init(this);
	}

	@Override
	// 建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	public void onTerminate() {
		Log.d(TAG, "======onTerminate======");
		stopLocBD();
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		Log.d(TAG, "======onLowMemory======");
		super.onLowMemory();
	}

	public static AppContext getAc() {
		return ac;
	}

	private void initApp() {
		AppConfig acf = AppConfig.getAppConfig(this);

		// 判断用户类型
		String userType = acf.get(Keys.LAST_LOGIN_USER_TYPE);
		String nickname = acf.get(Keys.LAST_LOGIN_NICKNAME);
		String username = acf.get(Keys.LAST_LOGIN_USERNAME);
		String psw = acf.get(Keys.PSW);

		if (UserInfo.USER_TYPE_NORMAL.equals(userType)) {
			if (username != null && psw != null) {
				autoLogin(username, psw);
			}
		} else if (UserInfo.USER_TYPE_OAUTH_QZONE.equals(userType)) {
			if (nickname != null && username != null) {
				autoLoginOAuth(username, userType, nickname);
			}
		} else if (UserInfo.USER_TYPE_OAUTH_SINA.equals(userType)) {
			if (nickname != null && username != null) {
				autoLoginOAuth(username, userType, nickname);
			}
		}
	}

	public void autoLogin(final String username, final String psw) {
		new Thread() {

			public void run() {
				try {
					UserInfo user = login(username, psw);

					if (user != null && user.rspMsg.OK()) {
						user.login_pwd = psw;
						ac.user = user;

						LoginEvent event = new LoginEvent();
						event.success = true;
						EventBus.getDefault().post(event);
					}
				} catch (AppException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public RspMsg checkUserExist(String username) throws AppException {
		return ApiClient.checkUserExist(username);
	}

	/**
	 * 登录
	 * 
	 * @param username
	 * @param psw
	 * @return
	 * @throws AppException
	 */
	public UserInfo login(String username, String psw) throws AppException {
		// 向talkingdata收集数据
		Map<String, Object> map = new HashMap<String, Object>();

		LatLng myLocation = getMyLocation();

		String loc = myLocation.latitude + "," + myLocation.longitude;

		map.put("最后一次登录时间", StringUtils.getNow());
		map.put("最后一次登录时位置", loc);
		map.put("登录方式", "用户登录");
		map.put("用户名", username);

		TCAgent.onEvent(ac, "登录统计", "登录数据", map);

		return ApiClient.login(this, username, psw);
	}

	public void autoLoginOAuth(final String uid, final String platformType,
			final String nickname) {
		new Thread() {
			public void run() {
				try {
					// 向talkingdata收集数据
					Map<String, Object> map = new HashMap<String, Object>();
					LatLng myLocation = getMyLocation();
					String loc = myLocation.latitude + ","
							+ myLocation.longitude;

					map.put("最后一次登录时间", StringUtils.getNow());
					map.put("最后一次登录时位置", loc);
					map.put("登录方式", "授权登录");
					map.put("用户名", nickname);

					TCAgent.onEvent(ac, "登录统计", "登录数据", map);

					UserInfo user = ApiClient.loginOAuth(uid, platformType,
							nickname);

					if (user.rspMsg.OK()) {

						ac.user = user;

						AppConfig acf = AppConfig.getAppConfig(ac);
						acf.set(Keys.LAST_LOGIN_USER_TYPE, "" + user.userType);
						acf.set(Keys.LAST_LOGIN_USERNAME, user.username);
						acf.set(Keys.LAST_LOGIN_NICKNAME, user.nickname);

						LoginEvent event = new LoginEvent();
						event.success = true;
						EventBus.getDefault().post(event);
					} else {
						AppContext.toastShow(user.rspMsg.message);
					}
				} catch (AppException e) {
					e.printStackTrace();
					// e.makeToast(ac);
				}
			}
		}.start();
	}

	public UserInfo loginOAuth(final String uid, final String platformType,
			final String nickname) throws AppException {

		// 向talkingdata收集数据
		Map<String, Object> map = new HashMap<String, Object>();
		LatLng myLocation = ac.getMyLocation();
		String loc = myLocation.latitude + "," + myLocation.longitude;

		map.put("最后一次登录时间", StringUtils.getNow());
		map.put("最后一次登录时位置", loc);
		map.put("登录方式", "授权登录");
		map.put("用户名", nickname);

		TCAgent.onEvent(ac, "登录统计", "登录数据", map);

		return ApiClient.loginOAuth(uid, platformType, nickname);

		// new Thread() {
		// public void run() {
		// try {
		// // 向talkingdata收集数据
		// Map<String, Object> map = new HashMap<String, Object>();
		// LocationData myLocation = getMyLocation();
		// String loc = myLocation.latitude + ","
		// + myLocation.longitude;
		//
		// map.put("最后一次登录时间", StringUtils.getNow());
		// map.put("最后一次登录时位置", loc);
		// map.put("登录方式", "授权登录");
		// map.put("用户名", nickname);
		//
		// TCAgent.onEvent(ac, "登录统计", "登录数据", map);
		//
		// UserInfo user = ApiClient.loginOAuth(uid, platformType,
		// nickname);
		//
		// if (user.rspMsg.OK()
		// || user.rspMsg.code
		// .equals(RspMsg.CODE_REGISTER_OAUTH_USER_EXIST)) {
		//
		// ac.user = user;
		//
		// AppConfig acf = AppConfig.getAppConfig(ac);
		// acf.set(Keys.LAST_LOGIN_USER_TYPE, "" + user.userType);
		// acf.set(Keys.LAST_LOGIN_USERNAME, user.username);
		// acf.set(Keys.LAST_LOGIN_NICKNAME, user.nickname);
		//
		// LoginEvent event = new LoginEvent();
		// event.success = true;
		// EventBus.getDefault().post(event);
		// } else {
		// AppContext.toastShow(user.rspMsg.message);
		// }
		// } catch (AppException e) {
		// e.printStackTrace();
		// }
		// }
		// }.start();
	}

//	public ItemPage getHotelByLatLng(double latitude, double longitude, int type)
//			throws AppException {
//		return ApiClient.getHotelByLatLng(latitude, longitude, type);
//	}

//	public ItemPage getHotelByLatLngLageData(double latitude, double longitude,
//			String lastUpdateTime) throws AppException {
//		return ApiClient.getHotelByLatLngLageData(latitude, longitude,
//				lastUpdateTime);
//	}

	protected ItemPage getHotelUpdateData(String cityId, String lastUpdateTime)
			throws AppException {
		return ApiClient.getHotelUpdateData(cityId, lastUpdateTime);
	}

//	public ItemPage getHotelByKeyword(double latitude, double longitude,
//			String keyword, String cityId) throws AppException {
//		return ApiClient
//				.getHotelByKeyword(latitude, longitude, keyword, cityId);
//	}

//	public RspMsg updateUser(String userId, String realName, String email)
//			throws AppException {
//		return ApiClient.updateUser(userId, realName, email);
//	}

	public RspMsg findPsw(String phone) throws AppException {
		return ApiClient.findPsw(phone);
	}

	public RspMsg updatePsw(String username, String oldPsw, String newPsw)
			throws AppException {
		return ApiClient.updatePsw(username, oldPsw, newPsw);
	}

	public RoomPage getRoomPage(String hotelId, String brandId)
			throws AppException {
		return ApiClient.getRoomPage(hotelId, brandId);
	}

	public ItemPage getOrderPage(String username, int pageNum)
			throws AppException {
		return ApiClient.getOrderPage(username, pageNum, AppContext.PAGE_SIZE);
	}

//	public ItemPage getCollectPage(String username, double latitude,
//			double longitude) throws AppException {
//		return ApiClient.getCollectPage(username, latitude, longitude);
//	}

//	public ItemPage getBrandPage(boolean isRefresh) throws AppException {
//		ItemPage list;
//
//		String key = "getBrandPage_" + "_" + PAGE_NUM + "_" + PAGE_SIZE;
//		if (isNetworkConnected() && (isRefresh || !isReadDataCache(key))) {
//			try {
//				list = ApiClient.getBrandPage();
//				if (list != null) {
//					list.setCacheKey(key);
//
//					saveObject(list, key);
//				}
//			} catch (AppException e) {
//				Log.e(TAG, "======getBrandPage======", e);
//				list = (ItemPage) readObject(key);
//				if (list == null)
//					throw e;
//			}
//		} else {
//			list = (ItemPage) readObject(key);
//			if (list == null)
//				list = new ItemPage();
//		}
//		return list;
//	}

	public ItemPage getCommentPage(String hotelId) throws AppException {
		return ApiClient.getCommentPage(hotelId);
	}

	public UserInfo register(String username, String psw, String verifyCode)
			throws AppException {
		return ApiClient.register(username, psw, verifyCode);
	}

	public Item getHotel(String hotelId) throws AppException {
		return ApiClient.getHotel(hotelId);
	}

//	/**
//	 * @param hotelId
//	 * @param userId
//	 * @param collect
//	 *            true-收藏，false-取消收藏
//	 * @param saleType
//	 * @return
//	 * @throws AppException
//	 */
//	public RspMsg putCollect(String hotelId, String userId, boolean collect,
//			int saleType) throws AppException {
//		return ApiClient.putCollect(hotelId, userId, collect, saleType);
//	}

//	public RspMsg batchDeleteCollect(String batchDelIds, String userId,
//			boolean delete) throws AppException {
//		return ApiClient.batchDeleteCollect(batchDelIds, userId, delete);
//	}

//	/**
//	 * 获取城市列表，如果本地有缓存则取缓存
//	 * 
//	 * @param isRefresh
//	 *            ，true-不管是否有缓存都重新获取
//	 * @return
//	 * @throws AppException
//	 */
//	public CityPage getCityList(boolean isRefresh) throws AppException {
//		CityPage list;
//
//		String imei = PhoneUtil.getImei(ac);
//
//		String key = "getCityList_" + "_" + PAGE_NUM + "_" + PAGE_SIZE;
//		if (isNetworkConnected() && (isRefresh || !isReadDataCache(key))) {
//			try {
//				list = ApiClient.getCityList(imei);
//				if (list != null) {
//					list.setCacheKey(key);
//
//					saveObject(list, key);
//				}
//			} catch (AppException e) {
//				Log.e(TAG, "======getCityList======", e);
//				list = (CityPage) readObject(key);
//				if (list == null)
//					throw e;
//			}
//		} else {
//			list = (CityPage) readObject(key);
//			if (list == null)
//				list = new CityPage();
//		}
//		return list;
//
//		// return ApiClient.getCityList(imei);
//	}

//	public CityPage getLocList(boolean isRefresh, String cityId)
//			throws AppException {
//		return ApiClient.getLocList(isRefresh, cityId);
//	}

	/**
	 * @param orderIds
	 *            以半角逗号相连的订单id
	 * @param userId
	 * @return
	 * @throws AppException
	 */
	public RspMsg deleteOrder(String orderIds, String userId)
			throws AppException {
		return ApiClient.deleteOrder(orderIds, userId);
	}

//	public RspMsg batchDeleteOrder(String batchDelIds, String userId)
//			throws AppException {
//		return ApiClient.batchDeleteOrder(batchDelIds, userId);
//	}

	public Item submitOrder(String hotelId, String userId, String roomSaleId,
			int roomType, int saleType, String dateArrive, String liveMan,
			String liveManPhone, String contact, String contactPhone,
			String count, int payWay, String hotelCode, String roomPlanId,
			String roomPrice, String brandId, String hotelName)
			throws AppException {
		// taikingdata
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("下单时间", StringUtils.getNow());
		map.put("用户id", userId);
		map.put("酒店名称", hotelName);
		map.put("支付方式", payWay);
		map.put("预订时段", saleType);
		map.put("品牌id", brandId);
		if (user != null) {
			map.put("用户名", StringUtils.isEmpty(user.username) ? ""
					: user.username);
			map.put("手机号", StringUtils.isEmpty(user.userPhone) ? ""
					: user.userPhone);
		}
		map.put("房型", roomType);

		TCAgent.onEvent(ac, "订单提交统计", "订单提交统计", map);

		return ApiClient.submitOrder(hotelId, userId, roomSaleId, roomType,
				saleType, dateArrive, liveMan, liveManPhone, contact,
				contactPhone, count, payWay, hotelCode, roomPlanId, roomPrice,
				brandId, hotelName);
	}

	public Item getOrderDetail(String orderId, int sellType)
			throws AppException {
		return ApiClient.getOrderDetail(orderId, sellType);
	}

//	public RspMsg orderDone(String orderId) throws AppException {
//		return ApiClient.orderDone(orderId);
//	}

	public void saveCurtChooseCity(City city) {
		ac.saveObject(city, Keys.LAST_CHOOSE_CITY);
	}

	public City getCurtChooseCity() {
		return (City) ac.readObject(Keys.LAST_CHOOSE_CITY);
	}

	/**
	 * @param item
	 * @return 是否保存成功
	 */
	public boolean saveRecentBrowseHotel(Item item) {
		ItemPage hotelPage = getRecentBrowseHotel();

		boolean isContain = false;
		int size = hotelPage.list.size();
		for (int i = 0; i < size; i++) {
			if (hotelPage.list.get(i).hotelsId.equals(item.hotelsId)) {
				isContain = true;
				hotelPage.list.remove(i);
				break;
			}
		}

		hotelPage.list.add(0, item);
		saveObject(hotelPage, hotelPage.getCacheKey());

		return isContain;
	}

	public ItemPage getRecentBrowseHotel() {
		String key = "recent_browse_hotel";

		ItemPage hotelPage;
		hotelPage = (ItemPage) readObject(key);

		if (hotelPage == null) {
			hotelPage = new ItemPage();

			RspMsg msg = new RspMsg();
			msg.code = RspMsg.CODE_SUCCESS;
			hotelPage.rspMsg = msg;

			hotelPage.list = new ArrayList<Item>();

			hotelPage.setCacheKey(key);
		}

		if (ac.myLocationBD != null) {
			for (int i = 0; i < hotelPage.list.size(); i++) {
				Item item = hotelPage.list.get(i);

				double distance = LatLngUtil.getDistance(
						ac.myLocationBD.getLatitude(),
						ac.myLocationBD.getLongitude(), item.hotelsLatitude,
						item.hotelsLongitude) / 1000;

				distance = RoundTool.round(distance, 1,
						BigDecimal.ROUND_HALF_UP);

				item.dist = "" + distance;

			}
		}

		return hotelPage;
	}

	/**
	 * 清除最近浏览酒店记录，并发出 DeleteRecentBrowseHotelEvent 事件
	 */
	public void removeRecentBrowsHotel() {
		String key = "recent_browse_hotel";
		ac.deleteObject(key);

		DeleteRecentBrowseHotelEvent event = new DeleteRecentBrowseHotelEvent();
		event.delete = true;
		EventBus.getDefault().post(event);
	}

	public void removeRecentBrowsHotel(Item item) {
		ItemPage hotelPage = getRecentBrowseHotel();

		boolean isContain = false;
		for (int i = 0; i < hotelPage.list.size(); i++) {
			if (hotelPage.list.get(i).hotelsId.equals(item.hotelsId)) {
				hotelPage.list.remove(i);
			}
		}

		saveObject(hotelPage, hotelPage.getCacheKey());
	}

	public RspMsg deleteUser(String userId) throws AppException {
		return ApiClient.deleteUser(userId);
	}

	protected CityPage getCityUpdateData(String lastUpdateTime)
			throws AppException {
		return ApiClient.getCityUpdateData(lastUpdateTime);
	}

	/**
	 * 同步城市列表，进行本地缓存，在应用启动时调用调用一次
	 * 
	 * @param dbHelp
	 */
	public void getCityPageInsertDb(final MyDbHelper dbHelp) {
		if (!ac.isNetworkConnected()) {
			AppContext.toastShow(R.string.pleaseCheckNet);
			return;
		}

		new Thread() {
			public void run() {
				AppConfig appConfig = AppConfigOnSdcard
						.getAppConfig(getApplicationContext());

				String versionCodeLast = appConfig.get(Keys.APK_VERCODE_LAST);
				String versionCode = ""
						+ PhoneUtil.getVersionCode(getApplicationContext());

				// 当前版本跟上一次进入的版本不一样，需要重置数据更新时间
				if (!versionCode.equals(versionCodeLast)) {
					appConfig.set(Keys.IS_NEED_RESET_UPDATE_TIME, "true");
				}

				String lastUpdateTime = appConfig
						.get(Keys.LAST_CACHE_HOTEL_UPDATE_TIME);

				String isNeedResetUpdateTime = appConfig
						.get(Keys.IS_NEED_RESET_UPDATE_TIME);

				try {

					if ("true".equals(isNeedResetUpdateTime)
							|| lastUpdateTime == null) {
						// sqlite数据库导入数据时间
						lastUpdateTime = MyDbHelper.DATE_IMPORT_TIME;
					}

					String reqTime = DateUtil.getDate4Zdf();

					CityPage q = ac.getCityUpdateData(lastUpdateTime);

					if (q.rspMsg.OK() && q.list.size() > 0) {
						dbHelp.insertCityData(q.list);

						if (lastUpdateTime.equals(MyDbHelper.DATE_IMPORT_TIME)) {
							appConfig.set(Keys.IS_NEED_RESET_UPDATE_TIME,
									"false");
						}

						AppConfigOnSdcard.getAppConfig(ac).set(
								Keys.LAST_CACHE_HOTEL_UPDATE_TIME, reqTime);

						Log.d(TAG,
								"======getCityPageInsertDb====== ok save updateTime="
										+ reqTime);
					}

				} catch (Exception e) {
					e.printStackTrace();
					AppConfigOnSdcard.getAppConfig(ac).set(
							Keys.LAST_CACHE_HOTEL_UPDATE_TIME, lastUpdateTime);
				}
			};
		}.start();
	}

	/**
	 * 同步酒店列表，进行本地缓存 ， 在应用启动时调用调用一次
	 * 
	 * @param dbHelp
	 */
	public void getHotelPageInsertDb(final MyDbHelper dbHelp,
			final String cityId) {
		if (isCacheingHotelData) {
			return;
		}

		if (!ac.isNetworkConnected()) {
			AppContext.toastShow(R.string.pleaseCheckNet);
			return;
		}

		new Thread() {
			public void run() {
				isCacheingHotelData = true;

				// 获取上次更新时间
				String lastUpdateTime = dbHelp.queryHotelLastUpdateTime(cityId);
				if (StringUtils.isEmpty(lastUpdateTime)) {
					// 如果这个城市下酒店的上次更新时间为空，则默认为当前版本数据导入时间
					lastUpdateTime = MyDbHelper.DATE_IMPORT_TIME;
				}

				try {
					String reqTime = DateUtil.getDate4Zdf();

					ItemPage q = ac.getHotelUpdateData(cityId, lastUpdateTime);

					if (q.rspMsg.OK() && q.list.size() > 0) {
						// 更新酒店数据
						dbHelp.insertHotelData(q.list);
						// 记录更新时间
						dbHelp.updateHotelLastUpdateTime(cityId, reqTime);

						Log.d(TAG,
								"======getHotelPageInsertDb====== ok save updateTime="
										+ reqTime);
					}

					isCacheingHotelData = false;
				} catch (Exception e) {
					isCacheingHotelData = false;
					e.printStackTrace();

					dbHelp.updateHotelLastUpdateTime(cityId, lastUpdateTime);
				} finally {
					isCacheingHotelData = false;
				}
			};
		}.start();
	}

	/**
	 * @param hotelIds
	 *            以逗号相连的酒店id，2251,2317,2324,2173,2192,2298,2214,2243,2322
	 * @param saleType
	 * @return
	 * @throws AppException
	 */
	public ItemPage getHotelsPrice(String hotelIds) throws AppException {
		return ApiClient.getHotelsPrice(hotelIds);
	}

//	public RspMsg orderPaySuccess(String hotelsId, String booksId,
//			String paySucDate, String priceOrder) throws AppException {
//		return ApiClient.orderPaySuccess(hotelsId, booksId, paySucDate,
//				priceOrder);
//	}

//	public RspMsg feedBack(String userId, String versionId, String userPhon,
//			String feedbackStatus, String phonimei, String feedbackContent,
//			String phonePlatform) throws AppException {
//		return ApiClient.feedBack(userId, versionId, userPhon, feedbackStatus,
//				phonimei, feedbackContent, phonePlatform);
//	}

	public RspMsg updateUsername(String userId, String username)
			throws AppException {
		return ApiClient.updateUsername(userId, username);
	}

	public RspMsg updateUserPhone(String userId, String username)
			throws AppException {
		return ApiClient.updateUserPhone(userId, username);
	}

	public ServerConfig getServerConfig(String phonePlatform)
			throws AppException {
		return ApiClient.getServerConfig(phonePlatform);
	}

	public RspMsg getVerifyCode(String phone, String type) throws AppException {
		return ApiClient.getVerifyCode(phone, type);
	}

//	public RspMsg getVerifycode4Register(String phone) throws AppException {
//		return ApiClient.getVerifycode4Register(phone);
//	}

	/**
	 * @return 当前定位位置；如果没有则返回上次定位位置，若还没有则返回北京位置（并发起定位请求）
	 */
	public LatLng getMyLocation() {
		LatLng locData = new LatLng(Constants.BEIJING.latitude,
				Constants.BEIJING.longitude);

		if (ac.myLocationBD != null) {
			// 当定定位位置
			locData = new LatLng(ac.myLocationBD.getLatitude(),
					ac.myLocationBD.getLongitude());

		} else {
			AppConfig appConfig = AppConfig.getAppConfig(ac);
			String lastLat = appConfig.get(Keys.LAST_LATITUDE);
			String lastLng = appConfig.get(Keys.LAST_LONGITUDE);

			// 没有定位数据则取上一次位置
			if (lastLat != null && lastLng != null) {
				locData = new LatLng(StringUtils.toDouble(lastLat),
						StringUtils.toDouble(lastLng));
			}

			// 发起定位
			ac.startLocBD();
		}
		return locData;
	}

	public ItemPage queryHotelPageInCityByKeyword(MyDbHelper dbHelp,
			double latitude, double longitude, String keyword, String cityId) {

		ItemPage cacheData = dbHelp.queryHotelPageInCityByKeyword(latitude,
				longitude, keyword, cityId);

		return cacheData;
	}

//	public ItemPage getGiftPage() throws AppException {
//		return ApiClient.getGiftPage();
//	}

//	public Item getGiftDetail(String giftId) throws AppException {
//		return ApiClient.getGiftDetail(giftId);
//	}

//	public RspMsg updateUser(String userId, String realName, String email,
//			String phone) throws AppException {
//		return ApiClient.updateUser(userId, realName, email, phone);
//	}

	public RspMsg resetPsw(String phone, String psw) throws AppException {
		return ApiClient.resetPsw(phone, psw);
	}

	public RspMsg baseDataCollect() throws AppException {
		String channelName = PhoneUtil.getChannel(ac);
		String versionCode = "" + PhoneUtil.getVersionCode(ac);
		String versionName = PhoneUtil.getVersionName(ac);
		String phonePlatform = Constant.PLATFORM_ANDROID;
		String brand = Build.BRAND;
		String model = Build.MODEL;
		String imei = PhoneUtil.getImei(ac);
		String imsi = PhoneUtil.getImsi(ac);
		String macAddress = PhoneUtil.getMacAddress(ac);

		return ApiClient.baseDataCollect(channelName, versionCode, versionName,
				phonePlatform, brand, model, imei, imsi, macAddress);
	}

	public Item balancePay(String userId, String booksId, float useBalance,
			String payPsw, String hotelsId, int sellType, String couponId)
			throws AppException {
		return ApiClient.balancePay(userId, booksId, useBalance, payPsw,
				hotelsId, sellType, couponId);
	}

	public Recharge submitRecharge(String rechargeMoney, String userId,
			String payPlatform) throws AppException {
		return ApiClient.submitRecharge(rechargeMoney, userId, payPlatform);
	}

	public Recharge queryRechargeBalance(String userId, String rechargeId)
			throws AppException {
		return ApiClient.queryRechargeBalance(userId, rechargeId);
	}

//	public RspMsg checkeIsTuan(String saleId, int saleType) throws AppException {
//		return ApiClient.checkeIsTuan(saleId, saleType);
//	}

	public RspMsg getUppayTn(String booksId, String mPayType, String payMoney,
			String couponId) throws AppException {
		return ApiClient.getUppayTn(booksId, mPayType, payMoney, couponId);
	}

	public void destroyValue() {
		ac.lastReqLatitude = 0;
		ac.lastReqLongitude = 0;
		ac.isSearch = false;
		ac.isJustSearch = false;
		ac.searchKeyword = null;
		ac.isSelectOneHotel = false;
	}

	public RspMsg updateEmail(String userId, String email) throws AppException {
		return ApiClient.updateEmail(userId, email);
	}

	public RspMsg updateRealName(String userId, String realName)
			throws AppException {
		return ApiClient.updateRealName(userId, realName);
	}

	public Item cancelOrder(String userId, String orderId) throws AppException {
		return ApiClient.cancelOrder(userId, orderId);
	}

	public ItemPage getCouponPage(String userId, int pageNum, int couponUseState)
			throws AppException {
		return ApiClient.getCouponPage(userId, pageNum, AppContext.PAGE_SIZE,
				couponUseState);
	}

	public RspMsg addCoupon(String userId, String couponKey)
			throws AppException {
		return ApiClient.addCoupon(userId, couponKey);
	}

	public RspMsg checkCoupon4Order(String couponId, String booksId)
			throws AppException {
		return ApiClient.checkCoupon4Order(couponId, booksId);
	}

	public Recharge queryBalance(String userId) throws AppException {
		return ApiClient.queryBalance(userId);
	}

	public void queryBalanceAfterPay(String id) {
		new Thread() {
			public void run() {
				try {
					Recharge r = ac.queryBalance(ac.getLoginUserId());

					if (r.rspMsg.OK()) {
						ac.user.balance = StringUtils.toFloat(r.balance);
					}
				} catch (AppException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	/**
	 * @return 当前登录的用户id，如果用户为空则返回null
	 */
	public String getLoginUserId() {
		if (ac.isLogin()) {
			return ac.user.getUserId();
		} else {
			// TODO 静默登录
			return null;
		}
	}

}