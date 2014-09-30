package cn.op.zdf.ui;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Properties;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import cn.op.common.AppConfig;
import cn.op.common.AppConfigOnSdcard;
import cn.op.common.AppException;
import cn.op.common.UIHelper;
import cn.op.common.constant.Constant;
import cn.op.common.constant.Keys;
import cn.op.common.domain.URLs;
import cn.op.common.util.DateUtil;
import cn.op.common.util.DisplayUtil;
import cn.op.common.util.Log;
import cn.op.common.util.PhoneUtil;
import cn.op.common.util.StringUtils;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.dao.MyDbHelper;
import cn.op.zdf.domain.ServerConfig;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.meizu.smartbar.SmartBarUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.tencent.stat.MtaSDkException;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatReportStrategy;
import com.tencent.stat.StatService;
import com.tencent.stat.common.StatLogger;
import com.umeng.analytics.MobclickAgent;

/**
 * 开场，入口；启动后立刻发起定位请求，
 * 
 * @author lufei
 * 
 */
public class StartActivity extends SherlockFragmentActivity {

	private static final String TAG = Log.makeLogTag(StartActivity.class);
	protected static final int WHAT_INIT_SERVER_CONFIG = 1;
	private View layoutStart;
	private boolean isStart = true;
	private AppContext ac;
	private ImageView ivChannel;
	private AppConfig appConfig;
	private ImageView ivNew;

	private static StatLogger logger = new StatLogger("MTADemon");

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
		Log.d(TAG, "======onCreate======");

		ac = AppContext.getAc();

		setContentView(R.layout.activity_start);
		layoutStart = findViewById(R.id.layout_Start);
		View layoutChannel = findViewById(R.id.layoutChannel);
		ivChannel = (ImageView) layoutChannel.findViewById(R.id.ivChannel);

		ivChannel.setImageDrawable(null);
		layoutChannel.setVisibility(View.GONE);

		// 市场首发图片
		String channelName = PhoneUtil.getChannel(ac);

		if ("taobao".equals(channelName)) {
			ivChannel.setImageResource(R.drawable.ic_logo_channel_taobao);
			layoutChannel.setVisibility(View.VISIBLE);
		}
		// else if ("apk91".equals(channelName)) {
		// ivChannel
		// .setImageResource(R.drawable.ic_logo_channel_baidu_91_hiapk);
		// layoutChannel.setVisibility(View.VISIBLE);
		// } else if ("hiapk".equals(channelName)) {
		// ivChannel
		// .setImageResource(R.drawable.ic_logo_channel_baidu_91_hiapk);
		// layoutChannel.setVisibility(View.VISIBLE);
		// } else if ("baidu".equals(channelName)) {
		// ivChannel
		// .setImageResource(R.drawable.ic_logo_channel_baidu_91_hiapk);
		// layoutChannel.setVisibility(View.VISIBLE);
		// }
		else {
			ivChannel.setImageDrawable(null);
			layoutChannel.setVisibility(View.GONE);
		}

		ac.startLocBD();
		ac.getCityPageInsertDb(MyDbHelper.getInstance(ac));
		if (ac.lastLocCity != null) {
			ac.getHotelPageInsertDb(MyDbHelper.getInstance(ac),
					ac.lastLocCity.cityId);
		}
		baseDataCollect();

		initView();
		initServerConfig();

		tencentMta();

	}

	private void initView() {
		ivNew = (ImageView) findViewById(R.id.ivNew);

		appConfig = AppConfig.getAppConfig(ac);
		boolean isShowStartImg = StringUtils.toBool(appConfig
				.get(Keys.START_IMG_SHOW));

		String startImgUrl = appConfig.get(Keys.START_IMG);
		String startImgBeginTime = appConfig.get(Keys.START_IMG_BEGIN_TIME);
		String startImgEndTime = appConfig.get(Keys.START_IMG_END_TIME);

		File startImgCache = DiskCacheUtils.findInCache(URLs.URL_ZDF_API
				+ startImgUrl, ac.mImageLoader.getDiskCache());

		// 是否显示配置图片，是否在有效气馁，图片是否存在
		if (isShowStartImg
				&& DateUtil.isLiveDate(startImgBeginTime, startImgEndTime)
				&& startImgCache != null) {

			ac.mImageLoader.displayImage(URLs.URL_ZDF_API + startImgUrl, ivNew,
					ac.options_noFailPic, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
						}

						@Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap arg2) {
							ivNew.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
						}
					});

		} else {

		}
	}

	private MyHandler myHandler = new MyHandler(this);
	private CountDownTimer timerStart;

	static class MyHandler extends Handler {
		private WeakReference<StartActivity> mWr;

		public MyHandler(StartActivity frag) {
			super();
			this.mWr = new WeakReference<StartActivity>(frag);
		}

		@Override
		public void handleMessage(Message msg) {
			final StartActivity activity = mWr.get();

			if (activity == null) {
				return;
			}

			switch (msg.what) {
			case WHAT_INIT_SERVER_CONFIG:

				ServerConfig serverConfig = (ServerConfig) msg.obj;
				if (serverConfig.rspMsg.OK()) {

					File startImgCache = DiskCacheUtils.findInCache(
							URLs.URL_ZDF_API + serverConfig.startImg,
							activity.ac.mImageLoader.getDiskCache());

					// 是否显示配置图片，是否在有效气馁，图片是否存在
					if (serverConfig.isShowStartImg
							&& DateUtil.isLiveDate(
									serverConfig.startImgBeginTime,
									serverConfig.startImgEndTime)
							&& startImgCache != null
							&& activity.ivNew.getVisibility() == View.GONE) {
						activity.ac.mImageLoader.displayImage(URLs.URL_ZDF_API
								+ serverConfig.startImg, activity.ivNew,
								activity.ac.options_noFailPic,
								new ImageLoadingListener() {

									@Override
									public void onLoadingStarted(String arg0,
											View arg1) {
									}

									@Override
									public void onLoadingFailed(String arg0,
											View arg1, FailReason arg2) {
									}

									@Override
									public void onLoadingComplete(String arg0,
											View arg1, Bitmap arg2) {
										activity.ivNew
												.setVisibility(View.VISIBLE);
									}

									@Override
									public void onLoadingCancelled(String arg0,
											View arg1) {
									}
								});
					}

					activity.ac.mImageLoader.loadImage(URLs.URL_ZDF_API
							+ serverConfig.markerImg,
							activity.ac.options_markerImg,
							new SimpleImageLoadingListener());
					activity.ac.mImageLoader.loadImage(URLs.URL_ZDF_API
							+ serverConfig.markerSmallImg,
							activity.ac.options_markerImg,
							new SimpleImageLoadingListener());
					activity.ac.mImageLoader.loadImage(URLs.URL_ZDF_API
							+ serverConfig.startImg,
							activity.ac.options_noFailPic,
							new SimpleImageLoadingListener());
				}
				break;

			default:
				break;
			}
		}
	}

	private void initServerConfig() {
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {

					String phonePlatform = Constant.PLATFORM_ANDROID;
					ServerConfig serverConfig = ac
							.getServerConfig(phonePlatform);

					if (serverConfig.rspMsg.OK()) {

						Properties prop = appConfig.get();

						prop.setProperty(Keys.START_IMG, serverConfig.startImg);
						prop.setProperty(Keys.START_IMG_SHOW,
								String.valueOf(serverConfig.isShowStartImg));
						prop.setProperty(Keys.START_IMG_BEGIN_TIME,
								serverConfig.startImgBeginTime);
						prop.setProperty(Keys.START_IMG_END_TIME,
								serverConfig.startImgEndTime);

						prop.setProperty(Keys.MARKER_IMG,
								serverConfig.markerImg);
						prop.setProperty(Keys.MARKER_SMALL_IMG,
								serverConfig.markerSmallImg);
						prop.setProperty(Keys.MARKER_IMG_SHOW,
								String.valueOf(serverConfig.isShowStartImg));
						prop.setProperty(Keys.MARKER_IMG_BEGIN_TIME,
								serverConfig.markerImgBeginTime);
						prop.setProperty(Keys.MARKER_IMG_END_TIME,
								serverConfig.markerImgEndTime);
						appConfig.setProps(prop);
					}

					msg.what = WHAT_INIT_SERVER_CONFIG;
					msg.obj = serverConfig;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -WHAT_INIT_SERVER_CONFIG;
					msg.obj = e;
				}

				myHandler.sendMessage(msg);
			}
		}.start();

	}

	private void tencentMta() {
		initMTAConfig(true);

		// 初始化并启动MTA
		// 第三方SDK必须按以下代码初始化MTA，其中appkey为规定的格式或MTA分配的代码。
		// 其它普通的app可自行选择是否调用
		// 第三个参数必须为：com.tencent.stat.common.StatConstants.VERSION
		String appkey = "Aqc1101130398";
		try {
			StatService.startStatService(this, appkey,
					com.tencent.stat.common.StatConstants.VERSION);
		} catch (MtaSDkException e) {
			// MTA初始化失败
			logger.error("MTA start failed.");
			logger.error("e");
		}
	}

	/**
	 * 根据不同的模式，建议设置的开关状态，可根据实际情况调整，仅供参考。
	 * 
	 * @param isDebugMode
	 *            根据调试或发布条件，配置对应的MTA配置
	 */
	private void initMTAConfig(boolean isDebugMode) {
		logger.d("isDebugMode:" + isDebugMode);
		if (isDebugMode) { // 调试时建议设置的开关状态
			// 查看MTA日志及上报数据内容
			StatConfig.setDebugEnable(true);
			// 禁用MTA对app未处理异常的捕获，方便开发者调试时，及时获知详细错误信息。
			// StatConfig.setAutoExceptionCaught(false);
			// StatConfig.setEnableSmartReporting(false);
			// Thread.setDefaultUncaughtExceptionHandler(new
			// UncaughtExceptionHandler() {
			//
			// @Override
			// public void uncaughtException(Thread thread, Throwable ex) {
			// logger.error("setDefaultUncaughtExceptionHandler");
			// }
			// });
			// 调试时，使用实时发送
			StatConfig.setStatSendStrategy(StatReportStrategy.INSTANT);
		} else { // 发布时，建议设置的开关状态，请确保以下开关是否设置合理
			// 禁止MTA打印日志
			StatConfig.setDebugEnable(false);
			// 根据情况，决定是否开启MTA对app未处理异常的捕获
			StatConfig.setAutoExceptionCaught(true);
			// 选择默认的上报策略
			StatConfig.setStatSendStrategy(StatReportStrategy.APP_LAUNCH);
		}
	}

	private void baseDataCollect() {

		new Thread() {
			public void run() {

				try {

					ac.baseDataCollect();

				} catch (AppException e) {
					e.printStackTrace();
				}
			};
		}.start();

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Log.d(TAG, "======onWindowFocusChanged====== hasFocus= " + hasFocus);

		if (isStart) {
			// ac.requestLocation();
			// statusBarHeight = DisplayUtil.getStatusBarHeight(this);
			ac.screenHight = DisplayUtil.getScreenHight(this);
			ac.screenWidth = DisplayUtil.getScreenWidth(this);

			startAnim();
			isStart = false;
		}
	}

	private void startAnim() {

		// 这里没有使用anim的setDuration来设置持续时间，因为在某些设备上setDuration设置的时间不起效
		timerStart = new CountDownTimer(2500, 2000) {

			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {

				AppConfig appConfig = AppConfigOnSdcard
						.getAppConfig(getApplicationContext());

				String versionCodeLast = appConfig.get(Keys.APK_VERCODE_LAST);

				String versionCode = ""
						+ PhoneUtil.getVersionCode(getApplicationContext());

				// 当前版本跟上一次进入的版本不一样，才显示新手引导
				if (!versionCode.equals(versionCodeLast)) {
					appConfig.set(Keys.APK_VERCODE_LAST, versionCode);

					Intent intent = new Intent(getApplicationContext(),
							GuideActivity.class);
					startActivity(intent);

					finish();
				} else {
					enterMainUi();
				}

				if (timerStart != null) {
					timerStart.cancel();
					timerStart = null;
				}

			}
		};

		timerStart.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		StatService.onPause(this);
		overridePendingTransition(0, 0);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		StatService.onResume(this);
	}

	@Override
	protected void onDestroy() {
		if (timerStart != null) {
			timerStart.cancel();
			timerStart = null;
		}

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater supportMenuInflater = getSupportMenuInflater();

		if (SmartBarUtils.hasSmartBar()) {
			UIHelper.makeEmptyMenu(supportMenuInflater, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	private void enterMainUi() {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);
		finish();

		// 避免闪屏问题
		overridePendingTransition(0, 0);
		// overridePendingTransition(R.anim.translucent_zoom_in,
		// R.anim.slide_out_to_top);
	}

}