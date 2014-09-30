package cn.op.zdf.ui;

import iamjiex.com.github.AndroidOverscrollViewPager.OverscrollViewPager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cn.op.common.AppConfig;
import cn.op.common.AppManager;
import cn.op.common.BasePagerAdapter;
import cn.op.common.UIHelper;
import cn.op.common.constant.Keys;
import cn.op.common.constant.Tags;
import cn.op.common.domain.URLs;
import cn.op.common.util.Constants;
import cn.op.common.util.DisplayUtil;
import cn.op.common.util.Log;
import cn.op.common.util.PhoneUtil;
import cn.op.common.util.StringUtils;
import cn.op.common.view.ViewPagerCustomDuration;
import cn.op.zdf.AlarmReceiver;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.dao.MyDbHelper;
import cn.op.zdf.domain.City;
import cn.op.zdf.domain.Item;
import cn.op.zdf.domain.ItemPage;
import cn.op.zdf.domain.Room;
import cn.op.zdf.event.AlarmEvent;
import cn.op.zdf.event.CityChooseEvent;
import cn.op.zdf.event.DecodeCityNameByLatLngEvent;
import cn.op.zdf.event.Event;
import cn.op.zdf.event.GotoHotelEvent;
import cn.op.zdf.event.HotelMarkerPriceUpdateEvent;
import cn.op.zdf.event.HotelVpItemSelectedEvent;
import cn.op.zdf.event.InitHotelPageDataEvent;
import cn.op.zdf.event.LoginEvent;
import cn.op.zdf.event.MenuSelectEvent;
import cn.op.zdf.event.ResetPswSuccessEvent;
import cn.op.zdf.event.ShowLogoutEvent;
import cn.op.zdf.event.ShowRemoveRecentBrowsEvent;
import cn.op.zdf.event.SlidingMenuOpenEvent;
import cn.op.zdf.event.VpItemSelectedEvent;
import cn.op.zdf.ui.BMapFragment.MyPagerAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.meizu.smartbar.SmartBarUtils;
import com.tencent.stat.StatService;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import de.greenrobot.event.EventBus;

/**
 * 主Activity，包含左侧DrawerLayout活动菜单控制，顶部ActionBar控制
 * 
 * @author lufei
 * 
 */
public class MainActivity extends SherlockFragmentActivity {

	private static final String TAG = Log.makeLogTag(MainActivity.class);

	// private static final int INDEX_DRAWER_MENU_HOTEL = 0;
	// private static final int INDEX_DRAWER_MENU_ORDER = 1;
	// private static final int INDEX_DRAWER_MENU_GIFT = -1;
	// private static final int INDEX_DRAWER_MENU_RECENT_BROWSE = 2;
	// private static final int INDEX_DRAWER_MENU_ACCOUNT = 3;
	// private static final int INDEX_DRAWER_MENU_SETTING = 4;

	private static final int INDEX_DRAWER_MENU_HOTEL = 0;
	private static final int INDEX_DRAWER_MENU_ORDER = 1;
	private static final int INDEX_DRAWER_MENU_GIFT = 2;
	private static final int INDEX_DRAWER_MENU_RECENT_BROWSE = -1;
	private static final int INDEX_DRAWER_MENU_ACCOUNT = 4;
	private static final int INDEX_DRAWER_MENU_SETTING = 5;

	public AppContext ac;
	FragmentManager fm;
	public LayoutInflater inflater;
	public int screenWidth;
	public int screenHight;
	int statusBarHeight;
	private boolean isStart = true;
	private View ivMask;
	// View cardBack;
	private MainActivity activity;
	// public DrawerLayout mDrawerLayout;
	private View mDrawerMenuLayout;

	// private ActionBar ab;
	public MyDbHelper dbHelp;
	// private ColorDrawable abBgRed;
	// private Drawable abBgTrans;
	private View layoutDialogExit;
	private View layoutDialogDeleteUser;
	private View layoutDialogLogout;
	private View layoutDialogClearCache;

	protected boolean isSlide2Top;

	private TextView tvTitle;

	private View ivTitleFlag;

	TextView tvRight;

	private View cardFace;

	View layoutBottomPanelMap;

	ViewPagerCustomDuration vp;

	private MyPagerAdapter vpAdapter;

	private View layoutDialogGetCash;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "======onCreate======");
		if (savedInstanceState != null) {
			savedInstanceState.setClassLoader(UIHelper.class.getClassLoader());
		}

		if (SmartBarUtils.hasSmartBar()) {
			getSherlock().setUiOptions(
					ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);

			// setTheme(R.style.MeizuAbsTheme);

			setTheme(R.style.Holo_Theme_CustomAbsOverlay);

			SmartBarUtils.setActionBarViewCollapsable(getActionBar(), true);
		}
		// else {
		// setTheme(R.style.NormalAbsTheme);
		// }

		super.onCreate(savedInstanceState);

		ac = AppContext.getAc();
		ac.initBMapManager(ac);

		AppManager.getAppManager().addActivity(this);
		EventBus.getDefault().register(this);

		TCAgent.init(this);
		TCAgent.setReportUncaughtExceptions(true);
		com.umeng.common.Log.LOG = AppContext.isDebugLog;

		activity = this;
		inflater = LayoutInflater.from(ac);
		fm = getSupportFragmentManager();

		initDb();

		setContentView(R.layout.activity_main);
		initView();
		initDrawerMenu();
		initAb();

		createAlarm();
	}

	/**
	 * 创建闹铃，在指定时间会由AlarmReceiver收到广播
	 */
	private void createAlarm() {
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(AlarmReceiver.ACTION_ALARM_TIME);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent,
				0);

		Calendar intendedCal = Calendar.getInstance();

		int alarmHour = 0;
		if (Room.isSellZdf()) {
			alarmHour = 18;
		} else if (Room.isSellWyf() || Room.isSellLsf()) {
			alarmHour = 8;
		}

		// 闹铃广播时间
		intendedCal.set(Calendar.HOUR_OF_DAY, alarmHour);
		intendedCal.set(Calendar.MINUTE, 0);
		intendedCal.set(Calendar.SECOND, 0);

		Calendar curent = Calendar.getInstance();
		long curentTime = curent.getTimeInMillis();
		long intendedTime = intendedCal.getTimeInMillis();

		if (intendedTime >= curentTime) {
			alarmManager.setRepeating(AlarmManager.RTC,
					intendedCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
					alarmIntent);
		} else {
			intendedCal.add(Calendar.DAY_OF_MONTH, 1);

			alarmManager.setRepeating(AlarmManager.RTC,
					intendedCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
					alarmIntent);

		}

	}

	private void initView() {
		// root = (RelativeLayout) findViewById(R.id.root_main);
		cardFace = (View) findViewById(R.id.layoutMap);
		// cardBack = (View) findViewById(R.id.layoutBottomPanelMap);

		View layoutDialog = findViewById(R.id.layoutDialog);
		layoutDialogClearCache = findViewById(R.id.dialogClearCache);
		layoutDialogExit = findViewById(R.id.dialogExit);
		layoutDialogDeleteUser = findViewById(R.id.dialogDeleteUser);
		layoutDialogLogout = findViewById(R.id.dialogLogout);
		layoutDialogGetCash = findViewById(R.id.dialogGetCash);

		// if (SmartBarUtils.hasSmartBar()) {
		// layoutDialog.setPadding(0, 0, 0, getResources()
		// .getDimensionPixelSize(R.dimen.marginTopAbs));
		// }

		ivMask = findViewById(R.id.ivMask);
		ivMask.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});

		initVp();
	}

	class MyPagerAdapter extends BasePagerAdapter<Item> {

		private View convertView;
		private LinkedList<View> mRecycledViewsList = new LinkedList<View>();

		public MyPagerAdapter(List<Item> data) {
			super(data);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object view) {
			container.removeView((View) view);
			mRecycledViewsList.push((View) view);

			Log.i(TAG, "Stored view in cache " + view.hashCode());
		}

		private View inflateOrRecycleView() {

			View viewToReturn;

			if (mRecycledViewsList.isEmpty()) {
				viewToReturn = inflater.inflate(R.layout.hotel_vp_item, null,
						false);
			} else {
				viewToReturn = mRecycledViewsList.pop();

				Log.i(TAG,
						"Restored recycled view from cache "
								+ viewToReturn.hashCode());
			}

			return viewToReturn;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {

			// convertView = inflater.inflate(R.layout.hotel_vp_item,
			// container, false);

			convertView = inflateOrRecycleView();

			container.addView(convertView, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			TextView tvTitle = (TextView) convertView
					.findViewById(R.id.textView1);
			TextView tvAddr = (TextView) convertView
					.findViewById(R.id.textView2);
			TextView tvDist = (TextView) convertView.findViewById(R.id.tvDist);
			View layoutPrice = convertView.findViewById(R.id.layout2);
			TextView tvPrice = (TextView) convertView
					.findViewById(R.id.textView5);
			View tvPricePre = convertView.findViewById(R.id.textView6);
			ImageView ivLogo = (ImageView) convertView
					.findViewById(R.id.imageView1);
			ImageView ivFavorable = (ImageView) convertView
					.findViewById(R.id.imageView2);

			View layoutService = convertView.findViewById(R.id.layoutService);
			ImageView ivServiceWifi = (ImageView) layoutService
					.findViewById(R.id.ivWifi);
			ImageView ivServicePark = (ImageView) layoutService
					.findViewById(R.id.ivPark);
			ImageView ivHours4 = (ImageView) layoutService
					.findViewById(R.id.ivHours4);
			ImageView ivHours6 = (ImageView) layoutService
					.findViewById(R.id.ivHours6);

			final Item hotel = getItem(position);

			tvTitle.setText(hotel.hotelsName);
			tvAddr.setText(hotel.hotelsAddr);
			tvDist.setText(hotel.dist + "km");

			// 酒店提供服务设施，具体参考：酒店提供设施对照.txt
			// 酒店所提供服务
			if (hotel.facilitysIds != null) {
				if (hotel.facilitysIds.contains("136")) {
					ivServiceWifi
							.setImageResource(R.drawable.ic_service_wifi_1_true);
				} else {
					ivServiceWifi
							.setImageResource(R.drawable.ic_service_wifi_1);
				}
				if (hotel.facilitysIds.contains("137")) {
					ivServicePark
							.setImageResource(R.drawable.ic_service_park_1_true);
				} else {
					ivServicePark
							.setImageResource(R.drawable.ic_service_park_1);
				}
			} else {
				ivServiceWifi.setImageResource(R.drawable.ic_service_wifi_1);
				ivServicePark.setImageResource(R.drawable.ic_service_park_1);
			}
			if (hotel.zdfDurationType != null) {
				if (hotel.zdfDurationType.contains("4")) {
					ivHours4.setImageResource(R.drawable.ic_hour_4_true);
				} else {
					ivHours4.setImageResource(R.drawable.ic_hour_4);
				}
				if (hotel.zdfDurationType.contains("6")) {
					ivHours6.setImageResource(R.drawable.ic_hour_6_true);
				} else {
					ivHours6.setImageResource(R.drawable.ic_hour_6);
				}
			} else {
				ivHours4.setImageResource(R.drawable.ic_hour_4);
				ivHours6.setImageResource(R.drawable.ic_hour_6);
			}

			// 根据当前销售时段显示相应的价格
			if (Room.isSellZdf()) {
				if (!StringUtils.isEmpty(hotel.hourroomPrice)) {
					tvPricePre.setVisibility(View.VISIBLE);
					tvPrice.setVisibility(View.VISIBLE);
					tvPrice.setText(hotel.hourroomPrice);
					layoutPrice.setVisibility(View.VISIBLE);
				} else {

					layoutPrice.setVisibility(View.INVISIBLE);
				}

				if (hotel.hasTuan) {
					ivFavorable.setVisibility(View.VISIBLE);
				} else {
					ivFavorable.setVisibility(View.GONE);
				}

			} else if (Room.isSellWyf()) {
				if (!StringUtils.isEmpty(hotel.dayroomPrice)) {
					layoutPrice.setVisibility(View.VISIBLE);
					tvPricePre.setVisibility(View.VISIBLE);
					tvPrice.setVisibility(View.VISIBLE);

					tvPrice.setText(hotel.dayroomPrice);
				} else {
					tvPricePre.setVisibility(View.INVISIBLE);
					tvPrice.setVisibility(View.INVISIBLE);
					layoutPrice.setVisibility(View.INVISIBLE);
				}

			} else if (Room.isSellLsf()) {
				if (!StringUtils.isEmpty(hotel.nightroomPrice)) {
					layoutPrice.setVisibility(View.VISIBLE);
					tvPricePre.setVisibility(View.VISIBLE);
					tvPrice.setVisibility(View.VISIBLE);

					tvPrice.setText(hotel.nightroomPrice);
				} else {
					tvPricePre.setVisibility(View.INVISIBLE);
					tvPrice.setVisibility(View.INVISIBLE);
					layoutPrice.setVisibility(View.INVISIBLE);
				}

			}

			ac.mImageLoader.displayImage(URLs.URL_ZDF_API + hotel.logopath,
					ivLogo, ac.optionsLogo);

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					UIHelper.showHotelActivity(activity, hotel);
				}
			});

			return convertView;
		}

	}

	private void initVp() {
		layoutBottomPanelMap = findViewById(R.id.layoutBottomPanelMap);
//		OverscrollViewPager mOverscrollViewPager = (OverscrollViewPager) layoutBottomPanelMap
//				.findViewById(R.id.viewPager);
//		vp = mOverscrollViewPager.getOverscrollView();
		
		vp = (ViewPagerCustomDuration) findViewById(R.id.viewPager);
		vp.setScrollDurationFactor(2);
		
		// ItemPage parseCouponDemo = ItemPage.parseCouponDemo();
		// vpAdapter = new MyPagerAdapter(parseCouponDemo.list);
		vpAdapter = new MyPagerAdapter(new ArrayList<Item>());
		vp.setAdapter(vpAdapter);

		vp.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(final int position) {

				VpItemSelectedEvent event = new VpItemSelectedEvent();
				event.index = position;
				EventBus.getDefault().post(event);

				vp.setScrollDurationFactor(1);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

	}

	void notifyVpAdapterData(List<Item> data) {
		vpAdapter.data = data;
		vpAdapter.notifyDataSetChanged();
	}

	// private void initVp() {
	//
	// if (true) {
	// return;
	// }
	//
	// View layoutBottomPanelMap = findViewById(R.id.layoutBottomPanelMap1);
	// OverscrollViewPager mOverscrollViewPager = (OverscrollViewPager)
	// layoutBottomPanelMap
	// .findViewById(R.id.viewPager);
	// ViewPager vp = mOverscrollViewPager.getOverscrollView();
	// ItemPage parseCouponDemo = ItemPage.parseCouponDemo();
	// MyPagerAdapter vpAdapter = new MyPagerAdapter(parseCouponDemo.list);
	// // vpAdapter = new MyPagerAdapter(mHotelList);
	// vp.setAdapter(vpAdapter);
	// vpAdapter.notifyDataSetChanged();
	//
	// vp.setOnPageChangeListener(new OnPageChangeListener() {
	//
	// @Override
	// public void onPageSelected(final int position) {
	//
	// new Handler().postDelayed(new Runnable() {
	//
	// @Override
	// public void run() {
	// }
	// }, 100);
	//
	// }
	//
	// @Override
	// public void onPageScrolled(int position, float positionOffset,
	// int positionOffsetPixels) {
	// }
	//
	// @Override
	// public void onPageScrollStateChanged(int state) {
	// }
	// });
	//
	// }

	private void initDb() {
		dbHelp = MyDbHelper.getInstance(activity);
	}

	private void initAb() {
		// ab = getSupportActionBar();
		// ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		// ab.setCustomView(R.layout.top_bar_main);

		View topBar = findViewById(R.id.topBarMain);
		ImageView btnLeft = (ImageView) topBar.findViewById(R.id.btnLeft);
		tvRight = (TextView) topBar.findViewById(R.id.tvRight);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		ivTitleFlag = topBar.findViewById(R.id.ivTitleFlag);
		btnLeft.setImageResource(R.drawable.ic_menu_click);
		btnLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleDrawerMenu();
			}
		});

		tvRight.setText("查看列表");
		tvRight.setVisibility(View.VISIBLE);
		tvRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				switch (curtCheckedMenuIndex) {
				case 0:
					UIHelper.showLvHotel(activity);
					break;
				case 1:

					TabListener<Fragment> tab = (TabListener<Fragment>) menus[1]
							.getTag();
					LvOrderFragment frag = (LvOrderFragment) tab.mFragment;
					frag.exitMultiCheck();
					break;
				case 2:
					UIHelper.showSimpleFragActivity(activity,
							CouponHelpFragment.class);
					break;
				case 4:
					UIHelper.showSimpleFragActivity(activity, FaqFragment.class);
					break;

				default:
					break;
				}

			}
		});

		if (ac.lastLocCity != null) {
			setCity(ac.lastLocCity);
		} else {
			setTitle("选择城市");
		}

		tvTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				UIHelper.showCityChoose(activity);
			}
		});

		// abBgRed = new
		// ColorDrawable(getResources().getColor(R.color.red_ab_zdf));

		// if (SmartBarUtils.hasSmartBar()) {
		// abBgTrans = getResources().getDrawable(R.drawable.transparent);
		// } else {
		// abBgTrans = getResources().getDrawable(R.drawable.transparent);
		// }

		// enable ActionBar app icon to behave as action to toggle nav drawer
		// ab.setDisplayHomeAsUpEnabled(true);

		// 隐藏ActionBar上的logo
		// ab.setLogo(getResources().getDrawable(R.drawable.transparent));
		// ab.setDisplayUseLogoEnabled(true);
		// ab.setDisplayShowHomeEnabled(true);

	}

	public void toggleDrawerMenu() {
		// if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
		// mDrawerLayout.closeDrawer(GravityCompat.START);
		// } else {
		// mDrawerLayout.openDrawer(GravityCompat.START);
		// }

		slidingMenu.toggle();
	}

	public CheckBox[] menus;
	protected CheckBox toSelectMenu;
	// private TextView tvCity;
	protected CheckBox lastCheckedMenu;
	protected int curtCheckedMenuIndex;
	private CheckBox tvMapHotels;
	// private ActionBarDrawerToggle mDrawerToggle;
	protected String mDrawerTitle = "菜单";
	protected CharSequence mTitle;
	protected boolean isSearchViewOpen;
	// public BadgeView badgeIsLogin;
	// public SearchView mSearchView;
	// public SearchAutoComplete mSearchViewTextView;
	MenuItem menuNav;
	public TextView tv8IsLogin;
	private CheckBox tvAccount;

	private int colorRed;
	public int colorGray;

	// private ImageView ivMenuStatePic;
	// private ImageView ivMenuStateIcon;
	// private TextView tvMenuStateHoursTip;
	// private TextView tvMenuStateHours;

	protected String searchTextChange;

	public View menuLogout;

	private CheckBox tvMyOrders;

	private CheckBox tvRecentBrows;

	private CheckBox tvAboutUs;

	private CheckBox tvMyCoupon;

	private SlidingMenu slidingMenu;

	private void initDrawerMenu() {

		slidingMenu = new SlidingMenu(this);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		slidingMenu.setMenu(R.layout.layout_menu_left);

		// mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// mDrawerMenuLayout = findViewById(R.id.drawer_view);

		mDrawerMenuLayout = slidingMenu.getMenu();

		mDrawerMenuLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
		// if (SmartBarUtils.hasSmartBar()) {
		// mDrawerMenuLayout.setPadding(0, getResources()
		// .getDimensionPixelSize(R.dimen.marginTopAbs), 0,
		// getResources().getDimensionPixelSize(R.dimen.marginTopAbs));
		// }

		View layoutAboutUs = mDrawerMenuLayout.findViewById(R.id.layoutAboutUs);
		tvMapHotels = (CheckBox) mDrawerMenuLayout.findViewById(R.id.textView1);
		tvMyOrders = (CheckBox) mDrawerMenuLayout.findViewById(R.id.textView2);
		tvRecentBrows = (CheckBox) mDrawerMenuLayout
				.findViewById(R.id.textView3);
		tvAccount = (CheckBox) mDrawerMenuLayout.findViewById(R.id.textView4);
		tvAboutUs = (CheckBox) mDrawerMenuLayout.findViewById(R.id.textView5);
		tvMyCoupon = (CheckBox) mDrawerMenuLayout.findViewById(R.id.textView7);
		// View ivHelp = mDrawerMenuLayout.findViewById(R.id.ivHelp);
		View ivNew = mDrawerMenuLayout.findViewById(R.id.ivNew);
		View layoutMenuFooter = mDrawerMenuLayout
				.findViewById(R.id.layoutMenuFooter);
		final View ivCustomerServiceTel = mDrawerMenuLayout
				.findViewById(R.id.ivCustomerServiceTel);

		tv8IsLogin = (TextView) mDrawerMenuLayout.findViewById(R.id.textView8);

		setMenuState();

		lastCheckedMenu = tvMapHotels;
		mTitle = tvMapHotels.getText().toString();

		colorRed = getResources().getColor(R.color.red_zdf);
		colorGray = getResources().getColor(R.color.gray_white_menu_left);

		if (ac.isLogin()) {
			tv8IsLogin.setText("已登录");
			tv8IsLogin.setTextColor(colorRed);
		} else {
			tv8IsLogin.setText("未登录");
			tv8IsLogin.setTextColor(colorGray);
		}

		AppConfig appConfig = AppConfig.getAppConfig(activity);
		String versionName = appConfig.get(Keys.VERSION_NEWEST);

		if (!StringUtils.isEmpty(versionName)
				&& !versionName.equals(PhoneUtil.getVersionName(activity))) {
			ivNew.setVisibility(View.VISIBLE);
		}

		menus = new CheckBox[] { tvMapHotels, tvMyOrders, tvMyCoupon,
				tvAccount, tvAboutUs };

		String[] fragTags = new String[] { "temp", Tags.TAG_ORDER,
				Tags.TAG_COUPON, Tags.TAG_ACCOUNT, Tags.TAG_ABOUT_US };

		Class[] fragClasses = new Class[] { Fragment.class,
				LvOrderFragment.class, LvCouponFragment.class,
				AccountFragment.class, AboutUsFragment.class };

		for (int i = 0; i < menus.length; i++) {
			menus[i].setOnClickListener(onMenuItemClickListener);
			menus[i].setTag(new TabListener<Fragment>(activity, fragTags[i],
					fragClasses[i], null));
		}

		// ivHelp.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// UIHelper.showSimpleFragActivity(activity, FaqFragment.class);
		// }
		// });

		layoutMenuFooter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				UIHelper.call(activity, Constants.TEL_JUJIA);
			}
		});

		layoutAboutUs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				tvAboutUs.performClick();
			}
		});

		initDrawerLayout();

	}

	private void setMenuState() {
		// if (Room.isSellWyf() || Room.isSellLsf()) {
		// ivMenuStatePic
		// .setImageResource(R.drawable.img_menu_state_night_rate_bg);
		// ivMenuStateIcon
		// .setImageResource(R.drawable.img_menu_state_night_rate_sun_icon);
		// tvMenuStateHoursTip.setText("午夜房特价时段");
		// tvMenuStateHours.setText(Room.SALE_DATE_INCLUDE_WYF_SHOW);
		// } else if (Room.isSellLsf()) {
		// ivMenuStatePic
		// .setImageResource(R.drawable.img_menu_state_midnight_rate_bg);
		// ivMenuStateIcon
		// .setImageResource(R.drawable.img_menu_state_night_rate_moon_icon);
		// tvMenuStateHoursTip.setText("凌时房特价时段");
		// tvMenuStateHours.setText(Room.SALE_DATE_INCLUDE_LSF_SHOW);
		// } else {
		// ivMenuStatePic
		// .setImageResource(R.drawable.img_menu_state_hour_rate_bg);
		// ivMenuStateIcon
		// .setImageResource(R.drawable.img_menu_state_hour_rate_clock_icon);
		// tvMenuStateHoursTip.setText("钟点房特价时段");
		// tvMenuStateHours.setText(Room.SALE_DATE_INCLUDE_ZDF_SHOW);
		// }
	}

	private void initDrawerLayout() {

		slidingMenu.setOnOpenedListener(new OnOpenedListener() {
			@Override
			public void onOpened() {
				// ab.setTitle(mDrawerTitle);
				supportInvalidateOptionsMenu();
				// creates call to onPrepareOptionsMenu()
				UIHelper.hideSoftInput(activity, slidingMenu);

				SlidingMenuOpenEvent event = new SlidingMenuOpenEvent();
				event.isOpen = true;
				EventBus.getDefault().post(event);

				if (ac.isLogin()) {
					tv8IsLogin.setText("已登录");
					tv8IsLogin.setTextColor(colorRed);
				} else {
					tv8IsLogin.setText("未登录");
					tv8IsLogin.setTextColor(colorGray);
				}

				setMenuState();

				// 菜单打开时，关闭已经显示的对话框
				// if (layoutDialogDeleteUser.getVisibility() == View.VISIBLE) {
				// UIHelper.showDialogDeleteUser(activity,
				// layoutDialogDeleteUser, ivMask);
				// }
				// if (layoutDialogExit.getVisibility() == View.VISIBLE) {
				// UIHelper.showDialogExit(activity, layoutDialogExit, ivMask);
				// }
			}
		});

		slidingMenu.setOnClosedListener(new OnClosedListener() {

			@Override
			public void onClosed() {
				supportInvalidateOptionsMenu();
			}
		});

		// // 黑色半透明遮罩
		// // mDrawerLayout.setScrimColor(Color.TRANSPARENT);
		//
		// mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
		// GravityCompat.START);
		//
		// // ActionBarDrawerToggle ties together the the proper interactions
		// // between the sliding drawer and the action bar app icon
		// mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		// mDrawerLayout, /* DrawerLayout object */
		// R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		// R.string.drawer_open, /* "open drawer" description for accessibility
		// */
		// R.string.drawer_close /* "close drawer" description for accessibility
		// */
		// ) {
		// public void onDrawerClosed(View view) {
		// // ab.setTitle(mTitle);
		// supportInvalidateOptionsMenu();
		// // creates call to onPrepareOptionsMenu()
		// }
		//
		// public void onDrawerOpened(View drawerView) {
		// // ab.setTitle(mDrawerTitle);
		// supportInvalidateOptionsMenu();
		// // creates call to onPrepareOptionsMenu()
		// UIHelper.hideSoftInput(activity, drawerView);
		// UIHelper.closeSearchView(mSearchView, mSearchViewTextView);
		//
		// SlidingMenuOpenEvent event = new SlidingMenuOpenEvent();
		// event.isOpen = true;
		// EventBus.getDefault().post(event);
		//
		// if (ac.isLogin()) {
		// tv8IsLogin.setText("已登录");
		// tv8IsLogin.setTextColor(colorRed);
		// } else {
		// tv8IsLogin.setText("未登录");
		// tv8IsLogin.setTextColor(colorGray);
		// }
		//
		// setMenuState();
		//
		// // 菜单打开时，关闭已经显示的对话框
		// // if (layoutDialogDeleteUser.getVisibility() == View.VISIBLE) {
		// // UIHelper.showDialogDeleteUser(activity,
		// // layoutDialogDeleteUser, ivMask);
		// // }
		// // if (layoutDialogExit.getVisibility() == View.VISIBLE) {
		// // UIHelper.showDialogExit(activity, layoutDialogExit, ivMask);
		// // }
		//
		// }
		// };
		//
		// mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater supportMenuInflater = getSupportMenuInflater();

		if (true) {
			if (SmartBarUtils.hasSmartBar()) {
				UIHelper.makeEmptyMenu(supportMenuInflater, menu);
			}
			return super.onCreateOptionsMenu(menu);
		}

		// if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
		// if (SmartBarUtils.hasSmartBar()) {
		// UIHelper.makeEmptyMenu(supportMenuInflater, menu);
		// }
		// return super.onCreateOptionsMenu(menu);
		// }
		if (slidingMenu.isMenuShowing()) {
			if (SmartBarUtils.hasSmartBar()) {
				UIHelper.makeEmptyMenu(supportMenuInflater, menu);
			}
			return super.onCreateOptionsMenu(menu);
		}

		if (layoutDialogExit.getVisibility() == View.VISIBLE) {
			if (SmartBarUtils.hasSmartBar()) {
				UIHelper.makeEmptyMenu(supportMenuInflater, menu);
			}
			return super.onCreateOptionsMenu(menu);
		}

		switch (curtCheckedMenuIndex) {
		case INDEX_DRAWER_MENU_HOTEL:
			supportMenuInflater.inflate(R.menu.main_menu, menu);

			menuNav = menu.findItem(R.id.menu_nav);
			// if (isShowHotelMap) {
			if (SmartBarUtils.hasSmartBar()) {
				menuNav.setIcon(R.drawable.ic_loc_meizu);
				menu.findItem(R.id.menu_search).setIcon(
						R.drawable.ic_action_right_meizu);
			}

			if (isSearchViewOpen) {
				menuNav.setVisible(false);
			} else {
				menuNav.setVisible(true);
			}

			// SearchView searchView = (SearchView) menu
			// .findItem(R.id.menu_search).getActionView();
			// initSearchView(searchView);

			// UIHelper.openSearchView(mSearchView, mSearchViewTextView);
			// } else {
			// menuNav.setIcon(R.drawable.ic_bottom_map_click);
			//
			// menu.removeItem(R.id.menu_search);
			// }

			// if (activity.mHotelList.size() == 0) {
			// menuNav.setVisible(false);
			// }

			break;

		case INDEX_DRAWER_MENU_ORDER:
			if (SmartBarUtils.hasSmartBar()) {
				UIHelper.makeEmptyMenu(supportMenuInflater, menu);
			}
			break;

		case INDEX_DRAWER_MENU_GIFT:
			if (SmartBarUtils.hasSmartBar()) {
				UIHelper.makeEmptyMenu(supportMenuInflater, menu);
			}
			break;

		case INDEX_DRAWER_MENU_RECENT_BROWSE:

			supportMenuInflater.inflate(R.menu.recent_browse_menu, menu);
			break;

		case INDEX_DRAWER_MENU_ACCOUNT:

			if (SmartBarUtils.hasSmartBar()) {
				UIHelper.makeEmptyMenu(supportMenuInflater, menu);

				if (menuLogout == null) {
					menuLogout = new View(activity);
				}
			} else {
				supportMenuInflater.inflate(R.menu.account_menu, menu);
				menuLogout = menu.findItem(R.id.menu_account_logout)
						.getActionView();

				menuLogout.setBackgroundResource(R.drawable.transparent);
				// ((ImageButton) menuLogout)
				// .setImageResource(R.drawable.ic_logout_click);

				menuLogout.setMinimumWidth(getResources()
						.getDimensionPixelSize(
								R.dimen.abs__action_button_min_width));
				menuLogout.setMinimumHeight(getResources()
						.getDimensionPixelSize(
								R.dimen.abs__action_bar_default_height));
			}

			menuLogout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (UIHelper.isFastDoubleClick(v)) {
						return;
					}

					showLogout();

				}
			});

			menuLogout.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					if (UIHelper.isFastDoubleClick(v)) {
						return true;
					}

					if (layoutDialogLogout.getVisibility() == View.VISIBLE) {
						return true;
					}

					if (layoutDialogDeleteUser.getVisibility() == View.VISIBLE) {
						return true;
					}

					UIHelper.showDialogDeleteUser(activity,
							layoutDialogDeleteUser, ivMask);

					return true;
				}
			});

			break;

		case INDEX_DRAWER_MENU_SETTING:

			if (SmartBarUtils.hasSmartBar()) {
				UIHelper.makeEmptyMenu(supportMenuInflater, menu);
			}
			break;
		}

		return super.onCreateOptionsMenu(menu);
	}

	protected void showLogout() {
		if (layoutDialogLogout.getVisibility() == View.VISIBLE) {
			return;
		}

		if (layoutDialogDeleteUser.getVisibility() == View.VISIBLE) {
			return;
		}

		UIHelper.showDialogLouout(activity, layoutDialogLogout, ivMask);
	}

	public void showDialogGetCash() {
		UIHelper.showDialogGetCash(activity, layoutDialogGetCash, ivMask);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// if (isSearchViewOpen) {
		// return super.onOptionsItemSelected(item);
		// }
		if (ivMask.getVisibility() == View.VISIBLE) {
			return true;
		}

		switch (item.getItemId()) {
		case android.R.id.home:
			// handle clicking the app icon/logo
			UIHelper.hideSoftInput(activity, slidingMenu);
			toggleDrawerMenu();
			return true;
		case R.id.menu_search:

			// UIHelper.showSearchActivity(activity);

			return true;
		case R.id.menu_nav:

			UIHelper.showLvHotel(activity);

			// UIHelper.hideSoftInput(activity, mSearchView);
			// cardRootLayout.collapsePane();
			//
			// ReqLocEvent ev = new ReqLocEvent();
			// ev.isReqLoc = true;
			// EventBus.getDefault().post(ev);
			//
			// supportInvalidateOptionsMenu();
			return true;

		case R.id.menu_delete_recent_brows:

			ShowRemoveRecentBrowsEvent evn = new ShowRemoveRecentBrowsEvent();
			evn.isShow = true;
			EventBus.getDefault().post(evn);

			return true;
		case R.id.menu_account_logout:

			ShowLogoutEvent event = new ShowLogoutEvent();
			event.isShow = true;
			EventBus.getDefault().post(event);

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// @Override
	// protected void onPostCreate(Bundle savedInstanceState) {
	// super.onPostCreate(savedInstanceState);
	// // Sync the toggle state after onRestoreInstanceState has occurred.
	// mDrawerToggle.syncState();
	// }
	//
	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// super.onConfigurationChanged(newConfig);
	// // Pass any configuration change to the drawer toggls
	// mDrawerToggle.onConfigurationChanged(newConfig);
	// }

	// private BadgeView getBadge(Context context, View tv1, int textColorResId,
	// int bgColorResId) {
	// BadgeView badge = new BadgeView(context, tv1);
	// badge.setBadgePosition(BadgeView.POSITION_CENTER_RIGHT);
	// badge.setBadgeMargin(
	// DisplayUtil.dip2px(ac,
	// getResources().getDimension(R.dimen.margin_medium)), 5);
	//
	// badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
	// badge.setTextColor(getResources().getColor(textColorResId));
	// badge.setBackgroundResource(bgColorResId);
	//
	// badge.setPadding(DisplayUtil.dip2px(ac, 4), 0,
	// DisplayUtil.dip2px(ac, 4), 0);
	// return badge;
	// }

	/**
	 * Callback interface invoked when a tab is focused, unfocused, added, or
	 * removed.
	 */
	private class TabListener<T extends Fragment> {
		private final FragmentActivity mActivity;
		private final String mTag;
		private final Class<T> mClass;
		private final Bundle mArgs;
		private Fragment mFragment;

		public TabListener(FragmentActivity activity, String tag, Class<T> clz,
				Bundle args) {
			super();
			this.mActivity = activity;
			this.mTag = tag;
			this.mClass = clz;
			this.mArgs = args;
		}

		/**
		 * Called when a tab enters the selected state.
		 * 
		 * @param tab
		 *            The tab that was selected
		 * @param ft
		 *            A {@link FragmentTransaction} for queuing fragment
		 *            operations to execute during a tab switch. The previous
		 *            tab's unselect and this tab's select will be executed in a
		 *            single transaction. This FragmentTransaction does not
		 *            support being added to the back stack.
		 */
		public void onTabSelected(View tab, FragmentTransaction ft) {
			if (mFragment == null) {
				Fragment fragment = activity.fm.findFragmentByTag(mTag);
				if (fragment != null) {
					mFragment = fragment;
					ft.attach(mFragment);
					// ft.show(mFragment);
				} else {
					mFragment = Fragment.instantiate(mActivity,
							mClass.getName(), mArgs);
					ft.add(R.id.content_frame, mFragment, mTag);
				}
			} else {
				ft.attach(mFragment);
				// ft.show(mFragment);
			}
		}

		public void onTabUnselected(View tab, FragmentTransaction ft) {
			if (mFragment == null) {
				Fragment fragment = activity.fm.findFragmentByTag(mTag);
				if (fragment != null) {
					mFragment = fragment;
					ft.detach(mFragment);
					// ft.hide(mFragment);
				}
			} else {
				// Log.d(TAG, "======onTabUnselected====== ");

				ft.detach(mFragment);
				// ft.hide(mFragment);
			}
		}
	}

	private OnClickListener onMenuItemClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (UIHelper.isFastDoubleClick(v)) {
				return;
			}

			// 需登录
			if (v == menus[3] || v == menus[1] || v == menus[2]) {
				if (!ac.isNetworkConnected()) {
					((CheckBox) v).setChecked(false);
					AppContext.toastShow(R.string.pleaseCheckNet);
					return;
				}

				if (!ac.isLogin()) {
					((CheckBox) v).setChecked(false);
					toSelectMenu = (CheckBox) v;

					UIHelper.showLoginActivity(activity);

					return;
				}
			}

			for (int i = 0; i < menus.length; i++) {
				if (menus[i] == v) {
					curtCheckedMenuIndex = i;
				}
			}
			EventBus.getDefault().post(
					new MenuSelectEvent(curtCheckedMenuIndex));

			// 顶部右侧按钮文字
			switch (curtCheckedMenuIndex) {
			case 0:
				tvRight.setText("查看列表");
				break;
			case 1:
				tvRight.setText("");
				break;
			case 2:
				tvRight.setText("帮助");
				break;
			case 4:
				tvRight.setText("帮助");
				break;
			default:
				tvRight.setText("");
				break;
			}

			// 顶部标题文字
			if (v == menus[0]) {
				ivTitleFlag.setVisibility(View.VISIBLE);
				tvTitle.setClickable(true);

				if (ac.lastChooseCity != null) {
					setTitle(ac.lastChooseCity.cityName);
				} else if (ac.lastLocCity != null) {
					setTitle(ac.lastLocCity.cityName);
				} else {
					setTitle("选择城市");
				}
			} else {
				ivTitleFlag.setVisibility(View.INVISIBLE);
				tvTitle.setClickable(false);
				mTitle = ((CheckBox) v).getText().toString();
				setTitle(mTitle);
			}

			if (lastCheckedMenu == v) {
				((CheckBox) v).setChecked(true);
				slidingMenu.showContent();
			} else {
				final FragmentTransaction ft = activity.fm.beginTransaction();

				// ft.setCustomAnimations(R.anim.umeng_socialize_fade_in,
				// R.anim.umeng_socialize_fade_out,
				// R.anim.umeng_socialize_fade_in,
				// R.anim.umeng_socialize_fade_out);

				if (v == tvMapHotels) {
					lastCheckedMenu.setChecked(false);
					((TabListener<?>) lastCheckedMenu.getTag())
							.onTabUnselected(lastCheckedMenu, ft);

					((CheckBox) v).setChecked(true);
					lastCheckedMenu = (CheckBox) v;
				} else {
					lastCheckedMenu.setChecked(false);

					if (lastCheckedMenu != tvMapHotels) {
						((TabListener<?>) lastCheckedMenu.getTag())
								.onTabUnselected(lastCheckedMenu, ft);
					}

					((CheckBox) v).setChecked(true);
					((TabListener<?>) v.getTag()).onTabSelected(v, ft);
					lastCheckedMenu = (CheckBox) v;
				}

				ft.commitAllowingStateLoss();

				slidingMenu.showContent();
				supportInvalidateOptionsMenu();
			}
		}
	};

	@Override
	public void setTitle(CharSequence title) {

		// if ("个人资料".equals(title) || "系统设置".equals(title)) {
		// title = "";
		// }

		tvTitle.setText(title);
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "======onStart======");
		super.onStart();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "======onResume======");

		super.onResume();
		TCAgent.onResume(this);
		StatService.onResume(this);

		if (ac.isJustCityChange) {
			cityChoose();
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		Log.d(TAG, "======onRestoreInstanceState======");
		if (savedInstanceState != null) {
			// savedInstanceState.setClassLoader(getClass().getClassLoader());

			String title = savedInstanceState.getString("title");
			setTitle(title);

			curtCheckedMenuIndex = savedInstanceState
					.getInt("curtCheckedMenuIndex");
			// menus[curtCheckedMenuIndex].setChecked(true);
			menus[curtCheckedMenuIndex].performClick();
			lastCheckedMenu = menus[curtCheckedMenuIndex];

			Log.d(TAG,
					"======onRestoreInstanceState====== curtCheckedMenuIndex="
							+ curtCheckedMenuIndex);

			FragmentTransaction ft = activity.fm.beginTransaction();
			for (int i = 1; i < menus.length; i++) {
				TabListener<?> menuListener = (TabListener<?>) menus[i]
						.getTag();
				if (curtCheckedMenuIndex == i) {
					menuListener.onTabSelected(menus[i], ft);
				} else {
					menuListener.onTabUnselected(menus[i], ft);
				}
			}
			ft.commitAllowingStateLoss();
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// if (outState != null) {
		// outState.putString("temp", "temp");
		// outState.setClassLoader(getClass().getClassLoader());
		// }
		Log.d(TAG, "======onSaveInstanceState======");

		outState.putInt("curtCheckedMenuIndex", curtCheckedMenuIndex);
		outState.putBoolean("isStart", isStart);
		outState.putString("title", "" + tvTitle.getText());

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Log.d(TAG, "======onWindowFocusChanged====== hasFocus= " + hasFocus);

		if (isStart) {
			isStart = false;
			// ac.requestLocation();
			statusBarHeight = DisplayUtil.getStatusBarHeight(this);
			screenHight = DisplayUtil.getScreenHight(this) - statusBarHeight;
			screenWidth = DisplayUtil.getScreenWidth(this);

			Log.d(TAG, "======onWindowFocusChanged====== screenHight="
					+ screenHight + " ,screenWidth" + screenWidth);

			new CountDownTimer(300, 400) {
				@Override
				public void onTick(long arg0) {
				}

				@Override
				public void onFinish() {
					Message msg = new Message();
					msg.what = WHAT_START;
					handler.sendMessage(msg);
				}
			}.start();
		}
	}

	private final int WHAT_START = 2;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case WHAT_START:
				if (!ac.isNetworkConnected()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					builder.setTitle("网络未开启");
					builder.setMessage("没有网络，很多事情都做不了的哦。\n是否去开启网络？");
					// 选择确定删除执行的代码
					builder.setPositiveButton("是",
							new Dialog.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											Settings.ACTION_WIRELESS_SETTINGS);
									startActivityForResult(
											intent,
											Constants.REQUEST_CODE_OPEN_WIRELESS); // 设置完成后返回到原来的界面
								}
							});
					// 选择取消所执行的代码
					builder.setNegativeButton("否",
							new Dialog.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									if (!ac.isNetworkConnected()) {
										// finish();
									}
								}
							});

					builder.setCancelable(false);

					builder.create().show();
				}

				UmengUpdateAgent.setUpdateOnlyWifi(false);
				UmengUpdateAgent.update(activity);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
					@Override
					public void onUpdateReturned(int updateStatus,
							UpdateResponse updateInfo) {
						switch (updateStatus) {
						case UpdateStatus.Yes: // has update
							AppConfig appConfig = AppConfig
									.getAppConfig(activity);
							appConfig.set(Keys.VERSION_NEWEST,
									updateInfo.version);

							break;
						case UpdateStatus.No: // has no update

							break;
						case UpdateStatus.NoneWifi: // none wifi

							break;
						case UpdateStatus.Timeout: // time out

							break;
						}
					}
				});

				// 提示打开GPS
				// if (!PhoneUtil.isGpsOPen(ac)) {
				// AlertDialog.Builder builder = new AlertDialog.Builder(
				// MainActivity.this);
				// builder.setTitle("是否开启GPS呢？");
				// builder.setMessage("在户外使用，采用 GPS 定位会更准。\n在室内就不用开启 GPS 了，因为建筑物遮挡会影响 GPS 信号强度。\n\n那么是否进入 GPS 设置界面");
				// // 选择确定删除执行的代码
				// builder.setPositiveButton("是",
				// new Dialog.OnClickListener() {
				// public void onClick(DialogInterface dialog,
				// int which) {
				//
				// Intent intent = new Intent(
				// Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				//
				// startActivityForResult(intent,
				// Constants.REQUEST_CODE_OPEN_GPS); // 设置完成后返回到原来的界面
				// }
				// });
				// // 选择取消所执行的代码
				// builder.setNegativeButton("否", null);
				// //
				// 设置这个对话框不能被用户按[返回键]而取消掉,但测试发现如果用户按了KeyEvent.KEYCODE_SEARCH,对话框还是会Dismiss掉
				// builder.setCancelable(false);
				//
				// AlertDialog dialog = builder.create();
				// // 由于设置alertDialog.setCancelable(false);
				// //
				// 发现如果用户按了KeyEvent.KEYCODE_SEARCH,对话框还是会Dismiss掉,这里的setOnKeyListener作用就是屏蔽用户按下KeyEvent.KEYCODE_SEARCH
				// dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
				// @Override
				// public boolean onKey(DialogInterface dialog,
				// int keyCode, KeyEvent event) {
				// if (keyCode == KeyEvent.KEYCODE_SEARCH) {
				// return true;
				// } else {
				// return false; // 默认返回 false
				// }
				// }
				// });
				//
				// dialog.show();
				// }

				break;
			default:
				break;
			}
		}
	};

	/**
	 * 数据是否经过过滤
	 */
	public boolean isFilter;

	public boolean isFilterPrice;
	public int curtFilterPriceMin;
	public int curtFilterPriceMax;

	public boolean isFilterBrand;
	// public String curtFilterBrandName;
	public Item curtFilterBrand;
	public boolean isToShowGuideFragment;

	/**
	 * 用于activity中fragment的popBackStack
	 */
	public void popActivityFragBackStack() {
		if (this.getSupportFragmentManager().getBackStackEntryCount() > 0) {
			super.onBackPressed();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "======onActivityResult====== requestCode=" + requestCode);
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Constants.REQUEST_CODE_OPEN_GPS:
			if (PhoneUtil.isGpsOPen(ac)) {
				ac.startLocBD();
			}
			break;

		case Constants.REQUEST_CODE_CHOOSE_CITY:
			if (resultCode == Constants.REQUEST_CODE_CHOOSE_CITY) {

				cityChoose();

				// Log.d(TAG, "======onActivityResult====== city=" +
				// city.cityName);
			}

			break;

		default:
			break;
		}

	}

	public void onEventMainThread(Event event) {
		Log.d(TAG, "======onEventMainThread======"
				+ event.getClass().getSimpleName());

		if (event instanceof ResetPswSuccessEvent) {
			ResetPswSuccessEvent e = (ResetPswSuccessEvent) event;
			if (e.isSuccess) {

				tvAccount.performClick();
			}
		}

		if (event instanceof AlarmEvent) {
			AlarmEvent e = (AlarmEvent) event;
			if (e.isAlarm) {
				setMenuState();
				createAlarm();
			}
		}

		if (event instanceof GotoHotelEvent) {
			GotoHotelEvent e = (GotoHotelEvent) event;
			if (e.isGoTo) {
				tvMapHotels.performClick();
			}
		}

		if (event instanceof LoginEvent) {
			if (toSelectMenu != null) {
				toSelectMenu.performClick();
				toSelectMenu = null;
			}

			tv8IsLogin.setText("已登录");
			tv8IsLogin.setTextColor(colorRed);
		}

		if (event instanceof DecodeCityNameByLatLngEvent) {
			DecodeCityNameByLatLngEvent ev = (DecodeCityNameByLatLngEvent) event;
			City city = ev.city;

			setCity(city);

			if (ac.lastChooseCity.cityId == null) {
				setCityId();
			}
		}

	}

	private void cityChoose() {
		ac.isSearch = false;
		ac.isSelectOneHotel = false;
		ac.searchKeyword = null;

		City city = ac.lastChooseCity;
		setCity(city);

		if (ac.lastChooseCity.cityId == null) {
			setCityId();
		}

		// 更新此城市酒店数据据
		ac.getHotelPageInsertDb(MyDbHelper.getInstance(ac),
				ac.lastChooseCity.cityId);
	}

	private void setCity(City city) {

		if (curtCheckedMenuIndex == 0) {
			setTitle(city.cityName);
		}

		ac.lastChooseCity = city;
	}

	private void setCityId() {
		String cityName = ac.lastChooseCity.cityName;

		String cityId = dbHelp.queryCityIdByCityName(cityName);

		if (cityId != null) {
			ac.lastChooseCity.cityId = cityId;
		}

	}

	@Override
	public void finish() {
		Log.d(TAG, "======finish======");
		super.finish();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "======onPause======");

		super.onPause();
		TCAgent.onPause(this);
		StatService.onPause(this);

		if (activity.isToShowGuideFragment) {
			activity.isToShowGuideFragment = false;
			// overridePendingTransition(R.anim.slide_in_from_bottom,
			// R.anim.slide_out_to_top);

			overridePendingTransition(R.anim.slide_in_from_right,
					R.anim.slide_out_to_left);
		} else {
			overridePendingTransition(R.anim.slide_in_from_right,
					R.anim.slide_out_to_left);
		}

	}

	@Override
	protected void onStop() {
		Log.d(TAG, "======onStop======");
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "======onDestroy======");
		EventBus.getDefault().unregister(this);

		ac.stopLocBD();
		dbHelp.close();

		ac.destroyValue();

		// AppManager.getAppManager().AppExit(ac);
		super.onDestroy();
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// // 用于activity中fragment的popBackStack
	// if (this.getSupportFragmentManager().getBackStackEntryCount() > 0
	// && keyCode == KeyEvent.KEYCODE_BACK) {
	// BackStackEntry backStackEntryAt = this.getSupportFragmentManager()
	// .getBackStackEntryAt(0);
	// String name = backStackEntryAt.getName();
	//
	// if (Tags.HOTEL_DETAIL.equals(name) && ac.isReserve) {
	// HideReserveEvent hideReserveEvent = new HideReserveEvent();
	// hideReserveEvent.hide = true;
	// EventBus.getDefault().post(hideReserveEvent);
	//
	// return true;
	// }
	//
	// animation1(false, ivMask);
	// super.onBackPressed();
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	@Override
	public void onBackPressed() {

		if (slidingMenu.isMenuShowing()) {
			slidingMenu.showContent();
			return;
		}

		if (fm.getBackStackEntryCount() > 0) {
			super.onBackPressed();
			return;
		}

		if (layoutDialogDeleteUser.getVisibility() == View.VISIBLE) {
			UIHelper.showDialogDeleteUser(activity, layoutDialogDeleteUser,
					ivMask);

		} else if (layoutDialogLogout.getVisibility() == View.VISIBLE) {
			UIHelper.showDialogDeleteUser(activity, layoutDialogLogout, ivMask);

		} else if (layoutDialogClearCache.getVisibility() == View.VISIBLE) {
			UIHelper.showDialogDeleteUser(activity, layoutDialogClearCache,
					ivMask);

		} else {

			Log.i(TAG, "======onBackPressed======");

			switch (curtCheckedMenuIndex) {
			case 0:
				// 显示或隐藏退出对话框
				// UIHelper.ExitWhitHandler(this);
				// UIHelper.showDialogExit(this, layoutDialogExit, ivMask);
				//
				// supportInvalidateOptionsMenu();

				AppManager.getAppManager().AppExit(ac);
				break;
			case 1:
				TabListener<Fragment> tab = (TabListener<Fragment>) menus[1]
						.getTag();
				LvOrderFragment frag = (LvOrderFragment) tab.mFragment;
				if (frag.isMultiCheck) {

					frag.exitMultiCheck();
				} else {
					// 返回第一个菜单页面
					tvMapHotels.performClick();
				}
				break;

			default:
				// 返回第一个菜单页面
				tvMapHotels.performClick();
				break;
			}
		}
	}

	public void showDialogClearCache() {
		UIHelper.showDialogClearCache(activity, layoutDialogClearCache, ivMask);
	}

}