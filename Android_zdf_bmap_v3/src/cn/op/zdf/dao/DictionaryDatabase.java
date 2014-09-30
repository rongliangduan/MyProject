/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.op.zdf.dao;

import java.util.Arrays;
import java.util.HashMap;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;
import cn.op.zdf.AppContext;
import cn.op.zdf.domain.Room;

import com.baidu.mapapi.model.LatLng;

/**
 * Contains logic to return specific words from the dictionary, and load the
 * dictionary table when it needs to be created.
 */
public class DictionaryDatabase {
	private static final String TAG = "DictionaryDatabase";

	// The columns we'll include in the dictionary table
	public static final String KEY_WORD = SearchManager.SUGGEST_COLUMN_TEXT_1;
	public static final String KEY_DEFINITION = SearchManager.SUGGEST_COLUMN_TEXT_2;

	// public static final String KEY_WORD = "name";
	// public static final String KEY_DEFINITION = "addr";

	private static final String DATABASE_NAME = "dictionary";
	private static final String FTS_VIRTUAL_TABLE = "FTSdictionary";
	private static final int DATABASE_VERSION = 2;

	// private final DictionaryOpenHelper mDatabaseOpenHelper;
	private final MyDbHelper mDatabaseOpenHelper;
	private static final HashMap<String, String> mColumnMap = buildColumnMap();

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The Context within which to work, used to create the DB
	 */
	public DictionaryDatabase(Context context) {
		// mDatabaseOpenHelper = new DictionaryOpenHelper(context);
		mDatabaseOpenHelper = MyDbHelper.getInstance(context);
	}

	/**
	 * Builds a map for all columns that may be requested, which will be given
	 * to the SQLiteQueryBuilder. This is a good way to define aliases for
	 * column names, but must include all columns, even if the value is the key.
	 * This allows the ContentProvider to request columns w/o the need to know
	 * real column names and create the alias itself.
	 */
	private static HashMap<String, String> buildColumnMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		// map.put(KEY_WORD, KEY_WORD);
		// map.put(KEY_DEFINITION, KEY_DEFINITION);

		map.put(KEY_WORD, MyDbHelper.COLUMN_NAME + " AS " + KEY_WORD);
		map.put(KEY_DEFINITION, MyDbHelper.COLUMN_ADDR + " AS "
				+ KEY_DEFINITION);

		map.put(MyDbHelper.COLUMN_HOTEL_ID, MyDbHelper.COLUMN_HOTEL_ID);
		map.put(MyDbHelper.COLUMN_NAME, MyDbHelper.COLUMN_NAME);
		map.put(MyDbHelper.COLUMN_ADDR, MyDbHelper.COLUMN_ADDR);
		map.put(MyDbHelper.COLUMN_LAT, MyDbHelper.COLUMN_LAT);
		map.put(MyDbHelper.COLUMN_LNG, MyDbHelper.COLUMN_LNG);
		map.put(MyDbHelper.COLUMN_BRAND_NAME, MyDbHelper.COLUMN_BRAND_NAME);
		map.put(MyDbHelper.COLUMN_LOGO, MyDbHelper.COLUMN_LOGO);
		map.put(MyDbHelper.COLUMN_BRAND_ID, MyDbHelper.COLUMN_BRAND_ID);
		map.put(MyDbHelper.COLUMN_TEL, MyDbHelper.COLUMN_TEL);

		map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS "
				+ SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS "
				+ SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
		return map;
	}

	/**
	 * Returns a Cursor positioned at the word specified by rowId
	 * 
	 * @param rowId
	 *            id of word to retrieve
	 * @param columns
	 *            The columns to include, if null then all are included
	 * @return Cursor positioned to matching word, or null if not found.
	 */
	public Cursor getWord(String rowId, String[] columns) {
		Log.d(TAG,
				"======getWord====== rowId=" + rowId + " ,columns"
						+ Arrays.toString(columns));

		String selection = "rowid = ?";
		String[] selectionArgs = new String[] { rowId };

		return query(selection, selectionArgs, columns);

		/*
		 * This builds a query that looks like: SELECT <columns> FROM <table>
		 * WHERE rowid = <rowId>
		 */
	}

	/**
	 * Returns a Cursor over all words that match the given query
	 * 
	 * @param query
	 *            The string to search for
	 * @param columns
	 *            The columns to include, if null then all are included
	 * @return Cursor over all words that match, or null if none found.
	 */
	public Cursor getWordMatches(String query, String[] columns) {

		if ("速八".equals(query)) {
			query = "速8";
		} else if ("七天".equals(query)) {
			query = "7天";
		}

		Log.d(TAG, "======getWordMatches====== query=" + query + " ,columns"
				+ Arrays.toString(columns));

		// 1.======SQLiteQueryBuilder查询 FTS3 TABLE 可以使用 MATCH 关键字
		// String selection = KEY_WORD + " MATCH ?";
		// String[] selectionArgs = new String[] { query + "*" };
		// return query(selection, selectionArgs, columns);
		/*
		 * This builds a query that looks like: SELECT <columns> FROM <table>
		 * WHERE <KEY_WORD> MATCH 'query*' which is an FTS3 search for the query
		 * text (plus a wildcard) inside the word column.
		 * 
		 * - "rowid" is the unique id for all rows but we need this value for
		 * the "_id" column in order for the Adapters to work, so the columns
		 * need to make "_id" an alias for "rowid" - "rowid" also needs to be
		 * used by the SUGGEST_COLUMN_INTENT_DATA alias in order for suggestions
		 * to carry the proper intent data. These aliases are defined in the
		 * DictionaryProvider when queries are made. - This can be revised to
		 * also search the definition text with FTS3 by changing the selection
		 * clause to use FTS_VIRTUAL_TABLE instead of KEY_WORD (to search across
		 * the entire table, but sorting the relevance could be difficult.
		 */

		// 2.======sql查询 FTS3 TABLE 可以使用 MATCH 关键字
		// String sql =
		// "SELECT rowid AS _id, suggest_text_1, suggest_text_2, rowid AS suggest_intent_data_id FROM FTSdictionary WHERE (suggest_text_1 MATCH "
		// + "?" + ")";
		// Cursor cursor =
		// mDatabaseOpenHelper.getReadableDatabase().rawQuery(sql,
		// new String[] { query + "*" });

		// 3.======sql查询 普通 的 LIKE关键字查询
		// String sql =
		// "SELECT rowid AS _id, suggest_text_1, suggest_text_2, rowid AS suggest_intent_data_id FROM FTSdictionary WHERE (suggest_text_1 LIKE '%"
		// + query + "%')";
		// Cursor cursor =
		// mDatabaseOpenHelper.getReadableDatabase().rawQuery(sql,
		// new String[] {});

		// 3.======sql查询 普通 的 LIKE关键字查询
		String sql = " SELECT " + "rowid AS _id," + MyDbHelper.COLUMN_NAME
				+ " AS suggest_text_1," + MyDbHelper.COLUMN_ADDR
				+ " AS suggest_text_2, rowid AS suggest_intent_data_id,"
				+ MyDbHelper.COLUMN_HOTEL_ID + "," + MyDbHelper.COLUMN_NAME
				+ "," + MyDbHelper.COLUMN_LAT + "," + MyDbHelper.COLUMN_LNG
				+ "," + MyDbHelper.COLUMN_ADDR + "," + MyDbHelper.COLUMN_LOGO
				+ "," + MyDbHelper.COLUMN_TEL + ","
				+ MyDbHelper.COLUMN_BRAND_ID + ","
				+ MyDbHelper.COLUMN_BRAND_NAME;

		LatLng myLocation = AppContext.getAc().getMyLocation();

		String[] selectionArgs = new String[] {};
		if (myLocation != null) {
			sql += ", ABS(lat-?) AS lat1, ABS(lng-?) AS lng1";
			selectionArgs = new String[] { "" + myLocation.latitude,
					"" + myLocation.longitude };
		}

		sql += " FROM hotel WHERE (" + MyDbHelper.COLUMN_NAME + " LIKE '%"
				+ query + "%' OR " + MyDbHelper.COLUMN_ADDR + "  LIKE '%"
				+ query + "%' OR " + MyDbHelper.COLUMN_BRAND_NAME + " LIKE '%"
				+ query + "%')";

		if (AppContext.getAc().lastChooseCity != null) {
			sql += " AND " + MyDbHelper.COLUMN_CITY_ID + "="
					+ AppContext.getAc().lastChooseCity.cityId;
		}
		
		if (Room.isSellWyf()) {
			sql += " AND priceWyf IS NOT null  AND priceWyf !='0'";
		}

		if (Room.isSellLsf()) {
			sql += " AND priceLdf IS NOT null AND priceLdf !='0'";
		}

		if (myLocation != null) {
			sql += " ORDER BY " + "lat1 + lng1" + " ASC ";
		}

		Cursor cursor = mDatabaseOpenHelper.getReadableDatabase().rawQuery(sql,
				selectionArgs);

		return cursor;

	}

	/**
	 * Performs a database query.
	 * 
	 * @param selection
	 *            The selection clause
	 * @param selectionArgs
	 *            Selection arguments for "?" components in the selection
	 * @param columns
	 *            The columns to return
	 * @return A Cursor over all rows matching the query
	 */
	private Cursor query(String selection, String[] selectionArgs,
			String[] columns) {

		Log.d(TAG, "======query====== selection=" + selection
				+ " ,selectionArgs" + Arrays.toString(selectionArgs)
				+ " ,columns" + Arrays.toString(columns));

		/*
		 * The SQLiteBuilder provides a map for all possible columns requested
		 * to actual columns in the database, creating a simple column alias
		 * mechanism by which the ContentProvider does not need to know the real
		 * column names
		 */
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		// builder.setTables(FTS_VIRTUAL_TABLE);
		// builder.setProjectionMap(mColumnMap);

		builder.setTables("hotel");
		builder.setProjectionMap(mColumnMap);

		Cursor cursor = builder.query(
				mDatabaseOpenHelper.getReadableDatabase(), columns, selection,
				selectionArgs, null, null, null);

		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		return cursor;
	}

	// /**
	// * This creates/opens the database.
	// */
	// private static class DictionaryOpenHelper extends SQLiteOpenHelper {
	//
	// private final Context mHelperContext;
	// private SQLiteDatabase mDatabase;
	//
	// /*
	// * Note that FTS3 does not support column constraints and thus, you
	// * cannot declare a primary key. However, "rowid" is automatically used
	// * as a unique identifier, so when making requests, we will use "_id" as
	// * an alias for "rowid"
	// */
	// private static final String FTS_TABLE_CREATE = "CREATE VIRTUAL TABLE "
	// + FTS_VIRTUAL_TABLE + " USING fts3 (" + KEY_WORD + ", "
	// + KEY_DEFINITION + ");";
	//
	// DictionaryOpenHelper(Context context) {
	// super(context, DATABASE_NAME, null, DATABASE_VERSION);
	// mHelperContext = context;
	// }
	//
	// @Override
	// public void onCreate(SQLiteDatabase db) {
	// mDatabase = db;
	// mDatabase.execSQL(FTS_TABLE_CREATE);
	// loadDictionary();
	// }
	//
	// /**
	// * Starts a thread to load the database table with words
	// */
	// private void loadDictionary() {
	// new Thread(new Runnable() {
	// public void run() {
	// try {
	// loadWords();
	// } catch (IOException e) {
	// throw new RuntimeException(e);
	// }
	// }
	// }).start();
	// }
	//
	// private void loadWords() throws IOException {
	// Log.d(TAG, "Loading words...");
	// final Resources resources = mHelperContext.getResources();
	// InputStream inputStream = resources
	// .openRawResource(R.raw.definitions);
	// BufferedReader reader = new BufferedReader(new InputStreamReader(
	// inputStream));
	//
	// try {
	// String line;
	// while ((line = reader.readLine()) != null) {
	// String[] strings = TextUtils.split(line, "-");
	// if (strings.length < 2)
	// continue;
	// long id = addWord(strings[0].trim(), strings[1].trim());
	// if (id < 0) {
	// Log.e(TAG, "unable to add word: " + strings[0].trim());
	// }
	// }
	// } finally {
	// reader.close();
	// }
	// Log.d(TAG, "DONE loading words.");
	// }
	//
	// /**
	// * Add a word to the dictionary.
	// *
	// * @return rowId or -1 if failed
	// */
	// public long addWord(String word, String definition) {
	// ContentValues initialValues = new ContentValues();
	// initialValues.put(KEY_WORD, word);
	// initialValues.put(KEY_DEFINITION, definition);
	//
	// return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
	// }
	//
	// @Override
	// public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	// {
	// Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
	// + newVersion + ", which will destroy all old data");
	// db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
	// onCreate(db);
	// }
	// }

}
