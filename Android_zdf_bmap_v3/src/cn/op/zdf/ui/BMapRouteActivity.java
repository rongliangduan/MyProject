package cn.op.zdf.ui;

import java.io.File;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.op.common.AppConfig;
import cn.op.common.UIHelper;
import cn.op.common.constant.Keys;
import cn.op.common.domain.URLs;
import cn.op.common.util.DateUtil;
import cn.op.common.util.DisplayUtil;
import cn.op.common.util.ImageUtils;
import cn.op.common.util.Log;
import cn.op.common.util.StringUtils;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.domain.Item;
import cn.op.zdf.domain.Room;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.meizu.smartbar.SmartBarUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.tendcloud.tenddata.TCAgent;

/**
 * 百度地图：路线导航页面
 * 
 * @author lufei
 * 
 */
public class BMapRouteActivity extends SherlockFragmentActivity {

	private static final String TAG = Log.makeLogTag(BMapRouteActivity.class);
	private Item item;

	// 地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	private MapView mMapView;
	private BaiduMap mMapController;
	private float mapZoomDefault = 15;
	private AppContext ac;
	private LatLng startGeoPoint;
	private View pb;
	private ViewGroup layoutMap;

	/**
	 * 保存路线数据的变量，供浏览节点时使用
	 */
	private RouteLine route = null;
	private BMapRouteActivity activity;
	private LayoutInflater inflater;
	private View viewMarker;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "======onCreate======");

		if (SmartBarUtils.hasSmartBar()) {
			getSherlock().setUiOptions(
					ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);

			setTheme(R.style.Holo_Theme_CustomAbsOverlay);

			SmartBarUtils.setActionBarViewCollapsable(getActionBar(), true);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bmap_route);

		ac = AppContext.getAc();
		activity = this;

		inflater = LayoutInflater.from(activity);
		item = (Item) getIntent().getSerializableExtra(Keys.ITEM);

		if (item == null) {
			Log.d(TAG, "======未传入酒店======");
			AppContext.toastShowException("未传入酒店");
		} else {
			initView();
			initMapView();
		}
	}

	private void initMapView() {
		BaiduMapOptions mapOptions = new BaiduMapOptions();
		mapOptions
				.mapStatus(new MapStatus.Builder().zoom(mapZoomDefault).build())
				.zoomControlsEnabled(false).scaleControlEnabled(false)
				.overlookingGesturesEnabled(false).rotateGesturesEnabled(false);

		mMapView = new MapView(this, mapOptions);
		layoutMap.addView(mMapView, 0, new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));

		mMapController = mMapView.getMap();

		// 初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {

			@Override
			public void onGetWalkingRouteResult(WalkingRouteResult arg0) {

			}

			@Override
			public void onGetTransitRouteResult(TransitRouteResult result) {
				pb.setVisibility(View.GONE);

				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR) {
					Toast.makeText(BMapRouteActivity.this, "抱歉，未找到结果",
							Toast.LENGTH_SHORT).show();

				}
				if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
					// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
					// result.getSuggestAddrInfo()
					Log.d(TAG,
							"======onGetTransitRouteResult====== 起终点或途经点地址有岐义");
					return;
				}

				if (result.error == SearchResult.ERRORNO.NO_ERROR) {
					nodeIndex = -1;
					route = result.getRouteLines().get(0);
					TransitRouteOverlay overlay = new MyTransitRouteOverlay(
							mMapController);
					mMapController.setOnMarkerClickListener(overlay);
					routeOverlay = overlay;
					overlay.setData(result.getRouteLines().get(0));
					overlay.addToMap();
					overlay.zoomToSpan();
				}

			}

			@Override
			public void onGetDrivingRouteResult(DrivingRouteResult result) {
				pb.setVisibility(View.GONE);

				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR) {
					Toast.makeText(BMapRouteActivity.this, "抱歉，未找到结果",
							Toast.LENGTH_SHORT).show();

				}
				if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
					// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
					// result.getSuggestAddrInfo()
					Log.d(TAG,
							"======onGetTransitRouteResult====== 起终点或途经点地址有岐义");
					return;
				}

				if (result.error == SearchResult.ERRORNO.NO_ERROR) {
					nodeIndex = -1;
					route = result.getRouteLines().get(0);
					DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(
							mMapController);
					routeOverlay = overlay;
					mMapController.setOnMarkerClickListener(overlay);
					overlay.setData(result.getRouteLines().get(0));
					overlay.addToMap();
					overlay.zoomToSpan();
				}
			}
		});

		AppConfig appConfig = AppConfig.getAppConfig(ac);

		String lastLat = appConfig.get(Keys.LAST_LATITUDE);
		String lastLng = appConfig.get(Keys.LAST_LONGITUDE);

		if (lastLat != null && lastLng != null) {
			double latitude = StringUtils.toDouble(lastLat);
			double longitude = StringUtils.toDouble(lastLng);

			// LocationData myLocData = new LocationData();
			// myLocData.latitude = latitude;
			// myLocData.longitude = longitude;

			startGeoPoint = new LatLng(latitude, longitude);

			mMapController.animateMapStatus(MapStatusUpdateFactory
					.newLatLng(startGeoPoint));

			// showMyLocation(latLng);
			// drawMarker();

			if (item != null) {
				endGeoPoint = new LatLng(item.hotelsLatitude,
						item.hotelsLongitude);

				new CountDownTimer(400, 1000) {

					@Override
					public void onTick(long millisUntilFinished) {
						Log.d(TAG, "======CountDownTimer====== onTick");
					}

					@Override
					public void onFinish() {
						Log.d(TAG, "======CountDownTimer====== onFinish");

						Log.d(TAG, "======end====== lat=" + item.hotelsLatitude
								+ " ,lng=" + item.hotelsLongitude);

						// 等onResume结束后再调用
						pathNav();
					}
				}.start();

			}
		}
	}

	private class MyTransitRouteOverlay extends TransitRouteOverlay {

		public MyTransitRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {

			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {

			return getTerminalMarkerBd();
		}
	}

	private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

		public MyDrivingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {

			return BitmapDescriptorFactory
					.fromResource(R.drawable.location_marker);
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {

			return getTerminalMarkerBd();
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private View makePriceMarkerView(Item item) {

		if (viewMarker == null) {
			viewMarker = inflater.inflate(R.layout.map_marker_price, layoutMap,
					false);
		}

		// ImageView ivMarkerBg = (ImageView) viewMarker
		// .findViewById(R.id.ivMarkerBg);
		final ImageView ivBusinessType = (ImageView) viewMarker
				.findViewById(R.id.ivBusinessType);
		LinearLayout layoutMarker = (LinearLayout) viewMarker
				.findViewById(R.id.layoutMarker);
		View layoutPrice = viewMarker.findViewById(R.id.layoutPrice);
		TextView tvPrice = (TextView) viewMarker.findViewById(R.id.textView2);
		TextView tvName = (TextView) viewMarker.findViewById(R.id.textView3);

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

		if (!StringUtils.isEmpty(item.priceOrder)) {
			tvPrice.setText(item.priceOrder);
			layoutPrice.setVisibility(View.VISIBLE);
		}

		return viewMarker;
	}

	public BitmapDescriptor getTerminalMarkerBd() {
		BitmapDescriptor bd = BitmapDescriptorFactory
				.fromView(makePriceMarkerView(item));
		return bd;
	}

	private void initView() {

		pb = findViewById(R.id.pb);
		layoutMap = (ViewGroup) findViewById(R.id.layoutMap);

		View btnLeft = findViewById(R.id.btnLeft);
		View btnRight = findViewById(R.id.btnRight);
		TextView tvTopBarTitle = (TextView) findViewById(R.id.tvTitle);

		btnRight.setVisibility(View.VISIBLE);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		TextView tvName = (TextView) findViewById(R.id.tvName);

		if (item != null) {
			tvName.setText(item.hotelsName);
		}

		btnRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				// startNaviBaidu();
				startNavi();

			}
		});
	}

	protected void startNavi() {
		if (startGeoPoint == null || endGeoPoint == null) {
			return;
		}

		String uriString = "geo:" + endGeoPoint.latitude + ","
				+ endGeoPoint.longitude + "?q=" + item.hotelsName;

		Uri mUri = Uri.parse(uriString);
		// Uri mUri = Uri.parse("geo:39.940409,116.355257?q=西直门");
		Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
		startActivity(mIntent);

	}

	/**
	 * 打开百度客户端进行导航
	 */
	protected void startNaviBaidu() {

		if (startGeoPoint == null || endGeoPoint == null) {
			return;
		}

		LatLng pt1 = startGeoPoint;
		LatLng pt2 = endGeoPoint;
		// 构建 导航参数
		final NaviPara para = new NaviPara();
		para.startPoint = pt1;
		para.startName = "从这里开始";
		para.endPoint = pt2;
		para.endName = "到这里结束";

		try {

			BaiduMapNavigation.openBaiduMapNavi(para, activity);
			// BaiduMapNavigation.openWebBaiduMapNavi(para, activity);

		} catch (BaiduMapAppNotSupportNaviException e) {
			e.printStackTrace();
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
			builder.setTitle("提示");
			builder.setPositiveButton("确认安装",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							BaiduMapNavigation.getLatestBaiduMapApp(activity);
						}
					});

			builder.setNegativeButton("Web导航",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							BaiduMapNavigation.openWebBaiduMapNavi(para,
									activity);
						}
					});

			builder.create().show();
		}
	}

	/**
	 * 调整缩放级别以容下当前显示的所有数据
	 * 
	 */
	public void zoomResize() {

		// // 离当前中心最远的一点
		// Projection projection = mMapView.getProjection();
		// Point point = new Point();
		//
		// try {
		// projection.toPixels(endGeoPoint, point);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// Log.d(TAG, "======zoomResize====== point:=" + point.x + "," +
		// point.y);
		//
		// boolean contains = true;
		//
		// if (point.x < 0 || point.y < 0) {
		// contains = false;
		// }
		//
		// if (!contains) {
		// mMapController.zoomOut();
		//
		// new Handler().postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// zoomResize();
		// }
		// }, MapController.DEFAULT_ANIMATION_TIME);
		// }

		// 缩放地图到能容下指定的经纬度范围
		// 这个方法不好用，地图总是被缩放到了最小
		routeOverlay.zoomToSpan();
	}

	int nodeIndex = -2;// 节点索引,供浏览节点时使用
	OverlayManager routeOverlay = null;
	int searchType = -1;// 记录搜索的类型，区分驾车/步行和公交

	// 搜索相关
	RoutePlanSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private LatLng endGeoPoint;

	/**
	 * 路线请求
	 */
	void pathNav() {
		// 重置浏览节点的路线数据
		route = null;
		routeOverlay = null;

		// 对起点终点的name进行赋值，也可以直接对坐标赋值，赋值坐标则将根据坐标进行搜索
		PlanNode stNode = PlanNode.withLocation(startGeoPoint);
		PlanNode enNode = PlanNode.withLocation(endGeoPoint);

		Log.d(TAG, "======pathNav====== " + " ,stNode= "
				+ startGeoPoint.latitude + " ," + startGeoPoint.longitude
				+ " ,enNode=" + endGeoPoint.latitude + " ,"
				+ endGeoPoint.longitude);

		// 公交路线搜索: 城市名，用于在哪个城市内进行检索(必须填写)
		// mSearch.transitSearch(city, stNode, enNode);

		// 驾车路线搜索: 城市名，用于在哪个城市内进行检索(必须填写)

		pb.setVisibility(View.VISIBLE);
		mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(
				enNode));

		// // 实际使用中请对起点终点城市进行正确的设定
		// String city = null;
		// if (ac.lastLocCity != null) {
		// city = ac.lastLocCity.cityName;
		//
		// Log.d(TAG, "======pathNav====== city=" + city + " ,stNode= "
		// + startGeoPoint.latitude + " ," + startGeoPoint.longitude
		// + " ,enNode=" + endGeoPoint.latitude + " ,"
		// + endGeoPoint.longitude);
		//
		// // 公交路线搜索: 城市名，用于在哪个城市内进行检索(必须填写)
		// // mSearch.transitSearch(city, stNode, enNode);
		//
		// // 驾车路线搜索: 城市名，用于在哪个城市内进行检索(必须填写)
		// pb.setVisibility(View.VISIBLE);
		// mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode)
		// .to(enNode));
		//
		// } else {
		// Log.w(TAG, "======SearchButtonProcess====== ac.lastLocCity==null");
		// AppContext.toastShowException("ac.lastLocCity==null");
		// }
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "======onResume======");
		/**
		 * MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		 */
		if (mMapView != null) {

			Log.d(TAG, "======onResume====== ! null");

			mMapView.onResume();
		}
		super.onResume();
		TCAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "======onPause======");
		/**
		 * MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		 */
		if (mMapView != null) {
			mMapView.onPause();
		}
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

	@Override
	protected void onDestroy() {
		Log.d(TAG, "======onDestroy======");
		/**
		 * MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		 */
		if (mMapView != null) {
			mMapView.onDestroy();
		}
		if (mSearch != null) {

			mSearch.destroy();
		}
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "======onSaveInstanceState======");
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(TAG, "======onRestoreInstanceState======");
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_from_left,
				R.anim.slide_out_to_right);
	}

}
