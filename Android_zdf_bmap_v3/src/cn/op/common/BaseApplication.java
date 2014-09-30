package cn.op.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import cn.op.common.constant.Keys;
import cn.op.common.domain.UserInfo;
import cn.op.common.util.StringUtils;
import cn.op.zdf.R;
import cn.op.zdf.dao.MyDbHelper;
import cn.op.zdf.domain.City;
import cn.op.zdf.event.DecodeCityNameByLatLngEvent;
import cn.op.zdf.event.LocFailEvent;
import cn.op.zdf.event.LocationChangedEvent;
import cn.op.zdf.ui.BMapFragment;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import de.greenrobot.event.EventBus;

public abstract class BaseApplication extends Application {
	private static final String TAG = "BaseApplication===";
	private static Toast toast;
	/**
	 * Toast. 异常是否显示
	 */
	public static final boolean isDebugToast = false;
	/**
	 * Log, 是否打印
	 */
	public static final boolean isDebugLog = true;
	public static final int PAGE_NUM = 1;// 默认分页第一页
	public static final int PAGE_SIZE = 10;// 默认分页大小
	private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间

	public int screenHight;

	public int screenWidth;

	public LocationClient mLocClientBD;
	public MyLocationListennerBD myLocListenerBD;
	public UserInfo user;

	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();
	public BDLocation myLocationBD;
	private GeoCoder mSearch;

	/**
	 * 上次定为到的城市
	 */
	public City lastLocCity;

	/**
	 * 上次选择的城市
	 */
	public City lastChooseCity;

	public ImageLoader mImageLoader;
	/**
	 * 磁盘缓存，内存缓存
	 */
	public DisplayImageOptions optionsLogo;
	/**
	 * 仅磁盘缓存
	 */
	public DisplayImageOptions options_largePic;

	/**
	 * 仅磁盘缓存,失败后不设置占位图片
	 */
	public DisplayImageOptions options_noFailPic;

	private boolean isInitedBMap;
	/**
	 * 是否正在定位
	 */
	public boolean isLocing;
	public DisplayImageOptions options_markerImg;

	@Override
	public void onCreate() {
		super.onCreate();
		toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
	}

	public boolean isLogin() {
		return user != null;
	}

	/**
	 * @return 全局单例toast，显示方式：</br>toast.setText("hello"); </br>toast.show();
	 */
	public static Toast getToast() {
		return toast;
	}

	/**
	 * 直接显示toast，无需通过toast.show();显示
	 * 
	 * @param s
	 */
	public static void toastShow(CharSequence s) {
		toast.setText(s);
		toast.show();
	}

	public static void toastShowException(String err) {
		if (isDebugToast) {
			toastShow(err);
		}
	}

	/**
	 * 直接显示toast，无需通过toast.show();显示
	 * 
	 * @param s
	 */
	public static void toastShowLong(CharSequence s) {
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setText(s);
		toast.show();
	}

	/**
	 * 直接显示toast，无需通过toast.show();显示
	 * 
	 * @param s
	 */
	public static void toastShowLong(int resId) {
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setText(resId);
		toast.show();
	}

	/**
	 * 直接显示toast，无需通过toast.show();显示
	 * 
	 * @param s
	 */
	public static void toastShow(int resId) {
		toast.setText(resId);
		toast.show();
	}

	public static void toastShowException(int resId) {
		if (isDebugToast) {
			toastShow(resId);
		}

	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	/**
	 * 获取App唯一标识
	 * 
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if (StringUtils.isEmpty(uniqueID)) {
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}

	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	/**
	 * 清除保存的缓存
	 */
	public void cleanCookie() {
		removeProperty(AppConfig.CONF_COOKIE);
	}

	/**
	 * 判断缓存数据是否可读
	 * 
	 * @param cachefile
	 * @return
	 */
	protected boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 判断缓存是否存在
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists())
			exist = true;
		return exist;
	}

	/**
	 * 判断缓存是否失效
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile) {
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists()
				&& (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if (!data.exists())
			failure = true;
		return failure;
	}

	/**
	 * 清除app缓存
	 */
	public void clearAppCache() {
		// 清除webview缓存
		// File file = CacheManager.getCacheFileBaseDir();
		// if (file != null && file.exists() && file.isDirectory()) {
		// for (File item : file.listFiles()) {
		// item.delete();
		// }
		// file.delete();
		// }
		deleteDatabase("webview.db");
		deleteDatabase("webview.db-shm");
		deleteDatabase("webview.db-wal");
		deleteDatabase("webviewCache.db");
		deleteDatabase("webviewCache.db-shm");
		deleteDatabase("webviewCache.db-wal");
		// 清除数据缓存
		clearCacheFolder(getFilesDir(), System.currentTimeMillis());
		clearCacheFolder(getCacheDir(), System.currentTimeMillis());
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			clearCacheFolder(MethodsCompat.getExternalCacheDir(this),
					System.currentTimeMillis());
		}
		// 清除编辑器保存的临时内容
		Properties props = getProperties();
		for (Object key : props.keySet()) {
			String _key = key.toString();
			if (_key.startsWith("temp"))
				removeProperty(_key);
		}
	}

	/**
	 * 清除缓存目录
	 * 
	 * @param dir
	 *            目录
	 * @param numDays
	 *            当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, curTime);
					}
					if (child.lastModified() < curTime) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}

	/**
	 * 将对象保存到内存缓存中
	 * 
	 * @param key
	 * @param value
	 */
	public void setMemCache(String key, Object value) {
		memCacheRegion.put(key, value);
	}

	/**
	 * 从内存缓存中获取对象
	 * 
	 * @param key
	 * @return
	 */
	public Object getMemCache(String key) {
		return memCacheRegion.get(key);
	}

	/**
	 * 保存磁盘缓存
	 * 
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void setDiskCache(String key, String value) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = openFileOutput("cache_" + key + ".data", Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.flush();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 获取磁盘缓存数据
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getDiskCache(String key) throws IOException {
		FileInputStream fis = null;
		try {
			fis = openFileInput("cache_" + key + ".data");
			byte[] datas = new byte[fis.available()];
			fis.read(datas);
			return new String(datas);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 保存对象
	 * 
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 读取对象
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file) {
		if (!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			Serializable s = (Serializable) ois.readObject();
			Log.d(TAG, "---readObject：" + file);
			return s;
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				File data = getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	public void deleteObject(String file) {
		File data = getFileStreamPath(file);
		data.delete();
	}

	public boolean containsProperty(String key) {
		Properties props = getProperties();
		return props.containsKey(key);
	}

	public void setProperties(Properties ps) {
		AppConfig.getAppConfig(this).setProps(ps);
	}

	public Properties getProperties() {
		return AppConfig.getAppConfig(this).get();
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value);
	}

	public String getProperty(String key) {
		return AppConfig.getAppConfig(this).get(key);
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}

	public void initImageLoader(Context context) {
		if (mImageLoader == null) {
			// This configuration tuning is custom. You can tune every option,
			// you
			// may tune some of them,
			// or you can create default configuration by
			// ImageLoaderConfiguration.createDefault(this);
			// method.
			Builder builder = new ImageLoaderConfiguration.Builder(context)
					.threadPriority(Thread.NORM_PRIORITY - 2)
					.denyCacheImageMultipleSizesInMemory()
					.diskCacheFileNameGenerator(new Md5FileNameGenerator())
					.tasksProcessingOrder(QueueProcessingType.LIFO);
			com.nostra13.universalimageloader.utils.L.writeLogs(true);

			// 更改图片缓存目录，外部sd卡优先
			// File cacheDir = null;
			// if (FileUtils.isExtStore2Mount()) {
			// cacheDir = FileUtils.getDirOnExtStore2("/Download/zdf/Picture");
			// } else {
			// cacheDir = FileUtils.getDirOnExtStore("/Download/zdf/Picture");
			// }
			// builder.discCache(new UnlimitedDiscCache(cacheDir,
			// new FileNameGenerator() {
			// @Override
			// public String generate(String imageUri) {
			// // 更改图片磁盘存储命名
			// Log.d(TAG, "更改图片磁盘存储命名=" + imageUri);
			// // http: //
			// //
			// 61.185.11.40//Images/PictureRes/5/红十五军团成立大会/ImgRes/a177828f-3e2c-41c0-a19e-4ce9f80fafc9_0.jpg
			// // if (imageUri.contains("/Images/PictureRes/")) {
			// // String str = imageUri.substring(0,
			// // imageUri.indexOf("/ImgRes/"));
			// // String name = str.substring(
			// // str.lastIndexOf("/") + 1, str.length());
			// // return name + "-"
			// // + String.valueOf(imageUri.hashCode());
			// // } else {
			// // return String.valueOf(imageUri.hashCode());
			// // }
			//
			// return String.valueOf(imageUri.hashCode());
			// }
			// }));

			ImageLoaderConfiguration config = builder.build();

			// Initialize ImageLoader with configuration.
			ImageLoader.getInstance().init(config);

			mImageLoader = ImageLoader.getInstance();

			// Display Options
			optionsLogo = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.img_hotel_logo_default)
					.showImageForEmptyUri(R.drawable.img_hotel_logo_default)
					.resetViewBeforeLoading(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.showImageOnFail(R.drawable.img_hotel_logo_default)
					.cacheInMemory(true).cacheOnDisk(true)
					.bitmapConfig(Bitmap.Config.RGB_565).build();

			// disable caching in memory at all in display options
			options_largePic = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.img_hotel_logo_default)
					.showImageForEmptyUri(R.drawable.img_hotel_logo_default)
					.resetViewBeforeLoading(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.showImageOnFail(R.drawable.img_hotel_logo_default)
					.cacheInMemory(false).cacheOnDisk(true)
					.bitmapConfig(Bitmap.Config.RGB_565).build();

			// disable caching in memory at all in display options
			options_noFailPic = new DisplayImageOptions.Builder()
					.resetViewBeforeLoading(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.cacheInMemory(false).cacheOnDisk(true)
					.bitmapConfig(Bitmap.Config.RGB_565).build();

			options_markerImg = new DisplayImageOptions.Builder()
					.resetViewBeforeLoading(true).cacheInMemory(true)
					.imageScaleType(ImageScaleType.EXACTLY).cacheOnDisk(true)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
		}
	}

	@Override
	public void onTerminate() {
		Log.d(TAG, "======onTerminate======");
		super.onTerminate();
	}

	// ====================百度地图======================
	/**
	 * 
	 * 使用地图sdk前需先初始化BMapManager.
	 * </br>BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
	 * </br>并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁 </br>TODO 什么时候释放
	 */
	public void initBMapManager(Application context) {
		long millis1 = System.currentTimeMillis();
		if (!isInitedBMap) {
			isInitedBMap = true;

			SDKInitializer.initialize(this);

			long millis2 = System.currentTimeMillis();
			Log.d(TAG, "======initBMapManager======: time="
					+ (millis2 - millis1));
		}
	}

	/**
	 * 初始化百度定位
	 */
	private void initLocBD() {
		mLocClientBD = new LocationClient(getApplicationContext());
		myLocListenerBD = new MyLocationListennerBD();
		mLocClientBD.registerLocationListener(myLocListenerBD);

		// ==============具体参考百度定位SDK
		// key:在开发者网站对应该APP已申请的AccessKey
		mLocClientBD.setAK(BMapFragment.strKey);

	}

	/**
	 * 请求定位
	 */
	public void startLocBD() {
		if (mLocClientBD == null) {
			initLocBD();
		}

		if (mLocClientBD != null) {

			setLocOptionBD();

			if (mLocClientBD.isStarted()) {
				mLocClientBD.requestLocation();
				Log.d(TAG, "======startLoc======: requestLocation");
				millisLocStart = System.currentTimeMillis();
			} else {
				mLocClientBD.start();
				Log.d(TAG, "======startLoc======: start");
				millisLocStart = System.currentTimeMillis();
			}

			isLocing = true;
		}
	}

	/**
	 * 在定位完成后调用，关闭gps定位
	 */
	private void setLocCloseGpsOptionBD() {
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setOpenGps(false);
		mLocClientBD.setLocOption(option);
	}

	/**
	 * 开始定位前调用一次，用来设置定位参数
	 */
	private void setLocOptionBD() {
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll"); // 设置坐标类型

		// 设置是否打开gps，使用gps前提是用户硬件打开gps。默认是不打开gps的。
		option.setOpenGps(true);
		// 设置定位方式的优先级,
		option.setPriority(LocationClientOption.GpsFirst);

		// setScanSpan < 1000 则为 app主动请求定位；
		// setScanSpan>=1000,则为定时定位模式（setScanSpan的值就是定时定位的时间间隔
		// 定位模式分为两类：一次定位和定时定位。
		// 一次定位：用户程序调用requestLocation()后，定位SDK会立刻整合定位依据，发起网络请求，获取定位结果。定位结果通过BDLocationListener返回。
		// 定时定位：用户程序调用requestLocation()后，每隔设定的时间，整合定位依据，发起网络请求，获取定位结果。定位结果通过BDLocationListener返回。
		option.setScanSpan(0);

		option.disableCache(true);// 禁止启用缓存定位
		// 设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
		// option.setProdName ("jujia");

		// 设置是否要返回地址信息，默认为无地址信息。String 值为 all时，表示返回地址信息。
		// option.setAddrType("all");

		// option.setPoiNumber(5); //最多返回POI个数
		// option.setPoiDistance(1000); //poi查询距离
		// option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息

		if (mLocClientBD != null) {
			mLocClientBD.setLocOption(option);
		} else {
			Log.w(TAG, "======setLocOptionBD====== mLocClientBD is null");
		}
	}

	/**
	 * 关闭定位
	 */
	public void stopLocBD() {
		if (mLocClientBD != null) {

			int scanSpan = mLocClientBD.getLocOption().getScanSpan();
			if (scanSpan >= 1000) {
				// stop：关闭定位SDK。调用stop之后，设置的参数LocationClientOption仍然保留。
				mLocClientBD.stop();
				Log.d(TAG, "=====stopLoc======: mLocClientBD.stop()");
			} else {
				Log.d(TAG, "=====stopLoc======: scanSpan<1000, 一次定位模式, 无需停止");
			}
		}

		isLocing = false;
	}

	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListennerBD implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {

			isLocing = false;

			if (location == null) {
				Log.d(TAG, "======onReceiveLocation======: location == null");
				return;
			}

			setLocCloseGpsOptionBD();

			int errorCode = location.getLocType();
			Log.d(TAG, "======onReceiveLocation======: errorCode=" + errorCode);

			switch (errorCode) {
			case 61:
				// GPS定位结果
			case 65:
				// 定位缓存的结果。
			case 66:
				// 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果
			case 161:
				// 表示网络定位结果
				myLocationBD = location;
				Log.d(TAG,
						"======onReceiveLocation======: 定位成功="
								+ myLocationBD.getLatitude() + " , "
								+ myLocationBD.getLongitude());
				// Log.d(TAG, "======onReceiveLocation======: 定位成功"
				// + ", latitude=" + location.getLatitude()
				// + ", longitude=" + location.getLongitude());

				millisLocSuccess = System.currentTimeMillis();
				Log.d(TAG, "======onReceiveLocation====== time= "
						+ (millisLocSuccess - millisLocStart));

				decodeCityByLatLng(myLocationBD.getLatitude(),
						myLocationBD.getLongitude());

				LocationChangedEvent event = new LocationChangedEvent();
				event.isChange = true;
				EventBus.getDefault().post(event);

				AppConfig appConfig = AppConfig
						.getAppConfig(getApplicationContext());
				appConfig.set(Keys.LAST_LATITUDE, "" + location.getLatitude());
				appConfig
						.set(Keys.LAST_LONGITUDE, "" + location.getLongitude());

				if (firstLoc != null) {
					firstLoc.locationFirst(location.getLatitude(),
							location.getLongitude());
				}

				break;

			case 62:
				// 扫描整合定位依据失败。此时定位结果无效。
			case 63:
				// 网络异常，没有成功向服务器发起请求。此时定位结果无效。
			case 67:
				// 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果
			case 68:
				// 网络连接失败时，查找本地离线定位时对应的返回结果
			case 166:
				Log.d(TAG, "======onReceiveLocation======: key无效");

			default:
				// 162~167：
				// 服务端定位失败。如果不能定位，请记住这个返回值，并到我们的hi群或者贴吧中交流。若返回值是162~167，请发送邮件至mapapi@baidu.com反馈。
				Log.d(TAG, "======onReceiveLocation======: 定位失败"
						+ ", errorCode=" + errorCode);

				LocFailEvent e = new LocFailEvent();
				e.isFail = true;
				EventBus.getDefault().post(e);

				break;
			}

		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	/**
	 * // 反Geo搜索Geo
	 * 
	 * @param latitude
	 * @param longitude
	 * @param isMapMove
	 *            是否地图移动后进行的
	 */
	private void decodeCityByLatLng(double latitude, double longitude) {

		Log.d(TAG, "======decodeCityByLatLng====== lat=" + latitude + ",lng="
				+ longitude);

		if (mSearch != null) {
			mSearch.destroy();
			mSearch = null;
		}

		// TODO 初始化搜索模块，注册事件监听；为了解决切换城市后，再次Geo搜索不回调的问题，只能在这里每次都初始化一个MKSearch
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
					city.cityId = MyDbHelper.getInstance(
							getApplicationContext()).queryCityIdByCityName(
							cityName);

					lastLocCity = city;
					lastChooseCity = city;

					DecodeCityNameByLatLngEvent event = new DecodeCityNameByLatLngEvent();
					event.city = city;
					EventBus.getDefault().post(event);
				}

				if (mSearch != null) {
					mSearch.destroy();
					mSearch = null;
				}

			}

		});

		LatLng ptCenter = new LatLng(latitude, longitude);
		boolean reverseGeoCode = mSearch
				.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));

		Log.d(TAG, "======mSearch====== reverseGeocode= " + reverseGeoCode);
	}

	protected int lastLocChangeFlag = 0;
	protected long millisLocSuccess;

	public FirstLoc firstLoc;

	public interface FirstLoc {
		public void locationFirst(double latitude, double longitude);
	}

	/**
	 * 是否正在定位，未返回定位结果
	 */
	public boolean isRequestLocation = false;
	private long millisLocStart;

}