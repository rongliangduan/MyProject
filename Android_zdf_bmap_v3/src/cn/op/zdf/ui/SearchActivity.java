package cn.op.zdf.ui;

import java.math.BigDecimal;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.op.common.UIHelper;
import cn.op.common.constant.Keys;
import cn.op.common.util.Constants;
import cn.op.common.util.LatLngUtil;
import cn.op.common.util.Log;
import cn.op.common.util.RoundTool;
import cn.op.common.util.StringUtils;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.dao.DictionaryProvider;
import cn.op.zdf.dao.MyDbHelper;
import cn.op.zdf.domain.Item;
import cn.op.zdf.event.ExitSearchEvent;
import cn.op.zdf.event.KeyWordSearchEvent;
import cn.op.zdf.event.ReqLastMapMoveLocData;
import cn.op.zdf.event.SearchResultEvent;
import cn.op.zdf.event.SelectOneHotelEvent;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnCloseListener;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.actionbarsherlock.widget.SearchView.OnSuggestionListener;
import com.actionbarsherlock.widget.SearchView.SearchAutoComplete;
import com.meizu.smartbar.SmartBarUtils;
import com.tendcloud.tenddata.TCAgent;

import de.greenrobot.event.EventBus;

public class SearchActivity extends SherlockActivity {

	protected static final String TAG = Log.makeLogTag(SearchActivity.class);
	private SearchAutoComplete mSearchViewTextView;
	private SearchView mSearchView;
	private SearchActivity activity;
	private AppContext ac;
	protected boolean isSearchViewOpen;
	protected String searchTextChange;
	// protected String mTitle;
	private SearchAutoComplete abs__search_src_text;
	private ImageView abs__search_go_btn;
	private View pb;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		if (SmartBarUtils.hasSmartBar()) {
			getSherlock().setUiOptions(
					ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);

			setTheme(R.style.Holo_Theme_CustomAbsOverlay);

			SmartBarUtils.setActionBarViewCollapsable(getActionBar(), true);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		// Show the Up button in the action bar.

		activity = this;
		ac = AppContext.getAc();

		// ActionBar ab = getSupportActionBar();
		// ab.setDisplayShowTitleEnabled(false);

		pb = findViewById(R.id.pb);
		View btnLeft = findViewById(R.id.btnLeft);
		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UIHelper.hideSoftInput(activity, v);
				finish();
			}
		});

		if (ac.lastChooseCity != null) {
			tvTitle.setText(ac.lastChooseCity.cityName);
		} else if (ac.lastLocCity != null) {
			tvTitle.setText(ac.lastLocCity.cityName);
		} else {
			tvTitle.setText("选择城市");
		}

		mSearchView = (SearchView) findViewById(R.id.searchView1);
		initSearchView(mSearchView);
	}

	@Override
	public void finish() {
		Log.d(TAG, "======finish======");

		if (StringUtils.isEmpty(mSearchViewTextView.getText().toString())) {
			mSearchViewTextView.setText(null);
			// ac.isSearch = false;
			// ac.isSelectOneHotel = false;
			ac.searchKeyword = null;

			UIHelper.hideSoftInput(activity, mSearchViewTextView);

			setResult(Constants.RESULT_CODE_SEARCH_EXIT);
		}

		super.finish();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "======onPause======");

		super.onPause();
		TCAgent.onPause(this);
		overridePendingTransition(R.anim.slide_in_from_left,
				R.anim.slide_out_to_right);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater supportMenuInflater = getSupportMenuInflater();

		if (SmartBarUtils.hasSmartBar()) {
			UIHelper.makeEmptyMenu(supportMenuInflater, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	private void initSearchView(final SearchView searchView) {
		// 参考ActionBarSherlock-library项目的abs__search_view.xml文件

		mSearchView = searchView;

		View abs__search_plate = searchView
				.findViewById(com.actionbarsherlock.R.id.abs__search_plate);
		// abs__search_plate.setBackgroundResource(R.drawable.et_bg_click);
		// abs__search_plate.setBackgroundResource(R.drawable.et_bg1);
		abs__search_plate.setBackgroundResource(R.drawable.transparent);

		abs__search_src_text = (SearchAutoComplete) searchView
				.findViewById(com.actionbarsherlock.R.id.abs__search_src_text);

		mSearchViewTextView = abs__search_src_text;

		abs__search_src_text.setTextColor(getResources().getColor(
				R.color.black_gray));
		abs__search_src_text.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		// abs__search_src_text.setPadding(0, DisplayUtil.dip2px(activity, 10),
		// 0,
		// 0);
		abs__search_src_text.setHintTextColor(getResources().getColor(
				R.color.gray_light));
		abs__search_src_text.setHint("酒店品牌、地址");
		abs__search_src_text.setGravity(Gravity.CENTER_VERTICAL);
		abs__search_src_text
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });

		final View abs__search_close_btn = searchView
				.findViewById(com.actionbarsherlock.R.id.abs__search_close_btn);
		// abs__search_close_btn.setBackgroundResource(R.drawable.);
		
		abs__search_close_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "======abs__search_close_btn======");
				//
				// if (ac.isSearch) {
				// exitSearch();
				//
				// mSearchViewTextView.setText(null);
				// } else {
				// if (!StringUtils.isEmpty(mSearchViewTextView.getText()
				// .toString())) {
				// mSearchViewTextView.setText(null);
				// } else {
				// mSearchViewTextView.setText(null);
				// exitSearch();
				// }
				// }

				if (!StringUtils.isEmpty(mSearchViewTextView.getText()
						.toString())) {
					mSearchViewTextView.setText(null);
				} else {
					exitSearch();
				}
			}
		});
		
		abs__search_close_btn.setVisibility(View.INVISIBLE);

		View abs__submit_area = searchView
				.findViewById(com.actionbarsherlock.R.id.abs__submit_area);
		abs__submit_area.setBackgroundResource(R.drawable.transparent);

		abs__search_go_btn = (ImageView) searchView
				.findViewById(com.actionbarsherlock.R.id.abs__search_go_btn);
		// abs__search_go_btn.setPadding(DisplayUtil.dip2px(activity, 8),
		// DisplayUtil.dip2px(activity, 3), 0, 0);
		// abs__search_go_btn.setImageResource(R.drawable.ic_action_right_click);
		abs__search_go_btn.setPadding(0, 0, 0, 0);
		abs__search_go_btn.setImageResource(R.drawable.transparent);

		ImageView abs__search_button = (ImageView) searchView
				.findViewById(com.actionbarsherlock.R.id.abs__search_button);
		abs__search_button.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		abs__search_button.setPadding(0, 0, 0, 0);
		abs__search_button.setImageResource(R.drawable.ic_search);

		searchView.setSubmitButtonEnabled(true);

		searchView.setOnSearchClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (ac.isLocing) {
					return;
				}

				Log.d(TAG, "======searchView===== onClick ");
				isSearchViewOpen = true;
				Log.d(TAG,
						"======searchView===== onClick, isSearchViewOpen = true ");

				setTitle("");

				if (ac.searchKeyword != null) {
					mSearchViewTextView.setText(ac.searchKeyword);
					mSearchViewTextView.setSelection(ac.searchKeyword.length());
				}

				if (ac.lastChooseCity != null) {
					if (ac.lastChooseCity.cityId == null) {
						setCityId();
					}
				}

			}
		});

		searchView.setOnCloseListener(new OnCloseListener() {
			@Override
			public boolean onClose() {
				Log.d(TAG, "======searchView===== onClose ");
				isSearchViewOpen = false;
				Log.d(TAG,
						"======searchView===== onClick, isSearchViewOpen = false ");

				if (StringUtils.isEmpty(searchTextChange)) {
					// exitSearch();
				}

				supportInvalidateOptionsMenu();

				setTitle(ac.searchKeyword);

				// if (ac.isSearch) {
				// setTitle(ac.searchKeyword);
				// } else {
				// setTitle(mTitle);
				// }

				return false;
			}
		});

		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				isSearchViewOpen = false;
				Log.d(TAG,
						"======onQueryTextSubmit===== onClick, isSearchViewOpen = false ");
				// UIHelper.hideSoftInput(activity, searchView);
				pb.setVisibility(View.VISIBLE);
				searchEvent(query);

				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				Log.d(TAG, "======onQueryTextChange======" + newText);
				searchTextChange = newText;

				if (StringUtils.isEmpty(newText)) {
					abs__search_close_btn.setVisibility(View.INVISIBLE);
				} else {
					abs__search_close_btn.setVisibility(View.VISIBLE);
				}

				return false;
			}
		});

		searchView.setOnSuggestionListener(new OnSuggestionListener() {

			@Override
			public boolean onSuggestionSelect(int position) {
				Log.d(TAG, "======onSuggestionSelect======");
				return false;
			}

			@Override
			public boolean onSuggestionClick(int position) {
				Log.d(TAG, "======onSuggestionClick======");
				return false;
			}
		});

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		// Tells your app's SearchView to use this activity's searchable
		// configuration
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		abs__search_button.performClick();

	}

	/**
	 * 发出搜索事件，进行搜索
	 * 
	 * @param query
	 */
	protected void searchEvent(String query) {
		if ("速八".equals(query)) {
			query = "速8";
		} else if ("七天".equals(query)) {
			query = "7天";
		}

		// 输入法搜索按钮响应，有的输入法不显示搜索按钮
		Log.d(TAG, "======onQueryTextSubmit======" + query);

		// ac.isSearch = true;
		// ac.isJustSearch = true;
		ac.searchKeyword = query;

		KeyWordSearchEvent event = new KeyWordSearchEvent();
		event.keyword = query;
		EventBus.getDefault().post(event);
	}

	public void onClickKeyword(View v) {
		TextView tv = (TextView) v;
		String keyword = tv.getText().toString();

		abs__search_src_text.setText(keyword);
		abs__search_go_btn.performClick();
	}

	protected void onNewIntent(Intent intent) {
		Log.d(TAG, "======onNewIntent======");
		super.onNewIntent(intent);
		setIntent(intent);

		// Because this activity has set launchMode="singleTop", the system
		// calls this method
		// to deliver the intent if this activity is currently the foreground
		// activity when
		// invoked again (when the user executes a search from this activity, we
		// don't create
		// a new instance of this activity, so the system delivers the search
		// intent here)
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {

		pb.setVisibility(View.GONE);

		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			// handles a click on a search suggestion
			Log.d(TAG, "======handleIntent====== ACTION_VIEW");

			handleSuggestionResult(intent.getData());
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// handles a search query
			Log.d(TAG, "======handleIntent====== ACTION_SEARCH");

			String query = intent.getStringExtra(SearchManager.QUERY);
			if (query == null) {

				Log.d(TAG, "======handleIntent====== ACTION_SEARCH query=null");

				handleSuggestionResult(intent.getData());
			} else {

				handleKeywordResult(query);
			}

		}
	}

	/**
	 * handles a search query </br>对输入关键字的处理
	 * 
	 * @param query
	 */
	private void handleKeywordResult(String query) {
		Log.d(TAG, "======handleKeywordResult====== query=" + query);

		showResults(query);

	}

	/**
	 * Searches the dictionary and displays results for the given query.
	 * 
	 * @param query
	 *            The search query
	 */
	private void showResults(String query) {
		Cursor cursor = managedQuery(DictionaryProvider.CONTENT_URI, null,
				null, new String[] { query }, null);

		if (cursor == null || cursor.getCount() == 0) {
			AppContext.toastShow(R.string.search_tip_no_match);
		} else {
			SearchResultEvent event = new SearchResultEvent();
			event.hasResult = true;
			EventBus.getDefault().post(event);

			setResult(Constants.RESULT_CODE_SEARCH_RESULT);
			finish();
		}
	}

	/**
	 * handles a click on a search suggestion </br>对搜索建议点击的处理
	 * 
	 * @param uri
	 */
	private void handleSuggestionResult(Uri uri) {
		Log.d(TAG, "======handleSuggestionResult====== uri=" + uri);

		Cursor cursor = managedQuery(uri, null, null, null, null);

		if (cursor == null) {
			// finish();
			// TODO

		} else {
			cursor.moveToFirst();
			// TODO

			// Item item = new Item();
			// item.hotelsId = cursor.getString(cursor
			// .getColumnIndex(MyDbHelper.COLUMN_HOTEL_ID));
			// item.hotelsName = cursor.getString(cursor
			// .getColumnIndex(MyDbHelper.COLUMN_NAME));
			// item.hotelsAddr = cursor.getString(cursor
			// .getColumnIndex(MyDbHelper.COLUMN_ADDR));
			// item.brandName = cursor.getString(cursor
			// .getColumnIndex(MyDbHelper.COLUMN_BRAND_NAME));
			//
			// // item.hourroomPrice = cursor.getString(6);
			//
			// item.hotelsLatitude = cursor.getDouble(cursor
			// .getColumnIndex(MyDbHelper.COLUMN_LAT));
			// item.hotelsLongitude = cursor.getDouble(cursor
			// .getColumnIndex(MyDbHelper.COLUMN_LNG));
			// item.logopath = cursor.getString(cursor
			// .getColumnIndex(MyDbHelper.COLUMN_LOGO));
			// item.brandId = cursor.getString(cursor
			// .getColumnIndex(MyDbHelper.COLUMN_BRAND_ID));
			// item.hotelsTel = cursor.getString(cursor
			// .getColumnIndex(MyDbHelper.COLUMN_TEL));

			// item.hours = cursor.getString(12);

			// 计算当前所在位置与酒店的距离
			// AppContext ac = AppContext.getAc();
			// LocationData myLocation = ac.getMyLocation();
			//
			//
			// double distance = LatLngUtil.getDistance(myLocation.latitude,
			// myLocation.longitude, item.hotelsLatitude,
			// item.hotelsLongitude) / 1000;

			// double distance = LatLngUtil.getDistance(ac.lastReqLatitude,
			// ac.lastReqLongitude, item.hotelsLatitude,
			// item.hotelsLongitude) / 1000;
			//
			// distance = RoundTool.round(distance, 1,
			// BigDecimal.ROUND_HALF_UP);
			//
			// item.dist = "" + distance;

			// ac.isSearch = true;
			// ac.isSelectOneHotel = true;
			isSearchViewOpen = false;
			ac.searchKeyword = searchTextChange;

			// SelectOneHotelEvent event = new SelectOneHotelEvent();
			// event.hotel = item;
			// EventBus.getDefault().post(event);
			//
			// supportInvalidateOptionsMenu();
			// setTitle(ac.searchKeyword);

			Intent intent = new Intent();
			String hotelId = cursor.getString(cursor
					.getColumnIndex(MyDbHelper.COLUMN_HOTEL_ID));
			intent.putExtra(Keys.ID, hotelId);
			setResult(Constants.RESULT_CODE_SEARCH_RESULT_ONE, intent);
			finish();
		}
	}

	protected void exitSearch() {

		mSearchViewTextView.setText(null);
		// ac.isSearch = false;
		// ac.isSelectOneHotel = false;
		ac.searchKeyword = null;

		UIHelper.hideSoftInput(activity, mSearchViewTextView);

		setResult(Constants.RESULT_CODE_SEARCH_EXIT);
		finish();
	}

	private void setCityId() {
		String cityName = ac.lastChooseCity.cityName;

		MyDbHelper dbHelp = MyDbHelper.getInstance(activity);
		String cityId = dbHelp.queryCityIdByCityName(cityName);

		if (cityId != null) {
			ac.lastChooseCity.cityId = cityId;
		}

	}

}