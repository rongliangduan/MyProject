package cn.op.zdf.dao;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.op.common.domain.RspMsg;
import cn.op.common.util.FileUtils;
import cn.op.common.util.LatLngUtil;
import cn.op.common.util.Log;
import cn.op.common.util.RoundTool;
import cn.op.common.util.StringUtils;
import cn.op.zdf.AppContext;
import cn.op.zdf.HotelUtil;
import cn.op.zdf.domain.City;
import cn.op.zdf.domain.CityPage;
import cn.op.zdf.domain.Item;
import cn.op.zdf.domain.ItemPage;
import cn.op.zdf.domain.Room;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * 具体参考 https://github.com/jgilfelt/android-sqlite-asset-helper <br>
 * TODO 解耦数据库层与业务层
 * 
 * @author lufei
 * 
 */
public class MyDbHelper extends SQLiteAssetHelper {

	private static final String TAG = Log.makeLogTag(MyDbHelper.class);

	private static MyDbHelper instance;

	private static final String DATABASE_NAME = "zdf";

	/**
	 * 数据库版本，与/assets/databases/zdf_upgrade_21-22.sql 后一个数字一致
	 */
	private static final int DATABASE_VERSION = 30;
	/**
	 * 当前版本酒店数据导入时间
	 */
	public static final String DATE_IMPORT_TIME = "20140925090000";

	public static final String COLUMN_ADDR = "addr";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_HOTEL_ID = "hotelId";
	public static final String COLUMN_LAT = "lat";
	public static final String COLUMN_LNG = "lng";
	public static final String COLUMN_CITY_ID = "cityId";
	public static final String COLUMN_BRAND_NAME = "brandName";
	public static final String COLUMN_LOGO = "logoPath";
	public static final String COLUMN_BRAND_ID = "brandId";
	public static final String COLUMN_TEL = "tel";

	public static MyDbHelper getInstance(Context context) {
		if (instance == null) {

			// if (FileUtils.checkSaveLocationExists()) {
			// File dirOnExtStore = FileUtils
			// .getDirOnExtStore("/zdf/databases");
			// instance = new MyDbHelper(context,
			// dirOnExtStore.getAbsolutePath());
			// } else {
			// instance = new MyDbHelper(context);
			// }

			instance = new MyDbHelper(context);

			// 版本变化，强制更新数据库，会用assets下的数据库替换原有数据库
			instance.setForcedUpgradeVersion(DATABASE_VERSION);
		}

		return instance;
	}

	/**
	 * database location : context.getApplicationInfo().dataDir + "/databases
	 * 
	 * @param context
	 */
	private MyDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		// you can use an alternate constructor to specify a database location
		// (such as a folder on the sd card)
		// you must ensure that this folder is available and you have permission
		// to write to it

		// super(context, DATABASE_NAME, context.getExternalFilesDir(null)
		// .getAbsolutePath(), null, DATABASE_VERSION);
	}

	private MyDbHelper(Context context, String storageDirectory) {

		// you can use an alternate constructor to specify a database location
		// (such as a folder on the sd card)
		// you must ensure that this folder is available and you have permission
		// to write to it

		super(context, DATABASE_NAME, storageDirectory, null, DATABASE_VERSION);
	}

	public void insertHotelData(List<Item> list) throws Exception {

		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			int size = list.size();
			Log.d(TAG, "======insertHotelData====== pre data size: " + size);
			long millis1 = System.currentTimeMillis();

			int countDelete = 0;
			int countInsert = 0;
			int countUpdate = 0;
			for (int i = 0; i < size; i++) {
				Item item = list.get(i);

				if (Item.HOTEL_UPDATE_TYPE_DELETE.equals(item.operateType)
						|| Item.HOTEL_UPDATE_TYPE_UNCHECK
								.equals(item.operateType)
						|| Item.HOTEL_UPDATE_TYPE_UNUSE
								.equals(item.operateType)) {
					db.delete("hotel", "hotelId=?",
							new String[] { item.hotelsId });
					countDelete++;
				} else if (Item.HOTEL_UPDATE_TYPE_ADD.equals(item.operateType)
						|| Item.HOTEL_UPDATE_TYPE_UPDATE
								.equals(item.operateType)
						|| Item.HOTEL_UPDATE_TYPE_CHECK
								.equals(item.operateType)
						|| Item.HOTEL_UPDATE_TYPE_USE.equals(item.operateType)) {

					if (StringUtils.isEmpty(item.hotelsId)) {
						continue;
					}

					ContentValues values = new ContentValues();
					values.put("hotelId", item.hotelsId);

					if (!StringUtils.isEmpty(item.hotelsName)) {
						values.put("name", item.hotelsName);
					}
					if (!StringUtils.isEmpty(item.cityId)) {
						values.put("cityId", item.cityId);
					}
					if (!StringUtils.isEmpty(item.hotelsAddr)) {
						values.put("addr", item.hotelsAddr);
					}
					if (!StringUtils.isEmpty(item.hotelsTel)) {
						values.put("tel", item.hotelsTel);
					}
					if (!StringUtils.isEmpty(item.brandId)) {
						values.put("brandId", item.brandId);
					}
					if (!StringUtils.isEmpty(item.brandName)) {
						values.put("brandName", item.brandName);
					}
					if (0 != item.hotelsLatitude) {
						values.put("lat", item.hotelsLatitude);
					}
					if (0 != item.hotelsLongitude) {
						values.put("lng", item.hotelsLongitude);
					}
					if (!StringUtils.isEmpty(item.logopath)) {
						values.put("logoPath", item.logopath);
					}
					if (!StringUtils.isEmpty(item.facilitysIds)) {
						values.put("facilitysIds", item.facilitysIds);
					}

					// 对于新增和更新类型，需要查询数据库中是否存在;
					// 不存在则插入；已存在的进行更新操作，以免覆盖价格等字段

					if (checkHotelExist(item.hotelsId)) {
						db.update("hotel", values, "hotelId=?",
								new String[] { item.hotelsId });
						countUpdate++;
					} else {
						db.insert("hotel", null, values);
						countInsert++;
					}
				}
			}
			db.setTransactionSuccessful();

			long millis2 = System.currentTimeMillis();
			Log.d(TAG, "======insertHotelData====== time: "
					+ (millis2 - millis1));
			Log.d(TAG, "======insertHotelData====== size: " + ",countDelete="
					+ countDelete + ",countInsert=" + countInsert
					+ ",countUpdate=" + countUpdate);
		} catch (Exception e) {
			throw e;
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 获取此经纬度周围2km内最近的最多10家酒店
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public ItemPage queryHotelPage2KmByLatLng(double latitude, double longitude) {
		long millis1 = System.currentTimeMillis();

		double point = LatLngUtil.POINT_5_KM;

		String sql = " SELECT " + SELECT_HOTEL_SQL + " FROM " + "hotel"
				+ " WHERE " + "(lat1<=" + point + ")" + " AND " + "(lng1<="
				+ point + ") ";

		if (Room.isSellWyf()) {
			sql += " AND priceWyf IS NOT null  AND priceWyf !='0'";
		}

		if (Room.isSellLsf()) {
			sql += " AND priceLdf IS NOT null AND priceLdf !='0'";
		}

		sql += " ORDER BY " + "lat1 + lng1" + " ASC " + " LIMIT " + PAGE_SIZE;

		Log.d(TAG, "======queryHotelPage2KmByLatLng====== sql=" + sql
				+ " ;args: lat=" + latitude + ",lng=" + longitude);

		String[] selectionArgs = new String[] { "" + latitude, "" + longitude };
		ItemPage itemPage = queryHotelPage(sql, selectionArgs, latitude,
				longitude);

		long millis2 = System.currentTimeMillis();
		Log.d(TAG, "======queryHotelPage2KmByLatLng====== time: "
				+ (millis2 - millis1) + " ,size: " + itemPage.list.size());

		return itemPage;
	}

	/**
	 * 获取此经纬度周围20km内最近的最多10家酒店
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public ItemPage queryHotelPage20KmByLatLng(double latitude,
			double longitude, int pageNum) {
		long millis1 = System.currentTimeMillis();

		double point = LatLngUtil.POINT_20_KM;
		int offset = pageNum * PAGE_SIZE;

		String sql = " SELECT " + SELECT_HOTEL_SQL + " FROM " + "hotel"
				+ " WHERE " + "(lat1<=" + point + ")" + " AND " + "(lng1<="
				+ point + ") ";

		if (Room.isSellWyf()) {
			sql += " AND priceWyf IS NOT null  AND priceWyf !='0'";
		}

		if (Room.isSellLsf()) {
			sql += " AND priceLdf IS NOT null AND priceLdf !='0'";
		}

		sql += " ORDER BY " + "lat1 + lng1" + " ASC " + " LIMIT " + PAGE_SIZE
				+ " OFFSET " + offset;

		Log.d(TAG, "======queryHotelPage20KmByLatLng====== sql=" + sql
				+ " ;args: lat=" + latitude + ",lng=" + longitude);

		String[] selectionArgs = new String[] { "" + latitude, "" + longitude };
		ItemPage itemPage = queryHotelPage(sql, selectionArgs, latitude,
				longitude);

		long millis2 = System.currentTimeMillis();
		Log.d(TAG, "======queryHotelPage20KmByLatLng====== time: "
				+ (millis2 - millis1) + " ,size: " + itemPage.list.size());

		return itemPage;
	}

	/**
	 * 获取此经纬度周围20km内最近的最多10家酒店
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public ItemPage queryHotelPage20KmByLatLng(double latitude, double longitude) {

		return queryHotelPage20KmByLatLng(latitude, longitude, 0);
	}

	/**
	 * 关键字 搜索 限定城市内 距 指定位置最近 的前20条记录
	 * 
	 * @param latitude
	 * @param longitude
	 * @param keyword
	 * @param cityId
	 * @return
	 */
	public ItemPage queryHotelPageInCityByKeyword(double latitude,
			double longitude, String keyword, String cityId) {
		return queryHotelPageInCityByKeyword(latitude, longitude, keyword,
				cityId, 0);
	}

	/**
	 * 查询酒店信息的sql字段
	 */
	private static final String SELECT_HOTEL_SQL = "hotelId, ABS(lat-?) AS lat1, ABS(lng-?) AS lng1, name, addr, brandName, priceZdf, lat, lng, logoPath,brandId,tel,hours,hasTuan,priceWyf,priceLdf,facilitysIds ";

	private static final int PAGE_SIZE = 10;

	private ItemPage queryHotelPage(String sql, String[] selectionArgs,
			double latitude, double longitude) {
		ItemPage itemPage = new ItemPage();

		Cursor cursor = null;
		try {
			SQLiteDatabase db = getReadableDatabase();
			cursor = db.rawQuery(sql, selectionArgs);

			while (cursor.moveToNext()) {
				Item item = new Item();
				item.hotelsId = cursor.getString(0);
				item.hotelsName = cursor.getString(3);
				item.hotelsAddr = cursor.getString(4);
				item.brandName = cursor.getString(5);
				item.hourroomPrice = cursor.getString(6);
				item.hotelsLatitude = cursor.getDouble(7);
				item.hotelsLongitude = cursor.getDouble(8);
				item.logopath = cursor.getString(9);
				item.brandId = cursor.getString(10);
				item.hotelsTel = cursor.getString(11);
				item.hours = cursor.getString(12);
				item.hasTuan = cursor.getInt(13) > 0;
				item.dayroomPrice = cursor.getString(14);
				item.nightroomPrice = cursor.getString(15);
				item.facilitysIds = cursor.getString(16);

				// 计算当前定位位置与酒店的距离
				// AppContext ac = AppContext.getAc();
				// LocationData myLocation = ac.getMyLocation();
				//
				// double distance = LatLngUtil.getDistance(myLocation.latitude,
				// myLocation.longitude, item.hotelsLatitude,
				// item.hotelsLongitude) / 1000;

				// 计算当前查询位置与酒店的距离
				double distance = LatLngUtil.getDistance(latitude, longitude,
						item.hotelsLatitude, item.hotelsLongitude) / 1000;

				// 距离精度
				distance = RoundTool.round(distance, 1,
						BigDecimal.ROUND_HALF_UP);

				item.dist = "" + distance;

				itemPage.list.add(item);
			}

			// TODO 距离排序，直接从数据库查处的顺序并不是准确的距离远近顺序
			// HotelUtil.listSortBy(Item.ORDER_BY_DISTANCE, itemPage.list,
			// Room.SALE_TYPE_ZDF);

			itemPage.rspMsg.code = RspMsg.CODE_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return itemPage;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return itemPage;
	}

	/**
	 * 更新酒店价格
	 * 
	 * @param list
	 */
	public void updateHotelPrice(List<Item> list) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			int size = list.size();
			Log.d(TAG, "======updateHotelPrice====== data size: " + size);
			long millis1 = System.currentTimeMillis();

			for (int i = 0; i < size; i++) {
				Item item = list.get(i);

				ContentValues values = new ContentValues();

				if (!StringUtils.isEmpty(item.hours)) {
					values.put("hours", item.hours);
				}
				if (!StringUtils.isEmpty(item.hourroomPrice)) {
					values.put("priceZdf", item.hourroomPrice);
				}
				values.put("hasTuan", item.hasTuan);
				if (!StringUtils.isEmpty(item.dayroomPrice)) {
					values.put("priceWyf", item.dayroomPrice);
				}
				if (!StringUtils.isEmpty(item.daySalePrice)) {
					values.put("priceOriginalWyf", item.daySalePrice);
				}
				if (!StringUtils.isEmpty(item.nightroomPrice)) {
					values.put("priceLdf", item.nightroomPrice);
				}

				db.update("hotel", values, "hotelId=?",
						new String[] { item.hotelsId });
			}
			db.setTransactionSuccessful();

			long millis2 = System.currentTimeMillis();
			Log.d(TAG, "======updateHotelPrice====== time: "
					+ (millis2 - millis1));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	public void insertCityData(List<City> list) throws Exception {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			int size = list.size();
			Log.d(TAG, "======insertCityData====== data size: " + size);
			long millis1 = System.currentTimeMillis();

			int countDelete = 0;
			int countInsert = 0;
			int countUpdate = 0;
			for (int i = 0; i < size; i++) {
				City item = list.get(i);

				if (StringUtils.isEmpty(item.cityId)) {
					continue;
				}

				if (Item.HOTEL_UPDATE_TYPE_DELETE.equals(item.updateType)
						|| Item.HOTEL_UPDATE_TYPE_UNCHECK
								.equals(item.updateType)
						|| Item.HOTEL_UPDATE_TYPE_UNUSE.equals(item.updateType)) {

					db.delete("city", "cityId=?", new String[] { item.cityId });
					countDelete++;

				} else if (Item.HOTEL_UPDATE_TYPE_ADD.equals(item.updateType)
						|| Item.HOTEL_UPDATE_TYPE_UPDATE
								.equals(item.updateType)
						|| Item.HOTEL_UPDATE_TYPE_CHECK.equals(item.updateType)
						|| Item.HOTEL_UPDATE_TYPE_USE.equals(item.updateType)) {

					// 对于新增和更新类型，需要查询数据库中是否存在;
					// 不存在则插入；已存在的进行更新操作，以免覆盖lastUpdateTime字段
					boolean isExist = checkCityExist(item.cityId);

					ContentValues values = new ContentValues();
					values.put("cityId", item.cityId);
					values.put("cityName", item.cityName);
					values.put("cityNamePy", item.cityNamePy);
					values.put("cityLatitude", item.cityLat);
					values.put("cityLongitude", item.cityLng);

					if (isExist) {
						db.update("city", values, "cityId=?",
								new String[] { item.cityId });
						countUpdate++;
					} else {
						db.insert("city", null, values);
						countInsert++;
					}

				}
			}

			db.setTransactionSuccessful();

			long millis2 = System.currentTimeMillis();
			Log.d(TAG, "======insertCityData====== time: "
					+ (millis2 - millis1));
			Log.d(TAG, "======insertCityData====== size: " + ",countDelete="
					+ countDelete + " ,countInsert=" + countInsert
					+ " ,countUpdate=" + countUpdate);

		} catch (Exception e) {
			throw e;
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 检查城市是否已存在
	 * 
	 * @param cityId
	 * @return true if exist
	 */
	private boolean checkCityExist(String cityId) {
		boolean isExist = false;

		Cursor cursor = null;
		try {
			SQLiteDatabase db = getReadableDatabase();
			String sql = " SELECT " + "cityId" + " FROM " + "city" + " WHERE "
					+ "cityId=" + "'" + cityId + "'";
			cursor = db.rawQuery(sql, new String[] {});

			Log.d(TAG, "======checkCityExist====== sql=" + sql);

			while (cursor.moveToNext()) {
				isExist = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		Log.d(TAG, "======checkCityExist====== isExist=" + isExist);

		return isExist;
	}

	/**
	 * 检查酒店是否已存在
	 * 
	 * @param hotelId
	 * @return true if exist
	 */
	private boolean checkHotelExist(String hotelId) {
		boolean isExist = false;

		Cursor cursor = null;
		try {
			SQLiteDatabase db = getReadableDatabase();
			String sql = " SELECT " + "hotelId" + " FROM " + "hotel"
					+ " WHERE " + "hotelId=" + "'" + hotelId + "'";
			cursor = db.rawQuery(sql, new String[] {});

			Log.d(TAG, "======checkHotelExist====== sql=" + sql);

			while (cursor.moveToNext()) {
				isExist = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		Log.d(TAG, "======checkHotelExist====== isExist=" + isExist);

		return isExist;
	}

	public CityPage queryCityPage() {
		Log.d(TAG, "======queryCityPage======");
		long millis1 = System.currentTimeMillis();

		CityPage itemPage = new CityPage();

		Cursor cursor = null;
		try {
			SQLiteDatabase db = getReadableDatabase();
			String sql = " SELECT "
					+ "cityId, cityName, cityNamePy, cityLatitude, cityLongitude "
					+ " FROM " + "city" + " ORDER BY " + "cityNamePy" + " ASC ";
			cursor = db.rawQuery(sql, new String[] {});
			Log.d(TAG, "======queryCityPage====== sql=" + sql);
			while (cursor.moveToNext()) {
				City item = new City();
				item.cityId = cursor.getString(0);
				item.cityName = cursor.getString(1);
				item.cityNamePy = cursor.getString(2);
				item.cityLat = cursor.getDouble(3);
				item.cityLng = cursor.getDouble(4);

				itemPage.list.add(item);
			}

			// cursor.close();

			itemPage.rspMsg.code = RspMsg.CODE_SUCCESS;
			long millis2 = System.currentTimeMillis();
			Log.d(TAG, "======queryCityPage====== time: " + (millis2 - millis1)
					+ "size: " + itemPage.list.size());

		} catch (Exception e) {
			e.printStackTrace();
			return itemPage;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return itemPage;
	}

	/**
	 * 根据城市名查询城市id
	 * 
	 * @param cityName
	 * @return cityId or null
	 */
	public String queryCityIdByCityName(String cityName) {
		String cityId = null;

		Cursor cursor = null;
		try {
			long millis1 = System.currentTimeMillis();

			SQLiteDatabase db = getReadableDatabase();
			String sql = " SELECT " + "cityId" + " FROM " + "city" + " WHERE "
					+ "cityName=" + "'" + cityName + "'";
			cursor = db.rawQuery(sql, new String[] {});

			Log.d(TAG, "======queryCityIdByCityName====== sql=" + sql);

			cityId = null;
			while (cursor.moveToNext()) {
				cityId = cursor.getString(0);
			}
			// cursor.close();

			long millis2 = System.currentTimeMillis();
			Log.d(TAG, "======queryCityIdByCityName====== time: "
					+ (millis2 - millis1));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return cityId;
	}

	/**
	 * 查询指定城市下酒店数据的最后更新时间
	 * 
	 * @param cityId
	 * @return 最后更新时间，格式 yyyyMMddhhmmss
	 */
	public String queryHotelLastUpdateTime(String cityId) {
		Cursor cursor = null;
		String lastUpdateTime = null;
		try {

			SQLiteDatabase db = getReadableDatabase();
			String sql = " SELECT " + "lastUpdateTime" + " FROM " + "city"
					+ " WHERE " + "cityId=" + "'" + cityId + "'";
			cursor = db.rawQuery(sql, new String[] {});

			Log.d(TAG, "======queryHotelLastUpdateTime====== sql=" + sql);

			while (cursor.moveToNext()) {
				lastUpdateTime = cursor.getString(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return lastUpdateTime;
	}

	/**
	 * 更新指定城市下酒店数据的最后更新时间
	 * 
	 * @param cityId
	 * @param lastUpdateTime
	 */
	public void updateHotelLastUpdateTime(String cityId, String lastUpdateTime) {

		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put("lastUpdateTime", lastUpdateTime);

			db.update("city", values, "cityId=?", new String[] { cityId });
			db.setTransactionSuccessful();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	public String querBrandName(String brandId) {
		String brandName = null;

		Cursor cursor = null;
		try {
			long millis1 = System.currentTimeMillis();

			SQLiteDatabase db = getReadableDatabase();
			String sql = " SELECT " + "brandName" + " FROM " + "hotel"
					+ " WHERE " + "brandId=" + "'" + brandId + "'" + " LIMIT "
					+ 1;
			cursor = db.rawQuery(sql, new String[] {});

			Log.d(TAG, "======querBrandName====== sql=" + sql);

			brandName = null;
			while (cursor.moveToNext()) {
				brandName = cursor.getString(0);
			}

			long millis2 = System.currentTimeMillis();
			Log.d(TAG, "======querBrandName====== time: " + (millis2 - millis1));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return brandName;
	}

	/**
	 * 分页：关键字 搜索 限定城市内 距指定位置最近 的 某一条之后 的20条记录(分页查询)
	 * 
	 * @param latitude
	 * @param longitude
	 * @param keyword
	 * @param cityId
	 * @param pageNum
	 * @return
	 */
	public ItemPage queryHotelPageInCityByKeyword(double latitude,
			double longitude, String keyword, String cityId, int pageNum) {
		long millis1 = System.currentTimeMillis();

		int offset = pageNum * PAGE_SIZE;
		String sql = " SELECT " + SELECT_HOTEL_SQL + " FROM " + "hotel"
				+ " WHERE " + "cityId=" + cityId + " AND " + "(name LIKE '%"
				+ keyword + "%' OR addr LIKE '%" + keyword
				+ "%' OR brandName LIKE '%" + keyword + "%')";

		if (Room.isSellWyf()) {
			sql += " AND priceWyf IS NOT null  AND priceWyf !='0'";
		}

		if (Room.isSellLsf()) {
			sql += " AND priceLdf IS NOT null AND priceLdf !='0'";
		}

		sql += " ORDER BY " + "lat1 + lng1" + " ASC " + " LIMIT " + PAGE_SIZE
				+ " OFFSET " + offset;

		String[] selectionArgs = new String[] { "" + latitude, "" + longitude };

		Log.d(TAG, "======queryHotelPageInCityByKeyword====== sql=" + sql
				+ " ;args: lat=" + latitude + ",lng=" + longitude + " ,cityId="
				+ cityId);

		ItemPage itemPage = queryHotelPage(sql, selectionArgs, latitude,
				longitude);

		long millis2 = System.currentTimeMillis();
		Log.d(TAG, "======queryHotelPageInCityByKeyword====== time: "
				+ (millis2 - millis1) + "size: " + itemPage.list.size());

		return itemPage;
	}

	/**
	 * @param latitude
	 * @param longitude
	 * @param keyword
	 * @param cityId
	 * @param pageNum
	 * @return
	 */
	public ItemPage queryHotelPage20KmByKeyword(double latitude,
			double longitude, String keyword, String cityId, int pageNum) {
		long millis1 = System.currentTimeMillis();

		double point = LatLngUtil.POINT_20_KM;
		int offset = pageNum * PAGE_SIZE;

		String sql = " SELECT " + SELECT_HOTEL_SQL + " FROM " + "hotel"
				+ " WHERE " + "(lat1<=" + point + ")" + " AND " + "(lng1<="
				+ point + ") " + " AND " + "(name LIKE '%" + keyword
				+ "%' OR addr LIKE '%" + keyword + "%' OR brandName LIKE '%"
				+ keyword + "%')";

		if (Room.isSellWyf()) {
			sql += " AND priceWyf IS NOT null  AND priceWyf !='0'";
		}

		if (Room.isSellLsf()) {
			sql += " AND priceLdf IS NOT null AND priceLdf !='0'";
		}

		sql += " ORDER BY " + "lat1 + lng1" + " ASC " + " LIMIT " + PAGE_SIZE
				+ " OFFSET " + offset;

		String[] selectionArgs = new String[] { "" + latitude, "" + longitude };

		Log.d(TAG, "======queryHotelPage20KmByKeyword====== sql=" + sql
				+ " ;args: lat=" + latitude + ",lng=" + longitude + " ,cityId="
				+ cityId);

		ItemPage itemPage = queryHotelPage(sql, selectionArgs, latitude,
				longitude);

		long millis2 = System.currentTimeMillis();
		Log.d(TAG, "======queryHotelPage20KmByKeyword====== time: "
				+ (millis2 - millis1) + "size: " + itemPage.list.size());

		return itemPage;
	}

	/**
	 * 获取当前城市此经纬度周围最近的最多10家酒店
	 * 
	 * @param latitude
	 * @param longitude
	 * @param cityId
	 * @return
	 */
	public ItemPage queryHotelPageInCityByLatLng(double latitude,
			double longitude, String cityId) {
		return queryHotelPageInCityByLatLng(latitude, longitude, cityId, 0);
	}

	/**
	 * 获取当前城市此经纬度周围最近的最多10家酒店(分页查询)
	 * 
	 * @param latitude
	 * @param longitude
	 * @param cityId
	 * @param pageNum
	 * @return
	 */
	public ItemPage queryHotelPageInCityByLatLng(double latitude,
			double longitude, String cityId, int pageNum) {
		long millis1 = System.currentTimeMillis();

		int offset = pageNum * PAGE_SIZE;
		String sql = " SELECT " + SELECT_HOTEL_SQL + " FROM " + "hotel"
				+ " WHERE " + "cityId=" + cityId;

		if (Room.isSellWyf()) {
			sql += " AND priceWyf IS NOT null  AND priceWyf !='0'";
		}

		if (Room.isSellLsf()) {
			sql += " AND priceLdf IS NOT null AND priceLdf !='0'";
		}

		sql += " ORDER BY " + "lat1 + lng1" + " ASC " + " LIMIT " + PAGE_SIZE
				+ " OFFSET " + offset;

		String[] selectionArgs = new String[] { "" + latitude, "" + longitude };

		Log.d(TAG, "======queryHotelPageInCityByLatLng====== sql=" + sql
				+ " ;args: lat=" + latitude + ",lng=" + longitude + " ,cityId="
				+ cityId);

		ItemPage itemPage = queryHotelPage(sql, selectionArgs, latitude,
				longitude);

		long millis2 = System.currentTimeMillis();
		Log.d(TAG, "======queryHotelPageInCityByLatLng====== time: "
				+ (millis2 - millis1) + "size: " + itemPage.list.size());

		return itemPage;
	}

	public Item queryHotelById(String hotelId, double latitude, double longitude) {
		long millis1 = System.currentTimeMillis();

		String sql = " SELECT " + SELECT_HOTEL_SQL + " FROM " + "hotel"
				+ " WHERE " + "hotelId=" + hotelId;

		String[] selectionArgs = new String[] { "" + latitude, "" + longitude };

		Log.d(TAG, "======queryHotelById====== sql=" + sql + " ;args: lat="
				+ latitude + ",lng=" + longitude);

		ItemPage itemPage = queryHotelPage(sql, selectionArgs, latitude,
				longitude);

		long millis2 = System.currentTimeMillis();
		Log.d(TAG, "======queryHotelById====== time: " + (millis2 - millis1)
				+ "size: " + itemPage.list.size());

		Item hotel = null;
		if (itemPage.list.size() > 0) {
			hotel = itemPage.list.get(0);
		}

		return hotel;
	}

}
