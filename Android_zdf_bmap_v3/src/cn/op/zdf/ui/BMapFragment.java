package cn.op.zdf.ui;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.op.common.AppConfig;
import cn.op.common.AppException;
import cn.op.common.BaseApplication;
import cn.op.common.BasePagerAdapter;
import cn.op.common.UIHelper;
import cn.op.common.constant.Keys;
import cn.op.common.domain.URLs;
import cn.op.common.util.BMapUtil;
import cn.op.common.util.Constants;
import cn.op.common.util.DateUtil;
import cn.op.common.util.DisplayUtil;
import cn.op.common.util.ImageUtils;
import cn.op.common.util.LatLngUtil;
import cn.op.common.util.Log;
import cn.op.common.util.PhoneUtil;
import cn.op.common.util.RoundTool;
import cn.op.common.util.StringUtils;
import cn.op.zdf.AppContext;
import cn.op.zdf.HotelUtil;
import cn.op.zdf.R;
import cn.op.zdf.dao.MyDbHelper;
import cn.op.zdf.domain.City;
import cn.op.zdf.domain.Item;
import cn.op.zdf.domain.ItemPage;
import cn.op.zdf.domain.Room;
import cn.op.zdf.event.AlarmEvent;
import cn.op.zdf.event.DecodeCityNameByLatLngEvent;
import cn.op.zdf.event.Event;
import cn.op.zdf.event.LocFailEvent;
import cn.op.zdf.event.LocationChangedEvent;
import cn.op.zdf.event.ReqLocEvent;
import cn.op.zdf.event.VpItemSelectedEvent;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.MapViewLayoutParams.Builder;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import de.greenrobot.event.EventBus;

/**
 * 百度地图：主地图页面，负责酒店列表数据的请求，Marker的绘制, 如果地图只一直显示格子，一般是key有问题 </br>具体参考：百度地图Api
 * 
 * @author lufei
 * 
 */
public class BMapFragment extends Fragment {

	private static final String TAG = Log.makeLogTag(BMapFragment.class);

	/**
	 * 正式，注意正式签名版本要切换成正式key
	 */
	public static final String strKey = "DFde133943982019a97e17a60157c218";

	/**
	 * debug lufeizhang-mac
	 */
	// public static final String strKey = "6Lr60gzqWgbr8DFS3gxY3env";

	private AppContext ac;
	private MainActivity activity;
	private MapView mMapView;
	private BaiduMap mMapController;
	public Marker mCurMarker;
	public Button button;

	// private View viewCache;
	// private View popupInfo;
	// private View popupLeft;
	// private View popupRight;
	// private TextView popupText;
	// protected PopupOverlay pop;
	private LayoutInflater inflater;
	private MarkersOverlay mMarkersOverlay;
	private ArrayList<Marker> mMarkerList = new ArrayList<Marker>();
	protected int roomType;
	private View pb;
	private TextView tvPbTitle;

	/**
	 * 是否
	 */
	// private boolean isLocBtnClickMapMove;
	private float mapZoomDefault = 17;

	private View ivTransMask;

	private GeoCoder mSearch;

	/**
	 * 地图的中心位置
	 */
	private LatLng curtCenterLoc;
	/**
	 * 数据加载前，是否第一次加载数据
	 */
	public boolean isFirstLoadData = true;

	/**
	 * 数据加载前，是否刚刚进行过定位
	 */
	private boolean isJustLocChange = false;

	/**
	 * 是否是本次关键字搜索第一次加载数据
	 */
	private boolean isFirstKeywordsSearch;

	private View layoutKeyword;

	private TextView tvKeyword;

	private View ivLocation;

	private ViewGroup layoutMap;

	private int heightInitMarker;

	private int widthInitMarker;

	private int lastScaleIndex = -1;

	private final float scaleXByValue = 0.3f;
	private final float scaleYByValue = 0.3f;

	/**
	 * 地图中显示的酒店集合
	 */
	public List<Item> mHotelList = new ArrayList<Item>();

	private View ivLocationMan;

	private View ivExitSearch;

	// private MyPagerAdapter vpAdapter;
	// private ViewPager vp;

	// private View layoutBottomPanelMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "======onCreate======");

		super.onCreate(savedInstanceState);

		ac = AppContext.getAc();
		activity = (MainActivity) getActivity();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "======onCreateView======");
		this.inflater = inflater;

		/**
		 * 注意： 由于MapView在setContentView()中初始化，
		 * 所以请在使用setContentView前初始化BMapManager对象， 否则会报错
		 */
		View view = inflater.inflate(R.layout.frag_baidu_map, container, false);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		initView(view);
		// initVp(view);
		initMapView(view);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(TAG, "======onViewCreated======");

		super.onViewCreated(view, savedInstanceState);
		// EventBus.getDefault().register(this);

		// btnLeft.setImageResource(R.drawable.ic_action_left_click);
		// tvTopBarTitle.setText("查看酒店");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "======onActivityCreated======");
		super.onActivityCreated(savedInstanceState);

		// if (savedInstanceState != null) {
		// if (mMapView != null) {
		// mMapView.onRestoreInstanceState(savedInstanceState);
		// }
		// }
	}

	private void initView(View view) {
		pb = view.findViewById(R.id.pb);
		pb.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});

		tvPbTitle = (TextView) pb.findViewById(R.id.tvPbTitle);

		layoutMap = (ViewGroup) view.findViewById(R.id.layoutMap);
		ivLocationMan = layoutMap.findViewById(R.id.imageView1);
		layoutKeyword = layoutMap.findViewById(R.id.layoutKeyword);
		tvKeyword = (TextView) layoutKeyword.findViewById(R.id.tvKeyword);
		ivExitSearch = layoutKeyword.findViewById(R.id.ivExitSearch);
		ivLocation = layoutKeyword.findViewById(R.id.imageView2);

		// if (SmartBarUtils.hasSmartBar()) {
		// android.widget.RelativeLayout.LayoutParams lp =
		// (android.widget.RelativeLayout.LayoutParams) layoutKeyword
		// .getLayoutParams();
		// Resources resources = getResources();
		// int margin = resources.getDimensionPixelSize(R.dimen.margin_large);
		// int absHeight = resources
		// .getDimensionPixelSize(R.dimen.abs__action_bar_default_height);
		//
		// lp.setMargins(absHeight, margin + absHeight, absHeight, 0);
		// } else {
		// android.widget.RelativeLayout.LayoutParams lp =
		// (android.widget.RelativeLayout.LayoutParams) layoutKeyword
		// .getLayoutParams();
		// Resources resources = getResources();
		// int margin = resources.getDimensionPixelSize(R.dimen.margin_large);
		// int absHeight = resources
		// .getDimensionPixelSize(R.dimen.abs__action_bar_default_height);
		//
		// lp.setMargins(margin, margin + absHeight, margin, 0);
		// }

		ivTransMask = view.findViewById(R.id.ivTransMask);
		ivTransMask.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				UIHelper.hideSoftInput(activity, v);

				if (activity.menuNav != null && mHotelList.size() > 0) {
					activity.menuNav.setVisible(true);
				}

				if (pb.getVisibility() == View.VISIBLE) {
					return true;
				} else {
					return false;
				}

			}
		});

		ivLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				reqLoc();
			}
		});

		tvKeyword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, SearchActivity.class);
				startActivityForResult(intent, Constants.REQUEST_CODE_SEARCH);
			}
		});

		ivExitSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				if (ac.isSearch) {
					exitSearch();
				}

			}
		});

		if (ac.isSearch) {
			ivExitSearch.setVisibility(View.VISIBLE);
		} else {
			ivExitSearch.setVisibility(View.INVISIBLE);
		}

	}

	// private void initVp(View view) {
	// if (true) {
	// return;
	// }
	//
	// layoutBottomPanelMap = view.findViewById(R.id.layoutBottomPanelMap);
	// OverscrollViewPager mOverscrollViewPager = (OverscrollViewPager) view
	// .findViewById(R.id.viewPager);
	// vp = mOverscrollViewPager.getOverscrollView();
	// ItemPage parseCouponDemo = ItemPage.parseCouponDemo();
	// // vpAdapter = new MyPagerAdapter(parseCouponDemo.list);
	// vpAdapter = new MyPagerAdapter(mHotelList);
	// vp.setAdapter(vpAdapter);
	//
	// vp.setOnPageChangeListener(new OnPageChangeListener() {
	//
	// @Override
	// public void onPageSelected(final int position) {
	//
	// if (scaleAnim != null && scaleAnim.isRunning()) {
	// Log.d(TAG, "======onPageSelected====== cancel");
	// scaleAnim.cancel();
	// }
	//
	// new Handler().postDelayed(new Runnable() {
	//
	// @Override
	// public void run() {
	// vpItemSelected(position);
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
	//
	// void notifyVpAdapterData() {
	// // vpAdapter.data = mHotelList;
	// // vpAdapter.notifyDataSetChanged();
	// }

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

				// 酒店提供服务设施，具体参考：酒店提供设施对照.txt
				// 酒店所提供服务
				if (hotel.facilitysIds != null) {
					if (hotel.facilitysIds.contains("136")) {
						ivServiceWifi
								.setImageResource(R.drawable.ic_service_wifi_1_true);
					}
					if (hotel.facilitysIds.contains("137")) {
						ivServicePark
								.setImageResource(R.drawable.ic_service_park_1_true);
					}
				}
				if (hotel.zdfDurationType != null) {
					if (hotel.zdfDurationType.contains("4")) {
						ivHours4.setImageResource(R.drawable.ic_hour_4_true);
					}
					if (hotel.zdfDurationType.contains("6")) {
						ivHours6.setImageResource(R.drawable.ic_hour_6_true);
					}
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

	protected void reqLoc() {
		if (!ac.isNetworkConnected()) {
			AppContext.toastShow(R.string.pleaseCheckNet);
			return;
		}

		if (!PhoneUtil.canLoc(ac)) {
			BaseApplication.toastShow("没有网络，GPS也未开启，无法定位");
			return;
		}

		pb.setVisibility(View.VISIBLE);
		tvPbTitle.setText("定位中·····");
		ac.startLocBD();

		mMapController.setMapStatus(MapStatusUpdateFactory
				.zoomTo(mapZoomDefault));
	}

	private class MyMKMapViewListener implements OnMapStatusChangeListener {
		private double lastMovedistance;
		protected boolean isMapFilpMove;
		private CountDownTimer countDownTimer;

		@Override
		public void onMapStatusChange(MapStatus arg0) {

		}

		@Override
		public void onMapStatusChangeFinish(MapStatus arg0) {
			Log.d(TAG, "======onMapMoveFinish====== ");

			// 地图移动完成时会回调此接口
			// mapMoveFinish();

			isMapFilpMove = false;

			if (countDownTimer != null) {
				countDownTimer.cancel();
				countDownTimer = null;
			}

			countDownTimer = new CountDownTimer(
					MAP_DEFAULT_ANIMATION_TIME + 190,
					MAP_DEFAULT_ANIMATION_TIME + 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					Log.d(TAG, "======countDownTimer====== onTick");

					if (!isMapFilpMove) {
						mapMoveFinish();
					}

					this.cancel();
					countDownTimer = null;
				}

				@Override
				public void onFinish() {
					Log.d(TAG, "======countDownTimer====== onFinish");
					if (!isMapFilpMove) {
						mapMoveFinish();
					}

					this.cancel();
					countDownTimer = null;
				}
			};

			countDownTimer.start();
		}

		@Override
		public void onMapStatusChangeStart(MapStatus arg0) {

		}

		private void mapMoveFinish() {
			LatLng center = mMapController.getMapStatus().target;

			Log.d(TAG, "======onMapMoveFinish====== center= " + center.latitude
					+ "," + center.longitude);

			lastMovedistance = LatLngUtil.getDistance(ac.lastReqLatitude,
					ac.lastReqLongitude, center.latitude, center.longitude);

			Log.d(TAG, "======onMapMoveFinish====== distance= "
					+ lastMovedistance);

			if (lastMovedistance <= 500) {
				return;
			}

			lastMovedistance = 0;

			if (ac.isSelectOneHotel) {
				return;
			}

			if (ac.isSearch) {
				initDataByKeywords(center.latitude, center.longitude,
						ac.searchKeyword);
			} else {
				initData(center.latitude, center.longitude);
			}

			Log.d(TAG, "======onMapMoveFinish====== initData");

			ac.lastChooseLoc = new LatLng(center.latitude, center.longitude);

			// decodeCityByLatLng(latitude, longitude, true);
		}

	};

	private void initMapView(View view) {
		BaiduMapOptions mapOptions = new BaiduMapOptions();
		mapOptions
				.mapStatus(new MapStatus.Builder().zoom(mapZoomDefault).build())
				.zoomControlsEnabled(false).scaleControlEnabled(false)
				.overlookingGesturesEnabled(false).rotateGesturesEnabled(false);

		mMapView = new MapView(activity, mapOptions);
		layoutMap.addView(mMapView, 0, new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));

		// mMapView = (MapView) view.findViewById(R.id.mapView);
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		mMapController = mMapView.getMap();

		// 为了解决对地图加动画时出现的黑色背景； 虽然起作用了，但不可用，因为其他view就无法在上显示
		// Class<? extends MapView> mapClass = mMapView.getClass();
		// try {
		// Field declaredField = mapClass.getDeclaredField("b");
		// declaredField.setAccessible(true);
		//
		// Object object = declaredField.get(mMapView);
		// if (object instanceof GLSurfaceView) {
		// GLSurfaceView obj = (GLSurfaceView) object;
		// obj.setZOrderOnTop(true);// necessary
		//
		// // SurfaceHolder holder = obj.getHolder();
		// // holder.setFormat(PixelFormat.TRANSPARENT);
		// }
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		mMapController.setMyLocationEnabled(true);
		mMapController.setMyLocationConfigeration(new MyLocationConfiguration(
				MyLocationConfiguration.LocationMode.NORMAL, true, null));

		mMapController.setOnMapStatusChangeListener(new MyMKMapViewListener());

		mMarkersOverlay = new MarkersOverlay(mMapController);
		mMapController.setOnMarkerClickListener(mMarkersOverlay);

		mMapController.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				// TODO 设置地图操作中心点在屏幕的坐标, 只有在 OnMapLoadedCallback.onMapLoaded()
				// 之后设置才生效

				// final int[] location = new int[2];
				// ivLocationMan.getLocationOnScreen(location);
				//
				// int x = (int) location[0];
				// int y = (int) location[1];
				//
				// Log.d(TAG, "======onMapLoaded===== ivLocationMan x=" + x
				// + ",y=" + y);
				//
				// if (x != 0 && y != 0) {
				// MapStatus mapStatus = new MapStatus.Builder().targetScreen(
				// new Point(x, y)).build();
				// mMapController.setMapStatus(MapStatusUpdateFactory
				// .newMapStatus(mapStatus));
				// }
			}
		});

		LatLng locData = moveCamera2MyLocation();
		MyLocationData myLocationData = new MyLocationData.Builder()
				.latitude(locData.latitude).longitude(locData.longitude)
				.build();
		mMapController.setMyLocationData(myLocationData);

		decodeCityByLatLng(locData.latitude, locData.longitude, false);

		initData(locData.latitude, locData.longitude);
	}

	/**
	 * @return 当前可用的定位位置，若无则取上一次位置，否则取北京
	 */
	private LatLng moveCamera2MyLocation() {
		Log.d(TAG, "======moveCamera2MyLocation======");

		LatLng locData = ac.getMyLocation();

		moveCameraWithAnim(locData.latitude, locData.longitude);

		return locData;
	}

	public void onEventMainThread(Event e) {

		Log.d(TAG, "======onEventMainThread======"
				+ e.getClass().getSimpleName());

		if (e instanceof VpItemSelectedEvent) {
			final VpItemSelectedEvent ev = (VpItemSelectedEvent) e;

			if (scaleAnim != null && scaleAnim.isRunning()) {
				Log.d(TAG, "======onPageSelected====== cancel");
				scaleAnim.cancel();
			}

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					vpItemSelected(ev.index);
				}
			}, 100);
		}

		if (e instanceof AlarmEvent) {
			AlarmEvent ev = (AlarmEvent) e;
			if (ev.isAlarm) {
				if (mHotelList.size() > 0) {
					updateHotelPrice(mHotelList);
				}
			}
		}

		if (e instanceof ReqLocEvent) {
			ReqLocEvent ev = (ReqLocEvent) e;
			if (ev.isReqLoc) {
				reqLoc();
			}
		}

		if (e instanceof LocationChangedEvent) {

			LocationChangedEvent ev = (LocationChangedEvent) e;

			if (ev.isChange) {
				ivExitSearch.setVisibility(View.INVISIBLE);
				ac.isSearch = false;
				ac.isSelectOneHotel = false;
				ac.searchKeyword = null;
				tvKeyword.setText("");

				pb.setVisibility(View.GONE);

				LatLng myLocation = moveCamera2MyLocation();
				ac.lastChooseLoc = myLocation;

				if (!isFirstLoadData) {
					isJustLocChange = true;
				}

				// 更新我的位置
				MyLocationData myLocationData = new MyLocationData.Builder()
						.latitude(myLocation.latitude)
						.longitude(myLocation.longitude).build();
				mMapController.setMyLocationData(myLocationData);

				ac.lastChooseCity = ac.lastLocCity;

				initData(myLocation.latitude, myLocation.longitude);
			}
		}

		if (e instanceof LocFailEvent) {
			LocFailEvent ev = (LocFailEvent) e;
			if (ev.isFail) {
				pb.setVisibility(View.GONE);

				AppConfig appConfig = AppConfig.getAppConfig(ac);
				String lastLat = appConfig.get(Keys.LAST_LATITUDE);
				String lastLng = appConfig.get(Keys.LAST_LONGITUDE);

				double latitude;
				double longitude;
				// 移动到上一次主动选择的位置搜索
				if (ac.lastChooseLoc != null) {

					latitude = ac.lastChooseLoc.latitude;
					longitude = ac.lastChooseLoc.longitude;

					AppContext.toastShowException("定位失败，以上一次选择位置搜索酒店");
				} else if (lastLat != null && lastLng != null) {
					// 若没主动选择的位置，则以上一次定位位置搜索

					latitude = StringUtils.toDouble(lastLat);
					longitude = StringUtils.toDouble(lastLng);

					AppContext.toastShowException("定位失败，以上一次定位位置搜索酒店");
				} else {
					// TODO 是否提示用户选择城市
					// 定位失败，也无上一次位置记录，则获取北京数据

					latitude = Constants.BEIJING.latitude;
					longitude = Constants.BEIJING.longitude;

					AppContext.toastShowException("定位失败，默认搜索北京酒店");
				}

				moveCameraWithAnim(latitude, longitude);
				initData(latitude, longitude);
			}
		}

	}

	private void vpItemSelected(int index) {
		// if (lastScaleIndex != -1) {
		// // 将上一次放大的marker复原
		//
		// if (mHotelList.size() == 0
		// || lastScaleIndex > mHotelList.size() - 1) {
		// return;
		// }
		//
		// Item lastScaleHotel = mHotelList.get(lastScaleIndex);
		//
		// Marker lastScaleMarker = getMatchMarker(lastScaleHotel.hotelsId);
		//
		// // if (lastScaleMarker.getExtraInfo()
		// // .getBoolean(Keys.IS_SCALE)) {
		//
		// View lastScaleVMarker = makePriceMarkerView(lastScaleHotel, false);
		// BitmapDescriptor fromView = BitmapDescriptorFactory
		// .fromView(lastScaleVMarker);
		// lastScaleMarker.setIcon(fromView);
		// lastScaleMarker.setZIndex(lastScaleIndex);
		// lastScaleMarker.getExtraInfo().putBoolean(Keys.IS_SCALE, false);
		// // }
		//
		// }

		// if (ac.isSearch) {
		// return;
		// }

		for (int i = 0; i < mMarkerList.size(); i++) {
			Marker marker = mMarkerList.get(i);
			if (marker.getExtraInfo().getBoolean(Keys.IS_SCALE)) {

				Item lastScaleHotel = getMatchHotel(marker);

				View lastScaleVMarker = makePriceMarkerView(lastScaleHotel,
						false);
				BitmapDescriptor fromView = BitmapDescriptorFactory
						.fromView(lastScaleVMarker);
				marker.setIcon(fromView);
				marker.setZIndex(i);
				marker.getExtraInfo().putBoolean(Keys.IS_SCALE, false);
			}
		}

		scaleAnim4Marker(index);
	}

	private void selectOneHotel(String hotelId) {
		ac.isSearch = true;
		ac.isSelectOneHotel = true;

		showLayoutKeyword();

		final Item hotel = MyDbHelper.getInstance(activity).queryHotelById(
				hotelId, ac.lastReqLatitude, ac.lastReqLongitude);

		mHotelList.clear();
		mHotelList.add(hotel);

		List<Item> data = mHotelList;

		activity.notifyVpAdapterData(data);
		reDrawMarkers(data);

		activity.layoutBottomPanelMap.setVisibility(View.VISIBLE);

		LatLng LatLng = new LatLng(hotel.hotelsLatitude, hotel.hotelsLongitude);
		ac.lastChooseLoc = LatLng;

		new CountDownTimer(200, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				Log.d(TAG, "======CountDownTimer====== onTick");
			}

			@Override
			public void onFinish() {

				moveCameraWithAnim(hotel.hotelsLatitude - 0.006,
						hotel.hotelsLongitude);
			}
		}.start();

		updateHotelPrice(data);
	}

	private void reqLastMapMoveLocData() {
		pb.setVisibility(View.GONE);

		double latitude;
		double longitude;

		if (curtCenterLoc != null) {
			latitude = curtCenterLoc.latitude;
			longitude = curtCenterLoc.longitude;
		} else if (ac.lastChooseLoc != null) {
			latitude = ac.lastChooseLoc.latitude;
			longitude = ac.lastChooseLoc.longitude;
		} else if (ac.myLocationBD != null) {
			// latitude = ac.myLocation.getLatitude();
			// longitude = ac.myLocation.getLongitude();
			latitude = ac.myLocationBD.getLatitude();
			longitude = ac.myLocationBD.getLongitude();
		} else {
			AppConfig appConfig = AppConfig.getAppConfig(ac);
			String lastLat = appConfig.get(Keys.LAST_LATITUDE);
			String lastLng = appConfig.get(Keys.LAST_LONGITUDE);

			latitude = StringUtils.toDouble(lastLat);
			longitude = StringUtils.toDouble(lastLng);
		}
		LatLng LatLng = new LatLng(latitude, longitude);
		ac.lastChooseLoc = LatLng;

		initData(LatLng.latitude, LatLng.longitude);
	}

	private void keyWordSearch() {
		showLayoutKeyword();
		ac.isSearch = true;
		ac.isJustSearch = true;

		double latitude;
		double longitude;

		if (curtCenterLoc != null) {
			latitude = curtCenterLoc.latitude;
			longitude = curtCenterLoc.longitude;
		} else if (ac.lastChooseLoc != null) {
			latitude = ac.lastChooseLoc.latitude;
			longitude = ac.lastChooseLoc.longitude;
		} else if (ac.myLocationBD != null) {
			latitude = ac.myLocationBD.getLatitude();
			longitude = ac.myLocationBD.getLongitude();
		} else {
			AppConfig appConfig = AppConfig.getAppConfig(ac);
			String lastLat = appConfig.get(Keys.LAST_LATITUDE);
			String lastLng = appConfig.get(Keys.LAST_LONGITUDE);

			latitude = StringUtils.toDouble(lastLat);
			longitude = StringUtils.toDouble(lastLng);
		}

		LatLng LatLng = new LatLng(latitude, longitude);
		ac.lastChooseLoc = LatLng;

		isFirstKeywordsSearch = true;
		mHotelList.clear();

		// 清空地图marker
		removeMarkers();

		initDataByKeywords(latitude, longitude, ac.searchKeyword);
	}

	private void showLayoutKeyword() {
		tvKeyword.setText(ac.searchKeyword);
	}

	/**
	 * // 反Geo搜索Geo
	 * 
	 * @param latitude
	 * @param longitude
	 * @param isMapMove
	 *            是否地图移动后进行的
	 */
	private void decodeCityByLatLng(double latitude, double longitude,
			final boolean isMapMove) {

		if (ac.myLocationBD == null) {
			return;
		}

		Log.d(TAG, "======decodeCityByLatLng====== lat=" + latitude + ",lng="
				+ longitude);

		if (mSearch != null) {
			mSearch.destroy();
			mSearch = null;
		}

		// 初始化搜索模块，注册事件监听；为了解决切换城市后，再次Geo搜索不回调的问题，只能在这里每次都初始化一个MKSearch
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

				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR) {
					Log.d(TAG,
							"======mSearch====== onGetReverseGeoCodeResult error= "
									+ result.error);
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

					Log.d(TAG, "======decodeCityNameByLatLng======" + cityName);

					City city = new City(cityName,
							result.getLocation().latitude,
							result.getLocation().longitude);
					city.cityId = activity.dbHelp
							.queryCityIdByCityName(cityName);

					if (!isMapMove) {
						ac.lastLocCity = city;
					} else {
						ac.lastChooseCity = city;
					}

					DecodeCityNameByLatLngEvent event = new DecodeCityNameByLatLngEvent();
					event.city = city;
					EventBus.getDefault().post(event);
				}
			}
		});

		LatLng ptCenter = new LatLng(latitude, longitude);
		boolean reverseGeoCode = mSearch
				.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));

		Log.d(TAG, "======mSearch====== reverseGeocode= " + reverseGeoCode);
	}

	/**
	 * 移动地图中心
	 * 
	 * @param latitude
	 * @param longitude
	 */
	private void moveCameraWithAnim(double latitude, double longitude) {
		Log.d(TAG, "======moveCameraWithAnim====== lat=" + latitude + ",lng="
				+ longitude);

		// isLocBtnClickMapMove = true;

		// mMapController.setZoom(mapZoomDefault);

		curtCenterLoc = new LatLng(latitude, longitude);
		mMapController.animateMapStatus(MapStatusUpdateFactory
				.newLatLng(curtCenterLoc));
	}

	private void moveCamera(double latitude, double longitude) {
		Log.d(TAG, "======moveCamera====== lat=" + latitude + ",lng="
				+ longitude);
		// isLocBtnClickMapMove = true;

		// mMapController.setZoom(mapZoomDefault);

		curtCenterLoc = new LatLng(latitude, longitude);

		mMapController.setMapStatus(MapStatusUpdateFactory
				.newLatLng(curtCenterLoc));

		// mMapController.setCenter(new GeoPoint((int) (latitude * 1E6),
		// (int) (longitude * 1E6)));
	}

	private static final int WHAT_EXCEPTION = -1;
	private static final int WHAT_INIT_HOTEL_PAGE_DATA = 1;
	protected static final int WHAT_UPDATE_HOTEL_PRICE = 2;
	protected static final int WHAT_EXCEPTION_KEYWORD = -3;

	private static final long MAP_DEFAULT_ANIMATION_TIME = 300;

	private MyHandler myHandler = new MyHandler(this);

	private ThreadInterrupt threadUpdateHotelPrice;

	private boolean isNotRealLocData;

	private View viewMarker;

	private AnimatorSet scaleAnim;

	// protected CountDownTimer mapMoveTimer;

	static class MyHandler extends Handler {
		private WeakReference<BMapFragment> mWr;

		public MyHandler(BMapFragment frag) {
			super();
			this.mWr = new WeakReference<BMapFragment>(frag);
		}

		@Override
		public void handleMessage(Message msg) {
			final BMapFragment frag = mWr.get();

			// 解决bug:Fragment被销毁了导致一些空指针异常、或者fragment not attached to
			// activity异常；原因是Fragment被销毁或者deatach后，线程才返回结果；最好的解决办法应该是，在Fragment被销毁时结束线程请求
			if (frag == null || !frag.isAdded()) {
				return;
			}

			switch (msg.what) {
			case WHAT_INIT_HOTEL_PAGE_DATA:
				frag.pb.setVisibility(View.GONE);
				// frag.ivTransMask.setVisibility(View.GONE);
				ItemPage q = (ItemPage) msg.obj;

				if (q.rspMsg.OK()) {
					if (q.list.size() == 0) {

						if (frag.ac.isSearch) {

							frag.ac.mHotelListShowChange = false;

							if (frag.mHotelList.size() == 0) {
								frag.activity.layoutBottomPanelMap
										.setVisibility(View.GONE);
							}else{
								frag.activity.layoutBottomPanelMap.setVisibility(View.VISIBLE);
							}

							if (frag.ac.isJustSearch) {
								frag.ivLocation.performClick();
								AppContext
										.toastShow(R.string.search_tip_no_match);
							}

						} else {
							AppContext
									.toastShow(R.string.map_tip_this_loc_no_hotel);

							frag.mHotelList.clear();

							frag.removeMarkers();

							frag.ac.mHotelListShowChange = true;

							frag.activity.layoutBottomPanelMap
									.setVisibility(View.GONE);
						}

					} else {
						frag.updateHotelPrice(q.list);

						if (frag.ac.isSearch) {

							if (frag.isFirstKeywordsSearch) {
								frag.isFirstKeywordsSearch = false;

								frag.removeMarkers();
							}

							for (int i = 0; i < q.list.size(); i++) {
								Item item = q.list.get(i);

								frag.drawMarker(item, i);
							}

							Log.d(TAG,
									"======handler isSearch initData q.list.size="
											+ q.list.size());
							Log.d(TAG,
									"======handler isSearch initData q.frag.mHotelList="
											+ frag.mHotelList.size());

							frag.mHotelList.addAll(0, q.list);

						} else {

							frag.mHotelList.clear();
							frag.mHotelList = q.list;

							frag.reDrawMarkers(frag.mHotelList);
						}

						frag.moveCamera(q.reqLatitude, q.reqLongitude);

						frag.ac.mHotelListShowChange = true;
						frag.activity.layoutBottomPanelMap
								.setVisibility(View.VISIBLE);

						frag.activity.supportInvalidateOptionsMenu();

						if (frag.isFirstLoadData || frag.isNotRealLocData) {
							frag.isFirstLoadData = false;

							if (frag.ac.myLocationBD != null) {
								frag.isNotRealLocData = false;
							}

							new CountDownTimer(400, 1000) {

								@Override
								public void onTick(long millisUntilFinished) {
									Log.d(TAG,
											"======CountDownTimer====== onTick");
								}

								@Override
								public void onFinish() {
									Log.d(TAG,
											"======CountDownTimer====== onFinish");
									frag.zoomResize();
								}
							}.start();
							Log.d(TAG,
									"======CountDownTimer====== isFirstLoadData");

						} else if (frag.ac.isJustCityChange) {
							frag.ac.isJustCityChange = false;
							frag.zoomResize();
						} else if (frag.isJustLocChange) {
							frag.isJustLocChange = false;
							frag.zoomResize();
						} else if (frag.ac.isJustSearch) {
							frag.ac.isJustSearch = false;
							frag.zoomResize();
						}

						Log.d(TAG,
								"=====handle init data frag.mHotelList.size="
										+ frag.mHotelList.size());
						frag.activity.notifyVpAdapterData(frag.mHotelList);
						if (frag.activity.vp.getCurrentItem() == 0) {
							frag.vpItemSelected(0);
						} else {
							frag.activity.vp.setCurrentItem(0, true);
						}

					}
				} else {
					frag.ac.isSearch = false;
					frag.ac.isSelectOneHotel = false;
					frag.ac.searchKeyword = null;
					frag.activity.setTitle(frag.activity.mTitle);

					AppContext.toastShowException(q.rspMsg.message);
					frag.moveCamera2MyLocation();
				}

				break;
			case WHAT_UPDATE_HOTEL_PRICE:

				ItemPage hotelPricePage = (ItemPage) msg.obj;

				if (hotelPricePage.rspMsg.OK()
						&& hotelPricePage.list.size() > 0) {

					Iterator<Item> iterator = frag.mHotelList.iterator();

					// 将价格等酒店销售状态更新到酒店集合中；当午夜房、零时放价格为空时，从酒店集合中移除
					while (iterator.hasNext()) {
						Item item = iterator.next();

						for (int j = 0; j < hotelPricePage.list.size(); j++) {
							Item itemUpdate = hotelPricePage.list.get(j);

							if (item.hotelsId.equals(itemUpdate.hotelsId)) {

								item.isHour = itemUpdate.isHour;
								item.isSpecial = itemUpdate.isSpecial;
								item.isMidNight = itemUpdate.isMidNight;
								item.hasTuan = itemUpdate.hasTuan;
								item.zdfDurationType = itemUpdate.zdfDurationType;

								if (Room.isSellZdf()) {
									if (!StringUtils
											.isEmpty(itemUpdate.hourroomPrice)) {
										item.hourroomPrice = itemUpdate.hourroomPrice;
									} else {
										// iterator.remove();
									}

									if (!StringUtils.isEmpty(itemUpdate.hours)) {
										item.hours = itemUpdate.hours;
									}
								}

								if (Room.isSellWyf()) {
									if (!StringUtils
											.isEmpty(itemUpdate.dayroomPrice)) {
										item.dayroomPrice = itemUpdate.dayroomPrice;
									} else {
										// 午夜房更新价格为空时，移除此酒店
										iterator.remove();
									}

									if (!StringUtils
											.isEmpty(itemUpdate.daySalePrice)) {
										item.daySalePrice = itemUpdate.daySalePrice;
									}
								}

								if (Room.isSellLsf()) {
									if (!StringUtils
											.isEmpty(itemUpdate.nightroomPrice)) {
										item.nightroomPrice = itemUpdate.nightroomPrice;
									} else {
										// 零时房更新价格为空时，移除此酒店
										iterator.remove();
									}

									if (!StringUtils
											.isEmpty(itemUpdate.nightSalePrice)) {
										item.nightSalePrice = itemUpdate.nightSalePrice;
									}
								}

								break;
							}
						}
					}

					// HotelMarkerPriceUpdateEvent event = new
					// HotelMarkerPriceUpdateEvent();
					// event.isUpdate = true;
					// EventBus.getDefault().post(event);

					frag.activity.notifyVpAdapterData(frag.mHotelList);

					if (frag.mHotelList.size() == 0) {
						frag.activity.layoutBottomPanelMap
								.setVisibility(View.GONE);
					}

					// 更新 地图marker
					// 当午夜房、零时放价格为空时，从地图上移除marker
					// 当更新价格与之前marker上的价格不等时才 更新marker
					Iterator<Marker> iteratorMarker = frag.mMarkerList
							.iterator();
					for (int i = 0; i < hotelPricePage.list.size(); i++) {
						Item itemUpdatePrice = hotelPricePage.list.get(i);

						while (iteratorMarker.hasNext()) {
							Marker marker = iteratorMarker.next();

							Bundle hotelBundle = marker.getExtraInfo();

							String hotelIdMarker = hotelBundle
									.getString(Keys.ID);
							String brandNameMarker = hotelBundle
									.getString(Keys.BRAND_NAME);
							String priceMarker = hotelBundle
									.getString(Keys.PRICE);
							boolean hasTuanMarker = hotelBundle.getBoolean(
									Keys.HAS_TUAN, false);

							if (itemUpdatePrice.hotelsId.equals(hotelIdMarker)) {

								if (!StringUtils.isEmpty(brandNameMarker)) {
									itemUpdatePrice.brandName = brandNameMarker;
								}

								boolean isNeedTempRemove = false;
								boolean isNeenUpdate = false;
								// 暂时无法取得钟点房价格
								if (Room.isSellZdf()) {
									if (StringUtils
											.isEmpty(itemUpdatePrice.hourroomPrice)) {
										isNeedTempRemove = false;
									} else if (!itemUpdatePrice.hourroomPrice
											.equals(priceMarker)) {
										isNeenUpdate = true;
									}

									if (hasTuanMarker != itemUpdatePrice.hasTuan) {
										isNeenUpdate = true;
									}
								}

								// 暂时无法取得午夜房价格
								if (Room.isSellWyf()) {
									if (StringUtils
											.isEmpty(itemUpdatePrice.dayroomPrice)) {
										isNeedTempRemove = true;
									}
									if (!itemUpdatePrice.dayroomPrice
											.equals(priceMarker)) {
										isNeenUpdate = true;
									}

								}

								// 暂时无法取得零时房价格
								if (Room.isSellLsf()) {
									if (StringUtils
											.isEmpty(itemUpdatePrice.nightroomPrice)) {
										isNeedTempRemove = true;
									}
									if (!itemUpdatePrice.nightroomPrice
											.equals(priceMarker)) {
										isNeenUpdate = true;
									}
								}

								if (isNeedTempRemove) {
									// 如果更新价格为空，从地图上删除marker
									try {
										Log.d(TAG,
												"======WHAT_UPDATE_HOTEL_PRICE====== remove marker = "
														+ brandNameMarker);
										marker.remove();
										iteratorMarker.remove();
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else if (isNeenUpdate) {
									Log.d(TAG,
											"======WHAT_UPDATE_HOTEL_PRICE====== update marker = "
													+ brandNameMarker);

									// 当获取的更新价格与之前marker上的价格不等时才 更新marker
									frag.updateMarker(marker, itemUpdatePrice);
								}

								break;
							}

						}
					}

				} else {
					// TODO 隐藏没有价格的午夜房
					AppContext
							.toastShowException(hotelPricePage.rspMsg.message);

					frag.hideEmptyPriceHotel();

				}

				break;

			case -WHAT_UPDATE_HOTEL_PRICE:
				((AppException) msg.obj).makeToast(frag.ac);
				frag.hideEmptyPriceHotel();

				break;
			case WHAT_EXCEPTION:
				frag.pb.setVisibility(View.GONE);
				// frag.ivTransMask.setVisibility(View.GONE);
				((AppException) msg.obj).makeToast(frag.ac);
				break;

			case WHAT_EXCEPTION_KEYWORD:
				frag.pb.setVisibility(View.GONE);

				frag.ac.isJustSearch = false;
				frag.ac.isSearch = false;
				frag.ac.isSelectOneHotel = false;
				frag.ac.searchKeyword = null;
				frag.activity.setTitle(frag.activity.mTitle);

				((AppException) msg.obj).makeToast(frag.ac);
				break;

			default:
				break;
			}
		}
	}

	private void initData(final double latitude, final double longitude) {
		ac.lastReqLatitude = latitude;
		ac.lastReqLongitude = longitude;

		if (ac.myLocationBD == null) {
			isNotRealLocData = true;
		}

		// 判断是否有缓存数据
		ItemPage cacheData;

		// if (isFirstLoadData || isJustCityChange) {
		//
		// if (ac.lastChooseCity != null && ac.lastChooseCity.cityId != null) {
		//
		// Log.d(TAG, "======initData====== cityId="
		// + ac.lastChooseCity.cityId);
		//
		// cacheData = activity.dbHelp.queryHotelPageInCityByLatLng(
		// latitude, longitude, ac.lastChooseCity.cityId);
		// } else {
		// cacheData = activity.dbHelp.queryHotelPage20KmByLatLng(
		// latitude, longitude);
		// }
		//
		// } else {

		if (ac.lastChooseCity != null && ac.lastChooseCity.cityId != null) {
			cacheData = activity.dbHelp.queryHotelPageInCityByLatLng(latitude,
					longitude, ac.lastChooseCity.cityId);
		} else {
			cacheData = activity.dbHelp.queryHotelPage20KmByLatLng(latitude,
					longitude);
		}

		// }

		cacheData.reqLatitude = latitude;
		cacheData.reqLongitude = longitude;

		Message msg = new Message();
		msg.what = WHAT_INIT_HOTEL_PAGE_DATA;
		msg.obj = cacheData;

		myHandler.sendMessage(msg);
	}

	public void hideEmptyPriceHotel() {
		Iterator<Item> iterator = mHotelList.iterator();

		// 将价格等酒店销售状态更新到酒店集合中；当午夜房、零时放价格为空时，从酒店集合中移除
		while (iterator.hasNext()) {
			Item item = iterator.next();

			if (Room.isSellWyf()) {

				if (StringUtils.isEmpty(item.dayroomPrice)) {
					// 午夜房更新价格为空时，移除此酒店
					iterator.remove();
				}

			}

			if (Room.isSellLsf()) {

				if (StringUtils.isEmpty(item.nightroomPrice)) {
					// 零时房更新价格为空时，移除此酒店
					iterator.remove();

				}
			}
		}

		activity.notifyVpAdapterData(mHotelList);

		if (mHotelList.size() == 0) {
			activity.layoutBottomPanelMap.setVisibility(View.GONE);
		}

		Iterator<Marker> iteratorMarker = mMarkerList.iterator();

		while (iteratorMarker.hasNext()) {
			Marker marker = iteratorMarker.next();

			Bundle hotelBundle = marker.getExtraInfo();

			// String hotelIdMarker = hotelBundle.getString(Keys.ID);
			String brandNameMarker = hotelBundle.getString(Keys.BRAND_NAME);
			String priceMarker = hotelBundle.getString(Keys.PRICE);

			// 暂时无法取得午夜房\零时房价格
			if (Room.isSellWyf() || Room.isSellLsf()) {
				if (StringUtils.isEmpty(priceMarker)) {
					try {
						Log.d(TAG,
								"======hideEmptyPriceHotel====== remove marker = "
										+ brandNameMarker);

						iteratorMarker.remove();
						marker.remove();
						marker.getIcon().recycle();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}

		}

	}

	public MarkerOptions makeMarker(Item item, int index) {

		BitmapDescriptor bd = BitmapDescriptorFactory
				.fromView(makePriceMarkerView(item));
		MarkerOptions overlayOptions = new MarkerOptions()
				.position(new LatLng(item.hotelsLatitude, item.hotelsLongitude))
				.icon(bd).zIndex(index);

		// 在marker上记录信息，以便在更新价格时使用
		String price = "0";
		if (Room.isSellZdf()) {

			if (!StringUtils.isEmpty(item.hourroomPrice)) {
				price = item.hourroomPrice;
			}

		} else if (Room.isSellWyf()) {
			// 当前暂时无法取得午夜房价格
			if (!StringUtils.isEmpty(item.dayroomPrice)) {
				price = item.dayroomPrice;
			}
		} else if (Room.isSellLsf()) {
			// 当前暂时无法取得零时房价格
			if (!StringUtils.isEmpty(item.nightroomPrice)) {
				price = item.nightroomPrice;
			}
		}

		Bundle bundle = new Bundle();
		bundle.putString(Keys.ID, item.hotelsId);
		bundle.putString(Keys.BRAND_NAME, item.brandName);
		bundle.putString(Keys.PRICE, price);
		bundle.putBoolean(Keys.HAS_TUAN, item.hasTuan);

		overlayOptions.extraInfo(bundle);

		return overlayOptions;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private View makePriceMarkerView(Item item) {
		return makePriceMarkerView(item, false);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private View makePriceMarkerView(Item item, boolean scale) {

		if (viewMarker == null) {
			viewMarker = inflater.inflate(R.layout.map_marker_price, layoutMap,
					false);
		}

		// ImageView ivMarkerBg = (ImageView) viewMarker
		// .findViewById(R.id.ivMarkerBg);
		final ImageView ivBusinessType = (ImageView) viewMarker
				.findViewById(R.id.ivBusinessType);
		ImageView ivMarkerPoint = (ImageView) viewMarker
				.findViewById(R.id.ivMarkerPoint);
		LinearLayout layoutMarker = (LinearLayout) viewMarker
				.findViewById(R.id.layoutMarker);
		View layoutPrice = viewMarker.findViewById(R.id.layoutPrice);
		TextView tvPrice = (TextView) viewMarker.findViewById(R.id.textView2);
		TextView tvName = (TextView) viewMarker.findViewById(R.id.textView3);

		if (scale) {
			ivMarkerPoint.setImageResource(R.drawable.ic_map_marker_true);
			layoutMarker
					.setBackgroundResource(R.drawable.ic_map_marker_price_true);
		} else {
			ivMarkerPoint.setImageResource(R.drawable.ic_map_marker);
			layoutMarker.setBackgroundResource(R.drawable.ic_map_marker_price);
		}

		// 午夜房 显示marker为足球
		if (Room.isSellWyf() || Room.isSellLsf()) {

			AppConfig appConfig = AppConfig.getAppConfig(ac);
			boolean isShowStartImg = StringUtils.toBool(appConfig
					.get(Keys.START_IMG_SHOW));

			String markerImgUrl = appConfig.get(Keys.MARKER_IMG);
			String markerSmallImgUrl = appConfig.get(Keys.MARKER_SMALL_IMG);
			String markerImgBeginTime = appConfig
					.get(Keys.MARKER_IMG_BEGIN_TIME);
			String markerImgEndTime = appConfig.get(Keys.MARKER_IMG_END_TIME);

			File startImgCache = DiskCacheUtils.findInCache(URLs.URL_ZDF_API
					+ markerImgUrl, ac.mImageLoader.getDiskCache());
			File startSmallImgCache = DiskCacheUtils.findInCache(
					URLs.URL_ZDF_API + markerSmallImgUrl,
					ac.mImageLoader.getDiskCache());

			// 是否显示配置图片，是否在有效气馁，图片是否存在
			if (isShowStartImg
					&& DateUtil
							.isLiveDate(markerImgBeginTime, markerImgEndTime)) {

				if (startImgCache != null) {
					Bitmap markerBitmap = ac.mImageLoader.loadImageSync(
							"file://" + startImgCache, ac.optionsLogo);

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						layoutMarker.setBackground(ImageUtils.bitmapToDrawable(
								getResources(), markerBitmap));
					} else {
						layoutMarker
								.setBackgroundDrawable(ImageUtils
										.bitmapToDrawable(getResources(),
												markerBitmap));
					}

					// ac.mImageLoader.displayImage(URLs.URL_ZDF_API
					// + markerImgUrl, ivMarkerBg, ac.optionsLogo);
				}

				if (startSmallImgCache != null) {
					ac.mImageLoader.displayImage(URLs.URL_ZDF_API
							+ markerSmallImgUrl, ivBusinessType,
							ac.optionsLogo, new ImageLoadingListener() {

								@Override
								public void onLoadingStarted(String imageUri,
										View view) {
								}

								@Override
								public void onLoadingFailed(String imageUri,
										View view, FailReason failReason) {
								}

								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									ivBusinessType.setVisibility(View.VISIBLE);
								}

								@Override
								public void onLoadingCancelled(String imageUri,
										View view) {
								}
							});
				}
			}
		}

		if (DisplayUtil.isLowDpi(activity)) {
			if (item.brandName.length() <= 2) {
				layoutMarker.getLayoutParams().width = DisplayUtil.dip2px(
						activity, 55);
			} else {
				layoutMarker.getLayoutParams().width = DisplayUtil.dip2px(
						activity, 68);
			}
			layoutMarker.getLayoutParams().height = DisplayUtil.dip2px(
					activity, 48);
			ivBusinessType.getLayoutParams().height = DisplayUtil.dip2px(
					activity, 21);
			ivBusinessType.getLayoutParams().width = DisplayUtil.dip2px(
					activity, 21);

			tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
					.getDimension(R.dimen.textSize_3));
		}

		tvName.setText(item.brandName);

		if (Room.isSellZdf()) {
			if (!StringUtils.isEmpty(item.hourroomPrice)) {
				tvPrice.setText(item.hourroomPrice);
				layoutPrice.setVisibility(View.VISIBLE);
			} else {
				layoutPrice.setVisibility(View.INVISIBLE);
			}

			if (item.hasTuan) {
				ivBusinessType.setVisibility(View.VISIBLE);
			}

		} else if (Room.isSellWyf()) {
			// 当前暂时无法取得午夜房价格
			if (!StringUtils.isEmpty(item.dayroomPrice)) {
				tvPrice.setText(item.dayroomPrice);
				layoutPrice.setVisibility(View.VISIBLE);
			} else {
				layoutPrice.setVisibility(View.INVISIBLE);
			}
		} else if (Room.isSellLsf()) {
			// 当前暂时无法取得零时房价格
			if (!StringUtils.isEmpty(item.nightroomPrice)) {
				tvPrice.setText(item.nightroomPrice);
				layoutPrice.setVisibility(View.VISIBLE);
			} else {
				layoutPrice.setVisibility(View.INVISIBLE);
			}
		}

		return viewMarker;
	}

	/**
	 * 调整缩放级别以容下当前显示的所有数据
	 * 
	 */
	public void zoomResize() {

		if (mMarkerList != null && mHotelList.size() > 0) {

			if (activity.screenWidth == 0 || activity.screenHight == 0) {
				activity.statusBarHeight = DisplayUtil
						.getStatusBarHeight(activity);
				activity.screenHight = DisplayUtil.getScreenHight(activity)
						- activity.statusBarHeight;
				activity.screenWidth = DisplayUtil.getScreenWidth(activity);
			}

			// 方法一
			LatLngBounds.Builder builder = new LatLngBounds.Builder();

			for (int i = 0; i < mHotelList.size(); i++) {
				Item item = mHotelList.get(i);

				builder.include(new LatLng(item.hotelsLatitude,
						item.hotelsLongitude));
			}

			LatLngBounds bounds = builder.build();
			mMapController.setMapStatus(MapStatusUpdateFactory
					.newLatLngBounds(bounds));

			// 方法二
			// Item itemMax = mHotelList.get(mHotelList.size() - 1);
			//
			// // 离当前中心最远的一点
			// LatLng pointMax = new LatLng(itemMax.hotelsLatitude,
			// itemMax.hotelsLongitude);
			//
			// Projection projection = mMapController.getProjection();
			// Point point = new Point();
			//
			// try {
			// if (projection == null) {
			// Log.d(TAG, "======zoomResize====== projection==null");
			// }
			// point = projection.toScreenLocation(pointMax);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			//
			// Log.d(TAG, "======zoomResize====== point:=" + point.x + ","
			// + point.y + " ,screenWidth=" + activity.screenWidth
			// + " ,screenHight=" + activity.screenHight);
			//
			// boolean contains = true;
			//
			// // 这一点是否在屏幕内
			// if (point.x < 0 || point.x > activity.screenWidth || point.y < 0
			// || point.y > activity.screenHight) {
			// contains = false;
			// }
			//
			// if (!contains) {
			//
			// mMapController.setMapStatus(MapStatusUpdateFactory.zoomOut());
			//
			// // zoomResize();
			//
			// new Handler().postDelayed(new Runnable() {
			// @Override
			// public void run() {
			// zoomResize();
			// }
			// // }, MAP_DEFAULT_ANIMATION_TIME);
			// }, 0);
			// }

		}

		// 方法三
		// 这个方法不好用，地图总是被缩放到了最小
		// mMarkersOverlay.zoomToSpan();

	}

	/**
	 * 关键字搜索
	 * 
	 * @param latitude
	 * @param longitude
	 * @param keyword
	 */
	private void initDataByKeywords(final double latitude,
			final double longitude, final String keyword) {

		if (ac.lastChooseCity == null) {
			Log.d(TAG,
					"======getDataByKeywords====== ac.curtChooseCity == null");
			return;
		}
		if (ac.lastChooseCity.cityId == null) {
			Log.d(TAG,
					"======getDataByKeywords====== ac.curtChooseCity.cityId == null");
			return;
		}

		// 数据库获取
		new Thread() {
			public void run() {

				ItemPage cacheData = ac.queryHotelPageInCityByKeyword(
						activity.dbHelp, latitude, longitude, keyword,
						ac.lastChooseCity.cityId);

				cacheData.reqLatitude = latitude;
				cacheData.reqLongitude = longitude;

				// if (mHotelList.size() == 0) {
				// mHotelList.addAll(cacheData.list);
				// } else {
				Log.d(TAG, "======initDataByKeywords======before cacheData="
						+ cacheData.list.size());
				Log.d(TAG,
						"======initDataByKeywords======before ac.mHotelList="
								+ mHotelList.size());

				Iterator<Item> newIterator = cacheData.list.iterator();
				while (newIterator.hasNext()) {
					Item newItem = newIterator.next();

					double distance = LatLngUtil.getDistance(latitude,
							longitude, newItem.hotelsLatitude,
							newItem.hotelsLongitude) / 1000;
					distance = RoundTool.round(distance, 1,
							BigDecimal.ROUND_HALF_UP);
					newItem.dist = "" + distance;

					for (int i = 0; i < mHotelList.size(); i++) {
						Item oldItem = mHotelList.get(i);

						distance = LatLngUtil
								.getDistance(latitude, longitude,
										oldItem.hotelsLatitude,
										oldItem.hotelsLongitude) / 1000;
						distance = RoundTool.round(distance, 1,
								BigDecimal.ROUND_HALF_UP);
						oldItem.dist = "" + distance;

						if (newItem.hotelsId.equals(oldItem.hotelsId)) {
							newIterator.remove();
							break;
						}
					}
					// }

					// mHotelList.addAll(cacheData.list);

					Log.d(TAG, "======initDataByKeywords======after cacheData="
							+ cacheData.list.size());
					Log.d(TAG,
							"======initDataByKeywords======after ac.mHotelList="
									+ mHotelList.size());

				}

				Message msg = new Message();
				msg.what = WHAT_INIT_HOTEL_PAGE_DATA;
				msg.obj = cacheData;

				myHandler.sendMessage(msg);

				// if (cacheData.list.size() > 0) {
				// updateHotelPrice(cacheData.list);
				// }

			};
		}.start();

	}

	/**
	 * 更新酒店价格，以及销售房间类型
	 * 
	 * @param list
	 */
	private void updateHotelPrice(final List<Item> list) {
		if (!ac.isNetworkConnected()) {
			AppContext.toastShow(R.string.pleaseCheckNet);
			return;
		}

		if (threadUpdateHotelPrice != null) {
			threadUpdateHotelPrice.stopMe();
		}

		threadUpdateHotelPrice = new ThreadInterrupt(list);
		threadUpdateHotelPrice.start();

	}

	/**
	 * 可终止业务逻辑的线程
	 * 
	 * @author lufei
	 * 
	 */
	class ThreadInterrupt extends Thread {

		private volatile boolean finished = false; // ① volatile条件变量
		private List<Item> list;

		public ThreadInterrupt(List<Item> list) {
			this.list = list;
		}

		public void stopMe() {
			finished = true; // ② 发出停止信号
			interrupt();
		}

		@Override
		public void run() {

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < list.size(); i++) {
				sb.append(list.get(i).hotelsId).append(",");
			}

			if (sb.length() == 0) {
				return;
			}

			String hotelIds = sb.deleteCharAt(sb.length() - 1).toString();

			Message msg = new Message();
			ItemPage q = new ItemPage();
			;
			try {

				q = ac.getHotelsPrice(hotelIds);

				msg.what = WHAT_UPDATE_HOTEL_PRICE;
				msg.obj = q;

			} catch (AppException e) {
				e.printStackTrace();
				msg.what = -WHAT_UPDATE_HOTEL_PRICE;

				msg.obj = e;
			}

			if (!finished) { // ③ 检测条件变量
				// do dirty work // ④业务代码
				myHandler.sendMessage(msg);

				// 更新数据库酒店价格
				if (q.rspMsg.OK()) {
					activity.dbHelp.updateHotelPrice(q.list);
				}
			}
		}

	}

	private void reDrawMarkers(List<Item> data) {
		// ivMapMask.setVisibility(View.GONE);

		Log.d(TAG, "======reDrawMarkers====== data.size()=" + data.size());

		removeMarkers();

		drawMarkers(data);
	}

	private void drawMarkers(List<Item> data) {

		for (int i = 0; i < data.size(); i++) {
			Item item = data.get(i);

			// MarkerOptions markerOption = makeMarker(item);
			// mMarkersOverlay.addMarkerOption(markerOption);

			drawMarker(item, i);
		}

	}

	private void drawMarker(Item hotel, int index) {
		MarkerOptions markerOption = makeMarker(hotel, index);

		Marker marker = (Marker) mMapController.addOverlay(markerOption);

		mMarkersOverlay.addMarkerOption(markerOption);
		mMarkerList.add(marker);
	}

	private void removeMarkers() {
		// mMarkersOverlay.removeFromMap();

		mMarkersOverlay.clear();

		mMapController.clear();

		for (int i = 0; i < mMarkerList.size(); i++) {
			mMarkerList.get(i).getIcon().recycle();
		}

		mMarkerList.clear();

		Log.d(TAG, "======removeMarkers====== size= "
				+ mMarkersOverlay.getOverlayOptions().size());
	}

	private void updateMarker(Marker marker, Item hotel) {

		Log.d(TAG, "======updateMarker======");

		BitmapDescriptor bd;
		if (marker.getExtraInfo().getBoolean(Keys.IS_SCALE)) {
			// TODO
			bd = getScaleBitmapDescriptor(makePriceMarkerView(hotel));

			Log.d(TAG, "======updateMarker====== scale bitmap");
		} else {
			bd = BitmapDescriptorFactory.fromView(makePriceMarkerView(hotel));
		}

		try {
			marker.getIcon().recycle();
			marker.setIcon(bd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取指定酒店id的marker
	 * 
	 * @param hotelsId
	 * @return
	 */
	private Marker getMatchMarker(String hotelsId) {
		int size = mMarkerList.size();
		for (int i = 0; i < size; i++) {
			Marker marker = mMarkerList.get(i);
			if (hotelsId.equals(marker.getExtraInfo().getString(Keys.ID))) {
				return marker;
			}
		}

		return null;
	}

	/**
	 * 获取指定酒店id的酒店
	 * 
	 * @param marker
	 * @return
	 */
	private Item getMatchHotel(Marker marker) {

		String hotelId = marker.getExtraInfo().getString(Keys.ID);

		int size = mHotelList.size();
		for (int i = 0; i < size; i++) {
			Item item = mHotelList.get(i);
			if (item.hotelsId.equals(hotelId)) {
				return item;
			}
		}

		return null;
	}

	/**
	 * 使marker看上去进行了缩放动画
	 * 
	 * @param index
	 *            地图上显示的酒店的索引
	 */
	private void scaleAnim4Marker(int index) {
		try {
			// 对marker进行放大动画
			Item hotel = mHotelList.get(index);

			Marker marker = getMatchMarker(hotel.hotelsId);

			if (marker == null) {
				return;
			}

			boolean isScaled = marker.getExtraInfo().getBoolean(Keys.IS_SCALE);
			if (isScaled) {

				Log.d(TAG, "======scaleAnim4Marker====== isScaled");
				return;
			}

			scaleAnim4Marker(hotel, marker);

			lastScaleIndex = index;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 使marker看上去进行了缩放动画
	 * 
	 * @param hotel
	 *            生成marker对应的酒店
	 * @param marker
	 *            地图上显示的marker
	 */
	public void scaleAnim4Marker(Item hotel, final Marker marker) {

		Log.d(TAG, "======scaleAnim4Marker======");

		// 生成view，还将用于展示缩放动画
		final View vMarkerAnim = makePriceMarkerView(hotel, true);

		final BitmapDescriptor scaleBitmapDescriptor = getScaleBitmapDescriptor(vMarkerAnim);

		// ======展示view的缩放动画
		// 生成view将要放在地图上位置参数
		LatLng latLng = new LatLng(hotel.hotelsLatitude, hotel.hotelsLongitude);
		Builder builder = new MapViewLayoutParams.Builder();
		builder.position(latLng).width(MapViewLayoutParams.WRAP_CONTENT)
				.height(MapViewLayoutParams.WRAP_CONTENT)
				.layoutMode(MapViewLayoutParams.ELayoutMode.mapMode);
		final MapViewLayoutParams geoLP = builder.build();

		// 让view在maeker的位置展示缩放动画
		scaleAnim = new AnimatorSet();

		scaleAnim.playTogether(ObjectAnimator.ofFloat(vMarkerAnim, "scaleX",
				1f, 1f + scaleXByValue), ObjectAnimator.ofFloat(vMarkerAnim,
				"scaleY", 1f, 1f + scaleYByValue), ObjectAnimator.ofFloat(
				vMarkerAnim, "translationY", 0, -heightInitMarker * 0.154f));
		scaleAnim.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

				mMapView.removeView(vMarkerAnim);

				// 隐藏原始marker，将view添加到marker的位置
				mMapView.addView(vMarkerAnim, geoLP);
				try {
					marker.setZIndex(mHotelList.size() + 1);
					marker.setVisible(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// set.removeAllListeners();

				// 显示原始marker，并设置放大后的图标
				try {
					marker.setVisible(true);
					// marker.setZIndex(mHotelList.size() + 1);
					marker.getIcon().recycle();
					marker.setIcon(scaleBitmapDescriptor);
					Bundle extraInfo = marker.getExtraInfo();
					extraInfo.putBoolean(Keys.IS_SCALE, true);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 延迟一小会再移除view，避免view与marker切换时明显的闪烁
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mMapView.removeView(vMarkerAnim);

						// 对view进行缩放动画后，view的尺寸也发生了变化；由于每次动画都是重用同一个view，所以需要在每次动画结束后将view尺寸复原
						animate(vMarkerAnim).setDuration(100)
								.scaleXBy(-scaleXByValue)
								.scaleYBy(-scaleYByValue).start();
					}
				}, 100);

			}

			@Override
			public void onAnimationCancel(Animator animation) {
				Log.d(TAG, "======scaleAnim4Marker====== canceled");
				marker.setZIndex(mHotelList.size());
				marker.setVisible(true);
				marker.getExtraInfo().putBoolean(Keys.IS_SCALE, false);
			}
		});

		scaleAnim.setDuration(500).start();
	}

	/**
	 * 生成放大后的要显示的 marker 图标
	 * 
	 * @param vMarkerAnim
	 * @return
	 */
	private BitmapDescriptor getScaleBitmapDescriptor(View vMarkerAnim) {
		// 将view转为bitmap
		Bitmap bitmapMarker = BMapUtil.getBitmapFromView(vMarkerAnim);
		// 以bitmap原尺寸计算目标尺寸
		widthInitMarker = bitmapMarker.getWidth();
		heightInitMarker = bitmapMarker.getHeight();
		float widthF = widthInitMarker * (1f + scaleXByValue);
		float heightF = heightInitMarker * (1f + scaleYByValue);
		// 生成目标尺寸的Bitmap
		Bitmap scaleBitmap = ImageUtils.scaleBitmap(bitmapMarker, widthF,
				heightF);
		// 生成目标尺寸的 marker图标
		BitmapDescriptor scaleBitmapDescriptor = BitmapDescriptorFactory
				.fromBitmap(scaleBitmap);

		return scaleBitmapDescriptor;
	}

	public class MarkersOverlay extends OverlayManager {

		private ArrayList<OverlayOptions> markerList = new ArrayList<OverlayOptions>();

		public MarkersOverlay(BaiduMap b) {
			super(b);
		}

		public void clear() {
			new Thread() {
				public void run() {
					for (int i = 0; i < markerList.size(); i++) {
						MarkerOptions markerOptions = (MarkerOptions) markerList
								.get(i);
						markerOptions.getIcon().recycle();
					}

					markerList.clear();
				};
			}.start();

			// for (int i = 0; i < markerList.size(); i++) {
			// MarkerOptions markerOptions = (MarkerOptions) markerList.get(i);
			// markerOptions.getIcon().recycle();
			// }
			// markerList.clear();
		}

		public void addMarkerOption(MarkerOptions markerOption) {
			markerList.add(markerOption);
		}

		@Override
		public List<OverlayOptions> getOverlayOptions() {
			return markerList;
		}

		@Override
		public boolean onMarkerClick(Marker marker) {
			// zoomResize();

			mCurMarker = marker;

			if (marker.getExtraInfo().getBoolean(Keys.IS_SCALE)) {
				String hotelIdMarker = marker.getExtraInfo().getString(Keys.ID);
				Item item = activity.dbHelp.queryHotelById(hotelIdMarker,
						ac.lastReqLatitude, ac.lastReqLongitude);

				UIHelper.showHotelActivity(activity, item);
			} else {
				Item hotel = getMatchHotel(marker);
				int index = mHotelList.indexOf(hotel);
				
				activity.vp.setScrollDurationFactor(2);

				activity.vp.setCurrentItem(index, true);
			}

			return true;
		}
	}

	protected void showHotel(Item item) {
		Intent intent = new Intent(activity, HotelDetailActivity.class);
		intent.putExtra(Keys.ITEM, item);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.slide_in_from_right,
				R.anim.slide_out_to_left);
	}

	long startTime;
	long costTime;

	// private boolean isFirstLoc = true;// 是否首次定位

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "======onSaveInstanceState======");
		super.onSaveInstanceState(outState);
		// if (mMapView != null) {
		// mMapView.onSaveInstanceState(outState);
		// }
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		Log.d(TAG, "======onViewStateRestored======");
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onResume() {
		Log.d(TAG, "======onResume======");
		if (mMapView != null) {
			mMapView.onResume();
			EventBus.getDefault().register(this);

			if (mMarkerList != null && mMarkerList.size() >= 0) {
				zoomResize();
			}

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (ac.isJustCityChange) {
						Log.d(TAG, "======onResume====== ac.isJustCityChange");

						moveCamera(ac.lastChooseCity.cityLat,
								ac.lastChooseCity.cityLng);
						initData(ac.lastChooseCity.cityLat,
								ac.lastChooseCity.cityLng);
					}

				}
			}, 100);

			// if (ac.lastChooseLoc != null) {
			// moveCamera(ac.lastChooseLoc.latitude,
			// ac.lastChooseLoc.longitude);
			// } else if (ac.lastChooseCity != null) {
			// moveCamera(ac.lastChooseCity.cityLat, ac.lastChooseCity.cityLng);
			// } else {
			// moveCamera2MyLocation();
			// }
			//
			// if (ac.isSearch && !ac.isSelectOneHotel) {
			// initDataByKeywords(curtCenterLoc.latitude,
			// curtCenterLoc.longitude, ac.searchKeyword);
			// } else {
			// initData(curtCenterLoc.latitude, curtCenterLoc.longitude);
			// }

			// if (mHotelList.size() > 0) {
			// reDrawMarkers(mHotelList);
			// zoomResize();
			//
			// if (lastScaleIndex != -1) {
			// scaleAnim4Marker(lastScaleIndex);
			// }
			// }

		}
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.d(TAG, "======onPause======");
		if (mMapView != null) {
			// mHotelList.clear();
			// removeMarkers();
			mMapView.onPause();
		}
		EventBus.getDefault().unregister(this);

		super.onPause();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.d(TAG, "======onActivityResult====== requestCode=" + requestCode
				+ " ,resultCode=" + resultCode);

		switch (requestCode) {
		case Constants.REQUEST_CODE_SEARCH:

			if (Constants.RESULT_CODE_SEARCH_EXIT == resultCode) {

				exitSearch();

			} else if (Constants.RESULT_CODE_SEARCH_RESULT == resultCode) {

				keyWordSearch();
			} else if (Constants.RESULT_CODE_SEARCH_RESULT_ONE == resultCode) {
				String hotelId = data.getStringExtra(Keys.ID);
				selectOneHotel(hotelId);
			}

			if (ac.isSearch) {
				ivExitSearch.setVisibility(View.VISIBLE);
			} else {
				ivExitSearch.setVisibility(View.INVISIBLE);
			}

			break;

		default:
			break;
		}

	}

	private void exitSearch() {
		ivExitSearch.setVisibility(View.INVISIBLE);
		ac.isSearch = false;
		ac.isSelectOneHotel = false;
		ac.searchKeyword = null;
		tvKeyword.setText("");
		reqLastMapMoveLocData();
	}

	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "======onAttach======");
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		Log.d(TAG, "======onDetach======");
		super.onDetach();
	};

	@Override
	public void onDestroy() {
		Log.d(TAG, "======onDestroy======");
		ac.stopLocBD();

		if (mMapView != null) {
			mMapView.onDestroy();
		}

		if (mSearch != null) {
			mSearch.destroy();
		}

		ac.lastChooseCity = ac.lastLocCity;

		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		Log.d(TAG, "======onDestroyView======");
		// EventBus.getDefault().unregister(this);
		super.onDestroyView();
	}

}
