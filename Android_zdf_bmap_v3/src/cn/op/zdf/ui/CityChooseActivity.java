package cn.op.zdf.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.op.common.AppConfig;
import cn.op.common.AppException;
import cn.op.common.UIHelper;
import cn.op.common.constant.Keys;
import cn.op.common.util.Constants;
import cn.op.common.util.DateUtil;
import cn.op.common.util.Log;
import cn.op.common.util.StringUtils;
import cn.op.common.view.SideBar;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.dao.MyDbHelper;
import cn.op.zdf.domain.City;
import cn.op.zdf.domain.CityPage;
import cn.op.zdf.event.CityChooseEvent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.meizu.smartbar.SmartBarUtils;
import com.tendcloud.tenddata.TCAgent;

import de.greenrobot.event.EventBus;

/**
 * 城市选择页面
 * 
 * @author lufei
 * 
 */
public class CityChooseActivity extends SherlockFragmentActivity {

	private static final String TAG = Log.makeLogTag(CityChooseActivity.class);

	protected static final int WHAT_TEMP_UPDATE_CITY = 2;
	protected static final int WHAT_INIT_DATA = 1;

	private CityChooseActivity activity;
	private View pb;
	private View pbGPS;
	private AppContext ac;
	private MyDbHelper dbHelp;
	private GeoCoder mSearch;

	private City mCity;
	private ListView lvCities;
	private CityAdapter lvCitiesAdapter;
	protected TextView tvGpsCity;

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
		setContentView(R.layout.activity_city_choose);

		activity = this;
		ac = AppContext.getAc();
		dbHelp = MyDbHelper.getInstance(activity);

		// addFragment();

		View layoutTopbar = findViewById(R.id.topBar);
		((TextView) layoutTopbar.findViewById(R.id.tvTitle)).setText("选择城市");
		ImageView btnLeft = (ImageView) layoutTopbar.findViewById(R.id.btnLeft);
		ImageView btnRight = (ImageView) layoutTopbar
				.findViewById(R.id.btnRight);

		btnLeft.setVisibility(View.VISIBLE);
		btnRight.setVisibility(View.INVISIBLE);

		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				// activity.onBackPressed();
				// ((MainActivity) activity).popActivityFragBackStack();
			}
		});

		initView();

		// int action = UIHelper.LISTVIEW_ACTION_INIT;
		// if (ac.serverConfig.isNeedUpdateCity) {
		// action = UIHelper.LISTVIEW_ACTION_REFRESH;
		// }
		//
		// initData(action);

		initData();
	}

	private void initView() {
		pb = findViewById(R.id.pb);
		pbGPS = findViewById(R.id.pb_small);

		boolean reChoose = false;
		// reChoose = getIntent().getBooleanExtra(Keys.RE_CHOOSE, false);

		mCity = ac.lastLocCity;

		tvGpsCity = (TextView) findViewById(R.id.tv_gpsCity);
		// if (reChoose) {
		// initLocation();
		// } else {
		// if (mCity != null) {
		// tvGpsCity.setText(mCity.name);
		// // UIHelper.showHome(this);
		// } else {
		// initLocation();
		// }
		// }

		if (mCity != null) {
			tvGpsCity.setText(mCity.cityName);
		} else {
			initLocation();
		}

		tvGpsCity.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				String gspCity = tvGpsCity.getText().toString();

				if (StringUtils.isEmpty(gspCity)) {
					return;
				}

				if (ac.lastLocCity != null) {
					selectCity(ac.lastLocCity);
				} else {
					AppContext.toastShow(R.string.noThisCityData);
				}
			}
		});

		lvCities = (ListView) findViewById(R.id.myListView);
		lvCities.setVerticalFadingEdgeEnabled(false);

		ArrayList<City> cities = new ArrayList<City>();
		lvCitiesAdapter = new CityAdapter(activity, cities);
		lvCities.setAdapter(lvCitiesAdapter);

		SideBar indexBar = (SideBar) findViewById(R.id.sideBar);
		indexBar.setListView(lvCities);

		lvCities.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (UIHelper.isFastDoubleClick(view)) {
					return;
				}

				City city = lvCitiesAdapter.getItem(position);

				selectCity(city);
			}
		});
	}

	private void initData() {
		pb.setVisibility(View.VISIBLE);
		// 获取城市列表
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = new Message();
				try {

					CityPage list = dbHelp.queryCityPage();

					msg.what = WHAT_INIT_DATA;
					msg.obj = list;
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				myHandler.sendMessage(msg);
			}
		}).start();
	}

	protected void selectCity(City city) {
		if (city.cityLat != 0 && city.cityLng != 0) {
			ac.lastChooseLoc = null;
			ac.lastChooseCity = city;
			ac.isJustCityChange = true;
			ac.saveObject(city, Keys.LAST_CHOOSE_CITY);

			setResult(Constants.REQUEST_CODE_CHOOSE_CITY);
			finish();
		} else {

			// TODO 没有经纬度，根据名称获取经纬度
			// ac.decodeCityLatLngByName(city.cityName);

			AppContext.toastShow("缺少城市经纬度，无法切换城市");
		}
	}

	private MyHandler myHandler = new MyHandler(this);

	static class MyHandler extends Handler {
		private WeakReference<CityChooseActivity> mWr;

		public MyHandler(CityChooseActivity frag) {
			super();
			this.mWr = new WeakReference<CityChooseActivity>(frag);
		}

		@Override
		public void handleMessage(Message msg) {
			CityChooseActivity frag = mWr.get();

			switch (msg.what) {
			case WHAT_INIT_DATA:
				frag.pb.setVisibility(View.GONE);

				CityPage cityList = (CityPage) msg.obj;

				if (cityList.rspMsg.OK()) {
					AppConfig appConfig = AppConfig.getAppConfig(frag.ac);

					String date = DateUtil.getDate4Zdf();
					appConfig.set(Keys.LAST_UPDATE_CITY_TIME, date);
				} else {
					AppContext.toastShowException(cityList.rspMsg.message);
				}

				City cityBj = new City("1", "北京", "热门城市",
						Constants.BEIJING.latitude, Constants.BEIJING.longitude);
				City citySh = new City("2", "上海", "热门城市",
						Constants.SHANGHAI.latitude,
						Constants.SHANGHAI.longitude);
				City cityGz = new City("32", "广州", "热门城市",
						Constants.GUANGZHOU.latitude,
						Constants.GUANGZHOU.longitude);
				City citySz = new City("30", "深圳", "热门城市",
						Constants.SHENZHEN.latitude,
						Constants.SHENZHEN.longitude);
				City cityHz = new City("17", "杭州", "热门城市",
						Constants.HANGZHOU.latitude,
						Constants.HANGZHOU.longitude);
				City cityXa = new City("10", "西安", "热门城市",
						Constants.XIAN.latitude, Constants.XIAN.longitude);

				cityList.list.add(0, cityXa);
				cityList.list.add(0, cityHz);
				cityList.list.add(0, citySz);
				cityList.list.add(0, cityGz);
				cityList.list.add(0, citySh);
				cityList.list.add(0, cityBj);

				frag.lvCitiesAdapter.data = cityList.list;
				frag.lvCitiesAdapter.notifyDataSetChanged();

				// frag.tempUpdateCitys(cList);

				break;
			case WHAT_TEMP_UPDATE_CITY:

				frag.pb.setVisibility(View.GONE);

				frag.lvCitiesAdapter.data = (List<City>) msg.obj;
				frag.lvCitiesAdapter.notifyDataSetChanged();
				break;
			case -1:
				frag.pb.setVisibility(View.GONE);
				// 有异常--显示加载出错 & 弹出错误消息
				((AppException) msg.obj).makeToast(frag.ac);
				break;
			}
		}
	}

	/**
	 * 定位&经纬度转地址
	 */
	private void initLocation() {
		if (ac.myLocationBD != null) {
			double latitude = ac.myLocationBD.getLatitude();
			double longitude = ac.myLocationBD.getLongitude();

			decodeCityByLatLng(latitude, longitude);
		} else {
			AppContext.toastShow("暂时无法定位");
		}

	}

	/**
	 * // 反Geo搜索
	 * 
	 * @param latitude
	 * @param longitude
	 */
	private void decodeCityByLatLng(double latitude, double longitude) {
		pbGPS.setVisibility(View.VISIBLE);

		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

			@Override
			public void onGetGeoCodeResult(GeoCodeResult result) {
				// 地理编码：通过地址检索坐标点
				Log.d(TAG, "======mSearch====== onGetGeoCodeResult");
			}

			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				// 反地理编码：通过坐标点检索详细地址及周边poi
				pbGPS.setVisibility(View.GONE);

				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR) {
					Log.d(TAG,
							"======mSearch====== onGetReverseGeoCodeResult error= "
									+ result.error);

					if (mCity != null) {
						tvGpsCity.setText(mCity.cityName);
					}
					AppContext.toastShow(R.string.canNotGetLocation);
					return;
				}

				AddressComponent addrDetail = result.getAddressDetail();
				Log.d(TAG,
						"======mSearch====== onGetReverseGeoCodeResult addrDetail= "
								+ addrDetail.toString());

				String cityName = addrDetail.city;
				String district = addrDetail.district;

				if (!StringUtils.isEmpty(district) && district.endsWith("市")) {
					cityName = district;
				}

				if (!StringUtils.isEmpty(cityName)) {
					if (cityName.endsWith("市")) {
						cityName = cityName.substring(0, cityName.length() - 1);
					}
					tvGpsCity.setText(cityName);

					ac.lastLocCity = new City(cityName,
							result.getLocation().latitude,
							result.getLocation().longitude);
				} else {
					if (mCity != null) {
						tvGpsCity.setText(mCity.cityName);
					} else {
						AppContext.toastShow("无法获取城市");
					}
				}

			}

		});

		LatLng ptCenter = new LatLng(latitude, longitude);
		boolean reverseGeoCode = mSearch
				.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));

		Log.d(TAG, "======mSearch====== reverseGeocode= " + reverseGeoCode);
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "======onResume======");
		super.onResume();
		TCAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "======onPause======");
		super.onPause();
		TCAgent.onPause(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater supportMenuInflater = getSupportMenuInflater();

		if (SmartBarUtils.hasSmartBar()) {
			UIHelper.makeEmptyMenu(supportMenuInflater, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	// protected void addFragment() {
	// CityChooseFragment fragment;
	//
	// fragment = (CityChooseFragment) Fragment.instantiate(activity,
	// CityChooseFragment.class.getName());
	//
	// fm.beginTransaction().add(R.id.root_city_choose, fragment,
	// Tags.CITY_CHOOSE)
	// .commit();
	// }

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_from_left,
				R.anim.slide_out_to_right);
	}

}
